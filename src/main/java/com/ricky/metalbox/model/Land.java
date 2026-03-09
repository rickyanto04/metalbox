package com.ricky.metalbox.model;

public interface Land {
    boolean addEntity(Entity e);

    boolean moveEntity(Entity e, Position anchor);
}
