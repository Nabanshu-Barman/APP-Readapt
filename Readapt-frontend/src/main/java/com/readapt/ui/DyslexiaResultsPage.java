package com.readapt.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.*;

public class DyslexiaResultsPage extends StackPane {
    // Static storage for latest result
    private static int latestLabel = 1;
    private static List<Double> latestFeatures = Arrays.asList(0d,0d,0d,0d,0d,0d);
    private static String latestDebugLog = "";
    private static int latestPreset = 2; // default to mild

    public static void setLatestResult(int label, List<Double> features, String debugLog) {
        latestLabel = label;
        latestFeatures = features;
        latestDebugLog = debugLog;
        latestPreset = mapLabelToPreset(label);
    }

    // Map label to preset for Adapt page (1=Normal, 2=Mild, 3=Severe)
    private static int mapLabelToPreset(int label) {
        if (label == 2) return 1; // Normal
        if (label == 1) return 2; // Mild
        return 3; // Severe
    }
    public static int getLatestPreset() {
        return latestPreset;
    }

    public DyslexiaResultsPage(Runnable onRedoQuiz, Runnable onBackToDashboard, Runnable onPasteText, Runnable onHelp) {
        setStyle("-fx-background-color: #fffbe6;");
        Header header = new Header(onHelp);

        VBox mainSection = new VBox(24);
        mainSection.setAlignment(Pos.TOP_CENTER);
        mainSection.setPadding(new Insets(120, 0, 28, 0));

        VBox resultsCard = new VBox(22);
        resultsCard.setAlignment(Pos.TOP_LEFT);
        resultsCard.setMaxWidth(760);
        resultsCard.setPadding(new Insets(38, 38, 32, 38));
        resultsCard.setStyle("-fx-background-color: #fff; -fx-background-radius: 32; -fx-border-radius: 32; -fx-border-color: #b08d23; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, #d1d5db, 16, 0.10, 0, 6);");

        // Severity info
        String[] severityNames = {"Severe", "Mild", "Normal"};
        String[] severityDesc = {
                "Stronger indicators. We’ll maximize spacing, highlight mirror letters, and suggest enabling TTS.",
                "Some mild indicators. Gentle spacing adjustments will assist decoding.",
                "No strong dyslexia indicators. Standard spacing & visuals applied."
        };
        String[] presetDesc = {
                "Heavy spacing + letter highlights + TTS suggestion",
                "Moderate spacing & interline spacing",
                "Normal reading mode"
        };
        Color[] severityColors = {Color.web("#ff4e4e"), Color.web("#f59e42"), Color.web("#25be7b")};
        int label = latestLabel;
        if (label < 0 || label > 2) label = 1;

        Color presetColor = severityColors[label];

        Label title = new Label("Dyslexia Assessment Result");
        title.setFont(Font.font("Outfit", FontWeight.BOLD, 27)); // slightly smaller
        title.setTextFill(Color.web("#2563eb"));
        title.setWrapText(true);
        title.setMaxWidth(700);

        Label severity = new Label("Severity: " + severityNames[label]);
        severity.setFont(Font.font("Outfit", FontWeight.BOLD, 21));
        severity.setTextFill(severityColors[label]);
        HBox severityRow = new HBox(severity);
        severityRow.setAlignment(Pos.CENTER_LEFT);

        Label labelChip = new Label("Label " + label);
        labelChip.setFont(Font.font("Outfit", FontWeight.BOLD, 17));
        labelChip.setTextFill(severityColors[label]);
        labelChip.setStyle("-fx-background-radius: 13; -fx-background-color: #f7f8fa; -fx-padding: 4 16 4 16;");
        HBox chipRow = new HBox(8, severityRow, labelChip);
        chipRow.setAlignment(Pos.CENTER_LEFT);

        Label desc = new Label(severityDesc[label]);
        desc.setFont(Font.font("Outfit", FontWeight.NORMAL, 15));
        desc.setTextFill(Color.web("#6c4210"));
        desc.setWrapText(true);

        Label preset = new Label("Applied/Recommended Preset: " + presetDesc[label]);
        preset.setFont(Font.font("Outfit", FontWeight.BOLD, 18));
        preset.setTextFill(presetColor);
        preset.setWrapText(true);
        preset.setMaxWidth(700);
        preset.setPadding(new Insets(10, 0, 0, 0));

        VBox presetCard = new VBox(4, preset,
            styledLabel("• Label 2: Normal reading mode."),
            styledLabel("• Label 1: Moderate spacing + moderate interline spacing."),
            styledLabel("• Label 0: Heavy spacing + heavy interline spacing + mirror letter highlight + TTS suggestion.")
        );
        presetCard.setStyle("-fx-background-color: #f7f8fa; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        presetCard.setPadding(new Insets(16, 16, 16, 16));
        presetCard.setMaxWidth(700);

        HBox buttonRow = new HBox(18);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button redoBtn = new Button("Redo Assessment");
        redoBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 15));
        redoBtn.setStyle("-fx-background-radius: 18; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 9 28 9 28;");
        redoBtn.setOnAction(e -> {
            if (onRedoQuiz != null) onRedoQuiz.run();
        });

