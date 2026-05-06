package com.hashvis.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;

public class ControlPanel extends JPanel {

  // UI Components
  private final JButton btnCreateTable = new JButton("Create Table");
  private final JLabel lblKey = new JLabel("Key: ");
  private final JTextField txtKey = new JTextField(10);
  private final JComboBox<String> cbAction = new JComboBox<>(new String[] { "Insert", "Search", "Delete" });
  private final JButton btnRun = new JButton("Run");

  private void makeHorizontalFill(JComponent comp) {
    // 1. Allow the component to grow to any width
    comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));

    // 2. Center the component horizontally relative to the BoxLayout
    comp.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  public ControlPanel() {

    // 1. Layout and Styling
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    lblKey.setForeground(new Color(60, 60, 60)); // Dark gray to match your theme
    // --- BORDER AND PADDING ---
    // LineBorder: Thin grey line (1 pixel)
    Border line = new LineBorder(new Color(180, 180, 180), 1);
    // EmptyBorder: 10px padding on Top, Left, Bottom, Right
    Border padding = new EmptyBorder(10, 10, 10, 10);
    // Combine them: Outer = Line, Inner = Padding
    this.setBorder(new CompoundBorder(line, padding));
    // Force scretch
    makeHorizontalFill(btnCreateTable);
    makeHorizontalFill(btnRun);

    // 3. Add components to panel
    add(btnCreateTable);
    add(Box.createVerticalStrut(10));
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    inputPanel.add(lblKey);
    inputPanel.add(Box.createHorizontalStrut(5));
    inputPanel.add(txtKey);
    add(inputPanel);
    add(Box.createVerticalStrut(10));
    add(cbAction);
    add(Box.createVerticalStrut(10));
    add(btnRun);

    setupEvents();
  }

  @Override
  public Dimension getMaximumSize() {
    // Allow any width, but lock height to the preferred height of the content
    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
  }

  private void setupEvents() {
  }
}
