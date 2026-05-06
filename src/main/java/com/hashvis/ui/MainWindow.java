package com.hashvis.ui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.hashvis.hashalgo.HashAlgorithmVisualizer;
import com.hashvis.table.Table;

public class MainWindow extends JPanel {

  private final Table hashTable;
  private final HashAlgorithmVisualizer visualizer;
  private ControlPanel controlPanel;
  private JSplitPane mainSplitPane;

  public MainWindow() {
    super();

    setLayout(new BorderLayout(10, 10));
    this.hashTable = new Table(10, true);

    // 2. Initialize the Visualizer (inject the table)
    this.visualizer = new HashAlgorithmVisualizer(hashTable);

    this.controlPanel = new ControlPanel();

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    leftPanel.add(controlPanel);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(visualizer);

    mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, hashTable);
    mainSplitPane.setDividerLocation(400);
    mainSplitPane.setBorder(null);

    add(mainSplitPane, BorderLayout.CENTER);
  }
}
