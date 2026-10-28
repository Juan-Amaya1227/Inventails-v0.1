package VistaInicio;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.util.Queue;
import java.util.Stack;
import javax.swing.table.DefaultTableModel;

public class TransaccionesRecientes extends javax.swing.JPanel {

    private final Queue<Object[]> colaCompra;
    private final Stack<Object[]> pilaVenta;

    public TransaccionesRecientes(Queue<Object[]> colaCompra, Stack<Object[]> pilaVenta) {
        this.colaCompra = colaCompra;
        this.pilaVenta = pilaVenta;
        FlatLightLaf.setup();
        initComponents();
        cargarCompras(); // Por defecto mostrar compras
    }

    private void cargarCompras() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (Object[] fila : colaCompra) {
            model.addRow(new Object[]{fila[0], fila[1], fila[2], "COMPRA"});
        }
    }

    private void cargarVentas() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        // Recorrer la pila (últimos primero)
        for (int i = pilaVenta.size() - 1; i >= 0; i--) {
            Object[] fila = pilaVenta.get(i);
            model.addRow(new Object[]{fila[0], fila[1], fila[2], "VENTA"});
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        BotonVercompras = new javax.swing.JPanel();
        TextoVerCompras = new javax.swing.JLabel();
        BotonVerventas = new javax.swing.JPanel();
        TextoVerVentas = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));

        BotonVercompras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BotonVercomprasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BotonVercomprasMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cargarCompras();
            }
        });

        TextoVerCompras.setFont(new java.awt.Font("Verdana", 1, 18)); 
        TextoVerCompras.setText("          Ver Compras");

        javax.swing.GroupLayout BotonVercomprasLayout = new javax.swing.GroupLayout(BotonVercompras);
        BotonVercompras.setLayout(BotonVercomprasLayout);
        BotonVercomprasLayout.setHorizontalGroup(
            BotonVercomprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TextoVerCompras, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        BotonVercomprasLayout.setVerticalGroup(
            BotonVercomprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BotonVercomprasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(TextoVerCompras)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        BotonVerventas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BotonVerventasMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                BotonVerventasMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cargarVentas();
            }
        });

        TextoVerVentas.setFont(new java.awt.Font("Verdana", 1, 18)); 
        TextoVerVentas.setText("           Ver Ventas");

        javax.swing.GroupLayout BotonVerventasLayout = new javax.swing.GroupLayout(BotonVerventas);
        BotonVerventas.setLayout(BotonVerventasLayout);
        BotonVerventasLayout.setHorizontalGroup(
            BotonVerventasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BotonVerventasLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(TextoVerVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        BotonVerventasLayout.setVerticalGroup(
            BotonVerventasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BotonVerventasLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(TextoVerVentas)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
            },
            new String [] {
                "Codigo", "Monto", "Fecha", "Tipo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(BotonVercompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(108, 108, 108)
                        .addComponent(BotonVerventas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonVercompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonVerventas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );
    }// </editor-fold>                        

    private void BotonVercomprasMouseEntered(java.awt.event.MouseEvent evt) {                                             
        BotonVercompras.setBackground(new Color(44,62,80));
        TextoVerCompras.setForeground(Color.white);
    }                                            

    private void BotonVercomprasMouseExited(java.awt.event.MouseEvent evt) {                                            
        BotonVercompras.setBackground(new Color(242,242,242));
        TextoVerCompras.setForeground(Color.black);
    }                                           

    private void BotonVerventasMouseEntered(java.awt.event.MouseEvent evt) {                                            
        BotonVerventas.setBackground(new Color(44,62,80));
        TextoVerVentas.setForeground(Color.white);
    }                                           

    private void BotonVerventasMouseExited(java.awt.event.MouseEvent evt) {                                           
        BotonVerventas.setBackground(new Color(242,242,242));
        TextoVerVentas.setForeground(Color.black);
    }                                          


    // Variables declaration - do not modify                     
    private javax.swing.JPanel BotonVercompras;
    private javax.swing.JPanel BotonVerventas;
    private javax.swing.JLabel TextoVerCompras;
    private javax.swing.JLabel TextoVerVentas;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration                   
}
