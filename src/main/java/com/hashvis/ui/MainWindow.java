package com.hashvis.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.hashvis.collision.CollisionResolver;
import com.hashvis.hashalgo.HashAlgorithmVisualizer;
import com.hashvis.table.Table;

public class MainWindow extends JPanel {

  private Table hashTable = null;
  private JPanel hashTablePanel = new JPanel(new BorderLayout());
  private final HashAlgorithmVisualizer visualizer;
  private ControlPanel controlPanel;
  private CreateControlPanel createControlPanel;
  private JPanel controlPanelArea;
  private JSplitPane mainSplitPane;
  private Runnable resetCallback;

  private Timer animTimer;

  private StartWindow startWindow;

  public MainWindow(CollisionResolver resolver, boolean isKeyString, StartWindow startWindow) {
    super();

    setLayout(new BorderLayout(10, 10));
    this.startWindow = startWindow;

    // 2. Initialize the Visualizer (inject the table)
    this.visualizer = new HashAlgorithmVisualizer();

    this.controlPanel = new ControlPanel(new ControlPanel.ActionListener() {
      @Override
      public void setActionCode(ArrayList<String> code, String key) {
        visualizer.reset(code);
        visualizer.getBaseSymbolTable().set("n", hashTable.tableSize());
        if (isKeyString) {
          ArrayList<BigInteger> keyArr = new ArrayList<BigInteger>();
          for (int i = 0; i < key.length(); i++) {
            keyArr.add(BigInteger.valueOf(key.charAt(i)));
          }
          visualizer.getBaseSymbolTable().set("key", keyArr);
        } else {
          if (key.length() == 0)
            key = "0";
          visualizer.getBaseSymbolTable().set("key", new BigInteger(key));
        }
      }

      @Override
      public void startAnimate(Runnable callback) {
        hashTable.reset();
        animTimer = new Timer(500, e -> {
          if (!visualizer.next()) {
            callback.run();
            animTimer.stop();
          }
        });
        animTimer.start();
      }

      @Override
      public void startReset(Runnable callback) {
        controlPanelArea.removeAll();
        controlPanelArea.add(createControlPanel);
        controlPanelArea.revalidate();
        controlPanelArea.repaint();
        resetCallback = callback;
      }
    }, resolver, isKeyString);

    this.createControlPanel = new CreateControlPanel(new CreateControlPanel.ActionListener() {

      @Override
      public void revertReset() {
        controlPanelArea.removeAll();
        controlPanelArea.add(controlPanel);
        controlPanelArea.revalidate();
        controlPanelArea.repaint();
        if (resetCallback != null)
          resetCallback.run();
      }

      @Override
      public void resetTable(int size, boolean isSeparateChaining) {
        hashTable = new Table(size, isSeparateChaining);
        hashTablePanel.removeAll();
        hashTablePanel.add(hashTable, BorderLayout.CENTER);
        hashTablePanel.revalidate();
        hashTablePanel.repaint();
        visualizer.setSymbolTable(resolver.getAlgorithmSymbolTable(hashTable, visualizer.getBaseSymbolTable()));
        revertReset();
      }
    }, resolver);

    controlPanelArea = new JPanel();
    controlPanelArea.setLayout(new BoxLayout(controlPanelArea, BoxLayout.Y_AXIS));
    controlPanelArea.setBorder(new EmptyBorder(10, 10, 10, 10));

    controlPanelArea.add(createControlPanel);
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    JButton backButton = new JButton("Back");
    backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    backButton.addActionListener(e -> {
      startWindow.back();
    });
    leftPanel.add(backButton);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(controlPanelArea);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(visualizer);

    mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, hashTablePanel);
    mainSplitPane.setDividerLocation(600);
    mainSplitPane.setBorder(null);

    add(mainSplitPane, BorderLayout.CENTER);
  }
}
