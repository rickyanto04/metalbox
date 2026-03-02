package com.ricky.metalbox.model;

import java.util.List;

public interface Entity {
    boolean isAlive();

    void setAnchorPosition(Position p);

    Position getAnchorPosition();

    List<Position> getOccupiedPositions();
}
