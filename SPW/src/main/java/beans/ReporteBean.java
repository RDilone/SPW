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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import sessions.PrestamoFacade;
import sessions.ReporteFacade;

/**
 *
 * @author emmanuel
 */
@Named
@ViewScoped
public class ReporteBean implements Serializable {

    //@EJBs   
    @EJB
    PrestamoFacade prestamoFacade;

    @EJB
    ReporteFacade reporteFacade;

    //db objects
    @Resource(name = "jdbc/dspw", lookup = "jdbc/dspw")
    private DataSource dataSource;

    //lista de rportes
    private List<CustomReporte> listCustomReporte;

    //listas de valores de rportes   
    private List<String> meses;
    private List<String> anos;

    //lista de posibles valores
    private List<List<String>> listvaluesReporte_a;

    //valor seleccionado temporal
    private String tempSelectedValue;

    private String anoSeleccionado;

    //variable para controlar el parametro 
    //mostrar o no el celular del prestamista
    private boolean mostrarCelular;

    //posibles valores de fechas
    private Date fechaInicio;
    private Date fechaFinal;

    private Date fechaActual;

    private SimpleDateFormat sdf;
    private SimpleDateFormat sdfYear;

    //servicios
    MessagesBean messagesBean;

    @PostConstruct
    private void init() {

        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdfYear = new SimpleDateFormat("yyyy");

        //inicializando
        messagesBean = new MessagesBean();

        listvaluesReporte_a = new ArrayList<>();

        anos = new ArrayList<>();
        meses = new ArrayList<>();

        listCustomReporte = new ArrayList<>();

        //llenando las listas de parametros de los reportes
        fillListValuesParameters();
        fillCustomReportList();
    }

    /*llena la lista de parametros de todos los reportes
      y sus posibles valores seleccionables */
    public void fillListValuesParameters() {

        fechaActual = new Date();

        tempSelectedValue = "";
        anoSeleccionado = sdfYear.format(fechaActual);
        mostrarCelular = true;

        //definiendo vlores de reportes
        //lista de meses
        meses.add("ENERO");
        meses.add("FEBRERO");
        meses.add("MARZO");
        meses.add("ABRIL");
        meses.add("MAYO");
        meses.add("JUNIO");
        meses.add("JULIO");
        meses.add("AGOSTO");
        meses.add("SEPTIEMBRE");
        meses.add("OCTUBRE");
        meses.add("NOVIEMBRE");
        meses.add("DICIEMBRE");

        //lista de años
        anos = prestamoFacade.getYearListPrestamos();
        anos.add("2021");
        anos.add("2020");

        //agregando listas de posibles valores 
        listvaluesReporte_a.add(meses);

    }

    
    //cambia el texto del control de mostrar/ocultar celular 
    public String getShowTextCelular() {
        if (mostrarCelular) {
            return "Mostrar Celular";
        } else {
            return "Ocultar Celular";
        }
    }

