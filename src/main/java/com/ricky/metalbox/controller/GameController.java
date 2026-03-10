package com.ricky.metalbox.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Utilities.Position;

public class GameController {

    private final Land land;
    private final Runnable viewRepaintCallback;
    private final Timeline gameLoop;
    private final Random random;

    private final Map<Entity, Map<Entity, Integer>> proximityTimers = new HashMap<>();

    public GameController(final Land land, final Runnable viewRepaintCallback) {
        this.land = land;
        this.viewRepaintCallback =viewRepaintCallback;
        this.random = new Random();

        // ogni tick/frame dura 0,2 secondi quindi 1 secondo equivale a 5 tick
        KeyFrame kFrame = new KeyFrame(Duration.millis(200), e -> gameTick());
        this.gameLoop = new Timeline(kFrame);
        this.gameLoop.setCycleCount(Timeline.INDEFINITE);
    }

    public void start() {
        this.gameLoop.play();
    }

    public void pause() {
        this.gameLoop.pause();
    }

    public boolean isRunning() {
        return this.gameLoop.getStatus() == Animation.Status.RUNNING;
    }

    public void togglePause() {
        if (isRunning()) {
            pause();
        } else {
            start();
        }
    }

    private void gameTick() {
        movementLogic(); // aggiorniamo tutti gli spazi e le coordinate delle entità

        friendshipLogic();

        if (this.viewRepaintCallback != null) {
            this.viewRepaintCallback.run(); // ridisegna!!
        }
    }

    private void movementLogic() {
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

                int newX = this.random.nextInt(240) + 8;
                int newY = this.random.nextInt(240) + 8;
                target = new Position(newX, newY);
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

    private void friendshipLogic() {
        // per ogni entità controlliamo se nel 10x10 attorno al suo anchor sono presenti altre entità
        // se non c'è alcuna entità si va avanti
        // se le entità attorno rimangono vicine alla centrale per più di 10 secondi allora diventano amiche
        // altrimenti si va avanti
        // le entità con amici e le entità amiche diventano rosa

        for (final Entity e1 : this.land.getEntities()) {
            this.proximityTimers.putIfAbsent(e1, new HashMap<>());
            Map<Entity, Integer> timersForE1 = this.proximityTimers.get(e1);

            Position e1Anchor = e1.getAnchorPosition();

            for (final Entity e2 : this.land.getEntities()) {
                if (e1 == e2) continue; // salto l'entità stessa

                Position e2Anchor = e2.getAnchorPosition();
                boolean isNear = false;

                //per il 10x10 attorno ad e1 calcoliamo la distanza assoluta
                //tra le x e le y dei due punti che siano quindi minori o = a 5
                int distanceX = Math.abs(e1Anchor.getX() - e2Anchor.getX());
                int distanceY = Math.abs(e1Anchor.getY() - e2Anchor.getY());

                if (distanceX <= 5 && distanceY <= 5) {
                    isNear = true;
                }

                //se sono vicini allora aggiungo ai frame passati +1 se sono ancora vicini attualmente
                //altrimenti rimuovo la vicinanza totalmente
                if (isNear) {
                    int ticks = timersForE1.getOrDefault(e2, 0) + 1;
                    timersForE1.put(e2, ticks);

                    if (ticks == 50) {
                        e1.addFriend(e2);
                        e2.addFriend(e1);

                        System.out.println("new friendship");
                    }

                } else {
                    timersForE1.remove(e2);
                }
            }
        }
    }
}
