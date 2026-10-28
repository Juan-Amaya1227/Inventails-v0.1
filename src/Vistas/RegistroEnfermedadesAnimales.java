package Vistas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;

public class RegistroEnfermedadesAnimales {
 

    int IDUsuario;
    String codigo;
    String enfermedad;
    String Veterinario;
    String comentario;
    public final Connection cx;
    
    public RegistroEnfermedadesAnimales(int IDUsuario,Connection cx, String Codigo, String Enfermedad, String Veterinario, String Comentario){
        
        this.IDUsuario = IDUsuario;
        this.codigo = Codigo;
        this.cx = cx;
        
        this.Veterinario = Veterinario;
        this.enfermedad = Enfermedad;
        this.comentario = Comentario;
        
    
    }
    
    public void RegistroEnfermedades() throws SQLException{
    LocalDateTime FechaDeRegistroEnfermedad = LocalDateTime.now();
    
       
         while (enfermedad == null||enfermedad.isEmpty()) {
            if (enfermedad == null||enfermedad.isEmpty()) {
                 JOptionPane.showMessageDialog(null,"El campo de enfermedad no puede estar vacío. Intentelo de nuevo ","error", JOptionPane.ERROR_MESSAGE); }
         return;}
         
        
        if(Veterinario == null||Veterinario.isEmpty()||"Ingrese Nombre del Veterinario, Ejem: Juan".equals(Veterinario))
        {Veterinario = "SE DESCONOCE";}
        
        if(comentario == null||comentario.isEmpty()||"Ingrese comentario de la enfermedad (Opcional)".equals(comentario))
        {comentario = "SIN COMENTARIOS";}
        
        // Consulta SQL correcta con marcadores de parámetros
        String sql = "INSERT INTO  registroenfermedades " + "(ID_usuario, ID_animal, F_Diagnostico, enfermedad, veterinario, comentario) "+
                        "VALUES (?, (SELECT ID FROM animales WHERE codigo = ? AND estado = 1), ?, ?, ?, ?)";

            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetro
            ps.setInt(1, IDUsuario);
            ps.setString(2, codigo);
            ps.setString(3, FechaDeRegistroEnfermedad.toString()); // Convertir FechaCompra a cadena
            ps.setString(4, enfermedad);
            ps.setString(5, Veterinario);
            ps.setString(6, comentario);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Transaccion insertada con éxito.");
            }
            System.out.println("Enfermedad registrada satisfactoriamente, fecha: " + FechaDeRegistroEnfermedad);   }
    
}
