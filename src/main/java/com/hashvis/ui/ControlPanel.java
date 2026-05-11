package com.hashvis.ui;

import javax.swing.*;

import com.hashvis.collision.CollisionResolver;
import com.hashvis.collision.CollisionResolver.HashAction;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ControlPanel extends JPanel {

  // UI Components
  private JButton btnCreateTable = new JButton("Create Table");
  private JLabel lblKey = new JLabel("Key: ");
  private JTextField txtKey = new JTextField(10);
  private JComboBox<String> cbAction = new JComboBox<>(new String[] { "Insert", "Search", "Delete" });
  private JButton btnRun = new JButton("Run");
  private ActionListener listener;
  private CollisionResolver resolver;

  private void makeHorizontalFill(JComponent comp) {
    // 1. Allow the component to grow to any width
    comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));

    // 2. Center the component horizontally relative to the BoxLayout
    comp.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  public interface ActionListener {
    void setAction(CollisionResolver.HashAction action, String key);

    void startAnimate(Runnable callback);

    void startReset(Runnable callback);
  }

  public ControlPanel(ActionListener listener, CollisionResolver resolver, boolean isKeyString) {
    super();
    this.listener = listener;
    this.resolver = resolver;
    // 1. Layout and Styling
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    lblKey.setForeground(new Color(60, 60, 60)); // Dark gray to match your theme

    // Force scretch
    makeHorizontalFill(btnCreateTable);
    makeHorizontalFill(btnRun);
    this.txtKey = new JTextField(10) {
      @Override
      public void processKeyEvent(KeyEvent ev) {
        if (isKeyString)
          super.processKeyEvent(ev);
        else if (Character.isDigit(ev.getKeyChar())) {
          super.processKeyEvent(ev);
        } else if (ev.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
          super.processKeyEvent(ev);
        }
        ev.consume();
        return;
      }
    };

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
    btnCreateTable.addActionListener(e -> {
      listener.startReset(() -> {
        onCBAction();
      });
    });
    btnRun.addActionListener(e -> {
      btnCreateTable.setEnabled(false);
      btnRun.setEnabled(false);
      cbAction.setEnabled(false);
      txtKey.setEnabled(false);
      onCBAction();
      listener.startAnimate(() -> {
        btnCreateTable.setEnabled(true);
        btnRun.setEnabled(true);
        cbAction.setEnabled(true);
        txtKey.setEnabled(true);
        cbAction.setSelectedIndex(cbAction.getSelectedIndex());
      });
    });
    cbAction.addActionListener(e -> {
      onCBAction();
    });
    txtKey.addActionListener(e -> {
      btnRun.setEnabled(txtKey.getText().length() > 0);
      onCBAction();
    });
  }

  private void onCBAction() {
    CollisionResolver.HashAction result;
    if (cbAction.getSelectedIndex() == 0) {
      result = HashAction.INSERT;
    } else if (cbAction.getSelectedIndex() == 1) {
      result = HashAction.SEARCH;
    } else {
      result = HashAction.DELETE;
    }
    listener.setAction(result, txtKey.getText());
  }
}
