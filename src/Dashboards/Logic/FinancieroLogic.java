package Dashboards.Logic;

import java.sql.*;
import java.util.*;

public class FinancieroLogic {
    private Connection connection;

    public FinancieroLogic(Connection connection) {
        this.connection = connection;
    }

    public double fetchSaldoActual() {
        double saldo = 0.0;
        String sql = "SELECT saldo FROM saldo WHERE id = 1";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                saldo = rs.getDouble(1);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return saldo;
    }

    public Map<String, double[]> fetchFlujoCaja() {
    // 1. Usar un LinkedHashMap para respetar el orden cronológico estricto
    Map<String, double[]> flow = new LinkedHashMap<>();
    
    // 2. Pre-poblar el mapa con los 12 meses del año en CERO
    for (int i = 1; i <= 12; i++) {
        flow.put("Mes " + i, new double[]{0.0, 0.0}); // [Compras, Ventas]
    }

    try {
        String sqlCompras = "SELECT MONTH(f_compra), SUM(v_compra) FROM compras GROUP BY 1";
        String sqlVentas = "SELECT MONTH(f_venta), SUM(v_venta) FROM ventas GROUP BY 1";
        
        Statement st = connection.createStatement();
        
        // 3. Mapear compras (Asegurando valores absolutos)
        ResultSet rs = st.executeQuery(sqlCompras);
        while (rs.next()) {
            String key = "Mes " + rs.getInt(1);
            if (flow.containsKey(key)) {
                flow.get(key)[0] = Math.abs(rs.getDouble(2)); 
            }
        }
        
        // 4. Mapear ventas
        rs = st.executeQuery(sqlVentas);
        while (rs.next()) {
            String key = "Mes " + rs.getInt(1);
            if (flow.containsKey(key)) {
                flow.get(key)[1] = Math.abs(rs.getDouble(2));
            }
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return flow;
}

    public Map<String, Double> fetchGastos() {
        Map<String, Double> gastos = new HashMap<>();
        String sql = "SELECT RazondeGasto, SUM(ABS(ValorGasto)) FROM gastosmantenimiento GROUP BY RazondeGasto";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                gastos.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return gastos;
    }
}
