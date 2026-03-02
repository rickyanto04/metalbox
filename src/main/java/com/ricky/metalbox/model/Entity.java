package com.ricky.metalbox.model;

import java.util.List;

public interface Entity {
    boolean isAlive();

    Position getAnchorPosition();

    List<Position> getOccupiedPositions();
}
