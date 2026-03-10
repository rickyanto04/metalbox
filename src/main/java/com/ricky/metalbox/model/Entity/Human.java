package com.ricky.metalbox.model.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import com.ricky.metalbox.model.Utilities.Position;

public class Human implements Entity{

    private final List<Position> shape = List.of(
        new Position(0, 0),
        new Position(2, 0),
        new Position(1, 1),
        new Position(1, 2)
    );
    private Position anchorPosition;
    private Position targetPosition;
    private boolean alive = true;
    private Set<Entity> friends = new HashSet<>();
    private int thinkingTicks = 0;

    public Human(final Position birthPosition) {
        this.anchorPosition = birthPosition;
    }

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    @Override
    public void setAnchorPosition(Position p) {
        this.anchorPosition = p;
    }

    @Override
    public Position getAnchorPosition() {
        return this.anchorPosition;
    }

    @Override
    public List<Position> getOccupiedPositions() {
        List<Position> occupiedPos = new ArrayList<>();
        occupiedPos.add(new Position(this.anchorPosition.getX() + this.shape.getFirst().getX(), this.anchorPosition.getY() + this.shape.getFirst().getY()));
        occupiedPos.add(new Position(this.anchorPosition.getX() + this.shape.get(1).getX(), this.anchorPosition.getY() + this.shape.get(1).getY()));
        occupiedPos.add(new Position(this.anchorPosition.getX() + this.shape.get(2).getX(), this.anchorPosition.getY() + this.shape.get(2).getY()));
        occupiedPos.add(new Position(this.anchorPosition.getX() + this.shape.getLast().getX(), this.anchorPosition.getY() + this.shape.getLast().getY()));
        return occupiedPos;
    }

    @Override
    public void addFriend(final Entity e) {
        this.friends.add(e);
    }

    @Override
    public Set<Entity> getFriends() {
        return Collections.unmodifiableSet(this.friends);
    }

    @Override
    public void setTargetPosition(Position p) {
        this.targetPosition = p;
    }

    @Override
    public Position getTargetPosition() {
        return this.targetPosition;
    }

    @Override
    public void setThinkingTicks(int ticks) {
        this.thinkingTicks = ticks;
    }

    @Override
    public int getThinkingTicks() {
        return this.thinkingTicks;
    }
}
