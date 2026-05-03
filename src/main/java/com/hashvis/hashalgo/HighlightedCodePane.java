package com.hashvis.hashalgo;

import com.hashvis.codepane.CodePane;
import com.hashvis.codepane.parser.SymbolTable;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

public class HighlightedCodePane extends HBox {
  private final Timeline glowTimeline;
  private final Timeline deglowTimeline;

  @FXML
  private Polygon currentMark;

  private CodePane codePane;

  public HighlightedCodePane() {
    this(new SymbolTable(), "sum(len(s))", true);
  }

  public HighlightedCodePane(SymbolTable symbolTable, String text, boolean readOnly) {
    super();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("HighlightedCodePane.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    codePane = new CodePane(symbolTable, text, readOnly);
    HBox.setHgrow(codePane, Priority.ALWAYS);
    this.getChildren().add(codePane);

    // Animations target the radius property
    glowTimeline = new Timeline(new KeyFrame(Duration.millis(300),
        new KeyValue(currentMark.fillProperty(), new Color(1.0, 0.5, 0.0, 1.0))));

    deglowTimeline = new Timeline(new KeyFrame(Duration.millis(300),
        new KeyValue(currentMark.fillProperty(), new Color(1.0, 1.0, 1.0, 1.0))));
  }

  public Object eval() {
    return codePane.eval();
  }

  public void glow() {
    deglowTimeline.stop();
    glowTimeline.play();
  }

  public void deglow() {
    glowTimeline.stop();
    deglowTimeline.play();
  }
}
