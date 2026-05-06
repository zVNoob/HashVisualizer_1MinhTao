package com.hashvis.table;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Test {
  private JFrame frame;
  private Table table;
  private boolean isSeparateChaining = true;
  private final int tableSize = 100; // Increased size to make scrolling more evident

  // UI Components
  private final JTextField inputField = new JTextField("Key", 10);
  private final JButton btnInsert = new JButton("Insert");
  private final JButton btnSearch = new JButton("Search");
  private final JButton btnReset = new JButton("Clear All");
  private final JComboBox<String> modeCombo = new JComboBox<>(new String[] { "Separate Chaining", "Open Addressing" });

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new Test().createAndShowGUI();
    });
  }

  private void createAndShowGUI() {
    frame = new JFrame("Hash Table Visualization - Scroll Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));
    frame.getContentPane().setBackground(new Color(240, 240, 240));

    // --- Control Panel ---
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
    controlPanel.setBackground(new Color(220, 220, 220));

    controlPanel.add(new JLabel("Mode:"));
    controlPanel.add(modeCombo);
    controlPanel.add(new JLabel(" Value:"));
    controlPanel.add(inputField);
    controlPanel.add(btnInsert);
    controlPanel.add(btnSearch);
    controlPanel.add(btnReset);

    modeCombo.addActionListener(e -> {
      isSeparateChaining = modeCombo.getSelectedIndex() == 0;
      initTable();
    });

    btnInsert.addActionListener(e -> handleInsert());
    btnSearch.addActionListener(e -> handleSearch());
    btnReset.addActionListener(e -> table.reset());

    // --- Table Area ---
    initTable();

    frame.add(controlPanel, BorderLayout.NORTH);
    frame.add(table, BorderLayout.CENTER);
    frame.setSize(1100, 700);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void initTable() {
    if (table != null)
      frame.remove(table);
    table = new Table(tableSize, isSeparateChaining);
    frame.add(table, BorderLayout.CENTER);
    frame.revalidate();
    frame.repaint();
  }

  private int calculateHash(String key) {
    return Math.abs(key.hashCode()) % tableSize;
  }

  private void handleInsert() {
    String val = inputField.getText();
    if (val.isEmpty())
      return;

    int hash = calculateHash(val);

    if (isSeparateChaining) {
      // 1. Jump to Row
      Row row = table.getRow(hash);
      table.scrollTo(row);

      // 2. Add Item
      Item newItem = row.addItem(val);

      // 3. Scroll to the specific new Item
      table.scrollTo(newItem);
    } else {
      // Open Addressing: Linear Probing
      int probeIndex = hash;
      Timer probeTimer = new Timer(300, null);
      final int[] currentProbe = { hash };

      probeTimer.addActionListener(e -> {
        Row row = table.getRow(currentProbe[0]);
        table.scrollTo(row); // Scroll to the currently probed row

        if (row.getItemsCount() == 0) {
          row.addItem(val);
          probeTimer.stop();
        } else {
          currentProbe[0] = (currentProbe[0] + 1) % tableSize;
          if (currentProbe[0] == hash) {
            JOptionPane.showMessageDialog(frame, "Table Full!");
            probeTimer.stop();
          }
        }
      });
      probeTimer.start();
    }
  }

  private void handleSearch() {
    String val = inputField.getText();
    if (val.isEmpty())
      return;

    int hash = calculateHash(val);

    if (isSeparateChaining) {
      Row row = table.getRow(hash);
      table.scrollTo(row);

      // Animate through the chain one by one
      Timer searchTimer = new Timer(500, null);
      final int[] itemIdx = { 0 };

      searchTimer.addActionListener(e -> {
        if (itemIdx[0] < row.getItemsCount()) {
          Item item = row.nextItem(); // Highlight the item
          table.scrollTo(item); // Scroll to the item
          itemIdx[0]++;
        } else {
          searchTimer.stop();
        }
      });
      searchTimer.start();
    } else {
      // Open Addressing search
      int probeIndex = hash;
      Timer probeTimer = new Timer(300, null);
      final int[] currentProbe = { hash };

      probeTimer.addActionListener(e -> {
        Row row = table.getRow(currentProbe[0]);
        table.scrollTo(row);

        // Logic: if it's the value we want, stop.
        // (For this simulation, we just probe until empty)
        if (row.getItemsCount() == 0) {
          probeTimer.stop();
        } else {
          currentProbe[0] = (currentProbe[0] + 1) % tableSize;
          if (currentProbe[0] == hash)
            probeTimer.stop();
        }
      });
      probeTimer.start();
    }
  }
}
