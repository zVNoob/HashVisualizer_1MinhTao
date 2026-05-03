package com.hashvis.table;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Item extends Label {
  private final DropShadow glowEffect = new DropShadow();
  private final Timeline glowTimeline;
  private final Timeline deglowTimeline;

  // Constructor for FXML text property
  public Item(String text) {
    super();
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("Item.fxml"));
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
        new KeyValue(glowEffect.colorProperty(), new Color(1.0, 0.0, 0.0, 1.0))));

    deglowTimeline = new Timeline(new KeyFrame(Duration.millis(300),
        new KeyValue(glowEffect.colorProperty(), new Color(0.0, 0.0, 0.0, 1.0))));
    setText(text);
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
