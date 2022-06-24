/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Amortizacion;
import entities.Prestamo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class AmortizacionFacade extends AbstractFacade<Amortizacion> {

    public AmortizacionFacade() {
        super(Amortizacion.class);
    }
    
    
    public double getCuotaByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select a.pagoCapital from Amortizacion a "
                + "where a.cuotaNum = 1 and a.idPrestamo = :prestamo")
                .setParameter("prestamo", prestamo);
        return (double) query.getSingleResult();
    }
    
    public List<Amortizacion> getPendingCuotas(){
               
        Query query = getEntityManager().createNativeQuery("select * from Amortizacion a where "
                + "cuota_num = (select min(cuota_num) from amortizacion where "
                + "id_prestamo = a.id_prestamo and estado = 'PENDIENT') order by a.fecha asc",Amortizacion.class);
        return (List<Amortizacion>) query.getResultList();
    }
    
    public List<Amortizacion> getAmortizacionByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select a from Amortizacion a "
                + "where a.idPrestamo = :prestamo order by a.cuotaNum asc").setParameter("prestamo", prestamo);
        return (List<Amortizacion>) query.getResultList();
    }
    
    public List<Amortizacion> getPendingAmortizacionByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select a from Amortizacion a "
                + "where a.idPrestamo = :prestamo and a.estado = 'PENDIENTE' order by a.cuotaNum asc").setParameter("prestamo", prestamo);
        return (List<Amortizacion>) query.getResultList();
    }
    
    public void payPendingAmortizacionByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("update Amortizacion a set a.estado = 'PAGADO' "
                + "where a.idPrestamo = :prestamo and a.estado = 'PENDIENTE'").setParameter("prestamo", prestamo);
        query.executeUpdate();
    }
    
    public Amortizacion getLastAmortizacionByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createNativeQuery("select * from amortizacion a "
                + "where a.cuota_num in(select max(cuota_num) from amortizacion "
                + "where id_prestamo = :prestamo) and a.id_prestamo = :prestamo", Amortizacion.class)
                .setParameter("prestamo", prestamo.getIdPrestamo());
        return query.getResultList().size() > 0 ?
               (Amortizacion) query.getResultList().get(0) : null ;
    }
    
    
    public Amortizacion getLastAmortizacionPagadaByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createNativeQuery("select * from Amortizacion a "
                + "where id_prestamo = :prestamo and cuota_num = ("
                + "select max(cuota_num) from Amortizacion "
                + "where id_prestamo = :prestamo and estado = 'PAGADO')", Amortizacion.class)
                .setParameter("prestamo", prestamo.getIdPrestamo());
        return query.getResultList().size() > 0 ? 
               (Amortizacion) query.getResultList().get(0) : null;
    }
    
    public double getInteresByPrestamoCuota(Prestamo prestamo, Integer cuotaNum){
        Query query = getEntityManager().createQuery("select a.pagoInteres from Amortizacion a "
                + "where a.idPrestamo = :prestamo and a.cuotaNum = :cuotaNum")
                .setParameter("prestamo", prestamo)
                .setParameter("cuotaNum", cuotaNum);
        return query.getResultList().size() > 0 ? 
                (double) query.getResultList().get(0) : 0;
    }

}
