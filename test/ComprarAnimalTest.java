
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.sql.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ComprarAnimalTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private PreparedStatement mockPreparedStatement2;

    @Mock
    private ResultSet mockResultSet;

    private ComprarAnimal comprarAnimal;
    private Queue<Object[]> colaCompraBovinos;
    private Queue<Object[]> colaCompraGallinas;
    private Map<String, Ventana> registrosBovinos;
    private Map<String, Ventana> registrosGallinas;

    @Before
    public void setUp() throws Exception {
        // Inicializar estructuras de datos
        colaCompraBovinos = new LinkedList<>();
        colaCompraGallinas = new LinkedList<>();
        registrosBovinos = new HashMap<>();
        registrosGallinas = new HashMap<>();

        // Configurar mocks básicos
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Crear instancia de ComprarAnimal
        comprarAnimal = new ComprarAnimal(
            1, // IDUsuario
            new LinkedList<>(), // colaCompraCaprinos
            colaCompraBovinos,
            colaCompraGallinas,
            new LinkedList<>(), // colaCompraCerdos
            new LinkedList<>(), // colaCompraCaballos
            new HashMap<>(), // registrosCaprinos
            registrosBovinos,
            registrosGallinas,
            new HashMap<>(), // registrosCerdos
            new HashMap<>(), // registrosCaballos
            "jdbc:mysql://localhost:3306/", // url
            "inventails", // db
            "root", // user
            "", // password
            "establo", // sitio
            "Bovinos", // tipa
            mockConnection // cx
        );
    }

    @Test
    public void testGenerarCodigoUnico() throws Exception {
        // Configurar
        Map<String, Ventana> registros = new HashMap<>();
        registros.put("BOV001", mock(Ventana.class));
        registros.put("BOV002", mock(Ventana.class));

        // Ejecutar método estático
        Method method = ComprarAnimal.class.getDeclaredMethod("generarCodigoUnico", String.class, Map.class);
        method.setAccessible(true);
        String codigo = (String) method.invoke(null, "BOV", registros);

        // Verificar
        Assert.assertNotNull("El código no debería ser null", codigo);
        Assert.assertTrue("El código debería empezar con BOV", codigo.startsWith("BOV"));
        Assert.assertFalse("El código no debería existir en los registros", registros.containsKey(codigo));
    }

    @Test
    public void testComprarA_EspaciosLlenos() throws Exception {
        // Configurar: llenar registros para simular espacios llenos
        for (int i = 0; i < 100; i++) {
            registrosBovinos.put("BOV" + String.format("%03d", i), mock(Ventana.class));
        }

        // Ejecutar
        Method method = ComprarAnimal.class.getDeclaredMethod("ComprarA");
        method.setAccessible(true);
        method.invoke(comprarAnimal);

        // Verificar que no se agregó a la cola (porque los espacios están llenos)
        Assert.assertTrue("No debería haber compras cuando los espacios están llenos", 
            colaCompraBovinos.isEmpty());
    }

    @Test
    public void testCRUDPedigree() throws Exception {
        // Configurar
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Ejecutar
        Method method = ComprarAnimal.class.getDeclaredMethod("CRUDPedigree", 
            String.class, String.class, String.class, String.class, String.class);
        method.setAccessible(true);
        method.invoke(comprarAnimal, "Brahman", "Produccion de carne", "Brahman", "Brahman", "BOV001");

        // Verificar
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testInsertarTransaccion() throws Exception {
        // Configurar
        LocalDateTime fechaCompra = LocalDateTime.now();
        float costoCompra = -1500.0f;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement2);
        when(mockPreparedStatement2.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);

        // Ejecutar
        Method method = ComprarAnimal.class.getDeclaredMethod("InsertarTransaccion", 
            LocalDateTime.class, Float.class);
        method.setAccessible(true);
        method.invoke(comprarAnimal, fechaCompra, costoCompra);

        // Verificar
        verify(mockPreparedStatement2, times(1)).executeQuery();
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testConstructorInicializacion() throws Exception {
        // Verificar que los campos se inicializan correctamente
        Field idUsuarioField = ComprarAnimal.class.getDeclaredField("IDUsuario");
        idUsuarioField.setAccessible(true);
        int idUsuario = (int) idUsuarioField.get(comprarAnimal);

        Field tipaField = ComprarAnimal.class.getDeclaredField("tipa");
        tipaField.setAccessible(true);
        String tipa = (String) tipaField.get(comprarAnimal);

        Field cxField = ComprarAnimal.class.getDeclaredField("cx");
        cxField.setAccessible(true);
        Connection cx = (Connection) cxField.get(comprarAnimal);

        Assert.assertEquals("IDUsuario debería ser 1", 1, idUsuario);
        Assert.assertEquals("tipa debería ser Bovinos", "Bovinos", tipa);
        Assert.assertNotNull("cx no debería ser null", cx);
    }

    @Test
    public void testEstructurasDatosReferencias() throws Exception {
        // Verificar que las referencias a las estructuras de datos funcionan correctamente
        Field colaBovinosField = ComprarAnimal.class.getDeclaredField("colaCompraBovinos");
        colaBovinosField.setAccessible(true);
        Queue<Object[]> colaBovinos = (Queue<Object[]>) colaBovinosField.get(comprarAnimal);

        Field registrosBovinosField = ComprarAnimal.class.getDeclaredField("registrosBovinos");
        registrosBovinosField.setAccessible(true);
        Map<String, Ventana> registrosBovinos = (Map<String, Ventana>) registrosBovinosField.get(comprarAnimal);

        // Agregar elementos a través de la referencia
        colaBovinos.offer(new Object[]{"BOV001", 1000.0, LocalDateTime.now()});
        registrosBovinos.put("BOV001", mock(Ventana.class));

        // Verificar que los cambios se reflejan en las estructuras originales
        Assert.assertFalse("La cola original debería tener elementos", this.colaCompraBovinos.isEmpty());
        Assert.assertFalse("Los registros originales deberían tener elementos", this.registrosBovinos.isEmpty());
    }

    @Test
    public void testConstantesTotales() throws Exception {
        // Verificar que las constantes están definidas correctamente
        Field totalBovinosField = ComprarAnimal.class.getDeclaredField("TOTAL_Bovinos");
        totalBovinosField.setAccessible(true);
        int totalBovinos = (int) totalBovinosField.get(null);

        Field totalGallinasField = ComprarAnimal.class.getDeclaredField("TOTAL_Gallinas");
        totalGallinasField.setAccessible(true);
        int totalGallinas = (int) totalGallinasField.get(null);

        Assert.assertEquals("TOTAL_Bovinos debería ser 100", 100, totalBovinos);
        Assert.assertEquals("TOTAL_Gallinas debería ser 100", 100, totalGallinas);
    }

    @Test
    public void testSwitchTipoAnimal() throws Exception {
        // Probar diferentes tipos de animales
        String[] tiposAnimales = {"Bovinos", "Gallinas", "Cerdos", "Caballos", "Caprinos"};
        
        for (String tipo : tiposAnimales) {
            // Crear nueva instancia para cada tipo
            ComprarAnimal animalEspecifico = new ComprarAnimal(
                1, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(),
                new LinkedList<>(), new LinkedList<>(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                "url", "db", "user", "password", "sitio", tipo, mockConnection
            );

            Field tipaField = ComprarAnimal.class.getDeclaredField("tipa");
            tipaField.setAccessible(true);
            String tipaActual = (String) tipaField.get(animalEspecifico);

            Assert.assertEquals("El tipo de animal debería coincidir", tipo, tipaActual);
        }
    }

    @Test
    public void testMetodosPrivadosAccesibles() throws Exception {
        // Verificar que podemos acceder a todos los métodos privados necesarios para testing
        Method generarCodigoUnico = ComprarAnimal.class.getDeclaredMethod("generarCodigoUnico", String.class, Map.class);
        Method comprarA = ComprarAnimal.class.getDeclaredMethod("ComprarA");
        Method crudPedigree = ComprarAnimal.class.getDeclaredMethod("CRUDPedigree", 
            String.class, String.class, String.class, String.class, String.class);
        Method insertarTransaccion = ComprarAnimal.class.getDeclaredMethod("InsertarTransaccion", 
            LocalDateTime.class, Float.class);

        Assert.assertNotNull("generarCodigoUnico debería ser accesible", generarCodigoUnico);
        Assert.assertNotNull("ComprarA debería ser accesible", comprarA);
        Assert.assertNotNull("CRUDPedigree debería ser accesible", crudPedigree);
        Assert.assertNotNull("InsertarTransaccion debería ser accesible", insertarTransaccion);
    }

    @After
    public void tearDown() {
        // Limpiar estructuras de datos
        if (colaCompraBovinos != null) colaCompraBovinos.clear();
        if (colaCompraGallinas != null) colaCompraGallinas.clear();
        if (registrosBovinos != null) registrosBovinos.clear();
        if (registrosGallinas != null) registrosGallinas.clear();
    }
}