package com.hashvis.table;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class Table extends ScrollPane {
  @FXML
  private VBox container;

  private int currentIndex = -1;

  public Table(int size, boolean isSeperateChaining) {
    super();
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Table.fxml"));
    mainLoader.setRoot(this);
    mainLoader.setController(this);
    try {
      mainLoader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (int i = 0; i < size; i++)
      container.getChildren().add(new Row(i));
    if (isSeperateChaining)
      container.setFillWidth(true);
  }

  public int size() {
    return container.getChildren().size();
  }

  public void resetHighlight() {
    if (currentIndex != -1)
      ((Row) container.getChildren().get(currentIndex)).unchoose();
    currentIndex = -1;
  }

  public void highlight(int index) {
    resetHighlight();
    if (index < 0 || index >= container.getChildren().size())
      return;
    ((Row) container.getChildren().get(index)).choose();
    currentIndex = index;
  }
}