    //llena la lista de los objetos CustomReport
    public void fillCustomReportList() {

        File reportes = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/"));
        listCustomReporte = new ArrayList<>();
        
        //recorriendo lista de reportes (archivos)
        for (File file : reportes.listFiles()) {
            CustomReporte rep = new CustomReporte();
            List<String> listParametros = new ArrayList<>();
            List<String> listFechaParametros = new ArrayList<>();

            //asignando parametros pasivos (default)
            for (Reporte report : reporteFacade.getReporteListByName(file.getName())) {

                if (report.getParametro().startsWith("FECHA")) {
                    //agrega los parametros de tipo FECHA a una lista distinta
                    listFechaParametros.add(report.getParametro());
                } else if (report.getParametro().startsWith("ANO")) {
                    //configura el parametro ANO y su valor correspondiente por defecto
                    rep.getParametros().put(report.getParametro(), anoSeleccionado);
                } else if (report.getParametro().startsWith("CEL")) {
                    //configura el parametro CEL y su valor correspondiente por defecto
                    rep.getParametros().put(report.getParametro(), (mostrarCelular ? "SI" : "NO"));
                } else if (report.getParametro().startsWith("ID_USUARIO")) {
                    rep.getParametros().put(report.getParametro(), String.valueOf(NavigationBean.usuario.getIdUsuario()));
                } else if(report.getParametro().startsWith("PATH")){
                    //agrega el valor de la ruta absoluta al parametro PATH que carga la imagen del reporte
                    rep.getParametros().put(report.getParametro(), reportes.getAbsolutePath()+"/spw_logo2.png");
                } else if(report.getParametro().startsWith("ESQUEMA")){ 
                    rep.getParametros().put(report.getParametro(), NavigationBean.usuario.getEsquema());
                } else {
                    //agrega los parametros de selectOneMenu a la lista
                    listParametros.add(report.getParametro());
                }

            }

            //definiendo lista de valores de selectOneMenu de cada reporte y 
            //cada reporte que se mostrara en la galeria de reportes
            switch (file.getName()) {

                case "Pagos_Pendientes.jasper": {
                    listCustomReporte.add(rep);
                }
                break;

                case "Ganancias_Generadas.jasper": {
                    listCustomReporte.add(rep);
                }
                break;

                case "Reporte_General_Credito.jasper": {
                    listCustomReporte.add(rep);
                    //rep.setListValores(listvaluesReporte_b); 
                }
                break;

            }

            rep.setListParametros(listParametros);
            rep.setListFechaParametros(listFechaParametros);
            rep.setReporte(file.getName());
            rep.setNombre(formatReportName(file.getName()));
         
        }       

    }

    
    //configura el parametro y valor ano en cada uno de los 
    //reportes que incluya un parametro ANO
    public void setParameterAno() {

        for (CustomReporte customR : listCustomReporte) {
            if(customR.getParametros().containsKey("ANO")){
                listCustomReporte.get(listCustomReporte.indexOf(customR)).getParametros().replace("ANO", anoSeleccionado);
            }

            //System.out.println(listCustomReporte.get(listCustomReporte.indexOf(customR)).getParametros());
//            System.out.println("\n"); 
        }

    }

    
    //configura el parametro y valor ano en cada uno de los 
    //reportes que incluya un parametro CEL
    public void setParameterCel() {
        
        for (CustomReporte customR : listCustomReporte) {     
            if(customR.getParametros().containsKey("CEL")){
                listCustomReporte.get(listCustomReporte.indexOf(customR)).getParametros().replace("CEL", mostrarCelular ? "SI" : "NO");
            }
            
            //System.out.println(listCustomReporte.get(listCustomReporte.indexOf(customR)).getParametros());
//            System.out.println("\n");
        }

    }
 
    
    //configura el parametro y valor ano en cada uno de los 
    //reportes que incluya cualquier otro parametro que no 
    //sea ANO o CEL
    public void setParameterValue(CustomReporte reporte, String parametro, String valor) {

        if (listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().containsKey(parametro)) {
            listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().replace(parametro, valor);
        } else {
            listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().put(parametro, valor);
        }

//        System.out.println("Reporte: " + reporte.getReporte());
//        System.out.println(listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros());
//        System.out.println("\n");

    }


    
    //Formatea el nombre (del archivo) del reporte que se va a presentar
    public String formatReportName(String reporte) {
        return reporte.replace("_", " ").replace(".jasper", "");
    }

    
    
    //devuelve none o block segun la listaAmortizacion 
    //tenga o no registros
    public String getDisplay(){
        if(listCustomReporte.size() > 0){
            return "none";
        }else {
            return "block";           
        }
    }

    
    //GETTERS & SETTERS
    public List<CustomReporte> getListCustomReporte() {
        return listCustomReporte;
    }

    public void setListCustomReporte(List<CustomReporte> listReporte) {
        this.listCustomReporte = listReporte;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getTempSelectedValue() {
        return tempSelectedValue;
    }

    public void setTempSelectedValue(String tempSelectedValue) {
        this.tempSelectedValue = tempSelectedValue;
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public boolean isMostrarCelular() {
        return mostrarCelular;
    }

    public void setMostrarCelular(boolean mostrarCelular) {
        this.mostrarCelular = mostrarCelular;
    }

    public List<String> getAnos() {
        return anos;
    }

    public void setAnos(List<String> anos) {
        this.anos = anos;
    }

    public String getAnoSeleccionado() {
        return anoSeleccionado;
    }

    public void setAnoSeleccionado(String anoSeleccionado) {
        this.anoSeleccionado = anoSeleccionado;
    }

    public Date getFechaActual() {
        return fechaActual;
    }

    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

}
