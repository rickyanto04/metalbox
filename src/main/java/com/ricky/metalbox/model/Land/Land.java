package com.ricky.metalbox.model.Land;

import java.util.List;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

public interface Land {
    // espone il database dell'ecs system
    EntityManager getEntityManager();

    //boolean moveEntity(int entityId, Position anchor);

    boolean moveEntity(int entityId, int newX, int newY);

    // metodo per spatial partitioning
    List<Integer> getEntitiesNear(Position center, int radius);

    //boolean isCellFree(Position p);

    boolean isCellFree(int x, int y);

    int getSize();

    TerrainType getTerrainAt(Position p);

    void setTerrainAt(int x, int y, TerrainType type);

    void removeEntity(int entityId);

}
