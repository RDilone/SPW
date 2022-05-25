/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Persona;
import javax.ejb.Stateless;

/**
 *
 * @author emmanuel
 */
@Stateless
public class PersonaFacade extends AbstractFacade<Persona> {

    public PersonaFacade() {
        super(Persona.class);
    }
    
    public Persona returnCreate(Persona persona){
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(persona);
        getEntityManager().getTransaction().commit();
        getEntityManager().flush();
        
        return persona;
    }
     
}
