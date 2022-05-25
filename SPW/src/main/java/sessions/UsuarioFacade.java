/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

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
    
    public Usuario findUsuarioByUser(String user){
        Query query = getEntityManager()
                .createQuery("select u from Usuario u where u.usuario = :user")
                .setParameter("user", user.toUpperCase());
        return (Usuario) query.getSingleResult();
    }
   
}
