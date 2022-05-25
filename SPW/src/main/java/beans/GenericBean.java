/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author emmanuel
 */

@Named
@ViewScoped
public class GenericBean implements Serializable{
    
    MessagesBean messagesBean;
    
    public GenericBean(){
        
    }
    
    @PostConstruct
    private void init(){
        messagesBean = new MessagesBean();
    }
    
    
    
}
