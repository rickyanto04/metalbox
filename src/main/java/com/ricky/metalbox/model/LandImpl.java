package com.ricky.metalbox.model;

public class LandImpl implements Land{
    private static final int landSize = 250;
    private final Cell[][] grid = new CellImpl[landSize][landSize];

    public LandImpl() {
        for (int i = 0; i < landSize; i++) {
            for (int j = 0; j < landSize; j++) {
                this.grid[j][i] = new CellImpl(new Position(j, i));
            }
        }
    }

    @Override
    public boolean addEntity(Entity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addEntity'");
    }

    @Override
    public boolean moveEntity(Entity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveEntity'");
    }

}
