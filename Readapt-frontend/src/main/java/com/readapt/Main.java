package com.readapt;

import com.readapt.ui.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Readapt – Assistive Reading");
        showModesPage();
        primaryStage.show();
    }

    // New: Central help page navigation
    public void showHelpPage() {
        HelpPage helpPage = new HelpPage(this::goBackFromHelp);
        Scene scene = new Scene(helpPage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/modes-effects.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    // Simple heuristic: go back to ModesPage (or keep stack for advanced back)
    private void goBackFromHelp() {
        showModesPage();
    }

    public void showModesPage() {
        ModesPage modesPage = new ModesPage(this::showDyslexiaDashboard, this::showHelpPage);
        Scene scene = new Scene(modesPage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/modes-effects.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public void showDyslexiaDashboard() {
        DyslexiaDashboard dashboard = new DyslexiaDashboard(this::showModesPage, this::showDyslexiaQuiz, this::showHelpPage);
        Scene scene = new Scene(dashboard, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-dashboard.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public void showDyslexiaQuiz() {
        DyslexiaQuizPage quizPage = new DyslexiaQuizPage(
            this::showDyslexiaResults,
            this::showDyslexiaDashboard,
            this::showHelpPage
        );
        Scene scene = new Scene(quizPage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-quiz.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public void showDyslexiaResults() {
        DyslexiaResultsPage resultsPage = new DyslexiaResultsPage(
            this::showDyslexiaQuiz,
            this::showDyslexiaDashboard,
            this::showPastePage,
            this::showHelpPage
        );
        Scene scene = new Scene(resultsPage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-dashboard.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-quiz.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public void showPastePage() {
        DyslexiaPastePage pastePage = new DyslexiaPastePage(
            this::showDyslexiaAdapt,
            this::showDyslexiaQuiz,
            this::showDyslexiaResults,
            this::showHelpPage
        );
        Scene scene = new Scene(pastePage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-dashboard.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-quiz.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public void showDyslexiaAdapt() {
        DyslexiaAdaptPage adaptPage = new DyslexiaAdaptPage(
            this::showDyslexiaDashboard,
            this::showPastePage,
            this::showDyslexiaResults,
            this::showHelpPage
        );
        Scene scene = new Scene(adaptPage, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/globals.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-dashboard.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/dyslexia-quiz.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(false);
        primaryStage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}