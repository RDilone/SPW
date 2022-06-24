/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import beans.NavigationBean;
import entities.Permiso;
import entities.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class PermisoFacade extends AbstractFacade<Permiso> {

    public PermisoFacade() {
        super(Permiso.class);
    }
    
     public List<Permiso> listPerfilByUsuario(Usuario usuario){
        Query query = getEntityManager(NavigationBean.DEFAULT_USER)
                .createQuery("select pm from Permiso pm, Perfil pf "
                           + "where pm.idPermiso = pf.idPermiso and pf.idUsuario = :user")
                .setParameter("user", usuario);
        
        return (List<Permiso>) query.getResultList();
    }
}
