package PieceIcons;

import javax.swing.*;
import java.awt.*;

public class CircleIcon implements Icon {
    private int diameter;
    private Color color;

    public CircleIcon(int diameter, Color color) {
        this.diameter = diameter;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3));
        int offset = (getIconWidth() - diameter) / 2;
        g2.drawOval(x + offset, y + offset, diameter, diameter);
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return 40;
    }

    @Override
    public int getIconHeight() {
        return 40;
    }
}