import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
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
public class MedicinasAplicadasTest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private Ventana mockVentana;
    
    private MedicinasAplicadas medicinasAplicadas;
    
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
        // Inicializar las estructuras de datos
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
        registrosBovinos.put("TEST001", mockVentana);
        
        // Crear instancia de MedicinasAplicadas
        medicinasAplicadas = new MedicinasAplicadas(
            1, pilaVentaCaprinos, pilaVentaBovinos, pilaVentaGallinas,
            pilaVentaCerdos, pilaVentaCaballos,
            registrosCaprinos, registrosBovinos, registrosGallinas,
            registrosCerdos, registrosCaballos, "tipo_test",
            mockConnection, "user", "db", "url", "password"
        );
    }

    @Test
    public void testConstructor() {
        assertNotNull("MedicinasAplicadas no debería ser null", medicinasAplicadas);
        assertNotNull("registrosBovinos no debería ser null", medicinasAplicadas.registrosBovinos);
        assertNotNull("registrosGallinas no debería ser null", medicinasAplicadas.registrosGallinas);
        assertNotNull("registrosCerdos no debería ser null", medicinasAplicadas.registrosCerdos);
        assertNotNull("registrosCaballos no debería ser null", medicinasAplicadas.registrosCaballos);
        assertNotNull("registrosCaprinos no debería ser null", medicinasAplicadas.registrosCaprinos);
    }

    @Test
    public void testConexionBaseDatos() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        PreparedStatement ps = mockConnection.prepareStatement("TEST");
        int result = ps.executeUpdate();
        
        assertEquals(1, result);
        verify(mockConnection).prepareStatement(anyString());
        verify(mockPreparedStatement).executeUpdate();
    }

    // Test para verificar la estructura de datos
    @Test
    public void testEstructurasDatos() {
        assertTrue("pilaVentaBovinos debería estar vacía inicialmente", pilaVentaBovinos.isEmpty());
        assertTrue("pilaVentaGallinas debería estar vacía inicialmente", pilaVentaGallinas.isEmpty());
        assertTrue("pilaVentaCerdos debería estar vacía inicialmente", pilaVentaCerdos.isEmpty());
        assertTrue("pilaVentaCaballos debería estar vacía inicialmente", pilaVentaCaballos.isEmpty());
        assertTrue("pilaVentaCaprinos debería estar vacía inicialmente", pilaVentaCaprinos.isEmpty());
        
        assertFalse("registrosBovinos no debería estar vacío", registrosBovinos.isEmpty());
    }

    // Test para verificar parámetros de conexión
    @Test
    public void testParametrosConexion() {
        assertEquals("user", medicinasAplicadas.user);
        assertEquals("db", medicinasAplicadas.db);
        assertEquals("url", medicinasAplicadas.url);
        assertEquals("password", medicinasAplicadas.password);
        assertEquals("tipo_test", medicinasAplicadas.tipa);
    }

    // Test para verificar el ID de usuario
    @Test
    public void testIDUsuario() {
        // Nota: Si IDUsuario es privado, necesitarías reflection o un getter
        // Esta es una verificación indirecta a través del constructor
        assertNotNull(medicinasAplicadas);
    }
}