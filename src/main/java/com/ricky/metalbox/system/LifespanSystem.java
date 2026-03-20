package com.ricky.metalbox.system;

import java.util.concurrent.ThreadLocalRandom;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.ECS.EntityType;
import com.ricky.metalbox.model.Land.AbstractLand;
import com.ricky.metalbox.model.Land.Land;

public class LifespanSystem implements EntitySystem{

    // probabilità fissa di morire per cause esterne ad ogni tick.
    // Con 0.0001 (0.01%), un'entità ha circa il 25% di probabilità di morire prematuramente
    // nel corso di 80 anni (2400 ticks)
    private static final double RANDOM_DEATH_PROBABILITY = 0.0001;

    private final Land land;

    public LifespanSystem(final Land land) {
        this.land = land;
    }

    @Override
    public void update() {
        EntityManager em = land.getEntityManager();

        for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
            // solo entità vive
            if (!em.isAlive[i] || em.type[i] != (byte) EntityType.HUMAN.ordinal()) {
                continue;
            }

            // aging
            em.ageInTicks[i]++;

            // 1. controllo morte di vecchiaia
            if (em.ageInTicks[i] >= em.maxLifespanInTicks[i]) {
                ((AbstractLand)land).removeEntity(i);
                continue;
            }

            // 2. controllo morte casuale (incidenti, malattie, ecc...)
            if (ThreadLocalRandom.current().nextDouble() < RANDOM_DEATH_PROBABILITY) {
                ((AbstractLand)land).removeEntity(i);
            }
        }
    }

}
