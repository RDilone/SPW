/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Archivo;
import entities.Prestamo;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class ArchivoFacade extends AbstractFacade<Archivo> {

    public ArchivoFacade() {
        super(Archivo.class);
    } 

    
    public List<Archivo> getListArchivoByPrestamo(Prestamo prestamo){
        Query query = getEntityManager().createQuery("select a from Archivo a "
                + "where a.idPrestamo = :prestamo").setParameter("prestamo", prestamo);
        return (List<Archivo>) query.getResultList();
    }
}
