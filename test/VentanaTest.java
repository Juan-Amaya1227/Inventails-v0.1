import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.Stack;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.verification.VerificationMode;

@RunWith(MockitoJUnitRunner.class)
public class VentanaTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private Ventana ventana;

    @Before
    public void setUp() throws Exception {
        // Configurar mocks básicos
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Crear instancia de Ventana con nivel de acceso y ID de usuario
        ventana = new Ventana(1, 1); // NivelAcceso = 1 (Administrador), IDUsuario = 1
    }

    @Test
    public void testConstructor() throws Exception {
        // Verificar que los campos se inicializan correctamente
        Field nivelAccesoField = Ventana.class.getDeclaredField("NivelAcceso");
        nivelAccesoField.setAccessible(true);
        int nivelAcceso = (int) nivelAccesoField.get(ventana);

        Field idUsuarioField = Ventana.class.getDeclaredField("IDUsuario");
        idUsuarioField.setAccessible(true);
        int idUsuario = (int) idUsuarioField.get(ventana);

        Assert.assertEquals("NivelAcceso debería ser 1", 1, nivelAcceso);
        Assert.assertEquals("IDUsuario debería ser 1", 1, idUsuario);
    }

    @Test
    public void testTransacciones_ConTipoAnimalSeleccionado() throws Exception {
        // Configurar el tipo de animal
        Field tipaField = Ventana.class.getDeclaredField("tipa");
        tipaField.setAccessible(true);
        tipaField.set(ventana, "Bovinos");

        // Agregar datos de prueba a las colas
        Queue<Object[]> colaBovinos = getColaCompraBovinos();
        colaBovinos.add(new Object[]{"B001", 1000.0, "2024-01-01"});

        Stack<Object[]> pilaBovinos = getPilaVentaBovinos();
        pilaBovinos.add(new Object[]{"B001", 1500.0, "2024-01-02"});

        // Ejecutar método Transacciones (no podemos probar la UI directamente, pero podemos verificar el estado)
        Method transaccionesMethod = Ventana.class.getDeclaredMethod("Transacciones");
        transaccionesMethod.setAccessible(true);
        transaccionesMethod.invoke(ventana);

        // Verificar que las estructuras de datos tienen los elementos esperados
        Assert.assertFalse("Cola de compras bovinos no debería estar vacía", colaBovinos.isEmpty());
        Assert.assertFalse("Pila de ventas bovinos no debería estar vacía", pilaBovinos.isEmpty());
    }

    @Test
    public void testCerrarSesion() throws Exception {
        // Mock para JOptionPane
        Mockito.mockStatic(JOptionPane.class);
        when(JOptionPane.showConfirmDialog(any(), anyString(), anyString(), anyInt()))
            .thenReturn(JOptionPane.YES_OPTION);

        // Ejecutar método CerrarSesion
        Method cerrarSesionMethod = Ventana.class.getDeclaredMethod("CerrarSesion");
        cerrarSesionMethod.setAccessible(true);
        cerrarSesionMethod.invoke(ventana);

        // Verificar que se llamó al método de confirmación
        verifyStatic(JOptionPane.class, times(1));
        JOptionPane.showConfirmDialog(any(), anyString(), anyString(), eq(JOptionPane.YES_NO_OPTION));
    }

    @Test
    public void testAuditoriaCierreSesion() throws Exception {
        // Configurar mock para la auditoría
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Configurar conexión en Ventana
        Field cxField = Ventana.class.getDeclaredField("cx");
        cxField.setAccessible(true);
        cxField.set(ventana, mockConnection);

        // Ejecutar método AuditoriaCierreSesion
        Method auditoriaMethod = Ventana.class.getDeclaredMethod("AuditoriaCierreSesion");
        auditoriaMethod.setAccessible(true);
        auditoriaMethod.invoke(ventana);

        // Verificar que se ejecutó la inserción
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testEstructurasDatosInicializadas() throws Exception {
        // Verificar que todas las estructuras de datos están inicializadas
        Assert.assertNotNull("Cola compra bovinos debería estar inicializada", getColaCompraBovinos());
        Assert.assertNotNull("Cola compra gallinas debería estar inicializada", getColaCompraGallinas());
        Assert.assertNotNull("Cola compra cerdos debería estar inicializada", getColaCompraCerdos());
        Assert.assertNotNull("Cola compra caballos debería estar inicializada", getColaCompraCaballos());
        Assert.assertNotNull("Cola compra caprinos debería estar inicializada", getColaCompraCaprinos());

        Assert.assertNotNull("Pila venta bovinos debería estar inicializada", getPilaVentaBovinos());
        Assert.assertNotNull("Pila venta gallinas debería estar inicializada", getPilaVentaGallinas());
        Assert.assertNotNull("Pila venta cerdos debería estar inicializada", getPilaVentaCerdos());
        Assert.assertNotNull("Pila venta caballos debería estar inicializada", getPilaVentaCaballos());
        Assert.assertNotNull("Pila venta caprinos debería estar inicializada", getPilaVentaCaprinos());

        Assert.assertNotNull("Registros bovinos deberían estar inicializados", getRegistrosBovinos());
        Assert.assertNotNull("Registros gallinas deberían estar inicializados", getRegistrosGallinas());
        Assert.assertNotNull("Registros cerdos deberían estar inicializados", getRegistrosCerdos());
        Assert.assertNotNull("Registros caballos deberían estar inicializados", getRegistrosCaballos());
        Assert.assertNotNull("Registros caprinos deberían estar inicializados", getRegistrosCaprinos());
    }

    @Test
    public void testConstantesTotales() throws Exception {
        // Verificar que las constantes de totales están definidas correctamente
        Field totalBovinosField = Ventana.class.getDeclaredField("TOTAL_Bovinos");
        totalBovinosField.setAccessible(true);
        int totalBovinos = (int) totalBovinosField.get(null);

        Field totalGallinasField = Ventana.class.getDeclaredField("TOTAL_Gallinas");
        totalGallinasField.setAccessible(true);
        int totalGallinas = (int) totalGallinasField.get(null);

        Assert.assertEquals("TOTAL_Bovinos debería ser 100", 100, totalBovinos);
        Assert.assertEquals("TOTAL_Gallinas debería ser 100", 100, totalGallinas);
    }

    // Métodos auxiliares para acceder a las estructuras de datos privadas
    @SuppressWarnings("unchecked")
    private Queue<Object[]> getColaCompraBovinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("colaCompraBovinos");
        field.setAccessible(true);
        return (Queue<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Queue<Object[]> getColaCompraGallinas() throws Exception {
        Field field = Ventana.class.getDeclaredField("colaCompraGallinas");
        field.setAccessible(true);
        return (Queue<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Queue<Object[]> getColaCompraCerdos() throws Exception {
        Field field = Ventana.class.getDeclaredField("colaCompraCerdos");
        field.setAccessible(true);
        return (Queue<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Queue<Object[]> getColaCompraCaballos() throws Exception {
        Field field = Ventana.class.getDeclaredField("colaCompraCaballos");
        field.setAccessible(true);
        return (Queue<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Queue<Object[]> getColaCompraCaprinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("colaCompraCaprinos");
        field.setAccessible(true);
        return (Queue<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Stack<Object[]> getPilaVentaBovinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("pilaVentaBovinos");
        field.setAccessible(true);
        return (Stack<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Stack<Object[]> getPilaVentaGallinas() throws Exception {
        Field field = Ventana.class.getDeclaredField("pilaVentaGallinas");
        field.setAccessible(true);
        return (Stack<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Stack<Object[]> getPilaVentaCerdos() throws Exception {
        Field field = Ventana.class.getDeclaredField("pilaVentaCerdos");
        field.setAccessible(true);
        return (Stack<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Stack<Object[]> getPilaVentaCaballos() throws Exception {
        Field field = Ventana.class.getDeclaredField("pilaVentaCaballos");
        field.setAccessible(true);
        return (Stack<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Stack<Object[]> getPilaVentaCaprinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("pilaVentaCaprinos");
        field.setAccessible(true);
        return (Stack<Object[]>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Ventana> getRegistrosBovinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("registrosBovinos");
        field.setAccessible(true);
        return (Map<String, Ventana>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Ventana> getRegistrosGallinas() throws Exception {
        Field field = Ventana.class.getDeclaredField("registrosGallinas");
        field.setAccessible(true);
        return (Map<String, Ventana>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Ventana> getRegistrosCerdos() throws Exception {
        Field field = Ventana.class.getDeclaredField("registrosCerdos");
        field.setAccessible(true);
        return (Map<String, Ventana>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Ventana> getRegistrosCaballos() throws Exception {
        Field field = Ventana.class.getDeclaredField("registrosCaballos");
        field.setAccessible(true);
        return (Map<String, Ventana>) field.get(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Ventana> getRegistrosCaprinos() throws Exception {
        Field field = Ventana.class.getDeclaredField("registrosCaprinos");
        field.setAccessible(true);
        return (Map<String, Ventana>) field.get(null);
    }

    @After
    public void tearDown() {
        // Limpiar estructuras de datos estáticas
        try {
            getColaCompraBovinos().clear();
            getColaCompraGallinas().clear();
            getColaCompraCerdos().clear();
            getColaCompraCaballos().clear();
            getColaCompraCaprinos().clear();

            getPilaVentaBovinos().clear();
            getPilaVentaGallinas().clear();
            getPilaVentaCerdos().clear();
            getPilaVentaCaballos().clear();
            getPilaVentaCaprinos().clear();

            getRegistrosBovinos().clear();
            getRegistrosGallinas().clear();
            getRegistrosCerdos().clear();
            getRegistrosCaballos().clear();
            getRegistrosCaprinos().clear();

            if (ventana != null) {
                ventana.setVisible(false);
                ventana.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyStatic(Class<JOptionPane> aClass, VerificationMode times) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}