package com.ricky.metalbox.model.Land;

import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

public class LandImpl extends AbstractLand {

    private static final int landSize = 2000;

    private final byte[] terrainGrid;
    private final boolean[] occupiedGrid;

    public LandImpl() {
        int totalCells = landSize * landSize;
        this.terrainGrid = new byte[totalCells];
        this.occupiedGrid = new boolean[totalCells];

        for (int i = 0; i < landSize; i++) { // = x
            for (int j = 0; j < landSize; j++) { // = y
                TerrainType type = TerrainType.GRASS;

                if (j<100 && j>50 && i<100 && i>50) {
                    type = TerrainType.WATER;
                }

                // numero da 0 a 3 dell'enum dei terreni invece dell'oggetto Enum
                this.terrainGrid[getIndex(j, i)] = (byte) type.ordinal();
            }
        }
    }

    //helper per mappare le coordinate 2d nell'array 1d
    private int getIndex(int y, int x) {
        return (y * landSize) + x;
    }

    /*
    @Override
    public boolean isCellFree(final Position p) {
        if (isPositionValid(p)) {
            int index = getIndex(p.getY(), p.getX());

            boolean isOccupied = this.occupiedGrid[index];
            //controlliamo col byte salvato per capire se è acqua
            boolean isWater = this.terrainGrid[index] == (byte) TerrainType.WATER.ordinal();

            return !isOccupied && !isWater;
        }
        return false;
    }
        */

    @Override
    public boolean isCellFree(int x, int y) {
        if (x >= 0 && x < landSize && y >= 0 && y < landSize) {
            int index = getIndex(y, x);
            return !this.occupiedGrid[index] && this.terrainGrid[index] != (byte) TerrainType.WATER.ordinal();
        }
        return false;
    }

    // Questa è l'implementazione del metodo richiesto da AbstractLand
    @Override
    protected void setCellOccupied(final Position p, final boolean occupied) {
        if (isPositionValid(p)) {
            int index = getIndex(p.getY(), p.getX());
            this.occupiedGrid[index] = occupied;
        }
    }

    private boolean isPositionValid(final Position p) {
        return p.getX() >= 0 && p.getX() < landSize && p.getY() >= 0 && p.getY() < landSize;
    }

    @Override
    public int getSize() {
        return landSize;
    }

    @Override
    public TerrainType getTerrainAt(Position p) {
        if (isPositionValid(p)) {
            int index = getIndex(p.getY(), p.getX());
            byte terrainID = this.terrainGrid[index];

            return TerrainType.values()[terrainID]; // conversione
        }
        return TerrainType.WATER; // Fallback di sicurezza
    }
}
