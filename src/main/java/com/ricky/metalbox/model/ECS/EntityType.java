package com.ricky.metalbox.model.ECS;

import java.io.InputStream;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public enum EntityType {
    // definizione di ogni tipo di entità
    HUMAN("human.png");

    private final String resourceName;
    private Color averageColor; //per LOD intermedio
    private int argb; //per pixelwriter LOD
    private Image sprite;

    EntityType(final String resourceName) {
        this.resourceName = resourceName;
        loadResource();
    }

    private void loadResource() {
        try {
            InputStream is = getClass().getResourceAsStream("/" + resourceName);
            if (is == null) {
                throw new RuntimeException("Resource not found: " + resourceName);
            }
            this.sprite = new Image(is);

            PixelReader pr = this.sprite.getPixelReader();
            this.averageColor = pr.getColor(2, 2); // colore del centro per il LOD
            this.argb = (255 << 24) |
                        ((int)(averageColor.getRed() * 255) << 16) |
                        ((int)(averageColor.getGreen() * 255) << 8) |
                        ((int)(averageColor.getBlue() * 255));

        } catch (final Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load sprite for " + this.name());
        }
    }

    public Image getSprite() { return this.sprite; }
    public Color getColor() { return this.averageColor; } //fallback
    public int getArgb() { return this.argb; }
}
