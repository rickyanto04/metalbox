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
                    em.ticksRemaining[id] = this.random.nextInt(96) + 5;
                }

                Position target;
                do {
                    target = new Position(this.random.nextInt((this.land.getSize() - 10)) + 8, this.random.nextInt((this.land.getSize() - 10)) + 8);
                } while (!this.land.isCellFree(target));

                em.targetX[id] = target.getX();
                em.targetY[id] = target.getY();
                em.hasTarget[id] = true;

                if (em.ticksRemaining[id] > 0) {
                    continue;
                }
            }

            int targetX = em.targetX[id];
            int targetY = em.targetY[id];

            int deltaX = Integer.compare(targetX, currentX);
            int deltaY = Integer.compare(targetY, currentY);

            if (deltaX != 0 || deltaY != 0) {
                Position nextStep = new Position(currentX + deltaX, currentY + deltaY);
                boolean movedSuccessfully = this.land.moveEntity(id, nextStep);

                if (!movedSuccessfully) {
                    em.hasTarget[id] = false; // Reset target
                    em.ticksRemaining[id] = this.random.nextInt(5) + 2; // Confusione
                }
            }
        }
    }
}
