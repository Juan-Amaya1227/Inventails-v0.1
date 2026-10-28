package Vistas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Stack;

/**
 * Lógica para el proceso de venta de animales.
 * Extraída y adaptada de JavaApplication10.
 */
public class VentaLogic {

    private final Connection cx;
    private final int idUsuario;

    public VentaLogic(Connection cx, int idUsuario) {
        this.cx = cx;
        this.idUsuario = idUsuario;
    }

    public boolean registrarVenta(
            String codigo, int idAnimal, double costoVenta, double pesoFinal,
            Stack<Object[]> pilaVenta, Map<String, Object> registros) throws SQLException {

        cx.setAutoCommit(false);

        try {
            // 1. Consultar y Actualizar Saldo
            double saldoActual = 0;
            try (PreparedStatement psS = cx.prepareStatement("SELECT saldo FROM saldo WHERE id = 1");
                 ResultSet rsS = psS.executeQuery()) {
                if (rsS.next()) saldoActual = rsS.getDouble("saldo");
            }
            
            double nuevoSaldo = saldoActual + costoVenta;
            try (PreparedStatement psUS = cx.prepareStatement("UPDATE saldo SET saldo = ? WHERE id = 1")) {
                psUS.setDouble(1, nuevoSaldo);
                psUS.executeUpdate();
            }

            // 2. Insertar en tabla ventas
            LocalDateTime fecha = LocalDateTime.now();
            String sqlVenta = "INSERT INTO ventas (ID_animal, ID_usuario, f_venta, v_venta, Peso_final_kg) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psV = cx.prepareStatement(sqlVenta, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psV.setInt(1, idAnimal);
                psV.setInt(2, idUsuario);
                psV.setString(3, fecha.toString());
                psV.setDouble(4, costoVenta);
                psV.setDouble(5, pesoFinal);
                psV.executeUpdate();
                
                ResultSet rsV = psV.getGeneratedKeys();
                if (rsV.next()) {
                    int idVenta = rsV.getInt(1);
                    // 3. Insertar Transacción
                    try (PreparedStatement psT = cx.prepareStatement("INSERT INTO transacciones (TIPO_TRANSACCION, id_venta) VALUES ('VENTA', ?)")) {
                        psT.setInt(1, idVenta);
                        psT.executeUpdate();
                    }
                }
            }

            // 4. Actualizar Memoria
            pilaVenta.push(new Object[]{codigo, costoVenta, fecha});
            registros.remove(codigo);

            cx.commit();
            return true;
        } catch (SQLException e) {
            cx.rollback();
            throw e;
        } finally {
            cx.setAutoCommit(true);
        }
    }
}
