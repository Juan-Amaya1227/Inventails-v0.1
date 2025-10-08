/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */

import java.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author amaya
 */
public class GastosDeMantenimientoTest {
    
    public GastosDeMantenimientoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of GastosDeMantenimiento method, of class GastosDeMantenimiento.
     */
    @Test
    public void testGastosDeMantenimiento() throws Exception {
        System.out.println("GastosDeMantenimiento");
        GastosDeMantenimiento instance = null;
        instance.GastosDeMantenimiento();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of InsertarTransaccion method, of class GastosDeMantenimiento.
     */
    @Test
    public void testInsertarTransaccion() throws Exception {
        System.out.println("InsertarTransaccion");
        LocalDateTime FechaDeGasto = null;
        float ValorGastoReal = 0.0F;
        GastosDeMantenimiento instance = null;
        instance.InsertarTransaccion(FechaDeGasto, ValorGastoReal);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
