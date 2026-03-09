package com.ricky.metalbox.model;

import java.util.List;

public interface Land {
    boolean addEntity(Entity e);

    boolean moveEntity(Entity e, Position anchor);

    List<Entity> getEntities();
}
