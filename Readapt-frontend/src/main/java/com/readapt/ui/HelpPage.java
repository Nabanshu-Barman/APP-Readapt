package com.readapt.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class HelpPage extends StackPane {
    public HelpPage(Runnable onBack) {
        // Particle background
        ParticlesPane particles = new ParticlesPane();
        particles.setMouseTransparent(true);

        // Overlay content
        VBox contentBox = new VBox(18);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(44, 0, 32, 0));
        contentBox.setMaxWidth(740);
        contentBox.setStyle("-fx-background-color: rgba(255,255,255,0.97); -fx-background-radius: 32; -fx-effect: dropshadow(gaussian, #e9dfa0, 22, 0.18, 0, 2);");

        Text title = new Text("How to Use Readapt");
        title.setFont(Font.font("Outfit", FontWeight.BOLD, 38));
        title.setFill(Color.web("#2563eb"));

        Text subtitle = new Text("A quick guide to make the most of your adaptive reading experience!");
        subtitle.setFont(Font.font("Outfit", FontWeight.MEDIUM, 18));
        subtitle.setFill(Color.web("#374151"));
        subtitle.setWrappingWidth(680);

        VBox steps = new VBox(22);
        steps.setPadding(new Insets(16, 32, 24, 32));
        steps.setMaxWidth(700);

        steps.getChildren().addAll(
            step("1. Launch Readapt", "Open the desktop application. You'll start at the Modes Page, where you can select your reading support mode (Dyslexia for now)."),
            step("2. Dashboard", "Review the features and science behind Readapt on the Dyslexia Dashboard. Click \"Start Assessment\" to begin."),
            step("3. Complete Quiz", "Answer 10 quick questions testing your reading, memory, and phoneme skills. Time taken is tracked for a personalized profile."),
            step("4. View Results", "See your recommended preset (Severe, Mild, or None) and a detailed breakdown of your reading profile."),
            step("5. Paste or OCR Text", "Paste any text, try a sample, or upload an image for OCR extraction. The text will be adapted for you!"),
            step("6. Adapt & Customize", "See your text rendered with your preset: spacing, letter cues, and TTS. Adjust the adaptation manually, or let the monitoring agent suggest increases if you're inactive."),
            step("7. Sync to Browser", "Export your preset to the browser extension to adapt any website. Use the extension's slider and modes for instant comfort online.")
        );

        // Back button with extra right shift
        javafx.scene.control.Button backBtn = new javafx.scene.control.Button("← Back");
        backBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        backBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 7 22 7 22;");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        HBox backBox = new HBox(backBtn);
        backBox.setAlignment(Pos.CENTER_LEFT);
        backBox.setPadding(new Insets(0, 0, 0, 20)); // <-- Move back button right by 20px

        contentBox.getChildren().addAll(backBox, title, subtitle, steps);

        // CENTERING HBox for the content
        HBox centerHBox = new HBox();
        centerHBox.setAlignment(Pos.TOP_CENTER);
        centerHBox.setMinWidth(0);
        centerHBox.getChildren().add(contentBox);

        // Scrollable content for smaller screens
        ScrollPane scroll = new ScrollPane(centerHBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);

        // Header (with Help disabled)
        Header header = new Header(null);

        this.getChildren().addAll(particles, scroll, header);

        // Proper center and shift down the help tab
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(scroll, Pos.TOP_CENTER);
        StackPane.setMargin(scroll, new Insets(140, 0, 0, 0)); 
    }

    private Node step(String heading, String desc) {
        VBox box = new VBox(2);
        Text h = new Text(heading);
        h.setFont(Font.font("Outfit", FontWeight.SEMI_BOLD, 20));
        h.setFill(Color.web("#6366f1"));
        Text d = new Text(desc);
        d.setFont(Font.font("Outfit", FontWeight.NORMAL, 16));
        d.setFill(Color.web("#374151"));
        d.setWrappingWidth(670);
        box.getChildren().addAll(h, d);
        return box;
    }
}