import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.sql.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class) 
public class LoginTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private PreparedStatement mockPreparedStatement2;

    @Mock
    private PreparedStatement mockPreparedStatement3;

    @Mock
    private ResultSet mockResultSet;

    private Login login;

    @Before
    public void setUp() throws Exception {
        // Configurar los mocks para evitar el NullPointerException en el constructor
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Configurar el ResultSet para VerificarNumeroCuentas
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("COUNT(*)")).thenReturn(1); // Simular que hay usuarios
        
        // Mock para auditorías
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Crear la instancia de Login después de configurar los mocks
        login = new Login(mockConnection);
        
        // Usar reflexión para acceder a campos privados necesarios para las pruebas
        Field numUsuariosField = Login.class.getDeclaredField("NumUsuarios");
        numUsuariosField.setAccessible(true);
        numUsuariosField.set(login, 1); // Simular que hay usuarios existentes
    }

    @Test
    public void testVerificarNumeroCuentas_CuandoHayUsuarios() throws Exception {
        // Configurar
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("COUNT(*)")).thenReturn(5);

        // Ejecutar método privado usando reflexión
        Method method = Login.class.getDeclaredMethod("VerificarNumeroCuentas", Connection.class);
        method.setAccessible(true);
        method.invoke(login, mockConnection);

        // Verificar
        Field numUsuariosField = Login.class.getDeclaredField("NumUsuarios");
        numUsuariosField.setAccessible(true);
        int numUsuarios = (int) numUsuariosField.get(login);
        
        Assert.assertEquals(5, numUsuarios);
    }

    @Test
    public void testValidarLogin_CredencialesCorrectas() throws Exception {
        // Configurar
        String usuario = "testUser";
        String password = "testPass";
        String hashPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        // Mockear componentes UI usando reflexión
        Field campoUsuarioField = Login.class.getDeclaredField("campoUsuario");
        campoUsuarioField.setAccessible(true);
        JTextField campoUsuario = (JTextField) campoUsuarioField.get(login);
        campoUsuario.setText(usuario);

        Field campoContraseñaField = Login.class.getDeclaredField("campoContraseña");
        campoContraseñaField.setAccessible(true);
        JPasswordField campoContraseña = (JPasswordField) campoContraseñaField.get(login);
        campoContraseña.setText(password);

        // Mockear resultado de base de datos para validarLogin
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement2);
        when(mockPreparedStatement2.executeQuery()).thenReturn(mockResultSet);
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashPassword);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getInt("nivelPermiso")).thenReturn(1);

        // Mock específico para la auditoría de asistencia
        when(mockConnection.prepareStatement("INSERT INTO Asistencia (ID_Usuario, Accion, FechaAccion) values (?,?,?)"))
            .thenReturn(mockPreparedStatement3);
        when(mockPreparedStatement3.executeUpdate()).thenReturn(1);

        // Ejecutar método privado
        Method method = Login.class.getDeclaredMethod("validarLogin");
        method.setAccessible(true);
        method.invoke(login);

        // Verificar estado interno
        Field ingresoField = Login.class.getDeclaredField("Ingreso");
        ingresoField.setAccessible(true);
        Boolean ingreso = (Boolean) ingresoField.get(login);

        Field nivelAccesoField = Login.class.getDeclaredField("NivelAcceso");
        nivelAccesoField.setAccessible(true);
        int nivelAcceso = (int) nivelAccesoField.get(login);

        Field idUsuarioField = Login.class.getDeclaredField("IDUsuario");
        idUsuarioField.setAccessible(true);
        int idUsuario = (int) idUsuarioField.get(login);

        Assert.assertTrue("El ingreso debería ser true", ingreso);
        Assert.assertEquals("El nivel de acceso debería ser 1", 1, nivelAcceso);
        Assert.assertEquals("El ID de usuario debería ser 1", 1, idUsuario);
    }

    @Test
    public void testValidarLogin_UsuarioNoEncontrado() throws Exception {
        // Configurar
        String usuario = "usuarioInexistente";
        String password = "password";

        Field campoUsuarioField = Login.class.getDeclaredField("campoUsuario");
        campoUsuarioField.setAccessible(true);
        JTextField campoUsuario = (JTextField) campoUsuarioField.get(login);
        campoUsuario.setText(usuario);

        Field campoContraseñaField = Login.class.getDeclaredField("campoContraseña");
        campoContraseñaField.setAccessible(true);
        JPasswordField campoContraseña = (JPasswordField) campoContraseñaField.get(login);
        campoContraseña.setText(password);

        // Mockear que no se encuentra el usuario
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement2);
        when(mockPreparedStatement2.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Ejecutar
        Method method = Login.class.getDeclaredMethod("validarLogin");
        method.setAccessible(true);
        method.invoke(login);

        // Verificar que no hay ingreso
        Field ingresoField = Login.class.getDeclaredField("Ingreso");
        ingresoField.setAccessible(true);
        Boolean ingreso = (Boolean) ingresoField.get(login);

        Assert.assertFalse("El ingreso debería ser false cuando el usuario no existe", ingreso);
    }

    @Test
    public void testValidarLogin_UsuarioBloqueado() throws Exception {
        // Configurar
        String usuario = "usuarioBloqueado";
        String password = "testPass";
        String hashPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Field campoUsuarioField = Login.class.getDeclaredField("campoUsuario");
        campoUsuarioField.setAccessible(true);
        JTextField campoUsuario = (JTextField) campoUsuarioField.get(login);
        campoUsuario.setText(usuario);

        Field campoContraseñaField = Login.class.getDeclaredField("campoContraseña");
        campoContraseñaField.setAccessible(true);
        JPasswordField campoContraseña = (JPasswordField) campoContraseñaField.get(login);
        campoContraseña.setText(password);

        // Mockear resultado de base de datos
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement2);
        when(mockPreparedStatement2.executeQuery()).thenReturn(mockResultSet);
        
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(hashPassword);
        when(mockResultSet.getInt("id")).thenReturn(2);
        when(mockResultSet.getInt("nivelPermiso")).thenReturn(3); // Usuario bloqueado

        // Mock para la auditoría
        when(mockConnection.prepareStatement("INSERT INTO Asistencia (ID_Usuario, Accion, FechaAccion) values (?,?,?)"))
            .thenReturn(mockPreparedStatement3);
        when(mockPreparedStatement3.executeUpdate()).thenReturn(1);

        // Ejecutar
        Method method = Login.class.getDeclaredMethod("validarLogin");
        method.setAccessible(true);
        method.invoke(login);

        // Verificar - después de LimpiarLogin(), el estado debería estar limpio
        Field ingresoField = Login.class.getDeclaredField("Ingreso");
        ingresoField.setAccessible(true);
        Boolean ingreso = (Boolean) ingresoField.get(login);

        Field nivelAccesoField = Login.class.getDeclaredField("NivelAcceso");
        nivelAccesoField.setAccessible(true);
        int nivelAcceso = (int) nivelAccesoField.get(login);

        Field idUsuarioField = Login.class.getDeclaredField("IDUsuario");
        idUsuarioField.setAccessible(true);
        int idUsuario = (int) idUsuarioField.get(login);

        // Después de LimpiarLogin(), todos los campos deberían estar en estado inicial
        Assert.assertFalse("El ingreso debería ser false para usuario bloqueado", ingreso);
        Assert.assertEquals("El nivel de acceso debería ser 0 después de limpiar", 0, nivelAcceso);
        Assert.assertEquals("El ID de usuario debería ser 0 después de limpiar", 0, idUsuario);
    }

    @Test
    public void testLimpiarLogin() throws Exception {
        // Configurar estado previo usando reflexión
        Field ingresoField = Login.class.getDeclaredField("Ingreso");
        ingresoField.setAccessible(true);
        ingresoField.set(login, true);

        Field nivelAccesoField = Login.class.getDeclaredField("NivelAcceso");
        nivelAccesoField.setAccessible(true);
        nivelAccesoField.set(login, 2);

        Field idUsuarioField = Login.class.getDeclaredField("IDUsuario");
        idUsuarioField.setAccessible(true);
        idUsuarioField.set(login, 5);

        // Configurar campos de UI
        Field campoUsuarioField = Login.class.getDeclaredField("campoUsuario");
        campoUsuarioField.setAccessible(true);
        JTextField campoUsuario = (JTextField) campoUsuarioField.get(login);
        campoUsuario.setText("usuarioPrueba");

        Field campoContraseñaField = Login.class.getDeclaredField("campoContraseña");
        campoContraseñaField.setAccessible(true);
        JPasswordField campoContraseña = (JPasswordField) campoContraseñaField.get(login);
        campoContraseña.setText("contraseñaPrueba");

        // Ejecutar
        Method method = Login.class.getDeclaredMethod("LimpiarLogin");
        method.setAccessible(true);
        method.invoke(login);

        // Verificar
        Assert.assertFalse("Ingreso debería ser false", (Boolean) ingresoField.get(login));
        Assert.assertEquals("NivelAcceso debería ser 0", 0, nivelAccesoField.get(login));
        Assert.assertEquals("IDUsuario debería ser 0", 0, idUsuarioField.get(login));
        Assert.assertEquals("Campo usuario debería estar vacío", "", campoUsuario.getText());
        Assert.assertEquals("Campo contraseña debería estar vacío", "", new String(campoContraseña.getPassword()));
    }

    @After
    public void tearDown() {
        // Cerrar la ventana si está abierta
        if (login != null) {
            login.dispose();
        }
    }
}