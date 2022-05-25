/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.CustomReporte;
import entities.Reporte;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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
import org.apache.log4j.BasicConfigurator;
import sessions.PrestamoFacade;
import sessions.ReporteFacade;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class ReporteBean implements Serializable{
    
    //@EJBs   
    @EJB
    PrestamoFacade prestamoFacade;
    
    @EJB
    ReporteFacade reporteFacade;
    
    //db objects
    private DataSource dataSource;
    
    //lista de rportes
    private List<CustomReporte> listCustomReporte;

    //listas de valores de rportes   
    private List<String> meses;
    private List<String> anos;
    
    //lista de posibles valores
    private List<List<String>> listvaluesReporte_x;
    private List<List<String>> listvaluesReporte_y;
    private List<List<String>> listvaluesReporte_a;
    private List<List<String>> listvaluesReporte_b;
    private List<List<String>> listvaluesReporte_c;
    
    
    //valor seleccionado temporal
    private String tempSelectedValue; 
    
    //posibles valores de fechas
    private Date fechaInicio;
    private Date fechaFinal;
   
    private SimpleDateFormat sdf;
    
    //servicios
    MessagesBean messagesBean;    
    
    @PostConstruct
    private void init(){
        
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        //inicializando
        messagesBean = new MessagesBean();
        
        listvaluesReporte_x = new ArrayList<>();
        listvaluesReporte_y = new ArrayList<>();
        listvaluesReporte_a = new ArrayList<>();
        listvaluesReporte_b = new ArrayList<>();
        listvaluesReporte_c = new ArrayList<>();
        
        anos = new ArrayList<>();
        meses = new ArrayList<>();
        
        listCustomReporte = new ArrayList<>();
        
        //llenando las listas de parametros de los reportes
        fillListValuesParameters();
        fillCustomReportList();
    }
    
    
    /*llena la lista de parametros de todos los reportes
      y sus posibles valores seleccionables */
    public void fillListValuesParameters(){
        
        tempSelectedValue = "";
        
        //definiendo vlores de reportes
               
        //lista de meses
        //meses.add("Seleccionar Mes"); 
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
        //anos.add(0, "Seleccionar Año");
        
        //agregando listas de posibles valores 
        
        listvaluesReporte_a.add(meses);
        listvaluesReporte_b.add(meses);        
        
        listvaluesReporte_x.add(meses);
        listvaluesReporte_x.add(anos);
        
        listvaluesReporte_y.add(meses);
        listvaluesReporte_y.add(meses);
        listvaluesReporte_y.add(anos);
    }
    
    
     /*verifica si hay parametros que comiencen con FECHA y devuelve la 
      cantidad de parametros fecha que hay */
    public boolean checkFecha(List<String> listParametros){
        boolean exist = false;
        
        for (String param : listParametros) {
            if(param.startsWith("FECHA")){
                exist = true;
            }
        }
        
        return exist;
    }
    
    
    public void fillCustomReportList(){

        File reportes = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/reportes/"));
        
        //recorriendo lista de reportes (archivos)
        for (File file : reportes.listFiles()) {
            CustomReporte rep = new CustomReporte();
            List<String> listParametros = new ArrayList<>();
            List<String> listFechaParametros = new ArrayList<>();
            
            //asignando parametros correspondientes
            for (Reporte report : reporteFacade.getReporteListByName(file.getName())) {
                if(report.getParametro().startsWith("FECHA")){
                    listFechaParametros.add(report.getParametro());
                }else {
                    listParametros.add(report.getParametro());
                }
                
            }

                        
            //definiendo caracteristicas de cada reporte
            switch(file.getName()){
                case "toolbox.png": {
                    rep.setListValores(listvaluesReporte_x);                    
                } break;
                
                case "MicrosoftTeams-image.png": {
                    rep.setListValores(listvaluesReporte_y); 
                } break;
                
                case "PES para cargar EJEMPLO.csv": {
                    rep.setListValores(listvaluesReporte_b); 
                } break; 
                
                case "JWSFileChooserDemo.jnlp": {
                    rep.setListValores(listvaluesReporte_a); 
                } break; 
            }
            
            
                    rep.setListParametros(listParametros);
                    rep.setListFechaParametros(listFechaParametros);
                    rep.setReporte(file.getName());
                    rep.setNombre(formatReportName(file.getName()));
            
            listCustomReporte.add(rep);
        }
        
    } 
    

    public void setParameterValue(CustomReporte reporte, String parametro, String valor){
                      
            if(listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().containsKey(parametro)){
                listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().replace(parametro, valor);
            }else {
                listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros().put(parametro, valor);
            }    

//            System.out.println(
//                        parametro +" - " + 
//                        valor + " - " + 
//                        listCustomReporte.get(listCustomReporte.indexOf(reporte)).getParametros());
//
//            System.out.println("\n");            
     
    }
    
    
    
    //Formatea el nombre (del archivo) del reporte que se va a presentar
    public String formatReportName(String reporte){
        return reporte.replace("_", " ").replace(".jasper", "");
    }
    
    
    
    //construye el reporte y retorna el string correspondiente
    public String buildReport(CustomReporte reporte) throws SQLException, JRException {
              
        String reportName = reporte.getReporte().substring(0, reporte.getReporte().length()-7);
        
        System.out.println("datos de reporte: ");
        System.out.println("Maps: "+reporte.getParametros());
        System.out.println("Reporte: "+reporte.getReporte());
        System.out.println("List parametros: "+reporte.getListParametros().size());
        System.out.println("List fechaParametros: "+reporte.getListFechaParametros().size());         
        
        
//        try( Connection con = dataSource.getConnection() ) {
//
//            FacesContext fc = FacesContext.getCurrentInstance();            
//            ExternalContext ec = fc.getExternalContext();            
//            HttpServletResponse res = (HttpServletResponse) fc.getExternalContext().getResponse();
//
//            BasicConfigurator.configure();          
//            
//            res.setContentType("application/pdf");//inline
//            res.addHeader("Content-disposition", "inline; filename="+reportName+".pdf");          
//            
//            File file = new File(ec.getRealPath("/resources/reportes/"+reporte.getReporte()));                         
//            
//            JasperReport rep = (JasperReport) JRLoader.loadObject(file);                                      
//            
//            JasperPrint jPrint = JasperFillManager.fillReport(rep, reporte.getParametros(), con);
//       
//            try (ServletOutputStream servletOutputStream = res.getOutputStream()) {
//                JasperExportManager.exportReportToPdfStream(jPrint, servletOutputStream);
//                servletOutputStream.flush();
//            }
//            FacesContext.getCurrentInstance().responseComplete();
//
//        } catch (JRException | IOException e) {
//            System.out.println("Error de Reporte: "+e.getMessage());
//            System.out.println("Causa: "+e.getCause());
//            e.getLocalizedMessage();
//        }
       
        return reportName;
    }
    
    
    //devuelve el string del reporte correspondiente al parametro reporte
    public String printReport(CustomReporte reporte){
        //Parametro de reporteGeneralPlanilla: EST               
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
    
    
    
    
}
