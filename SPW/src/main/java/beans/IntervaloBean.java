/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Intervalo;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sessions.IntervaloFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class IntervaloBean implements Serializable {

    @EJB
    IntervaloFacade intervaloFacade;

    MessagesBean messagesBean;

    private List<Intervalo> listIntervalo;
    private List<Intervalo> listEmptyIntervalo;
    private Intervalo intervaloSeleccionado;
    private Intervalo newIntervalo;

    private boolean newEdit;
    
    //campos de intervalo
    private String intervalo;
    private String plural; //significa: mensual: plural: meses
    private int cantDias;

    @PostConstruct
    private void init() {
        messagesBean = new MessagesBean();
        newEdit = false;
        fillListIntervalo();
    }

    public void fillListIntervalo() {
        listIntervalo = intervaloFacade.findAll(NavigationBean.DEFAULT_USER);
        cleanFields();
    }

    public void newIntervalo() {
        if(intervalo != null && plural != null){
            if (intervalo.length() <= 3 || plural.length() <= 3 || cantDias <= 0) {
            messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
            } else {
                newIntervalo = new Intervalo();
                newIntervalo.setIntervalo(intervalo.toUpperCase());
                newIntervalo.setPlural(plural.toUpperCase());
                newIntervalo.setCantDias(cantDias);
                intervaloFacade.create(newIntervalo);
                messagesBean.info("Intervalo creado!");
                PrimeFaces.current().executeScript("PF('dialogIntervalo').hide();");
                fillListIntervalo();
            }
        }      
    }
    
    public void newEditIntervalo(){
        if(newEdit){
            newIntervalo();
        }else{
            updateIntervalo();
        }
    }

    public void editIntervalo() {
        if(intervaloSeleccionado != null){
            intervalo = intervaloSeleccionado.getIntervalo();
            plural = intervaloSeleccionado.getPlural();
            cantDias = intervaloSeleccionado.getCantDias();
            
        }
        else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
        }
        
    }

    public void updateIntervalo() {
        if (intervalo.length() <= 3 || plural.length() <= 3 || cantDias <= 0) {
            messagesBean.error("Hay uno o mas campos que no fueron llenados correctamente!");
        } else {
            intervaloSeleccionado.setIntervalo(intervalo.toUpperCase());
            intervaloSeleccionado.setPlural(plural.toUpperCase());
            intervaloSeleccionado.setCantDias(cantDias);
            intervaloFacade.edit(intervaloSeleccionado);
            messagesBean.info("Intervalo Actualizado!");
            fillListIntervalo();
        }

    }

    public boolean removeIntervalo() {
        if (intervaloSeleccionado != null) {
            intervaloFacade.remove(intervaloSeleccionado.getIdIntervalo());          
            messagesBean.info("Intervalo eliminado!");
            fillListIntervalo();
            return true;
        } else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
            return false;
        }
        
    }

    public boolean isRowSelected() {
        if (intervaloSeleccionado != null) {
            return true;
        } else {
            return false;
        }
    }

    public void cleanFields() {
        intervalo = "";
        plural = "";
        cantDias = 0;
    }

    //GETTERS and SETTERS

    public boolean isNewEdit() {
        return newEdit;
    }

    public void setNewEdit(boolean newEdit) {
        this.newEdit = newEdit;
    }
    
    public List<Intervalo> getListIntervalo() {
        return listIntervalo;
    }

    public void setListIntervalo(List<Intervalo> listIntervalo) {
        this.listIntervalo = listIntervalo;
    }

    public List<Intervalo> getListEmptyIntervalo() {
        return listEmptyIntervalo;
    }

    public void setListEmptyIntervalo(List<Intervalo> listEmptyIntervalo) {
        this.listEmptyIntervalo = listEmptyIntervalo;
    }
    

    public Intervalo getIntervaloSeleccionado() {
        return intervaloSeleccionado;
    }

    public void setIntervaloSeleccionado(Intervalo intervaloSeleccionado) {
        this.intervaloSeleccionado = intervaloSeleccionado;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public int getCantDias() {
        return cantDias;
    }

    public void setCantDias(int valorDias) {
        this.cantDias = valorDias;
    }

}
