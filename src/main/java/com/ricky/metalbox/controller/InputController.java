package com.ricky.metalbox.controller;

import java.util.Random;

import com.ricky.metalbox.model.Entity.Human;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Obstacle.Rock;
import com.ricky.metalbox.model.Utilities.Position;
import com.ricky.metalbox.view.GameView;

import javafx.scene.input.MouseEvent;

public class InputController {

    private final Land land;
    private final GameView view;
    private final GameController gameController;
    private final Random random;

    // ---> Variabile di stato che ricorda se siamo in modalità "Costruzione Muro"
    private boolean isBuildingRock = false;

    public InputController(final Land land, final GameView view, final GameController gameController) {
        this.land = land;
        this.view = view;
        this.gameController = gameController;
        this.random = new Random();

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // 1. GESTIONE BOTTONE UMANO (Generazione Casuale)
        this.view.getAddHumanButton().setOnAction(event -> {
            int startX, startY;
            Position spawnPos;
            do {
                startX = random.nextInt(200) + 20;
                startY = random.nextInt(200) + 20;
                spawnPos = new Position(startX, startY);
            } while (!land.isCellFree(spawnPos));

            land.addEntity(new Human(spawnPos));
        });

        // 2. GESTIONE BOTTONE PAUSA
        this.view.getPauseButton().setOnAction(event -> {
            this.gameController.togglePause();

            if (this.gameController.isRunning()) {
                this.view.getPauseButton().setText("pause simulation");
                this.view.getPauseButton().setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            } else {
                this.view.getPauseButton().setText("play simulation");
                this.view.getPauseButton().setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ccffcc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            }
        });

        // 3. GESTIONE MODALITÀ COSTRUZIONE ROCCIA (Toggle)
        this.view.getAddRockButton().setOnAction(event -> {
            // isSelected() ritorna true se il bottone è "schiacciato", false se rilasciato
            this.isBuildingRock = this.view.getAddRockButton().isSelected();

            if (this.isBuildingRock) {
                // Stile del bottone attivo (Giallo/Arancio per far capire che stiamo costruendo)
                this.view.getAddRockButton().setStyle("-fx-opacity: 1.0; -fx-padding: 10px 20px; -fx-cursor: crosshair; -fx-background-color: #ffdd99; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            } else {
                // Stile normale disattivato
                this.view.getAddRockButton().setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
            }
        });

        // ---> 4. LA MAGIA DEL MOUSE SULLA MAPPA
        // Usiamo due eventi: MousePressed (click singolo) e MouseDragged (click tenuto premuto in scorrimento)
        this.view.getCanvas().setOnMousePressed(this::handleMapClickOrDrag);
        this.view.getCanvas().setOnMouseDragged(this::handleMapClickOrDrag);
    }

    private void handleMapClickOrDrag(MouseEvent event) {
        // Se non abbiamo schiacciato il bottone "build rock", ignoriamo i click sulla mappa
        if (!isBuildingRock) return;

        // Convertiamo i Pixel del mouse in coordinate della Griglia
        int gridX = (int) (event.getX() / GameView.TILE_SIZE);
        int gridY = (int) (event.getY() / GameView.TILE_SIZE);
        Position clickPos = new Position(gridX, gridY);

        // Prepariamo la roccia fittizia per controllare il suo spazio occupato (2x2)
        Rock proposedRock = new Rock(clickPos);

        // Controlliamo che tutte le celle che la roccia andrà a occupare siano effettivamente libere
        boolean canPlace = true;
        for (Position p : proposedRock.getOccupiedPositions()) {
            if (!land.isCellFree(p)) {
                canPlace = false;
                break; // Basta una cella occupata per bloccare il piazzamento
            }
        }

        // Se lo spazio è interamente libero (niente acqua, niente umani, niente altre rocce), piazzo il muro!
        if (canPlace) {
            land.addObstacle(proposedRock);

            // Richiediamo un aggiornamento visivo immediato alla View
            // (altrimenti il muro comparirebbe a scatti aspettando il frame successivo)
            this.view.renderMap();
        }
    }
}
