package com.ricky.metalbox.model.ECS;

public class EntityManager {

    public static final int MAX_ENTITIES = 200000;
    private int nextEntityId = 0;

    // (DOD, Data-Oriented-Design) serie di array paralleli primitivi
    public final boolean[] isAlive = new boolean[MAX_ENTITIES];
    public final byte[] type = new byte[MAX_ENTITIES]; // tipo di entità

    // posizione entità
    public final int[] posX = new int[MAX_ENTITIES];
    public final int[] posY = new int[MAX_ENTITIES];

    // obiettivo entità
    public final boolean[] hasTarget = new boolean[MAX_ENTITIES];
    public final int[] targetX = new int[MAX_ENTITIES];
    public final int[] targetY = new int[MAX_ENTITIES];

    // thinking time entità
    public final int[] ticksRemaining = new int[MAX_ENTITIES];

    public EntityManager() {
        //tutto empty
    }

    public synchronized int createEntity() {
        if (nextEntityId >= MAX_ENTITIES) {
            throw new RuntimeException("max entities limit reached");
        }
        int id = nextEntityId++; // per crearla basta muoversi avanti nell'array preallocato
        isAlive[id] = true;

        hasTarget[id] = false; // reset valori di default
        ticksRemaining[id] = 0;
        return id;
    }

    public synchronized void destroyEntity(final int id) {
        isAlive[id] = false;
    }
}
