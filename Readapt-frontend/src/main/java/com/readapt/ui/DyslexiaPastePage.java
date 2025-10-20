package com.readapt.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DyslexiaPastePage extends StackPane {
    private String text = "";
    private boolean ocrBusy = false;
    private int ocrProgress = 0;

    private final TextArea textArea = new TextArea();
    private final Label ocrLabel = new Label();
    private final ProgressBar ocrProgressBar = new ProgressBar(0);

    private final Runnable onProceed;
    private final Runnable onRedoQuiz;
    private final Runnable onGoToResults;

    private final Button proceedBtn = new Button("Proceed");
    private final Button useSampleBtn = new Button("Use sample text");
    private final Button redoBtn = new Button("Redo Assessment");
    private final Button ocrBtn = new Button("OCR Image");

    // The preset from the results page, for passing to Adapt
    private int passedPreset = 2; // default to mild

    private final String sampleText =
        "Stars are luminous, giant spheres of hot plasma—primarily hydrogen and helium—that generate their own energy through nuclear fusion in their cores, a process that powers them for millions to billions of years and makes them the primary sources of light and heat in the universe. They are born from vast clouds of gas and dust that collapse under gravity, and their properties vary widely in size, mass, temperature, and luminosity, shaping the diversity of stellar types such as red dwarfs, massive blue stars, and white dwarfs. The Sun, our closest star, sustains life on Earth by providing energy, and like all stars, it follows a life cycle in which it will eventually exhaust its nuclear fuel, expand into a red giant, and later shed its outer layers, leaving behind a dense white dwarf core. Some stars, particularly the most massive ones, end their lives in cataclysmic explosions called supernovae, which scatter heavy elements into space and can collapse into neutron stars or black holes. A black hole is an extraordinary region of space where matter is compressed so densely that its gravitational pull becomes so strong even light cannot escape once it crosses the event horizon, effectively making the black hole invisible except through its gravitational influence on surrounding matter. They form from the collapse of massive stars and exist in different scales, from stellar-mass black holes to supermassive black holes that reside at the centers of galaxies, including the Milky Way. These enigmatic objects warp spacetime itself, consume nearby gas and dust, and power energetic phenomena such as quasars, making their study crucial for understanding cosmic evolution. In contrast, a white hole is a purely theoretical concept described by general relativity as the opposite of a black hole, where matter, light, and energy can only flow outward, with nothing ever entering it. Though no evidence of white holes has been observed, they are often speculated about in physics as potential counterparts to black holes, sometimes imagined as connected through wormholes that could bridge distant regions of space and time, or even as hypothetical endpoints of black hole evaporation through Hawking radiation. While black holes are accepted as real, observable cosmic entities, white holes remain in the realm of theory, yet their study fuels profound questions about the nature of spacetime, the limits of physics, and the ultimate fate of the universe.";

    public DyslexiaPastePage(Runnable onProceed, Runnable onRedoQuiz, Runnable onGoToResults, Runnable onHelp) {
        this.onProceed = onProceed;
        this.onRedoQuiz = onRedoQuiz;
        this.onGoToResults = onGoToResults;

        // Set the correct preset from results page on construction
        this.passedPreset = DyslexiaResultsPage.getLatestPreset();

        setStyle("-fx-background-color: #fffbe6;");
        Header header = new Header(onHelp);

        Label pageTitle = new Label("Paste your text (Dyslexia)");
        pageTitle.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        pageTitle.setTextFill(Color.web("#2563eb"));
        pageTitle.setPadding(new Insets(0, 0, 8, 0));

        textArea.setPromptText("Paste any text here...");
        textArea.setFont(Font.font("Outfit", FontWeight.NORMAL, 17));
        textArea.setWrapText(true);
        textArea.setMinHeight(240);

        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            text = newVal;
            proceedBtn.setDisable(text.trim().isEmpty());
        });

        double wideBtnWidth = 220;

        useSampleBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        useSampleBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 12 24 12 24;");
        useSampleBtn.setMinWidth(wideBtnWidth);
        useSampleBtn.setOnAction(e -> {
            textArea.setText(sampleText);
            text = sampleText;
            proceedBtn.setDisable(text.trim().isEmpty());
        });

        ocrBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        ocrBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #fffbe6; -fx-border-color: #b08d23; -fx-border-width: 2; -fx-text-fill: #b08d23; -fx-padding: 12 24 12 24;");
        ocrBtn.setMinWidth(wideBtnWidth);
        ocrBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image for OCR");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
            File file = fileChooser.showOpenDialog(getScene() != null ? (Stage) getScene().getWindow() : null);
            if (file != null) handleOcrFile(file);
        });

        redoBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        redoBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #e0e7ff; -fx-text-fill: #2563eb; -fx-padding: 12 24 12 24;");
        redoBtn.setMinWidth(wideBtnWidth);
        redoBtn.setOnAction(e -> {
            if (onRedoQuiz != null) onRedoQuiz.run();
        });

        proceedBtn.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        proceedBtn.setStyle("-fx-background-radius: 14; -fx-background-color: #2563eb; -fx-text-fill: #fff; -fx-padding: 12 24 12 24;");
        proceedBtn.setMinWidth(wideBtnWidth);
        proceedBtn.setDisable(true);
        proceedBtn.setOnAction(e -> {
            DyslexiaAdaptPage.setPreset(DyslexiaResultsPage.getLatestPreset());
            DyslexiaAdaptPage.setPastedText(textArea.getText());
            if (onProceed != null) onProceed.run(); // main.showDyslexiaAdapt()
        });

        ocrLabel.setFont(Font.font("Outfit", FontWeight.NORMAL, 13));
        ocrLabel.setTextFill(Color.web("#2563eb"));
        ocrLabel.setPadding(new Insets(6, 0, 0, 0));
        ocrLabel.setVisible(false);

        ocrProgressBar.setMinWidth(220);
        ocrProgressBar.setPrefHeight(7);
        ocrProgressBar.setStyle("-fx-accent: #2563eb;");
        ocrProgressBar.setVisible(false);

        HBox topBtnRow = new HBox(24, useSampleBtn, ocrBtn);
        topBtnRow.setAlignment(Pos.CENTER);
        topBtnRow.setPadding(new Insets(6, 0, 0, 0));

        HBox bottomBtnRow = new HBox(24, redoBtn, proceedBtn);
        bottomBtnRow.setAlignment(Pos.CENTER);
        bottomBtnRow.setPadding(new Insets(6, 0, 0, 0));

        VBox glassCard = new VBox(18,
                pageTitle,
                textArea,
                ocrLabel,
                ocrProgressBar,
                topBtnRow,
                bottomBtnRow
        );
        glassCard.setMaxWidth(700);
        glassCard.setPadding(new Insets(24, 30, 32, 30));
        glassCard.setAlignment(Pos.TOP_CENTER);
        glassCard.setStyle("-fx-background-color: rgba(255,255,255,0.84); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, #d1d5db, 18, 0.10, 0, 10);");

        VBox pageBox = new VBox(glassCard);
        pageBox.setAlignment(Pos.TOP_CENTER);
        pageBox.setPadding(new Insets(94, 0, 40, 0));

        ScrollPane scrollPane = new ScrollPane(pageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(scrollPane, header);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(scrollPane, Pos.CENTER);
    }

    private void handleOcrFile(File file) {
        ocrBusy = true;
        Platform.runLater(() -> {
            ocrLabel.setText("Recognizing image...");
            ocrLabel.setVisible(true);
            ocrProgressBar.setVisible(true);
            ocrProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        });

        new Thread(() -> {
            try {
                String ocrApiUrl = "http://localhost:8080/api/ocr";
                HttpURLConnection conn = (HttpURLConnection) new URL(ocrApiUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream os = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);

                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: image/png\r\n\r\n").flush();

                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
                fis.close();

                writer.append("\r\n--").append(boundary).append("--\r\n").flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();
                String response = readStream(is);

                String ocrText = "";
                if (response.contains("\"text\"")) {
                    int idx = response.indexOf("\"text\"");
                    int colon = response.indexOf(":", idx) + 1;
                    int quote1 = response.indexOf("\"", colon);
                    int quote2 = response.indexOf("\"", quote1 + 1);
                    ocrText = response.substring(quote1 + 1, quote2);
                    ocrText = ocrText.replace("\\n", "\n");
                }

                if (!ocrText.isEmpty()) {
                    String prev = textArea.getText();
                    textArea.setText(prev.isEmpty() ? ocrText : prev + "\n\n" + ocrText);
                } else {
                    Platform.runLater(() ->
                            showAlert("OCR failed", "Could not recognize any text."));
                }
            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert("OCR failed", "Error: " + e.getMessage()));
            } finally {
                ocrBusy = false;
                Platform.runLater(() -> {
                    ocrLabel.setVisible(false);
                    ocrProgressBar.setVisible(false);
                });
            }
        }).start();
    }

    private String readStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}