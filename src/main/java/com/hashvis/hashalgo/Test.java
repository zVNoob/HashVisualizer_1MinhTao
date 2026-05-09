package com.hashvis.hashalgo;

import com.hashvis.table.Table;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Test extends JFrame {

  public Test() {
    setTitle("Hash Algorithm Visualizer Manual Test");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 800);
    setLayout(new BorderLayout());

    // 1. Setup dependencies
    Table mockTable = new Table(10, false); // Dummy table
    HashAlgorithmVisualizer visualizer = new HashAlgorithmVisualizer();

    // 2. Add visualizer to center
    add(visualizer, BorderLayout.CENTER);

    // 3. Setup Control Panel
    JPanel controlPanel = new JPanel();
    controlPanel.setBackground(Color.LIGHT_GRAY);

    JButton resetBtn = new JButton("Load/Reset Code");
    JButton nextBtn = new JButton("Next Step →");
    JLabel statusLabel = new JLabel("Status: Ready");

    // Action: Reset and load sample code
    resetBtn.addActionListener(e -> {
      ArrayList<String> sampleCode = new ArrayList<>(Arrays.asList(
          "s = 'hello'",
          "n = len(s)",
          "sum = 0",
          "for i in range(n):",
          "    sum += hash(s[i])",
          "return sum",
          "print('Done!')",
          "extra_line_to_test_scrolling()",
          "another_line()",
          "last_line()"));
      visualizer.reset(sampleCode);
      statusLabel.setText("Status: Code Loaded");
    });

    // Action: Step forward
    nextBtn.addActionListener(e -> {
      boolean canContinue = visualizer.next();
      if (canContinue) {
        statusLabel.setText("Status: Stepping through...");
      } else {
        statusLabel.setText("Status: Execution Finished");
      }
    });

    controlPanel.add(resetBtn);
    controlPanel.add(nextBtn);
    controlPanel.add(statusLabel);

    add(controlPanel, BorderLayout.SOUTH);

    setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }
      new Test().setVisible(true);
    });
  }
}
