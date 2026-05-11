package com.hashvis.hashalgo;

import com.hashvis.codepane.CodePane;
import com.hashvis.codepane.parser.SymbolTable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class HighlightedCodePane extends JPanel {
  private final JLabel codePane;
  private final MarkerComponent marker;
  private final Timer animationTimer;

  private Color targetColor = Color.WHITE;
  private Color currentColor = Color.WHITE;

  // Animation settings
  private final float step = 0.05f; // Speed of color transition (0.0 to 1.0)

  public HighlightedCodePane() {
    this("sum(len(s))");
  }

  public HighlightedCodePane(String text) {
    // Layout Setup
    // setLayout(new BorderLayout(10, 0));
    setLayout(new GridBagLayout());
    setAlignmentY(TOP_ALIGNMENT);
    setBackground(Color.DARK_GRAY); // Match the visual style of the original

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0;
    gbc.weighty = 0.0;
    // Marker (The Polygon replacement)
    marker = new MarkerComponent();
    marker.setAlignmentY(Component.CENTER_ALIGNMENT);
    add(marker, gbc);

    // CodePane (The JTextPane replacement)
    codePane = new JLabel(text);
    codePane.setForeground(Color.WHITE);
    codePane.setAlignmentY(Component.CENTER_ALIGNMENT);
    gbc.gridx = 1;
    add(codePane, gbc);
    gbc.weightx = 1.0;
    gbc.gridx = 2;
    add(Box.createHorizontalStrut(10), gbc);

    // Animation Timer (Replacement for FX Timeline)
    // Runs every 16ms (~60 FPS)
    animationTimer = new Timer(16, e -> updateAnimation());
    animationTimer.start();
  }

  @Override
  public Dimension getPreferredSize() {
    // Width = marker width + codePane width; Height = codePane height
    return new Dimension(
        marker.getPreferredSize().width + codePane.getPreferredSize().width,
        codePane.getPreferredSize().height);
  }

  @Override
  public Dimension getMaximumSize() {
    // This prevents BoxLayout from stretching the component vertically
    // We allow it to grow horizontally (Integer.MAX_VALUE) but lock the height
    return new Dimension(Integer.MAX_VALUE, codePane.getPreferredSize().height);
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(0, codePane.getPreferredSize().height);
  }

  public void glow() {
    targetColor = new Color(1.0f, 0.5f, 0.0f, 1.0f); // Orange
  }

  public void deglow() {
    targetColor = Color.WHITE;
  }

  private void updateAnimation() {
    // Linear interpolation between current color and target color
    int r = interpolate(currentColor.getRed(), targetColor.getRed());
    int g = interpolate(currentColor.getGreen(), targetColor.getGreen());
    int b = interpolate(currentColor.getBlue(), targetColor.getBlue());

    currentColor = new Color(r, g, b);
    marker.setCurrentColor(currentColor);
    marker.repaint();
  }

  private int interpolate(int start, int end) {
    float diff = end - start;
    if (Math.abs(diff) < 1)
      return end;
    return (int) (start + diff * step);
  }

  /**
   * Custom Component to draw the Triangle (Polygon)
   */
  private static class MarkerComponent extends JComponent {
    private Color currentColor = Color.WHITE;

    public void setCurrentColor(Color color) {
      this.currentColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2d.setColor(currentColor);

      // Recreating the Polygon points from FXML:
      // FX points: -50, 24, -50, 46, -35, 35
      // We translate these to a small component size
      Path2D triangle = new Path2D.Double();
      triangle.moveTo(0, 0); // Top left
      triangle.lineTo(0, 14); // Bottom left
      triangle.lineTo(7, 7); // Tip (Right)
      triangle.closePath();

      g2d.fill(triangle);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(10, 20);
    }

    @Override
    public Dimension getMinimumSize() {
      return new Dimension(10, 20);
    }
  }
}
