package Vistas;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class MedicinasAplicadas {

    private int IDUsuario;
    public final Connection cx;
    String ConcentracionTotal;
    String medicinaAplicada;
    String viaAplicacion;
    String cantidadm, unidadm;
    String codigo;

    
    public MedicinasAplicadas (int IDUsuario, Connection cx, String Codigo, 
            String medicinaAplicada, String viaAplicacion, String cantidadm, String unidadm){
        
        this.IDUsuario = IDUsuario;
        this.cx = cx;
        this.codigo = Codigo;
        this.medicinaAplicada = medicinaAplicada;
        this.viaAplicacion = viaAplicacion;
        this.cantidadm = cantidadm;
        this.unidadm = unidadm;
    
    }
        
        
        
        public void MedicinasAplicadas() throws SQLException{
        LocalDateTime FechaDeAplicacion = LocalDateTime.now();
        
        if(medicinaAplicada == null || medicinaAplicada.trim().isEmpty()
           || "Ejem: Oxitetraciclina".equals(medicinaAplicada)){
        JOptionPane.showMessageDialog(null, "Es obligatorio ingresar una medicina");
        return;
        }
        
        if(cantidadm == null || cantidadm.trim().isEmpty() || "Ejem: 500".equals(cantidadm)){
            JOptionPane.showMessageDialog(null, "Es obligatorio ingresar cantidad");
            return;
        }

        int cantidadInt;
        try {
            cantidadInt = Integer.parseInt(cantidadm.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser un número entero válido (Ej: 500)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ConcentracionTotal = cantidadInt + " " + unidadm;
        

        
        
       

         // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO medicinasaplicadas " + "(ID_usuario, ID_animal, MedicamentoAplicado, ViaAplicacion, Concentracion, FechaAplicacion)"+
                          "VALUES (?, (SELECT ID FROM animales WHERE codigo = ? AND estado = 1), ?, ?, ?, ?)";
                    
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetro
            ps.setInt(1, IDUsuario);
            ps.setString(2, codigo);
            ps.setString(3, medicinaAplicada);
            ps.setString(4, viaAplicacion);
            ps.setString(5, ConcentracionTotal);
            ps.setString(6, FechaDeAplicacion.toString()); // Convertir FechaCompra a cadena
            
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Transaccion insertada con éxito.");
            }

            System.out.println("Medicinas registradas con exito, fecha: " + FechaDeAplicacion);  
    
    }
    
    }

