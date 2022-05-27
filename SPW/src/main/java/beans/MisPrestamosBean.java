/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import com.google.common.collect.Lists;
import entities.Amortizacion;
import entities.Columna;
import entities.HistorialPago;
import entities.PagoCuota;
import entities.Prestamo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sessions.AmortizacionFacade;
import sessions.ColumnaFacade;
import sessions.HistorialPagoFacade;
import sessions.PrestamoFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class MisPrestamosBean implements Serializable {

    //injects Bean
    @Inject
    NavigationBean navigationBean;

    @Inject
    DetallePrestamoBean detallePrestamoBean;

    //EJB
    @EJB
    ColumnaFacade columnaFacade;

    @EJB
    PrestamoFacade prestamoFacade;

    @EJB
    AmortizacionFacade amortizacionFacade;

    @EJB
    HistorialPagoFacade historialPagoFacade;

    MessagesBean messagesBean;

    //listas y entidades
    private List<Columna> listColumna;
    private List<Columna> listCheckColumna;
    private List<Prestamo> listPrestamo;
    private List<PagoCuota> listPago;
    private List<Amortizacion> listAmortizacion;
    private List<Amortizacion> listAmortizacionToUpdate;

    //campos card prestamo
    private StreamedContent loadImage;

    //constante tabla columna referencia
    private static final String COLUMN_TABLE = "amortizacion";

    //constantes de estado
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_FINALIZADO = "SALDADO";

    //buscar String
    private String buscarPrestamo;
    
    //string pago multiple
    private String pagoMultiple = "Pagadas las cuotas: ";

    //entidades
    private Prestamo prestamoSeleccionado;
    private Amortizacion amortizacionGridSeleccionada;
    private Amortizacion amortizacionTableSeleccionada;
    private Amortizacion amortizacionGeneralSeleccionada;
    
    //constantes
    private static final String VENTANA = "MIS_PRESTAMOS";

    //control para vista: tabla / grid
    private boolean viewMode;
    
    //control para orden de prestamo: ascendente / descendente
    private boolean orderMode;   
    
    //controles de columnas
    private boolean nombre;
    private boolean pago_capital;
    private boolean cuota;
    private boolean foto_cedula;
    private boolean saldo;
    private boolean estado;
    private boolean cuota_num;
    private boolean pago_interes;
    private boolean fecha;

    //campos dialog pagar
    private double capital;
    private double capitalTemp;
    private double interes;
    private double interesTemp;
    private double total;
    private double balancePendiente;
    private double cargoMoraTemp;
    private double cargoMora;
    private String nombreCliente;
    private int cuotaNum;
    private String notaPago;
    private double capitalRestante;
    private double totalCapital;
    private double totalCargoMora;
    private double totalInteres;
    private double totalCuota;
    private boolean selectAll;
    private boolean checkBoxChanges;
    private boolean checkDisable;
    

    //fecha actual
    private Date fechaActual;
    private Duration duration;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf_usa;

    @PostConstruct
    private void init() {

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf_usa = new SimpleDateFormat("yyyy-MM-dd");
        messagesBean = new MessagesBean();

        cleanFields();

        fillListAmortizacion();
        fillCheckListColumnas();
        startVisibleColumns();
        updateVisibleColumns();

        amortizacionGridSeleccionada = listAmortizacion.get(0);
    }

    public void fillFormPagar() {

        cleanFieldsPagar();

        List<HistorialPago> listHistorialPago;
        listAmortizacionToUpdate = amortizacionFacade.getAmortizacionByPrestamo(amortizacionGeneralSeleccionada.getIdPrestamo());
        double currentCuota;

        LocalDateTime fechaPago = amortizacionGeneralSeleccionada
                .getFecha()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        //calculando el cargo por mora
        int diferenciaDias = (int) Duration.between(fechaPago, LocalDateTime.now()).toDays();

        if (diferenciaDias > 0) {
            cargoMora = (amortizacionGeneralSeleccionada.getIdPrestamo().getPorcentajeMora() / 100)
                    * amortizacionGeneralSeleccionada.getPagoCapital() * diferenciaDias;
        }

        //nombre y numero de cuota
        nombreCliente = amortizacionGeneralSeleccionada.getIdPrestamo().getIdPersona().getNombre();
        cuotaNum = amortizacionGeneralSeleccionada.getCuotaNum();

        //calculando cuota
        currentCuota = amortizacionFacade.getCuotaByPrestamo(amortizacionGeneralSeleccionada.getIdPrestamo());
        listHistorialPago = historialPagoFacade.getPreviousPagosByAmortizacion(amortizacionGeneralSeleccionada);

        //si hay abonos anteriores a esta cuota
        if (listHistorialPago.size() > 0) {

            double sumInteres = 0;
            double sumCargoMora = 0;
            capital = currentCuota;

            for (HistorialPago historialPago1 : listHistorialPago) {

                //restando pagos capital anteriores
                capital = capital - historialPago1.getCapital();
                capitalRestante = capital;

                //si ya se pago el interes de esta cuota, entonces interes = 0
                sumInteres = sumInteres + historialPago1.getInteres();
                if (diferenciaDias <= 0 && sumInteres > 0 || sumInteres > 0) {
                    interes = 0;
                } else {
                    interes = amortizacionGeneralSeleccionada.getPagoInteres();
                }

                //si ya se pago el cargo por mora en esta cuota, entonces cargoMora = 0
                sumCargoMora = sumCargoMora + historialPago1.getCargoMora();
                if (diferenciaDias <= 0 && sumCargoMora > 0 || sumCargoMora > 0) {
                    cargoMora = 0;
                }
            }
        } else {
            capitalRestante = amortizacionGeneralSeleccionada.getPagoCapital();
            capital = amortizacionGeneralSeleccionada.getPagoCapital();
            interes = amortizacionGeneralSeleccionada.getPagoInteres();
        }
        
        balancePendiente = amortizacionGeneralSeleccionada.getIdPrestamo().getBalanceCapital();
        total = capital + interes + cargoMora;
        interesTemp = interes;
        cargoMoraTemp = cargoMora;
    }

    //se usaba para cuando se realizaba un pago a un prestamo
//    public void showMessage() {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmación", "Pago realizado exitosamente!");
//
//        PrimeFaces.current().dialog().showMessageDynamic(message);
//    }

    public void fillListAmortizacion() {
        listAmortizacion = amortizacionFacade.getPendingCuotas();
    }

    public void fillCheckListColumnas() {
        listColumna = columnaFacade.listColumnaByTable(COLUMN_TABLE);
        listCheckColumna = columnaFacade.listCheckColumnaByTable(COLUMN_TABLE);
    }

    public void cleanFieldsPagar() {
        fechaActual = new Date();
        listPago = null;
        listAmortizacionToUpdate = null;
        nombreCliente = "";
        cuotaNum = 0;
        notaPago = "";
        capital = 0;
        interes = 0;
        capitalTemp = 0;
        interesTemp = 0;
        cargoMoraTemp = 0;
        cargoMora = 0;
        total = 0;
        balancePendiente = 0;
        capitalRestante = 0;
        totalCapital = 0;
        totalInteres = 0;
        totalCargoMora = 0;
        totalCuota = 0;
        selectAll = false;
        checkBoxChanges = false;
        checkDisable = false;
        pagoMultiple = "Pagadas las cuotas: ";
    }

    public void cleanFields() {

        //String buscarPor
        buscarPrestamo = "";
        
        //vista de tarjetas por default
        viewMode = false; 
        
        //orden de pago mas antiguo por default
        orderMode = false;

        //entidades
        prestamoSeleccionado = null;
        amortizacionGridSeleccionada = null;
        amortizacionTableSeleccionada = null;
        amortizacionGeneralSeleccionada = null;

        //listas
        listColumna = new ArrayList<>();
        listCheckColumna = new ArrayList<>();
        listPrestamo = new ArrayList<>();

        //controles
        startVisibleColumns();
    }

    public String updateNotaPago(double cap, double cap2) {
        if ((int) cap <= 0) {
            return "SOLO INTERES";
        } else if ((int) cap >= (int) cap2) {
            return "PAGO COMPLETO";
        } else {
            return "ABONO CAPITAL";
        }
    }
    
    public int getLastPendingCuota(Prestamo prestamo){
        int lastCuota = 0;       
        
        for (Amortizacion amo : amortizacionFacade.getPendingAmortizacionByPrestamo(prestamo)) {
            lastCuota = amo.getCuotaNum();
        }
        return lastCuota;
    }

    public void addPagoToList() {
        
        if(listPago == null){
            //si el monto ingresado es mayor al balance pendiente
            if((int) capital <= (int) amortizacionGeneralSeleccionada.getIdPrestamo().getBalanceCapital()){
                
                listPago = new ArrayList<>();
                interesTemp = interes;
                cargoMoraTemp = cargoMora;
                capitalTemp = capital;

                PagoCuota pagoCuota;

                //reiniciando totales
                totalCargoMora = 0;
                totalCapital = 0;
                totalCuota = 0;
                totalInteres = 0;

                if (amortizacionGeneralSeleccionada != null) {         
                    
                    if ((int) capital > (int) capitalRestante && (int) capital > (int) amortizacionGeneralSeleccionada.getPagoCapital()) {
                        
                        int pendingCuotas = getLastPendingCuota(amortizacionGeneralSeleccionada.getIdPrestamo());
                        int countCuotas = cuotaNum + (int) Math.ceil((int) (capital - interes - cargoMora) / (int) amortizacionGeneralSeleccionada.getPagoCapital());
                        double capitalDiferencia = (capital - interes - cargoMora);
                        countCuotas = capitalRestante < amortizacionGeneralSeleccionada.getPagoCapital() ? countCuotas + 1 : countCuotas;
                        
                        if(countCuotas > pendingCuotas){
                            countCuotas = pendingCuotas;
                        }
                        
                        pagoMultiple = "Pagadas las cuotas: ";
                        
                        for (int i = cuotaNum; i <= countCuotas; i++) {

                            pagoCuota = new PagoCuota();
                            pagoCuota.setIdPrestamo(amortizacionGeneralSeleccionada.getIdPrestamo());
                            pagoCuota.setCuotaNum(i);

                            if(i == cuotaNum){
                                pagoCuota.setCargoMora(cargoMora);
                                cargoMoraTemp = cargoMora;
                                pagoCuota.setInteres(interes);
                                pagoCuota.setCheck(true);
                            }else {
                                pagoCuota.setCargoMora(0);
                                pagoCuota.setInteres(0);
                                pagoCuota.setCheck(false);
                            }         


                            //si el capital diferencia es menor a la cuota normal: agrega un pago como ABONO CAPITAL
                            if((int) capitalDiferencia < (int) amortizacionGeneralSeleccionada.getPagoCapital()){              
                                pagoCuota.setCapital(capitalDiferencia);
                                pagoCuota.setNotaPago("ABONO CAPITAL"); 
                            }

                            //si habia un restante de abono anterior: agrega un pago completo
                            if((int) capitalRestante < (int) amortizacionGeneralSeleccionada.getPagoCapital()){                       
                                pagoCuota.setCapital(capitalRestante);       
                                pagoCuota.setNotaPago("PAGO COMPLETO");
                                capitalDiferencia = capitalDiferencia - capitalRestante;
                                capitalRestante = amortizacionGeneralSeleccionada.getPagoCapital();    
                            }

                            //si el capital es mayor a la cuota normal: agrega un pago completo y restalo a capitalDiferencia
                            if((int) capitalDiferencia >= (int) amortizacionGeneralSeleccionada.getPagoCapital() && 
                               (int) pagoCuota.getCapital() <= 0){
                                pagoCuota.setCapital(amortizacionGeneralSeleccionada.getPagoCapital());
                                pagoCuota.setNotaPago("PAGO COMPLETO");  
                                capitalDiferencia = capitalDiferencia - amortizacionGeneralSeleccionada.getPagoCapital();  
                            }
                            

                            pagoCuota.setFecha(fechaActual);
                            pagoCuota.setCuota(pagoCuota.getInteres() + pagoCuota.getCargoMora() + pagoCuota.getCapital());

                            totalCapital = totalCapital + pagoCuota.getCapital();
                            totalInteres = totalInteres + pagoCuota.getInteres();
                            totalCargoMora = totalCargoMora + pagoCuota.getCargoMora();
                            totalCuota = totalCuota + pagoCuota.getCuota();

                            listPago.add(pagoCuota);

                            pagoMultiple = pagoMultiple + i + ",";
                        }

                    } else {
                            notaPago = updateNotaPago(capital, capitalRestante);

                            //generando un nuevo historialPago
                            pagoCuota = new PagoCuota();
                            pagoCuota.setIdPrestamo(amortizacionGeneralSeleccionada.getIdPrestamo());
                            pagoCuota.setCuotaNum(cuotaNum);
                            pagoCuota.setCargoMora(cargoMora);               
                            pagoCuota.setInteres(interes);
                            pagoCuota.setCapital(capital);
                            pagoCuota.setFecha(fechaActual);
                            pagoCuota.setNotaPago(notaPago);
                            pagoCuota.setCuota(interes + cargoMora + capital);
                            pagoCuota.setCheck(true);

                            totalCapital = totalCapital + pagoCuota.getCapital();
                            totalInteres = totalInteres + pagoCuota.getInteres();
                            totalCargoMora = totalCargoMora + pagoCuota.getCargoMora();
                            totalCuota = totalCuota + pagoCuota.getCuota();

                            listPago.add(pagoCuota);

                    }


                }

                //limpiando solo los campos de texto pagar
                capital = 0;
                interes = 0;
                cargoMora = 0;
        }else{
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se puede agregar un monto mayor al balance pendiente!");
            PrimeFaces.current().dialog().showMessageDynamic(message);    
        }
        }else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "El pago ya ha sido agregado");
            PrimeFaces.current().dialog().showMessageDynamic(message);
        }
            
    }
    
    
    public void checkChanges(){
        
        if(listPago.size() > 0 && (int) interesTemp != (int) totalInteres){
            checkBoxChanges = true;
        }else{
            checkBoxChanges = false;
        }
    }

    public void reCalcPago(){
        
        interes = totalInteres;
        capital = capitalTemp;
        cargoMora = cargoMoraTemp;
        listPago = null;
        checkDisable = true;
        checkBoxChanges = false;
        
        addPagoToList();
    }
    
    public void calcTotalInteres(){
        totalInteres = 0;
        for (PagoCuota pagoCuota : listPago) {
            totalInteres = totalInteres + pagoCuota.getInteres();
        }        
        checkChanges();
    }
    
    public void checkAll(){
        if(listPago != null && listPago.size() > 0){
            for (int i = 0; i < listPago.size() ; i++) {
            listPago.get(i).setCheck(!selectAll);
            checkInteres(listPago.get(i));
            }
            selectAll = !selectAll;
        }      
    }
    
    //actualiza el interes en la listPago
    public void checkInteres(PagoCuota pagoC){
        if(pagoC.isCheck()){
            pagoC.setInteres(amortizacionFacade
                    .getInteresByPrestamoCuota(amortizacionGeneralSeleccionada
                            .getIdPrestamo(), pagoC.getCuotaNum()));
        }else{
            pagoC.setInteres(0);
        }
        calcTotalInteres();        
    }
    

      //actualiza el estado de los pagos la amortizacion como: PAGADO si se completo el pago
    public void updateListAmortizacionToUpdate(){
        
        for (int i = 0; i < listPago.size(); i++) {
            for (int x = 0; x < listAmortizacionToUpdate.size(); x++) {
                if(listPago.get(i).getCuotaNum().equals(listAmortizacionToUpdate.get(x).getCuotaNum())){
                    if(listPago.get(i).getNotaPago().equals("PAGO COMPLETO")){
                        listAmortizacionToUpdate.get(x).setEstado("PAGADO");
                    }
                }
            }
        }
    }
    
    

    //guarda todos los cambios y nuevos pagos
    public void savePago() {
        
        boolean lastPaid = false;
        
        if (listPago!= null && listPago.size() >= 1 && amortizacionGeneralSeleccionada != null) {
            
            if(checkBoxChanges){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Primero debe Recalcular!");
                PrimeFaces.current().dialog().showMessageDynamic(message);
            }else{

            updateListAmortizacionToUpdate();
            
            //actualizando datos del prestamo
            double diferencia = 0;
            double ganancia = 0;

            diferencia = amortizacionGeneralSeleccionada.getIdPrestamo().getBalanceCapital() - totalCapital;
            ganancia = amortizacionGeneralSeleccionada.getIdPrestamo().getGanancia() + totalInteres + cargoMoraTemp;

            prestamoSeleccionado = amortizacionGeneralSeleccionada.getIdPrestamo();

            //guardando pago multiple
            navigationBean.saveHistorialPrestamo(7, pagoMultiple, "UPDATE", prestamoSeleccionado);
                       
            int lastCuota = amortizacionFacade.getLastAmortizacionByPrestamo(amortizacionGeneralSeleccionada.getIdPrestamo()).getCuotaNum();
                
            //comprobando si la ultima cuota de la lista fue pagada
            for (Amortizacion amo : listAmortizacionToUpdate) {
                if(amo.getCuotaNum() == lastCuota){
                    if(amo.getEstado().equals("PAGADO")){
                        lastPaid = true;
                    }
                }
            }
          
            //Actualizando el prestamo para finalizarlo y generar el historial FINISH
            if (lastPaid) {
                prestamoSeleccionado.setEstado(ESTADO_FINALIZADO);
                prestamoSeleccionado.setFechaFinal(fechaActual);

                //guardando ultimo pago en historial de prestamo
                navigationBean.saveHistorialPrestamo(6, "", "FINISH", prestamoSeleccionado);
            }

            prestamoSeleccionado.setBalanceCapital(diferencia);
            prestamoSeleccionado.setGanancia(ganancia);

            prestamoFacade.edit(prestamoSeleccionado);

            //actualizando el historial de pago
            for (PagoCuota pagoCuota : listPago) {
                HistorialPago historialPago = new HistorialPago();
                historialPago.setCuotaNum(pagoCuota.getCuotaNum());
                historialPago.setIdPrestamo(pagoCuota.getIdPrestamo());
                historialPago.setCapital(pagoCuota.getCapital());
                historialPago.setInteres(pagoCuota.getInteres());
                historialPago.setCargoMora(pagoCuota.getCargoMora());
                historialPago.setNotaPago(pagoCuota.getNotaPago());
                historialPago.setFecha(pagoCuota.getFecha());
                
                historialPagoFacade.create(historialPago);
            }
             
            //actualizando el estado de la amortizacion
            for (Amortizacion amortizacion : listAmortizacionToUpdate) {
                amortizacionFacade.edit(amortizacion);
            }
            
            updateListAmortizacion();

            messagesBean.info("Pago efectuado!");
                        
            
            PrimeFaces.current().executeScript("PF('r_pago').hide();");
            }
        }else{
            messagesBean.warnigId("Primero debe agregar al menos un pago!", "formPagar:messagePagar");
        }
    }
    
    public String updateViewText(){
        if(viewMode){
            return "Vista tabla";
        }else{
            return  "Vista tarjetas";
        }
    }
    
    public String getDisplayMode(){
        if(viewMode){
            return "flex";
        }else{
            return  "none";
        }
    }
    
    public String updateOrderText(){
        if(orderMode){
            return "Más Recientes";
        }else{
            return  "Más Antiguos";
        }
    }
    
    public void revertListAmortizacion(){
        listAmortizacion = Lists.reverse(listAmortizacion);
    }
    

    public void updateVisibleColumns() {
        estado = checkColumn("estado");
        foto_cedula = checkColumn("foto_cedula");
        nombre = checkColumn("nombre");
        cuota_num = checkColumn("cuota_num");
        cuota = checkColumn("cuota");
        fecha = checkColumn("fecha");
        pago_capital = checkColumn("pago_capital");
        saldo = checkColumn("saldo");
        pago_interes = checkColumn("pago_interes");
    }

    public void updateCheckListColumna() {
        if (!listCheckColumna.isEmpty()) {
            columnaFacade.removeAllColumnaByTable(COLUMN_TABLE);
            for (Columna columna : listCheckColumna) {
                columna.setValor(1);
                columnaFacade.edit(columna);
            }
            updateVisibleColumns();
        }
    }

    public void startVisibleColumns() {
        pago_interes = true;
        saldo = true;
        pago_capital = true;
        estado = true;
        foto_cedula = true;
        nombre = true;
        cuota_num = true;
        cuota = true;
        fecha = true;

    }

    public boolean checkColumn(String col) {
        try {
            Columna columna = listCheckColumna.stream()
                    .filter(Columna -> Columna
                    .getColumna()
                    .equals(col))
                    .findAny()
                    .orElse(null);

            if (columna.getValor() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    
    public void updateListAmortizacion() {

        fillListAmortizacion();

        if (buscarPrestamo != null) {
            List<Amortizacion> tempList = new ArrayList<>();

            try {
                tempList = listAmortizacion.stream()
                        .filter(Amortizacion -> Amortizacion
                        .getIdPrestamo()
                        .getIdPersona()
                        .getNombre()
                        .contains(buscarPrestamo.toUpperCase()))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                e.printStackTrace();
            }

            listAmortizacion = tempList;
        } else {
            fillListAmortizacion();
        }

    }
    
    

    //actualiza el prestamo que se detallara
    public void detailGridAmortizacionPrestamo() {
        if (amortizacionGridSeleccionada != null) {

            NavigationBean.prestamoDetalle = amortizacionGridSeleccionada.getIdPrestamo();
            NavigationBean.PRE_VENTANA = VENTANA;
            navigationBean.showPrestamo();
            detallePrestamoBean.fillFormPrestamo();
        }
    }

    //actualiza el prestamo que se detallara
    public void detailTableAmortizacionPrestamo() {
        if (amortizacionTableSeleccionada != null) {

            NavigationBean.prestamoDetalle = amortizacionTableSeleccionada.getIdPrestamo();
            NavigationBean.PRE_VENTANA = VENTANA;
            navigationBean.showPrestamo();
            detallePrestamoBean.fillFormPrestamo();
        }
    }

    //convierte el archivo a imagen y lo descarga con fileDownload (primefaces)
    public StreamedContent downloadFile(byte[] image) {
        if (image != null) {

            InputStream is = new ByteArrayInputStream((byte[]) image);
            StreamedContent sc = DefaultStreamedContent
                    .builder()
                    .contentType("image/png")
                    .contentType("image/jpg")
                    .contentType("image/jpeg")
                    .stream(() -> is).build();

            return sc;
        } else {
            messagesBean.error("No hay ningún archivo para descargar!");
            return null;
        }

    }

    public String getDateColor(Amortizacion amortizacion) {

        LocalDateTime fechaPago = amortizacion
                .getFecha()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        int diferenciaDias = (int) Duration.between(fechaPago, LocalDateTime.now()).toDays();

        if (fechaPago.isAfter(LocalDateTime.now())) {
            return "#25a244";
        } else if (diferenciaDias <= 1 || fechaPago.isEqual(LocalDateTime.now())) {
            return "#ffb703";
        } else {
            return "#e63946";
        }
    }

    public String formatDate(Date fecha) {
        return fecha != null ? sdf.format(fecha) : "";
    }

    public String formatMoney(double valor) {
        return String.format("%,.2f", valor);
    }

    public boolean isRowSelected() {

        if (amortizacionTableSeleccionada != null) {
            return true;
        } else {
            return false;
        }
    }

    //GETTERS AND SETTERS

    public boolean isViewMode() {
        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }      

    public boolean isOrderMode() {
        return orderMode;
    }

    public void setOrderMode(boolean orderMode) {
        this.orderMode = orderMode;
    }
    
    public Prestamo getPrestamoSeleccionado() {
        return prestamoSeleccionado;
    }

    public void setPrestamoSeleccionado(Prestamo prestamoSeleccionado) {
        this.prestamoSeleccionado = prestamoSeleccionado;
    }

    public double getBalancePendiente() {
        return balancePendiente;
    }

    public void setBalancePendiente(double balancePendiente) {
        this.balancePendiente = balancePendiente;
    }

    public List<PagoCuota> getListPago() {
        return listPago;
    }

    public void setListPago(List<PagoCuota> listPago) {
        this.listPago = listPago;
    }

    public double getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(double totalCapital) {
        this.totalCapital = totalCapital;
    }

    public double getTotalInteres() {
        return totalInteres;
    }

    public void setTotalInteres(double totalInteres) {
        this.totalInteres = totalInteres;
    }

    public double getTotalCargoMora() {
        return totalCargoMora;
    }

    public void setTotalCargoMora(double totalCargoMora) {
        this.totalCargoMora = totalCargoMora;
    }

    public double getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(double totalCuota) {
        this.totalCuota = totalCuota;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public double getInteres() {
        return interes;
    }

    public void setInteres(double interes) {
        this.interes = interes;
    }

    public String getNotaPago() {
        return notaPago;
    }

    public void setNotaPago(String notaPago) {
        this.notaPago = notaPago;
    }

    public boolean isCheckDisable() {
        return checkDisable;
    }

    public void setCheckDisable(boolean checkDisable) {
        this.checkDisable = checkDisable;
    }

    public double getCargoMora() {
        return cargoMora;
    }

    public void setCargoMora(double cargoMora) {
        this.cargoMora = cargoMora;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public boolean isCheckBoxChanges() {
        return checkBoxChanges;
    }

    public void setCheckBoxChanges(boolean checkBoxChanges) {
        this.checkBoxChanges = checkBoxChanges;
    }

    public int getCuotaNum() {
        return cuotaNum;
    }

    public void setCuotaNum(int cuotaNum) {
        this.cuotaNum = cuotaNum;
    }

    public String getBuscarPrestamo() {
        return buscarPrestamo;
    }

    public void setBuscarPrestamo(String buscarPrestamo) {
        this.buscarPrestamo = buscarPrestamo;
    }

    public Amortizacion getAmortizacionGridSeleccionada() {
        return amortizacionGridSeleccionada;
    }

    public void setAmortizacionGridSeleccionada(Amortizacion amortizacionSeleccionada) {
        this.amortizacionGridSeleccionada = amortizacionSeleccionada;
    }

    public Amortizacion getAmortizacionTableSeleccionada() {
        return amortizacionTableSeleccionada;
    }

    public void setAmortizacionTableSeleccionada(Amortizacion amortizacionTableSeleccionada) {
        amortizacionGeneralSeleccionada = amortizacionTableSeleccionada;
        this.amortizacionTableSeleccionada = amortizacionTableSeleccionada;
    }

    public Amortizacion getAmortizacionGeneralSeleccionada() {
        return amortizacionGeneralSeleccionada;
    }

    public void setAmortizacionGeneralSeleccionada(Amortizacion amortizacionGeneralSeleccionada) {
        this.amortizacionGeneralSeleccionada = amortizacionGeneralSeleccionada;
    }

    public List<Amortizacion> getListAmortizacion() {
        return listAmortizacion;
    }

    public void setListAmortizacion(List<Amortizacion> listAmortizacion) {
        this.listAmortizacion = listAmortizacion;
    }

    public List<Prestamo> getListPrestamo() {
        return listPrestamo;
    }

    public void setListPrestamo(List<Prestamo> listPrestamo) {
        this.listPrestamo = listPrestamo;
    }

    public List<Columna> getListColumna() {
        return listColumna;
    }

    public void setListColumna(List<Columna> listColumna) {
        this.listColumna = listColumna;
    }

    public List<Columna> getListCheckColumna() {
        return listCheckColumna;
    }

    public void setListCheckColumna(List<Columna> listCheckColumna) {
        this.listCheckColumna = listCheckColumna;
    }

    public boolean isNombre() {
        return nombre;
    }

    public void setNombre(boolean nombre) {
        this.nombre = nombre;
    }

    public boolean isCuota_num() {
        return cuota_num;
    }

    public boolean isCuota() {
        return cuota;
    }

    public boolean isFecha() {
        return fecha;
    }

    public void setFecha(boolean fecha_pago) {
        this.fecha = fecha_pago;
    }

    public void setCuota(boolean cuota) {
        this.cuota = cuota;
    }

    public void setCuota_num(boolean cuota_num) {
        this.cuota_num = cuota_num;
    }

    public boolean isFoto_cedula() {
        return foto_cedula;
    }

    public void setFoto_cedula(boolean foto_cedula) {
        this.foto_cedula = foto_cedula;
    }

    public boolean isPago_capital() {
        return pago_capital;
    }

    public void setPago_capital(boolean pago_capital) {
        this.pago_capital = pago_capital;
    }

    public boolean isSaldo() {
        return saldo;
    }

    public void setSaldo(boolean saldo) {
        this.saldo = saldo;
    }

    public boolean isPago_interes() {
        return pago_interes;
    }

    public void setPago_interes(boolean pago_interes) {
        this.pago_interes = pago_interes;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

}
