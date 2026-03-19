package com.ricky.metalbox.controller;

import java.util.concurrent.ThreadLocalRandom; // più veloce del campo random condiviso

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Land.Land;

public class MovementController {

    private final Land land;

    public MovementController(final Land land) {
        this.land = land;
    }

    public void updateMovements() {
        EntityManager em = land.getEntityManager();

        // loop ecs di iterazione su array
        for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
            // saltiamo id morti o quelli che non hanno i componenti necessari per muoversi
            if (!em.isAlive[i] || em.type[i] != (byte) com.ricky.metalbox.model.ECS.EntityType.HUMAN.ordinal()) {
                continue;
            }

            int id = i;

            // gestione thinking time
            if (em.ticksRemaining[id] > 0) {
                em.ticksRemaining[id]--;
                continue;
            }

            int currentX = em.posX[id];
            int currentY = em.posY[id];
            boolean hasTarget = em.hasTarget[id];

            // se non c'è target o siamo arrivati a destinazione
            if (!hasTarget || (currentX == em.targetX[id] && currentY == em.targetY[id])) {

                if (hasTarget) { // era arrivato a destinazione
                    em.ticksRemaining[id] = ThreadLocalRandom.current().nextInt(96) + 5;
                }

                int tX = 0, tY = 0;
                boolean found = false;
                int attempts = 0;

                // tenta massimo 5 volte per non bloccare la cpu
                while (attempts < 5 && !found) {
                    tX = ThreadLocalRandom.current().nextInt((this.land.getSize() - 10)) + 8;
                    tY = ThreadLocalRandom.current().nextInt((this.land.getSize() - 10)) + 8;
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
                    em.ticksRemaining[id] = 15;
                    continue;
                }

                if (em.ticksRemaining[id] > 0) continue;
            }

            int targetX = em.targetX[id];
            int targetY = em.targetY[id];

            int deltaX = Integer.compare(targetX, currentX);
            int deltaY = Integer.compare(targetY, currentY);

            if (deltaX != 0 || deltaY != 0) {
                // zero allocazione, chiamata a metodo primitivo
                boolean movedSuccessfully = this.land.moveEntity(id, currentX + deltaX, currentY + deltaY);

                if (!movedSuccessfully) {
                    em.hasTarget[id] = false;
                    em.ticksRemaining[id] = ThreadLocalRandom.current().nextInt(5) + 2;
                }
            }
        }
    }
}
