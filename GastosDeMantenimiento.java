
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;


public class GastosDeMantenimiento {
    public final Connection cx;
    private int IDUsuario;
    public GastosDeMantenimiento(int IDUsuario,Connection cx){
    this.cx = cx;
    this.IDUsuario = IDUsuario;
    }
    
    public void GastosDeMantenimiento() throws SQLException{
    LocalDateTime FechaDeGasto = LocalDateTime.now();
    String RazonGasto = JOptionPane.showInputDialog("Ingrese la razon de gasto");
    if(RazonGasto == null || RazonGasto.isEmpty()){
        JOptionPane.showMessageDialog(null,"Cancelaste el registro de gasto de mantenimiento");
         return;}
    
        float ValorGasto=0;
                // Ciclo para asegurar una entrada numérica válida
                            while (true) { // Bucle infinito para forzar la validación
                                try {
                                 String fvg = JOptionPane.showInputDialog("Ingrese el valor de gasto de " + RazonGasto);
                                    // Verificar si el usuario cerró el cuadro de diálogo o presionó "Cancelar"
                                    if (fvg == null || fvg.trim().isEmpty()) {
                                        JOptionPane.showMessageDialog(null, "Debe ingresar un valor válido para continuar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                                        continue; // Volver a pedir el dato
                                    }
                                    ValorGasto = Float.parseFloat(fvg.trim()); // Intentar convertir a float
                                    // Si la conversión fue exitosa, salir del bucle
                                    break;
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null, "Debe ingresar un número válido para el costo del gasto " + RazonGasto, "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }

            float ValorGastoReal = ValorGasto*-1;
            JOptionPane.showMessageDialog(null, "valor gasto de mantenimiento: "+ValorGastoReal);
            
        
        // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO gastosmantenimiento " + "(ID_usuario, RazondeGasto, ValorGasto, FechaGasto) VALUES (?, ?, ?, ?)";
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            ps.setInt(1, IDUsuario);
            ps.setString(2, RazonGasto);
             ps.setFloat(3, ValorGastoReal);
            ps.setString(4, FechaDeGasto.toString()); // Convertir FechaCompra a cadena
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null,"Gasto de mantenimiento registrado con exito");
            }
            
            InsertarTransaccion(FechaDeGasto, ValorGastoReal );
    } 
    
    public void InsertarTransaccion(LocalDateTime FechaDeGasto, float ValorGastoReal) throws SQLException {
    int idGastoM = 0;
    String query = "SELECT id FROM GastosMantenimiento WHERE FechaGasto = ? AND ValorGasto = ?";

    // Consulta el ID de la compra
    try (PreparedStatement stmt = cx.prepareStatement(query)) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedFechaGasto = FechaDeGasto.format(formatter);
        stmt.setString(1, formattedFechaGasto);
        stmt.setFloat(2, Math.round(ValorGastoReal * 100.0) / 100.0f);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            idGastoM = rs.getInt("id");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el ID del Gasto de Mantenimiento, por favor verifica los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir si no se encuentra el ID
        }
    }

    // Inserta en transacciones
    String tipot = "GASTO_MANTENIMIENTO";
    String sql = "INSERT INTO transacciones (TIPO_TRANSACCION, id_GastoMantenimiento) VALUES (?, ?)";

    try (PreparedStatement ps = cx.prepareStatement(sql)) {
        ps.setString(1, tipot);
        ps.setInt(2, idGastoM);

        int filasInsertadas = ps.executeUpdate();
        if (filasInsertadas > 0) {  System.out.println("Si se registro algo");          
        }else{System.out.println("no se inserto transaccion");}
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al registrar la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
}
