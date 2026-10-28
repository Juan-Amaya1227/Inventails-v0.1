package Dashboards;

import Dashboards.Logic.TrazabilidadLogic;
import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.chart.plot.PlotOrientation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.Map;

public class DashboardTrazabilidad extends JPanel {
    private TrazabilidadLogic logic;
    private Connection connection;

    public DashboardTrazabilidad(Connection conn) {
        this.connection = conn;
        this.logic = new TrazabilidadLogic(conn);
        initComponents();
        setupCharts();
        setupTable();
    }

    private void initComponents() {
        setBackground(new Color(44, 62, 80));
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("TRAZABILIDAD DEL GANADO");
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Izquiera: grafico de burbujas
        RoundedPanel leftPanel = new RoundedPanel(30, new Color(52, 73, 94));
        leftPanel.setLayout(new BorderLayout());
        chartPanelBubble = new JPanel(new BorderLayout());
        chartPanelBubble.setOpaque(false);
        leftPanel.add(chartPanelBubble, BorderLayout.CENTER);
        mainPanel.add(leftPanel);

        // Dereche: KPI + Tabla
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        rightPanel.setOpaque(false);

        RoundedPanel kpiPanel = new RoundedPanel(30, new Color(52, 73, 94));
        kpiPanel.setLayout(new BorderLayout());
        chartPanelRing = new JPanel(new BorderLayout());
        chartPanelRing.setOpaque(false);
        kpiPanel.add(chartPanelRing, BorderLayout.CENTER);
        rightPanel.add(kpiPanel);

        RoundedPanel tablePanel = new RoundedPanel(30, new Color(52, 73, 94));
        tablePanel.setLayout(new BorderLayout());
        JLabel tableTitle = new JLabel("TOP 5 RAZAS PRINCIPALES", JLabel.CENTER);
        tableTitle.setForeground(Color.LIGHT_GRAY);
        tableTitle.setFont(new Font("Verdana", Font.BOLD, 14));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        tableRazas = new JTable();
        JScrollPane scroll = new JScrollPane(tableRazas);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        tablePanel.add(scroll, BorderLayout.CENTER);
        rightPanel.add(tablePanel);

        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupCharts() {
        // Grafico de burbujas
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        Map<String, Integer> bubbleData = logic.fetchBubbleData();
        int i = 0;
        for (Map.Entry<String, Integer> entry : bubbleData.entrySet()) {
            double[][] series = {{i++}, {entry.getValue()}, {entry.getValue() * 0.5}};
            dataset.addSeries(entry.getKey(), series);
        }
        JFreeChart bubbleChart = ChartFactory.createBubbleChart("BURBUJAS DEL GANADO", "Ubicación", "Cantidad", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartStyler.applyTheme(bubbleChart, true);
        ChartPanel cpBubble = new ChartPanel(bubbleChart);
        cpBubble.setBackground(new Color(52, 73, 94));
        chartPanelBubble.add(cpBubble);

        // Grafico de Dona(KPI)
        
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Integer> conteosEstado = logic.fetchConteosPorEstado();
        int activosReales = conteosEstado.getOrDefault("Activos", 0);
        int inactivosReales = conteosEstado.getOrDefault("Inactivos", 0);
        pieDataset.setValue("Activos", activosReales);
        pieDataset.setValue("Inactivos", inactivosReales);
        JFreeChart ringChart = ChartFactory.createRingChart("TOTAL ANIMALES ACTIVOS", pieDataset, false, true, false);
        ChartStyler.applyTheme(ringChart, true);
        ChartPanel cpRing = new ChartPanel(ringChart);
        cpRing.setBackground(new Color(52, 73, 94));
        chartPanelRing.add(cpRing);
    }

    private void setupTable() {
        String[] cols = {"Raza", "Propósito", "Cantidad"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Object[] row : logic.fetchTop5Razas()) {
            model.addRow(row);
        }
        tableRazas.setModel(model);
        tableRazas.setBackground(new Color(52, 73, 94));
        tableRazas.setForeground(Color.WHITE);
        tableRazas.setRowHeight(30);
    }

    private JPanel chartPanelBubble, chartPanelRing;
    private JTable tableRazas;
}
