package VistaInicio;

import Vistas.CompraLogic;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class PanelCompras extends javax.swing.JPanel {

    private final Connection cx;
    private final int idUsuario;
    private final String tipoAnimal;
    private final Queue<Object[]> colaCompra;
    private final Map<String, Object> registros;
    private final CompraLogic logic;
    
    private String codigoGenerado;
    private String ubicacionSeleccionada;
    private int numeroEspacioSeleccionado = -1;
    private JButton lastSelectedButton = null;
    
    // Configuración del Mapa
    private final int numFilas = 10;
    private final int numColumnas = 10;
    private final Map<Integer, JButton> botonesMapa = new HashMap<>();

    public PanelCompras(Connection cx, int idUsuario, String tipoAnimal, Queue<Object[]> colaCompra, Map<String, Object> registros) {
        this.cx = cx;
        this.idUsuario = idUsuario;
        this.tipoAnimal = tipoAnimal;
        this.colaCompra = colaCompra;
        this.registros = registros;
        this.logic = new CompraLogic(cx, idUsuario);
        
        initComponents();
        configurarVistaSegunTipo();
        generarMapa();
        generarCodigo();
    }

    private void generarCodigo() {
        String prefijo = switch (tipoAnimal) {
            case "Bovinos" -> "BOV";
            case "Gallinas" -> "GAL";
            case "Cerdos" -> "CER";
            case "Caballos" -> "CAB";
            case "Caprinos" -> "CAP";
            default -> "ANI";
        };
        codigoGenerado = logic.generarCodigoUnico(prefijo, registros);
        lblCodigo.setText("Código: " + codigoGenerado);
    }

    private void configurarVistaSegunTipo() {
        lblTitulo.setText("Registro de Compra - " + tipoAnimal);
        
        // Configurar Razas y Proyectos según tipo (Extraído de JavaApplication10)
        String[] razas, propositos, edades;
        
        switch (tipoAnimal) {
            case "Bovinos" -> {
                razas = new String[]{"Cebu Brahman", "Brahman", "Normando", "Holstein", "Pardo Suizo", "Girolando", "Otra"};
                propositos = new String[]{"Produccion de carne", "Produccion de leche", "Fecundacion", "Gestacion", "Comercializacion"};
                edades = new String[]{"Ternero", "Becerro", "Novillo", "Adulto", "Senior"};
            }
            case "Gallinas" -> {
                razas = new String[]{"Rhode island red", "Lohmann Brown", "Orpington", "Leghorn", "Otra"};
                propositos = new String[]{"Produccion de pollo", "Produccion de huevos", "Fecundacion", "Comercializacion"};
                edades = new String[]{"Pollito", "Crecimiento", "Joven", "Adulto", "Senior"};
            }
            case "Cerdos" -> {
                razas = new String[]{"Duroc", "Gilt", "Gottingen Minipig", "Large White", "San Pedreño", "Otra"};
                propositos = new String[]{"produccion de carne", "Exposicion", "Fecundacion", "Gestacion", "Comercializacion"};
                edades = new String[]{"Lechon", "Destetado", "Crecimiento", "Ceba", "Senior"};
            }
            case "Caballos" -> {
                razas = new String[]{"Criollo", "Perceron", "Arabe", "Turcomano", "Morgan", "Otra"};
                propositos = new String[]{"Exposicion", "Trabajo", "Fecundacion", "Gestacion", "Comercializacion"};
                edades = new String[]{"Lactante", "Potro", "Adolecente", "Adulto", "Senior"};
            }
            default -> { // Caprinos
                razas = new String[]{"Saanen", "Boer", "Nubia", "Alpina", "Toggenburg", "Otra"};
                propositos = new String[]{"Produccion de carne", "Produccion de leche", "Produccion de fibra", "Fecundacion", "Gestacion", "Pastoreo"};
                edades = new String[]{"Lactante", "Destetado", "Chivo", "Adulto", "Senior"};
            }
        }
        
        actualizarCombos(razas, propositos, edades);
    }

    private void actualizarCombos(String[] razas, String[] propositos, String[] edades) {
        comboRaza.setModel(new javax.swing.DefaultComboBoxModel<>(razas));
        comboMadre.setModel(new javax.swing.DefaultComboBoxModel<>(razas));
        comboPadre.setModel(new javax.swing.DefaultComboBoxModel<>(razas));
        comboProposito.setModel(new javax.swing.DefaultComboBoxModel<>(propositos));
        comboEdad.setModel(new javax.swing.DefaultComboBoxModel<>(edades));
    }

    private void generarMapa() {
        panelMapa.setLayout(new GridLayout(numFilas, numColumnas, 2, 2));
        botonesMapa.clear();
        
        for (int f = 0; f < numFilas; f++) {
            for (int c = 0; c < numColumnas; c++) {
                int idEspacio = (f * numColumnas) + (c + 1);
                JButton btn = new JButton(String.valueOf(idEspacio));
                btn.setPreferredSize(new Dimension(40, 40));
                btn.setToolTipText("Fila " + (f + 1) + ", Columna " + (c + 1));
                
                btn.addActionListener(e -> {
                    if (lastSelectedButton != null && lastSelectedButton.getBackground().equals(Color.BLUE)) {
                        lastSelectedButton.setBackground(Color.GREEN);
                    }
                    btn.setBackground(Color.BLUE);
                    lastSelectedButton = btn;
                    numeroEspacioSeleccionado = idEspacio;
                    ubicacionSeleccionada = obtenerSitio() + " espacio: " + idEspacio;
                    lblUbicacionSeleccionada.setText("Ubicación: " + ubicacionSeleccionada);
                });
                
                botonesMapa.put(idEspacio, btn);
                panelMapa.add(btn);
            }
        }
        actualizarEstadoMapa();
    }

    private String obtenerSitio() {
        return switch (tipoAnimal) {
            case "Bovinos" -> "establo";
            case "Gallinas" -> "gallinero";
            case "Cerdos" -> "chiquero";
            case "Caballos" -> "caballeriza";
            case "Caprinos" -> "cabriza";
            default -> "zona";
        };
    }

    private void actualizarEstadoMapa() {
        // 1. Obtener ocupados de DB
        Map<Integer, Boolean> ocupados = new HashMap<>();
        String sitio = obtenerSitio();
        try (Statement st = cx.createStatement();
             ResultSet rs = st.executeQuery("SELECT ubicacion FROM animales WHERE estado = 1 AND ubicacion LIKE '" + sitio + "%'")) {
            while (rs.next()) {
                String ubi = rs.getString("ubicacion");
                try {
                    int id = Integer.parseInt(ubi.substring(ubi.lastIndexOf(":") + 1).trim());
                    ocupados.put(id, true);
                } catch (Exception e) {}
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar ocupación: " + e.getMessage());
        }

        // 2. Aplicar restricciones de edad
        String edad = (String) comboEdad.getSelectedItem();
        int filaInicio = 0, filaFin = 9;
        
        if (edad != null) {
            int index = comboEdad.getSelectedIndex();
            filaInicio = index * 2;
            filaFin = filaInicio + 1;
        }

        for (int f = 0; f < numFilas; f++) {
            for (int c = 0; c < numColumnas; c++) {
                int id = (f * numColumnas) + (c + 1);
                JButton btn = botonesMapa.get(id);
                
                if (f >= filaInicio && f <= filaFin) {
                    if (ocupados.containsKey(id)) {
                        btn.setBackground(Color.RED);
                        btn.setEnabled(false);
                    } else {
                        btn.setBackground(Color.GREEN);
                        btn.setEnabled(true);
                    }
                } else {
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setEnabled(false);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblCodigo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPeso = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCosto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        comboEdad = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        comboGenero = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        comboRaza = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        comboProposito = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        comboMadre = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        comboPadre = new javax.swing.JComboBox<>();
        scrollMapa = new javax.swing.JScrollPane();
        panelMapa = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblUbicacionSeleccionada = new javax.swing.JLabel();
        btnRegistrar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(910, 590));

        lblTitulo.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(41, 128, 185));
        lblTitulo.setText("Registro de Compra");

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Complete los datos para ingresar un nuevo animal al inventario.");

        lblCodigo.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código: ---");

        jLabel3.setText("Peso Inicial (kg)");

        jLabel4.setText("Costo de Compra ($)");

        jLabel5.setText("Clasificación de Edad");

        comboEdad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEdadActionPerformed(evt);
            }
        });

        jLabel6.setText("Género");

        comboGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Macho", "Hembra" }));

        jLabel7.setText("Raza del Animal");

        jLabel8.setText("Propósito");

        jLabel9.setText("Raza Madre");

        jLabel10.setText("Raza Padre");

        scrollMapa.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapa de Ubicación"));

        panelMapa.setBackground(new java.awt.Color(240, 245, 240));

        javax.swing.GroupLayout panelMapaLayout = new javax.swing.GroupLayout(panelMapa);
        panelMapa.setLayout(panelMapaLayout);
        panelMapaLayout.setHorizontalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 425, Short.MAX_VALUE)
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        scrollMapa.setViewportView(panelMapa);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Seleccione un espacio libre en el mapa");

        lblUbicacionSeleccionada.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblUbicacionSeleccionada.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUbicacionSeleccionada.setText("Ubicación: Ninguna");

        btnRegistrar.setBackground(new java.awt.Color(41, 128, 185));
        btnRegistrar.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar Compra");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                                .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3)
                                        .addComponent(txtPeso)
                                        .addComponent(jLabel4)
                                        .addComponent(txtCosto, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                        .addComponent(jLabel5)
                                        .addComponent(comboEdad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addComponent(comboGenero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7)
                                    .addComponent(comboRaza, 0, 180, Short.MAX_VALUE)
                                    .addComponent(jLabel8)
                                    .addComponent(comboProposito, 0, 180, Short.MAX_VALUE)
                                    .addComponent(jLabel9)
                                    .addComponent(comboMadre, 0, 180, Short.MAX_VALUE)
                                    .addComponent(jLabel10)
                                    .addComponent(comboPadre, 0, 180, Short.MAX_VALUE))
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scrollMapa)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(470, 470, 470)
                        .addComponent(lblUbicacionSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitulo)
                    .addComponent(lblCodigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboRaza, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboProposito, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboMadre, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboPadre, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(scrollMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addComponent(lblUbicacionSeleccionada)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void comboEdadActionPerformed(java.awt.event.ActionEvent evt) {                                          
        actualizarEstadoMapa();
        numeroEspacioSeleccionado = -1;
        lblUbicacionSeleccionada.setText("Ubicación: Ninguna");
    }                                         

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        try {
            // 1. Validaciones básicas
            if (numeroEspacioSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un espacio en el mapa", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double peso = Double.parseDouble(txtPeso.getText());
            double costo = Double.parseDouble(txtCosto.getText());
            String genero = (String) comboGenero.getSelectedItem();
            String proposito = (String) comboProposito.getSelectedItem();
            
            // 2. Validación lógica
            if (!logic.validarGeneroProposito(genero, proposito)) {
                JOptionPane.showMessageDialog(this, "El género " + genero + " no es compatible con el propósito " + proposito, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Ejecutar Registro
            boolean exito = logic.registrarCompra(
                codigoGenerado, tipoAnimal, genero, ubicacionSeleccionada,
                (String) comboRaza.getSelectedItem(), proposito,
                (String) comboMadre.getSelectedItem(), (String) comboPadre.getSelectedItem(),
                (String) comboEdad.getSelectedItem(), peso, costo,
                colaCompra, registros
            );
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Animal registrado con éxito bajo el código: " + codigoGenerado);
                limpiarCampos();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Peso y Costo deben ser valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                            

    private void limpiarCampos() {
        txtPeso.setText("");
        txtCosto.setText("");
        generarCodigo();
        actualizarEstadoMapa();
        numeroEspacioSeleccionado = -1;
        lblUbicacionSeleccionada.setText("Ubicación: Ninguna");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> comboEdad;
    private javax.swing.JComboBox<String> comboGenero;
    private javax.swing.JComboBox<String> comboMadre;
    private javax.swing.JComboBox<String> comboPadre;
    private javax.swing.JComboBox<String> comboProposito;
    private javax.swing.JComboBox<String> comboRaza;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblUbicacionSeleccionada;
    private javax.swing.JPanel panelMapa;
    private javax.swing.JScrollPane scrollMapa;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtPeso;
    // End of variables declaration//GEN-END:variables
}
