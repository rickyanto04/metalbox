package com.ricky.metalbox.model.Land;

import com.ricky.metalbox.model.Cell.Cell;
import com.ricky.metalbox.model.Cell.CellImpl;
import com.ricky.metalbox.model.Cell.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

public class LandImpl extends AbstractLand {

    private static final int landSize = 250;
    private final Cell[][] grid = new CellImpl[landSize][landSize];

    public LandImpl() {
        for (int i = 0; i < landSize; i++) {
            for (int j = 0; j < landSize; j++) {

                if (j<100 && j>50 && i<100 && i>50) {
                    this.grid[j][i] = new CellImpl(new Position(j, i), TerrainType.WATER);
                    continue;
                }
                this.grid[j][i] = new CellImpl(new Position(j, i), TerrainType.GRASS);
            }
        }
    }

    @Override
    public boolean isCellFree(final Position p) {
        return isPositionValid(p) && !this.grid[p.getY()][p.getX()].isOccupied();
    }

    // Questa è l'implementazione del metodo richiesto da AbstractLand
    @Override
    protected void setCellOccupied(final Position p, final boolean occupied) {
        this.grid[p.getY()][p.getX()].setOccupied(occupied);
    }

    private boolean isPositionValid(final Position p) {
        return p.getX() >= 0 && p.getX() < landSize && p.getY() >= 0 && p.getY() < landSize;
    }
}
