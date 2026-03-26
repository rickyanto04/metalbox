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

        // MULTITHREADING, spalmo il carico su tutta la cpu, funge da for normale ma per multithreading
        java.util.stream.IntStream.range(0, EntityManager.MAX_ENTITIES).parallel().forEach(id -> {

            // nei flussi lambda "return" = "continue" di un ciclo for
            if (!em.isAlive[id] || em.type[id] != (byte) EntityType.HUMAN.ordinal()) {
                return;
            }

            if (em.thinkingTicksRemaining[id] > 0) {
                em.thinkingTicksRemaining[id]--;
                return;
            }

            int currentX = em.posX[id];
            int currentY = em.posY[id];
            boolean hasTarget = em.hasTarget[id];

            if (!hasTarget || (currentX == em.targetX[id] && currentY == em.targetY[id])) {

                if (hasTarget) {
                    // ThreadLocalRandom = random veloci senza conflitti tra thread
                    em.thinkingTicksRemaining[id] = ThreadLocalRandom.current().nextInt(60) + 15;
                }

                int radius = 20;
                int tX = 0, tY = 0;
                boolean found = false;
                int attempts = 0;

                while (attempts < 5 && !found) {
                    int offsetX = ThreadLocalRandom.current().nextInt(-radius, radius + 1);
                    int offsetY = ThreadLocalRandom.current().nextInt(-radius, radius + 1);

                    tX = Math.max(8, Math.min(this.land.getSize() - 10, currentX + offsetX));
                    tY = Math.max(8, Math.min(this.land.getSize() - 10, currentY + offsetY));

                    // isCellFree è in sola lettura = threadsafe
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
                    em.thinkingTicksRemaining[id] = 15;
                    return;
                }
            }

            int targetX = em.targetX[id];
            int targetY = em.targetY[id];

            int deltaX = Integer.compare(targetX, currentX);
            int deltaY = Integer.compare(targetY, currentY);

            if (deltaX != 0 || deltaY != 0) {
                // moveEntity ora è protetto dallo SpatialLock in abstractLand
                boolean moved = this.land.moveEntity(id, currentX + deltaX, currentY + deltaY);

                if (!moved) {
                    boolean movedX = false;
                    boolean movedY = false;

                    if (deltaX != 0) {
                        movedX = this.land.moveEntity(id, currentX + deltaX, currentY);
                    }
                    if (!movedX && deltaY != 0) {
                        movedY = this.land.moveEntity(id, currentX, currentY + deltaY);
                    }

                    if (!movedX && !movedY) {
                        em.hasTarget[id] = false;
                        em.thinkingTicksRemaining[id] = ThreadLocalRandom.current().nextInt(5) + 2;
                    }
                }
            }
        });
    }
}
