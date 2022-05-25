/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Amortizacion;
import entities.Archivo;
import entities.HistorialPago;
import entities.HistorialPrestamo;
import entities.Intervalo;
import entities.Persona;
import entities.Prestamo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import sessions.AmortizacionFacade;
import sessions.ArchivoFacade;
import sessions.HistorialPagoFacade;
import sessions.HistorialPrestamoFacade;
import sessions.IntervaloFacade;
import sessions.PersonaFacade;
import sessions.PrestamoFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class NuevoPrestamoBean implements Serializable {

    //Injects
    @Inject
    NavigationBean navigationBean;
    
    //EJBs
    @EJB
    PrestamoFacade prestamoFacade;

    @EJB
    IntervaloFacade intervaloFacade;

    @EJB
    PersonaFacade personaFacade;

    @EJB
    ArchivoFacade archivoFacade;

    @EJB
    AmortizacionFacade amortizacionFacade;

    @EJB
    HistorialPagoFacade historialPagoFacade;

    //servicios
    MessagesBean messagesBean;

    //listas
    private List<Intervalo> listIntervalo;
    private List<Persona> listPersona;
    private List<Archivo> listArchivo;
    private List<String> listTipoInteres;
    private List<Amortizacion> listAmortizacion;

    //entidades
    private Prestamo nuevoPrestamo;
    private Prestamo prestamoRefinanciar;
    private Archivo nuevoArchivo;
    private Persona nuevaPersona;
    private Persona crearPersona;
    private Amortizacion amortizacion;
    private Archivo archivoSeleccionado;
    private Intervalo periodoSeleccionado;
    private Intervalo intervaloPagoSeleccionado;
    private Persona personaSeleccionada;
    private HistorialPago historialPago;
    private String tipoInteresSeleccionado;

    //inputStream general
    InputStream stream;

    //stream de imagen cargada
    private StreamedContent loadImage;

    //stream de archivo cargado
    private StreamedContent loadFile;

    //campo de archivo
    private byte[] file;
    private UploadedFile uploadedFile;

    //campos de persona
    private String nombre;
    private String cedula;
    private String direccion;
    private byte[] fotoCedula;
    private String numTarjeta;
    private int pin;
    private String correo;
    private String celular;
    private String telefono;

    //campos de prestamo
    private double monto;
    private int plazo;
    private double tasa;
    private double mora;
    private static final String ESTADO_DEFAULT = "PENDIENTE"; //utilizado tambien para la Amortizacion
    private String concepto;
    private String garantia;

    //totales prestamo
    private double totalPagoCapital;
    private double totalPagoInteres;
    private double totalCuota;
    private double totalSaldo;

    //booleans amortizacion
    private boolean enviarCorreo;
    private boolean imprimirPdf;

    //fecha / hora actual
    Date fecha;
    Date fechaPago;
    SimpleDateFormat sdf;

    @PostConstruct
    private void init() {
        messagesBean = new MessagesBean();
        listArchivo = new ArrayList<>();

        crearPersona = new Persona();
        crearPersona.setIdPersona(0);
        crearPersona.setNombre("NUEVA PERSONA");

        sdf = new SimpleDateFormat("dd/MM/yyyy");

        fillListPersona();
        fillListIntervalo();
        fillListTipoInteres();
        cleanFields();
        
        
    }

    //fill lists
    public void fillListPersona() {
        listPersona = new ArrayList<>();
        listPersona.add(crearPersona);

        for (Persona persona : personaFacade.findAll()) {
            listPersona.add(persona);
        }
    }

    public void fillListIntervalo() {
        listIntervalo = intervaloFacade.findAll();
    }

    public void fillListTipoInteres() {
        listTipoInteres = new ArrayList<>();
        listTipoInteres.add("SIMPLE");
        listTipoInteres.add("COMPUESTO");
    }

    public void fillFormRefinanciarPrestamo() {

        if (NavigationBean.prestamoDetalle != null) {

            fecha = new Date();

            Prestamo prestamo = NavigationBean.prestamoDetalle;
            monto = getMontoTotal(prestamo);
            periodoSeleccionado = prestamo.getIdIntervaloInteres();
            plazo = prestamo.getPlazo();
            tasa = prestamo.getTasa();
            intervaloPagoSeleccionado = prestamo.getIdIntervaloPago();
            tipoInteresSeleccionado = prestamo.getTipoInteres();
            mora = prestamo.getPorcentajeMora();
            personaSeleccionada = prestamo.getIdPersona();
            fechaPago = prestamo.getFechaInicio();
            garantia = prestamo.getDescGarantia();
            concepto = prestamo.getConcepto();
            listArchivo = archivoFacade.getListArchivoByPrestamo(prestamo);
            PrimeFaces.current().ajax().update("@([id$=formNuevoPrestamo])");
        }

    }

    //other methods
    public void openNewPersonaDialog() {
        if (personaSeleccionada == crearPersona) {
            cleanFields();
            PrimeFaces.current().executeScript("PF('dialogPersona').show();");
        }
    }

    public void cleanFields() {
        //variables archivo
        archivoSeleccionado = null;
        listArchivo = new ArrayList<>();

        //booleans prestamo
        enviarCorreo = false;
        imprimirPdf = false;

        //variables prestamo
        monto = 0;
        plazo = 0;
        tasa = 0;
        mora = 0;
        concepto = "";
        garantia = "";
        intervaloPagoSeleccionado = null;
        periodoSeleccionado = null;
        personaSeleccionada = null;
        tipoInteresSeleccionado = null;

        //variables persona
        nombre = "";
        cedula = "";
        direccion = "";
        fotoCedula = null;
        numTarjeta = "";
        pin = 0;
        correo = "";
        celular = "";
        telefono = "";
        setDefaultImage();
    }

    public boolean checkFieldsPersona() {
        if (nombre != null && cedula != null
                && direccion != null && celular != null) {

            if (nombre.length() > 3 && cedula.length() >= 11
                    && direccion.length() >= 5 && celular.length() >= 10) {
                return true;
            } else {
                messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
                return false;
            }
        } else {
            messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
            return false;
        }
    }

    public boolean checkFieldsPrestamo() {
        if (intervaloPagoSeleccionado != null && periodoSeleccionado != null
                && tipoInteresSeleccionado != null && personaSeleccionada != null
                && monto > 0 && plazo > 0 && tasa > 0) {
            if (NavigationBean.refinanciar) {
                if (checkMontoRefinanciamiento(NavigationBean.prestamoDetalle)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
            return false;
        }
    }

    public void newPrestamo() {
        if (checkFieldsPrestamo()) {
            fecha = new Date();

            int cuotas = Math.round(periodoSeleccionado.getCantDias() * plazo / intervaloPagoSeleccionado.getCantDias());

            nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setCapitalPrestado(monto);
            nuevoPrestamo.setPlazo(plazo);
            nuevoPrestamo.setTasa(tasa);
            nuevoPrestamo.setPorcentajeMora(mora);
            nuevoPrestamo.setCantCuotas(cuotas);
            nuevoPrestamo.setEstado(ESTADO_DEFAULT);
            nuevoPrestamo.setConcepto(concepto != null ? concepto.toUpperCase() : "");
            nuevoPrestamo.setDescGarantia(garantia != null ? garantia.toUpperCase() : "");
            nuevoPrestamo.setIdIntervaloInteres(periodoSeleccionado);
            nuevoPrestamo.setIdIntervaloPago(intervaloPagoSeleccionado);
            nuevoPrestamo.setTipoInteres(tipoInteresSeleccionado);
            nuevoPrestamo.setIdPersona(personaSeleccionada);
            nuevoPrestamo.setBalanceCapital(monto);
            nuevoPrestamo.setFechaInicio(fecha);

            prestamoFacade.returnCreate(nuevoPrestamo);

            saveListArchivo(nuevoPrestamo);
            saveAmortizacion(nuevoPrestamo);
            refinanciarPrestamo();         
            
            //guardando accion de crear el nuevo prestamo
            navigationBean.saveHistorialPrestamo(0, personaSeleccionada.getNombre(),"START", nuevoPrestamo);
            
            cleanFields();

            messagesBean.info("Nuevo Préstamo creado satisfactoriamente!");
        }
    }

    public void saveListArchivo(Prestamo prestamo) {
        if (prestamo != null && listArchivo.size() > 0) {
            for (Archivo archivo : listArchivo) {
                archivo.setIdPrestamo(prestamo);
                archivoFacade.create(archivo);
            }
        }
    }

    public void newPersona() {
        if (checkFieldsPersona()) {
            nuevaPersona = new Persona();
            nuevaPersona.setNombre(nombre.toUpperCase());
            nuevaPersona.setCedula(cedula);
            nuevaPersona.setDireccion(direccion.toUpperCase());
            nuevaPersona.setPinTarjeta(pin);
            nuevaPersona.setNumTarjeta(numTarjeta != null ? numTarjeta : "");
            nuevaPersona.setCorreo(correo != null ? correo.toLowerCase() : "");
            nuevaPersona.setCelular(celular);
            nuevaPersona.setTelefono(telefono != null ? telefono : "");

            if (fotoCedula != null) {
                nuevaPersona.setFotoCedula(fotoCedula);
            }

            personaFacade.returnCreate(nuevaPersona);
            messagesBean.info("Persona creada!");
            PrimeFaces.current().executeScript("PF('dialogPersona').hide();");
            fillListPersona();
            personaSeleccionada = nuevaPersona;
        }
    }

    public void setDefaultImage() {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        stream = loader.getResourceAsStream("images/cedula.png");

        try {
            byte[] st = stream.readAllBytes();

            InputStream is = new ByteArrayInputStream(st);

            StreamedContent sc = DefaultStreamedContent
                    .builder()
                    .contentType("image/png")
                    .stream(() -> is).build();
            loadImage = sc;

        } catch (Exception e) {
            e.printStackTrace();
            loadImage = null;
        }
    }

    //recibe el archivo a traves de event y lo guarda en una variable byte[] fotoCedula
    public void uploadImage(FileUploadEvent event) {
        if (event.getFile() != null) {
            fotoCedula = event.getFile().getContent();
            loadImage = downloadImage(fotoCedula);
            //PrimeFaces.current().ajax().update("img");
        }
    }

    //convierte el archivo a pdf y lo descarga con fileDownload (primefaces)
    public StreamedContent downloadImage(byte[] image) {
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

    public void uploadFile(FileUploadEvent event) {
        if (event.getFile() != null) {
            loadFile = getFileCode(
                    event.getFile().getContent(),
                    event.getFile().getContentType(),
                    event.getFile().getFileName());

            nuevoArchivo = new Archivo();
            nuevoArchivo.setNombre(event.getFile().getFileName());
            nuevoArchivo.setExtension(event.getFile().getContentType());
            nuevoArchivo.setArchivo(event.getFile().getContent());

            listArchivo.add(nuevoArchivo);
            //PrimeFaces.current().ajax().update("img");
        }
    }

    public StreamedContent getFileCode(byte[] file, String extension, String nombre) {

        if (file != null) {

            InputStream is = new ByteArrayInputStream((byte[]) file);
            StreamedContent sc = DefaultStreamedContent
                    .builder()
                    .contentType(extension)
                    .name(nombre)
                    .stream(() -> is)
                    .build();

            return sc;
        } else {
            messagesBean.error("No hay ningún archivo para descargar!");
            return null;
        }
    }

    public StreamedContent downloadSelectedFile() {

        if (archivoSeleccionado.getArchivo() != null) {

            InputStream is = new ByteArrayInputStream((byte[]) archivoSeleccionado.getArchivo());
            StreamedContent sc = DefaultStreamedContent
                    .builder()
                    .contentType(archivoSeleccionado.getExtension())
                    .name(archivoSeleccionado.getNombre())
                    .stream(() -> is)
                    .build();

            return sc;
        } else {
            messagesBean.error("No hay ningún archivo para descargar!");
            return null;
        }
    }

    public void setGarantia() {
        //hay que asignar el id de la garantia a los archivos
    }

    public void deleteFile() {
        if (archivoSeleccionado != null) {
            listArchivo.remove(archivoSeleccionado);
        }

    }

    /*Formulas de Intereses:
        
      Variables: 
        Capital final (CF)
        Capital inicial (CI)
        Interes (IN)
        Periodos (P) 
    
      Interes simple: CF = (CI x IN)P 
      
        Ejemplo:
            Datos:
                Capital: 1.000.000.
                Interés: 10%
                Periodo: 12 
    
                Fórmula:
                1.000.000 x 10% = 100.000 x 12 = 1,200,000
    
                CF = 1,100,000
    
    
    
      Interes compuesto: CF = CI(1 + IN)^P
      
        Ejemplo: 
    
            Datos: 
            CI = 1.000.000
            i = 0,02.
            p = 12.
            CF = ¿?  
    
            Entonces:

            CF= 1.000.000(1+0,02)^12
            CF=1.000.000(1,02)^12
    
            Resolvemos primero la potencia de 1.02 elevado a la 12 = 1,268241795

            Luego aplicamos el resultado al capital inicial:

            CF = 1.000.000 x 1,268241795 =  1.268.242.
     */
    public void generateAmortizacion() {

        if (checkFieldsPrestamo()) {

            PrimeFaces.current().executeScript("PF('t_amo').show();");

            listAmortizacion = new ArrayList<>();

            totalPagoCapital = 0;
            totalPagoInteres = 0;
            totalCuota = 0;
            totalSaldo = 0;

            int cuotas = Math.round(periodoSeleccionado.getCantDias()
                    * plazo / intervaloPagoSeleccionado.getCantDias());

            double pagoCapital = 0;
            double pagoInteres = 0;
            double cuota = 0;
            double saldo = 0;

            switch (tipoInteresSeleccionado) {
                case "SIMPLE": {

                    pagoCapital = monto / cuotas;

                    pagoInteres = ((tasa / 100) * monto * plazo) / cuotas;

                    cuota = pagoCapital + pagoInteres;

                    saldo = monto;

                    for (int i = 1; i <= cuotas; i++) {

                        amortizacion = new Amortizacion();
                        amortizacion.setCuotaNum(i);
                        amortizacion.setPagoCapital(pagoCapital);
                        amortizacion.setPagoInteres(pagoInteres);
                        amortizacion.setEstado(ESTADO_DEFAULT);
                        amortizacion.setCuota(cuota);
                        amortizacion.setSaldo(saldo);
                        amortizacion.setFecha(fechaPago);

                        listAmortizacion.add(amortizacion);

                        saldo = saldo - pagoCapital;

                        totalPagoCapital = totalPagoCapital + pagoCapital;
                        totalPagoInteres = totalPagoInteres + pagoInteres;
                        totalCuota = totalCuota + cuota;

                        fechaPago = DateUtils.addDays(fechaPago, intervaloPagoSeleccionado.getCantDias());
                    }

                    totalSaldo = saldo;

                }
                break;

                case "COMPUESTO": {

                    pagoCapital = monto / cuotas;

                    saldo = monto;

                    for (int i = 1; i <= cuotas; i++) {

                        pagoInteres = ((tasa / 100) * saldo * plazo) / cuotas;

                        cuota = pagoCapital + pagoInteres;

                        amortizacion = new Amortizacion();
                        amortizacion.setCuotaNum(i);
                        amortizacion.setPagoCapital(pagoCapital);
                        amortizacion.setPagoInteres(pagoInteres);
                        amortizacion.setEstado(ESTADO_DEFAULT);
                        amortizacion.setCuota(cuota);
                        amortizacion.setSaldo(saldo);
                        amortizacion.setFecha(fechaPago);

                        listAmortizacion.add(amortizacion);

                        saldo = saldo - pagoCapital;

                        totalPagoCapital = totalPagoCapital + pagoCapital;
                        totalPagoInteres = totalPagoInteres + pagoInteres;
                        totalCuota = totalCuota + cuota;

                        fechaPago = DateUtils.addDays(fechaPago, intervaloPagoSeleccionado.getCantDias());
                    }

                    totalSaldo = saldo;

                }
                break;
            }

        }
    }

    public void saveAmortizacion(Prestamo prestamo) {
        for (Amortizacion amort : listAmortizacion) {
            amort.setIdPrestamo(prestamo);
            amortizacionFacade.create(amort);
        }
    }


    public String formatDouble(double numero) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.##");
        return decimalFormat.format(numero);
    }

    public String formatMoney(double valor) {
        return String.format("%,.2f", valor);
    }

    public String getTipoInteresMessage() {
        if (tipoInteresSeleccionado == null) {
            return "Selecciona el Tipo de interés";
        } else if (tipoInteresSeleccionado.equals("SIMPLE")) {
            return "El interés SIMPLE se calcula en base al monto inicial \n"
                    + "prestado y no varía durante todo el trayecto de pago.";
        } else {
            return "El interés COMPUESTO se calcula en base al monto que va  \n"
                    + "quedando luego de cada pago y si varia (quedando menos) \n"
                    + "a lo largo del trayecto de pago";
        }

    }

    public boolean checkMontoRefinanciamiento(Prestamo prestamo) {
        if (monto <= getMontoTotal(prestamo)) {
            messagesBean.error("El monto para el préstamo de refinanciamiento debe ser mayor a: " + formatMoney(getMontoTotal(prestamo)));
            return false;
        } else {
            return true;
        }
    }

    public double getMontoTotal(Prestamo prestamo) {

        double totalCu = 0;
        double totalIn = 0;
        double totalCap = 0;

        historialPago = new HistorialPago();

        //sumando los totales de los pagos pendientes
        for (Amortizacion amort : amortizacionFacade.getAmortizacionByPrestamo(prestamo)) {

            if (amort.getEstado().equals("PENDIENTE")) {
                totalCu = totalCu + amort.getCuota();
                totalIn = totalIn + amort.getPagoInteres();
                totalCap = totalCap + amort.getPagoCapital();
            }
        }

        historialPago.setIdPrestamo(prestamo);
        historialPago.setCuotaNum(historialPagoFacade.maxCuotaNumByPrestamo(prestamo));
        historialPago.setCapital(totalCap);
        historialPago.setInteres(totalIn);
        historialPago.setCargoMora(0);
        historialPago.setNotaPago("REFINANCIAMIENTO");
        historialPago.setFecha(fecha);

        return totalCu;
    }

    public void refinanciarPrestamo() {

        if (NavigationBean.prestamoDetalle != null && NavigationBean.refinanciar) {

            prestamoRefinanciar = NavigationBean.prestamoDetalle;

            //actualizando datos del prestamo
            prestamoRefinanciar.setEstado("SALDADO");
            prestamoRefinanciar.setBalanceCapital(0);
            prestamoRefinanciar.setGanancia(prestamoRefinanciar.getGanancia() + historialPago.getInteres());
            prestamoRefinanciar.setFechaFinal(fecha);

            prestamoFacade.edit(prestamoRefinanciar);

            //actualizando datos de la amortizacion (pagando todas las cuotas pendientes)
            amortizacionFacade.payPendingAmortizacionByPrestamo(prestamoRefinanciar);

            //asignando totales de deuda al historial de pago y actualizando historial de pago, 1 solo pago
            //getMontoTotal(prestamoRefinanciar);
            historialPagoFacade.create(historialPago);

            //guardando refinanciamiento en historial (5 = refinanciamiento)
            navigationBean.saveHistorialPrestamo(5, prestamoRefinanciar.getIdPersona().getNombre(),"REFINANCE", prestamoRefinanciar);
            navigationBean.saveHistorialPrestamo(6, "","FINISH", prestamoRefinanciar);
            messagesBean.info("Préstamo anterior saldado, datos actualizados");
            
            PrimeFaces.current().ajax().update("@([id$=principal])");
            
            NavigationBean.refinanciar = false;
        }
    }
    
    
    
    //GETTERS AND SETTERS
    public double getTotalPagoCapital() {
        return totalPagoCapital;
    }

    public void setTotalPagoCapital(double totalPagoCapital) {
        this.totalPagoCapital = totalPagoCapital;
    }

    public double getTotalPagoInteres() {
        return totalPagoInteres;
    }

    public void setTotalPagoInteres(double totalPagoInteres) {
        this.totalPagoInteres = totalPagoInteres;
    }

    public double getTotalCuota() {
        return totalCuota;
    }

    public void setTotalCuota(double totalCuota) {
        this.totalCuota = totalCuota;
    }

    public double getTotalSaldo() {
        return totalSaldo;
    }

    public boolean isEnviarCorreo() {
        return enviarCorreo;
    }

    public void setEnviarCorreo(boolean enviarCorreo) {
        this.enviarCorreo = enviarCorreo;
    }

    public boolean isImprimirPdf() {
        return imprimirPdf;
    }

    public void setImprimirPdf(boolean imprimirPdf) {
        this.imprimirPdf = imprimirPdf;
    }

    public void setTotalSaldo(double totalSaldo) {
        this.totalSaldo = totalSaldo;
    }

    public List<Intervalo> getListIntervalo() {
        return listIntervalo;
    }

    public void setListIntervalo(List<Intervalo> listIntervalo) {
        this.listIntervalo = listIntervalo;
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public List<Persona> getListPersona() {
        return listPersona;
    }

    public void setListPersona(List<Persona> listPersona) {
        this.listPersona = listPersona;
    }

    public List<Archivo> getListArchivo() {
        return listArchivo;
    }

    public void setListArchivo(List<Archivo> listArchivo) {
        this.listArchivo = listArchivo;
    }

    public List<String> getListTipoInteres() {
        return listTipoInteres;
    }

    public void setListTipoInteres(List<String> listTipoInteres) {
        this.listTipoInteres = listTipoInteres;
    }

    public Intervalo getPeriodoSeleccionado() {
        return periodoSeleccionado;
    }

    public void setPeriodoSeleccionado(Intervalo periodoSeleccionado) {
        this.periodoSeleccionado = periodoSeleccionado;
    }

    public Intervalo getIntervaloPagoSeleccionado() {
        return intervaloPagoSeleccionado;
    }

    public void setIntervaloPagoSeleccionado(Intervalo intervaloPagoSeleccionado) {
        this.intervaloPagoSeleccionado = intervaloPagoSeleccionado;
    }

    public Persona getPersonaSeleccionada() {
        return personaSeleccionada;
    }

    public void setPersonaSeleccionada(Persona personaSeleccionada) {
        this.personaSeleccionada = personaSeleccionada;
    }

    public String getTipoInteresSeleccionado() {
        return tipoInteresSeleccionado;
    }

    public void setTipoInteresSeleccionado(String tipoInteresSeleccionado) {
        this.tipoInteresSeleccionado = tipoInteresSeleccionado;
    }

    public StreamedContent getLoadImage() {
        return loadImage;
    }

    public void setLoadImage(StreamedContent loadFile) {
        this.loadImage = loadFile;
    }

    public List<Amortizacion> getListAmortizacion() {
        return listAmortizacion;
    }

    public void setListAmortizacion(List<Amortizacion> listAmortizacion) {
        this.listAmortizacion = listAmortizacion;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public double getMora() {
        return mora;
    }

    public void setMora(double mora) {
        this.mora = mora;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public Persona getNuevaPersona() {
        return nuevaPersona;
    }

    public void setNuevaPersona(Persona nuevaPersona) {
        this.nuevaPersona = nuevaPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Archivo getArchivoSeleccionado() {
        return archivoSeleccionado;
    }

    public void setArchivoSeleccionado(Archivo archivoSeleccionado) {
        this.archivoSeleccionado = archivoSeleccionado;
    }

    public StreamedContent getLoadFile() {
        return loadFile;
    }

    public void setLoadFile(StreamedContent loadFile) {
        this.loadFile = loadFile;
    }

    public Amortizacion getAmortizacion() {
        return amortizacion;
    }

    public void setAmortizacion(Amortizacion amortizacion) {
        this.amortizacion = amortizacion;
    }

}
