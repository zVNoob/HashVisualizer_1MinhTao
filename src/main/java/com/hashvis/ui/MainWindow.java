package com.hashvis.ui;

import com.hashvis.hashalgo.HashAlgorithmVisualizer;
import com.hashvis.table.Table;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainWindow extends VBox {
  @FXML
  private SplitPane injectTable;
  @FXML
  private TextField hashKey;
  @FXML
  private ChoiceBox action;
  @FXML
  private VBox injectVisualizer;
  @FXML
  private Button animControlButton;

  private HashAlgorithmVisualizer visualizer;
  private Table table;

  public MainWindow(int size) {
    super();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    table = new Table(size, false);
    injectTable.getItems().add(table);
    injectTable.setDividerPositions(0.3);

    visualizer = new HashAlgorithmVisualizer(table);
    injectVisualizer.getChildren().add(visualizer);
    VBox.setVgrow(visualizer, Priority.ALWAYS);
  }

  public MainWindow() {
    this(100);
  }
}
