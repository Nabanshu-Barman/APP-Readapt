package com.readapt.ui.components;

import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Separator;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ProfileMenu extends StackPane {
    public ProfileMenu() {
        setAlignment(Pos.CENTER);

        // Profile circle with icon
        Circle circle = new Circle(22, Color.web("#2563eb"));
        circle.setStroke(Color.web("#fff"));
        circle.setStrokeWidth(2);

        // Use emoji for user icon, or you can use SVG
        Label userIcon = new Label("\uD83D\uDC64"); // Unicode for user icon
        userIcon.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        userIcon.setTextFill(Color.WHITE);

        StackPane roundIcon = new StackPane(circle, userIcon);
        roundIcon.setPrefSize(44, 44);
        roundIcon.setMaxSize(44, 44);
        roundIcon.setStyle("-fx-effect: dropshadow(gaussian, #2563eb55, 4,0.2,0,1); -fx-cursor: hand;");

        // Animate subtle sway
        RotateTransition sway = new RotateTransition(Duration.seconds(3), roundIcon);
        sway.setFromAngle(-5);
        sway.setToAngle(5);
        sway.setCycleCount(RotateTransition.INDEFINITE);
        sway.setAutoReverse(true);
        sway.play();

        ContextMenu dropdown = new ContextMenu();

        VBox content = new VBox(8);
        content.setPadding(new Insets(18, 18, 18, 18));
        content.setAlignment(Pos.CENTER_LEFT);

        // Username + joined
        Text username = new Text("demoUser");
        username.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        username.setFill(Color.web("#2563eb"));
        Text joined = new Text("Joined: Sep 15, 2025");
        joined.setFont(Font.font("Outfit", 13));
        joined.setFill(Color.web("#989898"));

        VBox userInfo = new VBox(2, username, joined);

        // Latest Assessment
        VBox disorderBox = new VBox(1);
        Text assessLabel = new Text("Latest Assessment");
        assessLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 11));
        assessLabel.setFill(Color.web("#b08d23"));
        Text assessType = new Text("Dyslexia");
        assessType.setFont(Font.font("Outfit", FontWeight.BOLD, 13));
        assessType.setFill(Color.web("#7a5c19"));
        Text assessSeverity = new Text("Moderate");
        assessSeverity.setFont(Font.font("Outfit", FontWeight.NORMAL, 12));
        assessSeverity.setFill(Color.web("#b08d23"));
        Text assessDate = new Text("2025-10-01");
        assessDate.setFont(Font.font("Outfit", 11));
        assessDate.setFill(Color.web("#989898"));
        disorderBox.getChildren().addAll(assessLabel, assessType, assessSeverity, assessDate);

        // Custom preset
        VBox presetBox = new VBox(1);
        Text presetLabel = new Text("Custom Text Preset");
        presetLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 11));
        presetLabel.setFill(Color.web("#b08d23"));
        Text presetInfo = new Text("Preset Available");
        presetInfo.setFont(Font.font("Outfit", 12));
        presetInfo.setFill(Color.web("#2563eb"));
        Text presetDate = new Text("Updated 2025-10-02");
        presetDate.setFont(Font.font("Outfit", 11));
        presetDate.setFill(Color.web("#989898"));
        presetBox.getChildren().addAll(presetLabel, presetInfo, presetDate);

        content.getChildren().addAll(userInfo, new Separator(), disorderBox, new Separator(), presetBox);

        CustomMenuItem menuContent = new CustomMenuItem(content, false);
        dropdown.getItems().add(menuContent);

        roundIcon.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (dropdown.isShowing()) {
                    dropdown.hide();
                } else {
                    dropdown.show(roundIcon, e.getScreenX(), e.getScreenY() + 8);
                }
            }
        });

        setCursor(Cursor.HAND);
        getChildren().add(roundIcon);
    }
}