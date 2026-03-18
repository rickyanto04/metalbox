package com.ricky.metalbox.model.Land;

import java.util.List;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

public interface Land {
    // espone il database dell'ecs system
    EntityManager getEntityManager();

    boolean moveEntity(int entityId, Position anchor);

    // metodo per spatial partitioning
    List<Integer> getEntitiesNear(Position center, int radius);

    boolean addObstacle(Obstacle o);

    List<Obstacle> getObstacles();

    boolean isCellFree(Position p);

    int getSize();

    TerrainType getTerrainAt(Position p);

}
