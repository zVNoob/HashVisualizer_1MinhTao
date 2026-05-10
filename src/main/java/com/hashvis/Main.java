package com.hashvis;

import javax.swing.*;
import com.hashvis.ui.StartWindow;

public class Main {
  JFrame frame;

  public static void main(String[] args) {
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");

    Main main = new Main();
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }

      // Start the application at the Main Menu
      main.frame = new StartWindow();
      main.frame.setVisible(true);
    });
  }
}
