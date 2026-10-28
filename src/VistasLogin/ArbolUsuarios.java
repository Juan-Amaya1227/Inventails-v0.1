package VistasLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.sql.*;
import java.util.*;
import java.util.List;

/**
 * Versión final con botón de actualización y centrado garantizado.
 */
public class ArbolUsuarios extends JFrame {

    private static final String URL = "jdbc:mysql://bydeauiwkosfltg2tbfh-mysql.services.clever-cloud.com:3306/bydeauiwkosfltg2tbfh";
    private static final String USER = "u6xichrqiyoa9cgd";
    private static final String PASS = "NWt7bgrogWXno0sb6BJG";

    private JScrollPane scroll;
    private ArbolGraficoPanel panelGrafico;
    private JButton btnActualizar;

    public ArbolUsuarios() {
        setTitle("Jerarquía de Usuarios - ARIADNA");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        panelGrafico = new ArbolGraficoPanel();
        scroll = new JScrollPane(panelGrafico);
        scroll.setBorder(null);
        // Hacer el scroll más rápido y fluido
        scroll.getVerticalScrollBar().setUnitIncrement(25);
        scroll.getHorizontalScrollBar().setUnitIncrement(25);
        add(scroll, BorderLayout.CENTER);

        // BOTÓN DE ACTUALIZAR
        btnActualizar = new JButton("🔄 Actualizar Árbol");
        btnActualizar.setFont(new Font("Verdana", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(41, 128, 185));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setPreferredSize(new Dimension(100, 50));
        btnActualizar.addActionListener(e -> refrescarTodo());
        add(btnActualizar, BorderLayout.SOUTH);
        
        // Carga inicial y centrado automático
        refrescarTodo();
    }

    /**
     * Recarga los datos de la DB y reposiciona el scroll al centro.
     */
    private void refrescarTodo() {
        new Thread(() -> {
            panelGrafico.cargarDesdeDB();
            // Esperar un momento a que el layout se valide
            try { Thread.sleep(300); } catch (InterruptedException ex) {}
            SwingUtilities.invokeLater(this::centrarArbol);
        }).start();
    }

    private void centrarArbol() {
        if (panelGrafico.raiz == null) return;
        
        // Obtenemos la posición X del nodo raíz y la centramos en el Viewport
        int xCentro = panelGrafico.raiz.x - (scroll.getViewport().getWidth() / 2);
        
        // Aplicar el movimiento al scrollbar horizontal
        JScrollBar horizontal = scroll.getHorizontalScrollBar();
        horizontal.setValue(xCentro);
        
        // Refrescar UI
        scroll.revalidate();
        scroll.repaint();
    }

    class ArbolGraficoPanel extends JPanel {
        protected Nodo raiz;
        private final Map<Integer, Nodo> todosLosNodos = new HashMap<>();

        public ArbolGraficoPanel() {
            setBackground(new Color(245, 247, 250));
            // Canvas grande para permitir ramificaciones
            setPreferredSize(new Dimension(2500, 1500)); 
        }

        public void cargarDesdeDB() {
            todosLosNodos.clear();
            raiz = null;
            try (Connection cx = DriverManager.getConnection(URL, USER, PASS)) {
                Statement st = cx.createStatement();
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
                    // La raíz empieza en 1250 (centro del lienzo de 2500)
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (raiz != null) {
                dibujarLineas(g2, raiz);
                dibujarNodos(g2, raiz);
            }
        }

        private void dibujarLineas(Graphics2D g2, Nodo n) {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(new Color(189, 195, 199));
            for (Nodo hijo : n.hijos) {
                g2.draw(new Line2D.Float(n.x, n.y, hijo.x, hijo.y));
                dibujarLineas(g2, hijo);
            }
        }

        private void dibujarNodos(Graphics2D g2, Nodo n) {
            // Sombra
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new Ellipse2D.Float(n.x - 28, n.y - 28, 60, 60));
            // Nodo
            GradientPaint gradiente = new GradientPaint(n.x, n.y - 30, new Color(52, 152, 219), n.x, n.y + 30, new Color(41, 128, 185));
            g2.setPaint(gradiente);
            g2.fill(new Ellipse2D.Float(n.x - 30, n.y - 30, 60, 60));
            // Borde
            g2.setColor(Color.WHITE);
            g2.draw(new Ellipse2D.Float(n.x - 30, n.y - 30, 60, 60));
            // Etiqueta
            g2.setFont(new Font("Verdana", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            String label = n.nombre;
            int textX = n.x - (fm.stringWidth(label) / 2);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(textX - 8, n.y + 38, fm.stringWidth(label) + 16, 22, 10, 10);
            g2.setColor(new Color(44, 62, 80));
            g2.drawString(label, textX, n.y + 54);
            for (Nodo hijo : n.hijos) dibujarNodos(g2, hijo);
        }
    }

    class Nodo {
        int id; String nombre; int x, y;
        List<Nodo> hijos = new ArrayList<>();
        public Nodo(int id, String nombre) { this.id = id; this.nombre = nombre; }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> {
            ArbolUsuarios app = new ArbolUsuarios();
            app.setVisible(true);
        });
    }
}
