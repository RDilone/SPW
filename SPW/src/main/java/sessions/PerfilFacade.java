/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import beans.NavigationBean;
import entities.Perfil;
import entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class PerfilFacade extends AbstractFacade<Perfil> {

    public PerfilFacade() {
        super(Perfil.class);
    }
 
    public void removePerfilByUsuario(Usuario usuario){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER)
                .createQuery("delete from Perfil p where p.idUsuario = :user")
                .setParameter("user", usuario);
        
        query.executeUpdate();
    }
    
    public List<Perfil> getPerfilByUsuario(Usuario user){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER)
                .createQuery("select p from Perfil p "
                           + "where p.idUsuario = :user")
                .setParameter("user", user);
        
        return (List<Perfil>) query.getResultList();
    }
}
