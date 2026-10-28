/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author amaya
 */
public class ARIADNA {
    private static int NumUsuarios = 0;
    public static String host = "bydeauiwkosfltg2tbfh-mysql.services.clever-cloud.com";
    public static String port = "3306";
    public static String db = "bydeauiwkosfltg2tbfh";
    public static String user = "u6xichrqiyoa9cgd"; // Usuario real
    public static String password = "NWt7bgrogWXno0sb6BJG"; // Solo la contraseña
    public static String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
    public static Connection cx;
    
    public static  Connection conectar() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cx = DriverManager.getConnection(url, user, password);
            JOptionPane.showMessageDialog(null, "SE CONECTO A LA BASE DE DATOS " + db);
            //Llamar al metodo de contar usuarios
            VerificarNumeroCuentas(cx);
        } catch (SQLException ex) {
            //Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "NO SE CONECTO A LA BASE DE DATOS: " + db + ex.getMessage(),"Error critico: err000",  JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return cx;
    }
    
    private static void VerificarNumeroCuentas(Connection cx) throws SQLException{
    // Consulta de numero de cuentas
            String NumeroUsuarios = "SELECT COUNT(*) from usuarios";
             NumUsuarios = 0;  // Inicializar conteo
            try (PreparedStatement ps = cx.prepareStatement(NumeroUsuarios);
                 ResultSet rs = ps.executeQuery()) {
        
                if (rs.next()) {
                    NumUsuarios = rs.getInt("COUNT(*)");
                } else {
                    JOptionPane.showMessageDialog(null,"No se encontro el numero de cuentas","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Error al preparar o ejecutar la consulta: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // 1. Primero conectar a la base de datos
            Connection conexion = conectar();
            
            // 2. Solo si la conexión fue exitosa, mostrar el login
            if (conexion != null) {
                java.awt.EventQueue.invokeLater(() -> {
                    //new loginGUI(conexion,NumUsuarios).setVisible(true);
                     new LoginARIADNA(conexion,NumUsuarios).setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos. La aplicación se cerrará.", "Error critico: err000", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            System.exit(1);
        }
        // TODO code application logic here
    }
    
}
