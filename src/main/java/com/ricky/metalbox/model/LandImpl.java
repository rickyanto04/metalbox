package com.ricky.metalbox.model;

import java.util.ArrayList;
import java.util.List;

public class LandImpl implements Land{
    private static final int landSize = 250;
    private final Cell[][] grid = new CellImpl[landSize][landSize];

    public LandImpl() {
        // qui cosa metto? al momento non serve il costruttore
    }

    @Override
    public List<Cell> getOccupiedCells() {
        List<Cell> cellsList = new ArrayList<>();
        for (int i = 0; i < landSize; i++) {
            for (int j = 0; j < landSize; j++) {
                if (this.grid[i][j].isOccupied()) {
                    cellsList.add(c);
                }
            }
        }
        return cellsList;
    }
}
