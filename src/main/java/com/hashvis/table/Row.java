package com.hashvis.table;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class Row extends JPanel {
  // UI components
  private final JLabel indexLabel = new JLabel();
  private final JPanel contentPanel = new JPanel();

  // Logic components
  private final ArrayList<Item> items = new ArrayList<>();
  private int currentIndex = -1;
  private int prevIndex = -1;
  private boolean isChosen = false;

  // Row-level Animation (Border)
  private final Color COLOR_IDLE = new Color(0, 160, 0);
  private final Color COLOR_GLOW = new Color(0, 128, 255); // Blueish glow
  private final int ANIMATION_DURATION = 300;

  private Color currentBorderColor = COLOR_IDLE;
  private final AnimatableBorder rowBorder = new AnimatableBorder();
  private Timer animationTimer;

  public Row(int index) {
    super();

    // 1. Layout Setup
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.setOpaque(false);

    // 2. Index Label Setup
    indexLabel.setText(String.valueOf(index));
    indexLabel.setPreferredSize(new Dimension(30, 35));
    indexLabel.setMaximumSize(new Dimension(30, 35));
    indexLabel.setHorizontalAlignment(SwingConstants.LEFT);
    this.add(indexLabel);
    JLabel colon = new JLabel(":");
    colon.setHorizontalAlignment(SwingConstants.RIGHT);
    this.add(colon);
    this.add(Box.createHorizontalStrut(5));
    // 3. Content Panel Setup (Holds the items)
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
    contentPanel.setOpaque(false);

    this.add(contentPanel);

    // 5. Row Border Setup
    this.setBorder(new CompoundBorder(
        rowBorder,
        new EmptyBorder(2, 5, 2, 5)));
  }

  @Override
  public String toString() {
    return "Bucket " + indexLabel.getText();
  }

  private void updateIndex() {
    if (currentIndex == prevIndex)
      return;

    if (prevIndex != -1 && prevIndex < items.size()) {
      items.get(prevIndex).deglow();
    }

    if (currentIndex != -1 && currentIndex < items.size()) {
      if (isChosen) {
        items.get(currentIndex).glow();
      } else {
        items.get(currentIndex).deglow();
      }
    }
    prevIndex = currentIndex;
  }

  public int currentIndex() {
    return currentIndex;
  }

  public int maxIndex() {
    return items.size();
  }

  public Item getItem(int index) {
    return items.get(index);
  }

  public Item nextItem() {
    currentIndex++;
    if (currentIndex >= items.size()) {
      updateIndex();
      return null;
    }
    updateIndex();
    return items.get(currentIndex);
  }

  public int getItemsCount() {
    return items.size();
  }

  public void choose() {
    isChosen = true;
    startAnimation(COLOR_GLOW);
    updateIndex();
  }

  public void unchoose() {
    int temp = currentIndex;
    isChosen = false;
    currentIndex = -1;
    startAnimation(COLOR_IDLE);
    updateIndex();
    currentIndex = temp;
  }

  public void reset() {
    currentIndex = -1;
    prevIndex = -1;
    isChosen = false;
    startAnimation(Color.BLACK);
    for (Item item : items) {
      item.reset();
    }
  }

  public Item addItem(String text) {
    Item item = new Item(text, this);
    items.add(item);
    contentPanel.add(item);
    currentIndex = items.size() - 1;
    updateIndex();

    // In Swing, we must tell the container to recalculate layout
    contentPanel.revalidate();
    contentPanel.repaint();
    return item;
  }

  public void removeItem(Item item) {
    items.remove(item);
    contentPanel.remove(item);
    contentPanel.revalidate();
    contentPanel.repaint();
  }

  // =========================================================================
  // ANIMATION LOGIC (Same as Item class)
  // =========================================================================
  private void startAnimation(Color targetColor) {
    if (animationTimer != null && animationTimer.isRunning()) {
      animationTimer.stop();
    }

    final Color startColor = this.currentBorderColor;
    final long startTime = System.currentTimeMillis();

    animationTimer = new Timer(16, e -> {
      long elapsed = System.currentTimeMillis() - startTime;
      float progress = Math.min(1f, (float) elapsed / ANIMATION_DURATION);

      currentBorderColor = interpolateColor(startColor, targetColor, progress);
      rowBorder.setColor(currentBorderColor);
      repaint();

      if (progress >= 1f)
        animationTimer.stop();
    });
    animationTimer.start();
  }

  private Color interpolateColor(Color start, Color end, float progress) {
    int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
    int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
    int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
    return new Color(r, g, b);
  }
}
