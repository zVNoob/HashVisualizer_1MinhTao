package com.hashvis;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
  public static void main(String[] args) {
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    try {
      FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
      Scene scene = new Scene(mainLoader.load(), 900, 600);

      stage.setTitle("Visualization of operations on Hash Table");
      stage.setScene(scene);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
