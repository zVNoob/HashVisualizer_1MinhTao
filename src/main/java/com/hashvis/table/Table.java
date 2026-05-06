package com.hashvis.table;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    container.setBorder(new EmptyBorder(5, 5, 5, 5));

    container.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    // 2. Initialize the rows
    for (int i = 0; i < size; i++) {
      Row row = new Row(i);
      rows.add(row);
      // if (isSeperateChaining) {
      // Setup constraints for GridBagLayout
      if (isSeperateChaining) {
        gbc.gridx = 0; // Column 0
        gbc.gridy = i; // Row i
      } else {
        gbc.gridx = i / 10;
        gbc.gridy = i % 10;
      }
      gbc.anchor = GridBagConstraints.NORTHWEST; // <--- MAGIC: Forces alignment to the LEFT
      gbc.weightx = 0.0;
      gbc.insets = new Insets(0, 0, 10, 10); // Replaces VerticalStrut (10px bottom gap)

      container.add(row, gbc);
      if (isSeperateChaining) {
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        container.add(Box.createHorizontalStrut(0), gbc);
      }
    }

    // Add the last vertical strut
    gbc.gridx = 0;
    gbc.gridy = size;
    gbc.weightx = 0.0;
    gbc.weighty = 1.0;
    container.add(Box.createVerticalStrut(0), gbc);

    // 3. Setup the ScrollPane (this class)
    this.setViewportView(container);
    this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
  }

  public int tableSize() {
    return rows.size();
  }

  public void reset() {
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
      int y = centerInContainer.y - (this.getViewport().getHeight() / 2);

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
}
