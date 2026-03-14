package com.ricky.metalbox.model.Obstacle;

import java.util.ArrayList;
import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;

public abstract class AbstractObstacle implements Obstacle {

    private final Position anchorPosition;

    public AbstractObstacle(final Position anchorPosition) {
        this.anchorPosition = anchorPosition;
    }

    @Override
    public Position getAnchorPosition() {
        return this.anchorPosition;
    }

    // (template method) ottenere shape dalla classe figlia
    protected abstract List<Position> getShape();

    // logica spaziale centralizzata come con le entità
    @Override
    public List<Position> getOccupiedPositions() {
        List<Position> occupiedPositions = new ArrayList<>();
        for (Position p : getShape()) {
            occupiedPositions.add(new Position(
                this.anchorPosition.getX() + p.getX(),
                this.anchorPosition.getY() + p.getY()
            ));
        }
        return occupiedPositions;
    }
}
