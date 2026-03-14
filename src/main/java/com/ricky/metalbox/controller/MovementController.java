package com.ricky.metalbox.controller;

import java.util.Random;

import com.ricky.metalbox.model.Entity.Entity;
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
        for (final Entity entity : this.land.getEntities()) {

            // gestione thinking time
            int thinkingTicks = entity.getThinkingTicks();
            if (thinkingTicks > 0) {
                // se l'entità sta riposando, riduco il tempo di attesa di 1 e passo alla prossima
                entity.setThinkingTicks(thinkingTicks - 1);
                continue;
            }

            Position current = entity.getAnchorPosition();
            Position target = entity.getTargetPosition();

            // target nullo o entità arrivata a destinazione allora si genera nuovo target senza prendere in considerazione il bordo
            if (target == null || (current.getX() == target.getX() && current.getY() == target.getY())) {

                // se l'entità è arrivata a destinazione/ target NON nullo, pensa
                // se invece è appena spawnata / target è nullo, salta questo blocco e NON pensa
                if (target != null) {
                    int thinkingTime = this.random.nextInt(96) + 5; // 1 a 5 secondi
                    entity.setThinkingTicks(thinkingTime);
                }

                //ciclo che continua a generare coordinate finché non trova una cella libera da ostacoli/entità
                int newX, newY;
                do {
                    newX = this.random.nextInt(240) + 8;
                    newY = this.random.nextInt(240) + 8;
                    target = new Position(newX, newY);
                } while (!this.land.isCellFree(target));

                entity.setTargetPosition(target);

                //salta la fase di movement in quanto deve pensare prima di partire per la prossima meta
                if (entity.getThinkingTicks() > 0) {
                    continue;
                }
            }

            // direzione +1, 0, o -1 verso il target
            // restituisce -1 se target < current, 0 se uguale, 1 se target > current
            int deltaX = Integer.compare(target.getX(), current.getX());
            int deltaY = Integer.compare(target.getY(), current.getY());

            if (deltaX != 0 || deltaY != 0) {
                Position nextStep = new Position(current.getX() + deltaX, current.getY() + deltaY);
                boolean movedSuccessfully = this.land.moveEntity(entity, nextStep);

                // se moveentity restituisce false, significa che la strada è bloccata, reset del target a null
                if (!movedSuccessfully) {
                    entity.setTargetPosition(null);

                    // confusione da 0.4 secondi a 1.2 secondi nel caso sbatta contro qualcosa dato !movedSuccesfully
                    entity.setThinkingTicks(this.random.nextInt(5) + 2);
                }
            }
        }
    }
}
