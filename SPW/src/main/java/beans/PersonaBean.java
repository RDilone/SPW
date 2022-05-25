/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Columna;
import entities.Persona;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.shaded.commons.io.IOUtils;
import sessions.ColumnaFacade;
import sessions.PersonaFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class PersonaBean implements Serializable {

    @EJB
    PersonaFacade personaFacade;

    @EJB
    ColumnaFacade columnaFacade;

    MessagesBean messagesBean;

    //listas y entidades
    private List<Columna> listColumna;
    private List<Columna> listCheckColumna;
    private List<Persona> listPersona;
    private Persona personaSeleccionada;
    private Persona newPersona;

    //imagen de cedula
    StreamedContent loadImage;
    InputStream stream;

    private boolean newEdit;

    //controles de columnas
    private boolean col_id_persona;
    private boolean col_num_tarjeta;
    private boolean col_pin_tarjeta;
    private boolean col_cedula;
    private boolean col_direccion;
    private boolean col_foto_cedula;
    private boolean col_nombre;
    private boolean col_telefono;
    private boolean col_celular;
    private boolean col_correo;

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

    @PostConstruct
    private void init() {
        messagesBean = new MessagesBean();
        newEdit = false;

        fillListPersona();
        fillCheckListColumnas();
        startVisibleColumns();
        updateVisibleColumns();
    }

    public void fillCheckListColumnas() {
        listColumna = columnaFacade.listColumnaByTable("persona");
        listCheckColumna = columnaFacade.listCheckColumnaByTable("persona");
    }

    public void fillListPersona() {
        listPersona = personaFacade.findAll();
        cleanFields();
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

    public void newPersona() {
        if (checkFields()) {
            newPersona = new Persona();
            newPersona.setNombre(nombre.toUpperCase());
            newPersona.setCedula(cedula);
            newPersona.setDireccion(direccion.toUpperCase());
            newPersona.setPinTarjeta(pin);
            newPersona.setNumTarjeta(numTarjeta != null ? numTarjeta : "");
            newPersona.setCorreo(correo != null ? correo.toLowerCase() : "");
            newPersona.setCelular(celular);
            newPersona.setTelefono(telefono != null ? telefono : "");

            if (fotoCedula != null) {
                newPersona.setFotoCedula(fotoCedula);
            }

            personaFacade.create(newPersona);
            messagesBean.info("Persona creada!");
            PrimeFaces.current().executeScript("PF('dialogPersona').hide();");
            fillListPersona();
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
            loadImage = downloadFile(personaSeleccionada.getFotoCedula()); //fotoCedula            

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
            fillListPersona();
        }

    }

    public boolean removePersona() {
        if (personaSeleccionada != null) {
            personaFacade.remove(personaSeleccionada.getIdPersona());
            messagesBean.info("Persona eliminada!");
            fillListPersona();
            return true;
        } else {
            messagesBean.error("Primero debe seleccionar una fila de la tabla!");
            return false;
        }
    }

    public void updateCheckListColumna() {
        if (!listCheckColumna.isEmpty()) {
            columnaFacade.removeAllColumnaByTable("persona");
            for (Columna columna : listCheckColumna) {
                columna.setValor(1);
                columnaFacade.edit(columna);
            }
            updateVisibleColumns();
        }
    }

    public void startVisibleColumns() {
        col_id_persona = true;
        col_num_tarjeta = true;
        col_pin_tarjeta = true;
        col_cedula = true;
        col_direccion = true;
        col_foto_cedula = true;
        col_nombre = true;
        col_telefono = true;
        col_celular = true;
        col_correo = true;
    }

    public void updateVisibleColumns() {
        col_id_persona = checkColumn("id_persona");
        col_num_tarjeta = checkColumn("num_tarjeta");
        col_pin_tarjeta = checkColumn("pin_tarjeta");
        col_cedula = checkColumn("cedula");
        col_direccion = checkColumn("direccion");
        col_foto_cedula = checkColumn("foto_cedula");
        col_nombre = checkColumn("nombre");
        col_telefono = checkColumn("telefono");
        col_celular = checkColumn("celular");
        col_correo = checkColumn("correo");

    }

    public boolean checkColumn(String col) {
        try {
            Columna columna = listCheckColumna.stream()
                    .filter(Columna -> Columna
                    .getColumna()
                    .equals(col))
                    .findAny()
                    .orElse(null);

            if (columna.getValor() == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
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

    //recibe el archivo a traves de event y lo guarda en un objeto persona
    public void uploadFile(FileUploadEvent event) {
        if (event.getFile() != null) {
            fotoCedula = event.getFile().getContent();
            loadImage = downloadFile(fotoCedula);
            //PrimeFaces.current().ajax().update("img");
        }
    }

    //convierte el archivo a pdf y lo descarga con fileDownload (primefaces)
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

    public void newEditPersona() {
        if (newEdit) {
            newPersona();
        } else {
            updatePersona();
        }
    }

    public boolean isRowSelected() {
        if (personaSeleccionada != null) {
            return true;
        } else {
            return false;
        }
    }

    public void cleanFields() {
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

//GETTERS and SETTERS
    public boolean isNewEdit() {
        return newEdit;
    }

    public void setNewEdit(boolean newEdit) {
        this.newEdit = newEdit;
    }

    public List<Persona> getListPersona() {
        return listPersona;
    }

    public void setListPersona(List<Persona> listPersona) {
        this.listPersona = listPersona;
    }

    public Persona getPersonaSeleccionada() {
        return personaSeleccionada;
    }

    public void setPersonaSeleccionada(Persona personaSeleccionada) {
        this.personaSeleccionada = personaSeleccionada;
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

    public byte[] getFotoCedula() {
        return fotoCedula;
    }

    public void setFotoCedula(byte[] fotoCedula) {
        this.fotoCedula = fotoCedula;
    }

    public StreamedContent getLoadImage() {
        return loadImage;
    }

    public void setLoadImage(StreamedContent loadImage) {
        this.loadImage = loadImage;
    }

    public List<Columna> getListColumna() {
        return listColumna;
    }

    public void setListColumna(List<Columna> listColumna) {
        this.listColumna = listColumna;
    }

    public List<Columna> getListCheckColumna() {
        return listCheckColumna;
    }

    public void setListCheckColumna(List<Columna> listCheckColumna) {
        this.listCheckColumna = listCheckColumna;
    }

    public boolean isCol_num_tarjeta() {
        return col_num_tarjeta;
    }

    public void setCol_num_tarjeta(boolean col_num_tarjeta) {
        this.col_num_tarjeta = col_num_tarjeta;
    }

    public boolean isCol_pin_tarjeta() {
        return col_pin_tarjeta;
    }

    public void setCol_pin_tarjeta(boolean col_pin_tarjeta) {
        this.col_pin_tarjeta = col_pin_tarjeta;
    }

    public boolean isCol_cedula() {
        return col_cedula;
    }

    public void setCol_cedula(boolean col_cedula) {
        this.col_cedula = col_cedula;
    }

    public boolean isCol_direccion() {
        return col_direccion;
    }

    public void setCol_direccion(boolean col_direccion) {
        this.col_direccion = col_direccion;
    }

    public boolean isCol_foto_cedula() {
        return col_foto_cedula;
    }

    public void setCol_foto_cedula(boolean col_foto_cedula) {
        this.col_foto_cedula = col_foto_cedula;
    }

    public boolean isCol_nombre() {
        return col_nombre;
    }

    public void setCol_nombre(boolean col_nombre) {
        this.col_nombre = col_nombre;
    }

    public boolean isCol_telefono() {
        return col_telefono;
    }

    public void setCol_telefono(boolean col_telefono) {
        this.col_telefono = col_telefono;
    }

    public boolean isCol_celular() {
        return col_celular;
    }

    public void setCol_celular(boolean col_celular) {
        this.col_celular = col_celular;
    }

    public boolean isCol_correo() {
        return col_correo;
    }

    public void setCol_correo(boolean col_correo) {
        this.col_correo = col_correo;
    }

    public boolean isCol_id_persona() {
        return col_id_persona;
    }

    public void setCol_id_persona(boolean col_id_persona) {
        this.col_id_persona = col_id_persona;
    }

}
