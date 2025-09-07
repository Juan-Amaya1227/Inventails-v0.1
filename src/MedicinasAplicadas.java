import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Stack;

public class MedicinasAplicadas {

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
    private int IDUsuario;
    public final Connection cx;
    
    public MedicinasAplicadas (int IDUsuario,Stack<Object[]> pilaVentaCaprinos,Stack<Object[]> pilaVentaBovinos,Stack<Object[]> pilaVentaGallinas,
                         Stack<Object[]> pilaVentaCerdos, Stack<Object[]> pilaVentaCaballos,
                         Map<String, Ventana> registrosCaprinos,Map<String, Ventana> registrosBovinos, Map<String, Ventana> registrosGallinas, 
                         Map<String, Ventana> registrosCerdos,  Map<String, Ventana> registrosCaballos, String tipa, Connection cx,
                         String user, String db, String url, String password){
        
        this.IDUsuario = IDUsuario;
        this.registrosBovinos = registrosBovinos;
        this.pilaVentaCaprinos = pilaVentaCaprinos;
        this.registrosCaprinos = registrosCaprinos;
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
        
        
        
        public void MedicinasAplicadas() throws SQLException{
        String ConcentracionTotal = null;
        String medicinaAplicada = null;
        String viaAplicacion = null;
        LocalDateTime FechaDeAplicacion = LocalDateTime.now();
        
        String codigomin = JOptionPane.showInputDialog("Ingrese el codigo del animal: ");
        if (codigomin == null||codigomin.isEmpty()){JOptionPane.showMessageDialog(null, "Registro de medicinas aplicadas cancelado");
        return;}
        String codigo = codigomin.toUpperCase();
     
        RetirarAnimal retiroa = new RetirarAnimal(IDUsuario,pilaVentaCaprinos,pilaVentaBovinos,pilaVentaGallinas,pilaVentaCerdos,pilaVentaCaballos,
                                                           registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                           tipa, cx,user,db,url, password);
        int idAnimal = retiroa.BuscarElementoV(codigo); // Llamar al método y obtener el ID del animal
        if (idAnimal == 0) { // Si el ID es 0, el animal no se encontró
       
        JOptionPane.showMessageDialog(null,"El animal solicitado no se encuentra en la granja ","error", JOptionPane.ERROR_MESSAGE);
        System.out.println("");
        return; }
        
        String[] OpcionesViaAplicacion = {"Oral","Subcutanea","Intravenosa","Topica","Intranasal","Oftalmica", "Via otica",
                                          "Transdermica","Rectal","Intramamaria","Inhalatoria","Sublingual","Vía intraruminal","Intraarticular"};
        
        String[] opciones = {"ML", "MG","NO SABE"};
        
        JComboBox vias = new JComboBox<>(OpcionesViaAplicacion);
        JComboBox unidades = new JComboBox<>(opciones);
        JTextField Medicina = new JTextField();
        JTextField cantidad = new JTextField();
        boolean cmedicina = false;
        
        while(!cmedicina){
        // Crear el mensaje personalizado
        JPanel medicinas = new JPanel(new GridLayout(3, 2));
        medicinas.add(new JLabel("Selecciona Ingrese la medicina aplicada:"));
        medicinas.add(new JLabel(""));
        medicinas.add(Medicina);
        
        medicinas.add(new JLabel("Ingrese la concentracion: "));
        medicinas.add(cantidad);
        medicinas.add(unidades);
        
        medicinas.add(new JLabel("Ingrese la via de aplicacion: "));
        medicinas.add(new JLabel(""));
        medicinas.add(vias);

        // Mostrar el JOptionPane con el JComboBox incluido
        int result = JOptionPane.showConfirmDialog(
            null,
            medicinas,
            "Registre la medicina aplicada",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
         if (result != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Por favor termina de registrar la medicina antes", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
        
        
        medicinaAplicada = Medicina.getText();
        if(medicinaAplicada == null || medicinaAplicada.trim().isEmpty()){
        JOptionPane.showMessageDialog(null, "Es obligatorio ingresar una medicina");
        }
        
        String cantidadm, unidadm;
        
        cantidadm = cantidad.getText();
        if(cantidadm == null || cantidadm.trim().isEmpty()){
        JOptionPane.showMessageDialog(null, "Es obligatorio ingresar cantidad");
        continue;
        }
        unidadm = (String)unidades.getSelectedItem();
        ConcentracionTotal = cantidadm + " "+ unidadm;
        
        viaAplicacion = (String)vias.getSelectedItem();
        
        
        cmedicina = true;
        }

         // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO medicinasaplicadas " + "(ID_usuario, ID_animal, MedicamentoAplicado, ViaAplicacion, Concentracion, FechaAplicacion)"+
                          "VALUES (?, (SELECT ID FROM Animales WHERE codigo = ? AND estado = 'activo'), ?, ?, ?, ?)";
                    
        
            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetro
            ps.setInt(1, IDUsuario);
            ps.setString(2, codigo);
            ps.setString(3, medicinaAplicada);
            ps.setString(4, viaAplicacion);
            ps.setString(5, ConcentracionTotal);
            ps.setString(6, FechaDeAplicacion.toString()); // Convertir FechaCompra a cadena
            
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Transaccion insertada con éxito.");
            }

            System.out.println("Medicinas registradas con exito, fecha: " + FechaDeAplicacion);  
    
    }
    
    }

