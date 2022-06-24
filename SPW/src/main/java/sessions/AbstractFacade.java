/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import beans.NavigationBean;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import services.MuiltitenancyResolver;

 
/**
 *
 * @author emmanuel
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;
    
    @PersistenceUnit(unitName="SPWPersistence")
    protected EntityManagerFactory emf;
   
    private Session session;
    
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
        
        
    } 
    
    public EntityManager getEntityManager(String customUser){
        
        //System.out.println("Usuario logueado: " + customUser);
        SessionFactoryImplementor sfi = emf.unwrap(SessionFactoryImplementor.class);
        MuiltitenancyResolver tenantResolver = (MuiltitenancyResolver) sfi.getCurrentTenantIdentifierResolver();
        tenantResolver.setTenantIdentifier(customUser);
        return emf.createEntityManager();
    }
  
    
    public EntityManager getEntityManager(){
        
        //System.out.println("Usuario logueado: " + NavigationBean.loggedUser);
        SessionFactoryImplementor sfi = emf.unwrap(SessionFactoryImplementor.class);
        MuiltitenancyResolver tenantResolver = (MuiltitenancyResolver) sfi.getCurrentTenantIdentifierResolver();
        tenantResolver.setTenantIdentifier(NavigationBean.loggedUser);
        return emf.createEntityManager();
    }
   
  
    public void create(T entity) {
        session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();
        session.save(entity);
        session.close();
    }
    

    public void edit(T entity) {
        session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();
        session.merge(entity);
        session.close();
    }
    
    public void remove(Integer id){
        session = getEntityManager().unwrap(Session.class);
        session.beginTransaction();
        session.delete(session.find(entityClass, id));
        session.close();
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<T> findAll(String customUser) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager(customUser).getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager(customUser).createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
    
}
