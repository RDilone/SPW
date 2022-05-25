/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Perfil;
import entities.Permiso;
import entities.Usuario;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import services.EncryptMD5;
import sessions.PerfilFacade;
import sessions.PermisoFacade;
import sessions.UsuarioFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class UsuarioBean implements Serializable{
    
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    PermisoFacade permisoFacade;
    @EJB
    PerfilFacade perfilFacade;
    
    //listas
    private List<Usuario> listUsuario;
    private List<Usuario> listEmptyUsuario;
    private List<Permiso> listPermisoSeleccionados;
    private List<Permiso> listPermisoDisponibles;
    
    //entidades
    private Usuario newUsuario;
    private Usuario usuarioSeleccionado;
    private Perfil newPerfil;
    
    //servicios
    //EncryptMD5.encrypt()
    
    //controla ejecucion de editar o crear usuario
    private boolean newEditUsuario;
    
    //variable de check para generar o no contraseña
    private boolean generarPassword;
    
    //campos de Usuario
    private String usuario;
    private String contrasena;
    private String esquema;
    private String nombre;
    private String correo;
    private String celular;
    
           
    MessagesBean messagesBean;
    
    
    @PostConstruct
    private void init(){
        messagesBean = new MessagesBean();
        
        usuario = "";
        contrasena = "";
        esquema = "";
        nombre = "";
        correo = "";
        celular = "";
        
        newEditUsuario = false;
        fillListUsuario();
        fillListPermisoDisponibles();
        fillListPermisoSeleccionados();
    }
    
//    public void printPerfil(){
//        System.out.println("Permisos: ");
//        for (Permiso permiso : listPermisoSeleccionados) {
//            System.out.println(permiso.getPermiso());
//        }
//    }
    
    public void fillListPermisoDisponibles(){
        listPermisoDisponibles = permisoFacade.findAll();
    }

    
    public void fillListPermisoSeleccionados(){
        if(usuarioSeleccionado != null){
            listPermisoSeleccionados = permisoFacade.listPerfilByUsuario(usuarioSeleccionado);          
        }       
    }
    
    public void fillListUsuario(){
        listUsuario = usuarioFacade.findAll();
        cleanFields();
    }
    
    
        
    public void newEditUsuario(){
        if(newEditUsuario){
            newUsuario();
        }else {
            updateUsuario();
       }
    }
    
    public void cleanFields(){
        generarPassword = true;
        usuario = "";
        contrasena = "";
        esquema = "";
        nombre = "";
        correo = "";
        celular = "";
    }
    
    public boolean checkFields(){
        if(usuario != null && esquema != null && nombre != null && correo != null && celular != null){
            if(usuario.length() > 3 && esquema.length() > 3 &&
               nombre.length() > 5 && correo.length() > 5 && celular.length() > 9){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
        
    }
    
    
    public void newUsuario(){
        if(checkFields()){
            newUsuario = new Usuario();
            
            if(contrasena == null || generarPassword){
                String pass = EncryptMD5.passwordGenerator(5);
                newUsuario.setContrasena(EncryptMD5.encrypt(pass));
                System.out.println("Contraseña generada: " + pass);
            }else {
                newUsuario.setContrasena(EncryptMD5.encrypt(contrasena));
            }
            
            newUsuario.setUsuario(usuario.toUpperCase());         
            newUsuario.setEsquema(esquema.toLowerCase());
            newUsuario.setNombre(nombre.toUpperCase());
            newUsuario.setCorreo(correo.toLowerCase());
            newUsuario.setCelular(celular);
            
            usuarioFacade.create(newUsuario);
            messagesBean.info("Usuario Creado!");
            PrimeFaces.current().executeScript("PF('dialogUsuario').hide();");
            fillListUsuario();
        }else {
            messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
        }
    }
    
    public void editUsuario(){
        cleanFields();
        if(usuarioSeleccionado != null){
            usuario = usuarioSeleccionado.getUsuario();
            contrasena = "";
            esquema = usuarioSeleccionado.getEsquema();
            nombre = usuarioSeleccionado.getNombre();
            correo = usuarioSeleccionado.getCorreo();
            celular = usuarioSeleccionado.getCelular();
        }else{
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
        }
    }
    
    
    public void updateUsuario(){
        if(checkFields()){
            
            if(contrasena == null){
                String pass = EncryptMD5.passwordGenerator(5);
                usuarioSeleccionado.setContrasena(EncryptMD5.encrypt(pass));
                System.out.println("Contraseña generada: " + pass);
            }else {
                usuarioSeleccionado.setContrasena(EncryptMD5.encrypt(contrasena));
            }
            
            usuarioSeleccionado.setUsuario(usuario.toUpperCase());            
            usuarioSeleccionado.setEsquema(esquema.toLowerCase());
            usuarioSeleccionado.setNombre(nombre.toUpperCase());
            usuarioSeleccionado.setCorreo(correo.toLowerCase());
            usuarioSeleccionado.setCelular(celular);
            
            usuarioFacade.edit(usuarioSeleccionado);
            messagesBean.info("Usuario Actualizado!");
            PrimeFaces.current().executeScript("PF('dialogUsuario').hide();");
            fillListUsuario();
        }
    }
    
    
    public boolean removeUsuario(){
        if(usuarioSeleccionado != null){
            usuarioFacade.remove(usuarioSeleccionado.getIdUsuario());
            messagesBean.info("Usuario eliminado!");
            fillListUsuario();
            return true;
        }else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
            return false;
        }
    }
    
    public void updatePermisosUsuario(){
        if(usuarioSeleccionado != null && !listPermisoSeleccionados.isEmpty()){
            perfilFacade.removePerfilByUsuario(usuarioSeleccionado);
            for (Permiso permiso : listPermisoSeleccionados) {
                newPerfil = new Perfil();
                newPerfil.setIdPermiso(permiso);
                newPerfil.setIdUsuario(usuarioSeleccionado);
                perfilFacade.create(newPerfil);        
            }
            messagesBean.info("Perfil guardado!");
        }
    }
    
    
    public boolean isRowSelected() {
        if (usuarioSeleccionado != null) {
            return true;
        } else {
            return false;
        }
    }
    
    
    
    //GETTERS AND SETTERS

    public List<Usuario> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(List<Usuario> listUsuario) {
        this.listUsuario = listUsuario;
    }

    public List<Usuario> getListEmptyUsuario() {
        return listEmptyUsuario;
    }

    public void setListEmptyUsuario(List<Usuario> listEmptyUsuario) {
        this.listEmptyUsuario = listEmptyUsuario;
    }

    public boolean isGenerarPassword() {
        return generarPassword;
    }

    public void setGenerarPassword(boolean generarPassword) {
        this.generarPassword = generarPassword;
    }

    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
        this.usuarioSeleccionado = usuarioSeleccionado;
    }

    public boolean isNewEditUsuario() {
        return newEditUsuario;
    }

    public void setNewEditUsuario(boolean newEditUsuario) {
        this.newEditUsuario = newEditUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<Permiso> getListPermisoSeleccionados() {
        return listPermisoSeleccionados;
    }

    public void setListPermisoSeleccionados(List<Permiso> listPermisoSeleccionados) {
        this.listPermisoSeleccionados = listPermisoSeleccionados;
    }

    public List<Permiso> getListPermisoDisponibles() {
        return listPermisoDisponibles;
    }

    public void setListPermisoDisponibles(List<Permiso> listPermisoDisponibles) {
        this.listPermisoDisponibles = listPermisoDisponibles;
    }



    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEsquema() {
        return esquema;
    }

    public void setEsquema(String esquema) {
        this.esquema = esquema;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
   
    
}
