package com.readapt.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class ModeCard extends VBox {

    private ImageView imageView;

    public static class Mode {
        public String title;
        public String desc;
        public String imgPath;
        public List<String> tags;
        public String dashboardRoute;

        public Mode(String title, String desc, String imgPath, List<String> tags, String dashboardRoute) {
            this.title = title;
            this.desc = desc;
            this.imgPath = imgPath;
            this.tags = tags;
            this.dashboardRoute = dashboardRoute;
        }
    }

    public ModeCard(Mode mode) {
        this.setPadding(new Insets(38));
        this.setSpacing(26);
        this.setAlignment(Pos.TOP_CENTER);
        this.getStyleClass().add("mode-card");

        // Image
        imageView = new ImageView(new Image(getClass().getResourceAsStream(mode.imgPath)));
        imageView.setFitWidth(340);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        VBox imgWrapper = new VBox(imageView);
        imgWrapper.setAlignment(Pos.CENTER);

        // Title (dark brown)
        Label title = new Label(mode.title);
        title.setFont(Font.font("Outfit", FontWeight.BOLD, 38));
        title.setTextFill(Color.web("#6c4210")); // dark brown
        title.setWrapText(true);

        // Subtitle (dark brown, only requested text)
        Label desc = new Label("Enhanced spacing and highlights");
        desc.setFont(Font.font("Outfit", FontWeight.NORMAL, 24));
        desc.setWrapText(true);
        desc.setTextFill(Color.web("#6c4210")); // dark brown

        // Tags
        HBox tagsRow = new HBox(20);
        for (String tag : mode.tags) {
            Label tagLabel = new Label(tag);
            tagLabel.setStyle("-fx-background-color: #ffe59c; -fx-text-fill: #b08d23; -fx-font-size: 18px; -fx-padding: 10 26 10 26; -fx-background-radius: 22;");
            tagsRow.getChildren().add(tagLabel);
        }
        tagsRow.setAlignment(Pos.CENTER);

        this.getChildren().addAll(imgWrapper, title, desc, tagsRow);
    }

    public void setImageSize(double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }
}