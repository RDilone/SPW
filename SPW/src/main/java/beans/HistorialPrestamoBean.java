/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Amortizacion;
import entities.HistorialPrestamo;
import entities.Prestamo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sessions.AmortizacionFacade;
import sessions.HistorialPrestamoFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class HistorialPrestamoBean implements Serializable{
    
    //Injects
    @Inject
    NavigationBean navigationBean;
    
    //EJBs
    @EJB
    HistorialPrestamoFacade historialPrestamoFacade;
    
    @EJB
    AmortizacionFacade amortizacionFacade;
    
    //listas
    List<HistorialPrestamo> listHistorialPrestamo;
    
    //entidades
    Prestamo prestamoSeleccionado;
    Amortizacion amortizacionPagada;
    
    //servicios
    MessagesBean messagesBean;
    SimpleDateFormat sdf;
    StreamedContent streamContent;
    
    @PostConstruct
    private void init(){
        messagesBean = new MessagesBean();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        cleanFields();
        fillListHistorialPrestamo();
    }
    
    public void fillListHistorialPrestamo(){
        prestamoSeleccionado = NavigationBean.prestamoDetalle;
        amortizacionPagada = amortizacionFacade.getLastAmortizacionPagadaByPrestamo(prestamoSeleccionado);
        listHistorialPrestamo = historialPrestamoFacade.getListHistorialPrestamoByPrestamo(prestamoSeleccionado);
    }
    
    public void cleanFields(){
        prestamoSeleccionado = null;
        listHistorialPrestamo = null;
    }
    
    
     //retorna la clase css correspondiente al tipo de cambio (historial_prestamo.xhtml)
    public String getClassTree(String tipoCambio){
        if(tipoCambio.equals("START") || tipoCambio.equals("REFINANCE")){
            return "cd-timeline__img--picture";
        }else if(tipoCambio.equals("UPDATE")){
            return "cd-timeline__img--location";
        }else if(tipoCambio.equals("FINISH")){
            return "cd-timeline__img--movie";
        }else {
            return "";
        }
    }
   
    //retorna la imagen correspondiente al tipo de cambio (historial_prestamo.xhtml)
    public StreamedContent getImageTree(String tipoCambio) {
        
        String imagen = "";
        
        if(tipoCambio.equals("START")){         
            imagen = "images/start.png";
        }else if(tipoCambio.equals("REFINANCE")){
            imagen = "images/refinance.png";
        }else if(tipoCambio.equals("UPDATE")){
            imagen = "images/update.png";
        }else if(tipoCambio.equals("FINISH")){
            imagen = "images/finish.png";
        }else {
            imagen = "images/cd-icon-picture.png";
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(imagen);
        
        try {
            byte[] st = stream.readAllBytes();

            InputStream is = new ByteArrayInputStream(st);

            StreamedContent sc = DefaultStreamedContent
                    .builder()
                    .contentType("image/png")
                    .stream(() -> is).build();
            streamContent = sc;
            
            return streamContent;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se encontro la imagen");
            return null;
        }
        
    }
    
    
    //retorna el nombre de la imagen correspondiente al tipo de cambio (historial_prestamo.xhtml)
    public String getTitleTree(String tipoCambio){
        if(tipoCambio.equals("START")){
            return "Creación";
        }else if(tipoCambio.equals("REFINANCE")){
            return "Refinanciamiento";
        }else if(tipoCambio.equals("UPDATE")){
            return "Actualización";
        }else if(tipoCambio.equals("FINISH")){
            return "Saldado";
        }else {
            return "Default";
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
            messagesBean.error("No tiene imagen cargada!");
            return null;
        }
    }
    

    public String formatDate(Date fecha) {
        return fecha != null ? sdf.format(fecha) : "";
    }

    public String formatMoney(double valor) {
        return String.format("%,.2f", valor);
    }
    
    
    //GETTERS & SETTERS

    public List<HistorialPrestamo> getListHistorialPrestamo() {
        return listHistorialPrestamo;
    }

    public void setListHistorialPrestamo(List<HistorialPrestamo> listHistorialPrestamo) {
        this.listHistorialPrestamo = listHistorialPrestamo;
    }

    public Prestamo getPrestamoSeleccionado() {
        return prestamoSeleccionado;
    }

    public void setPrestamoSeleccionado(Prestamo prestamoSeleccionado) {
        this.prestamoSeleccionado = prestamoSeleccionado;
    }

    public Amortizacion getAmortizacionPagada() {
        return amortizacionPagada;
    }

    public void setAmortizacionPagada(Amortizacion amortizacionPagada) {
        this.amortizacionPagada = amortizacionPagada;
    }

    
    
    
}
