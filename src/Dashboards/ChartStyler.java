package Dashboards;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.block.BlockBorder;
import java.awt.*;

public class ChartStyler {
    public static void applyTheme(JFreeChart chart, boolean isDark) {
        Color bgColor = isDark ? new Color(44, 62, 80) : Color.WHITE;
        Color fgColor = isDark ? Color.WHITE : new Color(44, 62, 80);
        
        chart.setBackgroundPaint(bgColor);
        Plot plot = chart.getPlot();
        plot.setBackgroundPaint(bgColor);
        plot.setOutlineVisible(false);

        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(fgColor);
            chart.getTitle().setFont(new Font("Verdana", Font.BOLD, 16));
        }

        if (plot instanceof CategoryPlot) {
            CategoryPlot cPlot = (CategoryPlot) plot;
            cPlot.setRangeGridlinePaint(new Color(127, 140, 141));
            cPlot.getDomainAxis().setTickLabelPaint(fgColor);
            cPlot.getDomainAxis().setLabelPaint(fgColor);
            cPlot.getRangeAxis().setTickLabelPaint(fgColor);
            cPlot.getRangeAxis().setLabelPaint(fgColor);
            
            if (cPlot.getRenderer() instanceof BarRenderer) {
                BarRenderer renderer = (BarRenderer) cPlot.getRenderer();
                renderer.setBarPainter(new StandardBarPainter());
                renderer.setSeriesPaint(0, new Color(52, 152, 219)); 
                renderer.setShadowVisible(false);
            } else if (cPlot.getRenderer() instanceof org.jfree.chart.renderer.category.LineAndShapeRenderer) {
                org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = (org.jfree.chart.renderer.category.LineAndShapeRenderer) cPlot.getRenderer();
                renderer.setSeriesPaint(0, new Color(231, 76, 60));
                renderer.setSeriesStroke(0, new BasicStroke(3.0f));
            }
        } else if (plot instanceof XYPlot) {
            XYPlot xPlot = (XYPlot) plot;
            xPlot.setDomainGridlinePaint(new Color(127, 140, 141));
            xPlot.setRangeGridlinePaint(new Color(127, 140, 141));
            xPlot.getDomainAxis().setTickLabelPaint(fgColor);
            xPlot.getDomainAxis().setLabelPaint(fgColor);
            xPlot.getRangeAxis().setTickLabelPaint(fgColor);
            xPlot.getRangeAxis().setLabelPaint(fgColor);
        }

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(bgColor);
            chart.getLegend().setItemPaint(fgColor);
            chart.getLegend().setFrame(BlockBorder.NONE);
        }
    }
}
