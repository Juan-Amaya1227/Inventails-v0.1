
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class EspaciosYestadosA {
    public  String db = "inventails";
    public  String url = "jdbc:mysql://localhost:3306/";
    public  String user = "root";
    public  String password = "";
    public  String tipa;
    
    public EspaciosYestadosA(String url, String db, String password, String tipa, String user){
    this.url = url;
    this.db = db;
    this.password = password;
    this.tipa= tipa;
    this.user = user;
    }
    
    public void EspaciosA(){

        imprimirMatriz();
  
    }
    
     public void imprimirMatriz() {
        if(tipa == null){
        JOptionPane.showMessageDialog(null,"Error, debe seleccionar primero un tipo de animal","error",JOptionPane.ERROR_MESSAGE);
        return;}
        
            JTable tabla = crearMatrizUbicacion();

            // ¡No hagas nada si es null! Solo se mostró el mensaje de error.
            if (tabla != null) {
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("Ubicaciones de " + tipa + " activos");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(800, 400);
                    frame.add(new JScrollPane(tabla));
                    frame.setLocationRelativeTo(null); // Centrar ventana
                    frame.setVisible(true);
                });
            }
        }
     
     public JTable crearMatrizUbicacion() {
            try (Connection connection = DriverManager.getConnection(url+db, user, password);
                 PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT * FROM animales WHERE tipo_animal = ? AND estado = 'activo'",
                     ResultSet.TYPE_SCROLL_INSENSITIVE, 
                     ResultSet.CONCUR_READ_ONLY
                 )) {

                pstmt.setString(1, tipa); // ¡Seguro contra SQL injection!
                ResultSet rs = pstmt.executeQuery();

                // Verifica si hay datos
                if (!rs.isBeforeFirst()) {
                    JOptionPane.showMessageDialog(null, "No hay " + tipa + " activos/as",  "error: err004", JOptionPane.ERROR_MESSAGE);
                    return null; // Retorna null SIN crear una tabla
                }

                // Procesar datos si existen
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                }

                Object[][] data = rsToArray(rs, columnCount);
                return new JTable(new DefaultTableModel(data, columnNames));

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al cargar datos: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return null;
            }
        }
     
     private Object[][] rsToArray(ResultSet rs, int columnCount) throws SQLException {
        rs.last(); // se movera a la ultima fila para contar cuántas columnas hay
        int rowCount = rs.getRow();
        rs.beforeFirst(); // vuelve al inicio del ResultSet

        Object[][] data = new Object[rowCount][columnCount];
        int rowIndex = 0;

        while (rs.next()) {
            for (int col = 1; col <= columnCount; col++) {
                data[rowIndex][col - 1] = rs.getObject(col);
            }
            rowIndex++;
        }
        return data;
    }
    
}
