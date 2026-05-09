package com.hashvis.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hashvis.collision.CollisionResolver;
import com.hashvis.hashfunc.HashFunction;

public class CreateControlPanel extends JPanel {
  // UI Components
  private JButton btnCreateTable = new JButton("Cancel Create");
  private JLabel lblKey = new JLabel("Size: ");
  private JTextField txtKey = null;
  private ArrayList<HashFunction> hashFuncs = null;
  private JButton btnRun = new JButton("Create Table");
  private ActionListener listener;
  private CollisionResolver resolver;

  public interface ActionListener {
    void revertReset();

    void resetTable(int size, boolean isSeparateChaining);

  }

  private void makeHorizontalFill(JComponent comp) {
    // 1. Allow the component to grow to any width
    comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));

    // 2. Center the component horizontally relative to the BoxLayout
    comp.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  public CreateControlPanel(ActionListener listener, CollisionResolver resolver) {
    super();
    this.listener = listener;
    this.resolver = resolver;
    // 1. Layout and Styling
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    lblKey.setForeground(new Color(60, 60, 60)); // Dark gray to match your theme
    this.txtKey = new JTextField(10) {
      @Override
      public void processKeyEvent(KeyEvent ev) {
        if (Character.isDigit(ev.getKeyChar())) {
          if (txtKey.getText().length() < 4)
            super.processKeyEvent(ev);
        } else if (ev.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
          super.processKeyEvent(ev);
        }
        ev.consume();
        return;
      }
    };
    // Force scretch
    makeHorizontalFill(btnCreateTable);
    makeHorizontalFill(btnRun);

    btnCreateTable.setEnabled(false);
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
    hashFuncs = resolver.getHashFunctionFields();
    for (int i = 0; i < hashFuncs.size(); i++) {
      JPanel hashFuncPanel = new JPanel();
      hashFuncPanel.setLayout(new BoxLayout(hashFuncPanel, BoxLayout.X_AXIS));
      hashFuncPanel.add(new JLabel("Hash " + (i + 1) + ": "));
      hashFuncPanel.add(Box.createHorizontalStrut(5));
      hashFuncPanel.add(hashFuncs.get(i));
      add(hashFuncPanel);
      add(Box.createVerticalStrut(10));
    }
    add(btnRun);

    setupEvents();
  }

  private void setupEvents() {
    btnCreateTable.addActionListener(e -> {
      listener.revertReset();
    });

    btnRun.addActionListener(e -> {
      for (HashFunction hashFunc : hashFuncs) {
        if (!hashFunc.isValidHashFunc()) {
          return;
        }
      }
      listener.resetTable(Integer.parseInt(txtKey.getText()), resolver.isSeperateChaining());
      btnCreateTable.setEnabled(true);
    });
  }

  @Override
  public Dimension getMaximumSize() {
    // Allow any width, but lock height to the preferred height of the content
    return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
  }

}
