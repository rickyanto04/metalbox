package com.ricky.metalbox;

import com.ricky.metalbox.controller.GameController;
import com.ricky.metalbox.controller.InputController;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Land.LandImpl;
import com.ricky.metalbox.system.LifespanSystem;
import com.ricky.metalbox.system.MovementSystem;
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

        // inizializzazione dell'architettura ECS
        GameController gameController = new GameController(view::renderMap);
        gameController.addSystem(new MovementSystem(land));
        gameController.addSystem(new LifespanSystem(land));

        new InputController(land, view, gameController);

        // configurazione della finestra
        Scene scene = new Scene(view, 1024, 768);
        primaryStage.setTitle("MetalBox - Sandbox");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        // avvio
        gameController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
