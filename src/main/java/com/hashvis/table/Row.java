package com.hashvis.table;

import java.util.ArrayList;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Row extends HBox {
  @FXML
  private Label index;

  @FXML
  private HBox content;
  @FXML
  private ScrollPane scrollPane;

  private ArrayList<Item> items = new ArrayList<Item>();

  private int currentIndex = -1;
  private int prevIndex = -1;
  private boolean isChosen = false;

  private final DropShadow glowEffect = new DropShadow();
  private final Timeline glowTimeline;
  private final Timeline deglowTimeline;

  public Row(int index) {
    super();
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Row.fxml"));
    mainLoader.setRoot(this);
    mainLoader.setController(this);
    try {
      mainLoader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Setup the effect
    glowEffect.setRadius(3);
    glowEffect.setSpread(1.0);
    glowEffect.setColor(new Color(0.0, 0.0, 0.0, 1.0));
    this.setEffect(glowEffect);

    // Animations target the radius property
    glowTimeline = new Timeline(new KeyFrame(Duration.millis(300),
        new KeyValue(glowEffect.colorProperty(), new Color(0.0, 0.5, 1.0, 1.0))));

    deglowTimeline = new Timeline(new KeyFrame(Duration.millis(300),
        new KeyValue(glowEffect.colorProperty(), new Color(0.0, 0.0, 0.0, 1.0))));

    this.index.setText(String.valueOf(index));
  }

  private void updateIndex() {
    if (currentIndex == prevIndex)
      return;
    if (prevIndex != -1)
      items.get(prevIndex).deglow();
    if (currentIndex != -1)
      if (isChosen)
        items.get(currentIndex).glow();
      else
        items.get(currentIndex).deglow();
    prevIndex = currentIndex;
  }

  public Item nextItem() {
    currentIndex++;
    if (currentIndex >= items.size()) {
      currentIndex = -1;
      updateIndex();
      return null;
    }
    updateIndex();
    scrollToItem(items.get(currentIndex));
    return items.get(currentIndex);
  }

  public void choose() {
    isChosen = true;
    deglowTimeline.stop();
    glowTimeline.play();
    updateIndex();
  }

  public void unchoose() {
    isChosen = false;
    currentIndex = -1;
    glowTimeline.stop();
    deglowTimeline.play();
    updateIndex();
  }

  public Item addItem(String text) {
    Item item = new Item(text);
    items.add(item);
    content.getChildren().add(item);
    return item;
  }

  public void removeItem(Item item) {
    items.remove(item);
    content.getChildren().remove(item);
  }

  private void scrollToItem(Item node) {
    // We use Platform.runLater to ensure the layout pass is complete
    // so that layoutY is calculated correctly.
    Platform.runLater(() -> {
      double targetY = node.getLayoutX();
      double contentWidth = content.getWidth();
      double viewportWidth = scrollPane.getWidth();

      if (contentWidth > viewportWidth) {
        double desiredTop = targetY - (viewportWidth / 2) + (node.getWidth() / 2);
        double vValue = desiredTop / (contentWidth - viewportWidth);
        // Clamp value between 0 and 1 to prevent crashes
        vValue = Math.max(0, Math.min(1, vValue));
        scrollPane.setHvalue(vValue);
      }
    });
  }
}
