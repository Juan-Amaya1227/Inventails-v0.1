import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExportarExcelTest {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    private ExportarExcel exportarExcel;

    @Before
    public void setUp() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        exportarExcel = new ExportarExcel("jdbc:mysql://localhost:3306/", "test_db", "test_user", "test_password", 1, mockConnection);
    }

    @Test
    public void testConstructor() {
        ExportarExcel excel = new ExportarExcel("url", "db", "user", "pass", 2, mockConnection);
        
        assertEquals("url", excel.url);
        assertEquals("db", excel.db);
        assertEquals("user", excel.user);
        assertEquals("pass", excel.password);
        assertEquals(2, excel.IDUsuario);
        assertEquals(mockConnection, excel.cx);
        assertNotNull(excel.desktopPath);
    }

    @Test
    public void testDesktopPath() {
        String expectedPath = System.getProperty("user.home") + "/Desktop";
        assertEquals(expectedPath, exportarExcel.desktopPath);
    }

    @Test
    public void testCompraYVentaNoLanzaExcepcion() {
        try {
            exportarExcel.Compra_y_venta();
            // Si llega aquí, el test pasa
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testReportePedigreeNoLanzaExcepcion() {
        try {
            exportarExcel.ReportePedigree();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testReporteEngordeNoLanzaExcepcion() {
        try {
            exportarExcel.ReporteEngorde();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testMedicinasAplicadasNoLanzaExcepcion() {
        try {
            exportarExcel.MedicinasAplicadas();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testReporteDefuncionesNoLanzaExcepcion() {
        try {
            exportarExcel.ReporteDefunciones();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testReporteTransaccionesTotalesNoLanzaExcepcion() {
        try {
            exportarExcel.ReporteTransaccionesTotales();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testSeleccionartipodereporteNoLanzaExcepcion() {
        try {
            exportarExcel.seleccionartipodereporte();
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }

    @Test
    public void testInstanciacionMultiple() {
        ExportarExcel excel1 = new ExportarExcel("url1", "db1", "user1", "pass1", 1, mockConnection);
        ExportarExcel excel2 = new ExportarExcel("url2", "db2", "user2", "pass2", 2, mockConnection);
        
        assertNotSame(excel1, excel2);
        assertEquals("url1", excel1.url);
        assertEquals("url2", excel2.url);
    }

    @Test
    public void testCamposPublicos() {
        assertNotNull(exportarExcel.url);
        assertNotNull(exportarExcel.db);
        assertNotNull(exportarExcel.user);
        assertNotNull(exportarExcel.password);
        assertNotNull(exportarExcel.cx);
        assertEquals(1, exportarExcel.IDUsuario);
    }
}