package com.readapt.ui;

import com.readapt.ui.components.FeatureCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Dashboard for Dyslexia mode.
 * Accepts three callbacks:
 *  - onBackToModes: called when user clicks "Back"
 *  - onStartAssessment: called when user clicks "Start Assessment"
 *  - onHelp: called when user clicks "Help" in header
 */
public class DyslexiaDashboard extends StackPane {
    public DyslexiaDashboard(Runnable onBackToModes, Runnable onStartAssessment, Runnable onHelp) {
        // Gradient background
        Pane gradientBg = new Pane();
        gradientBg.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #fffbe6, #fffde7 80%, #ffffff 100%);");
        gradientBg.prefWidthProperty().bind(widthProperty());
        gradientBg.prefHeightProperty().bind(heightProperty());

        // Particles
        ParticlesPane particles = new ParticlesPane();
        particles.setOpacity(0.35);
        particles.prefWidthProperty().bind(widthProperty());
        particles.prefHeightProperty().bind(heightProperty());

        // Header
        Header header = new Header(onHelp);

        // Back button (top left)
        Button backBtn = new Button("← Back");
        backBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 18));
        backBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 7 22 7 22;");
        backBtn.setOnAction(e -> { if (onBackToModes != null) onBackToModes.run(); });
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(16,0,0,22));

        // Main section grid
        HBox sectionGrid = new HBox(30);
        sectionGrid.setAlignment(Pos.TOP_CENTER);
        sectionGrid.setPadding(new Insets(120, 0, 38, 0));

        // === Left Panel ===
        VBox leftPanel = new VBox(24);
        leftPanel.setMaxWidth(640);
        leftPanel.setPadding(new Insets(12, 18, 12, 18));
        leftPanel.setStyle("-fx-background-color: #fffefc; -fx-background-radius: 36; -fx-border-radius: 36; -fx-effect: dropshadow(gaussian, #e9dfa0, 18, 0.14, 0, 7); -fx-border-color: #ece5d1; -fx-border-width: 1;");
        leftPanel.setAlignment(Pos.TOP_LEFT);

        // Dashboard heading
        Text dashTitle = new Text("Dyslexia Mode Dashboard");
        dashTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 38));
        dashTitle.setFill(Color.web("#b08d23"));

        Text dashDesc = new Text("Spacing adjustments, mirror letter cues, and optional TTS. Run a quick 10‑question check or reuse your last preset.");
        dashDesc.setFont(Font.font("Outfit", FontWeight.NORMAL, 20));
        dashDesc.setFill(Color.web("#7a5c19"));

        VBox dashHeader = new VBox(7, dashTitle, dashDesc);

        // Action buttons
        HBox actionRow = new HBox(18);
        Button startAssessmentBtn = new Button("Start Assessment");
        startAssessmentBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 18));
        startAssessmentBtn.setStyle("-fx-background-radius: 16; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 9 36 9 36; -fx-effect: dropshadow(gaussian, #00000044, 10, 0.18, 0, 2);");
        startAssessmentBtn.setOnAction(e -> {
            if (onStartAssessment != null) onStartAssessment.run();
        });

        actionRow.getChildren().addAll(startAssessmentBtn);

        // === Assessment Model Box ===
        VBox modelBox = new VBox(12);
        modelBox.setStyle("-fx-background-color: #f9f7ef; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        modelBox.setPadding(new Insets(18));

        Text modelHeading = new Text("Assessment Model (Simple)");
        modelHeading.setFont(Font.font("Outfit", FontWeight.BOLD, 18));
        modelHeading.setFill(Color.web("#b08d23"));

        List<String> bullets = Arrays.asList(
                "10 short questions + completion time.",
                "Answers + time become 6 signals:",
                "Random Forest chooses label: 0 (Severe) • 1 (Mild) • 2 (None).",
                "Label sets spacing & highlight intensity (0 also suggests TTS).",
                "You can override manually or create one custom preset via AI.",
                "Browser extension applies preset inline or overlay on any site."
        );

        VBox bulletList = new VBox(6);
        for (String s : bullets) {
            Text bullet = new Text("• " + s);
            bullet.setFont(Font.font("Outfit", FontWeight.NORMAL, 16));
            bullet.setFill(Color.web("#1e293b"));
            bulletList.getChildren().add(bullet);
            if (s.startsWith("Answers")) {
                // Add nested list
                VBox nested = new VBox(3);
                for (String sub : Arrays.asList(
                        "Word familiarity",
                        "Memory support",
                        "Pace (time‑adjusted)",
                        "Visual clarity",
                        "Sound distinction",
                        "Overall difficulty rating"
                )) {
                    Text n = new Text("   • " + sub);
                    n.setFont(Font.font("Outfit", FontWeight.NORMAL, 14));
                    n.setFill(Color.web("#6c4210"));
                    nested.getChildren().add(n);
                }
                bulletList.getChildren().add(nested);
            }
        }

        Text labelMap = new Text("Label Mapping:\n2 Normal • 1 Moderate spacing • 0 Heavy spacing + highlights + TTS suggestion");
        labelMap.setFont(Font.font("Outfit", FontWeight.BOLD, 13));
        labelMap.setFill(Color.web("#7a5c19"));
        labelMap.setStyle("-fx-background-color: #f7f8fa; -fx-background-radius: 12; -fx-padding: 7 8 7 8;");

        modelBox.getChildren().addAll(modelHeading, bulletList, labelMap);

        // === Tip Box ===
        VBox tipBox = new VBox(5);
        tipBox.setStyle("-fx-background-color: #f7f8fa; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        tipBox.setPadding(new Insets(13));
        Text tipTitle = new Text("Tip:");
        tipTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 14));
        tipTitle.setFill(Color.web("#1e293b"));
        Text tipDesc = new Text("If mild still feels hard, switch to heavy spacing or toggle TTS temporarily.");
        tipDesc.setFont(Font.font("Outfit", FontWeight.NORMAL, 14));
        tipDesc.setFill(Color.web("#7a5c19"));
        tipBox.getChildren().addAll(tipTitle, tipDesc);

        leftPanel.getChildren().addAll(dashHeader, actionRow, modelBox, tipBox);

        // === Right Images ===
        VBox rightImages = new VBox(20);
        rightImages.setAlignment(Pos.TOP_CENTER);

        // Dyslexia head profile image
        StackPane imgFrame1 = new StackPane();
        imgFrame1.setPrefSize(320, 240);
        imgFrame1.setStyle("-fx-background-color: #f9f7ef; -fx-background-radius: 22; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        ImageView img1 = new ImageView(new Image(getClass().getResourceAsStream("/images/dyslexia-head-profile.jpg")));
        img1.setFitWidth(320);
        img1.setFitHeight(240);
        img1.setPreserveRatio(true);
        imgFrame1.getChildren().add(img1);

        // ML assist brain image
        StackPane imgFrame2 = new StackPane();
        imgFrame2.setPrefSize(320, 200);
        imgFrame2.setStyle("-fx-background-color: #f9f7ef; -fx-background-radius: 22; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        ImageView img2 = new ImageView(new Image(getClass().getResourceAsStream("/images/ml-assist-brain.jpg")));
        img2.setFitWidth(320);
        img2.setFitHeight(200);
        img2.setPreserveRatio(true);
        imgFrame2.getChildren().add(img2);

        Text madeBy = new Text("APP PROJECT");
        madeBy.setFont(Font.font("Outfit", FontWeight.NORMAL, 11));
        madeBy.setFill(Color.web("#7a5c19"));

        rightImages.getChildren().addAll(imgFrame1, imgFrame2, madeBy);

        sectionGrid.getChildren().addAll(leftPanel, rightImages);

        VBox sectionRoot = new VBox(0, sectionGrid);

        // === Divider ===
        Region divider1 = new Region();
        divider1.setPrefHeight(2);
        divider1.setStyle("-fx-background-color: linear-gradient(to right, transparent, #ece5d1, transparent); -fx-opacity: 0.55;");

        // === Features Section ===
        VBox featuresSection = new VBox(20);
        featuresSection.setPadding(new Insets(38, 0, 0, 36));
        Text featuresTitle = new Text("Core Dyslexia Adaptation Features");
        featuresTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 28));
        featuresTitle.setFill(Color.web("#b08d23"));

        Text featuresDesc = new Text("Spacing, letter cues, audio, summaries, OCR, monitoring, and one AI‑built preset. Extend everywhere with the browser extension.");
        featuresDesc.setFont(Font.font("Outfit", FontWeight.NORMAL, 18));
        featuresDesc.setFill(Color.web("#7a5c19"));

        // Feature cards
        List<FeatureCard.Feature> features = Arrays.asList(
                new FeatureCard.Feature("Adaptive Spacing", "Scales letter & line gaps by label."),
                new FeatureCard.Feature("Mirror Letter Highlight", "Optional cues for b/d, p/q, m/w."),
                new FeatureCard.Feature("Text‑to‑Speech", "Listen + read; suggested in severe cases."),
                new FeatureCard.Feature("Manual Option", "You can also adapt the text manually."),
                new FeatureCard.Feature("OCR", "Extract text from images/screenshots."),
                new FeatureCard.Feature("Monitoring Agent", "Prototype suggests tweaks after pauses."),
                new FeatureCard.Feature("Custom Preset", "One stored AI‑refined configuration."),
                new FeatureCard.Feature("Extension", "Apply preset inline or overlay on any site.")
        );

        FlowPane featureGrid = new FlowPane();
        featureGrid.setHgap(16);
        featureGrid.setVgap(16);
        for (FeatureCard.Feature feature : features) {
            featureGrid.getChildren().add(new FeatureCard(feature));
        }
        featureGrid.setPadding(new Insets(10, 0, 0, 0));

        featuresSection.getChildren().addAll(featuresTitle, featuresDesc, featureGrid);

        // === Divider ===
        Region divider2 = new Region();
        divider2.setPrefHeight(2);
        divider2.setStyle("-fx-background-color: linear-gradient(to right, transparent, #ece5d1, transparent); -fx-opacity: 0.55;");

        // === Workflow Section ===
        VBox workflowSection = new VBox(18);
        workflowSection.setPadding(new Insets(38, 0, 0, 36));
        Text workflowTitle = new Text("Quick Workflow");
        workflowTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        workflowTitle.setFill(Color.web("#b08d23"));

        List<String> workflowSteps = Arrays.asList(
                "Take 10‑question check (time captured).",
                "Label sets baseline spacing & cues.",
                "Toggle TTS if decoding slows.",
                "Refine one custom preset with AI.",
                "Use extension on any site.",
                "OCR for images; monitoring suggests adjustments.",
                "Re‑assess when experience changes."
        );

        VBox workflowList = new VBox(8);
        int stepNum = 1;
        for (String s : workflowSteps) {
            Text step = new Text(stepNum + ". " + s);
            step.setFont(Font.font("Outfit", FontWeight.NORMAL, 16));
            step.setFill(Color.web("#7a5c19"));
            workflowList.getChildren().add(step);
            stepNum++;
        }

        VBox workflowBox = new VBox(8, workflowList);
        workflowBox.setPadding(new Insets(10, 0, 0, 10));

        VBox workflowNoteBox = new VBox();
        workflowNoteBox.setStyle("-fx-background-color: #f7f8fa; -fx-background-radius: 14; -fx-border-radius: 14; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        workflowNoteBox.setPadding(new Insets(14));
        Text workflowNote = new Text("Assistive prototype — not a clinical diagnostic.");
        workflowNote.setFont(Font.font("Outfit", FontWeight.NORMAL, 12));
        workflowNote.setFill(Color.web("#7a5c19"));
        workflowNoteBox.getChildren().add(workflowNote);

        workflowSection.getChildren().addAll(workflowTitle, workflowBox, workflowNoteBox);

        // Footer
        Text footer = new Text("Prototype Dyslexia environment – presets & model subject to change.");
        footer.setFont(Font.font("Outfit", FontWeight.NORMAL, 11));
        footer.setFill(Color.web("#7a5c19"));

        VBox dashboardContent = new VBox(30,
                sectionRoot,
                divider1,
                featuresSection,
                divider2,
                workflowSection,
                footer
        );
        dashboardContent.setAlignment(Pos.TOP_CENTER);
        dashboardContent.setPadding(new Insets(22, 0, 22, 0));

        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(gradientBg, particles, scrollPane, header, backBtn);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(scrollPane, Pos.CENTER);
    }
}