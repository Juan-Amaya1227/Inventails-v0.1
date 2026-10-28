package VistaInicio;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import Dashboards.*;

/**
 * Dashboard administrativo 100% Visual.
 * Ahora puedes ver y editar las tarjetas directamente en el Design de NetBeans.
 */
public class AdminDashboard extends javax.swing.JPanel {

    private final Connection cx;
    private final int idUsuario;
    private CardLayout cardLayout;
    
    // Variables de datos
    private int totalAnimales = 0;
    private int totalUsuarios = 0;
    private Map<String, Integer> distribucionEspecies = new LinkedHashMap<>();

    public AdminDashboard(Connection cx, int idUsuario) {
        this.cx = cx;
        this.idUsuario = idUsuario;
        initComponents();
        
        // Corregir Layout para que el ComboBox sea visible
        setLayout(new BorderLayout());
        add(panelControles, BorderLayout.NORTH);
        add(panelContenedor, BorderLayout.CENTER);
        
        cardLayout = (CardLayout) panelContenedor.getLayout();
        
        // Configurar opciones del ComboBox
        comboVistas.setModel(new DefaultComboBoxModel<>(new String[] { 
            "Inicio", "Trazabilidad del Ganado", "Gestión Financiera", "Salud y Productividad", "Seguridad y Auditoría", "Jerarquía de Usuarios"
        }));
        
        // Integrar los nuevos dashboards de JFreeChart
        integrarNuevosDashboards();
        
        refrescarInventario();
    }

    private void integrarNuevosDashboards() {
        // Mapear panel original de Inventario a "Inicio"
        panelContenedor.add(panelInventario, "Inicio");
        
        // Agregar nuevos dashboards
        panelContenedor.add(new DashboardTrazabilidad(cx), "Trazabilidad del Ganado");
        panelContenedor.add(new DashboardFinanciero(cx), "Gestión Financiera");
        panelContenedor.add(new DashboardSalud(cx), "Salud y Productividad");
        panelContenedor.add(new DashboardAuditoria(cx), "Seguridad y Auditoría");
        panelContenedor.add(new JerarquiaUsuarios(cx), "Jerarquía de Usuarios");
    }

