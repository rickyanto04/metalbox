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
        // 1. Inizializza il Modello
        Land land = new LandImpl();

        // 2. Inizializza la Vista
        GameView view = new GameView(land);

        // 3. Inizializza i Controller Specializzati
        MovementController movementController = new MovementController(land);
        FriendshipController friendshipController = new FriendshipController(land);

        // 4. Inizializza il Controller Principale
        GameController gameController = new GameController(movementController, friendshipController, view::renderMap);

        // ---> 5. NUOVO: Inizializza l'Input Controller (che aggancerà in automatico i bottoni passati dalla View)
        // NOTA: il codice che era qui per il bottone pausa è stato spostato nell'InputController!
        new InputController(land, view, gameController);

        // 6. Configurazione della finestra
        Scene scene = new Scene(view);
        primaryStage.setTitle("MetalBox - Sandbox");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // 7. Avvio
        view.renderMap();
        gameController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
