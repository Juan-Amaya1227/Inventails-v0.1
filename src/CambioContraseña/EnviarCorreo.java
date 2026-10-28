/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CambioContraseña;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 *
 * @author amaya
 */
public final class EnviarCorreo {
    Connection Conexion;
    int Codigo;
    String UsuarioBuscar;
    String CorreoElectronico;
    String ServidorUsuario;
    Session session;
    Message mensaje;
    private boolean verificacionExitosa = false;
    
    public EnviarCorreo(Connection Conexion, String Usuario){
        this.Conexion = Conexion;
       this.UsuarioBuscar = Usuario;
    }
    
    public void VerificarNulo(){
    if(UsuarioBuscar == null ||UsuarioBuscar.trim().isEmpty()){
    JOptionPane.showMessageDialog(null, "Por favor ingrese un usuario para Cambiar su contraseña");
    return;
    }}
    
    public boolean VerificarExistenciaEnDB(){
    String query = "select count(*) from usuarios where Usuario = ? ";
            int coincidencias = 0;
            try (PreparedStatement stmt = Conexion.prepareStatement(query)) {
                stmt.setString(1, UsuarioBuscar);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    coincidencias = rs.getInt("count(*)");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al verificar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (coincidencias == 0) {
                JOptionPane.showMessageDialog(null, "El usuario Ingresado no esta registrado en el sistema");
                        return false;

                
            }
            else
                
                ExtraerCorreo();
        return true;
                

    }
    
    public void ExtraerCorreo(){
        
        String query = "select Correo_Electronico from usuarios where usuario = ?";
        
        try (PreparedStatement stmt = Conexion.prepareStatement(query)) {
                stmt.setString(1, UsuarioBuscar);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    CorreoElectronico = rs.getString("Correo_Electronico");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró el usuario: " + UsuarioBuscar, "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al Extraer Correo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
                
    }
    
    public void ConfigurarServidorSMTP(){
        
        // Configuración del servidor SMTP
    Properties props = new Properties();
    System.setProperty("https.protocols", "TLSv1.2");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    // Credenciales del correo emisor
    ServidorUsuario = "amayaalvarezjuanmanuel@gmail.com";
    final String contraseña = "akuzkklnchndnfuj";
    
     // Crear sesión con autenticación
     session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(ServidorUsuario, contraseña);
        }
    });
    
    }
    
    public void CrearMensaje(){
        
       try {
    mensaje = new MimeMessage(session);
    mensaje.setFrom(new InternetAddress(ServidorUsuario));
    mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CorreoElectronico));
    mensaje.setSubject("Codigo de verificacion");
    Random numR = new Random();
    Codigo = numR.nextInt(1000000);
    mensaje.setText("Este es el codigo de verificacion para realizar el cambio de su Contraseña, por su seguridad por favor no lo comparta con nadie mas:  "+Codigo);

    EnviarMensaje();

    // Solo redirige si el código es correcto
    if (verificar(Codigo)) {
        verificacionExitosa = true;
    }

        } catch (MessagingException e) {
            Throwable causa = e.getCause();
            if (causa instanceof java.net.UnknownHostException) {
                JOptionPane.showMessageDialog(null, "Error: No hay conexión a Internet", "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al enviar el correo", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void EnviarMensaje() throws MessagingException{
        
        // Enviar el mensaje
    Transport.send(mensaje);
    
    }
    
    private boolean verificar(int Codigo){
        String ci = JOptionPane.showInputDialog("Ingrese el codigo enviado a su correo electronico");

        if (ci == null) return false; // Canceló el diálogo

        if (ci.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor debe ingresar un codigo de verificacion");
            return false;
        }

        int codigo_Ingresado;
        try {
            codigo_Ingresado = Integer.parseInt(ci.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Debe ingresar solo numeros", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (codigo_Ingresado != Codigo) {
            JOptionPane.showMessageDialog(null, "El codigo ingresado no es valido", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
    
    
    public boolean isVerificacionExitosa() {
        return verificacionExitosa;
    }

    public void EjecutarMetodos(){
        VerificarNulo();
        if (!VerificarExistenciaEnDB()) return;
        ConfigurarServidorSMTP();
        CrearMensaje();
    }
    
}
