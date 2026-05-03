package com.hashvis.hashalgo;

import java.awt.TextField;
import java.util.ArrayList;
import java.util.HashMap;

import com.hashvis.table.Table;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class HashAlgorithmVisualizer extends SplitPane {
  public static class VariableEntry {
    private final String name;
    private final String value;

    public VariableEntry(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
  }

  @FXML
  private VBox codeArea;
  @FXML
  private ScrollPane codeScrollPane;
  @FXML
  private TableView<VariableEntry> varTable;
  @FXML
  private TableColumn<VariableEntry, String> varName;
  @FXML
  private TableColumn<VariableEntry, String> varValue;

  private ArrayList<HighlightedCodePane> sourceCodes = new ArrayList<HighlightedCodePane>();
  private HashMap<String, Object> variables = new HashMap<String, Object>();
  private HashAlgorithmSymbolTable symbolTable = null;

  public HashAlgorithmVisualizer(Table table) {
    super();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("HashAlgorithmVisualizer.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    symbolTable = new HashAlgorithmSymbolTable(variables, table);
    varName.setCellValueFactory(new PropertyValueFactory<>("name"));
    varValue.setCellValueFactory(new PropertyValueFactory<>("value"));
  }

  private void updateVariables() {
    ObservableList<VariableEntry> data = FXCollections.observableArrayList();
    variables.forEach((k, v) -> data.add(new VariableEntry(k, v.toString())));
    varTable.setItems(data);
  }

  private int currentHighlighted;

  public void reset(ArrayList<String> sourceCodes) {
    for (HighlightedCodePane pane : this.sourceCodes) {
      codeArea.getChildren().remove(pane);
    }
    this.sourceCodes.clear();
    for (String line : sourceCodes) {
      HighlightedCodePane pane = new HighlightedCodePane(symbolTable, line, true);
      codeArea.getChildren().add(pane);
      this.sourceCodes.add(pane);
    }
    if (sourceCodes.size() > 0) {
      currentHighlighted = 0;
      symbolTable.resetInstructionCount();
      this.sourceCodes.get(currentHighlighted).glow();
    }
    variables.clear();
    variables.put("i", 10);
    updateVariables();
  }

  public boolean next() {
    if (currentHighlighted == -1)
      return false;
    // TODO: Enable this when actual code is done
    // sourceCodes.get(currentHighlighted).eval();
    updateVariables();
    sourceCodes.get(currentHighlighted).deglow();
    symbolTable.incrementInstructionCount();
    currentHighlighted = symbolTable.getInstructionCount();
    if (currentHighlighted >= sourceCodes.size())
      currentHighlighted = -1;
    if (currentHighlighted == -1)
      return false;
    sourceCodes.get(currentHighlighted).glow();
    scrollToLine(sourceCodes.get(currentHighlighted));
    return true;
  }

  private void scrollToLine(HighlightedCodePane node) {
    // We use Platform.runLater to ensure the layout pass is complete
    // so that layoutY is calculated correctly.
    Platform.runLater(() -> {
      double targetY = node.getLayoutY();
      double contentHeight = codeArea.getHeight();
      double viewportHeight = codeScrollPane.getHeight();

      if (contentHeight > viewportHeight) {
        double desiredTop = targetY - (viewportHeight / 2) + (node.getHeight() / 2);
        double vValue = desiredTop / (contentHeight - viewportHeight);
        // Clamp value between 0 and 1 to prevent crashes
        vValue = Math.max(0, Math.min(1, vValue));
        codeScrollPane.setVvalue(vValue);
      }
    });
  }
}
