/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import beans.NavigationBean;
import entities.Usuario;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
  
    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    
    public boolean checkSchemaExist(String user){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER).createNativeQuery("SELECT EXISTS("
                + "SELECT 1 FROM information_schema.schemata "
                + "WHERE schema_name = 'sch_'||:user)")
                .setParameter("user", user);
        
        return (boolean) query.getResultList().get(0);
    }
    
    public Usuario findUsuarioByUser(String user){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER)
                .createQuery("select u from Usuario u where u.usuario = :user")
                .setParameter("user", user.toUpperCase());
        return query.getResultList().isEmpty() ? 
        null : (Usuario) query.getResultList().get(0);
    }
   
    
    public void executeSQL(String sql){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER).createNativeQuery(sql);
        query.executeUpdate();
    }
}
