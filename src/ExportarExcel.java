import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ExportarExcel {

    private final String url;
    private final String user;
    private final String password;
    private final String db;
    private String tiporeporte;
    private final int IDUsuario;
    public final Connection cx;
    public String desktopPath = System.getProperty("user.home") + "/Desktop";

    public ExportarExcel(String url, String db, String user, String password, int IDUsuario, Connection cx) {
        this.url = url;
        this.db = db;
        this.user = user;
        this.password = password;
        this.IDUsuario = IDUsuario;
        this.cx = cx;
    }

    public void seleccionartipodereporte() throws SQLException {
        String[] tiporeporteexcel = {"Compra y venta", "Pedigree", "Engorde", "Medicamentos Aplicados", "Defunciones", "Transacciones totales", "Auditoria"};
        JComboBox<String> comboboxtipodereporte = new JComboBox<>(tiporeporteexcel);
        tiporeporte = null;

        JPanel panelexcel = new JPanel();
        panelexcel.add(new JLabel("Seleccione un tipo de reporte"));
        panelexcel.add(comboboxtipodereporte);

        int resultadotexcel = JOptionPane.showConfirmDialog(
                null,
                panelexcel,
                "Seleccione la opcion",
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultadotexcel != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Generacion de reporte/s cancelado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (resultadotexcel == JOptionPane.OK_OPTION) {
            tiporeporte = (String) comboboxtipodereporte.getSelectedItem();
            if (null != tiporeporte) {
                switch (tiporeporte) {
                    case "Compra y venta" ->
                        Compra_y_venta();
                    case "Pedigree" ->
                        ReportePedigree();
                    case "Engorde" ->
                        ReporteEngorde();
                    case "Medicamentos Aplicados" ->
                        MedicinasAplicadas();
                    case "Defunciones" ->
                        ReporteDefunciones();
                    case "Transacciones totales" ->
                        ReporteTransaccionesTotales ();
                    case "Auditoria" ->
                        JOptionPane.showMessageDialog(null,"Opcion no disponible temporalmente");
                    default -> {
                    }
                }
            }

        }

    }
    
    private void AuditoriaGeneracionReporte()throws SQLException{
    LocalDateTime FechaDescargaReporte = LocalDateTime.now();
    // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO DescargaReportes(Responsable, ReporteDescargado, FechaDescarga)"+
                         " values (?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setInt(1, IDUsuario); 
            ps.setString(2, tiporeporte);
            ps.setString(3, FechaDescargaReporte.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
    }

    public void Compra_y_venta() throws SQLException {
        String excelFilePath = Paths.get(desktopPath, "Reporte_Compra_venta_Inventails.xlsx").toString();
        //primera hoja ubicacion bovinos

        try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {

            System.out.println("Conexion establecida y consulta ejecutada.");

            // 1. Hoja Bovinos
            Sheet sheet = workbook.createSheet("Ubi bovinos");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Codigo");
            headerRow.createCell(2).setCellValue("Tipo de animal");
            headerRow.createCell(3).setCellValue("Genero");
            headerRow.createCell(4).setCellValue("Ubicacion");
            headerRow.createCell(5).setCellValue("Estado");

            // Intentar llenar datos
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("select * from animales where tipo_animal = 'Bovinos' and estado = 'activo'")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet.getString("tipo_animal"));
                    row.createCell(3).setCellValue(resultSet.getString("Genero"));
                    row.createCell(4).setCellValue(resultSet.getString("ubicacion"));
                    row.createCell(5).setCellValue(resultSet.getString("estado"));
                }
                 
            } catch (SQLException e) {
                System.err.println("Error en bovinos: " + e.getMessage());
            }

            // 2. Hoja Gallinas (mismo patrón)
            Sheet sheet2 = workbook.createSheet("Ubi gallinas");
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("ID");
            headerRow2.createCell(1).setCellValue("Codigo");
            headerRow2.createCell(2).setCellValue("Tipo de animal");
            headerRow2.createCell(3).setCellValue("Genero");
            headerRow2.createCell(4).setCellValue("Ubicacion");
            headerRow2.createCell(5).setCellValue("Estado");

            try (Statement statement2 = connection.createStatement(); ResultSet resultSet2 = statement2.executeQuery("select * from animales where tipo_animal = 'Gallinas' and estado = 'activo'")) {

                int rowNum2 = 1;
                while (resultSet2.next()) {
                    Row row = sheet2.createRow(rowNum2++);
                    row.createCell(0).setCellValue(resultSet2.getInt("id"));
                    row.createCell(1).setCellValue(resultSet2.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet2.getString("tipo_animal"));
                    row.createCell(3).setCellValue(resultSet2.getString("Genero"));
                    row.createCell(4).setCellValue(resultSet2.getString("ubicacion"));
                    row.createCell(5).setCellValue(resultSet2.getString("estado"));
                }
            } catch (SQLException e) {
                System.err.println("Error en gallinas: " + e.getMessage());
            }

            // 3. Hoja Cerdos(mismo patrón)
            Sheet sheet3 = workbook.createSheet("Ubi cerdos");
            Row headerRow3 = sheet3.createRow(0);
            headerRow3.createCell(0).setCellValue("ID");
            headerRow3.createCell(1).setCellValue("Codigo");
            headerRow3.createCell(2).setCellValue("Tipo de animal");
            headerRow3.createCell(3).setCellValue("Genero");
            headerRow3.createCell(4).setCellValue("Ubicacion");
            headerRow3.createCell(5).setCellValue("Estado");

            try (Statement statement3 = connection.createStatement(); ResultSet resultSet3 = statement3.executeQuery("select * from animales where tipo_animal = 'Cerdos' and estado = 'activo'")) {

                int rowNum3 = 1;
                while (resultSet3.next()) {
                    Row row = sheet3.createRow(rowNum3++);
                    row.createCell(0).setCellValue(resultSet3.getInt("id"));
                    row.createCell(1).setCellValue(resultSet3.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet3.getString("tipo_animal"));
                    row.createCell(3).setCellValue(resultSet3.getString("Genero"));
                    row.createCell(4).setCellValue(resultSet3.getString("ubicacion"));
                    row.createCell(5).setCellValue(resultSet3.getString("estado"));
                }
            } catch (SQLException e) {
                System.err.println("Error en Cerdos: " + e.getMessage());
            }

            // 4. Hoja Caballos(mismo patrón)
            Sheet sheet4 = workbook.createSheet("Ubi caballos");
            Row headerRow4 = sheet4.createRow(0);
            headerRow4.createCell(0).setCellValue("ID");
            headerRow4.createCell(1).setCellValue("Codigo");
            headerRow4.createCell(2).setCellValue("Tipo de animal");
            headerRow4.createCell(3).setCellValue("Genero");
            headerRow4.createCell(4).setCellValue("Ubicacion");
            headerRow4.createCell(5).setCellValue("Estado");

            try (Statement statement4 = connection.createStatement(); ResultSet resultSet4 = statement4.executeQuery("select * from animales where tipo_animal = 'Caballos' and estado = 'activo'")) {

                int rowNum4 = 1;
                while (resultSet4.next()) {
                    Row row = sheet4.createRow(rowNum4++);
                    row.createCell(0).setCellValue(resultSet4.getInt("id"));
                    row.createCell(1).setCellValue(resultSet4.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet4.getString("tipo_animal"));
                    row.createCell(3).setCellValue(resultSet4.getString("Genero"));
                    row.createCell(4).setCellValue(resultSet4.getString("ubicacion"));
                    row.createCell(5).setCellValue(resultSet4.getString("estado"));
                }
            } catch (SQLException e) {
                System.err.println("Error en Caballos: " + e.getMessage());
            }

            // 5. Hoja Caprinos(mismo patrón)
            Sheet sheet5 = workbook.createSheet("Ubi caprinos");
            Row headerRow5 = sheet5.createRow(0);
            headerRow5.createCell(0).setCellValue("ID");
            headerRow5.createCell(1).setCellValue("Codigo");
            headerRow5.createCell(2).setCellValue("Tipo de animal");
            headerRow5.createCell(3).setCellValue("Genero");
            headerRow5.createCell(4).setCellValue("Ubicacion");
            headerRow5.createCell(5).setCellValue("Estado");

            try (Statement statement5 = connection.createStatement(); ResultSet resultSet5 = statement5.executeQuery("select * from animales where tipo_animal = 'Caprinos' and estado = 'activo'")) {

                int rowNum5 = 1;
                while (resultSet5.next()) {
                    Row row = sheet5.createRow(rowNum5++);
                    row.createCell(0).setCellValue(resultSet5.getInt("id"));
                    row.createCell(1).setCellValue(resultSet5.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet5.getString("tipo_animal"));
                    row.createCell(3).setCellValue(resultSet5.getString("Genero"));
                    row.createCell(4).setCellValue(resultSet5.getString("ubicacion"));
                    row.createCell(5).setCellValue(resultSet5.getString("estado"));
                }
            } catch (SQLException e) {
                System.err.println("Error en Caprinos: " + e.getMessage());
            }

            // 6. Hoja Transacciones Bovinos 
            Sheet sheet6 = workbook.createSheet("Transacciones Bovinos");
            Row headerRow6 = sheet6.createRow(0);
            headerRow6.createCell(0).setCellValue("ID");
            headerRow6.createCell(1).setCellValue("Codigo");
            headerRow6.createCell(2).setCellValue("Usuario Compra");
            headerRow6.createCell(3).setCellValue("Fecha compra");
            headerRow6.createCell(4).setCellValue("Valor monto Compra");
            headerRow6.createCell(5).setCellValue("Usuario Venta");
            headerRow6.createCell(6).setCellValue("Fecha Venta");
            headerRow6.createCell(7).setCellValue("Valor monto Venta");

            try (Statement statement6 = connection.createStatement(); ResultSet resultSet6 = statement6.executeQuery(
                                                                                "SELECT " +    
                                                                                "    a.ID, " +
                                                                                "    a.CODIGO, " +
                                                                                "    uc.USUARIO AS USUARIO_COMPRA, " +
                                                                                "    c.F_COMPRA, " +
                                                                                "    c.V_COMPRA,\n" +
                                                                                "    uv.USUARIO AS USUARIO_VENTA, " +
                                                                                "    v.F_VENTA, " +
                                                                                "    v.V_VENTA " +
                                                                                "FROM " +
                                                                                "    Animales a " +
                                                                                "LEFT JOIN " +
                                                                                "    Compras c ON a.ID = c.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uc ON c.ID_USUARIO = uc.ID " +
                                                                                "LEFT JOIN " +
                                                                                "    Ventas v ON a.ID = v.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uv ON v.ID_USUARIO = uv.ID " +
                                                                                "WHERE " +
                                                                                "    a.codigo LIKE '%BOV%'")) {

                int rowNum8 = 1;
                while (resultSet6.next()) {
                    Row row = sheet6.createRow(rowNum8++);
                    row.createCell(0).setCellValue(resultSet6.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet6.getString("CODIGO"));
                    row.createCell(2).setCellValue(resultSet6.getString("USUARIO_COMPRA"));
                    row.createCell(3).setCellValue(resultSet6.getString("F_COMPRA"));
                    row.createCell(4).setCellValue(resultSet6.getDouble("V_COMPRA"));
                    String uv = resultSet6.getString("USUARIO_VENTA");
                    if(uv == null){
                    uv = "No disponible";
                    }
                    row.createCell(5).setCellValue(uv);
                    String fv = resultSet6.getString("F_VENTA");
                    if (fv == null) {
                        fv = "No disponible";
                    }
                    row.createCell(6).setCellValue(fv);
                    Double vv = resultSet6.getDouble("V_VENTA");
                    if (resultSet6.wasNull()) {
                        vv = 0.0;
                    }
                    row.createCell(7).setCellValue(vv);

                }
            } catch (SQLException e) {
                System.err.println("Error en transacciones Bovinos: " + e.getMessage());
            }
            
            // septima hoja transacciones gallinas
            Sheet sheet7 = workbook.createSheet("Transacciones Gallinas");
            Row headerRow7 = sheet7.createRow(0);
            headerRow7.createCell(0).setCellValue("ID");
            headerRow7.createCell(1).setCellValue("Codigo");
            headerRow7.createCell(2).setCellValue("Usuario Compra");
            headerRow7.createCell(3).setCellValue("Fecha compra");
            headerRow7.createCell(4).setCellValue("Valor monto Compra");
            headerRow7.createCell(5).setCellValue("Usuario Venta");
            headerRow7.createCell(6).setCellValue("Fecha Venta");
            headerRow7.createCell(7).setCellValue("Valor monto Venta");

            try (Statement statement7 = connection.createStatement(); 
                 ResultSet resultSet7 = statement7.executeQuery(
                                                                                "SELECT " +    
                                                                                "    a.ID, " +
                                                                                "    a.CODIGO, " +
                                                                                "    uc.USUARIO AS USUARIO_COMPRA, " +
                                                                                "    c.F_COMPRA, " +
                                                                                "    c.V_COMPRA,\n" +
                                                                                "    uv.USUARIO AS USUARIO_VENTA, " +
                                                                                "    v.F_VENTA, " +
                                                                                "    v.V_VENTA " +
                                                                                "FROM " +
                                                                                "    Animales a " +
                                                                                "LEFT JOIN " +
                                                                                "    Compras c ON a.ID = c.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uc ON c.ID_USUARIO = uc.ID " +
                                                                                "LEFT JOIN " +
                                                                                "    Ventas v ON a.ID = v.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uv ON v.ID_USUARIO = uv.ID " +
                                                                                "WHERE " +
                                                                                "    a.codigo LIKE '%GAL%'")) {

                int rowNum8 = 1;
                while (resultSet7.next()) {
                    Row row = sheet7.createRow(rowNum8++);
                    row.createCell(0).setCellValue(resultSet7.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet7.getString("CODIGO"));
                    row.createCell(2).setCellValue(resultSet7.getString("USUARIO_COMPRA"));
                    row.createCell(3).setCellValue(resultSet7.getString("F_COMPRA"));
                    row.createCell(4).setCellValue(resultSet7.getDouble("V_COMPRA"));
                    String uv = resultSet7.getString("USUARIO_VENTA");
                    if(uv == null){
                    uv = "No disponible";
                    }
                    row.createCell(5).setCellValue(uv);
                    String fv = resultSet7.getString("F_VENTA");
                    if (fv == null) {
                        fv = "No disponible";
                    }
                    row.createCell(6).setCellValue(fv);
                    Double vv = resultSet7.getDouble("V_VENTA");
                    if (resultSet7.wasNull()) {
                        vv = 0.0;
                    }
                    row.createCell(7).setCellValue(vv);

                }
            } catch (SQLException e) {
                System.err.println("Error en transacciones Gallinass: " + e.getMessage());
            }
            
            // 8. Hoja Transacciones Cerdos 
            Sheet sheet8 = workbook.createSheet("Transacciones Cerdos");
            Row headerRow8 = sheet8.createRow(0);
            headerRow8.createCell(0).setCellValue("ID");
            headerRow8.createCell(1).setCellValue("Codigo");
            headerRow8.createCell(2).setCellValue("Usuario Compra");
            headerRow8.createCell(3).setCellValue("Fecha compra");
            headerRow8.createCell(4).setCellValue("Valor monto Compra");
            headerRow8.createCell(5).setCellValue("Usuario Venta");
            headerRow8.createCell(6).setCellValue("Fecha Venta");
            headerRow8.createCell(7).setCellValue("Valor monto Venta");

            try (Statement statement8 = connection.createStatement(); 
                 ResultSet resultSet8 = statement8.executeQuery(
                                                                                "SELECT " +    
                                                                                "    a.ID, " +
                                                                                "    a.CODIGO, " +
                                                                                "    uc.USUARIO AS USUARIO_COMPRA, " +
                                                                                "    c.F_COMPRA, " +
                                                                                "    c.V_COMPRA,\n" +
                                                                                "    uv.USUARIO AS USUARIO_VENTA, " +
                                                                                "    v.F_VENTA, " +
                                                                                "    v.V_VENTA " +
                                                                                "FROM " +
                                                                                "    Animales a " +
                                                                                "LEFT JOIN " +
                                                                                "    Compras c ON a.ID = c.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uc ON c.ID_USUARIO = uc.ID " +
                                                                                "LEFT JOIN " +
                                                                                "    Ventas v ON a.ID = v.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uv ON v.ID_USUARIO = uv.ID " +
                                                                                "WHERE " +
                                                                                "    a.codigo LIKE '%CER%'")) {

                int rowNum8 = 1;
                while (resultSet8.next()) {
                    Row row = sheet8.createRow(rowNum8++);
                    row.createCell(0).setCellValue(resultSet8.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet8.getString("CODIGO"));
                    row.createCell(2).setCellValue(resultSet8.getString("USUARIO_COMPRA"));
                    row.createCell(3).setCellValue(resultSet8.getString("F_COMPRA"));
                    row.createCell(4).setCellValue(resultSet8.getDouble("V_COMPRA"));
                    String uv = resultSet8.getString("USUARIO_VENTA");
                    if(uv == null){
                    uv = "No disponible";
                    }
                    row.createCell(5).setCellValue(uv);
                    String fv = resultSet8.getString("F_VENTA");
                    if (fv == null) {
                        fv = "No disponible";
                    }
                    row.createCell(6).setCellValue(fv);
                    Double vv = resultSet8.getDouble("V_VENTA");
                    if (resultSet8.wasNull()) {
                        vv = 0.0;
                    }
                    row.createCell(7).setCellValue(vv);

                }
            } catch (SQLException e) {
                System.err.println("Error en transacciones Cerdos: " + e.getMessage());
            }

            // 9. Hoja Transacciones Caballos 
            Sheet sheet9 = workbook.createSheet("Transacciones Caballos");
            Row headerRow9 = sheet7.createRow(0);
            headerRow9.createCell(0).setCellValue("ID");
            headerRow9.createCell(1).setCellValue("Codigo");
            headerRow9.createCell(2).setCellValue("Usuario Compra");
            headerRow9.createCell(3).setCellValue("Fecha compra");
            headerRow9.createCell(4).setCellValue("Valor monto Compra");
            headerRow9.createCell(5).setCellValue("Usuario Venta");
            headerRow9.createCell(6).setCellValue("Fecha Venta");
            headerRow9.createCell(7).setCellValue("Valor monto Venta");

            try (Statement statement9 = connection.createStatement(); 
                 ResultSet resultSet9 = statement9.executeQuery(
                                                                                "SELECT " +    
                                                                                "    a.ID, " +
                                                                                "    a.CODIGO, " +
                                                                                "    uc.USUARIO AS USUARIO_COMPRA, " +
                                                                                "    c.F_COMPRA, " +
                                                                                "    c.V_COMPRA,\n" +
                                                                                "    uv.USUARIO AS USUARIO_VENTA, " +
                                                                                "    v.F_VENTA, " +
                                                                                "    v.V_VENTA " +
                                                                                "FROM " +
                                                                                "    Animales a " +
                                                                                "LEFT JOIN " +
                                                                                "    Compras c ON a.ID = c.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uc ON c.ID_USUARIO = uc.ID " +
                                                                                "LEFT JOIN " +
                                                                                "    Ventas v ON a.ID = v.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uv ON v.ID_USUARIO = uv.ID " +
                                                                                "WHERE " +
                                                                                "    a.codigo LIKE '%CAB%'")) {

                int rowNum8 = 1;
                while (resultSet9.next()) {
                    Row row = sheet9.createRow(rowNum8++);
                    row.createCell(0).setCellValue(resultSet9.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet9.getString("CODIGO"));
                    row.createCell(2).setCellValue(resultSet9.getString("USUARIO_COMPRA"));
                    row.createCell(3).setCellValue(resultSet9.getString("F_COMPRA"));
                    row.createCell(4).setCellValue(resultSet9.getDouble("V_COMPRA"));
                    String uv = resultSet9.getString("USUARIO_VENTA");
                    if(uv == null){
                    uv = "No disponible";
                    }
                    row.createCell(5).setCellValue(uv);
                    String fv = resultSet9.getString("F_VENTA");
                    if (fv == null) {
                        fv = "No disponible";
                    }
                    row.createCell(6).setCellValue(fv);
                    Double vv = resultSet9.getDouble("V_VENTA");
                    if (resultSet9.wasNull()) {
                        vv = 0.0;
                    }
                    row.createCell(7).setCellValue(vv);

                }
            } catch (SQLException e) {
                System.err.println("Error en transacciones Caballos: " + e.getMessage());
            }

            // 10. Hoja Transacciones Caprinos 
            Sheet sheet10 = workbook.createSheet("Transacciones Caprinos");
            Row headerRow10 = sheet7.createRow(0);
            headerRow10.createCell(0).setCellValue("ID");
            headerRow10.createCell(1).setCellValue("Codigo");
            headerRow10.createCell(2).setCellValue("Usuario Compra");
            headerRow10.createCell(3).setCellValue("Fecha compra");
            headerRow10.createCell(4).setCellValue("Valor monto Compra");
            headerRow10.createCell(5).setCellValue("Usuario Venta");
            headerRow10.createCell(6).setCellValue("Fecha Venta");
            headerRow10.createCell(7).setCellValue("Valor monto Venta");

            try (Statement statement10 = connection.createStatement(); 
                 ResultSet resultSet10 = statement10.executeQuery(
                                                                                "SELECT " +    
                                                                                "    a.ID, " +
                                                                                "    a.CODIGO, " +
                                                                                "    uc.USUARIO AS USUARIO_COMPRA, " +
                                                                                "    c.F_COMPRA, " +
                                                                                "    c.V_COMPRA,\n" +
                                                                                "    uv.USUARIO AS USUARIO_VENTA, " +
                                                                                "    v.F_VENTA, " +
                                                                                "    v.V_VENTA " +
                                                                                "FROM " +
                                                                                "    Animales a " +
                                                                                "LEFT JOIN " +
                                                                                "    Compras c ON a.ID = c.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uc ON c.ID_USUARIO = uc.ID " +
                                                                                "LEFT JOIN " +
                                                                                "    Ventas v ON a.ID = v.ID_ANIMAL " +
                                                                                "LEFT JOIN " +
                                                                                "    Usuarios uv ON v.ID_USUARIO = uv.ID " +
                                                                                "WHERE " +
                                                                                "    a.codigo LIKE '%CAP%'")) {

                int rowNum8 = 1;
                while (resultSet10.next()) {
                    Row row = sheet10.createRow(rowNum8++);
                    row.createCell(0).setCellValue(resultSet10.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet10.getString("CODIGO"));
                    row.createCell(2).setCellValue(resultSet10.getString("USUARIO_COMPRA"));
                    row.createCell(3).setCellValue(resultSet10.getString("F_COMPRA"));
                    row.createCell(4).setCellValue(resultSet10.getDouble("V_COMPRA"));
                    String uv = resultSet10.getString("USUARIO_VENTA");
                    if(uv == null){
                    uv = "No disponible";
                    }
                    row.createCell(5).setCellValue(uv);
                    String fv = resultSet10.getString("F_VENTA");
                    if (fv == null) {
                        fv = "No disponible";
                    }
                    row.createCell(6).setCellValue(fv);
                    Double vv = resultSet10.getDouble("V_VENTA");
                    if (resultSet10.wasNull()) {
                        vv = 0.0;
                    }
                    row.createCell(7).setCellValue(vv);

                }
            } catch (SQLException e) {
                System.err.println("Error en transacciones Caprinos: " + e.getMessage());
            }

            // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        AuditoriaGeneracionReporte();
    }

    public void ReportePedigree() throws SQLException {

        String excelFilePath = Paths.get(desktopPath, "Reporte_Pedigre_Inventails.xlsx").toString();

        try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {

            //primera hoja pedigree bovinos
            Sheet sheet = workbook.createSheet("Pedigree bovinos");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Codigo");
            headerRow.createCell(2).setCellValue("Raza del animal");
            headerRow.createCell(3).setCellValue("Proposito del animal");
            headerRow.createCell(4).setCellValue("Raza del Padre");
            headerRow.createCell(5).setCellValue("Raza de la Madre");

            // Intentar llenar datos
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT "
                    + "    Animales.ID, "
                    + "    Animales.CODIGO, "
                    + "    Pedigree.RazaAnimal, "
                    + "    Pedigree.PropositoAnimal, "
                    + "    Pedigree.RazaPadre, "
                    + "    Pedigree.RazaMadre "
                    + "FROM "
                    + "    Animales "
                    + "INNER JOIN "
                    + "    Pedigree "
                    + "ON "
                    + "    Animales.ID = Pedigree.ID_ANIMAL "
                    + "where "
                    + " animales.codigo LIKE '%BOV%'")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet.getString("RazaAnimal"));
                    row.createCell(3).setCellValue(resultSet.getString("PropositoAnimal"));
                    row.createCell(4).setCellValue(resultSet.getString("RazaPadre"));
                    row.createCell(5).setCellValue(resultSet.getString("RazaMadre"));
                }
            } catch (SQLException e) {
                System.err.println("Error en pedigree bovinos: " + e.getMessage());
            }

            //segunda hoja pedigree gallinas
            Sheet sheet2 = workbook.createSheet("Pedigree gallinas");
            // Crear encabezados
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("ID");
            headerRow2.createCell(1).setCellValue("Codigo");
            headerRow2.createCell(2).setCellValue("Raza del animal");
            headerRow2.createCell(3).setCellValue("Proposito del animal");
            headerRow2.createCell(4).setCellValue("Raza del Padre");
            headerRow2.createCell(5).setCellValue("Raza de la Madre");

            // Intentar llenar datos
            try (Statement statement2 = connection.createStatement(); ResultSet resultSet2 = statement2.executeQuery("SELECT "
                    + "    Animales.ID, "
                    + "    Animales.CODIGO, "
                    + "    Pedigree.RazaAnimal, "
                    + "    Pedigree.PropositoAnimal, "
                    + "    Pedigree.RazaPadre, "
                    + "    Pedigree.RazaMadre "
                    + "FROM "
                    + "    Animales "
                    + "INNER JOIN "
                    + "    Pedigree "
                    + "ON "
                    + "    Animales.ID = Pedigree.ID_ANIMAL "
                    + "where "
                    + " animales.codigo LIKE '%GAL%'")) {

                int rowNum = 1;
                while (resultSet2.next()) {
                    Row row = sheet2.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet2.getInt("id"));
                    row.createCell(1).setCellValue(resultSet2.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet2.getString("RazaAnimal"));
                    row.createCell(3).setCellValue(resultSet2.getString("PropositoAnimal"));
                    row.createCell(4).setCellValue(resultSet2.getString("RazaPadre"));
                    row.createCell(5).setCellValue(resultSet2.getString("RazaMadre"));
                }
            } catch (SQLException e) {
                System.err.println("Error en pedigree gallinas: " + e.getMessage());
            }

            //tercer hoja pedigree cerdos
            Sheet sheet3 = workbook.createSheet("Pedigree cerdos");
            // Crear encabezados
            Row headerRow3 = sheet3.createRow(0);
            headerRow3.createCell(0).setCellValue("ID");
            headerRow3.createCell(1).setCellValue("Codigo");
            headerRow3.createCell(2).setCellValue("Raza del animal");
            headerRow3.createCell(3).setCellValue("Proposito del animal");
            headerRow3.createCell(4).setCellValue("Raza del Padre");
            headerRow3.createCell(5).setCellValue("Raza de la Madre");

            // Intentar llenar datos
            try (Statement statement3 = connection.createStatement(); ResultSet resultSet3 = statement3.executeQuery("SELECT "
                    + "    Animales.ID, "
                    + "    Animales.CODIGO, "
                    + "    Pedigree.RazaAnimal, "
                    + "    Pedigree.PropositoAnimal, "
                    + "    Pedigree.RazaPadre, "
                    + "    Pedigree.RazaMadre "
                    + "FROM "
                    + "    Animales "
                    + "INNER JOIN "
                    + "    Pedigree "
                    + "ON "
                    + "    Animales.ID = Pedigree.ID_ANIMAL "
                    + "where "
                    + " animales.codigo LIKE '%CER%'")) {

                int rowNum = 1;
                while (resultSet3.next()) {
                    Row row = sheet3.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet3.getInt("id"));
                    row.createCell(1).setCellValue(resultSet3.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet3.getString("RazaAnimal"));
                    row.createCell(3).setCellValue(resultSet3.getString("PropositoAnimal"));
                    row.createCell(4).setCellValue(resultSet3.getString("RazaPadre"));
                    row.createCell(5).setCellValue(resultSet3.getString("RazaMadre"));
                }
            } catch (SQLException e) {
                System.err.println("Error en pedigree cerdos: " + e.getMessage());
            }

            //cuarta hoja pedigree caballos
            Sheet sheet4 = workbook.createSheet("Pedigree caballos");
            // Crear encabezados
            Row headerRow4 = sheet4.createRow(0);
            headerRow4.createCell(0).setCellValue("ID");
            headerRow4.createCell(1).setCellValue("Codigo");
            headerRow4.createCell(2).setCellValue("Raza del animal");
            headerRow4.createCell(3).setCellValue("Proposito del animal");
            headerRow4.createCell(4).setCellValue("Raza del Padre");
            headerRow4.createCell(5).setCellValue("Raza de la Madre");

            // Intentar llenar datos
            try (Statement statement4 = connection.createStatement(); ResultSet resultSet4 = statement4.executeQuery("SELECT "
                    + "    Animales.ID, "
                    + "    Animales.CODIGO, "
                    + "    Pedigree.RazaAnimal, "
                    + "    Pedigree.PropositoAnimal, "
                    + "    Pedigree.RazaPadre, "
                    + "    Pedigree.RazaMadre "
                    + "FROM "
                    + "    Animales "
                    + "INNER JOIN "
                    + "    Pedigree "
                    + "ON "
                    + "    Animales.ID = Pedigree.ID_ANIMAL "
                    + "where "
                    + " animales.codigo LIKE '%CAB%'")) {

                int rowNum = 1;
                while (resultSet4.next()) {
                    Row row = sheet4.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet4.getInt("id"));
                    row.createCell(1).setCellValue(resultSet4.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet4.getString("RazaAnimal"));
                    row.createCell(3).setCellValue(resultSet4.getString("PropositoAnimal"));
                    row.createCell(4).setCellValue(resultSet4.getString("RazaPadre"));
                    row.createCell(5).setCellValue(resultSet4.getString("RazaMadre"));
                }
            } catch (SQLException e) {
                System.err.println("Error en pedigree caballos: " + e.getMessage());
            }

            //quinta hoja pedigree caprinos
            Sheet sheet5 = workbook.createSheet("Pedigree caprinos");
            // Crear encabezados
            Row headerRow5 = sheet5.createRow(0);
            headerRow5.createCell(0).setCellValue("ID");
            headerRow5.createCell(1).setCellValue("Codigo");
            headerRow5.createCell(2).setCellValue("Raza del animal");
            headerRow5.createCell(3).setCellValue("Proposito del animal");
            headerRow5.createCell(4).setCellValue("Raza del Padre");
            headerRow5.createCell(5).setCellValue("Raza de la Madre");

            // Intentar llenar datos
            try (Statement statement5 = connection.createStatement(); ResultSet resultSet5 = statement5.executeQuery("SELECT "
                    + "    Animales.ID, "
                    + "    Animales.CODIGO, "
                    + "    Pedigree.RazaAnimal, "
                    + "    Pedigree.PropositoAnimal, "
                    + "    Pedigree.RazaPadre, "
                    + "    Pedigree.RazaMadre "
                    + "FROM "
                    + "    Animales "
                    + "INNER JOIN "
                    + "    Pedigree "
                    + "ON "
                    + "    Animales.ID = Pedigree.ID_ANIMAL "
                    + "where "
                    + " animales.codigo LIKE '%CAP%'")) {

                int rowNum = 1;
                while (resultSet5.next()) {
                    Row row = sheet5.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet5.getInt("id"));
                    row.createCell(1).setCellValue(resultSet5.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet5.getString("RazaAnimal"));
                    row.createCell(3).setCellValue(resultSet5.getString("PropositoAnimal"));
                    row.createCell(4).setCellValue(resultSet5.getString("RazaPadre"));
                    row.createCell(5).setCellValue(resultSet5.getString("RazaMadre"));
                }
            } catch (SQLException e) {
                System.err.println("Error en pedigree caprinos: " + e.getMessage());
            }

            // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    AuditoriaGeneracionReporte();
    }

    public void ReporteEngorde() throws SQLException {

        String excelFilePath = Paths.get(desktopPath, "Reporte_Engorde_Animales_Inventails.xlsx").toString();

        try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {

            //primera hoja hoja Engorde Bovinos
            Sheet sheet = workbook.createSheet("Engorde Bovinos");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Codigo");
            headerRow.createCell(2).setCellValue("Peso Inicial");
            headerRow.createCell(3).setCellValue("Peso Final");
            headerRow.createCell(4).setCellValue("Engorde (en kg)");
            headerRow.createCell(5).setCellValue("Porcentaje de engorde (%)");

            // Intentar llenar datos
            try (Statement statement = connection.createStatement(); 
                    ResultSet resultSet = statement.executeQuery("SELECT " +
                                                                    "    c.ID AS id, " +
                                                                    "    a.CODIGO AS codigo, " +
                                                                    "    c.PESO_INICIAL_kg AS peso_inicial, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg, 0) AS peso_final, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) AS engorde, " +
                                                                    "    IF(c.PESO_INICIAL_kg > 0, (IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) / c.PESO_INICIAL_kg) * 100, 0) AS porcentaje_engorde " +
                                                                    " FROM " +
                                                                    "    Compras c " +
                                                                    " LEFT JOIN " +
                                                                    "    Animales a ON c.ID_ANIMAL = a.ID " +
                                                                    " LEFT JOIN " +
                                                                    "    Ventas v ON c.ID_ANIMAL = v.ID_ANIMAL " +
                                                                    " Where " +
                                                                    "    codigo like '%BOV%' " +
                                                                    " ORDER BY " +
                                                                    "    c.ID ASC ")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("id"));
                    row.createCell(1).setCellValue(resultSet.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet.getDouble("peso_inicial"));
                    row.createCell(3).setCellValue(resultSet.getDouble("peso_final"));
                    double engorde = resultSet.getDouble("engorde");
                    if (engorde < 0 ){
                    engorde = 0;}
                    row.createCell(4).setCellValue(engorde);
                    double porcentajee = resultSet.getDouble("porcentaje_engorde");
                    if (porcentajee < 0){
                        porcentajee = 0;
                    }
                    row.createCell(5).setCellValue(porcentajee);
                }
            } catch (SQLException e) {
                System.err.println("Error en engorde Bovinos: " + e.getMessage());
            }
            
            //segunda hoja hoja Engorde gallinas
            Sheet sheet2 = workbook.createSheet("Engorde Gallinas");
            // Crear encabezados
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("ID");
            headerRow2.createCell(1).setCellValue("Codigo");
            headerRow2.createCell(2).setCellValue("Peso Inicial");
            headerRow2.createCell(3).setCellValue("Peso Final");
            headerRow2.createCell(4).setCellValue("Engorde (en kg)");
            headerRow2.createCell(5).setCellValue("Porcentaje de engorde (%)");

            // Intentar llenar datos
            try (Statement statement2 = connection.createStatement(); 
                    ResultSet resultSet2 = statement2.executeQuery("SELECT " +
                                                                    "    c.ID AS id, " +
                                                                    "    a.CODIGO AS codigo, " +
                                                                    "    c.PESO_INICIAL_kg AS peso_inicial, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg, 0) AS peso_final, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) AS engorde, " +
                                                                    "    IF(c.PESO_INICIAL_kg > 0, (IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) / c.PESO_INICIAL_kg) * 100, 0) AS porcentaje_engorde " +
                                                                    " FROM " +
                                                                    "    Compras c " +
                                                                    " LEFT JOIN " +
                                                                    "    Animales a ON c.ID_ANIMAL = a.ID " +
                                                                    " LEFT JOIN " +
                                                                    "    Ventas v ON c.ID_ANIMAL = v.ID_ANIMAL " +
                                                                    " Where " +
                                                                    "    codigo like '%GAL%' " +
                                                                    " ORDER BY " +
                                                                    "    c.ID ASC ")) {

                int rowNum = 1;
                while (resultSet2.next()) {
                    Row row = sheet2.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet2.getInt("id"));
                    row.createCell(1).setCellValue(resultSet2.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet2.getDouble("peso_inicial"));
                    row.createCell(3).setCellValue(resultSet2.getDouble("peso_final"));
                    double engorde = resultSet2.getDouble("engorde");
                    if (engorde < 0 ){
                    engorde = 0;}
                    row.createCell(4).setCellValue(engorde);
                    double porcentajee = resultSet2.getDouble("porcentaje_engorde");
                    if (porcentajee < 0){
                        porcentajee = 0;
                    }
                    row.createCell(5).setCellValue(porcentajee);
                }
            } catch (SQLException e) {
                System.err.println("Error en engorde gallinas: " + e.getMessage());
            }
            
            //tercera hoja hoja Engorde cerdos
            Sheet sheet3 = workbook.createSheet("Engorde Cerdos");
            // Crear encabezados
            Row headerRow3 = sheet3.createRow(0);
            headerRow3.createCell(0).setCellValue("ID");
            headerRow3.createCell(1).setCellValue("Codigo");
            headerRow3.createCell(2).setCellValue("Peso Inicial");
            headerRow3.createCell(3).setCellValue("Peso Final");
            headerRow3.createCell(4).setCellValue("Engorde (en kg)");
            headerRow3.createCell(5).setCellValue("Porcentaje de engorde (%)");

            // Intentar llenar datos
            try (Statement statement3 = connection.createStatement(); 
                    ResultSet resultSet3 = statement3.executeQuery("SELECT " +
                                                                    "    c.ID AS id, " +
                                                                    "    a.CODIGO AS codigo, " +
                                                                    "    c.PESO_INICIAL_kg AS peso_inicial, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg, 0) AS peso_final, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) AS engorde, " +
                                                                    "    IF(c.PESO_INICIAL_kg > 0, (IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) / c.PESO_INICIAL_kg) * 100, 0) AS porcentaje_engorde " +
                                                                    " FROM " +
                                                                    "    Compras c " +
                                                                    " LEFT JOIN " +
                                                                    "    Animales a ON c.ID_ANIMAL = a.ID " +
                                                                    " LEFT JOIN " +
                                                                    "    Ventas v ON c.ID_ANIMAL = v.ID_ANIMAL " +
                                                                    " Where " +
                                                                    "    codigo like '%CER%' " +
                                                                    " ORDER BY " +
                                                                    "    c.ID ASC ")) {

                int rowNum = 1;
                while (resultSet3.next()) {
                    Row row = sheet3.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet3.getInt("id"));
                    row.createCell(1).setCellValue(resultSet3.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet3.getDouble("peso_inicial"));
                    row.createCell(3).setCellValue(resultSet3.getDouble("peso_final"));
                    double engorde = resultSet3.getDouble("engorde");
                    if (engorde < 0 ){
                    engorde = 0;}
                    row.createCell(4).setCellValue(engorde);
                    double porcentajee = resultSet3.getDouble("porcentaje_engorde");
                    if (porcentajee < 0){
                        porcentajee = 0;
                    }
                    row.createCell(5).setCellValue(porcentajee);
                }
            } catch (SQLException e) {
                System.err.println("Error en engorde cerdos: " + e.getMessage());
            }
            
            // cuarta hoja hoja Engorde cerdos
            Sheet sheet4 = workbook.createSheet("Engorde Caballos");
            // Crear encabezados
            Row headerRow4 = sheet4.createRow(0);
            headerRow4.createCell(0).setCellValue("ID");
            headerRow4.createCell(1).setCellValue("Codigo");
            headerRow4.createCell(2).setCellValue("Peso Inicial");
            headerRow4.createCell(3).setCellValue("Peso Final");
            headerRow4.createCell(4).setCellValue("Engorde (en kg)");
            headerRow4.createCell(5).setCellValue("Porcentaje de engorde (%)");

            // Intentar llenar datos
            try (Statement statement4 = connection.createStatement(); 
                    ResultSet resultSet4 = statement4.executeQuery("SELECT " +
                                                                    "    c.ID AS id, " +
                                                                    "    a.CODIGO AS codigo, " +
                                                                    "    c.PESO_INICIAL_kg AS peso_inicial, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg, 0) AS peso_final, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) AS engorde, " +
                                                                    "    IF(c.PESO_INICIAL_kg > 0, (IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) / c.PESO_INICIAL_kg) * 100, 0) AS porcentaje_engorde " +
                                                                    " FROM " +
                                                                    "    Compras c " +
                                                                    " LEFT JOIN " +
                                                                    "    Animales a ON c.ID_ANIMAL = a.ID " +
                                                                    " LEFT JOIN " +
                                                                    "    Ventas v ON c.ID_ANIMAL = v.ID_ANIMAL " +
                                                                    " Where " +
                                                                    "    codigo like '%CAB%' " +
                                                                    " ORDER BY " +
                                                                    "    c.ID ASC ")) {

                int rowNum = 1;
                while (resultSet4.next()) {
                    Row row = sheet4.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet4.getInt("id"));
                    row.createCell(1).setCellValue(resultSet4.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet4.getDouble("peso_inicial"));
                    row.createCell(3).setCellValue(resultSet4.getDouble("peso_final"));
                    double engorde = resultSet4.getDouble("engorde");
                    if (engorde < 0 ){
                    engorde = 0;}
                    row.createCell(4).setCellValue(engorde);
                    double porcentajee = resultSet4.getDouble("porcentaje_engorde");
                    if (porcentajee < 0){
                        porcentajee = 0;
                    }
                    row.createCell(5).setCellValue(porcentajee);
                }
            } catch (SQLException e) {
                System.err.println("Error en engorde caballos: " + e.getMessage());
            }
            
            // quinta hoja hoja Engorde caprinos
            Sheet sheet5 = workbook.createSheet("Engorde Caprinos");
            // Crear encabezados
            Row headerRow5 = sheet5.createRow(0);
            headerRow5.createCell(0).setCellValue("ID");
            headerRow5.createCell(1).setCellValue("Codigo");
            headerRow5.createCell(2).setCellValue("Peso Inicial");
            headerRow5.createCell(3).setCellValue("Peso Final");
            headerRow5.createCell(4).setCellValue("Engorde (en kg)");
            headerRow5.createCell(5).setCellValue("Porcentaje de engorde (%)");

            // Intentar llenar datos
            try (Statement statement5 = connection.createStatement(); 
                    ResultSet resultSet5 = statement5.executeQuery("SELECT " +
                                                                    "    c.ID AS id, " +
                                                                    "    a.CODIGO AS codigo, " +
                                                                    "    c.PESO_INICIAL_kg AS peso_inicial, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg, 0) AS peso_final, " +
                                                                    "    IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) AS engorde, " +
                                                                    "    IF(c.PESO_INICIAL_kg > 0, (IFNULL(v.PESO_FINAL_kg - c.PESO_INICIAL_kg, 0) / c.PESO_INICIAL_kg) * 100, 0) AS porcentaje_engorde " +
                                                                    " FROM " +
                                                                    "    Compras c " +
                                                                    " LEFT JOIN " +
                                                                    "    Animales a ON c.ID_ANIMAL = a.ID " +
                                                                    " LEFT JOIN " +
                                                                    "    Ventas v ON c.ID_ANIMAL = v.ID_ANIMAL " +
                                                                    " Where " +
                                                                    "    codigo like '%CAP%' " +
                                                                    " ORDER BY " +
                                                                    "    c.ID ASC ")) {

                int rowNum = 1;
                while (resultSet5.next()) {
                    Row row = sheet5.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet5.getInt("id"));
                    row.createCell(1).setCellValue(resultSet5.getString("codigo"));
                    row.createCell(2).setCellValue(resultSet5.getDouble("peso_inicial"));
                    row.createCell(3).setCellValue(resultSet5.getDouble("peso_final"));
                    double engorde = resultSet5.getDouble("engorde");
                    if (engorde < 0 ){
                    engorde = 0;}
                    row.createCell(4).setCellValue(engorde);
                    double porcentajee = resultSet5.getDouble("porcentaje_engorde");
                    if (porcentajee < 0){
                        porcentajee = 0;
                    }
                    row.createCell(5).setCellValue(porcentajee);
                }
            } catch (SQLException e) {
                System.err.println("Error en engorde caprinos: " + e.getMessage());
            }


            // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    AuditoriaGeneracionReporte();
    }

    public void MedicinasAplicadas() throws SQLException {

        String excelFilePath = Paths.get(desktopPath, "Reporte_Medicinas_Aplicadas_Inventails.xlsx").toString();

        try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {

            //primera hoja Medicinas bovinos
            Sheet sheet = workbook.createSheet("Medicinas bovinos");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Usuario responsable");
            headerRow.createCell(2).setCellValue("Codigo");
            headerRow.createCell(3).setCellValue("Fecha Aplicacion");
            headerRow.createCell(4).setCellValue("Medicamento Aplicado");
            headerRow.createCell(5).setCellValue("Via Aplicacion");
            headerRow.createCell(6).setCellValue("Concentracion");

            //intenta llenar los datos
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT " +
                                                                                                                    "    ma.ID AS ID, " +
                                                                                                                    "    u.USUARIO AS USUARIO_RESPONSABLE, " +
                                                                                                                    "    a.CODIGO AS CODIGO, " +
                                                                                                                    "    ma.FECHAAPLICACION AS FECHA_APLICACION, " +
                                                                                                                    "    ma.MEDICAMENTOAPLICADO AS MEDICAMENTO_APLICADO, " +
                                                                                                                    "    ma.VIAAPLICACION AS VIA, " +
                                                                                                                    "    ma.CONCENTRACION AS CONCENTRACION " +
                                                                                                                    "FROM " +
                                                                                                                    "    MedicinasAplicadas ma " +
                                                                                                                    "JOIN " +
                                                                                                                    "    Animales a ON ma.ID_ANIMAL = a.ID " +
                                                                                                                    "JOIN " +
                                                                                                                    "    Usuarios u ON ma.ID_USUARIO = u.ID " +
                                                                                                                    "WHERE " +
                                                                                                                    "    a.codigo like '%BOV%' ")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet.getString("USUARIO_RESPONSABLE"));
                    row.createCell(2).setCellValue(resultSet.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet.getString("FECHA_APLICACION"));
                    row.createCell(4).setCellValue(resultSet.getString("MEDICAMENTO_APLICADO"));
                    row.createCell(5).setCellValue(resultSet.getString("VIA"));
                    row.createCell(6).setCellValue(resultSet.getString("CONCENTRACION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en medicinas bovinos: " + e.getMessage());
            }

            //segunda hoja Medicinas gallinas
            Sheet sheet2 = workbook.createSheet("Medicinas Gallinas");
            // Crear encabezados
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("ID");
            headerRow2.createCell(1).setCellValue("Usuario responsable");
            headerRow2.createCell(2).setCellValue("Codigo");
            headerRow2.createCell(3).setCellValue("Fecha Aplicacion");
            headerRow2.createCell(4).setCellValue("Medicamento Aplicado");
            headerRow2.createCell(5).setCellValue("Via Aplicacion");
            headerRow2.createCell(6).setCellValue("Concentracion");

            //intenta llenar los datos
            try (Statement statement2 = connection.createStatement(); 
                    ResultSet resultSet2 = statement2.executeQuery("SELECT " +
                                                                   "    ma.ID AS ID, " +
                                                                   "    u.USUARIO AS USUARIO_RESPONSABLE, " +
                                                                   "    a.CODIGO AS CODIGO, " +
                                                                   "    ma.FECHAAPLICACION AS FECHA_APLICACION, " +
                                                                   "    ma.MEDICAMENTOAPLICADO AS MEDICAMENTO_APLICADO, " +
                                                                   "    ma.VIAAPLICACION AS VIA, " +
                                                                   "    ma.CONCENTRACION AS CONCENTRACION " +
                                                                   " FROM " +
                                                                   "    MedicinasAplicadas ma " +
                                                                   " JOIN " +
                                                                   "    Animales a ON ma.ID_ANIMAL = a.ID " +
                                                                   " JOIN " +
                                                                   "    Usuarios u ON ma.ID_USUARIO = u.ID " +
                                                                   " WHERE " +
                                                                   "    a.codigo like '%GAL%' ")) {

                int rowNum = 1;
                while (resultSet2.next()) {
                    Row row = sheet2.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet2.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet2.getString("USUARIO_RESPONSABLE"));
                    row.createCell(2).setCellValue(resultSet2.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet2.getString("FECHA_APLICACION"));
                    row.createCell(4).setCellValue(resultSet2.getString("MEDICAMENTO_APLICADO"));
                    row.createCell(5).setCellValue(resultSet2.getString("VIA"));
                    row.createCell(6).setCellValue(resultSet2.getString("CONCENTRACION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en medicinas gallinas: " + e.getMessage());
            }

            //tercera  hoja Medicinas cerdos
             Sheet sheet3 = workbook.createSheet("Medicinas Cerdos");
            // Crear encabezados
            Row headerRow3 = sheet3.createRow(0);
            headerRow3.createCell(0).setCellValue("ID");
            headerRow3.createCell(1).setCellValue("Usuario responsable");
            headerRow3.createCell(2).setCellValue("Codigo");
            headerRow3.createCell(3).setCellValue("Fecha Aplicacion");
            headerRow3.createCell(4).setCellValue("Medicamento Aplicado");
            headerRow3.createCell(5).setCellValue("Via Aplicacion");
            headerRow3.createCell(6).setCellValue("Concentracion");

            //intenta llenar los datos
            try (Statement statement3 = connection.createStatement(); 
                    ResultSet resultSet3 = statement3.executeQuery("SELECT " +
                                                                   "    ma.ID AS ID, " +
                                                                   "    u.USUARIO AS USUARIO_RESPONSABLE, " +
                                                                   "    a.CODIGO AS CODIGO, " +
                                                                   "    ma.FECHAAPLICACION AS FECHA_APLICACION, " +
                                                                   "    ma.MEDICAMENTOAPLICADO AS MEDICAMENTO_APLICADO, " +
                                                                   "    ma.VIAAPLICACION AS VIA, " +
                                                                   "    ma.CONCENTRACION AS CONCENTRACION " +
                                                                   " FROM " +
                                                                   "    MedicinasAplicadas ma " +
                                                                   " JOIN " +
                                                                   "    Animales a ON ma.ID_ANIMAL = a.ID " +
                                                                   " JOIN " +
                                                                   "    Usuarios u ON ma.ID_USUARIO = u.ID " +
                                                                   " WHERE " +
                                                                   "    a.codigo like '%CER%' ")) {

                int rowNum = 1;
                while (resultSet3.next()) {
                    Row row = sheet3.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet3.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet3.getString("USUARIO_RESPONSABLE"));
                    row.createCell(2).setCellValue(resultSet3.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet3.getString("FECHA_APLICACION"));
                    row.createCell(4).setCellValue(resultSet3.getString("MEDICAMENTO_APLICADO"));
                    row.createCell(5).setCellValue(resultSet3.getString("VIA"));
                    row.createCell(6).setCellValue(resultSet3.getString("CONCENTRACION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en medicinas Cerdos: " + e.getMessage());
            }

            //cuarta hoja Medicinas caballos
             Sheet sheet4 = workbook.createSheet("Medicinas Caballos");
            // Crear encabezados
            Row headerRow4 = sheet4.createRow(0);
            headerRow4.createCell(0).setCellValue("ID");
            headerRow4.createCell(1).setCellValue("Usuario responsable");
            headerRow4.createCell(2).setCellValue("Codigo");
            headerRow4.createCell(3).setCellValue("Fecha Aplicacion");
            headerRow4.createCell(4).setCellValue("Medicamento Aplicado");
            headerRow4.createCell(5).setCellValue("Via Aplicacion");
            headerRow4.createCell(6).setCellValue("Concentracion");

            //intenta llenar los datos
            try (Statement statement4 = connection.createStatement(); 
                    ResultSet resultSet4 = statement4.executeQuery("SELECT " +
                                                                   "    ma.ID AS ID, " +
                                                                   "    u.USUARIO AS USUARIO_RESPONSABLE, " +
                                                                   "    a.CODIGO AS CODIGO, " +
                                                                   "    ma.FECHAAPLICACION AS FECHA_APLICACION, " +
                                                                   "    ma.MEDICAMENTOAPLICADO AS MEDICAMENTO_APLICADO, " +
                                                                   "    ma.VIAAPLICACION AS VIA, " +
                                                                   "    ma.CONCENTRACION AS CONCENTRACION " +
                                                                   " FROM " +
                                                                   "    MedicinasAplicadas ma " +
                                                                   " JOIN " +
                                                                   "    Animales a ON ma.ID_ANIMAL = a.ID " +
                                                                   " JOIN " +
                                                                   "    Usuarios u ON ma.ID_USUARIO = u.ID " +
                                                                   " WHERE " +
                                                                   "    a.codigo like '%CAB%' ")) {

                int rowNum = 1;
                while (resultSet4.next()) {
                    Row row = sheet4.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet4.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet4.getString("USUARIO_RESPONSABLE"));
                    row.createCell(2).setCellValue(resultSet4.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet4.getString("FECHA_APLICACION"));
                    row.createCell(4).setCellValue(resultSet4.getString("MEDICAMENTO_APLICADO"));
                    row.createCell(5).setCellValue(resultSet4.getString("VIA"));
                    row.createCell(6).setCellValue(resultSet4.getString("CONCENTRACION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en medicinas caballos: " + e.getMessage());
            }

            //quinta hoja Medicinas caprinos
             Sheet sheet5 = workbook.createSheet("Medicinas Caprinos");
            // Crear encabezados
            Row headerRow5 = sheet5.createRow(0);
            headerRow5.createCell(0).setCellValue("ID");
            headerRow5.createCell(1).setCellValue("Usuario responsable");
            headerRow5.createCell(2).setCellValue("Codigo");
            headerRow5.createCell(3).setCellValue("Fecha Aplicacion");
            headerRow5.createCell(4).setCellValue("Medicamento Aplicado");
            headerRow5.createCell(5).setCellValue("Via Aplicacion");
            headerRow5.createCell(6).setCellValue("Concentracion");

            //intenta llenar los datos
            try (Statement statement5 = connection.createStatement(); 
                    ResultSet resultSet5 = statement5.executeQuery("SELECT " +
                                                                   "    ma.ID AS ID, " +
                                                                   "    u.USUARIO AS USUARIO_RESPONSABLE, " +
                                                                   "    a.CODIGO AS CODIGO, " +
                                                                   "    ma.FECHAAPLICACION AS FECHA_APLICACION, " +
                                                                   "    ma.MEDICAMENTOAPLICADO AS MEDICAMENTO_APLICADO, " +
                                                                   "    ma.VIAAPLICACION AS VIA, " +
                                                                   "    ma.CONCENTRACION AS CONCENTRACION " +
                                                                   " FROM " +
                                                                   "    MedicinasAplicadas ma " +
                                                                   " JOIN " +
                                                                   "    Animales a ON ma.ID_ANIMAL = a.ID " +
                                                                   " JOIN " +
                                                                   "    Usuarios u ON ma.ID_USUARIO = u.ID " +
                                                                   " WHERE " +
                                                                   "    a.codigo like '%CAP%' ")) {

                int rowNum = 1;
                while (resultSet5.next()) {
                    Row row = sheet5.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet5.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet5.getString("USUARIO_RESPONSABLE"));
                    row.createCell(2).setCellValue(resultSet5.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet5.getString("FECHA_APLICACION"));
                    row.createCell(4).setCellValue(resultSet5.getString("MEDICAMENTO_APLICADO"));
                    row.createCell(5).setCellValue(resultSet5.getString("VIA"));
                    row.createCell(6).setCellValue(resultSet5.getString("CONCENTRACION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en medicinas caprinos: " + e.getMessage());
            }

            // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    AuditoriaGeneracionReporte();
    }

    public void ReporteDefunciones() throws SQLException {
        String excelFilePath = Paths.get(desktopPath, "Reporte_Defunciones_Inventails.xlsx").toString();

        try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {

            //primera hoja defunciones bovinos
            Sheet sheet = workbook.createSheet("Defunciones Bovinos");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Usuario Responsable");
            headerRow.createCell(2).setCellValue("Codigo");
            headerRow.createCell(3).setCellValue("Razon de defuncion");
            headerRow.createCell(4).setCellValue("Fecha de defuncion");
           

            //intenta llenar los datos
            try (Statement statement = connection.createStatement(); 
                 ResultSet resultSet = statement.executeQuery("SELECT " +
                                                              "    d.ID AS ID, " +
                                                              "    u.USUARIO AS USUARIO, " +
                                                              "    a.CODIGO AS CODIGO, " +
                                                              "    d.R_DEFUNCION AS RAZON_DEFUNCION, " +
                                                              "    d.F_DEFUNCION AS FECHA_DEFUNCION " +
                                                              " FROM " +
                                                              "    Defunciones d " +
                                                              " JOIN " +
                                                              "    Animales a ON d.ID_ANIMAL = a.ID " +
                                                              "JOIN " +
                                                              "    USUARIOS u ON d.ID_USUARIO = u.ID " +
                                                              " WHERE " +
                                                              "    a.codigo LIKE '%BOV%' ")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet.getString("USUARIO"));
                    row.createCell(2).setCellValue(resultSet.getString("CODIGO"));
                    row.createCell(3).setCellValue(resultSet.getString("RAZON_DEFUNCION"));
                    row.createCell(4).setCellValue(resultSet.getString("FECHA_DEFUNCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones bovinos: " + e.getMessage());
            }

            //segunda hoja defunciones gallinas
            Sheet sheet2 = workbook.createSheet("Defunciones Gallinas");
            // Crear encabezados
            Row headerRow2 = sheet2.createRow(0);
            headerRow2.createCell(0).setCellValue("ID");
            headerRow2.createCell(1).setCellValue("Usuario Responsable");
            headerRow2.createCell(2).setCellValue("Codigo");
            headerRow2.createCell(3).setCellValue("Razon de defuncion");
            headerRow2.createCell(4).setCellValue("Fecha de defuncion");
           

            //intenta llenar los datos
            try (Statement statement2 = connection.createStatement(); 
                 ResultSet resultSet2 = statement2.executeQuery("SELECT " +
                                                               "    d.ID AS ID, " +
                                                               "    u.USUARIO AS USUARIO, " +
                                                               "    a.CODIGO AS CODIGO, " +
                                                               "    d.R_DEFUNCION AS RAZON_DEFUNCION, " +
                                                               "    d.F_DEFUNCION AS FECHA_DEFUNCION " +
                                                               " FROM " +
                                                               "    Defunciones d " +
                                                               " JOIN " +
                                                               "    Animales a ON d.ID_ANIMAL = a.ID " +
                                                               " JOIN " +
                                                               "    USUARIOS u ON d.ID_USUARIO = u.ID " +
                                                               " WHERE " +
                                                               "    a.codigo LIKE '%GAL%' ")) {

                int rowNum = 1;
                while (resultSet2.next()) {
                    Row row2 = sheet2.createRow(rowNum++);
                    row2.createCell(0).setCellValue(resultSet2.getInt("ID"));
                    row2.createCell(1).setCellValue(resultSet2.getString("USUARIO"));
                    row2.createCell(2).setCellValue(resultSet2.getString("CODIGO"));
                    row2.createCell(3).setCellValue(resultSet2.getString("RAZON_DEFUNCION"));
                    row2.createCell(4).setCellValue(resultSet2.getString("FECHA_DEFUNCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones gallinas: " + e.getMessage());
            }

            //tercera hoja defunciones cerdos
            Sheet sheet3 = workbook.createSheet("Defunciones Cerdos");
            // Crear encabezados
            Row headerRow3 = sheet3.createRow(0);
            headerRow3.createCell(0).setCellValue("ID");
            headerRow3.createCell(1).setCellValue("Usuario Responsable");
            headerRow3.createCell(2).setCellValue("Codigo");
            headerRow3.createCell(3).setCellValue("Razon de defuncion");
            headerRow3.createCell(4).setCellValue("Fecha de defuncion");
           

            //intenta llenar los datos
            try (Statement statement3 = connection.createStatement(); 
                 ResultSet resultSet3 = statement3.executeQuery("SELECT " +
                                                                "    d.ID AS ID, " +
                                                                "    u.USUARIO AS USUARIO, " +
                                                                "    a.CODIGO AS CODIGO, " +
                                                                "    d.R_DEFUNCION AS RAZON_DEFUNCION, " +
                                                                "    d.F_DEFUNCION AS FECHA_DEFUNCION " +
                                                                " FROM " +
                                                                "    Defunciones d " +
                                                                " JOIN " +
                                                                "    Animales a ON d.ID_ANIMAL = a.ID " +
                                                                " JOIN " +
                                                                "    USUARIOS u ON d.ID_USUARIO = u.ID " +
                                                                " WHERE " +
                                                                "    a.codigo LIKE '%CER%' ")) {

                int rowNum = 1;
                while (resultSet3.next()) {
                    Row row3 = sheet3.createRow(rowNum++);
                    row3.createCell(0).setCellValue(resultSet3.getInt("ID"));
                    row3.createCell(1).setCellValue(resultSet3.getString("USUARIO"));
                    row3.createCell(2).setCellValue(resultSet3.getString("CODIGO"));
                    row3.createCell(3).setCellValue(resultSet3.getString("RAZON_DEFUNCION"));
                    row3.createCell(4).setCellValue(resultSet3.getString("FECHA_DEFUNCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones cerdos: " + e.getMessage());
            }

            //CUARTA hoja defunciones caballos
            Sheet sheet4 = workbook.createSheet("Defunciones Caballos");
            // Crear encabezados
            Row headerRow4 = sheet4.createRow(0);
            headerRow4.createCell(0).setCellValue("ID");
            headerRow4.createCell(1).setCellValue("Usuario Responsable");
            headerRow4.createCell(2).setCellValue("Codigo");
            headerRow4.createCell(3).setCellValue("Razon de defuncion");
            headerRow4.createCell(4).setCellValue("Fecha de defuncion");
           

            //intenta llenar los datos
            try (Statement statement4 = connection.createStatement(); 
                 ResultSet resultSet4 = statement4.executeQuery("SELECT " +
                                                                "    d.ID AS ID, " +
                                                                "    u.USUARIO AS USUARIO, " +
                                                                "    a.CODIGO AS CODIGO, " +
                                                                "    d.R_DEFUNCION AS RAZON_DEFUNCION, " +
                                                                "    d.F_DEFUNCION AS FECHA_DEFUNCION " +
                                                                " FROM " +
                                                                "    Defunciones d " +
                                                                " JOIN " +
                                                                "    Animales a ON d.ID_ANIMAL = a.ID " +
                                                                " JOIN " +
                                                                "    USUARIOS u ON d.ID_USUARIO = u.ID " +
                                                                " WHERE " +
                                                                "    a.codigo LIKE '%CAB%' ")) {

                int rowNum = 1;
                while (resultSet4.next()) {
                    Row row4 = sheet4.createRow(rowNum++);
                    row4.createCell(0).setCellValue(resultSet4.getInt("ID"));
                    row4.createCell(1).setCellValue(resultSet4.getString("USUARIO"));
                    row4.createCell(2).setCellValue(resultSet4.getString("CODIGO"));
                    row4.createCell(3).setCellValue(resultSet4.getString("RAZON_DEFUNCION"));
                    row4.createCell(4).setCellValue(resultSet4.getString("FECHA_DEFUNCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones caballos: " + e.getMessage());
            }

            //quinta hoja defunciones caprinos
            Sheet sheet5 = workbook.createSheet("Defunciones Caprinos");
            // Crear encabezados
            Row headerRow5 = sheet5.createRow(0);
            headerRow5.createCell(0).setCellValue("ID");
            headerRow5.createCell(1).setCellValue("Usuario Responsable");
            headerRow5.createCell(2).setCellValue("Codigo");
            headerRow5.createCell(3).setCellValue("Razon de defuncion");
            headerRow5.createCell(4).setCellValue("Fecha de defuncion");
           

            //intenta llenar los datos
            try (Statement statement5 = connection.createStatement(); 
                 ResultSet resultSet5 = statement5.executeQuery("SELECT " +
                                                                "    d.ID AS ID, " +
                                                                "    u.USUARIO AS USUARIO, " +
                                                                "    a.CODIGO AS CODIGO, " +
                                                                "    d.R_DEFUNCION AS RAZON_DEFUNCION, " +
                                                                "    d.F_DEFUNCION AS FECHA_DEFUNCION " +
                                                                " FROM " +
                                                                "    Defunciones d " +
                                                                " JOIN " +
                                                                "    Animales a ON d.ID_ANIMAL = a.ID " +
                                                                " JOIN " +
                                                                "    USUARIOS u ON d.ID_USUARIO = u.ID " +
                                                                " WHERE " +
                                                                "    a.codigo LIKE '%CAP%' ")) {

                int rowNum = 1;
                while (resultSet5.next()) {
                    Row row5 = sheet5.createRow(rowNum++);
                    row5.createCell(0).setCellValue(resultSet5.getInt("ID"));
                    row5.createCell(1).setCellValue(resultSet5.getString("USUARIO"));
                    row5.createCell(2).setCellValue(resultSet5.getString("CODIGO"));
                    row5.createCell(3).setCellValue(resultSet5.getString("RAZON_DEFUNCION"));
                    row5.createCell(4).setCellValue(resultSet5.getString("FECHA_DEFUNCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones caballos: " + e.getMessage());
            }

            // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    AuditoriaGeneracionReporte();
    }
    
    public void ReporteTransaccionesTotales () throws SQLException{
    String excelFilePath = Paths.get(desktopPath, "Reporte_Transacciones_Totales_Inventails.xlsx").toString();
    try (Connection connection = DriverManager.getConnection(url + db, user, password); Workbook workbook = new XSSFWorkbook()) {
        
        //quinta hoja defunciones caprinos
            Sheet sheet = workbook.createSheet("Transacciones Totales");
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Tipo Transaccion");
            headerRow.createCell(2).setCellValue("Usuario");
            headerRow.createCell(4).setCellValue("Fecha de Transacci0n");
            headerRow.createCell(3).setCellValue("Valor Transaccion");

            //intenta llenar los datos
            try (Statement statement = connection.createStatement(); 
                 ResultSet resultSet = statement.executeQuery("SELECT " +
                                                              "    T.ID, " +
                                                              "    T.TIPO_TRANSACCION, " +
                                                              "    U.USUARIO AS USUARIO, " +
                                                              "    CASE " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'COMPRA' THEN C.F_COMPRA " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'VENTA' THEN V.F_VENTA " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'GASTO_MANTENIMIENTO' THEN GM.FechaGasto " +
                                                              "    END AS FECHA, " +
                                                              "    CASE " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'COMPRA' THEN C.V_COMPRA " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'VENTA' THEN V.V_VENTA " +
                                                              "        WHEN T.TIPO_TRANSACCION = 'GASTO_MANTENIMIENTO' THEN GM.ValorGasto " +
                                                              "    END AS VALOR_TRANSACCION " +
                                                              "FROM " +
                                                              "    Transacciones T " +
                                                              "LEFT JOIN " +
                                                              "    Compras C ON T.ID_COMPRA = C.ID AND T.TIPO_TRANSACCION = 'COMPRA' " +
                                                              "LEFT JOIN " +
                                                              "    Ventas V ON T.ID_VENTA = V.ID AND T.TIPO_TRANSACCION = 'VENTA' " +
                                                              "LEFT JOIN " +
                                                              "    GastosMantenimiento GM ON T.ID_GastoMantenimiento = GM.ID AND T.TIPO_TRANSACCION = 'GASTO_MANTENIMIENTO' " +
                                                              "LEFT JOIN " +
                                                              "    Usuarios U ON COALESCE(C.ID_USUARIO, V.ID_USUARIO, GM.ID_USUARIO) = U.ID ")) {

                int rowNum = 1;
                while (resultSet.next()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getInt("ID"));
                    row.createCell(1).setCellValue(resultSet.getString("TIPO_TRANSACCION"));
                    row.createCell(2).setCellValue(resultSet.getString("USUARIO"));
                    row.createCell(3).setCellValue(resultSet.getString("FECHA"));
                    row.createCell(4).setCellValue(resultSet.getFloat("VALOR_TRANSACCION"));
                }
            } catch (SQLException e) {
                System.err.println("Error en defunciones caballos: " + e.getMessage());
            }
        
         // Escribir el archivo (siempre se ejecutará)
            try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null,
                        "Archivo Excel generado en:\n" + excelFilePath,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
      
    }catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error general: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    AuditoriaGeneracionReporte();
    }

}
