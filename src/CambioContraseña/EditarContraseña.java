/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CambioContraseña;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author amaya
 */
public class EditarContraseña {
    Connection Conexion;
    String Usuario;
    String Contraseña1;
    String Contraseña2;
    String ContraseñaEncriptada;
    
    public EditarContraseña(String Usuario, String Contraseña1, String Contraseña2, Connection Conexion){
    this.Conexion = Conexion;
    this.Contraseña1 = Contraseña1;
    this.Contraseña2 = Contraseña2;
    this.Usuario = Usuario;
    }
    
    public boolean VerificarNulos(){
    if(Contraseña1 == null || Contraseña1.trim().isEmpty()){
    JOptionPane.showMessageDialog(null,"El campo de Contraseña no puede estar Vacio");
    return false;
    }
    
    if(Contraseña2 == null || Contraseña2.trim().isEmpty()){
    JOptionPane.showMessageDialog(null,"Es Obligatorio Confirmar La nueva contraseña");
    return false;
    }
    return true;
    }
    
    public boolean ComprobarContraseñas(){
    if(!Contraseña1.equals(Contraseña2)){
    JOptionPane.showMessageDialog(null,"Las contraseñas ingresadas no coinciden");
    return false;
    }
    return true;
    }
    
    public void EncriptarContraseña(){
        ContraseñaEncriptada = BCrypt.withDefaults().hashToString(12, Contraseña1.toCharArray());
    
    }
    
    public void ActualizarContraseña() throws SQLException{
        String sql = "UPDATE usuarios SET password = ? WHERE usuario = ?";
        try (PreparedStatement pstmt = Conexion.prepareStatement(sql)) {
            pstmt.setString(1, ContraseñaEncriptada);
            pstmt.setString(2, Usuario);
            int filasInsertadas = pstmt.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Cambio de contraseña registrado exitosamente");
            } else {
                JOptionPane.showMessageDialog(null, "Lo sentimos no ha sido posible registrar el cambio de Contraseña", "error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void EjecutarMetodos() throws SQLException{
        if (!VerificarNulos()) return;
        if (!ComprobarContraseñas()) return;
        EncriptarContraseña();
        ActualizarContraseña();
    }    
}
