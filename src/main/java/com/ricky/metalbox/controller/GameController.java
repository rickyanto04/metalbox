package com.ricky.metalbox.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;

import com.ricky.metalbox.model.Entity;
import com.ricky.metalbox.model.Land;
import com.ricky.metalbox.model.Position;

public class GameController {

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
        updateLogic(); // aggiorniamo tutti gli spazi e le coordinate

        if (this.viewRepaintCallback != null) {
            this.viewRepaintCallback.run(); // ridisegna!!
        }
    }

    private void updateLogic() {
        for (Entity entity : this.land.getEntities()) {
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
}
