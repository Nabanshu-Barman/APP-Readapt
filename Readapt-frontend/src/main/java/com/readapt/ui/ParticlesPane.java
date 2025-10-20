package com.readapt.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesPane extends Pane {
    private final Canvas canvas = new Canvas();
    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random();

    public ParticlesPane() {
        this.getChildren().add(canvas);
        this.setMouseTransparent(true);

        this.setStyle("-fx-background-color: transparent;");
        widthProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());
        heightProperty().addListener((obs, oldVal, newVal) -> resizeCanvas());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawParticles();
            }
        };
        timer.start();

        resizeCanvas();
    }

    private void resizeCanvas() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        initParticles();
    }

    private void initParticles() {
        particles.clear();
        double density = 0.00012;
        int count = (int) (getWidth() * getHeight() * density);
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(
                    rand.nextDouble() * getWidth(),
                    rand.nextDouble() * getHeight(),
                    rand.nextDouble() * 0.8 - 0.4, // FASTER movement
                    rand.nextDouble() * 0.8 - 0.4,
                    rand.nextDouble() * 12.0 + 7.5,
                    rand.nextDouble() * 0.34 + 0.23 // Higher opacity
            ));
        }
    }

    private void drawParticles() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Particle p : particles) {
            p.x += p.vx;
            p.y += p.vy;
            if (p.x < 0 || p.x > canvas.getWidth()) p.vx *= -1;
            if (p.y < 0 || p.y > canvas.getHeight()) p.vy *= -1;

            gc.setGlobalAlpha(p.a);
            gc.setFill(Color.web("#c79752", 1.0)); // rich gold/brown, visible
            gc.fillOval(p.x, p.y, p.r, p.r);

            gc.setGlobalAlpha(p.a * 0.6);
            gc.setFill(Color.web("#b07f34", 1.0)); // accent brown
            gc.fillRect(p.x + 2, p.y + 2, 2, 2);
        }
        gc.setGlobalAlpha(1.0);
    }

    private static class Particle {
        double x, y, vx, vy, r, a;
        Particle(double x, double y, double vx, double vy, double r, double a) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.r = r; this.a = a;
        }
    }
}