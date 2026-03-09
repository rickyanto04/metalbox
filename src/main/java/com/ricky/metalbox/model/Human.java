package com.ricky.metalbox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Human implements Entity{

    private final List<Position> shape = List.of(
        new Position(0, 0),
        new Position(2, 0),
        new Position(1, 1),
        new Position(1, 2)
    );
    private Position anchorPosition;
    private boolean alive = true;
    private Map<Entity, Boolean> friends = new HashMap<>();

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
    public void addFriend(final Entity friend) {
        this.friends.put(friend, true);
    }

    @Override
    public void removeFriend(final Entity friend) {
        this.friends.remove(friend);
    }

    @Override
    public List<Entity> getFriends() {
        List<Entity> friendsList = new ArrayList<>();
        for (final Entity e : this.friends.keySet()) {
            if (this.friends.get(e)) {
                friendsList.add(e);
            }
        }
        return friendsList;
    }

}
