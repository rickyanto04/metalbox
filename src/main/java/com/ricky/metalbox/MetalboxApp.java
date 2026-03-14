package com.ricky.metalbox;

import com.ricky.metalbox.controller.FriendshipController;
import com.ricky.metalbox.controller.GameController;
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
        // Inizializza il Modello
        Land land = new LandImpl();

        // Inizializza la Vista passando il Modello
        GameView view = new GameView(land);

        MovementController movementController = new MovementController(land);
        FriendshipController friendshipController = new FriendshipController(land);
        GameController controller = new GameController(movementController, friendshipController, view::renderMap);

        view.getPauseButton().setOnAction(event -> {
            controller.togglePause(); // Mette in pausa o riavvia

            // Cambia il testo del bottone in base a cosa sta facendo ora il gioco
            if (controller.isRunning()) {
                view.getPauseButton().setText("pause simulation");
                view.getPauseButton().setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            } else {
                view.getPauseButton().setText("play simulation");
                view.getPauseButton().setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ccffcc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            }
        });

        // Configurazione della finestra principale
        Scene scene = new Scene(view); // La root della Scene è direttamente la nostra GameView (che è uno StackPane)

        primaryStage.setTitle("MetalBox - Testing");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Facciamo un primo render a schermo statico e avviamo il loop del gioco
        view.renderMap();
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
