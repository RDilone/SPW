/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessions;

import entities.Intervalo;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author emmanuel
 */
@Stateless
public class IntervaloFacade extends AbstractFacade<Intervalo> {

    public IntervaloFacade() {
        super(Intervalo.class);
    }
}
