import java.sql.Connection;
import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet ;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class ComprarAnimal {
    private final Connection cx;
    public String tipa ;
    public String sitio;
    public String RangoEdad;
    
    private static final int TOTAL_Bovinos = 100;
    private static final int TOTAL_Gallinas = 100;
    private static final int TOTAL_Cerdos = 100;
    private static final int TOTAL_Caballos = 100;
    private static final int TOTAL_Caprinos = 100;
    
    //registros
    private final Map<String, Ventana> registrosBovinos;
    private final Map<String, Ventana> registrosGallinas;
    private final Map<String, Ventana> registrosCerdos;
    private final Map<String, Ventana> registrosCaballos;
    private final Map<String, Ventana> registrosCaprinos;

// Variables de instancia (usarán las referencias de Ventana)
    private final Queue<Object[]> colaCompraBovinos;
    private final Queue<Object[]> colaCompraGallinas;
    private final Queue<Object[]> colaCompraCerdos;
    private final Queue<Object[]> colaCompraCaballos;
    private final Queue<Object[]> colaCompraCaprinos;
    
    private int IDUsuario;

    public ComprarAnimal(int IDUsuario,Queue<Object[]> colaCompraCaprinos,Queue<Object[]> colaCompraBovinos, Queue<Object[]> colaCompraGallinas ,
                         Queue<Object[]> colaCompraCerdos,  Queue<Object[]> colaCompraCaballos,
                         Map<String, Ventana> registrosCaprinos,Map<String, Ventana> registrosBovinos, Map<String, Ventana> registrosGallinas, 
                         Map<String, Ventana> registrosCerdos,  Map<String, Ventana> registrosCaballos, 
                     String url, String db, String user, String password, 
                     String sitio, String tipa, Connection cx) {
        
        this.tipa = tipa;
        this.cx = cx;
        this.sitio = sitio;
        this.IDUsuario = IDUsuario;
        this.registrosBovinos = registrosBovinos; 
        this.colaCompraCaprinos = colaCompraCaprinos;
        this.colaCompraBovinos = colaCompraBovinos; // Usa la referencia de Ventana
        this.colaCompraGallinas = colaCompraGallinas;
        this.colaCompraCerdos = colaCompraCerdos;
        this.colaCompraCaballos = colaCompraCaballos;
        this.registrosCaprinos = registrosCaprinos;
        this.registrosGallinas = registrosGallinas;
        this.registrosCerdos = registrosCerdos;
        this.registrosCaballos = registrosCaballos;

    }
    
    public static String generarCodigoUnico(String prefijo, Map<String, Ventana> registros) {
    Random random = new Random();
    String codigo;
    do {
        int numeroAleatorio = random.nextInt(1000);  // Número aleatorio entre 0 y 999
        String numeroFormateado = String.format("%03d", numeroAleatorio);  // Formatea a 3 dígitos
        codigo = prefijo + numeroFormateado;
    } while (registros.containsKey(codigo));  // Verifica si el código ya existe en el mapa específico
    return codigo;}
     
    public  void ComprarA() throws SQLException {
       // System.out.println(tipa);
        if (tipa == null || tipa.isEmpty()) {
        JOptionPane.showMessageDialog(null,"Error: Debe seleccionar un tipo de animal antes de continuar","error", JOptionPane.ERROR_MESSAGE);
        return; // Salir del método si tipa es null o vacío
        }
            Map<String, Ventana> registros;
            LocalDateTime FechaCompra = LocalDateTime.now();
            int totalEspacios;
            String prefijo;
        
            switch (tipa) {
                case "Bovinos" -> {
                    registros = registrosBovinos;
                    totalEspacios = TOTAL_Bovinos;
                    prefijo = "BOV";
                }
                case "Gallinas" -> {
                    registros = registrosGallinas;
                    totalEspacios = TOTAL_Gallinas;
                    prefijo = "GAL";
                }
                case "Cerdos" -> {
                    registros = registrosCerdos;
                    totalEspacios = TOTAL_Cerdos;
                    prefijo = "CER";
                }
                case "Caballos" -> {
                    registros = registrosCaballos;
                    totalEspacios = TOTAL_Caballos;
                    prefijo = "CAB";
                }
                case "Caprinos" -> {
                    registros = registrosCaprinos;
                    totalEspacios = TOTAL_Caprinos;
                    prefijo = "CAP";
                }
                default -> {
                    System.out.println("Tipo de animal no válido.");
                    return;
                }
            }
            
            if (registros.size() == totalEspacios) {
                System.out.println("No hay espacios disponibles para " + tipa);
                return;
            }
        
            // Generar el código automáticamente
            String codigo = generarCodigoUnico(prefijo, registros);
            // 1. Obtener datos del pedigree (arreglo)
            String[] datos = PedigreeAnimales(codigo);
            editarmatriz(codigo, datos[0]);
             // 3. Registrar el pedigree (datos en índices 1, 2, 3, 4)
            CRUDPedigree(datos[1], datos[2], datos[3], datos[4], codigo);

            
            double pesoInicial = 0.0;
            boolean valido = false;
            while (!valido) {
                try {
                    String pi = JOptionPane.showInputDialog("Ingrese el peso del animal en kg: ");
                    // Verificar si el usuario cerró el cuadro de diálogo o presionó "Cancelar"
                        if (pi == null || pi.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Debe ingresar un valor. Intente nuevamente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                            continue; // Volver a pedir el dato
                        
                        }
                    pesoInicial = Double.parseDouble(pi); // Convertir a número
                    // Validar que el número no exceda los límites del DECIMAL(10,2)
                    if (pesoInicial >= 0 && pesoInicial <= 99999999.99) {
                        valido = true; // Salir del bucle si es válido
                    } else {
                        //System.out.println("El valor no es valido por favor intente de nuevo.");
                        JOptionPane.showMessageDialog(null,"El valor no es valido por favor intente de nuevo","error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    //System.out.println("Entrada no válida. Por favor, ingrese un número con formato correcto.");
                    JOptionPane.showMessageDialog(null,"Entrada no válida. Por favor, ingrese un número con formato correcto","error", JOptionPane.ERROR_MESSAGE);
                }
            }

            System.out.println("Peso inicial capturado: " + pesoInicial);

        
            // Asegurar entrada numérica válida para el costo de compra
            float Costoc = 0;
            boolean entradaValida = false;  // Bandera para controlar la validez de la entrada
        
            do {
                try {
                    //System.out.print("Ingrese el costo de compra del animal " + codigo + " : ");
                    String fcc = JOptionPane.showInputDialog(null,"Ingrese el costo de compra del animal " + codigo + " : ");
                    if(fcc == null || fcc.isEmpty()){
                    JOptionPane.showMessageDialog(null,"Compra cancelada con exito");
                    return;}
                    Costoc = Float.parseFloat(fcc);  // Intentar leer la entrada como un entero
                    entradaValida = true;  // Si no ocurre excepción, la entrada es válida
                } catch (NumberFormatException e) {             
                    // Mensaje en caso de excepción
                    JOptionPane.showMessageDialog(null,"Error: Debe ingresar un numero válido para el costo de compra","error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!entradaValida);  // Repetir hasta que la entrada sea válida
      
            // Consulta de saldo
            String sqlSaldo = "SELECT saldo FROM saldo WHERE id = 1";
            int saldo = 0;  // Inicializar saldo
            try (PreparedStatement ps = cx.prepareStatement(sqlSaldo);
                 ResultSet rs = ps.executeQuery()) {
        
                if (rs.next()) {
                    saldo = rs.getInt("saldo");
                    //System.out.println("Saldo consultado: " + saldo);
                    JOptionPane.showMessageDialog(null, "Saldo consultado: "+saldo);
                } else {
                    //System.out.println("No se encontro el saldo.");
                    JOptionPane.showMessageDialog(null,"No se encontro el saldo","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                //System.out.println("Error al preparar o ejecutar la consulta: " + e.getMessage());
                JOptionPane.showMessageDialog(null,"Error al preparar o ejecutar la consulta: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
        
            // Actualizar el saldo en la base de datos, permitiendo que quede negativo
            saldo -= Costoc;  // Restar el costo de compra del saldo
            String sqlActualizarSaldo = "UPDATE saldo SET saldo = ? WHERE id = 1"; 
            try {
                PreparedStatement ps = cx.prepareStatement(sqlActualizarSaldo);
                ps.setDouble(1, saldo);  // Pasar el nuevo saldo directamente
                int filasActualizadas = ps.executeUpdate();
        
                if (filasActualizadas > 0) {
             System.out.println("Saldo actualizado correctamente.");
                 JOptionPane.showMessageDialog(null,"Saldo actualizado correctamente");
                } else {
                    //System.out.println("No se pudo actualizar el saldo.");
                    JOptionPane.showMessageDialog(null,"No se pudo actualizar el saldo","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                //System.out.println("Error al actualizar el saldo: " + e.getMessage());
                JOptionPane.showMessageDialog(null,"Error al actualizar el saldo: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
               
            
            // Agregar a la cola de compra
            switch (tipa) {
                case "Bovinos" -> colaCompraBovinos.offer(new Object[]{codigo, Costoc, FechaCompra});
                case "Gallinas" -> colaCompraGallinas.offer(new Object[]{codigo, Costoc, FechaCompra});
                case "Cerdos" -> colaCompraCerdos.offer(new Object[]{codigo, Costoc, FechaCompra});
                case "Caballos" -> colaCompraCaballos.offer(new Object[]{codigo, Costoc, FechaCompra});
                case "Caprinos" -> colaCompraCaprinos.offer(new Object[]{codigo, Costoc, FechaCompra});
            }
            
            System.out.println("");
            
            float costocompraReal=Costoc*-1;
           
            // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO compras" + "(ID_animal,ID_usuario, f_compra, v_compra, Edad_inicial, Peso_inicial_kg)"+
                         "select animales.ID, ?,?,?,?,? "+
                         "from animales where animales.codigo = ? and animales.estado = 'activo'";
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            ps.setInt(1, IDUsuario);
            ps.setString(2, FechaCompra.toString()); // Convertir FechaCompra a cadena
            ps.setFloat(3, costocompraReal);
            ps.setString(4, RangoEdad);
            ps.setDouble(5, pesoInicial);
            ps.setString(6, codigo);
           
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Transaccion insertada con exito");
            }

            InsertarTransaccion(FechaCompra, costocompraReal);
        
        
    }
    
    public void InsertarTransaccion(LocalDateTime FechaCompra, Float costocompraReal) throws SQLException {
    int idCompra = 0;
    String query = "SELECT id FROM compras WHERE f_compra = ? AND v_compra = ?";

    // Consulta el ID de la compra
    try (PreparedStatement stmt = cx.prepareStatement(query)) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedFechaCompra = FechaCompra.format(formatter);
        stmt.setString(1, formattedFechaCompra);
        stmt.setFloat(2, Math.round(costocompraReal * 100.0) / 100.0f);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            idCompra = rs.getInt("id");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el ID de compra. Verifica los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir si no se encuentra el ID
        }
    }

    // Inserta en transacciones
    String tipot = "COMPRA";
    String sql = "INSERT INTO transacciones (TIPO_TRANSACCION, id_compra) VALUES (?, ?)";

    try (PreparedStatement ps = cx.prepareStatement(sql)) {
        ps.setString(1, tipot);
        ps.setInt(2, idCompra);

        int filasInsertadas = ps.executeUpdate();
        if (filasInsertadas > 0) {            
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al registrar la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    public  void editarmatriz(String codigo, String GeneroAnimal) throws SQLException {
    int numero_espacio;
    Integer xx = null,yy = null;
    int[] filas1 = {1,2};
    int[] filas2 = {3,4};
    int[] filas3 = {5,6};
    int[] filas4 = {7,8};
    int[] filas5 = {9,10};
    int[] columnas ={1,2,3,4,5,6,7,8,9,10};
    boolean updated = true;
    //System.out.println(tipa);

     // Convertir el arreglo de enteros en un arreglo de objetos Integer
        Integer[] numerosFilas1 = new Integer[filas1.length];
        Integer[] numerosFilas2 = new Integer[filas2.length];
        Integer[] numerosFilas3 = new Integer[filas3.length];
        Integer[] numerosFilas4 = new Integer[filas4.length];
        Integer[] numerosFilas5 = new Integer[filas5.length];
        Integer[] numerosColumnas = new Integer[columnas.length];
        
        for (int i = 0; i < filas1.length; i++) {
            numerosFilas1[i] = filas1[i]; }
        for (int i = 0; i < filas2.length; i++) {
            numerosFilas2[i] = filas2[i]; }
        for (int i = 0; i < filas3.length; i++) {
            numerosFilas3[i] = filas3[i]; }
        for (int i = 0; i < filas4.length; i++) {
            numerosFilas4[i] = filas4[i]; }
        for (int i = 0; i < filas5.length; i++) {
            numerosFilas5[i] = filas5[i]; }
        for (int j = 0; j < columnas.length; j++) {
                    numerosColumnas[j] = columnas[j];}
        
        switch (tipa) {
            case "Bovinos" ->   sitio = "establo";
            case "Gallinas" ->  sitio = "gallinero";
            case "Cerdos" ->    sitio = "chiquero";
            case "Caballos" ->  sitio = "caballeriza";
            case "Caprinos" ->  sitio = "cabriza";
            default -> {}
        }
        JComboBox<Integer> comboBoxfilas = null;

        // Crear el JComboBox con el arreglo de Integer
        if("Ternero".equals(RangoEdad)||"Pollito".equals(RangoEdad)||"Lechon".equals(RangoEdad)||"Lactante".equals(RangoEdad)){
        comboBoxfilas = new JComboBox<>(numerosFilas1);
        }
        if("Becerro".equals(RangoEdad)||"Crecimiento".equals(RangoEdad)||"Destetado".equals(RangoEdad)||"Potro".equals(RangoEdad)){
        comboBoxfilas = new JComboBox<>(numerosFilas2);
        }
        if("Novillo".equals(RangoEdad)||"Joven".equals(RangoEdad)||"Crecimiento".equals(RangoEdad)||"Adolecente".equals(RangoEdad)||"Chivo".equals(RangoEdad)){
        comboBoxfilas = new JComboBox<>(numerosFilas3);
        }
        if("Adulto".equals(RangoEdad)||"Ceba".equals(RangoEdad)){
        comboBoxfilas = new JComboBox<>(numerosFilas4);
        }
        if("Senior".equals(RangoEdad)){
        comboBoxfilas = new JComboBox<>(numerosFilas5);
        }
        
        
        JComboBox<Integer> comboBoxcolumnas = new JComboBox<>(numerosColumnas);
        
        while(updated){
        // Crear el mensaje personalizado
        JPanel panelfilas = new JPanel(new GridLayout(2,2));
        panelfilas.add(new JLabel("Selecciona la fila (sentido horizontal)"));
        panelfilas.add(comboBoxfilas);
        panelfilas.add(new JLabel("Selecciona la columna (sentido vertical)"));
        panelfilas.add(comboBoxcolumnas);
        

        // Mostrar el JOptionPane con el JComboBox incluido
        int resultu = JOptionPane.showConfirmDialog(
            null,
            panelfilas,
            "Selecciona una opción",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE );

        // Obtener la opción seleccionada
        if (resultu == JOptionPane.OK_OPTION|| resultu == JOptionPane.CANCEL_OPTION || resultu == JOptionPane.CLOSED_OPTION) {
             xx  = (Integer) comboBoxfilas.getSelectedItem();
            if (null==xx){
                //System.out.println("Es necesario escojer una opcion antes de continuar");
                JOptionPane.showMessageDialog(null,"Es necesario escojer una fila antes de continuar","error", JOptionPane.ERROR_MESSAGE); 
                continue;//volver a pedir fila si es null
            }}

        // Obtener la opción seleccionada
        if (resultu == JOptionPane.CANCEL_OPTION || resultu == JOptionPane.CLOSED_OPTION||resultu == JOptionPane.OK_OPTION) {
            yy  = (Integer) comboBoxcolumnas.getSelectedItem();
            if (null==yy){
                JOptionPane.showMessageDialog(null,"Es necesario escojer una columna antes de continuar","error", JOptionPane.ERROR_MESSAGE); 
                continue;//volver a pedir columna si es null;
            }}

        // fila (f) = xx y columna (c) = yy
        // fórmula para saber el espacio = ((f-1)*10)+c
        numero_espacio = ((xx - 1) * 10) + yy;
        String Ubicacion_completa = sitio + " espacio: " + numero_espacio;
        String estado = "activo";

        // Verificar si ya existe un animal en la misma ubicación con estado activo
        String checkSql = "SELECT COUNT(*) FROM animales WHERE ubicacion = ? AND estado = ?";
        PreparedStatement checkPs = cx.prepareStatement(checkSql);
        checkPs.setString(1, Ubicacion_completa);
        checkPs.setString(2, estado);

        ResultSet rs = checkPs.executeQuery();
        rs.next();
        int count = rs.getInt(1);
       
        if (count > 0) {
            //System.out.println("Ya existe un animal en esta ubicación con estado activo. Intente nuevamente.");
            JOptionPane.showMessageDialog(null,"Ya existe un animal en esta ubicación con, Intente nuevamente.","Error de ubicacio: err002",JOptionPane.WARNING_MESSAGE); 
              continue; // Pedir nuevamente fila y columna
        }

        // Consulta SQL para insertar el nuevo registro
        String insertSql = "INSERT INTO animales (codigo, tipo_animal, Genero, ubicacion, estado) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = cx.prepareStatement(insertSql);
        ps.setString(1, codigo);
        ps.setString(2, tipa);
        ps.setString(3, GeneroAnimal);
        ps.setString(4, Ubicacion_completa);
        ps.setString(5, estado);
        

        int filasInsertadas = ps.executeUpdate();
        if (filasInsertadas > 0) {
            System.out.println("Registro llevado con éxito.");
            updated = false; // Salir del bucle
        }
    }
}
    
    public String[] PedigreeAnimales(String codigo) throws SQLException{
        //System.out.println(tipa);
        String[] datos = new String[5];
        JComboBox<String> comboBoxRazaAnimal = null;//inicializamos las opciones de raza
        JComboBox<String> comboBoxPropositoAnimal = null;//inicializamos las opciones de proposito
        JComboBox<String> comboBoxGenerosAnimal = null;//inicializamos las opciones de genero 
        JComboBox<String> comboBoxRazaPadre = null;
        JComboBox<String> comboBoxRazaMadre = null;
        JComboBox<String> comboBoxClasificacionEdad = null;//inicializamos las opciones de clasificacion por edad
        
        // Crear combo boxes para las 3 razas
        String GeneroAnimal = null, PropositoAnimal = null,RazaAnimal = null,RazaMadreAnimal = null, RazaPadreAnimal= null;//inicializamos las variables del registro pedigree y genero
        
        //lista de generos
        String[] GenerosAnimales = {"Macho","Hembra"};
        //creamos el combobox de una vez
        comboBoxGenerosAnimal = new JComboBox<>(GenerosAnimales);
        
        //lista razas segun tipos de animales 1.0
        String[] RazasBovinos = {"Cebu Brahman","Brahman","Normando","Holstein","Pardo Suizo","Girolando","Otra"};
        String[] RazaGallinas = {"Rhode island red","Lohmann Brown","Orpington","Leghorn","Otra"};
        String[] RazaCerdos   = {"Duroc","Gilt","Gottingen Minipig","Large White","San Pedreño","Otra"};
        String[] RazaCaballos = {"Criollo","Perceron","Arabe","Turcomano","Morgan","Otra"};
        String[] RazaCaprinos = {"Saanen","Boer","Nubia","Alpina","Toggenburg","Otra"};
        
        //lista clasificacion de animales por edad
        String[] EdadBovinos  = {"Ternero","Becerro","Novillo","Adulto","Senior"};
        String[] EdadGallinas = {"Pollito","Crecimiento","Joven","Adulto","Senior"};
        String[] EdadCerdos   = {"Lechon", "Destetado","Crecimiento","Ceba","Senior"};
        String[] EdadCaballos = {"Lactante","Potro","Adolecente","Adulto","Senior"};
        String[] EdadCaprinos = {"Lactante","Destetado","Chivo","Adulto","Senior"};
        
                
        //lista propositos segun tipos de animales 1.0
        String[] PropositosBovinos  = {"Produccion de carne","Produccion de leche","Fecundacion","Gestacion","Comercializacion"};
        String[] PropositosGallinas = {"Produccion de pollo","Produccion de huevos","Fecundacion","Comercializacion"};
        String[] PropositosCerdos   = {"produccion de carne","Exposicion","Fecundacion","Gestacion","Comercializacion"};
        String[] PropositosCaballos = {"Exposicion","Trabajo","Fecundacion","Gestacion","Comercializacion"};
        String[] PropositosCaprinos = {"Produccion de carne","Produccion de leche","Produccion de fibra","Fecundacion","Gestacion","Pastoreo"};
        
        if(null != tipa) // Crear el JComboBox
        switch (tipa) {
            case "Bovinos" -> {
                 comboBoxRazaAnimal = new JComboBox<>(RazasBovinos);
                 comboBoxPropositoAnimal = new JComboBox<>(PropositosBovinos);
                 comboBoxRazaPadre  = new JComboBox<>(RazasBovinos);
                 comboBoxRazaMadre  = new JComboBox<>(RazasBovinos);
                 comboBoxClasificacionEdad = new JComboBox(EdadBovinos);}
            case "Gallinas" -> {
                comboBoxRazaAnimal = new JComboBox<>(RazaGallinas);
                comboBoxPropositoAnimal = new JComboBox<>(PropositosGallinas);
                comboBoxRazaPadre  = new JComboBox<>(RazaGallinas);
                comboBoxRazaMadre  = new JComboBox<>(RazaGallinas);
                comboBoxClasificacionEdad = new JComboBox(EdadGallinas);}
            case "Cerdos" -> {
                comboBoxRazaAnimal = new JComboBox<>(RazaCerdos);
                comboBoxPropositoAnimal = new JComboBox<>(PropositosCerdos);
                comboBoxRazaPadre  = new JComboBox<>(RazaCerdos);
                comboBoxRazaMadre  = new JComboBox<>(RazaCerdos);
                comboBoxClasificacionEdad = new JComboBox(EdadCerdos);}
            case "Caprinos" -> {
                comboBoxRazaAnimal = new JComboBox<>(RazaCaprinos);
                comboBoxPropositoAnimal = new JComboBox<>(PropositosCaprinos);
                comboBoxRazaPadre  = new JComboBox<>(RazaCaprinos);
                comboBoxRazaMadre  = new JComboBox<>(RazaCaprinos);
                comboBoxClasificacionEdad = new JComboBox(EdadCaprinos);}
            case "Caballos" -> {
               comboBoxRazaAnimal = new JComboBox<>(RazaCaballos);
               comboBoxPropositoAnimal = new JComboBox<>(PropositosCaballos);
               comboBoxRazaPadre  = new JComboBox<>(RazaCaballos);
               comboBoxRazaMadre  = new JComboBox<>(RazaCaballos);
               comboBoxClasificacionEdad = new JComboBox(EdadCaballos);}
            default -> {JOptionPane.showMessageDialog(null,"Tipo de animal no valido, por favor intente mas luego","error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        
        // Sección para selección de razas (animal, padre y madre)
    boolean razasValidas = false;
    
    while (!razasValidas) {
        JPanel panelRazas = new JPanel(new GridLayout(3, 2));
        panelRazas.add(new JLabel("Raza del animal:"));
        panelRazas.add(comboBoxRazaAnimal);
        panelRazas.add(new JLabel("Raza del padre:"));
        panelRazas.add(comboBoxRazaPadre);
        panelRazas.add(new JLabel("Raza de la madre:"));
        panelRazas.add(comboBoxRazaMadre);

        int resultadoRazas = JOptionPane.showConfirmDialog(
            null,
            panelRazas,
            "Seleccione las razas",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (resultadoRazas != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Debe completar todas las selecciones de raza", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }

        RazaAnimal = (String) comboBoxRazaAnimal.getSelectedItem();
        RazaPadreAnimal = (String) comboBoxRazaPadre.getSelectedItem();
        RazaMadreAnimal = (String) comboBoxRazaMadre.getSelectedItem();

        // Manejo de la opción "Otra"
        if ("Otra".equals(RazaAnimal)) {
            RazaAnimal = JOptionPane.showInputDialog("Ingrese la raza del animal:");
            if (RazaAnimal == null || RazaAnimal.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar una raza válida", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
        }

        if ("Otra".equals(RazaPadreAnimal)) {
            RazaPadreAnimal = JOptionPane.showInputDialog("Ingrese la raza del padre:");
            if (RazaPadreAnimal == null || RazaPadreAnimal.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar una raza válida", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
        }

        if ("Otra".equals(RazaMadreAnimal)) {
            RazaMadreAnimal = JOptionPane.showInputDialog("Ingrese la raza de la madre:");
            if (RazaMadreAnimal == null || RazaMadreAnimal.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar una raza válida", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
        }

        razasValidas = true;
    }

    // Sección para selección de género y propósito
    boolean generoPropositoValidos = false;
    
    while (!generoPropositoValidos) {
        JPanel panelGeneroProposito = new JPanel(new GridLayout(3, 2));
        panelGeneroProposito.add(new JLabel("Clasificacion edad: "));
        panelGeneroProposito.add(comboBoxClasificacionEdad);
        panelGeneroProposito.add(new JLabel("Género del animal:"));
        panelGeneroProposito.add(comboBoxGenerosAnimal);
        panelGeneroProposito.add(new JLabel("Propósito del animal:"));
        panelGeneroProposito.add(comboBoxPropositoAnimal);

        int resultadoGeneroProposito = JOptionPane.showConfirmDialog(
            null,
            panelGeneroProposito,
            "Seleccione género y propósito",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (resultadoGeneroProposito != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar género y propósito", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }

        GeneroAnimal = (String) comboBoxGenerosAnimal.getSelectedItem();
        PropositoAnimal = (String) comboBoxPropositoAnimal.getSelectedItem();
        RangoEdad = (String) comboBoxClasificacionEdad.getSelectedItem();
        
        if("Macho".equals(GeneroAnimal) & ("Produccion de leche".equals(PropositoAnimal) || "Produccion de huevos".equals(PropositoAnimal) || "Gestacion".equals(PropositoAnimal))){
            JOptionPane.showMessageDialog(null,"Lo sentimos, los animales macho son incapaces de realizar el proposito indicado por favor selccione otro proposito","Erro de proposito vs genero",JOptionPane.WARNING_MESSAGE);
            continue;
        }
        
        if("Hembra".equals(GeneroAnimal) & "Fecundacion".equals(PropositoAnimal)){
         JOptionPane.showMessageDialog(null,"Lo sentimos los animales hembra son incapaces de fecundar debido a que no pueden inseminar, por favor escoge otro proposito","Erro de proposito vs genero",JOptionPane.WARNING_MESSAGE);
         continue;
        }
        
        if (GeneroAnimal != null && PropositoAnimal != null) {
            generoPropositoValidos = true;
        }
    }

    // Asignar los valores al arreglo de datos
    datos[0] = GeneroAnimal;
    datos[1] = RazaAnimal;
    datos[2] = PropositoAnimal;
    datos[3] = RazaMadreAnimal;
    datos[4] = RazaPadreAnimal;

    return datos;

   
    }
    
    public void CRUDPedigree(String RazaAnimal, String PropositoAnimal, String RazaMadreAnimal,String RazaPadreAnimal, String codigo) throws SQLException{
        
        // Consulta completa para insertar en la tabla Pedigree
        String sqlInsertPedigree = "INSERT INTO Pedigree (ID_ANIMAL,ID_usuario, RazaAnimal, PropositoAnimal, RazaMadre, RazaPadre) " +
                                   "SELECT Animales.ID, ?, ?, ?, ?, ? " +
                                   "FROM Animales " +
                                   "WHERE Animales.Codigo = ? AND Animales.Estado = 'activo'";

        // Preparar la consulta con parámetros
        PreparedStatement preparedStatement = cx.prepareStatement(sqlInsertPedigree);

        // Asignar valores para los parámetros
        preparedStatement.setInt(1, IDUsuario);
        preparedStatement.setString(2, RazaAnimal);       // Raza del animal
        preparedStatement.setString(3, PropositoAnimal); // Propósito del animal
        preparedStatement.setString(4, RazaMadreAnimal);       // Raza de la madre
        preparedStatement.setString(5, RazaPadreAnimal);       // Raza del padre
        preparedStatement.setString(6, codigo);    // Código del animal
        

        // Ejecutar la consulta
        int filasInsertadas = preparedStatement.executeUpdate();
        if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(null,"Pedigree registrado exitosamente");
        } else {
            JOptionPane.showMessageDialog(null,"Lo sentimos no ha sido posible registrar el pedigree","error", JOptionPane.ERROR_MESSAGE);
        }
    
    }    
    
}
