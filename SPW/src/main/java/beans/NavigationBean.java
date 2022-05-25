/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Amortizacion;
import entities.HistorialPrestamo;
import entities.Prestamo;
import entities.Usuario;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;
import services.EncryptMD5;
import services.SchemaTenantResolver;
import sessions.HistorialPrestamoFacade;
import sessions.UsuarioFacade;

//import org.primefaces.context.RequestContext;
/**
 * Para manejo de fecha optimizada: import java.time.Duration; //objeto en
 * 00:00:00 import java.time.LocalDate; //fecha en dd/mm/yyyy 00:00:00 con mas
 * funcionalidades
 *
 * @author emmanuel
 */
@Named
@SessionScoped
public class NavigationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String contenido;

    private MessagesBean messagesBean;
    
    @EJB
    HistorialPrestamoFacade historialPrestamoFacade;
    
    @Inject
    private NuevoPrestamoBean nuevoPrestamoBean;

    private String redirect;
    private String opcion;
    
    //datos de usuario
    public static String loggedUser;
    private String user;
    private String pass;
    public static Usuario usuario;
    
    //prestamo a detallar
    public static Prestamo prestamoDetalle;
    
    //control para cuando se vaya a refinanciar
    public static boolean refinanciar = false;
    
    //lista de eventos de prestamo
    public static List<String> listEventosPrestamo;
    
    //lista de usuarios (esquema: public) 
    public static List<Usuario> defaultUserList;
    
    //control pre-ventana 
    public static String PRE_VENTANA;
    
    //default tab MisPrestamos
    private int defaultTab;
    //fecha
    Date fechaActual;
    SimpleDateFormat sdf;
    String fecha;
    
    //campos del dialog
    private String currentPass;
    private String newPass;
    private String confirmPass;

    //control validadar password
    private boolean checkPassword;

    //constantes
    private static final int MIN_DIGITS_PASSWORD = 4;
    private static final String DEFAULT_USER = "JODILONE";

    @EJB
    private UsuarioFacade usuarioFacade;

    

    @PostConstruct
    private void init() {
        
        fechaActual = new Date();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        fecha = sdf.format(fechaActual);        
        
        redirect = "?faces-redirect=true";
        contenido = "/pages/welcome.xhtml";
        opcion = "Colocar opción aquí";
        
        defaultTab = 0;

        messagesBean = new MessagesBean();

        loggedUser = DEFAULT_USER;
        usuario = usuarioFacade.findUsuarioByUser(loggedUser);
        
        
        cleanFields();
        fillListEventosPrestamo();
        fillMapUserList();
        fillDefaultUserList();

        //luego de aqui se trabajara con los datos del usuario que se loguee...
        
    }

    //llena la lista de posibles descripciones de actualizaciones y eventos de un prestamo
    public void fillListEventosPrestamo(){
        listEventosPrestamo = new ArrayList<>();
        
        listEventosPrestamo.add("Creación del préstamo a nombre de: ");
        listEventosPrestamo.add("Cambiando porcentaje mora a: ");
        listEventosPrestamo.add("Actualizando concepto: ");
        listEventosPrestamo.add("Actualizando descripción de la garantía: ");
        listEventosPrestamo.add("Adjuntando archivos: ");
        listEventosPrestamo.add("Refinanciando préstamo a nombre de: ");
        listEventosPrestamo.add("Saldado el último pago, Finalizando préstamo");
        listEventosPrestamo.add("Realizando pago múltiple: ");
        listEventosPrestamo.add("Eliminando archivos: ");
    }
    
    public void fillDefaultUserList(){
        defaultUserList = usuarioFacade.findAll();
    }
    
    
    public String login(){
        usuario = new Usuario();
        boolean exist = false;
        
        for (Usuario us : usuarioFacade.findAll()) {
            if(us.getNombre().equals(user)){
                exist = true;
                usuario = us;
            }
        }
        
        if(exist){
            if(usuario.getContrasena().equals(EncryptMD5.encrypt(pass))){
                loggedUser = usuario.getNombre();
                return "main.xhtml?faces-redirect=true";
            }else{
                messagesBean.error("Contraseña icorrecta!");
                return "";
            }
        }else {
            messagesBean.error("Este usuario no existe!");
            return "";
        }
        
        
    }
    
    //muestra la ventana que precedio a detallePrestamo
    public void showPreVentana(){
        switch(PRE_VENTANA){
            case "MIS_PRESTAMOS": {
                showMisPrestamos();
            }break;
            case "PRESTAMOS_SALDADOS": {
                showMisPrestamosSaldados();
            }break;
        }
    }
    
    
    //guarda las actualizaciones y eventos del prestamo en cuestion en historialPrestamo 
    //Este metodo acepta 4 posibles tipoCambio: START, UPDATE, REFINANCE y FINISH
    public void saveHistorialPrestamo(int indice, String valor, String tipoCambio, Prestamo prestamo) {

        HistorialPrestamo historialPrestamo;
        String nuevoCambio = NavigationBean.listEventosPrestamo.get(indice);

        fechaActual = new Date();

        if (historialPrestamoFacade.getLastChangeDateByPrestamo(prestamo) != null) {
            
            historialPrestamo = historialPrestamoFacade.getLastChangeDateByPrestamo(prestamo);
            
            LocalDate fecha_cambio = historialPrestamoFacade
                    .getLastChangeDateByPrestamo(prestamo)
                    .getFechaCambio()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (fecha_cambio.isEqual(LocalDate.now()) && historialPrestamo.getTipoCambio().equals(tipoCambio)) {
                historialPrestamo.setDescripcionCambio(historialPrestamo.getDescripcionCambio() + ", " + nuevoCambio + valor);
                historialPrestamoFacade.edit(historialPrestamo);
            } else {
                historialPrestamo = new HistorialPrestamo();
                historialPrestamo.setIdPrestamo(prestamo);
                historialPrestamo.setDescripcionCambio(nuevoCambio + valor);
                historialPrestamo.setTipoCambio(tipoCambio);
                historialPrestamo.setFechaCambio(fechaActual);
                historialPrestamoFacade.create(historialPrestamo);
            }
        } else {
            historialPrestamo = new HistorialPrestamo();
            historialPrestamo.setIdPrestamo(prestamo);
            historialPrestamo.setTipoCambio(tipoCambio);
            historialPrestamo.setDescripcionCambio(nuevoCambio + valor);
            historialPrestamo.setFechaCambio(fechaActual);
            historialPrestamoFacade.create(historialPrestamo);
        }

    }

    public void cleanFields() {
        currentPass = "";
        newPass = "";
        confirmPass = "";
    }
    
    public void changeDefaultTab(){
        switch(defaultTab){
            case 0 : { defaultTab = 1; } break;
            case 1 : { defaultTab = 0; } break;
        }
    }

    public boolean validateConfirmPassword() {
        if (newPass.equals(confirmPass)) {
            return true;
        } else {
            messagesBean.error("La contraseña de confirmacion y la nueva contraseña no son iguales!");
            return false;
        }
    }

    public boolean validateCurrentPassword() {
        if (EncryptMD5.encrypt(currentPass).equals(
                EncryptMD5.encrypt(usuarioFacade.findUsuarioByUser(
                        loggedUser).getContrasena()))) {
            return true;
        } else {
            messagesBean.error("La contraseña actual es incorrecta!");
            return false;
        }
    }

    public boolean validateEmptyPassword() {

        if (currentPass != null && newPass != null && confirmPass != null) {
            if (currentPass.length() > MIN_DIGITS_PASSWORD
                    && newPass.length() > MIN_DIGITS_PASSWORD
                    && confirmPass.length() > MIN_DIGITS_PASSWORD) {
                return true;
            } else {
                messagesBean.error("La nueva contraseña debe tener al menos 5 caracteres!");
                return false;
            }
        } else {
            messagesBean.error("La nueva contraseña debe tener al menos 5 caracteres!");
            return false;
        }

    }

    public boolean validateSamePassword() {
        if (!EncryptMD5.encrypt(newPass).equals(
                EncryptMD5.encrypt(usuarioFacade.findUsuarioByUser(
                        loggedUser).getContrasena()))) {
            return true;
        } else {
            messagesBean.error("La nueva contraseña no puede ser igual que la anterior!");
            return false;
        }
    }

    public boolean changeUserPassword() {

        if (validateEmptyPassword()
                && validateCurrentPassword()
                && validateConfirmPassword()
                && validateSamePassword()) {
            usuario = usuarioFacade.findUsuarioByUser(loggedUser);
            usuario.setContrasena(EncryptMD5.encrypt(newPass));
            usuarioFacade.edit(usuario);
            messagesBean.info("La contraseña fue actualizada!");
            //PrimeFaces.current().executeScript("PF('dialogContrasena').hide();");
            return true;
        } else {

            return false;
        }

    }

    public void fillMapUserList() {
        for (Usuario us : usuarioFacade.findAll()) {
            if (!us.getUsuario().equals(DEFAULT_USER)) {
                SchemaTenantResolver.userDatasourceMap.put(us.getUsuario(), us.getEsquema());
            }
        }
    }

    public void printSession() {
        System.out.println("Usuarios registrados: \n");
        for (Usuario u : usuarioFacade.findAll()) {
            System.out.println(u.getUsuario());
        }

        System.out.println("\n");
        System.out.println("Esquemas registrados: \n");
        System.out.println(SchemaTenantResolver.userDatasourceMap.values());
        System.out.println("\n");
        System.out.println("Usuario logueado: " + loggedUser);
    }


    //Call Methods   
    public void ejemploLlamarPagina() {
        contenido = "/pages/nueva_evaluacion.xhtml";
    }
    
    public void showWelcome() {
        contenido = "/pages/welcome.xhtml";
    } 

    public void showAjustesReporte() {
        contenido = "/pages/ajustes_reporte.xhtml";
    } 
    
    public void showPermisos() {
        contenido = "/pages/permisos.xhtml";
    }
    
    public void showReportes() {
        contenido = "/pages/reportes.xhtml";
    }

    public void showIntervalo() {
        contenido = "/pages/intervalos.xhtml";
    }

    public void showHistorialPrestamo() {
        contenido = "/pages/historial_prestamo.xhtml";
    }        
            
    public void showPersona() {
        contenido = "/pages/personas.xhtml";
    }

    public void showMisPrestamos() {
        contenido = "/pages/mis_prestamos.xhtml";
    }
    
    public void showMisPrestamosSaldados() {
        contenido = "/pages/mis_prestamos_saldados.xhtml";
    }

    public void showNuevoPrestamo() {
        contenido = "/pages/nuevo_prestamo.xhtml";
        if(refinanciar){
            nuevoPrestamoBean.fillFormRefinanciarPrestamo();
        }
    }

    public void showPrestamo() {
        contenido = "/pages/prestamos.xhtml";       
    }

    public void showUsuario() {
        contenido = "/pages/usuarios.xhtml";
    }
    
    public void showContrasena() {
        contenido = "/pages/contrasena.xhtml";
    }

    public String getFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }
   
    
    
    //GETTERS and SETTERS
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    
    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getDefaultTab() {
        return defaultTab;
    }

    public void setDefaultTab(int defaultTab) {
        this.defaultTab = defaultTab;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCurrentPass() {
        return currentPass;
    }

    public void setCurrentPass(String currentPass) {
        this.currentPass = currentPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        if(!contenido.equals("/pages/nuevo_prestamo.xhtml")){
            NavigationBean.refinanciar = false;
        }
        this.contenido = contenido;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    
}
