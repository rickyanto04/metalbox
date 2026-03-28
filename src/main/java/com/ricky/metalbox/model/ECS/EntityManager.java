package com.ricky.metalbox.model.ECS;

public class EntityManager {

    public static final int MAX_ENTITIES = 800000;
    private int nextEntityId = 0;

    // ID recycling system (Object Pooling)
    private final int[] freeIds = new int[MAX_ENTITIES];
    private int freeIdsCount = 0;

    // statistics variables
    private int aliveEntitiesCount = 0;
    private int totalDeathsCount = 0;

    // world age
    public static final int TICKS_PER_YEAR = 2250; // 75 secondi a 30 TPS
    private long worldAgeTicks = 0;

    // (DOD, Data-Oriented-Design) serie di array paralleli primitivi
    public final boolean[] isAlive = new boolean[MAX_ENTITIES];
    public final byte[] type = new byte[MAX_ENTITIES]; // tipo di entità

    // età e vita entità
    public final int[] ageInTicks = new int[MAX_ENTITIES];
    public final int[] maxLifespanInTicks = new int[MAX_ENTITIES];

    // posizione entità
    public final int[] posX = new int[MAX_ENTITIES];
    public final int[] posY = new int[MAX_ENTITIES];

    // obiettivo entità
    public final boolean[] hasTarget = new boolean[MAX_ENTITIES];
    public final int[] targetX = new int[MAX_ENTITIES];
    public final int[] targetY = new int[MAX_ENTITIES];

    // thinking time entità
    public final int[] thinkingTicksRemaining = new int[MAX_ENTITIES];

    // ritardo movimento entità
    public final byte[] moveCooldown = new byte[MAX_ENTITIES];

    public EntityManager() {
        //tutto empty
    }

    public int getAliveCount() { return aliveEntitiesCount; }
    public int getDeadCount() { return totalDeathsCount; }

    public void incrementWorldAge() { this.worldAgeTicks++; }
    public int getWorldAgeYears() { return (int) (this.worldAgeTicks / TICKS_PER_YEAR); }

    public synchronized int createEntity() {
        int id;

        // controllo per riciclare vecchio id
        if (freeIdsCount > 0) {
            freeIdsCount--;
            id = freeIds[freeIdsCount];
        } else {
            // no id vecchi, ne crea uno
            if (nextEntityId >= MAX_ENTITIES) {
                throw new RuntimeException("max entities limit reached");
            }
            id = nextEntityId++;
        }

        isAlive[id] = true;
        ageInTicks[id] = 0;
        maxLifespanInTicks[id] = 0;
        hasTarget[id] = false;
        thinkingTicksRemaining[id] = 0;
        moveCooldown[id] = 0;

        this.aliveEntitiesCount++;
        return id;
    }

    public synchronized void destroyEntity(final int id) {
        if (isAlive[id]) {
            isAlive[id] = false;
            this.aliveEntitiesCount--;
            this.totalDeathsCount++;

            // push nello stack degli id riutilizzabili
            freeIds[freeIdsCount] = id;
            freeIdsCount++;
        }
    }
}
