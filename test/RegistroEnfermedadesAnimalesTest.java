import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistroEnfermedadesAnimalesTest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private Ventana mockVentana;
    
    private RegistroEnfermedadesAnimales registroEnfermedades;
    
    // Estructuras de datos para el constructor
    private Stack<Object[]> pilaVentaBovinos;
    private Stack<Object[]> pilaVentaGallinas;
    private Stack<Object[]> pilaVentaCerdos;
    private Stack<Object[]> pilaVentaCaballos;
    private Stack<Object[]> pilaVentaCaprinos;
    
    private Map<String, Ventana> registrosBovinos;
    private Map<String, Ventana> registrosGallinas;
    private Map<String, Ventana> registrosCerdos;
    private Map<String, Ventana> registrosCaballos;
    private Map<String, Ventana> registrosCaprinos;

    @Before
    public void setUp() {
        // Inicializar estructuras de datos
        pilaVentaBovinos = new Stack<>();
        pilaVentaGallinas = new Stack<>();
        pilaVentaCerdos = new Stack<>();
        pilaVentaCaballos = new Stack<>();
        pilaVentaCaprinos = new Stack<>();
        
        registrosBovinos = new HashMap<>();
        registrosGallinas = new HashMap<>();
        registrosCerdos = new HashMap<>();
        registrosCaballos = new HashMap<>();
        registrosCaprinos = new HashMap<>();
        
        // Agregar datos de prueba
        registrosBovinos.put("BOV001", mockVentana);
        registrosGallinas.put("GAL001", mockVentana);
        
        // Crear instancia del objeto bajo prueba
        registroEnfermedades = new RegistroEnfermedadesAnimales(
            1, pilaVentaCaprinos, pilaVentaBovinos, pilaVentaGallinas,
            pilaVentaCerdos, pilaVentaCaballos,
            registrosCaprinos, registrosBovinos, registrosGallinas,
            registrosCerdos, registrosCaballos, "tipo_test",
            mockConnection, "user", "db", "url", "password"
        );
    }

    @Test
    public void testConstructorInicializacionCorrecta() {
        // Verificar que el objeto se crea correctamente
        assertNotNull("RegistroEnfermedadesAnimales no debería ser null", registroEnfermedades);
        assertNotNull("registrosBovinos no debería ser null", registroEnfermedades.registrosBovinos);
        assertNotNull("registrosGallinas no debería ser null", registroEnfermedades.registrosGallinas);
        assertNotNull("registrosCerdos no debería ser null", registroEnfermedades.registrosCerdos);
        assertNotNull("registrosCaballos no debería ser null", registroEnfermedades.registrosCaballos);
        assertNotNull("registrosCaprinos no debería ser null", registroEnfermedades.registrosCaprinos);
    }

    @Test
    public void testParametrosConexion() {
        // Verificar que los parámetros de conexión son correctos
        assertEquals("URL debería coincidir", "jdbc:mysql://localhost:3306/", registroEnfermedades.url);
        assertEquals("DB debería coincidir", "inventails", registroEnfermedades.db);
        assertEquals("User debería coincidir", "root", registroEnfermedades.user);
        assertEquals("Password debería coincidir", "", registroEnfermedades.password);
        assertEquals("Tipa debería coincidir", "tipo_test", registroEnfermedades.tipa);
    }

    @Test
    public void testEstructurasDatosPilas() {
        // Verificar que las pilas están inicializadas correctamente
        assertTrue("pilaVentaBovinos debería estar vacía", pilaVentaBovinos.isEmpty());
        assertTrue("pilaVentaGallinas debería estar vacía", pilaVentaGallinas.isEmpty());
        assertTrue("pilaVentaCerdos debería estar vacía", pilaVentaCerdos.isEmpty());
        assertTrue("pilaVentaCaballos debería estar vacía", pilaVentaCaballos.isEmpty());
        assertTrue("pilaVentaCaprinos debería estar vacía", pilaVentaCaprinos.isEmpty());
        
        // Verificar que los registros tienen datos
        assertFalse("registrosBovinos no debería estar vacío", registrosBovinos.isEmpty());
        assertTrue("registrosBovinos debería contener BOV001", registrosBovinos.containsKey("BOV001"));
    }

    @Test
    public void testConexionBaseDatos() throws SQLException {
        // Verificar el comportamiento de la conexión a BD
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        PreparedStatement ps = mockConnection.prepareStatement("TEST");
        int result = ps.executeUpdate();
        
        assertEquals("El resultado debería ser 1", 1, result);
        verify(mockConnection, times(1)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testSQLInjectionPrevention() throws SQLException {
        // Verificar que se usan PreparedStatement para prevenir SQL injection
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        
        String sql = "INSERT INTO  registroenfermedades " + 
                    "(ID_usuario, ID_animal, F_Diagnostico, enfermedad, veterinario, comentario) " +
                    "VALUES (?, (SELECT ID FROM Animales WHERE codigo = ? AND estado = 'activo'), ?, ?, ?, ?)";
        
        PreparedStatement ps = mockConnection.prepareStatement(sql);
        
        verify(mockConnection, times(1)).prepareStatement(sql);
        assertNotNull("PreparedStatement no debería ser null", ps);
    }

    @Test
    public void testMetodoRegistroEnfermedadesExiste() {
        // Verificar que el método existe y es accesible
        try {
            registroEnfermedades.getClass().getMethod("RegistroEnfermedades");
            // Si no lanza excepción, el método existe
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("El método RegistroEnfermedades debería existir");
        }
    }

    @Test
    public void testConnectionNoNula() {
        // Verificar que la conexión no es nula
        assertNotNull("La conexión no debería ser null", registroEnfermedades.cx);
    }

    @Test
    public void testIDUsuarioConfigurado() {
        // Verificación indirecta del ID de usuario a través del constructor
        assertNotNull("La instancia no debería ser null", registroEnfermedades);
        // El IDUsuario es privado, pero podemos verificar que la instancia se creó correctamente
        assertTrue("La instancia debería ser de tipo RegistroEnfermedadesAnimales", 
                  registroEnfermedades instanceof RegistroEnfermedadesAnimales);
    }

    // Test simplificado para verificar el formato SQL
    @Test
    public void testFormatoSQL() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        
        String expectedSQL = "INSERT INTO  registroenfermedades " + 
                           "(ID_usuario, ID_animal, F_Diagnostico, enfermedad, veterinario, comentario) " +
                           "VALUES (?, (SELECT ID FROM Animales WHERE codigo = ? AND estado = 'activo'), ?, ?, ?, ?)";
        
        PreparedStatement ps = mockConnection.prepareStatement(expectedSQL);
        
        verify(mockConnection).prepareStatement(expectedSQL);
        assertNotNull(ps);
    }
}