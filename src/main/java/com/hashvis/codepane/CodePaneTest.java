package com.hashvis.codepane;

import javax.swing.*;
import java.awt.*;
import com.hashvis.codepane.parser.SymbolTable;
import com.hashvis.hashfunc.HashFunction;
import com.hashvis.hashfunc.HashSymbolTable;

public class CodePaneTest {

  public static void main(String[] args) {
    // Ensure GUI is created on the Event Dispatch Thread (EDT)
    SwingUtilities.invokeLater(() -> {
      createAndShowGUI();
    });
  }

  private static void createAndShowGUI() {
    // 1. Setup the Frame
    JFrame frame = new JFrame("CodePane Component Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));
    frame.getContentPane().setBackground(new Color(230, 230, 230));

    // 2. Create a Symbol Table
    // (Assuming SymbolTable has a default constructor or simple setup)
    SymbolTable symbolTable = new SymbolTable(HashSymbolTable.getGlobalSymbolTable());
    // If your SymbolTable allows adding variables, add some for testing
    // autocomplete:
    // symbolTable.define("myVar", 10);
    // symbolTable.define("pi", 3.14159);

    // 3. Create an editable CodePane
    // We'll use the constructor that takes just the symbol table
    CodePane editablePane = new CodePane(symbolTable);
    // editablePane.setLayout(new BorderLayout());
    // editablePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

    // 4. Create a read-only CodePane with initial code
    CodePane readOnlyPane = new CodePane(symbolTable, "10 + 5 * 2", true);

    // --- UI Layout ---
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    mainPanel.setBackground(new Color(230, 230, 230));

    // Section 1: Read Only Demo
    mainPanel.add(new JLabel("Read-Only Pane (Initial Code):"));
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    mainPanel.add(readOnlyPane);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

    // Section 2: Editable Demo
    mainPanel.add(new JLabel("Editable Pane (Type here):"));
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    mainPanel.add(editablePane);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    // Section 3: Evaluation Logic
    JLabel resultLabel = new JLabel("Result: ");
    resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

    JButton evalButton = new JButton("Evaluate Editable Pane");
    evalButton.addActionListener(e -> {
      try {
        Object result = editablePane.eval();
        resultLabel.setText("Result: " + (result != null ? result.toString() : "null"));
      } catch (Exception ex) {
        resultLabel.setText("Error: " + ex.getMessage());
      }
    });

    mainPanel.add(evalButton);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    mainPanel.add(resultLabel);

    frame.add(mainPanel, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null); // Center on screen
    frame.setVisible(true);
  }
}
