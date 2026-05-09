package com.hashvis.table;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class Table extends JScrollPane {
  private final JPanel container;
  private final List<Row> rows = new ArrayList<>();
  private int currentIndex = -1;

  public Table(int size, boolean isSeperateChaining) {
    super();

    // 1. Setup the container panel
    container = new JPanel();
    container.setOpaque(false);
    if (!isSeperateChaining)
      container.setLayout(new WrapLayout());

    container.setBorder(new EmptyBorder(5, 5, 5, 5));

    if (isSeperateChaining) {
      container.setLayout(new GridBagLayout());
    }
    // 2. Initialize the rows
    for (int i = 0; i < size; i++) {
      Row row = new Row(i);
      rows.add(row);

      // Setup constraints for GridBagLayout
      if (isSeperateChaining) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Column 0
        gbc.gridy = i; // Row i
        gbc.anchor = GridBagConstraints.NORTHWEST; // <--- MAGIC: Forces alignment to the LEFT
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, 0, 10, 10); // Replaces VerticalStrut (10px bottom gap)
        container.add(row, gbc);
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        container.add(Box.createHorizontalStrut(0), gbc);
      } else
        container.add(row);
    }

    // 3. Setup the ScrollPane (this class)
    this.setViewportView(container);
    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.getViewport().addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        // Force the internal panel to be exactly the width of the visible area
        Dimension pref = container.getLayout().preferredLayoutSize(container);
        container.setPreferredSize(new Dimension(getViewport().getWidth(), pref.height));
        container.revalidate();
      }
    });
  }

  public int tableSize() {
    return rows.size();
  }

  public int currentIndex() {
    return currentIndex;
  }

  public void reset() {
    resetHighlight();
    for (Row row : rows) {
      row.reset();
    }
  }

  public void resetHighlight() {
    if (currentIndex != -1 && currentIndex < rows.size()) {
      rows.get(currentIndex).unchoose();
    }
    currentIndex = -1;
  }

  public void scrollTo(Item item) {
    if (item == null)
      return;

    SwingUtilities.invokeLater(() -> {
      // 1. Find the center of the item relative to the Table's container
      Point itemCenter = new Point(item.getWidth() / 2, item.getHeight() / 2);
      Point centerInContainer = SwingUtilities.convertPoint(item, itemCenter, this.container);

      // 2. Calculate the top-left corner of the viewport needed to center the item
      // x = center of item - half of the visible window width
      int x = Math.max(centerInContainer.x - (this.getViewport().getWidth() / 2), 0);
      // y = center of item - half of the visible window height
      int y = Math.max(centerInContainer.y - (this.getViewport().getHeight() / 2), 0);

      // 3. Directly move the viewport to that position
      this.getViewport().setViewPosition(new Point(x, y));
    });
  }

  public void scrollTo(Row row) {
    if (row == null)
      return;

    SwingUtilities.invokeLater(() -> {
      // 1. Find the center of the row relative to the Table's container
      Point rowCenter = new Point(row.getWidth() / 2, row.getHeight() / 2);
      Point centerInContainer = SwingUtilities.convertPoint(row, rowCenter, this.container);

      // 2. Calculate the viewport position
      int y = centerInContainer.y - (this.getViewport().getHeight() / 2);
      y = Math.max(y, 0);
      // 3. Directly move the viewport
      this.getViewport().setViewPosition(new Point(0, y));
    });
  }

  public Row getRow(int index) {
    resetHighlight();
    if (index < 0 || index >= rows.size()) {
      return null;
    }
    rows.get(index).choose();
    currentIndex = index;
    return rows.get(index);
  }

  private static class WrapLayout extends FlowLayout {
    WrapLayout() {
      super(FlowLayout.LEFT, 10, 10);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
      synchronized (target.getTreeLock()) {
        int targetWidth = target.getWidth();
        if (targetWidth <= 0)
          targetWidth = 400;

        int x = 0;
        int y = 0;
        int rowHeight = 0;

        for (Component comp : target.getComponents()) {
          if (comp.isVisible()) {
            Dimension d = comp.getPreferredSize();
            // Wrap if the component exceeds the current target width
            if (x + d.width > targetWidth && x > 0) {
              x = 0;
              y += rowHeight + 10;
              rowHeight = 0;
            }
            x += d.width + 10;
            rowHeight = Math.max(rowHeight, d.height);
          }
        }
        // We return exactly the width we were given and the calculated height
        return new Dimension(targetWidth, y + rowHeight + 10);
      }
    }

    @Override
    public void layoutContainer(Container target) {
      synchronized (target.getTreeLock()) {
        int targetWidth = target.getWidth();
        if (targetWidth <= 0)
          targetWidth = 400;

        int x = 10;
        int y = 10;
        int rowHeight = 0;

        for (Component comp : target.getComponents()) {
          if (comp.isVisible()) {
            Dimension d = comp.getPreferredSize();
            // EXACT SAME WRAPPING LOGIC AS preferredLayoutSize
            if (x + d.width > targetWidth && x > 0) {
              x = 10;
              y += rowHeight + 10;
              rowHeight = 0;
            }
            // Set the actual bounds of the component on screen
            comp.setBounds(x, y, d.width, d.height);
            x += d.width + 10;
            rowHeight = Math.max(rowHeight, d.height);
          }
        }
      }
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
      return preferredLayoutSize(target);
    }
  }
}
