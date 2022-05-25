/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Permiso;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sessions.PermisoFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class PermisoBean implements Serializable{
    
    @EJB
    PermisoFacade permisoFacade;
    
    private List<Permiso> listPermiso;
    private List<Permiso> listEmptyPermiso;
    private Permiso newPermiso;
    private Permiso permisoSeleccionado;
    
    private boolean newEditPermiso;
    
    //campos de Permiso
    private String permiso;
    private String descripcion;
           
    MessagesBean messagesBean;
    
    
    @PostConstruct
    private void init(){
        messagesBean = new MessagesBean();
        
        permiso = "";
        descripcion = "";
        newEditPermiso = false;
        fillListPermiso();
    }
    
    
    public void newEditPermiso(){
        if(newEditPermiso){
            newPermiso();
        }else {
            updatePermiso();
       }
    }
    
    public void fillListPermiso(){
        listPermiso = permisoFacade.findAll();
        cleanFields();
    }
    
    
    public void cleanFields(){
        permiso = "";
        descripcion = "";
    }
    
    public void newPermiso(){
        if(permiso != null && descripcion != null){
            if(permiso.length() > 3 || descripcion.length() > 4){
                newPermiso = new Permiso();
                newPermiso.setPermiso(permiso.toUpperCase());
                newPermiso.setDescripcion(descripcion.toUpperCase());
                permisoFacade.create(newPermiso);
                messagesBean.info("Permiso Creado!");
                PrimeFaces.current().executeScript("PF('dialogPermiso').hide();");
                fillListPermiso();
            }else {
                messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
            }
        }
        
    }
    
    public void editPermiso(){
        if(permisoSeleccionado != null){
            permiso = permisoSeleccionado.getPermiso();
            descripcion = permisoSeleccionado.getDescripcion();
        }else{
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
        }
    }
    
    
    public void updatePermiso(){
        if(permiso.length() > 3 || descripcion.length() > 4){
            permisoSeleccionado.setPermiso(permiso.toUpperCase());
            permisoSeleccionado.setDescripcion(descripcion.toUpperCase());
            permisoFacade.edit(permisoSeleccionado);
            messagesBean.info("Permiso Actualizado!");
            fillListPermiso();
        }
    }
    
    
    public boolean removePermiso(){
        if(permisoSeleccionado != null){
            //permisoFacade.remove(permisoSeleccionado);
            permisoFacade.remove(permisoSeleccionado.getIdPermiso());
            messagesBean.info("Permiso eliminado!");
            fillListPermiso();
            return true;
        }else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
            return false;
        }
    }
    
    
    public boolean isRowSelected() {
        if (permisoSeleccionado != null) {
            return true;
        } else {
            return false;
        }
    }
    
    
    
    //GETTERS AND SETTERS

    public List<Permiso> getListPermiso() {
        return listPermiso;
    }

    public void setListPermiso(List<Permiso> listPermiso) {
        this.listPermiso = listPermiso;
    }

    public List<Permiso> getListEmptyPermiso() {
        return listEmptyPermiso;
    }

    public void setListEmptyPermiso(List<Permiso> listEmptyPermiso) {
        this.listEmptyPermiso = listEmptyPermiso;
    }

    public Permiso getPermisoSeleccionado() {
        return permisoSeleccionado;
    }

    public void setPermisoSeleccionado(Permiso permisoSeleccionado) {
        this.permisoSeleccionado = permisoSeleccionado;
    }

    public boolean isNewEditPermiso() {
        return newEditPermiso;
    }

    public void setNewEditPermiso(boolean newEditPermiso) {
        this.newEditPermiso = newEditPermiso;
    }

    public String getPermiso() {
        return permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    
    
}
