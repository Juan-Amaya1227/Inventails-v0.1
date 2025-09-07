import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;



public class Login extends JFrame {
    String[] PermisosNewU = {"Administrador","Gestor"};
    String[] Permisos = {"Administrador","Gestor","Bloqueado"};
    String[] DominiosElectronicos = {"@gmail.com", "@hotmail.com", "@outlook.com", "@live.com", "@yahoo.com", "@yahoo.es","@ucundinamarca.edu.co", "otro"};
    private final JTextField campoUsuario;
    private final JPasswordField campoContraseña;
    private final Connection  conexion;
    private JPanel mainPanel;
    private Boolean Ingreso = false;
    private final JButton AgregarUsuario;
    private final JButton CambiarPermisos;
    private final JButton AccesoAlSistema;
    private final JButton IniciarSesion;
    private final JButton CerrarSesion;
    private String NivelAcceso ;
    private int NumUsuarios = 0;
    private int IDUsuario = 0;
    private Boolean VerificacionCorreo = false;
    

    public  Login (Connection conexion) throws SQLException{
        this.conexion = conexion;
        setTitle("Iniciar sesion InvenTails");
        setSize(520, 390);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
         campoUsuario = new JTextField();
        campoContraseña = new JPasswordField();
        IniciarSesion = new JButton("Iniciar sesion");
        AgregarUsuario = new JButton("Agregar Usuario");
        CambiarPermisos = new JButton("Cambiar Rol");
        AccesoAlSistema = new JButton("Comenzar");
        CerrarSesion = new JButton("Cerrar Sesion");
        
        VerificarNumeroCuentas(conexion);
        if(NumUsuarios == 0){
            CrearPrimerUsuario();
        }
        else{
        // Inicialización de componentes
        initComponentes();
        configurarEventos();
        setVisible(true);}
    }
    