    public void refrescarInventario() {
        new Thread(() -> {
            try {
                distribucionEspecies.clear();
                try (Statement st = cx.createStatement()) {
                    ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM animales WHERE estado = 1");
                    if (rs1.next()) totalAnimales = rs1.getInt(1);
                    ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM usuarios");
                    if (rs2.next()) totalUsuarios = rs2.getInt(1);
                    ResultSet rs3 = st.executeQuery("SELECT tipo_animal, COUNT(*) FROM animales WHERE estado = 1 GROUP BY tipo_animal");
                    while (rs3.next()) distribucionEspecies.put(rs3.getString(1), rs3.getInt(2));
                }
                
                SwingUtilities.invokeLater(() -> {
                    lblTotalAnimales.setText(String.valueOf(totalAnimales));
                    lblTotalUsuarios.setText(String.valueOf(totalUsuarios));
                    lblTotalEspecies.setText(String.valueOf(distribucionEspecies.size()));
                    panelGrafica.repaint();
                });
                
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void dibujarGraficoBarras(Graphics2D g2, int x, int y, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (distribucionEspecies.isEmpty()) return;

        int barW = 60;
        int spacing = 30;
        int numEspecies = distribucionEspecies.size();
        int totalBarsW = (numEspecies * barW) + ((numEspecies - 1) * spacing);
        int startBarsX = (w - totalBarsW) / 2;
        
        int chartH = h - 100;
        int maxVal = 0;
        for (int v : distribucionEspecies.values()) if (v > maxVal) maxVal = v;
        if (maxVal == 0) maxVal = 1;

        int i = 0;
        for (Map.Entry<String, Integer> entry : distribucionEspecies.entrySet()) {
            int val = entry.getValue();
            int barH = (int) (((double) val / maxVal) * chartH);
            int bx = startBarsX + (i * (barW + spacing));
            int by = h - 60 - barH;

            GradientPaint gp = new GradientPaint(bx, by, new Color(41, 128, 185), bx, by + barH, new Color(52, 152, 219));
            g2.setPaint(gp);
            g2.fillRoundRect(bx, by, barW, barH, 10, 10);
            
            g2.setColor(new Color(52, 73, 94));
            g2.setFont(new Font("Verdana", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(entry.getKey(), bx + (barW/2) - (fm.stringWidth(entry.getKey())/2), h - 40);
            
            g2.setColor(new Color(41, 128, 185));
            g2.drawString(String.valueOf(val), bx + (barW/2) - (fm.stringWidth(String.valueOf(val))/2), by - 10);
            i++;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelControles = new javax.swing.JPanel();
        lblTextoVisualizar = new javax.swing.JLabel();
        comboVistas = new javax.swing.JComboBox<>();
        btnActualizar = new javax.swing.JButton();
        panelContenedor = new javax.swing.JPanel();
        panelInventario = new javax.swing.JPanel();
        lblTituloInv = new javax.swing.JLabel();
        cardAnimales = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTotalAnimales = new javax.swing.JLabel();
        cardUsuarios = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalUsuarios = new javax.swing.JLabel();
        cardEspecies = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblTotalEspecies = new javax.swing.JLabel();
        panelGrafica = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoBarras((Graphics2D) g, 0, 0, getWidth(), getHeight());
            }
        };
        lblGraficoTitulo = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(910, 590));
        setLayout(new java.awt.BorderLayout());

        panelControles.setBackground(new java.awt.Color(255, 255, 255));
        panelControles.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 40, 0, 40));
        panelControles.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 15, 10));

        lblTextoVisualizar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lblTextoVisualizar.setForeground(new java.awt.Color(127, 140, 141));
        lblTextoVisualizar.setText("Visualizar:");
        panelControles.add(lblTextoVisualizar);

        comboVistas.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        comboVistas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Inventario General", "Salud Animal", "Análisis Financiero", "Auditoría de Usuarios" }));
        comboVistas.setPreferredSize(new java.awt.Dimension(180, 30));
        panelControles.add(comboVistas);

        btnActualizar.setBackground(new java.awt.Color(41, 128, 185));
        btnActualizar.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setPreferredSize(new java.awt.Dimension(110, 30));
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });
        panelControles.add(btnActualizar);

        add(panelControles);

        panelContenedor.setBackground(new java.awt.Color(255, 255, 255));
        panelContenedor.setLayout(new java.awt.CardLayout());

        panelInventario.setBackground(new java.awt.Color(255, 255, 255));
        panelInventario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTituloInv.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        lblTituloInv.setForeground(new java.awt.Color(41, 128, 185));
        lblTituloInv.setText("Inventario General");
        panelInventario.add(lblTituloInv, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, -1, -1));

        cardAnimales.setBackground(new java.awt.Color(255, 255, 255));
        cardAnimales.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        cardAnimales.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(149, 165, 166));
        jLabel1.setText("TOTAL ANIMALES");
        cardAnimales.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        lblTotalAnimales.setFont(new java.awt.Font("Verdana", 1, 32)); // NOI18N
        lblTotalAnimales.setForeground(new java.awt.Color(44, 62, 80));
        lblTotalAnimales.setText("0");
        cardAnimales.add(lblTotalAnimales, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        panelInventario.add(cardAnimales, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 250, 110));

        cardUsuarios.setBackground(new java.awt.Color(255, 255, 255));
        cardUsuarios.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        cardUsuarios.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(149, 165, 166));
        jLabel2.setText("USUARIOS");
        cardUsuarios.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        lblTotalUsuarios.setFont(new java.awt.Font("Verdana", 1, 32)); // NOI18N
        lblTotalUsuarios.setForeground(new java.awt.Color(44, 62, 80));
        lblTotalUsuarios.setText("0");
        cardUsuarios.add(lblTotalUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        panelInventario.add(cardUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 60, 250, 110));

        cardEspecies.setBackground(new java.awt.Color(255, 255, 255));
        cardEspecies.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        cardEspecies.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(149, 165, 166));
        jLabel3.setText("ESPECIES");
        cardEspecies.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        lblTotalEspecies.setFont(new java.awt.Font("Verdana", 1, 32)); // NOI18N
        lblTotalEspecies.setForeground(new java.awt.Color(44, 62, 80));
        lblTotalEspecies.setText("0");
        cardEspecies.add(lblTotalEspecies, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        panelInventario.add(cardEspecies, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 60, 250, 110));

        panelGrafica.setBackground(new java.awt.Color(250, 251, 252));
        panelGrafica.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGrafica.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGraficoTitulo.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        lblGraficoTitulo.setForeground(new java.awt.Color(52, 73, 94));
        lblGraficoTitulo.setText("Distribución por Especie");
        panelGrafica.add(lblGraficoTitulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 15, -1, -1));

        panelInventario.add(panelGrafica, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 820, 310));

        panelContenedor.add(panelInventario, "Inventario General");

        add(panelContenedor);
    }// </editor-fold>//GEN-END:initComponents

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        String seleccion = (String) comboVistas.getSelectedItem();
        cardLayout.show(panelContenedor, seleccion);
    }//GEN-LAST:event_btnActualizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JPanel cardAnimales;
    private javax.swing.JPanel cardEspecies;
    private javax.swing.JPanel cardUsuarios;
    private javax.swing.JComboBox<String> comboVistas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblGraficoTitulo;
    private javax.swing.JLabel lblTextoVisualizar;
    private javax.swing.JLabel lblTituloInv;
    private javax.swing.JLabel lblTotalAnimales;
    private javax.swing.JLabel lblTotalEspecies;
    private javax.swing.JLabel lblTotalUsuarios;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JPanel panelControles;
    private javax.swing.JPanel panelGrafica;
    private javax.swing.JPanel panelInventario;
    // End of variables declaration//GEN-END:variables
}
