package com.ricky.metalbox.model.Cell;

import com.ricky.metalbox.model.Utilities.Position;

public class CellImpl implements Cell{

    private final Position position;
    private final TerrainType terrainType;
    private boolean occupied;

    public CellImpl(final Position p, final TerrainType terrainType) {
        this.position = p;
        this.terrainType = terrainType;
    }

    @Override
    public boolean isOccupied() {
        return this.occupied;
    }

    @Override
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public TerrainType getTerrainType() {
        return this.terrainType;
    }
}