    private void VerificarNumeroCuentas(Connection conexion) throws SQLException{
    // Consulta de numero de cuentas
            String NumeroUsuarios = "SELECT COUNT(*) from usuarios";
             NumUsuarios = 0;  // Inicializar conteo
            try (PreparedStatement ps = conexion.prepareStatement(NumeroUsuarios);
                 ResultSet rs = ps.executeQuery()) {
        
                if (rs.next()) {
                    NumUsuarios = rs.getInt("COUNT(*)");
                } else {
                    JOptionPane.showMessageDialog(null,"No se encontro el numero de cuentas","error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,"Error al preparar o ejecutar la consulta: " + e.getMessage(),"error", JOptionPane.ERROR_MESSAGE);
            }
    }
    
    private void CrearPrimerUsuario() throws SQLException{
      boolean usuario = false;
      String UsuarioI = null;
      String ip1;
      String ip2;
      String CorreoParcial;
      String Dominio;
      String CorreoElectronico = null;
      String PasswordReal = null;
      
      while(!usuario){
      JPanel primerUsuario = new JPanel(new GridLayout(4, 3));
      JTextField campoprimerusuario = new JTextField();
      JTextField campocorreoelectronico = new JTextField();
      JComboBox<String> comboboxDominiosElectronicos = new JComboBox<>(DominiosElectronicos);
      JPasswordField PrimeraEntrada = new JPasswordField();
      JPasswordField SegundaEntrada = new JPasswordField();
      
      
      primerUsuario.add(new JLabel("Ingrese su nuevo usuario: "));
      primerUsuario.add(new JLabel(""));
      primerUsuario.add(campoprimerusuario);
      primerUsuario.add(new JLabel("Ingrese su correo electronico"));
      primerUsuario.add(campocorreoelectronico);
      primerUsuario.add(comboboxDominiosElectronicos);
      primerUsuario.add(new JLabel("Ingrese  su contraseña: "));
      primerUsuario.add(new JLabel(""));
      primerUsuario.add(PrimeraEntrada);
      primerUsuario.add(new JLabel("Confirme su contraseña: "));
      primerUsuario.add(new JLabel(""));
      primerUsuario.add(SegundaEntrada);
      
      int resultadoUsuario = JOptionPane.showConfirmDialog(
            null,
             primerUsuario,
            "Ingrese su nombre",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
      
       if (resultadoUsuario != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Por favor termina de crear tu usuario antes", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
        }
       
       UsuarioI = campoprimerusuario.getText();
       
       if (UsuarioI == null || UsuarioI.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Debe ingresar el campo de usuario", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }   
       CorreoParcial = campocorreoelectronico.getText();
       Dominio = (String) comboboxDominiosElectronicos.getSelectedItem();
       CorreoElectronico = CorreoParcial + Dominio;
       
       VerificarCorreo(CorreoElectronico);
       
       if (CorreoParcial == null || CorreoParcial.trim().isEmpty()){
            JOptionPane.showMessageDialog(null, "Debe ingresar su correo electronico para proceder", "Error", JOptionPane.ERROR_MESSAGE);
            continue;
       }
           
      
       
        ip1 = new String(PrimeraEntrada.getPassword());
        ip2 = new String(SegundaEntrada.getPassword());
        
        if(ip2.trim().isEmpty() || ip1 == null || ip1.trim().isEmpty() || ip2 == null){
         JOptionPane.showMessageDialog(null,"Debe ingresar ambos campos antes de proceder");
         continue;
        }
        
        if(ip1.equals(ip2)& ip2.equals(ip1)){
       PasswordReal = BCrypt.withDefaults().hashToString(12, ip2.toCharArray());
        }
        else{
        JOptionPane.showMessageDialog(null,"Las contraseñas no coinciden, intentalo de nuevo");
        continue;
        }
       usuario = true;}
      
      
      String Acceso = "Administrador";
        
        // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO usuarios (Usuario, Correo_Electronico, VerificacionCorreo, Password, nivelPermiso)"+
                         " values (?,?,?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = conexion.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setString(1, UsuarioI);
            ps.setString(2, CorreoElectronico); 
            ps.setString(3, VerificacionCorreo.toString()); 
            ps.setString(4, PasswordReal);
            ps.setString(5, Acceso);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null,"Registro finalizado satisfactoriamente");
            }
            
             // Inicializar componentes y configurar eventos después de crear el primer usuario
        initComponentes();
        configurarEventos();
        setVisible(true); // Mostrar la ventana de login
        VerificacionCorreo = false;
      
    }
    
    private void VerificarCorreo(String CorreoElectronico){
            int Codigo;
        // Configuración de las propiedades del servidor SMTP
        Properties props = new Properties();
        System.setProperty("https.protocols", "TLSv1.2");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor SMTP
        props.put("mail.smtp.port", "587"); // Puerto SMTP
        props.put("mail.smtp.auth", "true"); // Autenticación requerida
        props.put("mail.smtp.starttls.enable", "true"); // Habilitar STARTTLS

        // Credenciales del correo emisor
        final String usuario = "amayaalvarezjuanmanuel@gmail.com"; // Tu correo
        final String contraseña = "akuzkklnchndnfuj"; // Tu contraseña

        // Crear una sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, contraseña);
            }
        });

