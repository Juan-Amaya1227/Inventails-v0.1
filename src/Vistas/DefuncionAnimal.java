package Vistas;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class DefuncionAnimal {
   
    public final Connection cx;
    final int IDUsuario;
    int idAnimal;
    String RazonDefuncion;

    public DefuncionAnimal(Connection Conexion, int IDUsuario, int idAnimal, String RazonDefuncion){
        this.cx = Conexion;
        this.IDUsuario = IDUsuario;
        this.idAnimal = idAnimal;
        this.RazonDefuncion = RazonDefuncion;

    }
    
    
    
    
    public void DefuncionA() throws SQLException{
        
       
        
        LocalDateTime FechaDefuncion = LocalDateTime.now();
        
        
         
            String sql = "INSERT INTO defunciones (f_defuncion, r_defuncion, ID_usuario, Id_animal) " +
             "VALUES (?, ?, ?, ?)";
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setInt(3, IDUsuario);
            ps.setInt(4, idAnimal);
            ps.setString(1, FechaDefuncion.toString()); // Convertir FechaDefuncion a cadena
            ps.setString(2, RazonDefuncion);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(null,"Defuncion registrada con exito");
            }

            
            JOptionPane.showMessageDialog(null,"Lamentamos mucho la muerte del animal");

    }
    
    
}


