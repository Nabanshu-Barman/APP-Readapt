package com.readapt.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class DyslexiaQuizPage extends StackPane {
    public static class QuizOption {
        public String code;
        public String label;
        public int value;
        public QuizOption(String code, String label, int value) {
            this.code = code; this.label = label; this.value = value;
        }
    }
    public static class QuizQuestion {
        public int index;
        public String id;
        public String typeLabel;
        public String prompt;
        public String mediaType;
        public String imageSrc;
        public String audioSrc;
        public List<QuizOption> options;
        public List<String> constructs;
        public QuizQuestion(int index, String id, String typeLabel, String prompt, String mediaType,
                            String imageSrc, String audioSrc, List<QuizOption> options, List<String> constructs) {
            this.index = index; this.id = id; this.typeLabel = typeLabel; this.prompt = prompt;
            this.mediaType = mediaType; this.imageSrc = imageSrc; this.audioSrc = audioSrc;
            this.options = options; this.constructs = constructs;
        }
    }

    public static final List<QuizQuestion> QUESTIONS = Arrays.asList(
        new QuizQuestion(0, "q0-letter-b-d", "Letter Discrimination (Visual + Language)",
                "What is the second letter of the alphabet?", "image", "/images/quiz/q1.png", null,
                Arrays.asList(new QuizOption("A","1",2), new QuizOption("B","2",1), new QuizOption("C","3",0)),
                Arrays.asList("Language_vocab","Visual_discrimination")),
        new QuizQuestion(1, "q1-cat-word", "Word Recognition (Language + Memory)",
                "Which word matches the picture?", "image", "/images/quiz/q2.jpg", null,
                Arrays.asList(new QuizOption("A","cat",2), new QuizOption("B","ↄat",1), new QuizOption("C","cta",0)),
                Arrays.asList("Language_vocab","Memory")),
        new QuizQuestion(2, "q2-letter-g", "Shape / Letter Identification (Visual + Language)",
                "Which of the two is correct?", "image", "/images/quiz/q3.png", null,
                Arrays.asList(new QuizOption("A","1",2), new QuizOption("B","2",1), new QuizOption("C","3",0)),
                Arrays.asList("Language_vocab","Visual_discrimination")),
        new QuizQuestion(3, "q3-letter-order", "Letter Order / Serial (Visual + Language)",
                "Which picture shows the correct left‑to‑right order?", "image", "/images/quiz/q4.png", null,
                Arrays.asList(new QuizOption("A","1",2), new QuizOption("B","2",1), new QuizOption("C","none",0)),
                Arrays.asList("Language_vocab","Visual_discrimination")),
        new QuizQuestion(4, "q4-pineapple", "Vocabulary / Semantics (Language)",
                "Which label best names the pictured object?", "image", "/images/quiz/q5.png", null,
                Arrays.asList(new QuizOption("A","pineapple",2), new QuizOption("B","pineabble",1), new QuizOption("C","peinaddle",0)),
                Arrays.asList("Language_vocab")),
        new QuizQuestion(5, "q5-upper-lower", "Upper–Lower Mapping (Visual + Language)",
                "Which lowercase letter matches the uppercase letter shown?", "image", "/images/quiz/q6.png", null,
                Arrays.asList(new QuizOption("A","q",2), new QuizOption("B","p",1), new QuizOption("C","d",0)),
                Arrays.asList("Language_vocab","Visual_discrimination")),
        new QuizQuestion(6, "q6-audio-phoneme", "Phoneme Discrimination (Audio)",
                "What sound do you hear? (Choose the closest single consonant.)", "audio", null, "/images/quiz/q7.mp3",
                Arrays.asList(new QuizOption("A","m",2), new QuizOption("B","n",1), new QuizOption("C","f",0)),
                Arrays.asList("Audio_Discrimination")),
        new QuizQuestion(7, "q7-object-teapot", "Object Naming (Language)",
                "Which is the object in the picture?", "image", "/images/quiz/q8.png", null,
                Arrays.asList(new QuizOption("A","teapot",2), new QuizOption("B","taepot",1), new QuizOption("C","taebot",0)),
                Arrays.asList("Language_vocab")),
        new QuizQuestion(8, "q8-recall-cat", "Picture Recall (Memory)",
                "Has this picture been shown earlier in the quiz?", "image", "/images/quiz/q9.jpg", null,
                Arrays.asList(new QuizOption("A","Yes (exact same image)",2), new QuizOption("B","similar image shown",1), new QuizOption("C","No",0)),
                Arrays.asList("Memory")),
        new QuizQuestion(9, "q9-audio-word", "Word Discrimination (Audio)",
                "Which word was spoken?", "audio", null, "/images/quiz/q10.mp3",
                Arrays.asList(new QuizOption("A","Lake",2), new QuizOption("B","⅃ake",1), new QuizOption("C","ache",0)),
                Arrays.asList("Audio_Discrimination"))
    );

    private int current = 0;
    private final List<Integer> answers = new ArrayList<>();
    private long startTime = 0;
    private int elapsedSeconds = 0;
    private AnimationTimer timer;
    private MediaPlayer currentAudioPlayer;

    private final Label timerLabel = new Label("00:00");
    private final ProgressBar progressBar = new ProgressBar(0.0);
    private final VBox card = new VBox(16);
    private final VBox questionBlock = new VBox(8);
    private final HBox progressRow = new HBox(14);
    private final StackPane mainArea = new StackPane();
    private final HBox optionsRow = new HBox(16);
    private final HBox navRow = new HBox(14);
    private final Label footer = new Label("Demo only – not a medical or diagnostic tool. Research refs: Snowling; Swanson & Siegel; Vidyasagar & Pammer; Tallal; Reading Rockets.");

    private final Runnable onFinishQuiz;
    private final Runnable onBackToDashboard;

    public DyslexiaQuizPage(Runnable onFinishQuiz, Runnable onBackToDashboard, Runnable onHelp) {
        this.onFinishQuiz = onFinishQuiz;
        this.onBackToDashboard = onBackToDashboard;

        answers.addAll(Collections.nCopies(QUESTIONS.size(), null));
        setStyle("-fx-background-color: #fffbe6;");
        Header header = new Header(onHelp);

        timerLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        timerLabel.setTextFill(Color.web("#222"));
        timerLabel.setPadding(new Insets(0, 16, 0, 0));

        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(38, 38, 32, 38));
        card.setStyle("-fx-background-color: #fff; -fx-background-radius: 32; -fx-border-radius: 32; -fx-border-color: #b08d23; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, #d1d5db, 16, 0.10, 0, 6);");

        footer.setFont(Font.font("Outfit", FontWeight.BOLD, 13));
        footer.setTextFill(Color.web("#6b7280"));
        footer.setPadding(new Insets(24, 0, 0, 0));
        footer.setWrapText(true);
        footer.setMaxWidth(900);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long sec = (System.currentTimeMillis() - startTime) / 1000;
                elapsedSeconds = (int) sec;
                timerLabel.setText(String.format("%02d:%02d", elapsedSeconds/60, elapsedSeconds%60));
            }
        };
        startTime = System.currentTimeMillis();
        timer.start();

        updateUI();

        VBox rootCardWrap = new VBox(card);
        rootCardWrap.setAlignment(Pos.TOP_CENTER);
        rootCardWrap.setPadding(new Insets(94, 0, 40, 0));
        ScrollPane scrollPane = new ScrollPane(rootCardWrap);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(scrollPane, header);
        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setAlignment(scrollPane, Pos.CENTER);
    }

    private void updateUI() {
        card.getChildren().clear();
        QuizQuestion question = QUESTIONS.get(current);

        // Type label and prompt
        Label typeLabel = new Label(question.typeLabel);
        typeLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 14));
        typeLabel.setTextFill(Color.web("#2563eb"));
        typeLabel.setPadding(new Insets(0, 0, 6, 0));
        Label promptLabel = new Label("Q" + (question.index+1) + " • " + question.prompt);
        promptLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 22));
        promptLabel.setTextFill(Color.web("#222"));
        promptLabel.setWrapText(true);

        questionBlock.getChildren().setAll(typeLabel, promptLabel);

        // Progress
        Label qNumLabel = new Label((current+1) + " / " + QUESTIONS.size());
        qNumLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        qNumLabel.setTextFill(Color.web("#6b7280"));

        Label timerLabelCopy = new Label(timerLabel.getText());
        timerLabelCopy.setFont(Font.font("Outfit", FontWeight.BOLD, 16));
        timerLabelCopy.setTextFill(Color.web("#2563eb"));

        double progressPct = (answers.stream().filter(a -> a != null).count() * 1.0 / QUESTIONS.size());
        progressBar.setProgress(progressPct);
        progressBar.setPrefWidth(270);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #2563eb; -fx-background-radius: 7; -fx-border-radius: 7;");

        progressRow.getChildren().setAll(qNumLabel, timerLabelCopy, progressBar);

        // Main content: image or audio
        mainArea.getChildren().clear();
        if ("image".equals(question.mediaType) && question.imageSrc != null) {
            ImageView img = new ImageView(new Image(getClass().getResourceAsStream(question.imageSrc)));
            img.setFitHeight(200);
            img.setPreserveRatio(true);
            VBox imgBox = new VBox(img);
            imgBox.setAlignment(Pos.CENTER);
            imgBox.setPadding(new Insets(8));
            imgBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 22; -fx-border-radius: 22; -fx-border-color: #ece5d1; -fx-border-width: 1;");
            mainArea.getChildren().add(imgBox);
        } else if ("audio".equals(question.mediaType) && question.audioSrc != null) {
            VBox audioBox = new VBox(10);
            audioBox.setAlignment(Pos.CENTER);
            audioBox.setPadding(new Insets(12));
            audioBox.setStyle("-fx-background-color: #fff; -fx-background-radius: 22; -fx-border-radius: 22; -fx-border-color: #ece5d1; -fx-border-width: 1;");
            Button playBtn = new Button("▶ Play Audio");
            playBtn.setStyle("-fx-font-size:15px;-fx-background-color:#e0e7ff;-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-text-fill:#2563eb;");
            playBtn.setDisable(true);

            try {
                String filename = question.audioSrc.substring(question.audioSrc.lastIndexOf('/') + 1);
                String projectRoot = System.getProperty("user.dir");
                File audioFile = new File(projectRoot + File.separator + "out" + File.separator + "images" + File.separator + "quiz" + File.separator + filename);
                if (audioFile.exists()) {
                    Media media = new Media(audioFile.toURI().toString());
                    if (currentAudioPlayer != null) {
                        currentAudioPlayer.stop();
                        currentAudioPlayer.dispose();
                        currentAudioPlayer = null;
                    }
                    currentAudioPlayer = new MediaPlayer(media);
                    playBtn.setDisable(false);
                    playBtn.setOnAction(ev -> {
                        currentAudioPlayer.stop();
                        currentAudioPlayer.play();
                    });
                } else {
                    playBtn.setText("Audio unavailable");
                }
            } catch (Exception ex) {
                playBtn.setText("Audio error");
            }
            audioBox.getChildren().add(playBtn);
            Label audioPrompt = new Label("Play the audio and answer.");
            audioPrompt.setFont(Font.font("Outfit", FontWeight.NORMAL, 12));
            audioPrompt.setTextFill(Color.web("#222"));
            audioBox.getChildren().add(audioPrompt);
            mainArea.getChildren().add(audioBox);
        }

        // Option buttons: grid, 3 columns, CENTERED
        optionsRow.getChildren().clear();
        optionsRow.setAlignment(Pos.CENTER);
        for (QuizOption opt : question.options) {
            boolean selected = answers.get(current) != null && answers.get(current) == opt.value;
            Button optBtn = new Button();
            optBtn.setPrefHeight(68);
            optBtn.setPrefWidth(230);
            optBtn.setStyle(
                "-fx-background-radius: 14; " +
                (selected ? "-fx-background-color:#2563eb; -fx-text-fill:#fff; -fx-border-color:#2563eb; -fx-font-weight:bold;"
                          : "-fx-background-color:#e0e7ff; -fx-text-fill:#222; -fx-border-color:#d1d5db;") +
                "-fx-font-size:17px;-fx-font-family:'Outfit';"
            );
            VBox v = new VBox(2);
            Label codeLabel = new Label(opt.code);
            codeLabel.setFont(Font.font("Outfit", FontWeight.BOLD, 14));
            codeLabel.setTextFill(selected ? Color.WHITE : Color.web("#2563eb"));
            Label label = new Label(opt.label);
            label.setFont(Font.font("Outfit", FontWeight.NORMAL, 18));
            label.setTextFill(selected ? Color.WHITE : Color.web("#222"));
            v.getChildren().addAll(codeLabel, label);
            v.setAlignment(Pos.CENTER);
            optBtn.setGraphic(v);

            optBtn.setOnAction(ev -> {
                answers.set(current, opt.value);
                if (current < QUESTIONS.size() - 1) {
                    current++;
                    updateUI();
                } else {
                    timer.stop();
                    if (currentAudioPlayer != null) {
                        currentAudioPlayer.stop();
                        currentAudioPlayer.dispose();
                    }
                    predictAndShowResults();
                }
            });
            optionsRow.getChildren().add(optBtn);
        }

        // Navigation row: only add previous button and nav message for final slide
        navRow.getChildren().clear();
        Button prevBtn = new Button("Previous");
        prevBtn.setDisable(current == 0);
        prevBtn.setFont(Font.font("Outfit", FontWeight.NORMAL, 14));
        prevBtn.setStyle("-fx-border-radius:8;-fx-background-radius:8;-fx-background-color:#fff;-fx-border-color:#d1d5db;-fx-padding:6 18 6 18;-fx-text-fill:#2563eb;");
        prevBtn.setOnAction(e -> {
            if (current > 0) {
                current--;
                updateUI();
            }
        });

        Label navMsg = new Label(current == QUESTIONS.size()-1
                ? "You have finished the quiz. Redirecting to results…"
                : "");
        navMsg.setFont(Font.font("Outfit", FontWeight.NORMAL, 13));
        navMsg.setTextFill(Color.web("#6b7280"));

        navRow.getChildren().addAll(prevBtn, navMsg);
        navRow.setSpacing(18);

        card.getChildren().setAll(
            timerLabel,
            questionBlock,
            progressRow,
            mainArea,
            optionsRow,
            navRow,
            new Separator(),
            footer
        );
    }

    private void predictAndShowResults() {
        // Show loading dialog/modal (optional)
        Dialog<Void> loadingDialog = new Dialog<>();
        loadingDialog.setTitle("Predicting Results...");
        loadingDialog.setHeaderText("Analyzing your responses...");
        loadingDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        loadingDialog.setOnCloseRequest(e -> loadingDialog.hide());
        loadingDialog.show();

        new Thread(() -> {
            try {
                // Prepare payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("answers", answers);
                payload.put("time", elapsedSeconds);

                String json = toJson(payload);
                URL url = new URL("http://localhost:8080/api/predict-dyslexia"); // Spring Boot default port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();
                String response = readStream(is);

                // Parse JSON (simple parsing)
                Map<String, Object> result = parseJson(response);
                int label = result.containsKey("label") ? ((Number) result.get("label")).intValue() : 1;
                List<Double> features = result.containsKey("features") ? (List<Double>) result.get("features") : Arrays.asList(0d,0d,0d,0d,0d,0d);
                String debugLog = result.containsKey("debug_log") ? (String) result.get("debug_log") : "";

                Platform.runLater(() -> {
                    loadingDialog.hide();
                    DyslexiaResultsPage.setLatestResult(label, features, debugLog);
                    if (onFinishQuiz != null) onFinishQuiz.run();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    loadingDialog.hide();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Prediction failed: " + ex.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                });
            }
        }).start();
    }

    // Helper: Convert Map to JSON string
    private String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (i++ > 0) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":");
            if (e.getValue() instanceof List) {
                sb.append("[");
                List<?> lst = (List<?>)e.getValue();
                for (int j = 0; j < lst.size(); j++) {
                    Object v = lst.get(j);
                    sb.append(v instanceof Number ? v : "\"" + v + "\"");
                    if (j < lst.size()-1) sb.append(",");
                }
                sb.append("]");
            } else {
                sb.append(e.getValue() instanceof Number ? e.getValue() : "\"" + e.getValue() + "\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // Helper: Read InputStream to String
    private String readStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // Simple JSON parser for {"label":...,"features":[...],"debug_log":"..."}
    private Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (json.contains("\"label\"")) {
                int idx = json.indexOf("\"label\"");
                int colon = json.indexOf(":", idx) + 1;
                int comma = json.indexOf(",", colon);
                int end = comma > 0 ? comma : json.indexOf("}", colon);
                result.put("label", Integer.parseInt(json.substring(colon, end).trim()));
            }
            if (json.contains("\"features\"")) {
                int idx = json.indexOf("\"features\"");
                int lb = json.indexOf("[", idx);
                int rb = json.indexOf("]", lb);
                String arrStr = json.substring(lb+1, rb);
                String[] vals = arrStr.split(",");
                List<Double> features = new ArrayList<>();
                for (String v : vals) features.add(Double.parseDouble(v.trim()));
                result.put("features", features);
            }
            if (json.contains("\"debug_log\"")) {
                int idx = json.indexOf("\"debug_log\"");
                int colon = json.indexOf(":", idx) + 1;
                int quote1 = json.indexOf("\"", colon);
                int quote2 = json.indexOf("\"", quote1+1);
                result.put("debug_log", json.substring(quote1+1, quote2));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return result;
    }
}