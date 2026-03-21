package com.ricky.metalbox.model.ECS;

import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;
import javafx.scene.paint.Color;

public enum EntityType {
    // definizione di ogni tipo di entità
    HUMAN(Color.BLACK, List.of(
        new Position(0, 0), new Position(2, 0),
        new Position(1, 1), new Position(1, 2)
    ));

    private final Color color;
    private final List<Position> shape;
    private final int argb;

    EntityType(Color color, List<Position> shape) {
        this.color = color;
        this.shape = shape;

        // calcoliamo dell'intero a 32-bit (Alpha, Red, Green, Blue)
        // per iniezioni dirette di pixel
        this.argb = (255 << 24) |
                    ((int)(color.getRed() * 255) << 16) |
                    ((int)(color.getGreen() * 255) << 8) |
                    ((int)(color.getBlue() * 255));
    }

    public Color getColor() { return this.color; }
    public List<Position> getShape() { return this.shape; }
    public int getArgb() { return this.argb; }
}
