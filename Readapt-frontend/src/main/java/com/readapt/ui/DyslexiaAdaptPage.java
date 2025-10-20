package com.readapt.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class DyslexiaAdaptPage extends StackPane {
    private static double preset = 2;
    private static String pastedText = "";
    public static void setPreset(double p) { preset = Math.max(1, Math.min(3, p)); }
    public static double getPreset() { return preset; }
    public static void setPastedText(String t) { pastedText = t; }
    public static String getPastedText() { return pastedText.isEmpty() ? "No text found. Go back and paste some text." : pastedText; }

    private final Runnable onBackToDashboard;
    private final Runnable onPastePage;
    private final Runnable onResultsPage;
    private final VBox adaptedTextBox = new VBox();

    private boolean speaking = false;
    private Process ttsProcess = null;
    private Button ttsBtn;

    private boolean agentEnabled = true;
    private boolean agentDialogOpen = false;
    private long snoozedUntil = 0;
    private long lastActivity = System.currentTimeMillis();
    private Timer monitorTimer;
    private Alert agentDialog;

    // --- Keep slider and number as fields so agent can update them
    private Slider presetSlider;
    private Label presetNum;

    public DyslexiaAdaptPage(Runnable onBackToDashboard, Runnable onPastePage, Runnable onResultsPage, Runnable onHelp) {
        this.onBackToDashboard = onBackToDashboard;
        this.onPastePage = onPastePage;
        this.onResultsPage = onResultsPage;

        setStyle("-fx-background-color: #fffbe6;");
        Header header = new Header(onHelp);
        setupMonitoringAgent();
        this.setOnMouseMoved(e -> markActivity());
        this.setOnKeyPressed(e -> markActivity());
        this.setOnScroll(e -> markActivity());

        VBox mainSection = new VBox(18);
        mainSection.setAlignment(Pos.TOP_CENTER);
        mainSection.setPadding(new Insets(140, 0, 28, 0));

        HBox glassCardRow = new HBox();
        glassCardRow.setAlignment(Pos.CENTER);

        VBox glassCard = new VBox(40);
        glassCard.setAlignment(Pos.TOP_CENTER);
        glassCard.setMaxWidth(1700);
        glassCard.setMinWidth(1350);
        glassCard.setPrefWidth(1550);
        glassCard.setPadding(new Insets(48, 64, 48, 64));
        glassCard.setStyle(
            "-fx-background-color: #fff;" +
            "-fx-background-radius: 28;" +
            "-fx-border-radius: 28;" +
            "-fx-border-color: #b08d23;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, #d1d5db, 24, 0.10, 0, 11);"
        );

        VBox manualAndButtons = new VBox(18);
        manualAndButtons.setAlignment(Pos.CENTER);

        HBox topBar = new HBox(60);
        topBar.setAlignment(Pos.CENTER);

        Label adjustLabel = new Label("Manual Adjust");
        adjustLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 24));
        adjustLabel.setTextFill(Color.web("#6c4210"));
        adjustLabel.setMinWidth(210);

        presetSlider = new Slider(1, 3, preset);
        presetSlider.setBlockIncrement(0.25);
        presetSlider.setMajorTickUnit(0.25);
        presetSlider.setMinorTickCount(0);
        presetSlider.setSnapToTicks(true);
        presetSlider.setShowTickMarks(true);
        presetSlider.setShowTickLabels(false);
        presetSlider.setPrefWidth(900);

        presetNum = new Label("Preset " + String.format("%.2f", preset));
        presetNum.setFont(Font.font("Outfit", FontWeight.BOLD, 24));
        presetNum.setTextFill(Color.web("#2563eb"));
        presetNum.setMinWidth(180);

        presetSlider.valueProperty().addListener((obs, ov, nv) -> {
            double newValue = Math.round(nv.doubleValue() * 4.0) / 4.0;
            presetSlider.setValue(newValue);
            preset = newValue;
            presetNum.setText("Preset " + String.format("%.2f", preset));
            renderAdaptedText();
            markActivity();
        });

        topBar.getChildren().addAll(adjustLabel, presetSlider, presetNum);

        HBox buttonRow = new HBox(24);
        buttonRow.setAlignment(Pos.CENTER);

        // Removed Custom and Readapt buttons as requested

        Button pasteBtn = new Button("Paste");
        pasteBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        pasteBtn.setStyle("-fx-background-radius: 12; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 16 64 16 64;");
        pasteBtn.setMinWidth(230);
        pasteBtn.setMaxWidth(320);
        pasteBtn.setPrefWidth(250);
        pasteBtn.setOnAction(e -> {
            if (onPastePage != null) onPastePage.run();
        });

        Button resultsBtn = new Button("Results");
        resultsBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        resultsBtn.setStyle("-fx-background-radius: 12; -fx-background-color: #f7f8fa; -fx-text-fill: #2563eb; -fx-padding: 16 64 16 64;");
        resultsBtn.setMinWidth(230);
        resultsBtn.setMaxWidth(320);
        resultsBtn.setPrefWidth(250);
        resultsBtn.setOnAction(e -> {
            if (onResultsPage != null) onResultsPage.run();
        });

        ttsBtn = new Button(getTtsLabel());
        ttsBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        ttsBtn.setStyle("-fx-background-radius: 12; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 16 64 16 64;");
        ttsBtn.setMinWidth(230);
        ttsBtn.setMaxWidth(320);
        ttsBtn.setPrefWidth(250);
        ttsBtn.setVisible(Math.floor(preset) >= 3);
        ttsBtn.setOnAction(e -> {
            if (speaking) {
                stopTTS();
            } else {
                startTTS(getPastedText());
            }
        });

        // Only add pasteBtn, resultsBtn, ttsBtn to the row
        buttonRow.getChildren().addAll(pasteBtn, resultsBtn, ttsBtn);

        manualAndButtons.getChildren().addAll(topBar, buttonRow);

        VBox contentBox = new VBox(44);
        contentBox.setAlignment(Pos.TOP_CENTER);

        adaptedTextBox.setAlignment(Pos.TOP_LEFT);
        adaptedTextBox.setStyle("-fx-background-color: #fffce8; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #ece5d1; -fx-border-width: 1;");
        adaptedTextBox.setPadding(new Insets(48, 60, 48, 60));
        adaptedTextBox.setMaxWidth(1500);
        adaptedTextBox.setMinWidth(1400);
        adaptedTextBox.setPrefWidth(1450);

        renderAdaptedText();

        contentBox.getChildren().addAll(adaptedTextBox);

        glassCard.getChildren().addAll(manualAndButtons, contentBox);

        glassCardRow.getChildren().add(glassCard);

        ScrollPane scrollPane = new ScrollPane(glassCardRow);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0,0,0,0));

        mainSection.getChildren().add(scrollPane);

        this.getChildren().addAll(mainSection, header);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(mainSection, Pos.TOP_CENTER);
        header.toFront();
    }

    private void setupMonitoringAgent() {
        monitorTimer = new Timer(true);
        monitorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (agentEnabled && !agentDialogOpen && !speaking && Math.floor(preset) < 3) {
                        long now = System.currentTimeMillis();
                        if (snoozedUntil > now) return;
                        if (now - lastActivity > 15000) {
                            agentDialogOpen = true;
                            showAgentDialog();
                        }
                    }
                });
            }
        }, 15000, 1000);
    }

    private void markActivity() {
        lastActivity = System.currentTimeMillis();
        if (agentDialog != null && agentDialog.isShowing()) agentDialog.hide();
        agentDialogOpen = false;
    }

    private void showAgentDialog() {
        if (agentDialog != null && agentDialog.isShowing()) return;
        agentDialog = new Alert(Alert.AlertType.CONFIRMATION);
        agentDialog.setTitle("Struggling to read?");
        agentDialog.setHeaderText("We detected inactivity. Increase preset by +0.25?");
        ButtonType yesBtn = new ButtonType("Yes, enhance", ButtonBar.ButtonData.YES);
        ButtonType noBtn = new ButtonType("No, thanks", ButtonBar.ButtonData.NO);
        ButtonType disableBtn = new ButtonType("Disable agent permanently", ButtonBar.ButtonData.CANCEL_CLOSE);
        agentDialog.getButtonTypes().setAll(yesBtn, noBtn, disableBtn);
        agentDialog.setOnCloseRequest(e -> agentDialogOpen = false);
        agentDialog.show();

        agentDialog.resultProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == yesBtn && preset < 3) {
                preset = Math.min(3, Math.round((preset + 0.25) * 4.0) / 4.0);
                presetSlider.setValue(preset);
                presetNum.setText("Preset " + String.format("%.2f", preset));
                renderAdaptedText();
                markActivity();
            } else if (newVal == noBtn) {
                snoozedUntil = System.currentTimeMillis() + 300000; // 5 mins
                markActivity();
            } else if (newVal == disableBtn) {
                agentEnabled = false;
            }
            agentDialogOpen = false;
        });
    }

    private void renderAdaptedText() {
        adaptedTextBox.getChildren().clear();

        // User requested values and proportional increments
        double fontSize = interp(preset, new double[]{34, 35, 36});
        double letterSpacingEm = interp(preset, new double[]{0.0, 0.2, 0.3});
        double wordSpacingEm = interp(preset, new double[]{0.8, 1.2, 1.8});
        double lineHeightEm = interp(preset, new double[]{0.8, 1.6, 2.4});

        Font font = Font.font("Outfit", FontWeight.NORMAL, fontSize);
        boolean showHighlights = Math.floor(preset) >= 3;
        ttsBtn.setVisible(showHighlights);

        String[] paragraphs = getPastedText().split("\\r?\\n");

        for (int pidx = 0; pidx < paragraphs.length; ++pidx) {
            String para = paragraphs[pidx];

            // Always use FlowPane for word spacing with hgap for all presets
            FlowPane flow = new FlowPane();
            flow.setAlignment(Pos.TOP_LEFT);
            flow.setHgap(wordSpacingEm * fontSize);
            flow.setVgap(lineHeightEm * fontSize);
            flow.setMaxWidth(1450);
            flow.setMinWidth(1200);
            flow.setPrefWidth(1400);
            flow.setPadding(new Insets(2, 2, 2, 2));
            flow.setStyle("-fx-background-color: transparent;");
            String[] words = para.split("\\s+");
            for (String word : words) {
                TextFlow wordFlow = buildTextFlowWithLetterSpacingEm(word, font, letterSpacingEm, showHighlights);
                flow.getChildren().add(wordFlow);
            }
            adaptedTextBox.getChildren().add(flow);

            if (pidx < paragraphs.length-1) {
                Region spacer = new Region();
                spacer.setMinHeight(lineHeightEm * fontSize);
                adaptedTextBox.getChildren().add(spacer);
            }
        }
    }

    private TextFlow buildTextFlowWithLetterSpacingEm(String text, Font font, double letterSpacingEm, boolean highlight) {
        TextFlow flow = new TextFlow();
        flow.setLineSpacing(0);
        Pattern confuser = Pattern.compile("[bdpqmnun]", Pattern.CASE_INSENSITIVE);

        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            Text t = new Text(String.valueOf(ch));
            t.setFont(font);

            if (highlight && confuser.matcher(String.valueOf(ch)).find()) {
                t.setFill(Color.web("#2563eb"));
                t.setStyle("-fx-background-color: #cbd5f7; -fx-background-radius: 4;");
            } else {
                t.setFill(Color.web("#111"));
            }

            flow.getChildren().add(t);

            // Letter spacing: add after each letter except space/last char
            if (i < text.length() - 1 && text.charAt(i) != ' ' && text.charAt(i+1) != ' ') {
                Region space = new Region();
                space.setMinWidth(letterSpacingEm * font.getSize());
                space.setPrefWidth(letterSpacingEm * font.getSize());
                flow.getChildren().add(space);
            }
        }
        return flow;
    }

    private double interp(double v, double[] arr) {
        int i = (int)Math.floor(v) - 1;
        double frac = v - Math.floor(v);
        if (i >= arr.length - 1) return arr[arr.length - 1];
        return arr[i] + (arr[i + 1] - arr[i]) * frac;
    }

    private String getTtsLabel() {
        return speaking ? "Stop TTS" : "Listen";
    }

    private void startTTS(String text) {
        stopTTS();
        speaking = true;
        ttsBtn.setText(getTtsLabel());

        String os = System.getProperty("os.name").toLowerCase();
        try {
            String toSpeak = text.length() > 6000 ? text.substring(0, 6000) : text;
            final Process process;
            if (os.contains("win")) {
                String command = "PowerShell -Command \"Add-Type –AssemblyName System.speech; " +
                        "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "$voices = $speak.GetInstalledVoices() | Where-Object { $_.VoiceInfo.Gender -eq 'Female' };" +
                        "if ($voices.Count -gt 0) { $speak.SelectVoice($voices[0].VoiceInfo.Name); } else { $speak.SelectVoice('Microsoft Zira Desktop'); };" +
                        "$speak.Rate = -2; " +
                        "$speak.Speak('" + toSpeak.replace("'", "''") + "');\"";
                process = Runtime.getRuntime().exec(command);
            } else if (os.contains("mac")) {
                String command = "say -v \"Kate\" -r 165 \"" + toSpeak.replace("\"", "\\\"") + "\"";
                process = Runtime.getRuntime().exec(command);
            } else {
                process = null;
            }
            ttsProcess = process;
            if (process == null) {
                speaking = false;
                Platform.runLater(() -> ttsBtn.setText(getTtsLabel()));
            } else {
                new Thread(() -> {
                    try { process.waitFor(); } catch (Exception ignored) {}
                    Platform.runLater(() -> {
                        speaking = false;
                        ttsBtn.setText(getTtsLabel());
                        ttsProcess = null;
                    });
                }).start();
            }
        } catch (Exception ex) {
            speaking = false;
            Platform.runLater(() -> ttsBtn.setText(getTtsLabel()));
        }
    }

    private void stopTTS() {
        if (ttsProcess != null) {
            ttsProcess.destroy();
            ttsProcess = null;
        }
        speaking = false;
        Platform.runLater(() -> ttsBtn.setText(getTtsLabel()));
    }

    private void showAlert(String title, String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.setTitle(title);
            alert.showAndWait();
        });
    }
}