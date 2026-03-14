package com.ricky.metalbox.model.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ricky.metalbox.model.Utilities.Position;

public abstract class AbstractEntity implements Entity {

    private Position anchorPosition;
    private Position targetPosition;
    private boolean alive = true;
    private final Set<Entity> friends = new HashSet<>();
    private int thinkingTicks = 0;

    public AbstractEntity(final Position birthPosition) {
        this.anchorPosition = birthPosition;
    }

    // (template method) ogni creatura figlia definirà solo la propria forma
    protected abstract List<Position> getShape();

    // metodi comuni a tutte le entità
    @Override public boolean isAlive() { return this.alive; }
    @Override public void setAnchorPosition(Position p) { this.anchorPosition = p; }
    @Override public Position getAnchorPosition() { return this.anchorPosition; }
    @Override public void setTargetPosition(Position p) { this.targetPosition = p; }
    @Override public Position getTargetPosition() { return this.targetPosition; }
    @Override public void setThinkingTicks(int ticks) { this.thinkingTicks = ticks; }
    @Override public int getThinkingTicks() { return this.thinkingTicks; }
    @Override public void addFriend(final Entity e) { this.friends.add(e); }
    @Override public Set<Entity> getFriends() { return Collections.unmodifiableSet(this.friends); }

    @Override
    public List<Position> getOccupiedPositions() {
        List<Position> occupiedPos = new ArrayList<>();
        for (Position p : getShape()) {
            occupiedPos.add(new Position(
                this.anchorPosition.getX() + p.getX(),
                this.anchorPosition.getY() + p.getY()
            ));
        }
        return occupiedPos;
    }
}
