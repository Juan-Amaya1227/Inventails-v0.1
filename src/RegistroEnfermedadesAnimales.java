import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Stack;
import javax.swing.JOptionPane;

public class RegistroEnfermedadesAnimales {
    
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
    
    public RegistroEnfermedadesAnimales(int IDUsuario,Stack<Object[]> pilaVentaCaprinos,Stack<Object[]> pilaVentaBovinos,Stack<Object[]> pilaVentaGallinas,
                         Stack<Object[]> pilaVentaCerdos, Stack<Object[]> pilaVentaCaballos,
                         Map<String, Ventana> registrosCaprinos,Map<String, Ventana> registrosBovinos, Map<String, Ventana> registrosGallinas, 
                         Map<String, Ventana> registrosCerdos,  Map<String, Ventana> registrosCaballos, String tipa, Connection cx,
                         String user, String db, String url, String password){
        
        this.IDUsuario = IDUsuario;
        this.pilaVentaCaprinos = pilaVentaCaprinos;
        this.registrosCaprinos = registrosCaprinos;
        this.registrosBovinos = registrosBovinos;
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
    
    public void RegistroEnfermedades() throws SQLException{
    LocalDateTime FechaDeRegistroEnfermedad = LocalDateTime.now();
    
        String codigomin = JOptionPane.showInputDialog("Ingrese el codigo del animal: ");
        if (codigomin == null||codigomin.isEmpty()){JOptionPane.showMessageDialog(null, "Registro de Enfermedades diagnosticadas cancelado");
        return;}
        String codigo = codigomin.toUpperCase();
        
        RetirarAnimal retiroa = new RetirarAnimal(IDUsuario, pilaVentaCaprinos,pilaVentaBovinos,pilaVentaGallinas,pilaVentaCerdos,pilaVentaCaballos,
                                                           registrosCaprinos,registrosBovinos, registrosGallinas, registrosCerdos, registrosCaballos,
                                                           tipa, cx,user,db,url, password);
        int idAnimal = retiroa.BuscarElementoV(codigo); // Llamar al método y obtener el ID del animal
        if (idAnimal == 0) { // Si el ID es 0, el animal no se encontró
       
        JOptionPane.showMessageDialog(null,"El animal solicitado no se encuentra en la granja ","error", JOptionPane.ERROR_MESSAGE);
        System.out.println("");
        return; }
        
        String enfermedad = JOptionPane.showInputDialog("Ingrese la enfermedad del animal: ");
         while (enfermedad == null||enfermedad.isEmpty()) {
            enfermedad = JOptionPane.showInputDialog("Por favor, Ingrese la enfermedad del animal: ");
            if (enfermedad == null||enfermedad.isEmpty()) {
                 JOptionPane.showMessageDialog(null,"El dato no puede estar vacío. Intentelo de nuevo ","error", JOptionPane.ERROR_MESSAGE); }}
         
        String Veterinario = JOptionPane.showInputDialog("Ingrese el nombre del veterinario: ");
        if(Veterinario == null||Veterinario.isEmpty()){Veterinario = "SE DESCONOCE";}
        
        String comentario = JOptionPane.showInputDialog("Ingrese un comentario al respecto de la enfermedad: "+enfermedad+" del animal "+codigo+ " (Opcional)");
        if(comentario == null||comentario.isEmpty()){comentario = "SIN COMENTARIOS";}
        
        // Consulta SQL correcta con marcadores de parámetros
        String sql = "INSERT INTO  registroenfermedades " + "(ID_usuario, ID_animal, F_Diagnostico, enfermedad, veterinario, comentario) "+
                        "VALUES (?, (SELECT ID FROM Animales WHERE codigo = ? AND estado = 'activo'), ?, ?, ?, ?)";

            // Preparar el statement
            PreparedStatement ps = cx.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetro
            ps.setInt(1, IDUsuario);
            ps.setString(2, codigo);
            ps.setString(3, FechaDeRegistroEnfermedad.toString()); // Convertir FechaCompra a cadena
            ps.setString(4, enfermedad);
            ps.setString(5, Veterinario);
            ps.setString(6, comentario);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Transaccion insertada con éxito.");
            }
            System.out.println("Enfermedad registrada satisfactoriamente, fecha: " + FechaDeRegistroEnfermedad);   }
    
}
