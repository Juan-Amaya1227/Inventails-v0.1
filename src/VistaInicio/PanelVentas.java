package VistaInicio;

import Vistas.VentaLogic;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Stack;
import javax.swing.JOptionPane;

public class PanelVentas extends javax.swing.JPanel {

    private final Connection cx;
    private final int idUsuario;
    private final int idAnimal;
    private final String codigo;
    private final Stack<Object[]> pilaVenta;
    private final Map<String, Object> registros;
    private final VentaLogic logic;

    public PanelVentas(Connection cx, int idUsuario, int idAnimal, String codigo, Stack<Object[]> pilaVenta, Map<String, Object> registros) {
        this.cx = cx;
        this.idUsuario = idUsuario;
        this.idAnimal = idAnimal;
        this.codigo = codigo;
        this.pilaVenta = pilaVenta;
        this.registros = registros;
        this.logic = new VentaLogic(cx, idUsuario);
        
        initComponents();
        lblTitulo.setText("Registro de Venta - " + codigo);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCosto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPesoFinal = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(910, 590));

        lblTitulo.setFont(new java.awt.Font("Verdana", 1, 24)); 
        lblTitulo.setForeground(new java.awt.Color(41, 128, 185));
        lblTitulo.setText("Registro de Venta");

        jLabel1.setFont(new java.awt.Font("Verdana", 0, 14)); 
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Ingrese los detalles finales para completar la venta del animal.");

        jLabel2.setText("Costo de Venta ($)");

        jLabel3.setText("Peso Final (kg)");

        btnRegistrar.setBackground(new java.awt.Color(41, 128, 185));
        btnRegistrar.setFont(new java.awt.Font("Verdana", 1, 14)); 
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar Venta");
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
                        .addGap(304, 304, 304)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnRegistrar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtCosto, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtPesoFinal, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(270, 270, 270)
                        .addComponent(jLabel1)))
                .addContainerGap(271, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(50, 50, 50)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesoFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(149, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        try {
            double costoVenta = Double.parseDouble(txtCosto.getText());
            double pesoFinal = Double.parseDouble(txtPesoFinal.getText());
            
            boolean exito = logic.registrarVenta(codigo, idAnimal, costoVenta, pesoFinal, pilaVenta, registros);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Venta registrada con éxito para el animal: " + codigo);
                // Aquí podrías regresar al dashboard o limpiar
                btnRegistrar.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Debe ingresar valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                            


    // Variables declaration - do not modify                     
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtPesoFinal;
    // End of variables declaration                   
}
