/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import beans.MessagesBean;
import entities.CustomEmail;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author emmanuel
 */
public class EmailManager {
    
    //propiedades del email
    private String host; //direccion servidor
    private String port;
    private Properties properties;
    private Session session;
    private MessagesBean messagesBean;
    private String password;
    
    public EmailManager(){
        password = "JODilone0769";
        host = "smtp.gmail.com"; //al desplegar, iria la direccion del servidor en la que la app esta alojado
        port = "465";
        
        properties = System.getProperties();
        //properties.put("mail.smtp.starttls.enable", "true");
        
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
                
        //creando session con las credenciales del correo oficial de la Aplicacion SPW: spwapplication@gmail.com
        session = Session.getInstance(properties, new Authenticator() {
             protected PasswordAuthentication getPasswordAuthentication(){
                 return new PasswordAuthentication("spwapplication", password);
             };
        });
        
        messagesBean = new MessagesBean();
    }
    
    
    public void SendEmail(CustomEmail email){
        try {
            MimeMessage message = new MimeMessage(session);
            BodyPart bodyText = new MimeBodyPart();
            BodyPart attachFile = new MimeBodyPart();
            
            //cabecera del correo
            message.setFrom(new InternetAddress(email.getRemitente()));
            message.addRecipients(Message.RecipientType.TO, new InternetAddress(email.getDestinatario()).getAddress());
            message.setSubject(email.getAsunto());
            
            //contenido en texto del correo
            bodyText.setText(email.getMensaje());
            
            //archivo adjunto del correo
            ByteArrayDataSource bad = new ByteArrayDataSource(email.getArchivo(), "application/octet-stream");
            attachFile.setDataHandler(new DataHandler(bad));
            attachFile.setFileName(email.getNombreArchivo());
                        
            //configurando el texto y archivo al multiPart
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(bodyText);
            mp.addBodyPart(attachFile);
            
            //asignando el multipart al contenido del correo
            message.setContent(mp);
            
            //enviando mensaje
            Transport.send(message);
            
            messagesBean.info("El correo fue enviado exitosamente!");
            
        } catch (MessagingException e) {
            messagesBean.error("No se pudo enviar el correo!");
            e.printStackTrace();
        }
    }
    
}
