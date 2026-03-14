package com.ricky.metalbox.model.Obstacle;

import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;

public class Rock extends AbstractObstacle {

    // forma della roccia
    private static final List<Position> SHAPE = List.of(
        new Position(0, 0), new Position(1, 0), new Position(2, 0),
        new Position(0, 1), new Position(1, 1), new Position(2, 1),
        new Position(0, 2), new Position(1, 2), new Position(2, 2)
    );

    public Rock(final Position anchorPosition) {
        super(anchorPosition);
    }

    // passiamo anche qui la forma alla classe madre
    @Override
    protected List<Position> getShape() {
        return SHAPE;
    }
}
