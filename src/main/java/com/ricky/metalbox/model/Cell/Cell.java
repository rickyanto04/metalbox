package com.ricky.metalbox.model.Cell;

import com.ricky.metalbox.model.Utilities.Position;

public interface Cell {

    boolean isOccupied();

    void setOccupied(boolean occupied);

    Position getPosition();

    TerrainType getTerrainType();
}
