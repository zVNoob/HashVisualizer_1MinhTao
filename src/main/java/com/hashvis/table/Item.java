package com.hashvis.table;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Item extends JLabel {
  private final Color COLOR_IDLE = Color.ORANGE;
  private final Color COLOR_GLOW = Color.RED;
  private final int ANIMATION_DURATION = 300; // milliseconds

  private Color currentBorderColor = Color.BLACK;
  private final AnimatableBorder animBorder = new AnimatableBorder();
  private Timer animationTimer;

  private boolean ghosted = false;

  private Row row;

  public Item() {
    super();
    // Swing components are transparent by default; must be opaque to show
    // background color
    this.setOpaque(true);
    this.setBackground(Color.WHITE);
    this.setHorizontalAlignment(SwingConstants.CENTER);

    // Setup Compound Border:
    // Outer: Our custom animatable border
    // Inner: Padding (equivalent to setPadding in JavaFX)
    this.setBorder(new CompoundBorder(
        new EmptyBorder(4, 4, 4, 4), // <--- OUTER MARGIN (The gap between boxes)
        new CompoundBorder(
            animBorder, // The white box border
            new EmptyBorder(4, 4, 4, 4) // <--- INNER PADDING (Space inside the box)
        )));
    // this.setBorder(new CompoundBorder(
    // animBorder,
    // new EmptyBorder(5, 10, 5, 10)));
  }

  public Item(String text, Row row) {
    this();
    this.setText(text);
    this.row = row;
  }

  /**
   * Transitions the border color to Red
   */
  public void glow() {
    if (!ghosted)
      startAnimation(COLOR_GLOW);
  }

  /**
   * Transitions the border color to Black
   */
  public void deglow() {
    if (!ghosted)
      startAnimation(COLOR_IDLE);
  }

  public void reset() {
    if (!ghosted)
      startAnimation(Color.BLACK);
  }

  public void ghost() {
    startAnimation(Color.LIGHT_GRAY);
    this.setForeground(Color.LIGHT_GRAY);
    ghosted = true;
  }

  public boolean isGhosted() {
    return ghosted;
  }

  public void delete() {
    row.removeItem(this);
  }

  private void startAnimation(Color targetColor) {
    if (animationTimer != null && animationTimer.isRunning()) {
      animationTimer.stop();
    }

    final Color startColor = this.currentBorderColor;
    final long startTime = System.currentTimeMillis();

    animationTimer = new Timer(16, new ActionListener() { // ~60 FPS
      @Override
      public void actionPerformed(ActionEvent e) {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, (float) elapsed / ANIMATION_DURATION);

        // Linearly interpolate between start color and target color
        currentBorderColor = interpolateColor(startColor, targetColor, progress);
        animBorder.setColor(currentBorderColor);

        // Repaint the component to show the new color
        repaint();

        if (progress >= 1f) {
          animationTimer.stop();
        }
      }
    });
    animationTimer.start();
  }

  private Color interpolateColor(Color start, Color end, float progress) {
    int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
    int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
    int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
    return new Color(r, g, b);
  }

  @Override
  public String toString() {
    return this.getText();
  }
}
