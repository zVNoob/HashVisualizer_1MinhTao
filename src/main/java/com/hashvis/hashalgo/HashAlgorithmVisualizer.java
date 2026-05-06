package com.hashvis.hashalgo;

import com.hashvis.table.Table;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HashAlgorithmVisualizer extends JPanel {
  // UI Components
  private final JPanel codeArea;
  private final JScrollPane codeScrollPane;
  private final JLabel statusLabel;
  private final JLabel headerLabel;

  // Logic Components
  private final ArrayList<HighlightedCodePane> sourceCodes = new ArrayList<>();
  private final HashMap<String, Object> variables = new HashMap<>();
  private HashAlgorithmSymbolTable symbolTable;
  private int currentHighlighted = -1;

  public HashAlgorithmVisualizer(Table table) {
    // 1. General Layout Setup
    setLayout(new BorderLayout(10, 10));
    setBackground(Color.DARK_GRAY);

    // 2. Header Label ("Code")
    headerLabel = new JLabel("Code", SwingConstants.CENTER);
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

    // 3. Code Area Setup (The "VBox" replacement)
    codeArea = new JPanel();
    codeArea.setLayout(new BoxLayout(codeArea, BoxLayout.Y_AXIS));
    codeArea.setBackground(Color.DARK_GRAY);

    // 4. ScrollPane Setup
    codeScrollPane = new JScrollPane(codeArea);
    codeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    codeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    codeScrollPane.setBorder(BorderFactory.createEmptyBorder());

    // 5. Status Label
    statusLabel = new JLabel("Status: ");
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

    // 6. Assemble the main layout
    JPanel topContainer = new JPanel(new BorderLayout());
    topContainer.setOpaque(false);
    topContainer.add(headerLabel, BorderLayout.NORTH);
    topContainer.add(codeScrollPane, BorderLayout.CENTER);

    JPanel bottomContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bottomContainer.setOpaque(false);
    bottomContainer.add(statusLabel);

    add(topContainer, BorderLayout.CENTER);
    add(bottomContainer, BorderLayout.SOUTH);

    // Initialize logic
    this.symbolTable = new HashAlgorithmSymbolTable(variables, table);
  }

  public void reset(ArrayList<String> sourceCodesText) {
    // Clear existing components from the codeArea
    codeArea.removeAll();
    this.sourceCodes.clear();

    // Add new HighlightedCodePanes
    for (String line : sourceCodesText) {
      HighlightedCodePane pane = new HighlightedCodePane(symbolTable, line, true);
      codeArea.add(pane);
      this.sourceCodes.add(pane);
    }

    // Refresh UI
    codeArea.revalidate();
    codeArea.repaint();

    if (!this.sourceCodes.isEmpty()) {
      currentHighlighted = 0;
      symbolTable.resetInstructionCount();
      this.sourceCodes.get(currentHighlighted).glow();
    }

    variables.clear();
    variables.put("i", 10);
    // Variable table update logic removed as per request
  }

  public boolean next() {
    if (currentHighlighted == -1)
      return false;

    // Logic from original code
    // sourceCodes.get(currentHighlighted).eval();

    sourceCodes.get(currentHighlighted).deglow();
    symbolTable.incrementInstructionCount();

    currentHighlighted = symbolTable.getInstructionCount();

    if (currentHighlighted >= sourceCodes.size()) {
      currentHighlighted = -1;
    }

    if (currentHighlighted == -1) {
      return false;
    }

    // Highlight the next line and scroll to it
    HighlightedCodePane nextPane = sourceCodes.get(currentHighlighted);
    nextPane.glow();
    scrollToLine(nextPane);

    return true;
  }

  private void scrollToLine(HighlightedCodePane node) {
    // Swing's equivalent of Platform.runLater is SwingUtilities.invokeLater
    SwingUtilities.invokeLater(() -> {
      JViewport viewport = codeScrollPane.getViewport();
      int viewportHeight = viewport.getHeight();

      // The Y position of the component relative to the codeArea
      int targetY = node.getY();
      int nodeHeight = node.getHeight();

      // Calculate the position to center the node in the viewport
      int desiredTop = targetY - (viewportHeight / 2) + (nodeHeight / 2);

      // Ensure we don't scroll into negative values
      int scrollPosition = Math.max(0, desiredTop);

      codeScrollPane.getVerticalScrollBar().setValue(scrollPosition);
    });
  }
}
