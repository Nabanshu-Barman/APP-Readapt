package com.readapt.ui;

import com.readapt.ui.components.ProfileMenu;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Header extends StackPane {
    public Header(Runnable onHelp) {
        // Bar background (fixed max width, padding so nothing chips)
        HBox outerBar = new HBox();
        outerBar.setAlignment(Pos.CENTER_LEFT);
        outerBar.setSpacing(22);
        outerBar.setPadding(new Insets(0, 32, 0, 32));
        outerBar.setMaxWidth(1024);
        outerBar.setMinHeight(78);
        outerBar.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #fffbe6, #f7f8fdcc 80%, #fff 100%);" +
            "-fx-effect: dropshadow(gaussian, #e9dfa0, 10, 0.14, 0, 2);" +
            "-fx-background-radius: 22px;"
        );

        // Help button
        Button helpBtn = new Button("Help");
        helpBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        helpBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 7 22 7 22;");
        if (onHelp != null) {
            helpBtn.setOnAction(e -> onHelp.run());
        } else {
            helpBtn.setDisable(true);
        }

        // ProfileMenu (icon style)
        ProfileMenu profileMenu = new ProfileMenu();

        HBox leftBox = new HBox(12, helpBtn, profileMenu);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // Brand (logo + Readapt, to the right, not centered)
        HBox brandBox = new HBox(16);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 0, 0, 28));

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
        logo.setFitHeight(64);
        logo.setFitWidth(64);
        Rectangle clip = new Rectangle(64, 64);
        clip.setArcWidth(26);
        clip.setArcHeight(26);
        logo.setClip(clip);

        RotateTransition sway = new RotateTransition(Duration.seconds(3), logo);
        sway.setFromAngle(-5);
        sway.setToAngle(5);
        sway.setCycleCount(RotateTransition.INDEFINITE);
        sway.setAutoReverse(true);
        sway.play();

        Text brand = new Text("READAPT");
        brand.setFont(Font.font("Outfit", FontWeight.BOLD, 36));
        brand.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 0%, #2563eb, #0ea5e9, #6366f1);");

        brandBox.getChildren().addAll(logo, brand);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        outerBar.getChildren().addAll(leftBox, spacer, brandBox);

        this.getChildren().add(outerBar);
        StackPane.setAlignment(outerBar, Pos.TOP_CENTER);
        this.setMaxWidth(1024);
        this.setMaxHeight(82);
    }
}