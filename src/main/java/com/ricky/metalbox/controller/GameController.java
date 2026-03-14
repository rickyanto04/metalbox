package com.ricky.metalbox.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation;

public class GameController {

    private final Runnable viewRepaintCallback;
    private final Timeline gameLoop;

    //solita dependency injection
    private final MovementController movementController;
    private final FriendshipController friendshipController;

    //non prende più la Land direttamente, ma i due controller specializzati
    public GameController(final MovementController movementController, final FriendshipController friendshipController, final Runnable viewRepaintCallback) {
        this.movementController = movementController;
        this.friendshipController = friendshipController;
        this.viewRepaintCallback = viewRepaintCallback;

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
        // DELEGHIAMO LA LOGICA AI RISPETTIVI CONTROLLER
        this.movementController.updateMovements();
        this.friendshipController.updateFriendships();

        if (this.viewRepaintCallback != null) {
            this.viewRepaintCallback.run(); // ridisegna!!
        }
    }
}
