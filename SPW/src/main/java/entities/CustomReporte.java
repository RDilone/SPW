/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author emmanuel
 */
public class CustomReporte {
    
    private String reporte;
    private String nombre;
    private HashMap parametros;
    private List<String> listParametros;
    private List<List<String>> listValores;
    private List<String> listFechaParametros;
    private List<String> listFechaValores;
    
    private SimpleDateFormat sdf;
    
    public CustomReporte(){
        sdf = new SimpleDateFormat("dd/MM/yyyy");

        reporte = "";
        nombre = "";
        listValores = new ArrayList<>();
        listParametros = new ArrayList<>();
        listFechaParametros = new ArrayList<>();
        listFechaValores = new ArrayList<>();
        parametros = new HashMap();
    }
    
    
    //GETTERS & SETTERS
    
    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public HashMap getParametros() {
        return parametros;
    }

    public void setParametros(HashMap parametros) {
        this.parametros = parametros;
    }


    public List<String> getListParametros() {
        return listParametros;
    }

    public void setListParametros(List<String> listParametros) {
        this.listParametros = listParametros;
    }

    public List<List<String>> getListValores() {
        return listValores;
    }

    public void setListValores(List<List<String>> listValores) {
        this.listValores = listValores;
    }

    public List<String> getListFechaParametros() {
        return listFechaParametros;
    }

    public void setListFechaParametros(List<String> listFechaParametros) {
        this.listFechaParametros = listFechaParametros;
    }

    public List<String> getListFechaValores() {
        return listFechaValores;
    }

    public void setListFechaValores(List<String> listFechaValores) {
        this.listFechaValores = listFechaValores;
    }

    
}
