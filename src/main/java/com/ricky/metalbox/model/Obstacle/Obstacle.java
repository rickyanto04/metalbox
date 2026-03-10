package com.ricky.metalbox.model.Obstacle;

import java.util.List;

import com.ricky.metalbox.model.Utilities.Position;

public interface Obstacle {
    Position getAnchorPosition();

    List<Position> getOccupiedPositions();
}
