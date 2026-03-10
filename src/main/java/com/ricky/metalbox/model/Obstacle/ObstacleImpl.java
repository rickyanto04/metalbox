package com.ricky.metalbox.model.Obstacle;

import java.util.ArrayList;
import java.util.List;

import com.ricky.metalbox.model.Utilities.Position;

public class ObstacleImpl implements Obstacle{

    private final Position anchorPosition;
    private final List<Position> shape = List.of(
        new Position(0, 0), new Position(1, 0),
        new Position(0, 1), new Position(1, 1)
    );

    public ObstacleImpl(final Position anchorPosition) {
        this.anchorPosition = anchorPosition;
    }

    @Override
    public Position getAnchorPosition() {
        return this.anchorPosition;
    }

    @Override
    public List<Position> getOccupiedPositions() {
        List<Position> occupiedPositions = new ArrayList<>();

        for (Position p : this.shape) {
            occupiedPositions.add(new Position(
            this.anchorPosition.getX() + p.getX(),
            this.anchorPosition.getX() + p.getX()
            ));
        }
        return occupiedPositions;
    }

}
