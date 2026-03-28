package com.ricky.metalbox.system;

import java.util.concurrent.ThreadLocalRandom;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.ECS.EntityType;
import com.ricky.metalbox.model.Land.AbstractLand;
import com.ricky.metalbox.model.Land.Land;

public class LifespanSystem implements EntitySystem{

    // Probabilità scalata proporzionalmente alla dilatazione del tempo
    private static final double RANDOM_DEATH_PROBABILITY = 0.00001 / 75.0;

    private final Land land;

    public LifespanSystem(final Land land) {
        this.land = land;
    }

    @Override
    public void update() {
        EntityManager em = land.getEntityManager();

        em.incrementWorldAge();

        java.util.stream.IntStream.range(0, EntityManager.MAX_ENTITIES).parallel().forEach(i -> {
            // solo entità vive
            if (!em.isAlive[i] || em.type[i] != (byte) EntityType.HUMAN.ordinal()) {
                return;
            }

            // aging
            em.ageInTicks[i]++;

            // 1. controllo morte di vecchiaia
            if (em.ageInTicks[i] >= em.maxLifespanInTicks[i]) {
                ((AbstractLand)land).removeEntity(i);
                return;
            }

            // 2. controllo morte casuale (incidenti, malattie, ecc...)
            if (ThreadLocalRandom.current().nextDouble() < RANDOM_DEATH_PROBABILITY) {
                ((AbstractLand)land).removeEntity(i);
            }
        });
    }

}
