package com.ricky.metalbox.model.ECS;

import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;
import javafx.scene.paint.Color;

public enum EntityType {
    // definizione di ogni tipo di entità
    HUMAN(Color.BLACK, List.of(
        new Position(0, 0), new Position(2, 0),
        new Position(1, 1), new Position(1, 2)
    )),

    ROCK(Color.DARKSLATEGRAY, List.of(
        new Position(0, 0), new Position(1, 0), new Position(2, 0),
        new Position(0, 1), new Position(1, 1), new Position(2, 1),
        new Position(0, 2), new Position(1, 2), new Position(2, 2)
    ));

    private final Color color;
    private final List<Position> shape;

    EntityType(Color color, List<Position> shape) {
        this.color = color;
        this.shape = shape;
    }

    public Color getColor() { return this.color; }
    public List<Position> getShape() { return this.shape; }
}
