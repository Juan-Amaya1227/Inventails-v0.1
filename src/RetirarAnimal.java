import javax.swing.*;
import java.sql.ResultSet ;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Stack;

public class RetirarAnimal {
    
    // Pilas para manejar la  venta
    private  final Stack<Object[]> pilaVentaBovinos ;
    private  final Stack<Object[]> pilaVentaGallinas ;
    private  final Stack<Object[]> pilaVentaCerdos ;
    private  final Stack<Object[]> pilaVentaCaballos ;
    private  final Stack<Object[]> pilaVentaCaprinos ;

    //registros
    public final Map<String, Ventana> registrosBovinos;
    public final Map<String, Ventana> registrosGallinas;
    public final Map<String, Ventana> registrosCerdos;
    public final Map<String, Ventana> registrosCaballos;
    public final Map<String, Ventana> registrosCaprinos;
    
    public  String db = "inventails";
    public  String url = "jdbc:mysql://localhost:3306/";
    public  String user = "root";
    public  String password = "";
    public  String tipa;
    public final Connection cx;
    private final int IDUsuario;
    
    public RetirarAnimal(int IDUsuario,Stack<Object[]> pilaVentaCaprinos,Stack<Object[]> pilaVentaBovinos,Stack<Object[]> pilaVentaGallinas,
                         Stack<Object[]> pilaVentaCerdos, Stack<Object[]> pilaVentaCaballos,
                         Map<String, Ventana> registrosCaprinos,Map<String, Ventana> registrosBovinos, Map<String, Ventana> registrosGallinas, 
                         Map<String, Ventana> registrosCerdos,  Map<String, Ventana> registrosCaballos, String tipa, Connection cx,
                         String user, String db, String url, String password){
        
        this.IDUsuario = IDUsuario;
        this.registrosBovinos = registrosBovinos;
        this.registrosCaprinos = registrosCaprinos;
        this.pilaVentaCaprinos = pilaVentaCaprinos;
        this.registrosGallinas = registrosGallinas;
        this.registrosCerdos = registrosCerdos;
        this.registrosCaballos = registrosCaballos;
        this.pilaVentaBovinos= pilaVentaBovinos;
        this.pilaVentaCaballos = pilaVentaCaballos;
        this.pilaVentaCerdos = pilaVentaCerdos;
        this.pilaVentaGallinas =  pilaVentaGallinas;
        this.user = user;
        this.db = db;
        this.url = url;
        this.password = password;
        this.tipa = tipa;
        this.cx = cx;
    }
    
