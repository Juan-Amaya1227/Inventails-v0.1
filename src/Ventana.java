import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import javax.swing.table.DefaultTableModel;


public class Ventana extends JFrame {
    public static String sitio; 
    
    
    public static String db = "inventails";
    public static String url = "jdbc:mysql://localhost:3306/";
    public static String user = "root";
    public static String password = "";
    private Image imagenFondo;

    String driver = "com.mysql.cj.jdbc.Driver";
    
     //cola compras 
    public static final Queue<Object[]> colaCompraBovinos = new LinkedList<>();
    public static final Queue<Object[]> colaCompraGallinas = new LinkedList<>();
    public static final Queue<Object[]> colaCompraCerdos = new LinkedList<>();
    public static final Queue<Object[]> colaCompraCaballos = new LinkedList<>();
    public static final Queue<Object[]> colaCompraCaprinos = new LinkedList<>();
    
    // Pilas para manejar la  venta
    public static final Stack<Object[]> pilaVentaBovinos = new Stack<>();
    public static final Stack<Object[]> pilaVentaGallinas = new Stack<>();
    public static final Stack<Object[]> pilaVentaCerdos = new Stack<>();
    public static final Stack<Object[]> pilaVentaCaballos = new Stack<>();
    public static final Stack<Object[]> pilaVentaCaprinos = new Stack<>();
    
    //registros
    public static final Map<String, Ventana> registrosBovinos = new HashMap<>();
    public static final Map<String, Ventana> registrosGallinas = new HashMap<>();
    public static final Map<String, Ventana> registrosCerdos = new HashMap<>();
    public static final Map<String, Ventana> registrosCaballos = new HashMap<>();
    public static final Map<String, Ventana> registrosCaprinos = new HashMap<>();
    
    

