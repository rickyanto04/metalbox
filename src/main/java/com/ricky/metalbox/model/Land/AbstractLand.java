package com.ricky.metalbox.model.Land;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Utilities.Position;

// creata per poter cambiare la generazione della mappa in futuro ovvero poter avere mappe infinite, esagonali, ecc...
public abstract class AbstractLand implements Land {

    // Gestione centralizzata delle liste di elementi nel mondo
    protected final List<Entity> entities = new ArrayList<>();
    protected final List<Obstacle> obstacles = new ArrayList<>();

    private List<List<Entity>> spatialChunks;
    private final int CHUNK_SIZE = 10; // ogni partizione sarà 10x10 celle
    private int chunksPerRow;

    @Override public List<Entity> getEntities() { return Collections.unmodifiableList(this.entities); }
    @Override public List<Obstacle> getObstacles() { return Collections.unmodifiableList(this.obstacles); }

    // ---> METODI ASTRATTI che la classe matrice (LandImpl) dovrà definire
    protected abstract void setCellOccupied(Position p, boolean occupied);

    // Essendo l'interfaccia Land a richiedere isCellFree, qui la implementerà il figlio,
    // ma noi possiamo usarla tranquillamente nei metodi sottostanti!

    //lazy initialization quando serve per la prima volta
    private void initSpatialGridIfNeeded() {
        if (this.spatialChunks == null) {
            //calcolo di quante partizioni ci stanno in una riga (250/10 = 25)
            this.chunksPerRow = (int) Math.ceil((double) getSize() / CHUNK_SIZE);
            int totalChunks = this.chunksPerRow * this.chunksPerRow;

            this.spatialChunks = new ArrayList<>(totalChunks);
            for (int i = 0; i < totalChunks; i++) {
                this.spatialChunks.add(new ArrayList<>());
            }
        }
    }

    //traduzione di una coordinata 2d in index dell'array di partizioni
    private int getChunkIndex(final Position p) {
        int cx = Math.max(0, Math.min(p.getX() / CHUNK_SIZE, chunksPerRow - 1));
        int cy = Math.max(0, Math.min(p.getY() / CHUNK_SIZE, chunksPerRow - 1));
        return (cy * chunksPerRow) + cx;
    }

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

        //aggiunta dell'entità alla partizione corretta
        int chunkIdx = getChunkIndex(e.getAnchorPosition());
        this.spatialChunks.get(chunkIdx).add(e);
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

        // aggiornamento dell'appartenenza di un entità
        // ad una partizione nel caso dell'attraversamento di un confine
        int oldChunkIdx = getChunkIndex(oldAnchorPos);
        int newChunkIdx = getChunkIndex(newAnchorPos);
        if (oldChunkIdx != newChunkIdx) {
            this.spatialChunks.get(oldChunkIdx).remove(e);
            this.spatialChunks.get(newChunkIdx).add(e);
        }

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

    //ricerca veloce delle entità vicine
    @Override
    public List<Entity> getEntitiesNear(final Position center, final int radius) {
        initSpatialGridIfNeeded();
        List<Entity> nearbyEntities = new ArrayList<>();

        //coordinate dei settori che compongono il quadrato attorno a noi
        int startCX = Math.max(0, (center.getX() - radius) / CHUNK_SIZE);
        int endCX = Math.min(chunksPerRow - 1, (center.getX() + radius) / CHUNK_SIZE);
        int startCY = Math.max(0, (center.getY() - radius) / CHUNK_SIZE);
        int endCY = Math.min(chunksPerRow - 1, (center.getY() + radius) / CHUNK_SIZE);

        // raccogliamo i contenuti solo dei settori adiacenti rilevanti
        for (int cy = startCY; cy <= endCY; cy++) {
            for (int cx = startCX; cx <= endCX; cx++) {
                int chunkIdx = (cy * chunksPerRow) + cx;
                nearbyEntities.addAll(this.spatialChunks.get(chunkIdx));
            }
        }
        return nearbyEntities;
    }
}
