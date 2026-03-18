package com.ricky.metalbox;

import com.ricky.metalbox.controller.FriendshipController;
import com.ricky.metalbox.controller.GameController;
import com.ricky.metalbox.controller.InputController;
import com.ricky.metalbox.controller.MovementController;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Land.LandImpl;
import com.ricky.metalbox.view.GameView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MetalboxApp extends Application{

    @Override
    public void start(Stage primaryStage) {
        // inizializzazione del model
        Land land = new LandImpl();

        // inizializzazione della view
        GameView view = new GameView(land);

        // inizializzazione dei controller
        MovementController movementController = new MovementController(land);
        FriendshipController friendshipController = new FriendshipController(land);
        GameController gameController = new GameController(land, movementController, friendshipController, view::renderMap);
        new InputController(land, view, gameController);

        // configurazione della finestra
        Scene scene = new Scene(view, 1024, 768);
        primaryStage.setTitle("MetalBox - Sandbox");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        // avvio
        view.renderMap();
        gameController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
