package Dashboards.Logic;

import java.sql.*;
import java.util.*;

public class SaludLogic {
    private Connection connection;

    public SaludLogic(Connection connection) {
        this.connection = connection;
    }

    public Map<String, Integer> fetchCausasDefuncion() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT r_defuncion, COUNT(*) FROM defunciones GROUP BY r_defuncion";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    public List<Object[]> fetchEnfermedadesTiempo() {
        List<Object[]> timeline = new ArrayList<>();
        String sql = "SELECT DATE(F_Diagnostico), COUNT(*) FROM registroenfermedades GROUP BY 1 ORDER BY 1";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                timeline.add(new Object[]{rs.getString(1), rs.getInt(2)});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return timeline;
    }

    public Map<String, Integer> fetchMedicamentos() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT MedicamentoAplicado, COUNT(*) FROM medicinasaplicadas GROUP BY MedicamentoAplicado";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
}
