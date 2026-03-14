package com.ricky.metalbox.model.Land;

import java.util.List;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Utilities.Position;

public interface Land {
    boolean addEntity(Entity e);

    boolean moveEntity(Entity e, Position anchor);

    List<Entity> getEntities();

    boolean addObstacle(Obstacle o);
}
