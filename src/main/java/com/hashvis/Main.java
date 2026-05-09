package com.hashvis;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.hashvis.ui.StartWindow;

public class Main {
  public static void main(String[] args) {
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }

      // Start the application at the Main Menu
      java.awt.Frame frame = new StartWindow();
      frame.setVisible(true);
    });
  }
}
