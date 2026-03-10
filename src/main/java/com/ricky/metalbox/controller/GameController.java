package com.ricky.metalbox.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Utilities.Position;

public class GameController {

    enum Around {
        UP_LEFT(-1, -1),
        UP_CENTER(-1, 0),
        UP_RIGHT(-1, 1),
        CENTER_LEFT(0, -1),
        CENTER_CENTER(0, 0),
        CENTER_RIGHT(0, 1),
        DOWN_LEFT(1, -1),
        DOWN_CENTER(1, 0),
        DOWN_RIGHT(1, 1);

        public final int x;
        public final int y;

        Around(final int x, final int y) {
        this.x = x;
        this.y = y;
        }
    }

    private final Land land;
    private final Runnable viewRepaintCallback;
    private final Timeline gameLoop;
    private final Random random;

    public GameController(final Land land, final Runnable viewRepaintCallback) {
        this.land = land;
        this.viewRepaintCallback =viewRepaintCallback;
        this.random = new Random();

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
        for (final Entity entity : this.land.getEntities()) {
            // per ogni entità controlliamo se nel 3x3 attorno al suo anchor sono presenti altre entità
            // se non c'è alcuna entità si va avanti
            // se le entità attorno rimangono vicine alla centrale per più di 10 secondi allora diventano amiche
            // altrimenti si va avanti

            // le entità con amici e le entità amiche diventano rosa
        }
    }
}
