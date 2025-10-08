import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EspaciosYestadosATest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Mock
    private ResultSetMetaData mockMetaData;
    
    @Mock
    private DriverManager mockDriverManager;
    
    private EspaciosYestadosA espaciosYestadosA;
    private final String testUrl = "jdbc:mysql://localhost:3306/";
    private final String testDb = "test_db";
    private final String testPassword = "test_password";
    private final String testUser = "test_user";

    @Before
    public void setUp() throws SQLException {
        // Configuración común para los mocks
        when(mockConnection.prepareStatement(
                anyString(), 
                eq(ResultSet.TYPE_SCROLL_INSENSITIVE), 
                eq(ResultSet.CONCUR_READ_ONLY)))
                .thenReturn(mockPreparedStatement);
    }

    @Test
    public void testConstructor() {
        // Given
        String expectedTipa = "test_tipa";
        
        // When
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, expectedTipa, testUser);
        
        // Then
        assertEquals(testUrl, espaciosYestadosA.url);
        assertEquals(testDb, espaciosYestadosA.db);
        assertEquals(testPassword, espaciosYestadosA.password);
        assertEquals(expectedTipa, espaciosYestadosA.tipa);
        assertEquals(testUser, espaciosYestadosA.user);
    }

    @Test
    public void testImprimirMatrizConTipoAnimalNulo() {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, null, testUser);
        
        // When - Then: No debería lanzar excepción, solo mostrar mensaje de error
        espaciosYestadosA.imprimirMatriz();
        // Verificamos que no hay excepciones
    }

    @Test
    public void testCrearMatrizUbicacionSinTipoAnimal() {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, null, testUser);
        
        // When
        JTable result = espaciosYestadosA.crearMatrizUbicacion();
        
        // Then
        assertNull("Debe retornar null cuando tipa es null", result);
    }

    @Test
    public void testCrearMatrizUbicacionSinDatos() throws SQLException {
        // Given
        String testTipa = "Perro";
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, testTipa, testUser);
        
        // Crear un spy para mockear la creación de conexión
        EspaciosYestadosA spyEspacios = spy(espaciosYestadosA);
        
        // Mockear la conexión interna
        doReturn(mockConnection).when(spyEspacios).crearMatrizUbicacionConConexion(any());
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(false); // No hay datos
        
        // When
        JTable result = spyEspacios.crearMatrizUbicacion();
        
        // Then
        assertNull("Debe retornar null cuando no hay datos", result);
        verify(mockPreparedStatement).setString(1, testTipa);
    }

    @Test
    public void testCrearMatrizUbicacionConDatos() throws SQLException {
        // Given
        String testTipa = "Gato";
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, testTipa, testUser);
        
        // Crear un spy
        EspaciosYestadosA spyEspacios = spy(espaciosYestadosA);
        doReturn(mockConnection).when(spyEspacios).crearMatrizUbicacionConConexion(any());
        
        // Configurar mocks para datos existentes
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.isBeforeFirst()).thenReturn(true); // Hay datos
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        when(mockMetaData.getColumnName(1)).thenReturn("id");
        when(mockMetaData.getColumnName(2)).thenReturn("nombre");
        when(mockMetaData.getColumnName(3)).thenReturn("tipo_animal");
        
        // Mockear el comportamiento de rsToArray
        Object[][] datosMock = {{1, "Felix", "Gato"}, {2, "Garfield", "Gato"}};
        doReturn(datosMock).when(spyEspacios).rsToArray(mockResultSet, 3);
        
        // When
        JTable result = spyEspacios.crearMatrizUbicacion();
        
        // Then
        assertNotNull("Debe retornar una tabla cuando hay datos", result);
        assertTrue(result.getModel() instanceof DefaultTableModel);
        verify(mockPreparedStatement).setString(1, testTipa);
    }

    @Test
    public void testCrearMatrizUbicacionConExcepcionSQL() throws SQLException {
        // Given
        String testTipa = "Pajaro";
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, testTipa, testUser);
        
        // Crear un spy
        EspaciosYestadosA spyEspacios = spy(espaciosYestadosA);
        
        // Simular excepción al crear la conexión
        doThrow(new SQLException("Error de conexión")).when(spyEspacios).crearMatrizUbicacionConConexion(any());
        
        // When
        JTable result = spyEspacios.crearMatrizUbicacion();
        
        // Then
        assertNull("Debe retornar null cuando hay excepción", result);
    }

    @Test
    public void testRsToArray() throws SQLException {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, "Test", testUser);
        
        // Configurar comportamiento del ResultSet mock
        when(mockResultSet.last()).thenReturn(true);
        when(mockResultSet.getRow()).thenReturn(2);
        when(mockResultSet.next()).thenReturn(true, true, false); // Dos filas
        when(mockResultSet.getObject(1)).thenReturn("Dato1", "Dato3");
        when(mockResultSet.getObject(2)).thenReturn("Dato2", "Dato4");
        
        // When
        Object[][] result = espaciosYestadosA.rsToArray(mockResultSet, 2);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
        assertEquals("Dato1", result[0][0]);
        assertEquals("Dato2", result[0][1]);
        assertEquals("Dato3", result[1][0]);
        assertEquals("Dato4", result[1][1]);
        
        // Verificar que se llamaron los métodos necesarios
        verify(mockResultSet).last();
        verify(mockResultSet).beforeFirst();
        verify(mockResultSet, times(3)).next(); // Dos filas + el false final
    }

    @Test
    public void testRsToArrayConResultSetVacio() throws SQLException {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, "Test", testUser);
        
        // Configurar ResultSet vacío
        when(mockResultSet.last()).thenReturn(false);
        when(mockResultSet.getRow()).thenReturn(0);
        when(mockResultSet.next()).thenReturn(false);
        
        // When
        Object[][] result = espaciosYestadosA.rsToArray(mockResultSet, 3);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.length);
        
        verify(mockResultSet).last();
        verify(mockResultSet).beforeFirst();
        verify(mockResultSet, times(1)).next();
    }

    @Test
    public void testRsToArrayConUnaFila() throws SQLException {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, "Test", testUser);
        
        // Configurar ResultSet con una fila
        when(mockResultSet.last()).thenReturn(true);
        when(mockResultSet.getRow()).thenReturn(1);
        when(mockResultSet.next()).thenReturn(true, false); // Una fila
        when(mockResultSet.getObject(1)).thenReturn("SoloDato1");
        when(mockResultSet.getObject(2)).thenReturn("SoloDato2");
        
        // When
        Object[][] result = espaciosYestadosA.rsToArray(mockResultSet, 2);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals(2, result[0].length);
        assertEquals("SoloDato1", result[0][0]);
        assertEquals("SoloDato2", result[0][1]);
    }

    @Test
    public void testEspaciosA() {
        // Given
        String testTipa = "Ave";
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, testTipa, testUser);
        
        // Crear spy para mockear imprimirMatriz
        EspaciosYestadosA spyEspacios = spy(espaciosYestadosA);
        doNothing().when(spyEspacios).imprimirMatriz();
        
        // When
        spyEspacios.EspaciosA();
        
        // Then
        verify(spyEspacios, times(1)).imprimirMatriz();
    }

    @Test
    public void testEspaciosAConTipoAnimalNulo() {
        // Given
        espaciosYestadosA = new EspaciosYestadosA(testUrl, testDb, testPassword, null, testUser);
        
        // Crear spy para mockear imprimirMatriz
        EspaciosYestadosA spyEspacios = spy(espaciosYestadosA);
        doNothing().when(spyEspacios).imprimirMatriz();
        
        // When
        spyEspacios.EspaciosA();
        
        // Then - No debería lanzar excepción
        verify(spyEspacios, times(1)).imprimirMatriz();
    }

    // Método helper para testing - NO modifica la clase original
    // Este método se usa solo en los tests para mockear la conexión
    private Connection mockCrearConexion() throws SQLException {
        return mockConnection;
    }
}   

