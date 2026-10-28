package Dashboards;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.sql.*;
import java.util.*;
import java.util.List;

public class JerarquiaUsuarios extends JPanel {
    private Connection connection;
    private JScrollPane scroll;
    private ArbolGraficoPanel panelGrafico;
    private JButton btnActualizar;

    public JerarquiaUsuarios(Connection conn) {
        this.connection = conn;
        setLayout(new BorderLayout());
        setBackground(new Color(26, 37, 47));

        panelGrafico = new ArbolGraficoPanel();
        scroll = new JScrollPane(panelGrafico);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        scroll.getHorizontalScrollBar().setUnitIncrement(25);
        add(scroll, BorderLayout.CENTER);

        btnActualizar = new JButton("Actualizar Jerarquía");
        btnActualizar.setFont(new Font("Verdana", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(41, 128, 185));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setPreferredSize(new Dimension(100, 40));
        btnActualizar.addActionListener(e -> refrescarTodo());
        add(btnActualizar, BorderLayout.SOUTH);
        
        refrescarTodo();
    }

    private void refrescarTodo() {
        new Thread(() -> {
            panelGrafico.cargarDesdeDB();
            try { Thread.sleep(300); } catch (InterruptedException ex) {}
            SwingUtilities.invokeLater(this::centrarArbol);
        }).start();
    }

    private void centrarArbol() {
        if (panelGrafico.raiz == null) return;
        int xCentro = panelGrafico.raiz.x - (scroll.getViewport().getWidth() / 2);
        scroll.getHorizontalScrollBar().setValue(xCentro);
        scroll.revalidate();
        scroll.repaint();
    }

    class ArbolGraficoPanel extends JPanel {
        protected Nodo raiz;
        private final Map<Integer, Nodo> todosLosNodos = new HashMap<>();

        public ArbolGraficoPanel() {
            setBackground(new Color(26, 37, 47));
            setPreferredSize(new Dimension(2500, 1500)); 
        }

        public void cargarDesdeDB() {
            todosLosNodos.clear();
            raiz = null;
            try {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT id, Usuario FROM usuarios");
                while (rs.next()) {
                    Nodo n = new Nodo(rs.getInt("id"), rs.getString("Usuario"));
                    todosLosNodos.put(n.id, n);
                }

                ResultSet rsJ = st.executeQuery("SELECT u.id, c.ID_CreadoPor FROM usuarios u LEFT JOIN creacionusuarios c ON u.id = c.ID_Usuario");
                while (rsJ.next()) {
                    int idHijo = rsJ.getInt("id");
                    int idPadre = rsJ.getInt("ID_CreadoPor");
                    if (rsJ.wasNull() || idPadre == 0) {
                        raiz = todosLosNodos.get(idHijo);
                    } else {
                        Nodo padre = todosLosNodos.get(idPadre);
                        Nodo hijo = todosLosNodos.get(idHijo);
                        if (padre != null && hijo != null) padre.hijos.add(hijo);
                    }
                }

                if (raiz != null) {
                    distribuirNodos(raiz, 1250, 100, 1400);
                }
                repaint();
            } catch (Exception e) { e.printStackTrace(); }
        }

        private void distribuirNodos(Nodo n, int x, int y, int anchoDisponible) {
            n.x = x; n.y = y;
            if (n.hijos.isEmpty()) return;
            int numHijos = n.hijos.size();
            int subAncho = anchoDisponible / numHijos;
            int inicioX = x - (anchoDisponible / 2) + (subAncho / 2);
            for (int i = 0; i < numHijos; i++) {
                distribuirNodos(n.hijos.get(i), inicioX + (i * subAncho), y + 180, subAncho);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (raiz != null) {
                dibujarLineas(g2, raiz);
                dibujarNodos(g2, raiz);
            }
        }

        private void dibujarLineas(Graphics2D g2, Nodo n) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(52, 152, 219, 100));
            for (Nodo hijo : n.hijos) {
                g2.draw(new Line2D.Float(n.x, n.y, hijo.x, hijo.y));
                dibujarLineas(g2, hijo);
            }
        }

        private void dibujarNodos(Graphics2D g2, Nodo n) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fill(new Ellipse2D.Float(n.x - 28, n.y - 28, 60, 60));
            GradientPaint gradiente = new GradientPaint(n.x, n.y - 30, new Color(0, 255, 255), n.x, n.y + 30, new Color(41, 128, 185));
            g2.setPaint(gradiente);
            g2.fill(new Ellipse2D.Float(n.x - 30, n.y - 30, 60, 60));
            g2.setColor(Color.WHITE);
            g2.draw(new Ellipse2D.Float(n.x - 30, n.y - 30, 60, 60));
            g2.setFont(new Font("Verdana", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String label = n.nombre;
            int textX = n.x - (fm.stringWidth(label) / 2);
            g2.setColor(Color.WHITE);
            g2.drawString(label, textX, n.y + 50);
            for (Nodo hijo : n.hijos) dibujarNodos(g2, hijo);
        }
    }

    class Nodo {
        int id; String nombre; int x, y;
        List<Nodo> hijos = new ArrayList<>();
        public Nodo(int id, String nombre) { this.id = id; this.nombre = nombre; }
    }
}
