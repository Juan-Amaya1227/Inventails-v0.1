package Dashboards;

import Dashboards.Logic.FinancieroLogic;
import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.Map;

public class DashboardFinanciero extends JPanel {
    private FinancieroLogic logic;
    private Connection connection;

    public DashboardFinanciero(Connection conn) {
        this.connection = conn;
        this.logic = new FinancieroLogic(conn);
        initComponents();
        setupCharts();
    }

    private void initComponents() {
        setBackground(new Color(26, 37, 47));
        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("GESTIÓN FINANCIERA");
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(new Color(0, 255, 255));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        RoundedPanel saldoPanel = new RoundedPanel(20, new Color(44, 62, 80));
        saldoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));
        JLabel lblSaldoTit = new JLabel("SALDO ACTUAL: ");
        lblSaldoTit.setFont(new Font("Verdana", Font.PLAIN, 18));
        lblSaldoTit.setForeground(Color.WHITE);
        JLabel lblSaldoVal = new JLabel("$" + String.format("%,.2f", logic.fetchSaldoActual()));
        lblSaldoVal.setFont(new Font("Verdana", Font.BOLD, 48));
        lblSaldoVal.setForeground(new Color(39, 174, 96));
        saldoPanel.add(lblSaldoTit);
        saldoPanel.add(lblSaldoVal);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centerPanel.add(saldoPanel, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);

        RoundedPanel leftChart = new RoundedPanel(25, new Color(44, 62, 80));
        leftChart.setLayout(new BorderLayout());
        chartPanelFlow = new JPanel(new BorderLayout());
        chartPanelFlow.setOpaque(false);
        leftChart.add(chartPanelFlow);
        chartsPanel.add(leftChart);

        RoundedPanel rightChart = new RoundedPanel(25, new Color(44, 62, 80));
        rightChart.setLayout(new BorderLayout());
        chartPanelExpenses = new JPanel(new BorderLayout());
        chartPanelExpenses.setOpaque(false);
        rightChart.add(chartPanelExpenses);
        chartsPanel.add(rightChart);

        centerPanel.add(chartsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupCharts() {
    XYSeries seriesCompras = new XYSeries("Compras");
    XYSeries seriesVentas = new XYSeries("Ventas");
    Map<String, double[]> flowData = logic.fetchFlujoCaja();
    
    // Iterar sobre las llaves estructuradas "Mes X"
    for (Map.Entry<String, double[]> entry : flowData.entrySet()) {
        // Extraer el número de mes real del String "Mes 1", "Mes 2"...
        int numeroMes = Integer.parseInt(entry.getKey().replace("Mes ", ""));
        double[] values = entry.getValue();
        
        seriesCompras.add(numeroMes, values[0]);
        seriesVentas.add(numeroMes, values[1]);
    }
    
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesCompras);
        dataset.addSeries(seriesVentas);
        JFreeChart lineChart = ChartFactory.createXYLineChart("FLUJO DE CAJA (Mensual)", "Mes", "Valor", dataset);
        ChartStyler.applyTheme(lineChart, true);
        ChartPanel cpFlow = new ChartPanel(lineChart);
        cpFlow.setBackground(new Color(44, 62, 80));
        chartPanelFlow.add(cpFlow);

        // Pie/Donut Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Double> expenses = logic.fetchGastos();
        for (Map.Entry<String, Double> entry : expenses.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart donutChart = ChartFactory.createRingChart("COMPOSICIÓN DE GASTOS", pieDataset, true, true, false);
        ChartStyler.applyTheme(donutChart, true);
        ChartPanel cpExp = new ChartPanel(donutChart);
        cpExp.setBackground(new Color(44, 62, 80));
        chartPanelExpenses.add(cpExp);
    }

    private JPanel chartPanelFlow, chartPanelExpenses;
}
