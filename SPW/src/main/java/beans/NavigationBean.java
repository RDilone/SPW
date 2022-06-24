/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Amortizacion;
import entities.CustomReporte;
import entities.HistorialPrestamo;
import entities.Perfil;
import entities.Permiso;
import entities.Prestamo;
import entities.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.BasicConfigurator;
import org.primefaces.event.FlowEvent;
import services.EncryptMD5;
import services.SchemaTenantResolver;
import sessions.HistorialPrestamoFacade;
import sessions.PerfilFacade;
import sessions.PermisoFacade;
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

    
    @EJB
    private HistorialPrestamoFacade historialPrestamoFacade;
    
    @EJB
    private PermisoFacade permisoFacade;
            
    @EJB
    private PerfilFacade perfilFacade;
    
    @Inject
    private NuevoPrestamoBean nuevoPrestamoBean;

    
    private static final long serialVersionUID = 1L;
    private String contenido;

    private MessagesBean messagesBean;
    
    //db objects
    @Resource(name = "jdbc/dspw", lookup = "jdbc/dspw")
    private DataSource dataSource;

    private String redirect;
    private String opcion;
    
    //datos de usuario
    public static String loggedUser;
    private String user;
    private String pass;
    public static Usuario usuario;
    public static Usuario mainUsuario;
    
    //prestamo a detallar
    public static Prestamo prestamoDetalle;
    
    //perfil del usuario
    private List<Perfil> listPerfil;
    
    private HashMap perfilUsuario; 
    
    //control para cuando se vaya a refinanciar
    public static boolean refinanciar = false;
    
    //lista de eventos de prestamo
    public static List<String> listEventosPrestamo;
    
    //lista de usuarios (esquema: public) 
    public static List<Usuario> defaultUserList;
    
    //control pre-ventana (la ventana que le 
    //precede a la que se acaba de abrir)
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
    public static final String DEFAULT_USER = "JODILONE";

    @EJB
    private UsuarioFacade usuarioFacade;

    

    @PostConstruct
    private void init() {
        
        fechaActual = new Date();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        fecha = sdf.format(fechaActual);        
        perfilUsuario = new HashMap();
        
        
        redirect = "?faces-redirect=true";
        contenido = "/pages/welcome.xhtml";
        opcion = "Colocar opción aquí";
        
        defaultTab = 0;

        messagesBean = new MessagesBean();

        //loggedUser = "PRUEBAC";
        loggedUser = DEFAULT_USER;
        
        //usuario normal
        usuario = usuarioFacade.findUsuarioByUser(loggedUser);
        
        //usuario principal
        mainUsuario = usuarioFacade.findUsuarioByUser(DEFAULT_USER);
        
        cleanFields();
        fillListEventosPrestamo();
        fillMapUserList();
        loadPerfilUsuario();
        
        System.out.println("perfil de " + usuario.getUsuario() + ": \n " + perfilUsuario);
        
        //FUNCIONA LO DE INICIAR LA APLICACION CON OTRO USUARIO
    }
    
    
    /*Crea un esquema en base al nombre del parametro
      user y las tablas que usara */
    public void generateNewSchema(String user){
        
        HashMap<String, String> userParam = new HashMap<>();
        userParam.put("user", user);
        userParam.put("cs", "'"); //cs comilla simple
        
        StringSubstitutor sub = new StringSubstitutor(userParam);
        
        /*
         archivo generate_schema_user.sql contiene las sentencias 
         necesarias para crear un nuevo esquema y todas las tablas
         en que el usuario almacenara sus datos, automaticamente y 
         desde que comience a utilizar la aplicacion.*/
        String sql = getSQL("/resources/sql/generate_schema_user.sql");
        
        usuarioFacade.executeSQL(sub.replace(sql));
    }
    
    
    //elimina el esquema y todas las tablas dentro de el 
    //SI el esquema exis    te
    public void dropSchemaUser(String user){
        if(usuarioFacade.checkSchemaExist(user)){
            String drop = "drop schema sch_"+user + " cascade;";    
            usuarioFacade.executeSQL(drop);
        }       
    }
    
    
     //construye el reporte y retorna el byte[] del reporte pdf construido
    public byte[] getByteReport(CustomReporte reporte) throws SQLException, JRException {
        
        byte[] byteReport = null;
        
        String reportName = reporte.getReporte().substring(0, reporte.getReporte().length() - 7);

        try( Connection con = dataSource.getConnection() ) {

            FacesContext fc = FacesContext.getCurrentInstance();            
            ExternalContext ec = fc.getExternalContext();            
            HttpServletResponse res = (HttpServletResponse) fc.getExternalContext().getResponse();

            BasicConfigurator.configure();          
            
            res.setContentType("application/pdf");//inline
            res.addHeader("Content-disposition", "inline; filename="+reportName+".pdf");          
            
            File file = new File(ec.getRealPath("/resources/reportes/"+reporte.getReporte()));                         
            
            JasperReport rep = (JasperReport) JRLoader.loadObject(file);                                      
            
            JasperPrint jPrint = JasperFillManager.fillReport(rep, reporte.getParametros(), con);
            
            byteReport = JasperExportManager.exportReportToPdf(jPrint);
             

        } catch (JRException | SQLException e) {
            System.out.println("Error de Reporte: "+e.getMessage());
            System.out.println("Causa: "+e.getCause());
            e.getLocalizedMessage();
            e.printStackTrace();
        }
        
        return byteReport;
    }
    
    
     //construye el reporte y retorna el string correspondiente
    public String buildReport(CustomReporte reporte) throws SQLException, JRException {

        String reportName = reporte.getReporte().substring(0, reporte.getReporte().length() - 7);

//        System.out.println("datos de reporte: ");
//        System.out.println("Maps: " + reporte.getParametros());
//        System.out.println("Reporte: " + reporte.getReporte());
//        System.out.println("List parametros: " + reporte.getListParametros().size());
//        System.out.println("List fechaParametros: " + reporte.getListFechaParametros().size());
//        System.out.println("-----------------------------------------------");
        
        try( Connection con = dataSource.getConnection() ) {

            FacesContext fc = FacesContext.getCurrentInstance();            
            ExternalContext ec = fc.getExternalContext();            
            HttpServletResponse res = (HttpServletResponse) fc.getExternalContext().getResponse();

            BasicConfigurator.configure();          
            
            res.setContentType("application/pdf");//inline
            res.addHeader("Content-disposition", "inline; filename="+reportName+".pdf");          
            
            File file = new File(ec.getRealPath("/resources/reportes/"+reporte.getReporte()));                         
            
            JasperReport rep = (JasperReport) JRLoader.loadObject(file);                                      
            
            JasperPrint jPrint = JasperFillManager.fillReport(rep, reporte.getParametros(), con);
            
            
            try (ServletOutputStream servletOutputStream = res.getOutputStream()) {
                JasperExportManager.exportReportToPdfStream(jPrint, servletOutputStream);                
                servletOutputStream.flush();
            }
            FacesContext.getCurrentInstance().responseComplete();

        } catch (JRException | IOException e) {
            System.out.println("Error de Reporte: "+e.getMessage());
            System.out.println("Causa: "+e.getCause());
            e.getLocalizedMessage();
            e.printStackTrace();
        }
        return reportName;
    }

    
    //devuelve el string del reporte correspondiente al parametro reporte
    //genera el reporte en una pestaña nueva
    public String printReport(CustomReporte reporte) {
                      
        try {
            return buildReport(reporte);
        } catch (SQLException ex) {
            Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al Generar el archivo";
        } catch (JRException ex) {
            Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al Generar el archivo";
        }
    }

    //llena la lista de perfil de usuario en el objeto listPerfil
    public void fillListPerfilUsuario(Usuario user){
        listPerfil = perfilFacade.getPerfilByUsuario(user);
    }
    
    //define el map que tendra el perfil completo del usuario
    public void loadPerfilUsuario(){
        
        fillListPerfilUsuario(usuario);
        
        for (Permiso permiso : permisoFacade.findAll(DEFAULT_USER)) {
            Optional<Perfil> per = listPerfil
                    .stream()
                    .parallel()
                    .filter(Perfil -> Perfil
                            .getIdPermiso()
                            .equals(permiso))
                    .findAny();
            
            if(per.isPresent()){
                perfilUsuario.put(permiso.getPermiso(), true);
            }else {
                perfilUsuario.put(permiso.getPermiso(), false);
            }
        }
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
    
    //llena la lista por defecto de todos los usuarios. 
//    public void fillDefaultUserList(){
//        defaultUserList = usuarioFacade.findAll(DEFAULT_USER);
//    }
    
    
    //loguea la aplicacion con el usuario y contraseña
    public String login(){
        usuario = new Usuario();
        boolean exist = false;
        
        for (Usuario us : usuarioFacade.findAll(DEFAULT_USER)) {
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

    /*llena la lista de Maps del schemaTenantResolver que es 
      quien define la conexion a la base de datos en el esquema
      correspondiente */
    public void fillMapUserList() {
        for (Usuario us : usuarioFacade.findAll(DEFAULT_USER)) {
            if (!us.getUsuario().equals(DEFAULT_USER)) {
                SchemaTenantResolver.userDatasourceMap.put(us.getUsuario(), us.getEsquema());
            }
        }
    }
       
     /**
     * Recibe como parametro un String con la ruta de un archivo 
     * local (sql) para leer su contenido y devolver el contenido 
     * del archivo.
     * 
     * @param ruta del archivo
     * @return sentencia sql 
     */
    public String getSQL(String ruta) {                
        
        String sql = "";
        try {
            InputStream in = FacesContext
                    .getCurrentInstance()
                    .getExternalContext()
                    .getResourceAsStream(ruta);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            String tmp = "";
            while ((tmp = bf.readLine()) != null) {
                sql += tmp + "\n";
            }
            bf.close();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return sql;
    }     
    
    
    //devuelve el nombre del usuario logueado
    public String getUserName(){
        return usuario.getNombre();
    }
    

    public void printSession() {
        System.out.println("Usuarios registrados: \n");
        for (Usuario u : usuarioFacade.findAll(DEFAULT_USER)) {
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

    public HashMap getPerfilUsuario() {
        return perfilUsuario;
    }

    public void setPerfilUsuario(HashMap perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    
}
