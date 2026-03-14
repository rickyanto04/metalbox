package com.ricky.metalbox.model.Land;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ricky.metalbox.model.Cell.Cell;
import com.ricky.metalbox.model.Cell.CellImpl;
import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Utilities.Position;
import com.ricky.metalbox.model.Obstacle.Obstacle;

public class LandImpl implements Land{
    private static final int landSize = 250;
    private Cell[][] grid = new CellImpl[landSize][landSize];
    private List<Entity> entities = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();

    public LandImpl() {
        for (int i = 0; i < landSize; i++) {
            for (int j = 0; j < landSize; j++) {
                this.grid[j][i] = new CellImpl(new Position(j, i));
            }
        }
    }

    @Override
    public boolean addEntity(final Entity e) {
        for(Position p : e.getOccupiedPositions()) {
            if (!isPositionValid(p)) {
                return false;
            }
        }

        setEntityOccupation(e, true);
        this.entities.add(e);
        return true;
    }

    @Override
    public boolean moveEntity(final Entity e, final Position newAnchorPos) {

        Position oldAnchorPos = e.getAnchorPosition(); // salvo vecchia anchor
        setEntityOccupation(e, false); // libero le celle della griglia

        e.setAnchorPosition(newAnchorPos); // fisso la nuova anchor
        for(Position p : e.getOccupiedPositions()) { // controllo se la nuova posizione andrebbe bene
            if (!isPositionValid(p)) {
                // se c'è anche solo un ostacolo allora si ritorna a prima e ritorna false
                e.setAnchorPosition(oldAnchorPos);
                setEntityOccupation(e, true);
                return false;
            }
        }

        setEntityOccupation(e, true);
        return true;
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(this.entities);
    }

    @Override
    public boolean addObstacle(final Obstacle o) {
        for(Position p : o.getOccupiedPositions()) {
            if (!isPositionValid(p)) {
                return false;
            }
        }

        this.obstacles.add(o);
        return true;
    }

    //helper 1
    private boolean isPositionValid(final Position p) {
        return p.getX() >= 0 && p.getX() < landSize && p.getY() >= 0 && p.getY() < landSize;
    }

    //helper 2
    private void setEntityOccupation(final Entity e, final boolean value) {
        for(Position p : e.getOccupiedPositions()) {
            this.grid[p.getY()][p.getX()].setOccupied(value);
        }
    }

    @Override
    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(this.obstacles);
    }

}
