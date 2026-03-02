package com.ricky.metalbox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Human implements Entity{

    private boolean alive = true;
    private Map<Entity, Boolean> friends = new HashMap<>();

    @Override
    public boolean isAlive() {
        return this.alive;
    }

    public void addFriend(final Entity friend) {
        this.friends.put(friend, true);
    }

    public void removeFriend(final Entity friend) {
        this.friends.remove(friend);
    }

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
