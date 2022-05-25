/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Prestamo;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sessions.PrestamoFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class MisPrestamosSaldados implements Serializable{
    
    
    //Injects
    @Inject
    NavigationBean navigationBean;
    
    @Inject
    DetallePrestamoBean detallePrestamoBean;
    
    //EJB
    @EJB
    PrestamoFacade prestamoFacade;            
            
    //listas
    private List<Prestamo> listPrestamos;
    
    //entidades
    private Prestamo prestamoSeleccionado;    
       
    
    //objetos
    private StreamedContent loadImage;
    private SimpleDateFormat sdf;
    
    //variables
    private String buscarPrestamo;
    private String color; 
    private static final String VENTANA = "PRESTAMOS_SALDADOS";
    
    //mensajes
    MessagesBean messagesBean;
    
    
    @PostConstruct
    private void init(){
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        messagesBean = new MessagesBean();
        cleanFields();
        fillListPrestamosSaldados();
    }
    
    public void fillListPrestamosSaldados(){
        listPrestamos = prestamoFacade.getPaidLoan();
    }
    
    public void cleanFields(){
        listPrestamos = null;
        color = "";
        buscarPrestamo = "";
    }

    public void updateListAmortizacion() {

        fillListPrestamosSaldados();

        if (buscarPrestamo != null) {
            List<Prestamo> tempList = new ArrayList<>();

            try {
                tempList = listPrestamos.stream()
                        .filter(Prestamo -> Prestamo
                        .getIdPersona()
                        .getNombre()
                        .contains(buscarPrestamo.toUpperCase()))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                e.printStackTrace();
            }

            listPrestamos = tempList;
        } else {
            fillListPrestamosSaldados();
        }
        
        //System.out.println("lista Actualizada: "+listPrestamos.size());
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

    
     //actualiza el prestamo que se detallara
    public void showDetailsPrestamoSaldado() {
        if (prestamoSeleccionado != null) {
            NavigationBean.prestamoDetalle = prestamoSeleccionado;
            NavigationBean.PRE_VENTANA = VENTANA;
            navigationBean.showPrestamo();
            detallePrestamoBean.fillFormPrestamo();
        }
    }
    
    //metodo no usado
    public String getRandomColor(){
        
        List<String> listColor = new ArrayList<>();
        listColor.add("#f27059");
        listColor.add("#3a7ca5");
        listColor.add("#0e9594");
        listColor.add("#a44a3f");
        listColor.add("#815ac0");
        listColor.add("#99582a");
        listColor.add("#6a994e");
        listColor.add("#555b6e");
        listColor.add("#1a659e");
        listColor.add("#8f2d56");
        listColor.add("#fbb02d");
        listColor.add("#ff758f");
        listColor.add("#adc178");
        
        Random random = new Random();
        int index = random.nextInt(listColor.size());
        
        return listColor.get(index);
    }
    
    
    public String getDurationLoan(Date fecha_ini, Date fecha_fin){
        LocalDateTime fecha_inicio = fecha_ini
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        
        LocalDateTime fecha_final = fecha_fin
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return formatDuration(Duration.between(fecha_inicio, fecha_final).toDays());
    }
    
    
    public String formatDate(Date fecha) {
        return fecha != null ? sdf.format(fecha) : "";
    }

    public String formatMoney(double valor) {
        return String.format("%,.2f", valor);
    }

    public String formatDuration(double valor){
        if(valor <= 30.417){
            return String.format("%1.0f", valor) + " DIAS";
        }else{
            return String.format("%1.2f", (valor / 30.417)) + " MESES";
        }
        
    }
    
    //SETTERS & GETTERS

    public String getBuscarPrestamo() {
        return buscarPrestamo;
    }

    public void setBuscarPrestamo(String buscarPrestamo) {
        this.buscarPrestamo = buscarPrestamo;
    }

    public List<Prestamo> getListPrestamos() {
        return listPrestamos;
    }

    public void setListPrestamos(List<Prestamo> listPrestamos) {
        this.listPrestamos = listPrestamos;
    }

    public Prestamo getPrestamoSeleccionado() {
        return prestamoSeleccionado;
    }

    public void setPrestamoSeleccionado(Prestamo prestamoSeleccionado) {
        System.out.println("Prestamo: "+prestamoSeleccionado.getIdPersona().getNombre());
        this.prestamoSeleccionado = prestamoSeleccionado;
    }

    public StreamedContent getLoadImage() {
        return loadImage;
    }

    public void setLoadImage(StreamedContent loadImage) {
        this.loadImage = loadImage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    
}
