package com.ricky.metalbox.model.Entity;

import java.util.List;
import java.util.Set;

import com.ricky.metalbox.model.Utilities.Position;

public interface Entity {
    boolean isAlive();

    void setAnchorPosition(Position p);

    Position getAnchorPosition();

    List<Position> getOccupiedPositions();

    void addFriend(Entity e);

    Set<Entity> getFriends();

    void setTargetPosition(Position p);

    Position getTargetPosition();

    void setThinkingTicks(int ticks);

    int getThinkingTicks();
}
