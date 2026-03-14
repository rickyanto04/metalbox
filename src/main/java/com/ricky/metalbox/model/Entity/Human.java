package com.ricky.metalbox.model.Entity;

import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;

public class Human extends AbstractEntity {

    // forma dell'umano
    private static final List<Position> SHAPE = List.of(
        new Position(0, 0), new Position(2, 0),
        new Position(1, 1), new Position(1, 2)
    );

    public Human(final Position birthPosition) {
        super(birthPosition);
    }

    // passiamo la forma alla classe madre
    @Override
    protected List<Position> getShape() {
        return SHAPE;
    }
}