        Button dashboardBtn = new Button("Back to Dashboard");
        dashboardBtn.setFont(Font.font("Outfit", FontWeight.NORMAL, 15));
        dashboardBtn.setStyle("-fx-background-radius: 18; -fx-background-color: #f7f8fa; -fx-text-fill: #7a5c19; -fx-padding: 9 28 9 28;");
        dashboardBtn.setOnAction(e -> {
            if (onBackToDashboard != null) onBackToDashboard.run();
        });

        Button pasteTextBtn = new Button("Paste Text");
        pasteTextBtn.setFont(Font.font("Outfit", FontWeight.NORMAL, 15));
        pasteTextBtn.setStyle("-fx-background-radius: 18; -fx-background-color: #f7f8fa; -fx-text-fill: #2563eb; -fx-padding: 9 28 9 28;");
        pasteTextBtn.setOnAction(e -> {
            if (onPasteText != null) onPasteText.run();
        });

        buttonRow.getChildren().addAll(redoBtn, dashboardBtn, pasteTextBtn);

        resultsCard.getChildren().addAll(
                title,
                chipRow,
                desc,
                presetCard,
                buttonRow,
                new Separator()
        );

        // Feature Breakdown (with bigger text)
        VBox featureBreakdown = new VBox(12);
        featureBreakdown.setAlignment(Pos.TOP_LEFT);
        featureBreakdown.setMaxWidth(700);

        Label featureTitle = new Label("Feature Breakdown");
        featureTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 24));
        featureTitle.setTextFill(Color.web("#2563eb"));

        String[] featureNames = {
                "Word & Language Skill",
                "Short-Term Memory",
                "Reading Pace Factor",
                "Visual Letter Clarity",
                "Sound Recognition",
                "Overall Difficulty Index"
        };
        String[] featureExplanations = {
                "Recognizing letters & familiar words",
                "Holding word / letter info briefly",
                "Relative speed (shorter time → higher score)",
                "Distinguishing similar letter shapes",
                "Separating similar speech sounds",
                "Self-reported + aggregate challenge"
        };

        GridPane featureTable = new GridPane();
        featureTable.setHgap(20);
        featureTable.setVgap(12);
        featureTable.setPadding(new Insets(12, 0, 12, 0));
        Label col1 = new Label("Component");
        col1.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        Label col2 = new Label("Plain Meaning");
        col2.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        Label col3 = new Label("Score (0–1)");
        col3.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        featureTable.add(col1, 0, 0);
        featureTable.add(col2, 1, 0);
        featureTable.add(col3, 2, 0);

        for (int i = 0; i < latestFeatures.size(); i++) {
            Label n = new Label(featureNames[i]);
            n.setFont(Font.font("Outfit", FontWeight.BOLD, 15));
            Label e = new Label(featureExplanations[i]);
            e.setFont(Font.font("Outfit", FontWeight.NORMAL, 15));
            Label v = new Label(String.format("%.2f", latestFeatures.get(i)));
            v.setFont(Font.font("Outfit", FontWeight.BOLD, 15));
            featureTable.add(n, 0, i+1);
            featureTable.add(e, 1, i+1);
            featureTable.add(v, 2, i+1);
        }
        featureBreakdown.getChildren().add(featureTitle);
        featureBreakdown.getChildren().add(featureTable);

        // Stats for Nerds: include formulas and formatted debug log
        if (latestDebugLog != null && !latestDebugLog.isEmpty()) {
            String formulas =
                    "Language_vocab = (Ans.1 + Ans.2 + Ans.3 + Ans.4 + Ans.5 + Ans.6 + Ans.8)/14\n" +
                    "Memory = (Ans.2 + Ans.9)/4\n" +
                    "Speed = Calculated on the basis of time taken to complete the quiz\n" +
                    "Visual_discrimination = (Ans.1 + Ans.3 + Ans.4 + Ans.6)/8\n" +
                    "Audio_Discrimination = (Ans.7 + Ans.10)/4\n" +
                    "Survey_Score = (Sum of all answers)/40\n\n";
            String formattedLog = formulas + latestDebugLog.replace("\\n", "\n");
            TextArea logTextArea = new TextArea(formattedLog);
            logTextArea.setWrapText(true);
            logTextArea.setEditable(false);
            logTextArea.setPrefRowCount(14);
            logTextArea.setFont(Font.font("Consolas", 14));
            TitledPane debugPane = new TitledPane("Stats for Nerds", logTextArea);
            debugPane.setExpanded(false);
            featureBreakdown.getChildren().add(debugPane);
        }

        VBox inner = new VBox(40, resultsCard, featureBreakdown);
        inner.setAlignment(Pos.TOP_CENTER);
        inner.setPadding(new Insets(0, 0, 32, 0));
        ScrollPane scrollPane = new ScrollPane(inner);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0, 0, 0, 0));

        mainSection.getChildren().addAll(scrollPane);
        this.getChildren().addAll(mainSection, header);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(mainSection, Pos.TOP_CENTER);
        header.toFront();
    }

    private static Label styledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Outfit", FontWeight.NORMAL, 15));
        lbl.setTextFill(Color.web("#6b7280"));
        lbl.setWrapText(true);
        lbl.setMaxWidth(700);
        return lbl;
    }
}