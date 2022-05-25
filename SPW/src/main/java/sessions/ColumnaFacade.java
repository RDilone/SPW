/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Columna;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class ColumnaFacade extends AbstractFacade<Columna> {

    public ColumnaFacade() {
        super(Columna.class);
    }
    
    public List<Columna> listColumnaByTable(String table){
        Query query = getEntityManager()
                .createQuery("select c from Columna c where c.tabla = :table")
                .setParameter("table", table);
        return (List<Columna>) query.getResultList();
    }
    
    public List<Columna> listCheckColumnaByTable(String table){
        Query query = getEntityManager()
                .createQuery("select c from Columna c where c.tabla = :table and c.valor = 1")
                .setParameter("table", table);
        return (List<Columna>) query.getResultList();
    }
    
    public void removeAllColumnaByTable(String table){
        Query query = getEntityManager()
                .createQuery("update Columna c set c.valor = 0 where c.tabla = :table")
                .setParameter("table", table);
        query.executeUpdate();
    }
    
}
