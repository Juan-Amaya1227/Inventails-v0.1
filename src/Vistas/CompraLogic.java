package Vistas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class CompraLogic {

    private final Connection cx;
    private final int idUsuario;

    public CompraLogic(Connection cx, int idUsuario) {
        this.cx = cx;
        this.idUsuario = idUsuario;
    }

    public boolean validarGeneroProposito(String genero, String proposito) {
        if ("Macho".equals(genero)) {
            return !("Produccion de leche".equals(proposito) || 
                     "Produccion de huevos".equals(proposito) || 
                     "Gestacion".equals(proposito));
        }
        if ("Hembra".equals(genero)) {
            return !"Fecundacion".equals(proposito);
        }
        return true;
    }

    public String generarCodigoUnico(String prefijo, Map<String, ?> registros) {
        Random random = new Random();
        String codigo;
        do {
            int numeroAleatorio = random.nextInt(1000);
            codigo = prefijo + String.format("%03d", numeroAleatorio);
        } while (registros.containsKey(codigo));
        return codigo;
    }

    public boolean registrarCompra(
            String codigo, String tipoAnimal, String genero, String ubicacion,
            String raza, String proposito, String razaMadre, String razaPadre,
            String rangoEdad, double peso, double costo,
            Queue<Object[]> colaCompra, Map<String, Object> registros) throws SQLException {

        cx.setAutoCommit(false); // Iniciar transacción

        try {
            // 1. Insertar en tabla animales
            String sqlAnimal = "INSERT INTO animales (codigo, tipo_animal, Genero, ubicacion, estado) VALUES (?, ?, ?, ?, 1)";
            try (PreparedStatement ps = cx.prepareStatement(sqlAnimal, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, codigo);
                ps.setString(2, tipoAnimal);
                ps.setString(3, genero);
                ps.setString(4, ubicacion);
                ps.executeUpdate();
                
                // Obtener ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Error al obtener ID del animal");
                int idAnimal = rs.getInt(1);

                // 2. Insertar en tabla pedigree
                String sqlPedigree = "INSERT INTO pedigree (ID_ANIMAL, ID_usuario, RazaAnimal, PropositoAnimal, RazaMadre, RazaPadre) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psP = cx.prepareStatement(sqlPedigree)) {
                    psP.setInt(1, idAnimal);
                    psP.setInt(2, idUsuario);
                    psP.setString(3, raza);
                    psP.setString(4, proposito);
                    psP.setString(5, razaMadre);
                    psP.setString(6, razaPadre);
                    psP.executeUpdate();
                }

                // 3. Consultar y Actualizar Saldo
                double saldoActual = 0;
                try (PreparedStatement psS = cx.prepareStatement("SELECT saldo FROM saldo WHERE id = 1");
                     ResultSet rsS = psS.executeQuery()) {
                    if (rsS.next()) saldoActual = rsS.getDouble("saldo");
                }
                
                double nuevoSaldo = saldoActual - costo;
                try (PreparedStatement psUS = cx.prepareStatement("UPDATE saldo SET saldo = ? WHERE id = 1")) {
                    psUS.setDouble(1, nuevoSaldo);
                    psUS.executeUpdate();
                }

                // 4. Insertar en tabla compras
                LocalDateTime fecha = LocalDateTime.now();
                String sqlCompra = "INSERT INTO compras (ID_animal, ID_usuario, f_compra, v_compra, Edad_inicial, Peso_inicial_kg) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psC = cx.prepareStatement(sqlCompra, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    psC.setInt(1, idAnimal);
                    psC.setInt(2, idUsuario);
                    psC.setString(3, fecha.toString());
                    psC.setDouble(4, costo * -1); // Costo negativo en compras
                    psC.setString(5, rangoEdad);
                    psC.setDouble(6, peso);
                    psC.executeUpdate();
                    
                    ResultSet rsC = psC.getGeneratedKeys();
                    if (rsC.next()) {
                        int idCompra = rsC.getInt(1);
                        // 5. Insertar Transacción
                        try (PreparedStatement psT = cx.prepareStatement("INSERT INTO transacciones (TIPO_TRANSACCION, id_compra) VALUES ('COMPRA', ?)")) {
                            psT.setInt(1, idCompra);
                            psT.executeUpdate();
                        }
                    }
                }

                // 6. Actualizar Memoria
                colaCompra.offer(new Object[]{codigo, costo, fecha});
                registros.put(codigo, new Object()); 

                cx.commit();
                return true;
            }
        } catch (SQLException e) {
            cx.rollback();
            throw e;
        } finally {
            cx.setAutoCommit(true);
        }
    }
}
