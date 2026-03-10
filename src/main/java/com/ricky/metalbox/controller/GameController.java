package com.ricky.metalbox.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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

    private void gameTick() {
        movementLogic(); // aggiorniamo tutti gli spazi e le coordinate delle entità

        friendshipLogic();

        if (this.viewRepaintCallback != null) {
            this.viewRepaintCallback.run(); // ridisegna!!
        }
    }

    private void movementLogic() {
        for (final Entity entity : this.land.getEntities()) {
            // numero tra -1 e 1 inclusi, in quanto l'entità sulla land 2D può muoversi
            // lungo l'asse x e y di soli tre valori, -1, 0, 1
            int deltaX = this.random.nextInt(3) - 1;
            int deltaY = this.random.nextInt(3) - 1;

            if (deltaX != 0 || deltaY != 0) {// nel caso l'entità voglia muoversi
                Position currentAnchor = entity.getAnchorPosition();

                Position newAnchorPos = new Position(currentAnchor.getX() + deltaX, currentAnchor.getY() + deltaY);
                this.land.moveEntity(entity, newAnchorPos);
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
