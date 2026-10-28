package Dashboards.Logic;

import java.sql.*;
import java.util.*;

public class AuditoriaLogic {
    private Connection connection;

    public AuditoriaLogic(Connection connection) {
        this.connection = connection;
    }

    public List<String> fetchFeed() {
        List<String> feed = new ArrayList<>();
        String sql = "SELECT CONCAT(u.Usuario, ': ', a.Accion, ' (', a.FechaAccion, ')') " +
                     "FROM asistencia a JOIN usuarios u ON a.ID_Usuario = u.id " +
                     "ORDER BY a.FechaAccion DESC LIMIT 10";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                feed.add(rs.getString(1));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return feed;
    }

    public Map<Integer, int[]> fetchUserActions() {
    Map<Integer, int[]> actions = new TreeMap<>();
    try {
        // Cambiar para filtrar últimas 24 horas
        String sql = "SELECT HOUR(FechaAccion), Accion, COUNT(*) " +
                     "FROM asistencia " +
                     "WHERE FechaAccion >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                     "GROUP BY 1, 2";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int hour = rs.getInt(1);
                String type = rs.getString(2);
                int count = rs.getInt(3);
                int[] vals = actions.getOrDefault(hour, new int[]{0, 0});
                // CORREGIDO: usar "Autenticacion" en lugar de "Conexion"
                if ("Autenticacion".equalsIgnoreCase(type)) vals[0] += count;
                else if ("Desconexion".equalsIgnoreCase(type)) vals[1] += count;
                actions.put(hour, vals);
            }
        }
    } catch (Exception e) { e.printStackTrace(); }
    return actions;
}

    public int fetchFailedAttempts() {
        int count = 0;
        String sql = """
                     SELECT COUNT(*) as EventosBloqueoHoy
                     FROM IntentosFallidos 
                     WHERE DATE(FechaAccion) = CURDATE();""";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }

    public int fetchSecurityLevel() {
        // 2: Verde, 1: Amarillo, 0: Rojo
        int failed = fetchFailedAttempts();
        if (failed > 5) return 0;
        if (failed > 0) return 1;
        return 2;
    }
}
