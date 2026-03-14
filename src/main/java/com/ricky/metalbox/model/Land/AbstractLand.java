package com.ricky.metalbox.model.Land;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Utilities.Position;

// creata per poter cambiare la generazione della mappa in futuro ovvero poter avere mappe infinite, esagonali, ecc...
public abstract class AbstractLand implements Land {

    // Gestione centralizzata delle liste di elementi nel mondo
    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Obstacle> obstacles = new ArrayList<>();

    @Override public List<Entity> getEntities() { return Collections.unmodifiableList(this.entities); }
    @Override public List<Obstacle> getObstacles() { return Collections.unmodifiableList(this.obstacles); }

    // ---> METODI ASTRATTI che la classe matrice (LandImpl) dovrà definire
    protected abstract void setCellOccupied(Position p, boolean occupied);

    // Essendo l'interfaccia Land a richiedere isCellFree, qui la implementerà il figlio,
    // ma noi possiamo usarla tranquillamente nei metodi sottostanti!

    // ---> METODI GESTIONALI SPOSTATI DALL'IMPL ALLA CLASSE ASTRATTA
    @Override
    public boolean addEntity(final Entity e) {
        for(Position p : e.getOccupiedPositions()) {
            if (!isCellFree(p)) return false;
        }
        for(Position p : e.getOccupiedPositions()) {
            setCellOccupied(p, true);
        }
        this.entities.add(e);
        return true;
    }

    @Override
    public boolean moveEntity(final Entity e, final Position newAnchorPos) {
        Position oldAnchorPos = e.getAnchorPosition();
        for(Position p : e.getOccupiedPositions()) setCellOccupied(p, false);

        e.setAnchorPosition(newAnchorPos);
        for(Position p : e.getOccupiedPositions()) {
            if (!isCellFree(p)) {
                e.setAnchorPosition(oldAnchorPos);
                // Rollback in caso di fallimento
                for(Position oldP : e.getOccupiedPositions()) setCellOccupied(oldP, true);
                return false;
            }
        }

        for(Position p : e.getOccupiedPositions()) setCellOccupied(p, true);
        return true;
    }

    @Override
    public boolean addObstacle(final Obstacle o) {
        for(Position p : o.getOccupiedPositions()) {
            if (!isCellFree(p)) return false;
        }
        for(Position p : o.getOccupiedPositions()) {
            setCellOccupied(p, true);
        }
        this.obstacles.add(o);
        return true;
    }
}
