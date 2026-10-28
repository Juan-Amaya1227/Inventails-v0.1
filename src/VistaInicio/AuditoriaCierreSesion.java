/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package VistaInicio;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author amaya
 */
public class AuditoriaCierreSesion {
    int IDUsuario;
    String host= "bydeauiwkosfltg2tbfh-mysql.services.clever-cloud.com";
    String user = "u6xichrqiyoa9cgd";
    String password = "NWt7bgrogWXno0sb6BJG";
    String db = "bydeauiwkosfltg2tbfh";
    String sql;
    int port = 3306;
    String Accion;
    String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
    LocalDateTime HoraAsistencia;
    
    public AuditoriaCierreSesion(int IDUsuario){
        this.IDUsuario = IDUsuario;
    }
    
    public void RegistrarCierreSesion() throws SQLException{
        
        HoraAsistencia = LocalDateTime.now();
        // Consulta SQL correcta con marcadores de parámetros
            sql = "INSERT INTO asistencia (ID_Usuario, Accion, FechaAccion)"+
                         " values (?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = DriverManager.getConnection(url, user, password).prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            Accion = "Desconexion";
            ps.setInt(1, IDUsuario); 
            ps.setString(2, Accion);
            ps.setString(3, HoraAsistencia.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
    
    }
}
