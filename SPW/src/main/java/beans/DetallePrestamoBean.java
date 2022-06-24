/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Amortizacion;
import entities.Archivo;
import entities.CustomEmail;
import entities.CustomReporte;
import entities.HistorialPago;
import entities.Intervalo;
import entities.Persona;
import entities.Prestamo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import services.EmailManager;
import sessions.AmortizacionFacade;
import sessions.ArchivoFacade;
import sessions.HistorialPagoFacade;
import sessions.PersonaFacade;
import sessions.PrestamoFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class DetallePrestamoBean implements Serializable {

    //Injects
    @Inject
    NavigationBean navigationBean;

    //EJBs
    @EJB
    PrestamoFacade prestamoFacade;

    @EJB
    AmortizacionFacade amortizacionFacade;

    @EJB
    HistorialPagoFacade historialPagoFacade;

    @EJB
    PersonaFacade personaFacade;

    @EJB
    ArchivoFacade archivoFacade;

    //servicios
    MessagesBean messagesBean;

    //listas
    private List<Amortizacion> listAmortizacion;
    private List<HistorialPago> listHistorialPago;
    private List<Archivo> listArchivo;
    private List<Archivo> listArchivosToDelete;
    private List<Archivo> listArchivosToAdd;

    //entidades
    private Prestamo prestamoDetallado;
    private Archivo nuevoArchivo;
    private Archivo archivoSeleccionado;
    private Persona personaSeleccionada;
    private UploadedFile uploadedFile;

    //inputStream general
    InputStream stream;

    //stream de imagen cargada
    private StreamedContent loadImage;

    //stream de archivo cargado
    private StreamedContent loadFile;

    //campo de archivo
    private byte[] file;

    //campos prestamo
    private double capitalPrestado;
    private double balance;
    private double ganancias;
    private int idPrestamo;
    private double tasa;
    private String tipoInteres;
    private double porcentajeMora;
    private int cantCuotas;
    private Intervalo periodo;
    private Intervalo intervaloPago;
    private String estado;
    private int plazo;
    private String concepto;
    private String descGarantia;
    private Date fechaInicio;
    private Date fechaFinal;

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

    //totales amortizacion 
    //pendientes
    private double totalPagoCapital;
    private double totalPagoInteres;
    private double totalCuota;
    private double totalSaldo;
    private int cantCuotasPendientes;
    
    //constantes
    private String DEFAULT_MAIL = "spwapplication@gmail.com";    

    //pagados
    private double totalPagoCapitalPagado;
    private double totalPagoInteresPagado;
    private double totalCuotaPagado;
    private int cantCuotasPagadas;

    //historialPago
    private double totalInteres;
    private double totalCapital;
    private double totalMora;

    //controles amortizacion
    private boolean enviarCorreo;
    private boolean imprimirPdf;
    
    //control para deshabilitar el refinanciamiento (cuando el prestamo esta saldado)
    private boolean checkRefinance;

    //herramientas
    SimpleDateFormat sdf;

    @PostConstruct
    private void init() {

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        messagesBean = new MessagesBean();
        
        if(NavigationBean.prestamoDetalle!= null){
            fillFormPrestamo();
        }
    }

    public void fillFormPrestamo() {

        cleanFields();
        disableRefinanciamiento();
        prestamoDetallado = NavigationBean.prestamoDetalle;

        //llenando la lista de amortizacion
        listAmortizacion = amortizacionFacade.getAmortizacionByPrestamo(prestamoDetallado);
        listHistorialPago = historialPagoFacade.getListHistorialPagoByPrestamo(prestamoDetallado);
        listArchivo = archivoFacade.getListArchivoByPrestamo(prestamoDetallado);
        personaSeleccionada = prestamoDetallado.getIdPersona();

        calcularTotalesAmortizacion();
        calcularTotalesHistorialPago();

        if (prestamoDetallado != null) {
            nombre = prestamoDetallado.getIdPersona().getNombre();
            celular = prestamoDetallado.getIdPersona().getCelular();
            capitalPrestado = prestamoDetallado.getCapitalPrestado();
            balance = prestamoDetallado.getBalanceCapital();
            ganancias = prestamoDetallado.getGanancia();
            idPrestamo = prestamoDetallado.getIdPrestamo();
            tasa = prestamoDetallado.getTasa();
            tipoInteres = prestamoDetallado.getTipoInteres();
            porcentajeMora = prestamoDetallado.getPorcentajeMora();
            cantCuotas = prestamoDetallado.getCantCuotas();
            periodo = prestamoDetallado.getIdIntervaloInteres();
            intervaloPago = prestamoDetallado.getIdIntervaloPago();
            estado = prestamoDetallado.getEstado();
            plazo = prestamoDetallado.getPlazo();
            concepto = prestamoDetallado.getConcepto() != null ? prestamoDetallado.getConcepto() : "";
            descGarantia = prestamoDetallado.getDescGarantia() != null ? prestamoDetallado.getDescGarantia() : "";
            fechaInicio = prestamoDetallado.getFechaInicio();
            fechaFinal = prestamoDetallado.getFechaFinal() != null ? prestamoDetallado.getFechaFinal() : null;

            PrimeFaces.current().ajax().update("@([id$=formPrestamo])");
        }
    }

    public void cleanFields() {

        //lista  de archivos
        listArchivo = null;
        listArchivosToDelete = new ArrayList<>();
        listArchivosToAdd = new ArrayList<>();

        //campos de prestamo
        capitalPrestado = 0;
        balance = 0;
        ganancias = 0;
        idPrestamo = 0;
        tasa = 0;
        tipoInteres = "";
        porcentajeMora = 0;
        cantCuotas = 0;
        periodo = null;
        intervaloPago = null;
        estado = "";
        plazo = 0;
        concepto = "";
        descGarantia = "";
        fechaInicio = null;
        fechaFinal = null;

        //campos totales
        totalPagoCapital = 0;
        totalPagoInteres = 0;
        totalCuota = 0;
        totalSaldo = 0;
        totalCuotaPagado = 0;
        totalPagoCapitalPagado = 0;
        totalPagoInteresPagado = 0;
        cantCuotasPendientes = 0;
        cantCuotasPagadas = 0;

        //campos persona
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

        //booleans amortizacion
        enviarCorreo = false;
        imprimirPdf = false;
        
        //booleans refinanciamiento
        checkRefinance = false;

        //campos total historial
        totalCapital = 0;
        totalInteres = 0;
        totalMora = 0;
    }

    public boolean checkFields() {
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

    public void editPersona() {
        if (personaSeleccionada != null) {
            nombre = personaSeleccionada.getNombre();
            cedula = personaSeleccionada.getCedula();
            direccion = personaSeleccionada.getDireccion();
            correo = personaSeleccionada.getCorreo();
            celular = personaSeleccionada.getCelular();
            telefono = personaSeleccionada.getTelefono();
            pin = personaSeleccionada.getPinTarjeta();
            numTarjeta = personaSeleccionada.getNumTarjeta();
            loadImage = downloadImage(personaSeleccionada.getFotoCedula()); //fotoCedula    

        } else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
        }

    }

    public void updatePersona() {
        if (checkFields()) {

            personaSeleccionada.setNombre(nombre.toUpperCase());
            personaSeleccionada.setCedula(cedula);
            personaSeleccionada.setDireccion(direccion.toUpperCase());
            personaSeleccionada.setPinTarjeta(pin);
            personaSeleccionada.setNumTarjeta(numTarjeta);
            personaSeleccionada.setCorreo(correo.toLowerCase());
            personaSeleccionada.setCelular(celular);
            personaSeleccionada.setTelefono(telefono);

            if (fotoCedula != null && fotoCedula != personaSeleccionada.getFotoCedula()) {
                personaSeleccionada.setFotoCedula(fotoCedula);
            }

            personaFacade.edit(personaSeleccionada);
            messagesBean.info("Datos de la Persona Actualizados!");
            PrimeFaces.current().executeScript("PF('dialogPersona').hide();");
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
    
    public boolean isFileContained(String nombre){
        boolean exist = false;
        for (Archivo archivo : listArchivo) {
            if(nombre.equals(archivo.getNombre()))
                exist = true;            
        }        
        return exist;
    }
    
    public void updateListArchivo(){
        
        if(listArchivosToAdd.size() > 0){
            for (Archivo archivo : listArchivosToAdd) {
                if(!isFileContained(archivo.getNombre())){
                    listArchivo.add(archivo);
                }
            }
        }
        
        if(listArchivosToDelete.size() > 0){
            for (Archivo archivo : listArchivosToDelete) {
                if(isFileContained(archivo.getNombre())){
                    listArchivo.remove(archivo);
                }
            }
        }
        
    }

    public void saveListArchivo(Prestamo prestamo) {
        if (prestamo != null) {
            
            if(listArchivosToAdd.size() > 0){
                for (Archivo archivo : listArchivosToAdd) {
                    archivo.setIdPrestamo(prestamo);
                    archivoFacade.create(archivo);

                }
            }
            
            if(listArchivosToDelete.size() > 0){
                for (Archivo archivo : listArchivosToDelete) {
                   archivoFacade.remove(archivo.getIdArchivo());
                }
            }
            
        }
    }
    
    public void deleteFile() {
        if (archivoSeleccionado != null) {
            listArchivosToDelete.add(archivoSeleccionado);
            updateListArchivo();
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

            listArchivosToAdd.add(nuevoArchivo);
            updateListArchivo();
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

    public void calcularTotalesHistorialPago() {

        for (HistorialPago historialPago : listHistorialPago) {
            totalInteres = totalInteres + historialPago.getInteres();
            totalCapital = totalCapital + historialPago.getCapital();
            totalMora = totalMora + historialPago.getCargoMora();
        }
    }

    public void calcularTotalesAmortizacion() {

        for (Amortizacion amortizacion : listAmortizacion) {

            if (amortizacion.getEstado().equals("PAGADO")) {

                totalSaldo = amortizacion.getSaldo();
                totalCuotaPagado = totalCuotaPagado + amortizacion.getCuota();
                totalPagoCapitalPagado = totalPagoCapitalPagado + amortizacion.getPagoCapital();
                totalPagoInteresPagado = totalPagoInteresPagado + amortizacion.getPagoInteres();
                cantCuotasPagadas++;
            }

            if (amortizacion.getEstado().equals("PENDIENTE")) {

                totalCuota = totalCuota + amortizacion.getCuota();
                totalPagoCapital = totalPagoCapital + amortizacion.getPagoCapital();
                totalPagoInteres = totalPagoInteres + amortizacion.getPagoInteres();
                cantCuotasPendientes++;
            }

        }

    }

    public void savePrestamo() {

        if (prestamoDetallado.getPorcentajeMora() > porcentajeMora
                || prestamoDetallado.getPorcentajeMora() < porcentajeMora) {
            //guardando porcentaje mora en historial de prestamo
            navigationBean.saveHistorialPrestamo(1, "" + porcentajeMora, "UPDATE", prestamoDetallado);
        }

        if (concepto != null) {
            if (prestamoDetallado.getConcepto() != null) {
                if (!prestamoDetallado.getConcepto().equals(concepto)) {
                    //guardando concepto en historial de prestamo
                    navigationBean.saveHistorialPrestamo(2, concepto.toUpperCase(), "UPDATE", prestamoDetallado);
                }
            } else {
                //guardando concepto en historial de prestamo
                navigationBean.saveHistorialPrestamo(2, concepto.toUpperCase(), "UPDATE", prestamoDetallado);
            }
        }

        if (descGarantia != null) {
            if (prestamoDetallado.getDescGarantia() != null) {
                if (!prestamoDetallado.getDescGarantia().equals(descGarantia)) {
                    //guardando descGarantia en historial de prestamo
                    navigationBean.saveHistorialPrestamo(3, descGarantia.toUpperCase(), "UPDATE", prestamoDetallado);
                }
            } else {
                //guardando descGarantia en historial de prestamo
                navigationBean.saveHistorialPrestamo(3, descGarantia.toUpperCase(), "UPDATE", prestamoDetallado);
            }
        }

        if (archivoFacade.getListArchivoByPrestamo(prestamoDetallado).size() > 0 || 
                listArchivosToAdd.size() > 0 || listArchivosToDelete.size() > 0) {
            
            
            String nombreArchivos = "";

            for (Archivo archivo : listArchivosToAdd) {
                nombreArchivos = nombreArchivos + archivo.getNombre() + ", ";
            }
            
            for (Archivo archivo : listArchivosToDelete) {
                nombreArchivos = nombreArchivos + archivo.getNombre() + ", ";
            }

            
            if (listArchivosToAdd.size() > 0) {
                navigationBean.saveHistorialPrestamo(4, nombreArchivos, "UPDATE", prestamoDetallado);
            } 

            if (listArchivosToDelete.size() > 0) {
                System.out.println("ListDel: " + nombreArchivos);
                navigationBean.saveHistorialPrestamo(8, nombreArchivos, "UPDATE", prestamoDetallado);
            }

        }

        prestamoDetallado.setPorcentajeMora(porcentajeMora);
        prestamoDetallado.setConcepto(concepto != null ? concepto.toUpperCase() : "");
        prestamoDetallado.setDescGarantia(descGarantia != null ? descGarantia.toUpperCase() : "");

        prestamoFacade.edit(prestamoDetallado);

        saveListArchivo(prestamoDetallado);

        messagesBean.info("Datos actualizados!");
        
        updateListArchivo();
        cleanFields();
        fillFormPrestamo();
    }
    
    
    //llena el objeto customReporte del reporte Historial Pago
    public CustomReporte getCustomReporteHistorialPago(){
        CustomReporte customReporte = new CustomReporte();
        File reportes = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/"));
        
        customReporte.setReporte("Historial_Pago.jasper");
        customReporte.getParametros().put("PATH", reportes.getAbsolutePath()+"/spw_logo2.png");
        customReporte.getParametros().put("CEL", "SI");
        customReporte.getParametros().put("ID_USUARIO", String.valueOf(NavigationBean.usuario.getIdUsuario()));
        customReporte.getParametros().put("ID_PRESTAMO", String.valueOf(NavigationBean.prestamoDetalle.getIdPrestamo()));
        customReporte.getParametros().put("ESQUEMA", NavigationBean.usuario.getEsquema());
        
        return customReporte;  
    }
    
    
    //llena el objeto customReporte del reporte Amortizacion Prestamo
    public CustomReporte getCustomReportAmortizacion(){
        CustomReporte customReporte = new CustomReporte();
        File reportes = new File(
                FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getRealPath("/resources/reportes/"));
        
        customReporte.setReporte("Amortizacion_Prestamo.jasper");
        customReporte.getParametros().put("PATH", reportes.getAbsolutePath()+"/spw_logo2.png");
        customReporte.getParametros().put("CEL", "SI");
        customReporte.getParametros().put("ID_USUARIO", String.valueOf(NavigationBean.usuario.getIdUsuario()));
        customReporte.getParametros().put("ID_PRESTAMO", String.valueOf(NavigationBean.prestamoDetalle.getIdPrestamo()));
        customReporte.getParametros().put("ESQUEMA", NavigationBean.usuario.getEsquema());
        
        return customReporte;    
    }
    
    //genera el reporte que se le pase como parametro
    public void generateReport(CustomReporte customReporte){
        navigationBean.printReport(customReporte);
    }
    
    
    //envia el correo con los datos del usuario
    public void sendEmail(){
        
        EmailManager emailManager = new EmailManager();      
        CustomEmail email = new CustomEmail();
        CustomReporte customReporte = getCustomReportAmortizacion();
        
        byte[] tablaAmortizacion = null;
        
        try {
            tablaAmortizacion = navigationBean.getByteReport(customReporte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        email.setRemitente(DEFAULT_MAIL);
        email.setDestinatario(NavigationBean.usuario.getCorreo());
        email.setAsunto("Amortización de Préstamo - "+NavigationBean.prestamoDetalle.getIdPersona().getNombre());
        email.setMensaje("Saludos Estimad@ \n \n"
                       + "Por medio del presente, le remito la Tabla de Amortización referente \n"
                       + "a su préstamo, el cual tiene un balance actual de RD$. "+ formatMoney(NavigationBean.prestamoDetalle.getBalanceCapital()) + " de un\n"
                       + "monto total inicial de RD$. "+formatMoney(NavigationBean.prestamoDetalle.getCapitalPrestado())
                       + ", en estado: "+ NavigationBean.prestamoDetalle.getEstado()+". \n \n"
                       + "Adjunto: Reporte_Amortizacion.pdf"+" \n \n"
                       + "Que tenga buen resto del dia!");
        email.setNombreArchivo("Reporte_Amortizacion.pdf");
        email.setArchivo(tablaAmortizacion);
        
        emailManager.SendEmail(email);
        
    }

    //ejecuta las acciones seleccionadas en el dialog AmortizacionEdit
    //acciones: enviar Correo e imprimir PDF
    public void executeActions() {
        //Acciones: enviar correo e imprimir PDF
        if(enviarCorreo){
            sendEmail();
        }
        
        if(imprimirPdf){
            generateReport(getCustomReportAmortizacion());
        }
    }
    
    //genera el reporte de historial de pago
    public void printReportHistorialPago(){
        generateReport(getCustomReporteHistorialPago());
    }
    
  
    
    //verifica si la ventana anterior es la ventana de refinanciamiento
    public void disableRefinanciamiento(){
        if(NavigationBean.PRE_VENTANA.endsWith("PRESTAMOS_SALDADOS")){
            checkRefinance = true;
        }else {
            checkRefinance = false;
        }
    }

    public String getColorByEstado(String estado) {

        if (estado.equals("PAGADO")) {
            return "#38b000";
        } else {
            return "#ff0000";
        }
    }

    public String formatDate(Date fecha) {
        return fecha != null ? sdf.format(fecha) : "";
    }

    public String formatMoney(double valor) {
        return String.format("%,.2f", valor);
    }

    public void setRefinanciar() {
        NavigationBean.refinanciar = true;
    }
    
    
    
    // GETTERS AND SETTERS
    
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }
    
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isCheckRefinance() {
        return checkRefinance;
    }

    public void setCheckRefinance(boolean checkRefinance) {
        this.checkRefinance = checkRefinance;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Archivo> getListArchivo() {
        return listArchivo;
    }

    public void setListArchivo(List<Archivo> listArchivo) {
        this.listArchivo = listArchivo;
    }

    public Archivo getArchivoSeleccionado() {
        return archivoSeleccionado;
    }

    public void setArchivoSeleccionado(Archivo archivoSeleccionado) {
        this.archivoSeleccionado = archivoSeleccionado;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public StreamedContent getLoadImage() {
        return loadImage;
    }

    public void setLoadImage(StreamedContent loadImage) {
        this.loadImage = loadImage;
    }

    public StreamedContent getLoadFile() {
        return loadFile;
    }

    public void setLoadFile(StreamedContent loadFile) {
        this.loadFile = loadFile;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
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

    public byte[] getFotoCedula() {
        return fotoCedula;
    }

    public void setFotoCedula(byte[] fotoCedula) {
        this.fotoCedula = fotoCedula;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getTotalInteres() {
        return totalInteres;
    }

    public void setTotalInteres(double totalInteres) {
        this.totalInteres = totalInteres;
    }

    public double getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(double totalCapital) {
        this.totalCapital = totalCapital;
    }

    public double getTotalMora() {
        return totalMora;
    }

    public List<HistorialPago> getListHistorialPago() {
        return listHistorialPago;
    }

    public void setListHistorialPago(List<HistorialPago> listHistorialPago) {
        this.listHistorialPago = listHistorialPago;
    }

    public void setTotalMora(double totalMora) {
        this.totalMora = totalMora;
    }

    public int getCantCuotasPendientes() {
        return cantCuotasPendientes;
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

    public void setCantCuotasPendientes(int cantCuotasPendientes) {
        this.cantCuotasPendientes = cantCuotasPendientes;
    }

    public int getCantCuotasPagadas() {
        return cantCuotasPagadas;
    }

    public void setCantCuotasPagadas(int cantCuotasPagadas) {
        this.cantCuotasPagadas = cantCuotasPagadas;
    }

    public List<Amortizacion> getListAmortizacion() {
        return listAmortizacion;
    }

    public void setListAmortizacion(List<Amortizacion> listAmortizacion) {
        this.listAmortizacion = listAmortizacion;
    }

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

    public void setTotalSaldo(double totalSaldo) {
        this.totalSaldo = totalSaldo;
    }

    public double getTotalPagoCapitalPagado() {
        return totalPagoCapitalPagado;
    }

    public void setTotalPagoCapitalPagado(double totalPagoCapitalPagado) {
        this.totalPagoCapitalPagado = totalPagoCapitalPagado;
    }

    public double getTotalPagoInteresPagado() {
        return totalPagoInteresPagado;
    }

    public void setTotalPagoInteresPagado(double totalPagoInteresPagado) {
        this.totalPagoInteresPagado = totalPagoInteresPagado;
    }

    public double getTotalCuotaPagado() {
        return totalCuotaPagado;
    }

    public void setTotalCuotaPagado(double totalCuotaPagado) {
        this.totalCuotaPagado = totalCuotaPagado;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public double getCapitalPrestado() {
        return capitalPrestado;
    }

    public void setCapitalPrestado(double capitalPrestado) {
        this.capitalPrestado = capitalPrestado;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getGanancias() {
        return ganancias;
    }

    public void setGanancias(double ganancias) {
        this.ganancias = ganancias;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public String getTipoInteres() {
        return tipoInteres;
    }

    public void setTipoInteres(String tipoInteres) {
        this.tipoInteres = tipoInteres;
    }

    public double getPorcentajeMora() {
        return porcentajeMora;
    }

    public void setPorcentajeMora(double porcentajeMora) {
        this.porcentajeMora = porcentajeMora;
    }

    public int getCantCuotas() {
        return cantCuotas;
    }

    public void setCantCuotas(int cantCuotas) {
        this.cantCuotas = cantCuotas;
    }

    public Intervalo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Intervalo periodo) {
        this.periodo = periodo;
    }

    public Intervalo getIntervaloPago() {
        return intervaloPago;
    }

    public void setIntervaloPago(Intervalo intervaloPago) {
        this.intervaloPago = intervaloPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getDescGarantia() {
        return descGarantia;
    }

    public void setDescGarantia(String descGarantia) {
        this.descGarantia = descGarantia;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

}
