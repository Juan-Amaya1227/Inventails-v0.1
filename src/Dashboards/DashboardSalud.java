package Dashboards;

import Dashboards.Logic.SaludLogic;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Map;

public class DashboardSalud extends JPanel {
    private SaludLogic logic;
    private Connection connection;

    public DashboardSalud(Connection conn) {
        this.connection = conn;
        this.logic = new SaludLogic(conn);
        initComponents();
        setupCharts();
    }

    private void initComponents() {
        setBackground(new Color(26, 37, 47));
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setOpaque(false);
        JLabel title = new JLabel("SALUD Y PRODUCTIVIDAD");
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(new Color(0, 255, 255));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(2, 2, 20, 20));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel p1 = new RoundedPanel(25, new Color(44, 62, 80));
        p1.setLayout(new BorderLayout());
        chartPanelDeath = new JPanel(new BorderLayout());
        chartPanelDeath.setOpaque(false);
        p1.add(chartPanelDeath);
        main.add(p1);

        RoundedPanel p2 = new RoundedPanel(25, new Color(44, 62, 80));
        p2.setLayout(new BorderLayout());
        chartPanelMeds = new JPanel(new BorderLayout());
        chartPanelMeds.setOpaque(false);
        p2.add(chartPanelMeds);
        main.add(p2);

        RoundedPanel p3 = new RoundedPanel(25, new Color(44, 62, 80));
        p3.setLayout(new BorderLayout());
        chartPanelDisease = new JPanel(new BorderLayout());
        chartPanelDisease.setOpaque(false);
        p3.add(chartPanelDisease);
        main.add(p3);

        add(main, BorderLayout.CENTER);
    }

    private void setupCharts() {
        // Bar Chart (Horizontal) - Defunciones
        DefaultCategoryDataset deathSet = new DefaultCategoryDataset();
        Map<String, Integer> deathData = logic.fetchCausasDefuncion();
        for (Map.Entry<String, Integer> entry : deathData.entrySet()) {
            deathSet.addValue(entry.getValue(), "Causas", entry.getKey());
        }
        JFreeChart barChart = ChartFactory.createBarChart("CAUSAS DE DEFUNCIÓN", "Causa", "Cantidad", deathSet, org.jfree.chart.plot.PlotOrientation.HORIZONTAL, false, true, false);
        ChartStyler.applyTheme(barChart, true);
        ChartPanel cpDeath = new ChartPanel(barChart);
        cpDeath.setBackground(new Color(44, 62, 80));
        chartPanelDeath.add(cpDeath);

        // Line Chart - Enfermedades
        DefaultCategoryDataset diseaseSet = new DefaultCategoryDataset();
        for (Object[] row : logic.fetchEnfermedadesTiempo()) {
            if (row != null && row.length >= 2) {
                diseaseSet.addValue(Double.valueOf(row[1].toString()), "Enfermedades", row[0].toString());
            }
        }
        if (diseaseSet.getColumnCount() == 0) diseaseSet.addValue(0, "Enfermedades", "Sin Datos");
        
        JFreeChart lineChart = ChartFactory.createLineChart("ENFERMEDADES REGISTRADAS", "Tiempo", "Casos", diseaseSet);
        ChartStyler.applyTheme(lineChart, true);
        ChartPanel cpDisease = new ChartPanel(lineChart);
        cpDisease.setBackground(new Color(44, 62, 80));
        chartPanelDisease.add(cpDisease);

        // Bar Chart - Medicamentos
        DefaultCategoryDataset medSet = new DefaultCategoryDataset();
        Map<String, Integer> meds = logic.fetchMedicamentos();
        for (Map.Entry<String, Integer> entry : meds.entrySet()) {
            medSet.addValue(entry.getValue(), "Uso", entry.getKey());
        }
        if (medSet.getColumnCount() == 0) medSet.addValue(0, "Uso", "Sin Datos");

        JFreeChart medChart = ChartFactory.createBarChart("USO DE MEDICAMENTOS", "Medicina", "Dosis", medSet, org.jfree.chart.plot.PlotOrientation.VERTICAL, false, true, false);
        ChartStyler.applyTheme(medChart, true);
        ChartPanel cpMeds = new ChartPanel(medChart);
        cpMeds.setBackground(new Color(44, 62, 80));
        chartPanelMeds.add(cpMeds);
    }

    private JPanel chartPanelDeath, chartPanelDisease, chartPanelMeds;
}
