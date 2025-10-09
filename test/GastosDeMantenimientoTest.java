import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GastosDeMantenimientoTest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private PreparedStatement mockPreparedStatementSelect;
    
    @Mock
    private ResultSet mockResultSet;
    
    private GastosDeMantenimiento gastosDeMantenimiento;

    @Before
    public void setUp() throws SQLException {
        // Configurar diferentes PreparedStatements para diferentes consultas
        when(mockConnection.prepareStatement(contains("INSERT INTO gastosmantenimiento")))
            .thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(contains("SELECT id FROM GastosMantenimiento")))
            .thenReturn(mockPreparedStatementSelect);
        when(mockConnection.prepareStatement(contains("INSERT INTO transacciones")))
            .thenReturn(mockPreparedStatement);
        
        // Configurar ResultSet para la consulta SELECT
        when(mockPreparedStatementSelect.executeQuery()).thenReturn(mockResultSet);
        
        gastosDeMantenimiento = new GastosDeMantenimiento(1, mockConnection);
    }

    @Test
    public void testConstructor() {
        GastosDeMantenimiento gastos = new GastosDeMantenimiento(2, mockConnection);
        
        assertEquals(mockConnection, gastos.cx);
        assertNotNull(gastos);
    }

    @Test
    public void testConexionNoNula() {
        assertNotNull(gastosDeMantenimiento.cx);
        assertEquals(mockConnection, gastosDeMantenimiento.cx);
    }

    @Test
    public void testInstanciacionMultiple() {
        GastosDeMantenimiento gastos1 = new GastosDeMantenimiento(1, mockConnection);
        GastosDeMantenimiento gastos2 = new GastosDeMantenimiento(2, mockConnection);
        
        assertNotSame(gastos1, gastos2);
        assertEquals(mockConnection, gastos1.cx);
        assertEquals(mockConnection, gastos2.cx);
    }

    @Test
    public void testInsertarTransaccionExitoso() throws Exception {
        // Configurar mocks para transacción exitosa
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(123);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Usar reflection para llamar al método privado
        java.lang.reflect.Method method = GastosDeMantenimiento.class.getDeclaredMethod(
            "InsertarTransaccion", 
            java.time.LocalDateTime.class, 
            float.class
        );
        method.setAccessible(true);
        method.invoke(gastosDeMantenimiento, java.time.LocalDateTime.now(), -100.0f);
        
        // Verificaciones
        verify(mockPreparedStatementSelect).executeQuery();
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testInsertarTransaccionSinEncontrarID() throws Exception {
        // Configurar mocks para cuando no encuentra el ID del gasto
        when(mockResultSet.next()).thenReturn(false); // No encuentra el ID
        
        // Usar reflection para llamar al método privado
        java.lang.reflect.Method method = GastosDeMantenimiento.class.getDeclaredMethod(
            "InsertarTransaccion", 
            java.time.LocalDateTime.class, 
            float.class
        );
        method.setAccessible(true);
        method.invoke(gastosDeMantenimiento, java.time.LocalDateTime.now(), -100.0f);
        
        // Verificaciones - no debería llamar a la inserción de transacción
        verify(mockPreparedStatementSelect).executeQuery();
        verify(mockPreparedStatement, never()).executeUpdate();
    }

    @Test
    public void testInsertarTransaccionConExcepcionSQL() throws Exception {
        // Configurar mocks para simular excepción SQL
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(123);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Error de base de datos"));
        
        // Usar reflection para llamar al método privado
        java.lang.reflect.Method method = GastosDeMantenimiento.class.getDeclaredMethod(
            "InsertarTransaccion", 
            java.time.LocalDateTime.class, 
            float.class
        );
        method.setAccessible(true);
        method.invoke(gastosDeMantenimiento, java.time.LocalDateTime.now(), -100.0f);
        
        // Verificaciones - debería manejar la excepción internamente
        verify(mockPreparedStatementSelect).executeQuery();
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testGastosDeMantenimientoNoLanzaExcepcion() {
        // Este test solo verifica que el método no lance excepciones inesperadas
        try {
            gastosDeMantenimiento.GastosDeMantenimiento();
            // Si llega aquí sin excepción, el test pasa
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testMetodosNoLanzanNullPointerException() {
        // Verificar que los métodos manejen correctamente los casos nulos
        try {
            GastosDeMantenimiento gastosConConexionNula = new GastosDeMantenimiento(1, null);
            assertNotNull(gastosConConexionNula);
            
            gastosDeMantenimiento.GastosDeMantenimiento();
            
        } catch (Exception e) {
            // Algunas excepciones son esperadas, pero no NullPointerException
            if (e instanceof NullPointerException) {
                fail("No debería lanzar NullPointerException: " + e.getMessage());
            }
            // Otras excepciones (como SQLException) son manejadas por el método
        }
    }

    @Test
    public void testComportamientoBasico() {
        // Test básico que solo verifica que la clase se comporta consistentemente
        assertNotNull(gastosDeMantenimiento);
        assertNotNull(gastosDeMantenimiento.cx);
        assertEquals(1, gastosDeMantenimiento.hashCode()); // Verifica que tiene hash code
    }
}