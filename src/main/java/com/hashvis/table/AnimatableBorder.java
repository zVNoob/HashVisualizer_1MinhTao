package com.hashvis.table;

import java.awt.*;

/**
 * Custom Border class that allows changing the color without
 * recreating the border object.
 */
public class AnimatableBorder extends javax.swing.border.AbstractBorder {
  private Color color = Color.BLACK;

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(color);
    g2.setStroke(new BasicStroke(2f)); // Set border thickness
    g2.drawRect(x, y, width - 1, height - 1);
    g2.dispose();
  }

  @Override
  public Insets getBorderInsets(Component c) {
    return new Insets(1, 1, 1, 1);
  }
}
