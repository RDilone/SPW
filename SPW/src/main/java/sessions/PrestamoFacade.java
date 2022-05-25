/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Prestamo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class PrestamoFacade extends AbstractFacade<Prestamo> {

    public PrestamoFacade() {
        super(Prestamo.class);
    }
    
    public Prestamo returnCreate(Prestamo prestamo){
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(prestamo);
        getEntityManager().getTransaction().commit();
        getEntityManager().flush();
        
        return prestamo;
    }
    
    public List<Prestamo> getPaidLoan(){
        Query query = getEntityManager().createQuery("select p from Prestamo p where p.estado = 'SALDADO'");
        return (List<Prestamo>) query.getResultList();
    }
    
    
    public List<String> getYearListPrestamos(){
        Query query = getEntityManager().createNativeQuery("select extract(year from fecha_inicio) as ano from prestamo group by ano");
        return (List<String>) query.getResultList();
    }
}
