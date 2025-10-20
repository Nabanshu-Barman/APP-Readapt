package com.readapt.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FeatureCard extends VBox {

    public static class Feature {
        public String title;
        public String body;
        public Feature(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    public FeatureCard(Feature feature) {
        super(8); // spacing between elements, a little larger for clarity
        this.setPadding(new Insets(18, 16, 18, 16));
        this.getStyleClass().add("dx-feature-card");

        Label t = new Label(feature.title);
        t.getStyleClass().add("dx-feature-card-title");

        Label body = new Label(feature.body);
        body.getStyleClass().add("dx-feature-card-body");

        this.getChildren().addAll(t, body);
    }
}