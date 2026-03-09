package com.ricky.metalbox.model;

public interface Cell {

    boolean isOccupied();

    void setOccupied(boolean occupied);

    Position getPosition();
}
