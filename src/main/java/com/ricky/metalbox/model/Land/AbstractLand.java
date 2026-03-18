package com.ricky.metalbox.model.Land;

import java.util.List;
import java.util.ArrayList;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Utilities.Position;

// creata per poter cambiare la generazione della mappa in futuro ovvero poter avere mappe infinite, esagonali, ecc...
public abstract class AbstractLand implements Land {

    //gestione entità con ECS
    protected final EntityManager entityManager = new EntityManager();

    //variabili per spatial partitioning tramite id entità
    private List<List<Integer>> spatialChunks;
    private final int CHUNK_SIZE = 32; // ogni partizione sarà 32x32 celle
    private int chunksPerRow;

    @Override public EntityManager getEntityManager() { return this.entityManager; }

    // ---> METODI ASTRATTI che la classe matrice (LandImpl) dovrà definire
    protected abstract void setCellOccupied(Position p, boolean occupied);

    // Essendo l'interfaccia Land a richiedere isCellFree, qui la implementerà il figlio,
    // ma noi possiamo usarla tranquillamente nei metodi sottostanti!

    //lazy initialization quando serve per la prima volta
    private void initSpatialGridIfNeeded() {
        if (this.spatialChunks == null) {
            //calcolo di quante partizioni ci stanno in una riga (es: 1000/32 = 31.25)
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

    // aggiunta di entità al database
    public void registerEntity(final int entityId) {
        initSpatialGridIfNeeded();
        Position pos = new Position(entityManager.positionComponents[entityId].x, entityManager.positionComponents[entityId].y);

        for(Position relative : entityManager.shapeComponents[entityId].relativePositions) {
            setCellOccupied(new Position(pos.getX() + relative.getX(), pos.getY() + relative.getY()), true);
        }

        int chunkIdx = getChunkIndex(pos);
        this.spatialChunks.get(chunkIdx).add(entityId);
    }

    @Override
    public boolean moveEntity(final int entityId, final Position newPos) {
        initSpatialGridIfNeeded();
        Position oldPos = new Position(
            entityManager.positionComponents[entityId].x,
            entityManager.positionComponents[entityId].y
        );

        // libera old positions
        for(Position relative : entityManager.shapeComponents[entityId].relativePositions) {
            setCellOccupied(new Position(oldPos.getX() + relative.getX(), oldPos.getY() + relative.getY()), false);
        }

        // controllo se nuova pos è libera
        boolean canMove = true;
        for(Position relative : entityManager.shapeComponents[entityId].relativePositions) {
            if (!isCellFree(new Position(newPos.getX() + relative.getX(), newPos.getY() + relative.getY()))) {
                canMove = false;
                break;
            }
        }

        // rollback per urto contro muro
        if (!canMove) {
            for(Position relative : entityManager.shapeComponents[entityId].relativePositions) {
                setCellOccupied(new Position(oldPos.getX() + relative.getX(), oldPos.getY() + relative.getY()), true);
            }
            return false;
        }

        // occupa nuove posizioni e aggiorna dati
        for(Position relative : entityManager.shapeComponents[entityId].relativePositions) {
            setCellOccupied(new Position(newPos.getX() + relative.getX(), newPos.getY() + relative.getY()), true);
        }

        entityManager.positionComponents[entityId].x = newPos.getX();
        entityManager.positionComponents[entityId].y = newPos.getY();

        // aggiornamento spatial grid se l'entità cambia chunk
        int oldChunkIdx = getChunkIndex(oldPos);
        int newChunkIdx = getChunkIndex(newPos);
        if (oldChunkIdx != newChunkIdx) {
            this.spatialChunks.get(oldChunkIdx).remove(Integer.valueOf(entityId));
            this.spatialChunks.get(newChunkIdx).add(entityId);
        }
        return true;
    }

    //ricerca entità vicine
    @Override
    public List<Integer> getEntitiesNear(final Position center, final int radius) {
        initSpatialGridIfNeeded();
        List<Integer> nearbyEntities = new ArrayList<>();

        //angoli chunk
        int startCX = Math.max(0, (center.getX() - radius) / CHUNK_SIZE);
        int endCX = Math.min(chunksPerRow - 1, (center.getX() + radius) / CHUNK_SIZE);
        int startCY = Math.max(0, (center.getY() - radius) / CHUNK_SIZE);
        int endCY = Math.min(chunksPerRow - 1, (center.getY() + radius) / CHUNK_SIZE);

        for (int cy = startCY; cy <= endCY; cy++) {
            for (int cx = startCX; cx <= endCX; cx++) {
                int chunkIdx = (cy * chunksPerRow) + cx;
                nearbyEntities.addAll(this.spatialChunks.get(chunkIdx));
            }
        }
        return nearbyEntities;
    }
}