        try {
    // Crear el mensaje
    Message mensaje = new MimeMessage(session);
    mensaje.setFrom(new InternetAddress(usuario)); // Dirección del remitente
    mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CorreoElectronico)); // Destinatario
    mensaje.setSubject("Codigo de verificacion"); // Asunto
    Random numR = new Random();
    Codigo = numR.nextInt(1000000);
    
    mensaje.setText("Este es el codigo de verificacion para la creacion de su cuenta en la plataforma Inventails, por su seguridad por favor no lo comparta con nadie mas"
    + " "+Codigo); // Contenido del mensaje

    // Enviar el mensaje
    Transport.send(mensaje);

    verificar(Codigo);
        } catch (MessagingException e) {
            // Verificar si la causa subyacente es UnknownHostException
            Throwable causa = e.getCause();
            if (causa instanceof java.net.UnknownHostException) {
                JOptionPane.showMessageDialog(null, "Error: No hay conexión a Internet", "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Error al enviar el correo", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void verificar(int Codigo){
        int codigo_Ingresado;
        while (true){
        try {
        String ci = JOptionPane.showInputDialog("Ingrese el codigo enviado a su correo electronico");
        
        if(ci == null || ci.trim().isEmpty()){
            JOptionPane.showMessageDialog(null,"Por favor debe ingresar un codigo de verificacion");
            continue;
        }
        
        codigo_Ingresado = Integer.parseInt(ci.trim());
        // Si la conversión fue exitosa, salir del bucle
        break;
        }
        catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Debe ingresar el codigo con el formato correcto", "Error", JOptionPane.ERROR_MESSAGE);
                                }
        }
        if(codigo_Ingresado == Codigo){
            JOptionPane.showMessageDialog(null, "Su correo ha sido verificado exitosamente :) ");
            VerificacionCorreo = true;
        }
        else
            JOptionPane.showMessageDialog(null, "Su correo no ha podido verificarse", "Error", JOptionPane.WARNING_MESSAGE);
    }
    
    private void initComponentes() {
        mainPanel = new PanelConImagen();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);
        crearPanelSuperior();
        crearPanelInferior();
    }
    
    private void crearPanelSuperior() {
      JPanel panelSuperior = new JPanel(new GridLayout(0, 1,10,10));
        panelSuperior.setOpaque(false);
        // Limpiar componentes previos para evitar superposición
        panelSuperior.removeAll();

         // Crear y centrar los JLabel
        JLabel espacio1 = new JLabel("      ");
        espacio1.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(espacio1);

        JLabel usuarioLabel = new JLabel("Ingrese su usuario: ");
        usuarioLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(usuarioLabel);

        panelSuperior.add(campoUsuario);

        JLabel contraseñaLabel = new JLabel("Ingrese su contraseña: ");
        contraseñaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panelSuperior.add(contraseñaLabel);

        panelSuperior.add(campoContraseña);
        // Solo mostrar el botón de inicio de sesión si no hay ingreso
        if (!Ingreso) {
            panelSuperior.add(new JLabel()); // Espacio vacío
            panelSuperior.add(IniciarSesion);
        }
        if(Ingreso == true){
         panelSuperior.remove(new JLabel());
         panelSuperior.remove(IniciarSesion);
         campoUsuario.setText("");       // Limpia el campo de usuario
         campoContraseña.setText("");    // Limpia la contraseña
         
        }
            // Actualizar la interfaz
            panelSuperior.revalidate();
            panelSuperior.repaint();
            // Eliminar el panel superior anterior y añadir el nuevo
            mainPanel.removeAll(); // Limpiar todo el mainPanel
            mainPanel.add(panelSuperior, BorderLayout.NORTH);
    }
    
     private void crearPanelInferior(){
        JPanel panelInferior = new JPanel(new GridLayout(4, 4));
        panelInferior.setOpaque(false);
        // Limpiar componentes previos para evitar superposición
        panelInferior.removeAll();
        
        if (Ingreso) {
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            
            panelInferior.add(new JLabel());
            panelInferior.add(AccesoAlSistema);
            panelInferior.add(CerrarSesion);
            panelInferior.add(new JLabel());
           
            
            panelInferior.add(new JLabel());
            if("Administrador".equals(NivelAcceso)){
            panelInferior.add(AgregarUsuario);
            panelInferior.add(CambiarPermisos);}
            else{
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            }
            panelInferior.add(new JLabel());
            
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
            panelInferior.add(new JLabel());
        }

            mainPanel.add(panelInferior, BorderLayout.CENTER);
     }
    
    // Clase interna para el panel con imagen de fondo
    private class PanelConImagen extends JPanel {
        private final Image imagenFondo;

        public PanelConImagen() {
            // Cargar la imagen
            imagenFondo = new ImageIcon("C:\\Users\\amaya\\Downloads\\Inventails\\JavaApplication10\\src\\LogotipoLogin.png").getImage();
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
    
    private void configurarEventos() {
    IniciarSesion.addActionListener(e ->  {try {
        validarLogin();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
});
    AgregarUsuario.addActionListener(e ->  {try {
        AgregarNuevoUsuario();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
});
    CambiarPermisos.addActionListener(e ->  {try {
        CambiarPermisosUsuarios();
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
});
   
    AccesoAlSistema.addActionListener(e -> {
     this.setVisible(false); // Oculta la ventana de login

     JOptionPane.showMessageDialog(null, "Recuerde seleccionar un tipo de animal antes de cualquier operación");

     Ventana ventana = new Ventana(NivelAcceso,  IDUsuario);
     ventana.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosed(WindowEvent e) {
             LimpiarLogin();
             setVisible(true); }});
                });
    
    CerrarSesion.addActionListener(e -> MensajeDepedida());
    }
    
    private void MensajeDepedida(){
            // Mensaje de despedida
          JOptionPane.showMessageDialog(this, "Sesión cerrada. ¡Hasta luego! 👋", "Cerrar sesión", JOptionPane.INFORMATION_MESSAGE);
          LimpiarLogin();
    }
    
    public void LimpiarLogin(){
    
     Ingreso = false;               // Restablece el estado de sesión
          NivelAcceso = null;            // Limpia el nivel de acceso
          IDUsuario = 0;                 // Reinicia el ID
          campoUsuario.setText("");       // Limpia el campo de usuario
          campoContraseña.setText("");    // Limpia la contraseña

          // Limpia y reconstruye los paneles desde cero
          mainPanel.removeAll();

          // Vuelve a cargar los paneles (como al iniciar el programa)
          crearPanelSuperior();          // Muestra campos de login + botón "Iniciar sesión"
          crearPanelInferior();          // Oculta los botones de admin
          mainPanel.revalidate();
          mainPanel.repaint();
    }
      
    private void AgregarNuevoUsuario() throws SQLException{
    if(!"Administrador".equals(NivelAcceso)){
        JOptionPane.showMessageDialog(null,"Actualmente no tiene los permisos necesarios para realizar esta accion","Bloqueo",JOptionPane.WARNING_MESSAGE);
        return;}
    boolean registro = false;
    VerificacionCorreo = false;
    String newUsuario = null, tipoPermiso = null, p1 = null,p2 = null, PasswordBC = null, correo2, dominio2, CorreoElectronico = null;
    JTextField campousuario = new JTextField();
    JTextField campocorreo2 = new JTextField();
    JPasswordField PrimerIntentoP = new JPasswordField();
    JPasswordField SegundoIntentoP = new JPasswordField();
    JComboBox<String> comboboxDominiosElectronicos2 = new JComboBox<>(DominiosElectronicos);
    JComboBox<String> JcomboBoxPermisos = null;//inicializamos las opciones de tipo de acceso
    
    JcomboBoxPermisos =  new JComboBox<>(PermisosNewU);
    
    while(!registro){
        JPanel panelregistro = new JPanel(new GridLayout(5, 3));
        panelregistro.add(new JLabel("Ingrese el usuario"));
        panelregistro.add(new JLabel(""));
        panelregistro.add(campousuario);
        panelregistro.add(new JLabel("Ingrese el correo electronico: "));
        panelregistro.add(campocorreo2);
        panelregistro.add(comboboxDominiosElectronicos2);
        panelregistro.add(new JLabel("Escoja el tipo permiso"));
        panelregistro.add(new JLabel(""));
        panelregistro.add(JcomboBoxPermisos);
        panelregistro.add(new JLabel("Ingresa la contraseña"));
        panelregistro.add(new JLabel(""));
        panelregistro.add(PrimerIntentoP);
        panelregistro.add(new JLabel("Confirme la contraseña"));
        panelregistro.add(new JLabel(""));
        panelregistro.add(SegundoIntentoP);
        
         int resultadoregistro = JOptionPane.showConfirmDialog(
            null,
            panelregistro,
            "Caracterize al nuevo usuario",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
         
          if (resultadoregistro != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Registro de nuevo usuario cancelado con exito", "Error", JOptionPane.WARNING_MESSAGE);
            return;}
          
        newUsuario = campousuario.getText();
        
        if(newUsuario == null ||newUsuario.trim().isEmpty()){
        JOptionPane.showMessageDialog(null, "El campo de usuario es obligatorio, por favor rellenelo", "Error", JOptionPane.WARNING_MESSAGE);
        continue;}
        
         // Consulta a la base de datos para evitar usuarios duplicados
        String query = "select count(*) from usuarios where Usuario = ? ";
        int coincidencias = 0;
        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, newUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
            coincidencias = rs.getInt("count(*)");
            }
        }catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al verificar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        if(coincidencias != 0){
        JOptionPane.showMessageDialog(null, "Por favor ingrese otro nombre de usuario, el anterior ya se encuentra en uso", "Error", JOptionPane.WARNING_MESSAGE);
        continue;
        }
        
        
        
        tipoPermiso = (String) JcomboBoxPermisos.getSelectedItem();
        
        p1 = new String(PrimerIntentoP.getPassword());
        p2 = new String(SegundoIntentoP.getPassword());
        
         if(p2.trim().isEmpty() || p1 == null || p1.trim().isEmpty() || p2 == null){
         JOptionPane.showMessageDialog(null,"Debe ingresar ambos campos antes de proceder");
         continue;}
         
        if(p1.equals(p2)& p2.equals(p1)){
        PasswordBC = BCrypt.withDefaults().hashToString(12, p2.toCharArray());}
        
        
        else{
        JOptionPane.showMessageDialog(null,"Las contraseñas no coinciden, intentelo de nuevo");
        continue;}
        
        correo2 = campocorreo2.getText();
        
        if(campocorreo2.getText().trim().isEmpty() || campocorreo2 == null){
        JOptionPane.showMessageDialog(null,"Es necesario que digite un correo electronico(sin dominio) ");
        continue;
        }
        
        dominio2 = (String) comboboxDominiosElectronicos2.getSelectedItem();
        
        if(dominio2.trim().isEmpty() || dominio2 == null){
        JOptionPane.showMessageDialog(null,"Es necesario escoger un dominio electronico antes de continuar");
        continue;
        }
        
        CorreoElectronico = correo2 + dominio2;
        VerificarCorreo(CorreoElectronico);
       
        registro = true;
        
       }

       // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO usuarios (Usuario, Correo_Electronico, VerificacionCorreo, Password, nivelPermiso)"+
                         " values (?,?,?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = conexion.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setString(1, newUsuario); 
            ps.setString(2, CorreoElectronico);
            ps.setString(3, VerificacionCorreo.toString());
            ps.setString(4, PasswordBC);
            ps.setString(5, tipoPermiso);
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null,"Registro finalizado satisfactoriamente");
            }
            
            AuditoriaCreacionUsuarios(newUsuario);
    
    
    }
    
    private void AuditoriaCreacionUsuarios(String newUsuario) throws SQLException{
        
    String query = "SELECT id FROM usuarios WHERE usuario = ?";
    int IDNewUsuario = 0;
    
    try (PreparedStatement stmt = conexion.prepareStatement(query)) {
        stmt.setString(1, newUsuario);
        ResultSet rs = stmt.executeQuery();
        
        // Verificar si hay resultados ANTES de obtener el ID
        if (rs.next()) {
            IDNewUsuario = rs.getInt("id");
        } else {
            // Manejar error: usuario no encontrado
            JOptionPane.showMessageDialog(null, "Error: Usuario recién creado no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (SQLException ex) {
        // Manejar excepción adecuadamente
        throw ex;
    }
    
            
    LocalDateTime FechaCreacion = LocalDateTime.now();
    // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO CreacionUsuarios (ID_Usuario, ID_CreadoPor, FechaCreacion)"+
                         " values (?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = conexion.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setInt(1, IDNewUsuario); 
            ps.setInt(2, IDUsuario);
            ps.setString(3, FechaCreacion.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
    }
    
    private void CambiarPermisosUsuarios() throws SQLException{
        if(!"Administrador".equals(NivelAcceso)){
        JOptionPane.showMessageDialog(null,"Actualmente no posee autorizacion para llevar este proceso a cabo","Error",JOptionPane.WARNING_MESSAGE);
        return;
        }
        
        boolean estadoP = false;
        int idConsultado = 0;
        String UsuarioAcambiar = null;
        String NuevoNivel = null;
        String nivelActual = null;
        JComboBox<String> newPermisos = null;
        JTextField usuarioaeditar = new JTextField();

        
        newPermisos = new JComboBox<>(Permisos);
        JPanel editarpermiso = new JPanel(new GridLayout(2, 2));
        editarpermiso.add(new JLabel("Ingrese el usuario: "));
        editarpermiso.add(usuarioaeditar);
        editarpermiso.add(new JLabel("Seleccione el nuevo tipo de permiso: "));
        editarpermiso.add(newPermisos);
        
        while (!estadoP){
        int resultadocambioP = JOptionPane.showConfirmDialog(
            null,
            editarpermiso,
            "Ingrese el usuario a editar",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (resultadocambioP != JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, "Cambio de rol cancelado satisfactoriamente", "Error", JOptionPane.WARNING_MESSAGE);
           return;
        }
        UsuarioAcambiar=usuarioaeditar.getText();
        if (UsuarioAcambiar == null || UsuarioAcambiar.trim().isEmpty()){
        JOptionPane.showMessageDialog(null, "Es obligatorio ingresar el usuario del cual se quieren cambiar sus permisos", "Error", JOptionPane.WARNING_MESSAGE);
        continue;
        }
        
        // Consulta previa
            String sqlUsuario = "select id, nivelPermiso from usuarios where usuario = ? ";
            PreparedStatement ps = conexion.prepareStatement(sqlUsuario);
            ps.setString(1, UsuarioAcambiar);
                 
                ResultSet rs = ps.executeQuery();
     
                if (rs.next()) {
                    idConsultado = rs.getInt("id");
                     nivelActual = rs.getString("nivelPermiso");
                } else {
                    JOptionPane.showMessageDialog(null,"El usuario especificado no se encuentra registrado", "error", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            
            
        //verifica que el usuario no autocambie su rol, y da un mensaje personalizado si es el usuario fundador quien lo intenta, se asume que el fundador tiene ID = 1   
        if(idConsultado == IDUsuario){
        JOptionPane.showMessageDialog(null, "Error no puedes cambiar tu propio nivel de acesso al sistema","error",JOptionPane.WARNING_MESSAGE);
        if(idConsultado == 1){
         JOptionPane.showMessageDialog(null,"Estimado fundador por razones de seguridad el equipo de Inventails no permite su autocambio en el nivel de acceso al sistema",
         "error",JOptionPane.WARNING_MESSAGE);
        }
        continue;
        }
        
        //verifica que un usuario no cambie el rol del usuario fundador, osea el primero que se registro
        if(idConsultado == 1 & IDUsuario != 1){
        JOptionPane.showMessageDialog(null, "ERROR!!!! no puedes cambiar los permisos de acceso del fundador","ERROR",JOptionPane.ERROR_MESSAGE);
        return;
        }
        
        //verifica que el nuevo nivel de acceso sea diferente al actual para el usuario especificado
        NuevoNivel = (String) newPermisos.getSelectedItem();
        if(NuevoNivel.equals(nivelActual) ){
        JOptionPane.showMessageDialog(null,"El usuario especificado ya tiene el nivel de acceso: "+nivelActual,"Error",JOptionPane.WARNING_MESSAGE);
        continue;
        }
        estadoP = true;
        }
        
        String sql = "UPDATE usuarios SET nivelPermiso = ? WHERE id = ?";
          try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            // Valores para el UPDATE
            pstmt.setString(1, NuevoNivel ); // Cambiar 'nivelpermiso' 
            pstmt.setInt(2, idConsultado); // Actualizar el registro con idConsultado
            
            int filasInsertadas = pstmt.executeUpdate();
        if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(null,"Cambio en el nivel de acceso registrado exitosamente");
        } else {
            JOptionPane.showMessageDialog(null,"Lo sentimos no ha sido posible registrar el cambio de nivel de acceso","error", JOptionPane.ERROR_MESSAGE);}
          }
       AuditoriaModificacionPermisos(idConsultado, nivelActual, NuevoNivel);
    }
    
    private void AuditoriaModificacionPermisos(int idConsultado, String nivelActual, String NuevoNivel) throws SQLException{
     LocalDateTime FechaModificacion = LocalDateTime.now();
    // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO ModificacionAcceso (ID_UsuarioAfectado, ID_ModificadoPor, NivelAccesoPrevio, NivelAccesoNuevo, FechaModificacion)"+
                         " values (?,?,?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = conexion.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            
            ps.setInt(1, idConsultado); 
            ps.setInt(2, IDUsuario);
            ps.setString(3, nivelActual);
            ps.setString(4, NuevoNivel);
            ps.setString(5, FechaModificacion.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
    }
    
    private void validarLogin() throws SQLException {
        String usuario = campoUsuario.getText();
        String contraseña = new String(campoContraseña.getPassword());

        // Consulta a la base de datos para validar credenciales
        String query = "SELECT id, password, nivelPermiso FROM usuarios WHERE usuario = ? ";
        try (PreparedStatement stmt = conexion.prepareStatement(query)) {
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
              String contraseñaEncriptadaBD = rs.getString("password"); // Contraseña hasheada
              // Verifica si la contraseña coincide con el hash de la BD
            if (BCrypt.verifyer().verify(contraseña.toCharArray(), contraseñaEncriptadaBD).verified) {
                IDUsuario=rs.getInt("id");
                NivelAcceso = rs.getString("nivelPermiso");
                boolean acceso = false;
                while(!acceso){
                if("Bloqueado".equals(NivelAcceso)){
                   JOptionPane.showMessageDialog(null,"Tu cuenta ha sido bloqueada, contacta al administrador si crees que se trata de un error","Bloqueo",JOptionPane.ERROR_MESSAGE);
                   LimpiarLogin();
                   return;
                   
                }
                acceso = true;
                
                }
                Ingreso = true;
                crearPanelSuperior(); // Actualizar panel superior
                crearPanelInferior(); // Actualizar panel inferior
                mainPanel.revalidate(); // Forzar actualización
                mainPanel.repaint();
                
                JOptionPane.showMessageDialog(this, "Bienvenido, " + usuario + "!");
                AuditoriaAsistencia();
                
            }else {
                  JOptionPane.showMessageDialog(this, "Contraseña incorrecta", "Error: err001", JOptionPane.ERROR_MESSAGE);      
                        }
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al validar credenciales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void AuditoriaAsistencia() throws SQLException {
        LocalDateTime HoraAsistencia = LocalDateTime.now();
        // Consulta SQL correcta con marcadores de parámetros
            String sql = "INSERT INTO Asistencia (ID_Usuario, Accion, FechaAccion)"+
                         " values (?,?,?)";
                         
        
            // Preparar el statement
            PreparedStatement ps = conexion.prepareStatement(sql);
        
            // Asignar valores a los marcadores de parámetros
            String accion = "Autenticacion";
            ps.setInt(1, IDUsuario); 
            ps.setString(2, accion);
            ps.setString(3, HoraAsistencia.toString());
        
            // Ejecutar la inserción
            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas <= 0) {
                System.out.println("Auditoria fallo");
            }
        
     }
}

