package com.ricky.metalbox.model.Cell;

import com.ricky.metalbox.model.Utilities.Position;

public class CellImpl implements Cell{

    private final Position position;
    private boolean occupied;

    public CellImpl(final Position p) {
        this.position = p;
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
}
