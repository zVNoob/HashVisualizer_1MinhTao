package com.hashvis;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.hashvis.ui.ControlPanel;
import com.hashvis.ui.MainWindow;

public class Main {
  public static void main(String[] args) {
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }
      JFrame frame = new JFrame("Hash Algorithm Visualizer");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new MainWindow());
      frame.setSize(800, 600);
      frame.setVisible(true);
    });
  }

}
