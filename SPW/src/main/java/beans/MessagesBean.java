/*
    MessagesBean
 */
package beans;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/*
 * @author sop
 */

@Named
@ViewScoped
public class MessagesBean implements Serializable{

    //bean para manejar los diferentes tipos de alertas (info, warning, error)
    
    public MessagesBean() {
    }
    
    public void info(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }
 
    public void warnig(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!", message));
    }
 
    public void error(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", message));
    }
    
    //para mensages en zonas especificas (como dialogs) se especifica el ID del messages
    public void infoId(String message, String id) {
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", message));
    }
 
    public void warnigId(String message, String id) {
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!", message));
    }
 
    public void errorId(String message, String id) {
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", message));
    }
}
