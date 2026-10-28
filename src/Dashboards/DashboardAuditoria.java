package Dashboards;

import Dashboards.Logic.AuditoriaLogic;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Map;

public class DashboardAuditoria extends JPanel {
    private AuditoriaLogic logic;
    private Connection connection;

    public DashboardAuditoria(Connection conn) {
        this.connection = conn;
        this.logic = new AuditoriaLogic(conn);
        initComponents();
        setupCharts();
        setupFeed();
    }

    private void initComponents() {
        setBackground(new Color(26, 37, 47));
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("SEGURIDAD Y AUDITORÍA");
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(new Color(0, 255, 255));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Contenido
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Izquierda: Feed de actividad
        RoundedPanel leftPanel = new RoundedPanel(25, new Color(44, 62, 80));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        JLabel lblFeed = new JLabel(" FEED DE ACTIVIDAD", JLabel.CENTER);
        lblFeed.setForeground(Color.CYAN);
        leftPanel.add(lblFeed, BorderLayout.NORTH);
        listFeed = new JList<>();
        listFeed.setBackground(new Color(44, 62, 80));
        listFeed.setForeground(Color.WHITE);
        JScrollPane scrollFeed = new JScrollPane(listFeed);
        scrollFeed.setBorder(null);
        leftPanel.add(scrollFeed, BorderLayout.CENTER);
        main.add(leftPanel, BorderLayout.WEST);

        // Centro: Chart
        RoundedPanel centerPanel = new RoundedPanel(25, new Color(44, 62, 80));
        centerPanel.setLayout(new BorderLayout());
        chartPanelActions = new JPanel(new BorderLayout());
        chartPanelActions.setOpaque(false);
        centerPanel.add(chartPanelActions, BorderLayout.CENTER);
        main.add(centerPanel, BorderLayout.CENTER);

        // Derecha: Indicadores
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(250, 0));

        RoundedPanel semPanel = new RoundedPanel(25, new Color(44, 62, 80));
        semPanel.setLayout(new BorderLayout());
        JLabel lblSem = new JLabel("ESTADO SEGURIDAD", JLabel.CENTER);
        lblSem.setForeground(Color.WHITE);
        semPanel.add(lblSem, BorderLayout.NORTH);
        panelSemaphore = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int lvl = logic.fetchSecurityLevel();
                g.setColor(lvl == 0 ? Color.RED : new Color(50, 0, 0));
                g.fillOval(getWidth()/2-15, 20, 30, 30);
                g.setColor(lvl == 1 ? Color.YELLOW : new Color(50, 50, 0));
                g.fillOval(getWidth()/2-15, 60, 30, 30);
                g.setColor(lvl == 2 ? Color.GREEN : new Color(0, 50, 0));
                g.fillOval(getWidth()/2-15, 100, 30, 30);
            }
        };
        
        panelSemaphore.setOpaque(false);
        semPanel.add(panelSemaphore, BorderLayout.CENTER);
        rightPanel.add(semPanel);

        RoundedPanel failPanel = new RoundedPanel(25, new Color(44, 62, 80));
        failPanel.setLayout(new BorderLayout());
        JLabel lblFailT = new JLabel("INTENTOS FALLIDOS", JLabel.CENTER);
        lblFailT.setForeground(Color.WHITE);
        failPanel.add(lblFailT, BorderLayout.NORTH);
        JLabel lblFail = new JLabel(String.valueOf(logic.fetchFailedAttempts()), JLabel.CENTER);
        lblFail.setFont(new Font("Verdana", Font.BOLD, 48));
        lblFail.setForeground(new Color(231, 76, 60));
        failPanel.add(lblFail, BorderLayout.CENTER);
        rightPanel.add(failPanel);

        main.add(rightPanel, BorderLayout.EAST);
        add(main, BorderLayout.CENTER);
    }

    private void setupCharts() {
        DefaultCategoryDataset actionSet = new DefaultCategoryDataset();
        Map<Integer, int[]> actions = logic.fetchUserActions();
        for (Map.Entry<Integer, int[]> entry : actions.entrySet()) {
            actionSet.addValue(entry.getValue()[0], "Conexión", entry.getKey());
            actionSet.addValue(entry.getValue()[1], "Desconexión", entry.getKey());
        }
        if (actionSet.getColumnCount() == 0) actionSet.addValue(0, "Login", "00:00");
        
        JFreeChart stackedChart = ChartFactory.createStackedBarChart("ACCIONES DE USUARIO (24h)", "Hora", "Acciones", actionSet);
        ChartStyler.applyTheme(stackedChart, true);
        ChartPanel cpActions = new ChartPanel(stackedChart);
        cpActions.setBackground(new Color(44, 62, 80));
        chartPanelActions.add(cpActions);
    }

    private void setupFeed() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : logic.fetchFeed()) {
            model.addElement(item);
        }
        if (model.isEmpty()) model.addElement("No hay actividad reciente.");
        listFeed.setModel(model);
    }

    private JPanel chartPanelActions, panelSemaphore;
    private JList<String> listFeed;
}
