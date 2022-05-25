/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.CustomReporte;
import entities.Reporte;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DualListModel;
import sessions.PrestamoFacade;
import sessions.ReporteFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class AjustesReporteBean implements Serializable{
    
    //@EJBs   
    @EJB
    ReporteFacade reporteFacade;
    
    //listas
    private List<Reporte> listReporteParametros;
    private List<String> listFileReporte;
    
    //entidades
    private String reporteSeleccionado;       
    private Reporte parametroSeleccionado;
    
    //varibles
    private String nombreParametro;
    
    MessagesBean messagesBean;
    
    
    @PostConstruct
    private void init(){
        messagesBean = new MessagesBean();
        
        listFileReporte = new ArrayList<>();
        listReporteParametros = new ArrayList<>();
        
        //llena la lista de reportes (archivos) 
        fillListFileReporte();  
    }
    
    //llenando lista de reportes que estan en: resources/reportes/
    public void fillListFileReporte(){

        File reportes = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/"));
        
        if(reportes.listFiles().length > 0){
            for (File file : reportes.listFiles()) {              
                listFileReporte.add(file.getName());
            }
        }        
    }
    
    public void updateParameterList(String report){
        listReporteParametros = new ArrayList<>();        
        if(report != null){
            listReporteParametros = reporteFacade.getReporteListByName(report);
        }
    }
    
    
    public boolean isReportSelected(){
        if(reporteSeleccionado != null){
            return false;
        }else {
            return true;
        }
    }
    
    public boolean isParameterSelected(){
        if(parametroSeleccionado != null){
            return false;
        }else {
            return true;
        }
    }
    
    
    //agregar parametro
    public void addParametro(){
        Reporte reporte = new Reporte();
        reporte.setReporte(reporteSeleccionado);
        reporte.setParametro(nombreParametro.toUpperCase());
        reporteFacade.create(reporte);
        updateParameterList(reporteSeleccionado);
        
    }
    
    
    //eliminar parametro
    public void delParametro(){
        int idRporte = reporteFacade.getIdReporteByReporte(parametroSeleccionado);
        reporteFacade.remove(idRporte);
        updateParameterList(reporteSeleccionado);
    }

    
    //GETTERS & SETTERS
    
    

    public String getNombreParametro() {
        return nombreParametro;
    }

    public void setNombreParametro(String nombreParametro) {
        this.nombreParametro = nombreParametro;
    }

    public String getReporteSeleccionado() {
        return reporteSeleccionado;
    }

    public void setReporteSeleccionado(String reporteSeleccionado) {
        this.reporteSeleccionado = reporteSeleccionado;
    }

    public Reporte getParametroSeleccionado() {
        return parametroSeleccionado;
    }

    public void setParametroSeleccionado(Reporte parametroSeleccionado) {
        this.parametroSeleccionado = parametroSeleccionado;
    }

    public List<Reporte> getListReporteParametros() {
        return listReporteParametros;
    }

    public void setListReporteParametros(List<Reporte> listReporteParametros) {
        this.listReporteParametros = listReporteParametros;
    }

    public List<String> getListFileReporte() {
        return listFileReporte;
    }

    public void setListFileReporte(List<String> listFileReporte) {
        this.listFileReporte = listFileReporte;
    }
    
    
}