    public static Connection cx;
    private JPanel mainPanel;
    private JComboBox<String> comboBoxtipa;
    private JButton buttonIngresar;
    private JButton buttonEspaciosA;
    private JButton buttonCsv;
    private JButton buttonDecidirMotivoRetirarA;
    private JButton buttonLimpiar;
    private JButton buttonTransaccionesR;
    private JButton buttonGastosMantenimiento;
    private JButton buttonMedicinasAplicadas;
    private JButton buttonRegistroEnfermedades;
    private JLabel label_TOTAL_BOVINOS;
    private JLabel label_TOTAL_GALLINAS;
    private JLabel label_TOTAL_CABALLOS;
    private JLabel label_TOTAL_CERDOS;
    private static final int TOTAL_Bovinos = 100;
    private static final int TOTAL_Gallinas = 100;
    private static final int TOTAL_Cerdos = 100;
    private static final int TOTAL_Caballos = 100;
    private static final int TOTAL_Caprinos = 100;
    public  String tipa;
    private static boolean encontrado;
    private static  float SALDO = 0;
    private String NivelAcceso; // Campo para almacenar el nivel de acceso
    private int IDUsuario;      // Campo para almacenar el ID del usuario

    
    public Ventana(String NivelAcceso, int IDUsuario) {
        
        this.NivelAcceso = NivelAcceso;
        this.IDUsuario = IDUsuario;
        
        setTitle("InvenTails");
        setSize(520, 390);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setResizable(false);

        // Inicialización de componentes
        initComponentes();
        configurarEventos();


        setVisible(true);
        
    } 
    private void initComponentes() {
        mainPanel = new PanelConImagen();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        crearPanelSuperior();
        crearPanelInferior();
    }
    private void crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new GridLayout(5, 3));
        panelSuperior.setOpaque(false);
        new JTextField(10);
        comboBoxtipa = new JComboBox<>(new String[]{"Bovinos", "Gallinas", "Cerdos", "Caballos","Caprinos"});
        tipa = (String) comboBoxtipa.getSelectedItem(); // Valor inicial
        // --- Listener único y permanente ---
        comboBoxtipa.addActionListener(e -> {
            tipa = (String) comboBoxtipa.getSelectedItem(); 
        });
        buttonIngresar = new JButton("Comprar A");
        buttonCsv = new JButton("Excel");
        buttonEspaciosA = new JButton("Espacios A");
        buttonDecidirMotivoRetirarA = new JButton("Retirar A");
        buttonLimpiar = new JButton("Cerrar sesion");
        buttonTransaccionesR = new JButton("Transa R");
        buttonGastosMantenimiento = new JButton("Gastos M");
        buttonMedicinasAplicadas = new JButton("Medicinas");
        buttonRegistroEnfermedades = new JButton("Enfermedades");

       
        panelSuperior.add(new JLabel(" Tipo  de  animal:"));
        panelSuperior.add(new JLabel("      "));
        panelSuperior.add(comboBoxtipa);
        panelSuperior.add(new JLabel("      "));
        panelSuperior.add(new JLabel("               "));
        panelSuperior.add(new JLabel("      "));
        panelSuperior.add(buttonIngresar);
        panelSuperior.add(buttonCsv);
        panelSuperior.add(buttonDecidirMotivoRetirarA);
        panelSuperior.add(buttonEspaciosA);
        panelSuperior.add(buttonRegistroEnfermedades);
        panelSuperior.add(buttonTransaccionesR);
        panelSuperior.add(buttonMedicinasAplicadas);
        panelSuperior.add(buttonLimpiar);
        panelSuperior.add(buttonGastosMantenimiento);
        

        mainPanel.add(panelSuperior, BorderLayout.NORTH);
    }
    private void crearPanelInferior() {
        JPanel panelInferior = new JPanel(new GridLayout(2, 2));
        panelInferior.setOpaque(false);

        label_TOTAL_BOVINOS = new JLabel("    Espacios para Bovinos: " + TOTAL_Bovinos);
        label_TOTAL_GALLINAS = new JLabel("Espacios para Gallinas: " + TOTAL_Gallinas);
        label_TOTAL_CERDOS = new JLabel("Espacios para por Cerdos: " + TOTAL_Cerdos);
        label_TOTAL_CABALLOS = new JLabel("    Espacios para Caballos: "+ TOTAL_Caballos);

        panelInferior.add(label_TOTAL_BOVINOS);
        panelInferior.add(label_TOTAL_GALLINAS);
        panelInferior.add(label_TOTAL_CABALLOS);
        panelInferior.add(label_TOTAL_CERDOS);

        mainPanel.add(panelInferior, BorderLayout.CENTER);
    }
    private void configurarEventos() {
        
        buttonIngresar.addActionListener(e ->  {//ya tiene identificado quien hace registros de compra (comprar, pedigree, tabla animales) falta comprobacion genero vs proposito
            ComprarAnimal comprara = new ComprarAnimal(IDUsuario,colaCompraCaprinos,colaCompraBovinos,colaCompraGallinas,colaCompraCerdos,colaCompraCaballos,
                                                      registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                        url,db,user,password,sitio,tipa,(java.sql.Connection) cx);
            try {comprara.ComprarA();} 
            catch (SQLException ex) {Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);}
            });
        
        buttonCsv.addActionListener(e -> { if(!"Administrador".equals(NivelAcceso)){//ya bloquea si no cuenta con el permiso hace falta completar los reportes
                                           JOptionPane.showMessageDialog(null,"Actualmente no se encuentra autorizado para llevar a cabo esta accion","Error",JOptionPane.WARNING_MESSAGE);
                                              }
        else{ExportarExcel exportar = new ExportarExcel(url,db,user,password,IDUsuario,cx);
        
            try {
                exportar.seleccionartipodereporte();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
}
        });
        
         buttonDecidirMotivoRetirarA.addActionListener(e ->{//ya tiene identificado quien registra venta/defunciones de animales
                RetirarAnimal retirara = new RetirarAnimal(IDUsuario,pilaVentaCaprinos,pilaVentaBovinos,pilaVentaGallinas,pilaVentaCerdos,pilaVentaCaballos,
                                                           registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                           tipa, cx,user,db,url, password);
            try { retirara.DecidirMotivoRetirarA();} catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);}
        });
         
               buttonMedicinasAplicadas.addActionListener(e ->{//ya registra quien insterto el registrp de meidicinas
            MedicinasAplicadas medicinasa = new MedicinasAplicadas(IDUsuario,pilaVentaCaprinos,pilaVentaBovinos,pilaVentaGallinas,pilaVentaCerdos,pilaVentaCaballos,
                                                           registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                           tipa, cx,user,db,url, password);
            try {
                medicinasa.MedicinasAplicadas();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        
        buttonRegistroEnfermedades.addActionListener(e -> {
           RegistroEnfermedadesAnimales enfermedadesa = new RegistroEnfermedadesAnimales(IDUsuario,pilaVentaCaprinos,pilaVentaBovinos,pilaVentaGallinas,pilaVentaCerdos,pilaVentaCaballos,
                                                           registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                           tipa, cx,user,db,url, password);
            try {
                enfermedadesa.RegistroEnfermedades();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttonEspaciosA.addActionListener(e -> {
        EspaciosYestadosA espacios = new EspaciosYestadosA(url, db,password,tipa,user);
        espacios.EspaciosA();
        });
        
        buttonGastosMantenimiento.addActionListener(e ->{//ya identifica quien registro el gasto de mantenimiento
           GastosDeMantenimiento gastos = new GastosDeMantenimiento(IDUsuario,cx);
            try {
                gastos.GastosDeMantenimiento();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
});

        buttonTransaccionesR.addActionListener(e ->Transacciones());
        buttonLimpiar.addActionListener(e ->{
            try {
                CerrarSesion();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
  
        
    }

   public void CerrarSesion() throws SQLException{
   
   // Mensaje de confirmación
    int confirmacion = JOptionPane.showConfirmDialog(
        this,
        "¿Está seguro que desea cerrar la sesión?",
        "Confirmar cierre de sesión",
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        // Cierra la ventana actual
        this.dispose();
        
        // Obtener la referencia al login original
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof Login) {
                Login login = (Login) window;
                login.LimpiarLogin(); // Restablece al estado inicial
                login.setVisible(true); // Muestra el login
                break;
            }
        }
         AuditoriaCierreSesion();
        // Mensaje de despedida
        JOptionPane.showMessageDialog(
            null,
            "Sesión cerrada correctamente. ¡Hasta pronto!",
            "Sesión finalizada",
            JOptionPane.INFORMATION_MESSAGE
        );
    }   
       
   }
   
   public void AuditoriaCierreSesion() throws SQLException{
       LocalDateTime HoraAsistencia = LocalDateTime.now();
        // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO Asistencia (ID_Usuario, Accion, FechaAccion)"+
                         " values (?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            String accion = "Desconexion";
            ps.setInt(1, IDUsuario); 
            ps.setString(2, accion);
            ps.setString(3, HoraAsistencia.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
   }

   public  void Transacciones(){
        // Crear un nuevo JFrame para mostrar las transacciones
    JFrame transaccionesFrame = new JFrame("Transacciones");
    transaccionesFrame.setSize(600, 400);
    transaccionesFrame.setLocationRelativeTo(this);
    transaccionesFrame.setLayout(new BorderLayout());

    // Panel para los botones de selección
    JPanel panelSeleccion = new JPanel(new FlowLayout());
    JButton btnCompras = new JButton("Ver Compras");
    JButton btnVentas = new JButton("Ver Ventas");
    panelSeleccion.add(btnCompras);
    panelSeleccion.add(btnVentas);

    // Modelo de tabla
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Código");
    model.addColumn("Monto");
    model.addColumn("Fecha");
    model.addColumn("Tipo");

    // Tabla para mostrar los datos
    JTable tabla = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(tabla);

    // Acción para el botón de compras
    btnCompras.addActionListener(e -> {
        model.setRowCount(0); // Limpiar la tabla
        
        if (tipa == null || tipa.isEmpty()) {
            JOptionPane.showMessageDialog(transaccionesFrame, 
                "Debe seleccionar un tipo de animal antes de continuar", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Queue<Object[]> colaSeleccionada = switch (tipa) {
            case "Bovinos" -> colaCompraBovinos;
            case "Gallinas" -> colaCompraGallinas;
            case "Cerdos" -> colaCompraCerdos;
            case "Caprinos" -> colaCompraCaprinos;
            case "Caballos" -> colaCompraCaballos;
            default -> null;
        };

        if (colaSeleccionada == null || colaSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(transaccionesFrame, 
                "No hay compras registradas para " + tipa, 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Object[] compra : colaSeleccionada) {
            model.addRow(new Object[]{compra[0], "-" + compra[1], compra[2], "Compra"});
        }
    });

    // Acción para el botón de ventas
    btnVentas.addActionListener(e -> {
        model.setRowCount(0); // Limpiar la tabla
        
        if (tipa == null || tipa.isEmpty()) {
            JOptionPane.showMessageDialog(transaccionesFrame, 
                "Debe seleccionar un tipo de animal antes de continuar", 
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Stack<Object[]> pilaSeleccionada = switch (tipa) {
            case "Bovinos" -> pilaVentaBovinos;
            case "Gallinas" -> pilaVentaGallinas;
            case "Cerdos" -> pilaVentaCerdos;
            case "Caprinos" -> pilaVentaCaprinos;
            case "Caballos" -> pilaVentaCaballos;
            default -> null;
        };

        if (pilaSeleccionada == null || pilaSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(transaccionesFrame, 
                "No hay ventas registradas para " + tipa, 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Object[] venta : pilaSeleccionada) {
            model.addRow(new Object[]{venta[0], "+" + venta[1], venta[2], "Venta"});
        }
    });

    // Agregar componentes al frame
    transaccionesFrame.add(panelSeleccion, BorderLayout.NORTH);
    transaccionesFrame.add(scrollPane, BorderLayout.CENTER);

    // Mostrar el frame
    transaccionesFrame.setVisible(true);
    }    
    
 // Clase interna para el panel con imagen de fondo
    private class PanelConImagen extends JPanel {
        private final Image imagenFondo;

        public PanelConImagen() {
            // Cargar la imagen
            imagenFondo = new ImageIcon("C:\\Users\\amaya\\Downloads\\Inventails\\JavaApplication10\\src\\javaapplication10\\Inventails.png").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagenFondo != null) {
                // Dibuja la imagen en todo el panel
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
    
    public static Connection conectar() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cx = DriverManager.getConnection(url + db, user, password);
            JOptionPane.showMessageDialog(null, "SE CONECTO A LA BASE DE DATOS " + db);
        } catch (SQLException ex) {
            //Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "NO SE CONECTO A LA BASE DE DATOS: " + db +" "+ ex.getMessage(),"Error critico: err000",  JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return cx;
    }
   
    
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
   
            Connection conexion = conectar(); 
            //Ventana ventana = new Ventana();
            
            //2. Mostrar la ventana de login (y pasarle la conexión)
            Login ventanaLogin = new Login(conexion);
            //ventanaLogin.setVisible(true);
  
    }}
