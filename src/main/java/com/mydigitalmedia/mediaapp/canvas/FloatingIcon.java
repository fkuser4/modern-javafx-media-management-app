package com.mydigitalmedia.mediaapp.canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class FloatingIcon {

    private double x, y;
    private double fallSpeed;
    private double driftSpeed;
    private final Random rand = new Random();
    public static final Map<Integer, String> IconPaths = new HashMap<>();
    private Image image;

    static {
        IconPaths.put(0, "/com/mydigitalmedia/mediaapp/images/twitterx.png");
        IconPaths.put(1, "/com/mydigitalmedia/mediaapp/images/facebook.png");
        IconPaths.put(2, "/com/mydigitalmedia/mediaapp/images/instagram.png");
        IconPaths.put(3, "/com/mydigitalmedia/mediaapp/images/youtube.png");
        IconPaths.put(4, "/com/mydigitalmedia/mediaapp/images/tiktok.png");
    }

    public FloatingIcon(double x, double y) {
        this.x = x;
        this.y = y;
        this.fallSpeed = rand.nextDouble() + 0.3;
        this.driftSpeed = rand.nextDouble() * 0.5 - 0.25;
        this.image = new Image(Objects.requireNonNull(FloatingIcon.class
                .getResourceAsStream(IconPaths.get(rand.nextInt(IconPaths.size())))));
    }

    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(true);
        gc.save();
        gc.translate(x, y);
        gc.drawImage(image, 0, 0);
        gc.restore();
    }

    public void update() {
        y += fallSpeed;
        x += driftSpeed;

    }

    public boolean isOffScreen(int canvasWidth, int canvasHeight) {
        if (x <= 1 || x > canvasWidth - 45) {

            driftSpeed = -driftSpeed;
        }

        return y > canvasHeight;
    }

}
