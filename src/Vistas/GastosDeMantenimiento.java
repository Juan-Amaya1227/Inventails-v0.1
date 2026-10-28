package Vistas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;


public class GastosDeMantenimiento {
    public final Connection cx;
    public int idGastoM;
    private int IDUsuario;
    int GastoM1;
    String RazonGasto;
    public GastosDeMantenimiento(int IDUsuario,Connection cx, String RazonGasto, int ValorGasto){
    this.cx = cx;
    this.GastoM1 = ValorGasto;
    this.IDUsuario = IDUsuario;
    this.RazonGasto = RazonGasto;
    }
    
    public void GastosDeMantenimiento() throws SQLException{
    LocalDateTime FechaDeGasto = LocalDateTime.now();
    
    
        float ValorGasto=0;
                // Ciclo para asegurar una entrada numérica válida
                            while (true) { // Bucle infinito para forzar la validación
                                try {
                                    ValorGasto = (float) GastoM1;
                                    break;
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null, "Debe ingresar un número válido para el costo del gasto " + RazonGasto, "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }

            float ValorGastoReal = ValorGasto*-1;
            JOptionPane.showMessageDialog(null, "valor gasto de mantenimiento: "+ValorGastoReal);
            
        
        // Consulta SQL correcta con marcadores de parámetros
        String sql = "INSERT INTO gastosmantenimiento (ID_usuario, RazondeGasto, ValorGasto, FechaGasto) VALUES (?, ?, ?, ?)";

        PreparedStatement ps = cx.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

        ps.setInt(1, IDUsuario);
        ps.setString(2, RazonGasto);
        ps.setFloat(3, ValorGastoReal);
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(FechaDeGasto));

        int filasInsertadas = ps.executeUpdate();

        idGastoM = 0;

        if (filasInsertadas > 0) {

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGastoM = rs.getInt(1);
            }

            JOptionPane.showMessageDialog(null, "Gasto de mantenimiento registrado con exito");
        }    
            
            InsertarTransaccion(FechaDeGasto, ValorGastoReal );
    } 
    
    public void InsertarTransaccion(LocalDateTime FechaDeGasto, float ValorGastoReal) throws SQLException {
    

    // Inserta en transacciones
    String tipot = "GASTO_MANTENIMIENTO";
    String sql = "INSERT INTO transacciones (TIPO_TRANSACCION, id_GastoMantenimiento) VALUES (?, ?)";

    try (PreparedStatement ps = cx.prepareStatement(sql)) {
        ps.setString(1, tipot);
        ps.setInt(2, idGastoM);

        int filasInsertadas = ps.executeUpdate();
        if (filasInsertadas > 0) {  //System.out.println("Si se registro algo");          
        }else{JOptionPane.showMessageDialog(null,"no se inserto transaccion");}
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al registrar la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
}
