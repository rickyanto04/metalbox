package com.ricky.metalbox;

import com.ricky.metalbox.controller.GameController;
import com.ricky.metalbox.model.Land;
import com.ricky.metalbox.model.LandImpl;
import com.ricky.metalbox.view.GameView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MetalboxApp {
    @Override
    public void start(Stage primaryStage) {
        // 1. Inizializza il Modello
        Land land = new LandImpl();

        // 2. Inizializza la Vista passando il Modello
        GameView view = new GameView(land);

        // 3. Inizializza il Controller passando il Modello e il metodo di render della Vista
        // Usiamo la sintassi view::renderMap per passare il riferimento al metodo
        GameController controller = new GameController(land, view::renderMap);

        // 4. Configurazione della finestra principale
        Scene scene = new Scene(view); // La root della Scene è direttamente la nostra GameView (che è uno StackPane)

        primaryStage.setTitle("MetalBox - Testing");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // 5. Facciamo un primo render a schermo statico e avviamo il loop del gioco
        view.renderMap();
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
