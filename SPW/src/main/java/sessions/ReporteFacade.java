/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import beans.NavigationBean;
import entities.Reporte;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class ReporteFacade extends AbstractFacade<Reporte> {

    public ReporteFacade() {
        super(Reporte.class);
    }
    
    //devuelve el ID del reporte en base al nombre reporte y el parametro
    public int getIdReporteByReporte(Reporte reporte){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER).createQuery("select r.idReporte from Reporte r "
                + "where r.reporte = :reporte and r.parametro = :parametro")
                .setParameter("reporte", reporte.getReporte())
                .setParameter("parametro", reporte.getParametro());
        return query.getResultList().size() > 0 ? 
         (int) query.getResultList().get(0) : 0;        
    }
    
    public List<String> getOnlyReportList(){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER).createQuery("select r.reporte from Reporte r group by r.reporte");
        return (List<String>) query.getResultList();
    }
    
    public List<Reporte> getReporteListByName(String report){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER).createQuery("select r from Reporte r "
                + "where r.reporte = :rep").setParameter("rep", report);
        return (List<Reporte>) query.getResultList();
    }
}
