package Dashboards;

import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {
    private int radius = 20;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(radius, radius);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundColor != null) {
            graphics.setColor(backgroundColor);
        } else {
            graphics.setColor(getBackground());
        }
        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
    }
}
