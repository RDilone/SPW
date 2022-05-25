/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Amortizacion;
import entities.HistorialPago;
import entities.Prestamo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class HistorialPagoFacade extends AbstractFacade<HistorialPago> {

    public HistorialPagoFacade() {
        super(HistorialPago.class);
    }

    public List<HistorialPago> getPreviousPagosByAmortizacion(Amortizacion amortizacion){
        Query query = getEntityManager().createQuery("select h from HistorialPago h "
                + "where h.notaPago like 'ABONO CAPITAL' and h.cuotaNum = :cuotaNum and h.idPrestamo = :prestamo")
                .setParameter("prestamo", amortizacion.getIdPrestamo())
                .setParameter("cuotaNum", amortizacion.getCuotaNum());
        
        return (List<HistorialPago>) query.getResultList();
    }
    
    public int maxCuotaNumByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select max(a.cuotaNum) from Amortizacion a "
                + "where a.idPrestamo = :prestamo").setParameter("prestamo", prestamo);
        return (int) query.getSingleResult();
    }
    
    public List<HistorialPago> getListHistorialPagoByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select h from HistorialPago h "
                + "where h.idPrestamo = :prestamo order by h.cuotaNum asc").setParameter("prestamo", prestamo);
        return (List<HistorialPago>) query.getResultList();
    }
    
}
