package com.ricky.metalbox.system;

import java.util.concurrent.ThreadLocalRandom; // più veloce del campo random condiviso

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.ECS.EntityType;
import com.ricky.metalbox.model.Land.Land;

public class MovementSystem implements EntitySystem{

    private final Land land;

    public MovementSystem(final Land land) {
        this.land = land;
    }

    @Override
    public void update() {
        EntityManager em = land.getEntityManager();

        // loop ecs di iterazione su array
        for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
            // saltiamo id morti o quelli che non hanno i componenti necessari per muoversi
            if (!em.isAlive[i] || em.type[i] != (byte) EntityType.HUMAN.ordinal()) {
                continue;
            }

            int id = i;

            // gestione thinking time
            if (em.thinkingTicksRemaining[id] > 0) {
                em.thinkingTicksRemaining[id]--;
                continue;
            }

            int currentX = em.posX[id];
            int currentY = em.posY[id];
            boolean hasTarget = em.hasTarget[id];

            // se non c'è target o siamo arrivati a destinazione
            if (!hasTarget || (currentX == em.targetX[id] && currentY == em.targetY[id])) {

                if (hasTarget) { // era arrivato a destinazione
                    em.thinkingTicksRemaining[id] = ThreadLocalRandom.current().nextInt(60) + 15;
                }

                // FASE WANDERING LOCALE, raggio 20 celle
                int radius = 20;
                int tX = 0, tY = 0;
                boolean found = false;
                int attempts = 0;

                // tenta massimo 5 volte per non bloccare la cpu
                while (attempts < 5 && !found) {
                    // distribuzione uniforme in un bounding box locale, ristretta ai limiti della mappa
                    int offsetX = ThreadLocalRandom.current().nextInt(-radius, radius + 1);
                    int offsetY = ThreadLocalRandom.current().nextInt(-radius, radius + 1);

                    tX = Math.max(8, Math.min(this.land.getSize() - 10, currentX + offsetX));
                    tY = Math.max(8, Math.min(this.land.getSize() - 10, currentY + offsetY));

                    if (this.land.isCellFree(tX, tY)) {
                        found = true;
                    }
                    attempts++;
                }

                if (found) {
                    em.targetX[id] = tX;
                    em.targetY[id] = tY;
                    em.hasTarget[id] = true;
                } else {
                    // se la mappa è troppo piena, riposa e riprova più tardi
                    em.thinkingTicksRemaining[id] = 15;
                    continue;
                }
            }

            int targetX = em.targetX[id];
            int targetY = em.targetY[id];

            int deltaX = Integer.compare(targetX, currentX);
            int deltaY = Integer.compare(targetY, currentY);

            if (deltaX != 0 || deltaY != 0) {
                // movimento ideale (diagonale/dritto)
                boolean moved = this.land.moveEntity(id, currentX + deltaX, currentY + deltaY);

                if (!moved) {
                    boolean movedX = false;
                    boolean movedY = false;

                    // prova solo X se ci stiamo muovendo in diagonale
                    if (deltaX != 0) {
                        movedX = this.land.moveEntity(id, currentX + deltaX, currentY);
                    }
                    // se fallisce anche X, prova solo Y
                    if (!movedX && deltaY != 0) {
                        movedY = this.land.moveEntity(id, currentX, currentY + deltaY);
                    }

                    // se tutti i tentativi falliscono, resetta il target
                    if (!movedX && !movedY) {
                        em.hasTarget[id] = false;
                        em.thinkingTicksRemaining[id] = ThreadLocalRandom.current().nextInt(5) + 2;
                    }
                }
            }
        }
    }
}
