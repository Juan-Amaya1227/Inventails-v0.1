package Dashboards.Logic;

import java.sql.*;
import java.util.*;

public class TrazabilidadLogic {
    private Connection connection;

    public TrazabilidadLogic(Connection connection) {
        this.connection = connection;
    }

    public Map<String, Integer> fetchBubbleData() {
        Map<String, Integer> data = new HashMap<>();
        // Agrupar por el sitio principal (ej: "Potrero" en lugar de "Potrero espacio: 1")
        String sql = "SELECT SUBSTRING_INDEX(ubicacion, ' espacio:', 1) as sitio, COUNT(*) " +
                     "FROM animales WHERE estado = 1 GROUP BY sitio";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    public Map<String, Integer> fetchConteosPorEstado() {
    Map<String, Integer> estados = new HashMap<>();
    
    String sql = "SELECT CASE estado WHEN 1 THEN 'Activos' WHEN 2 THEN 'Inactivos' END, COUNT(*) " +
                 "FROM animales GROUP BY estado";
    try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
            String situacion = rs.getString(1);
            int cantidad = rs.getInt(2);
            if (situacion != null) {
                estados.put(situacion, cantidad);
            }
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
    return estados;
}

    public List<Object[]> fetchTop5Razas() {
        List<Object[]> razas = new ArrayList<>();
        String sql = "SELECT RazaAnimal, PropositoAnimal, COUNT(*) as c " +
                     "FROM pedigree GROUP BY 1, 2 ORDER BY c DESC LIMIT 5";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                razas.add(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3)});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return razas;
    }
}
