/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

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
        Query query = getEntityManager()
                .createQuery("delete from Perfil p where p.idUsuario = :user")
                .setParameter("user", usuario);
        
        query.executeUpdate();
    }
}