    public int BuscarElementoV(String codigo) {
         // Consulta SQL para buscar el ID del animal con el código dado y estado "activo"
     String buscarQuery = "SELECT ID FROM Animales WHERE CODIGO = ? AND ESTADO = 'activo'";

     try (Connection conn = DriverManager.getConnection(url + db, user, password);
          PreparedStatement buscarStmt = conn.prepareStatement(buscarQuery)) {

         buscarStmt.setString(1, codigo);
         ResultSet rs = buscarStmt.executeQuery();

         if (rs.next()) {
             int idAnimal = rs.getInt("ID"); // Obtener el ID del animal encontrado
             //System.out.println("ID del animal encontrado: " + idAnimal);
             JOptionPane.showMessageDialog(null,"ID del animal encontrado:  " + idAnimal,"ID hallado",JOptionPane.INFORMATION_MESSAGE);
             return idAnimal; // Retornar el ID
         } else {
            // System.out.println("No se encontró ningún animal con el código " + codigo + " y estado 'activo'.");
            JOptionPane.showMessageDialog(null,"No se encontró ningún animal con el código " + codigo + " y estado 'activo'","error: err003", JOptionPane.ERROR_MESSAGE);
         }

     } catch (SQLException e) {
         //System.err.println("Error al acceder a la base de datos: " + e.getMessage());
         JOptionPane.showMessageDialog(null,"Error al acceder a la base de datos: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);

     }
         return 0;
     }
    
    public void DefinirInactivo(String codigo){
    // Consulta SQL para actualizar el estado del animal
     String updateQuery = "UPDATE Animales SET ESTADO = 'inactivo' WHERE CODIGO = ? AND estado = 'activo'";
     try (Connection conn = DriverManager.getConnection(url + db, user, password);
          PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {  
         updateStmt.setString(1, codigo); // Asignar el código al parámetro de la consulta
         // Ejecutar la actualización
         int filasActualizadas = updateStmt.executeUpdate();
         if (filasActualizadas > 0) {
             JOptionPane.showMessageDialog(null,"El estado del animal con código " + codigo + " ha sido cambiado a inactivo"); } 
         else {JOptionPane.showMessageDialog(null,"No se encontró un animal con el código " + codigo + " y estado 'activo'","error: err003", JOptionPane.ERROR_MESSAGE); }
     }  catch (SQLException e) { JOptionPane.showMessageDialog(null,"Error al actualizar el estado del animal: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);}}
    
    public void DecidirMotivoRetirarA() throws SQLException{
    // Opciones para el JComboBox
        String[] options = {"Venta", "Defuncion"};
        
        // Crear el JComboBox
        JComboBox<String> comboBox = new JComboBox<>(options);

        // Crear el mensaje personalizado
        JPanel panel = new JPanel();
        panel.add(new JLabel("Selecciona el motivo del retiro:"));
        panel.add(comboBox);

        // Mostrar el JOptionPane con el JComboBox incluido
        int result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Selecciona una opción",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        // Obtener la opción seleccionada
        if (result == JOptionPane.OK_OPTION) {
            String OpcionElegida = (String) comboBox.getSelectedItem();
            JOptionPane.showMessageDialog(null, "Seleccionaste: " + OpcionElegida);
            if (null==OpcionElegida){
                System.out.println("Es necesario escojer una opcion antes de continuar");
            }
            else switch (OpcionElegida) {
                case "Venta" -> VenderA();
                case "Defuncion" -> DefuncionA();
                default -> System.out.println("Es necesario escojer una opcion antes de continuar");//joda
            }
        } else {
            JOptionPane.showMessageDialog(null, "No seleccionaste ninguna opción.");
        }
    }   
    
    public void DefuncionA() throws SQLException{
        
        if (tipa == null || tipa.isEmpty()) {
        JOptionPane.showMessageDialog(null,"Error: Debe seleccionar un tipo de animal antes de continuar","error", JOptionPane.ERROR_MESSAGE);
        return;}
        
        LocalDateTime FechaDefuncion = LocalDateTime.now();
        
        String codigomin = JOptionPane.showInputDialog("Ingrese el codigo del animal: ");
        if (codigomin == null||codigomin.isEmpty()){JOptionPane.showMessageDialog(null, "Registro de defunciones cancelado");
        return;}
        String codigo = codigomin.toUpperCase();
        
        BuscarElementoV(codigo);
        
          
    int idAnimal = BuscarElementoV(codigo); // Llamar al método y obtener el ID del animal
    if (idAnimal == 0) { // Si el ID es 0, el animal no se encontró
        JOptionPane.showMessageDialog(null,"El animal solicitado no se encuentra en la granja","error", JOptionPane.ERROR_MESSAGE);
        return;
    }
        
        String RazonDefuncion = JOptionPane.showInputDialog(null,"Ingrese la razon de defuncion del anima: "+codigo);
         
            String sql = "INSERT INTO defunciones (ID_usuario, Id_animal, f_defuncion, r_defuncion) " +
             "VALUES (?, (SELECT ID FROM Animales WHERE codigo = ? AND estado = 'activo'), ?, ?)";
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setInt(1, IDUsuario);
            ps.setString(2, codigo);
            ps.setString(3, FechaDefuncion.toString()); // Convertir FechaDefuncion a cadena
            ps.setString(4, RazonDefuncion);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(null,"Defuncion registrada con exito");
            }
        
            System.out.println(tipa + " registrado con éxito. Codigo: " + codigo);
            System.out.println("Fecha y hora del registro de defuncion : " + FechaDefuncion);  
            JOptionPane.showMessageDialog(null,"Lamentamos mucho la muerte del animal");
            DefinirInactivo(codigo);
    }
    
    public void VenderA() throws SQLException{
        if (tipa == null || tipa.isEmpty()) {
         JOptionPane.showMessageDialog(null,"Error: Debe seleccionar un tipo de animal antes de continuar","error", JOptionPane.ERROR_MESSAGE);
        return; // Salir del método si tipa es null o vacío
    }

        Map<String, Ventana> registros;
        LocalDateTime FechaVenta = LocalDateTime.now();

        switch (tipa) {
            case "Bovinos" -> registros = registrosBovinos; 
            case "Gallinas" -> registros = registrosGallinas;
            case "Cerdos" -> registros = registrosCerdos;
            case "Caballos" -> registros = registrosCaballos;
            case "Caprinos" -> registros = registrosCaprinos;
            default -> {
                JOptionPane.showMessageDialog(null,"Tipo de Animal no valido","error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        
        String codigomin = JOptionPane.showInputDialog("Ingrese el codigo del animal: ");
        if (codigomin == null||codigomin.isEmpty()){JOptionPane.showMessageDialog(null, "Registro de venta cancelado exitosamente");
        return;}
        String codigo = codigomin.toUpperCase();
        
        int idAnimal = BuscarElementoV(codigo);
        if (idAnimal == 0) { // Si el ID es 0, el animal no se encontró
        JOptionPane.showMessageDialog(null,"El animal solicitado no se encuentra en la granja.","error", JOptionPane.ERROR_MESSAGE);
        System.out.println("");
        return;
    }
        
        double costov = 0; // Inicializar la variable para almacenar el costo
        boolean entradaValida = false; // Bandera para controlar el bucle

               do {
                  try {
                      String dcv = JOptionPane.showInputDialog(null,"Ingrese el costo de venta para el animal con código: "+codigo);
                      costov = Double.parseDouble(dcv); //convierte dcv a un double
                      entradaValida = true; // Si tiene éxito, marcamos como válida la entrada
                      } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,"Error: debe ingresar un número válido para el costo de venta","error", JOptionPane.ERROR_MESSAGE);
                      } 
               } while (!entradaValida); // Repetir hasta que se ingrese un valor válido
  
         // Consulta de saldo
            String sqlSaldo = "SELECT saldo FROM saldo WHERE id = 1";
            int saldo = 0;  // Inicializar saldo
            try (PreparedStatement ps = cx.prepareStatement(sqlSaldo);
                 ResultSet rs = ps.executeQuery()) {
        
                if (rs.next()) {
                    saldo = rs.getInt("saldo");
                    JOptionPane.showMessageDialog(null,"Saldo consultado: " + saldo);
                } else {
                    JOptionPane.showMessageDialog(null,"No se encontro el saldo","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Error al preparar o ejecutar la consulta: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
            
            // Actualizar el saldo en la base de datos, permitiendo que quede negativo
            saldo += costov;  // Restar el costo de compra del saldo
            String sqlActualizarSaldo = "UPDATE saldo SET saldo = ? WHERE id = 1"; 
            try {
                PreparedStatement ps = cx.prepareStatement(sqlActualizarSaldo);
                ps.setDouble(1, saldo);  // Pasar el nuevo saldo directamente
                int filasActualizadas = ps.executeUpdate();
        
                if (filasActualizadas > 0) {
                    JOptionPane.showMessageDialog(null,"Saldo actualizado correctamente");
                } else {
                    JOptionPane.showMessageDialog(null,"No se pudo actualizar el saldo.","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Error al actualizar el saldo: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
            
            double pesoFinal = 0.0;
            boolean valido = false;

           do {
                  try {
                      String dpf = JOptionPane.showInputDialog(null,"Ingrese el peso final para el animal con código: "+codigo);
                      pesoFinal = Double.parseDouble(dpf); //convierte dpf a un double
                      valido = true; // Si tiene éxito, marcamos como válida la entrada
                      } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null,"Error: debe ingresar un número válido para el peso final","error", JOptionPane.ERROR_MESSAGE);
                      } 
               } while (!valido); // Repetir hasta que se ingrese un valor válido
  

        System.out.println("Peso final capturado: " + pesoFinal);



        
        System.out.println("");
       

        // Eliminar el animal de los registros
        registros.remove(codigo);

        // Añadir el código a la pila de venta
        switch (tipa) {
            case "Bovinos"  -> pilaVentaBovinos.push(new Object[]{codigo,costov,FechaVenta});
            case "Gallinas" -> pilaVentaGallinas.push(new Object[]{codigo,costov,FechaVenta});
            case "Cerdos"   -> pilaVentaCerdos.push(new Object[]{codigo,costov,FechaVenta});
            case "Caballos" -> pilaVentaCaballos.push(new Object[]{codigo,costov,FechaVenta});
            case "Caprinos" -> pilaVentaCaprinos.push(new Object[]{codigo,costov,FechaVenta});
        }
        
       
    // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO ventas" + "(ID_usuario, ID_animal, f_venta, v_venta, Peso_final_kg)"+
                         "select animales.ID, ?,?,?,? "+
                         "from animales where animales.codigo = ? and animales.estado = 'activo'";
    
    try (PreparedStatement ps = cx.prepareStatement(sql)) {
        ps.setInt(1, IDUsuario);
        ps.setObject(2, FechaVenta);  
        ps.setDouble(3, costov);
        ps.setDouble(4, pesoFinal);
        ps.setString(5, codigo);

        int filasActualizadas = ps.executeUpdate();
        if (filasActualizadas > 0) {
            //System.out.println("Venta registrada con exito en la base de datos.");
            JOptionPane.showMessageDialog(null,"Venta registrada con exito en la base de datos");
        } else {
            //System.out.println("Error: No se pudo registrar la venta en la base de datos.");
            JOptionPane.showMessageDialog(null,"Error: No se pudo registrar la venta en la base de datos","error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        //System.out.println("Error al actualizar la venta: " + e.getMessage());
        JOptionPane.showMessageDialog(null,"Error al actualizar la venta: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
    }
        System.out.println("El " + tipa + " con codigo " + codigo + " ha sido vendido con exito.");
        System.out.println("Fecha y hora de venta: " + FechaVenta);
        DefinirInactivo(codigo);
        InsertarTransaccion(FechaVenta, costov);
    }
    
    public void InsertarTransaccion(LocalDateTime FechaVenta, double costov) throws SQLException {
    int idVenta = 0;
    String query = "SELECT id FROM ventas WHERE f_venta = ? AND v_venta = ?";

    // Consulta el ID de la compra
    try (PreparedStatement stmt = cx.prepareStatement(query)) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedFechaVenta = FechaVenta.format(formatter);
        stmt.setString(1, formattedFechaVenta);
        stmt.setFloat(2, Math.round(costov * 100.0) / 100.0f);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            idVenta = rs.getInt("id");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el ID de venta. Verifica los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir si no se encuentra el ID
        }
    }

    // Inserta en transacciones
    String tipot = "VENTA";
    String sql = "INSERT INTO transacciones (TIPO_TRANSACCION, id_venta) VALUES (?, ?)";

    try (PreparedStatement ps = cx.prepareStatement(sql)) {
        ps.setString(1, tipot);
        ps.setInt(2, idVenta);

        int filasInsertadas = ps.executeUpdate();
        if (filasInsertadas > 0) {            
        }
        else{System.out.println("no se inserto venta");}
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al registrar la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}


