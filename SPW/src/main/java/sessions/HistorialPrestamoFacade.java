/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.HistorialPrestamo;
import entities.Prestamo;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class HistorialPrestamoFacade extends AbstractFacade<HistorialPrestamo> {

    public HistorialPrestamoFacade() {
        super(HistorialPrestamo.class);
    }
     
    public HistorialPrestamo getLastChangeDateByPrestamo(Prestamo prestamo){
        try {
            Query query = getEntityManager().createNativeQuery("select * from Historial_Prestamo hp "
                + "where hp.id_prestamo = :prestamo and hp.fecha_cambio = " 
                +"(select max(fecha_cambio) from Historial_Prestamo "
                + "where id_prestamo = :prestamo)",HistorialPrestamo.class)
                .setParameter("prestamo", prestamo.getIdPrestamo());
            return (HistorialPrestamo) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }              
    }
    
    
    public List<HistorialPrestamo> getListHistorialPrestamoByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select hp from HistorialPrestamo hp "
                + "where hp.idPrestamo = :prestamo order by hp.fechaCambio asc")
                .setParameter("prestamo", prestamo);
        return (List<HistorialPrestamo>) query.getResultList();
    }
    
}
