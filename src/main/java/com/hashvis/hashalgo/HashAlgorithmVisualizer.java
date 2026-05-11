package com.hashvis.hashalgo;

import com.hashvis.collision.CollisionResolver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HashAlgorithmVisualizer extends JPanel {
  // UI Components
  private final JPanel codeArea;
  private final JScrollPane codeScrollPane;
  private final JLabel statusLabel;
  private final JLabel headerLabel;

  // Logic Components
  private final ArrayList<HighlightedCodePane> sourceCodes = new ArrayList<>();
  private int currentHighlighted = 0;

  public HashAlgorithmVisualizer() {
    // 2. General Layout Setup
    setLayout(new BorderLayout(11, 10));
    setBackground(Color.DARK_GRAY);

    // 3. Header Label ("Code")
    headerLabel = new JLabel("Code", SwingConstants.CENTER);
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
    headerLabel.setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 0));

    // 4. Code Area Setup (The "VBox" replacement)
    codeArea = new JPanel();
    codeArea.setLayout(new BoxLayout(codeArea, BoxLayout.Y_AXIS));
    codeArea.setBackground(Color.DARK_GRAY);

    // 5. ScrollPane Setup
    codeScrollPane = new JScrollPane(codeArea);
    codeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    codeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    codeScrollPane.setBorder(BorderFactory.createEmptyBorder());

    // 6. Status Label
    statusLabel = new JLabel("Status: ");
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
    statusLabel.setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 0));

    // 7. Assemble the main layout

    JPanel topContainer = new JPanel();
    topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
    topContainer.setOpaque(false);
    topContainer.add(headerLabel);
    topContainer.add(statusLabel);

    add(topContainer, BorderLayout.NORTH);

    add(codeScrollPane, BorderLayout.CENTER);
  }

  public void reset(ArrayList<String> sourceCodesText) {
    // Clear existing components from the codeArea
    codeArea.removeAll();
    this.sourceCodes.clear();
    // Add new HighlightedCodePanes
    for (String line : sourceCodesText) {
      HighlightedCodePane pane = new HighlightedCodePane(line);
      codeArea.add(pane);
      codeArea.add(Box.createVerticalStrut(5));
      this.sourceCodes.add(pane);
    }

    // Refresh UI
    codeArea.revalidate();
    codeArea.repaint();

    if (!this.sourceCodes.isEmpty()) {
      currentHighlighted = 0;
      this.sourceCodes.get(currentHighlighted).glow();
    } else {
      currentHighlighted = -1;
    }
  }

  public boolean onStep(CollisionResolver.CollisionResolverResult actionResult) {

    if (currentHighlighted == -1)
      return false;

    statusLabel.setText("Status: " + actionResult.message());

    sourceCodes.get(currentHighlighted).deglow();

    currentHighlighted = actionResult.currentLine();
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
      int desiredTop = targetY - (viewportHeight / 3) + (nodeHeight / 2);

      // Ensure we don't scroll into negative values
      int scrollPosition = Math.max(1, desiredTop);

      codeScrollPane.getVerticalScrollBar().setValue(scrollPosition);
    });
  }
}
