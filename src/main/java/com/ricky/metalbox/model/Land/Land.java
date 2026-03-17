package com.ricky.metalbox.model.Land;

import java.util.List;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

public interface Land {
    boolean addEntity(Entity e);

    boolean moveEntity(Entity e, Position anchor);

    List<Entity> getEntities();

    boolean addObstacle(Obstacle o);

    List<Obstacle> getObstacles();

    // per far capire se una cella è dentro i bordi e non occupata
    boolean isCellFree(Position p);

    int getSize();

    TerrainType getTerrainAt(Position p);

    // metodo per spatial partitioning
    List<Entity> getEntitiesNear(Position center, int radius);
}
