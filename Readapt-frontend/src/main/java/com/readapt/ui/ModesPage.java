package com.readapt.ui;

import com.readapt.ui.components.ModeCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class ModesPage extends StackPane {
    public ModesPage(Runnable onContinue, Runnable onHelp) {
        // Gradient background
        Pane gradientBg = new Pane();
        gradientBg.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #fffbe6, #fffde7 80%, #ffffff 100%);");
        gradientBg.prefWidthProperty().bind(widthProperty());
        gradientBg.prefHeightProperty().bind(heightProperty());

        // Particles
        ParticlesPane particles = new ParticlesPane();
        particles.setOpacity(0.32);
        particles.prefWidthProperty().bind(widthProperty());
        particles.prefHeightProperty().bind(heightProperty());

        // Header (sticky at top)
        Header header = new Header(onHelp);

        // Main content (VBox in ScrollPane)
        VBox mainContent = new VBox(48);
        mainContent.setPadding(new Insets(160, 32, 36, 32)); // move down for header
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: transparent;");

        // Title
        Text title = new Text("Dyslexia Adaptation Mode");
        title.setFont(Font.font("Outfit", FontWeight.BOLD, 46));
        title.setFill(Color.web("#b08d23"));

        // Subtitle
        Text desc = new Text("Optimized reading experience: Spacing, cues, and TTS for dyslexia.");
        desc.setFont(Font.font("Outfit", FontWeight.SEMI_BOLD, 26));
        desc.setFill(Color.web("#7a5c19"));

        // Mode card with continue button
        ModeCard.Mode dyslexiaMode = new ModeCard.Mode(
                "Dyslexia",
                "Enhanced spacing and highlights",
                "/images/dyslexia-support-book-icon.jpg",
                java.util.List.of("Spacing", "Letters", "TTS"),
                "dashboard-dyslexia"
        );
        ModeCard dyslexiaCard = new ModeCard(dyslexiaMode);
        dyslexiaCard.setPrefWidth(580);
        dyslexiaCard.setPrefHeight(540);

        // Continue button
        Button continueBtn = new Button("Continue →");
        continueBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 24));
        continueBtn.setPrefWidth(220);
        continueBtn.setStyle("-fx-background-radius: 18; -fx-background-color: #b9fbc0; -fx-text-fill: #219c50; -fx-padding: 13 0 13 0; -fx-font-size: 21px; -fx-effect: dropshadow(gaussian, #b9fbc0, 4,0.10,0,1);");
        continueBtn.setOnAction(e -> {
            if (onContinue != null) onContinue.run();
        });
        dyslexiaCard.getChildren().add(continueBtn);

        HBox cardBox = new HBox(dyslexiaCard);
        cardBox.setAlignment(Pos.CENTER);

        // Core capabilities section
        Text capabilitiesTitle = new Text("Core Assistive Capabilities");
        capabilitiesTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 28));
        capabilitiesTitle.setFill(Color.web("#b08d23"));
        Text capabilitiesDesc = new Text(
            "• Real-time adaptation\n" +
            "• Enhanced contrast and spacing\n" +
            "• Optional text-to-speech\n" +
            "• Smart highlights"
        );
        capabilitiesDesc.setFont(Font.font("Outfit", FontWeight.NORMAL, 18));
        capabilitiesDesc.setFill(Color.web("#7a5c19"));

        VBox capabilitiesBox = new VBox(10, capabilitiesTitle, capabilitiesDesc);
        capabilitiesBox.setAlignment(Pos.CENTER);

        // Picking a Mode section
        Text guidanceTitle = new Text("Picking a Mode");
        guidanceTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 24));
        guidanceTitle.setFill(Color.web("#b08d23"));
        Text guidanceDesc = new Text("Dyslexia mode: Spacing and letter cues ease reversals; TTS supports decoding.");
        guidanceDesc.setFont(Font.font("Outfit", FontWeight.NORMAL, 16));
        guidanceDesc.setFill(Color.web("#7a5c19"));

        VBox guidanceBox = new VBox(8, guidanceTitle, guidanceDesc);
        guidanceBox.setAlignment(Pos.CENTER);

        mainContent.getChildren().addAll(title, desc, cardBox, capabilitiesBox, guidanceBox);

        // Scrollable
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Layering
        this.getChildren().addAll(gradientBg, particles, scrollPane, header);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(scrollPane, Pos.CENTER);
    }
}