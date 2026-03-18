package com.ricky.metalbox.controller;

import java.util.Random;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Utilities.Position;

public class MovementController {

    private final Land land;
    private final Random random;

    public MovementController(final Land land) {
        this.land = land;
        this.random = new Random();
    }

    public void updateMovements() {
        EntityManager em = land.getEntityManager();

        // loop ecs di iterazione su array
        for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
            // saltiamo id morti o quelli che non hanno i componenti necessari per muoversi
            if (!em.isAlive[i] || em.positionComponents[i] == null || em.targetComponents[i] == null || em.thinkingComponents[i] == null) {
                continue;
            }

            int id = i;

            // gestione thinking time
            if (em.thinkingComponents[id].ticksRemaining > 0) {
                em.thinkingComponents[id].ticksRemaining--;
                continue;
            }

            int currentX = em.positionComponents[id].x;
            int currentY = em.positionComponents[id].y;
            boolean hasTarget = em.targetComponents[id].hasTarget;

            // se non c'è target o siamo arrivati a destinazione
            if (!hasTarget || (currentX == em.targetComponents[id].targetX && currentY == em.targetComponents[id].targetY)) {

                if (hasTarget) { // era arrivato a destinazione
                    em.thinkingComponents[id].ticksRemaining = this.random.nextInt(96) + 5;
                }

                Position target;
                do {
                    target = new Position(this.random.nextInt((this.land.getSize() - 10)) + 8, this.random.nextInt((this.land.getSize() - 10)) + 8);
                } while (!this.land.isCellFree(target));

                em.targetComponents[id].targetX = target.getX();
                em.targetComponents[id].targetY = target.getY();
                em.targetComponents[id].hasTarget = true;

                if (em.thinkingComponents[id].ticksRemaining > 0) {
                    continue;
                }
            }

            int targetX = em.targetComponents[id].targetX;
            int targetY = em.targetComponents[id].targetY;

            int deltaX = Integer.compare(targetX, currentX);
            int deltaY = Integer.compare(targetY, currentY);

            if (deltaX != 0 || deltaY != 0) {
                Position nextStep = new Position(currentX + deltaX, currentY + deltaY);
                boolean movedSuccessfully = this.land.moveEntity(id, nextStep);

                if (!movedSuccessfully) {
                    em.targetComponents[id].hasTarget = false; // Reset target
                    em.thinkingComponents[id].ticksRemaining = this.random.nextInt(5) + 2; // Confusione
                }
            }
        }
    }
}
