package com.ricky.metalbox.controller;

import java.util.List;
import java.util.Random;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.ECS.Components.GraphicsComponent;
import com.ricky.metalbox.model.ECS.Components.PositionComponent;
import com.ricky.metalbox.model.ECS.Components.ShapeComponent;
import com.ricky.metalbox.model.ECS.Components.TargetComponent;
import com.ricky.metalbox.model.ECS.Components.ThinkingComponent;
import com.ricky.metalbox.model.Land.AbstractLand;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Utilities.Position;
import com.ricky.metalbox.view.GameView;

import javafx.scene.input.MouseEvent;

public class InputController {

    private final Land land;
    private final GameView view;
    private final GameController gameController;
    private final Random random;

    // variabile di stato che ricorda se siamo in modalità "Costruzione Muro"
    private boolean isBuildingRock = false;

    // variabile che mantiene l'ultima posizione del mouse trascinato
    private Position lastDragPos = null;

    public InputController(final Land land, final GameView view, final GameController gameController) {
        this.land = land;
        this.view = view;
        this.gameController = gameController;
        this.random = new Random();

        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // GESTIONE BOTTONE UMANO (Generazione Casuale)
        this.view.getAddHumanButton().setOnAction(event -> {
                synchronized (this.land) {
                int startX, startY;
                Position spawnPos;
                do {
                    startX = random.nextInt(200) + 20;
                    startY = random.nextInt(200) + 20;
                    spawnPos = new Position(startX, startY);
                } while (!land.isCellFree(spawnPos));

                // creazione id per il nuovo umano
                EntityManager em = ((AbstractLand)land).getEntityManager();
                int entityId = em.createEntity();
                em.graphicsComponents[entityId] = new GraphicsComponent(javafx.scene.paint.Color.BLACK);

                // unione dei componenti all'id
                em.positionComponents[entityId] = new PositionComponent(spawnPos.getX(), spawnPos.getY());

                em.shapeComponents[entityId] = new ShapeComponent(List.of(
                    new Position(0, 0), new Position(2, 0),
                    new Position(1, 1), new Position(1, 2)
                ));

                em.targetComponents[entityId] = new TargetComponent();
                em.thinkingComponents[entityId] = new ThinkingComponent();

                ((AbstractLand)land).registerEntity(entityId); //aggiunge umano a land e chunk
            }
        });

        // GESTIONE BOTTONE PAUSA
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

        // GESTIONE MODALITÀ COSTRUZIONE ROCCIA (Toggle)
        this.view.getAddRockButton().setOnAction(event -> {
            // isSelected() ritorna true se il bottone è "schiacciato", false se rilasciato
            this.isBuildingRock = this.view.getAddRockButton().isSelected();

            // interruttore per il panning
            this.view.getScrollPane().setPannable(!this.isBuildingRock);

            if (this.isBuildingRock) {
                this.view.getAddRockButton().setStyle("-fx-opacity: 1.0; -fx-padding: 10px 20px; -fx-cursor: crosshair; -fx-background-color: #ffdd99; -fx-border-color: black; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-background-radius: 3px;");
                this.view.getCanvas().setCursor(javafx.scene.Cursor.CROSSHAIR);
            } else {
                this.view.getAddRockButton().setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");
                this.view.getCanvas().setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        // GESTIONE BOTTONE SPAWN MULTIPLO
        this.view.getSpawnMultipleButton().setOnAction(event -> {
            try {
                // Legge il numero inserito
                int count = Integer.parseInt(this.view.getSpawnCountField().getText());

                // Blocchiamo la mappa mentre aggiungiamo la folla
                synchronized (this.land) {
                    for (int i = 0; i < count; i++) {
                        int startX, startY;
                        Position spawnPos;
                        int attempts = 0;

                        // Cerca una cella libera (max 50 tentativi per evitare loop se la mappa è piena)
                        do {
                            startX = random.nextInt(200) + 20;
                            startY = random.nextInt(200) + 20;
                            spawnPos = new Position(startX, startY);
                            attempts++;
                        } while (!land.isCellFree(spawnPos) && attempts < 50);

                        // Se ha trovato una cella libera, aggiunge l'umano
                        if (attempts < 50) {
                            EntityManager em = land.getEntityManager();
                            int entityId = em.createEntity();
                            em.graphicsComponents[entityId] = new GraphicsComponent(javafx.scene.paint.Color.BLACK);

                            em.positionComponents[entityId] = new PositionComponent(spawnPos.getX(), spawnPos.getY());
                            em.shapeComponents[entityId] = new ShapeComponent(List.of(
                                new Position(0, 0), new Position(2, 0),
                                new Position(1, 1), new Position(1, 2)
                            ));
                            em.targetComponents[entityId] = new TargetComponent();
                            em.thinkingComponents[entityId] = new ThinkingComponent();

                            // Registra l'entità nella mappa e nei chunk
                            ((AbstractLand)land).registerEntity(entityId);
                        }
                    }
                }
                System.out.println("Spawn di " + count + " umani completato!");
            } catch (NumberFormatException e) {
                System.out.println("Errore: Inserisci un numero valido nella casella.");
            }
        });

        this.view.getCanvas().addEventHandler(MouseEvent.ANY, event -> {
        if (!isBuildingRock) {
            // Se NON stiamo costruendo, lasciamo che l'evento "passi oltre" verso lo ScrollPane
            return;
        }
        handleMapClickOrDrag(event);
        event.consume(); // Consumiamo l'evento così non attiva il panning durante la costruzione
    });
    }

    private void handleMapClickOrDrag(MouseEvent event) {
        // Se non abbiamo schiacciato il bottone "build rock", ignoriamo i click sulla mappa
        if (!isBuildingRock) return;

        // assicuriamoci che si stia usando il tasto sx del mouse
        if (!event.isPrimaryButtonDown() && event.getEventType() != MouseEvent.MOUSE_PRESSED) return;

        // Convertiamo i Pixel del mouse in coordinate della Griglia
        int gridX = (int) (event.getX() / GameView.TILE_SIZE);
        int gridY = (int) (event.getY() / GameView.TILE_SIZE);
        Position currentPos = new Position(gridX, gridY);

        boolean redrawNeeded = false;

        // LOGICA DI DISEGNO CONTINUO
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            lastDragPos = currentPos;
            redrawNeeded = tryPlaceRock(currentPos);

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (lastDragPos != null) {
                // Richiama l'algoritmo che disegna i blocchi mancanti tra il frame precedente e questo
                redrawNeeded = drawLineOfRocks(lastDragPos, currentPos);
            }
            lastDragPos = currentPos;

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            lastDragPos = null; // Fine della linea
        }

        /*
        // OTTIMIZZAZIONE, ridisegniamo la mappa solo UNA VOLTA alla fine del calcolo, non per ogni singola roccia!
        if (redrawNeeded) {
            this.view.renderMap();
        }
        */
    }

    /*
    Algoritmo di Bresenham per interpolazione della linea
    */
    private boolean drawLineOfRocks(Position start, Position end) {
        boolean anyRockPlaced = false;
        int x0 = start.getX();
        int y0 = start.getY();
        int x1 = end.getX();
        int y1 = end.getY();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            boolean placed = tryPlaceRock(new Position(x0, y0));
            if (placed) anyRockPlaced = true;

            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
        return anyRockPlaced;
    }

    // restituisce true con roccia piazzata e non chiama rendermap internamente
    private boolean tryPlaceRock(Position p) {
        synchronized (this.land) {
            EntityManager em = land.getEntityManager();

            // La forma standard della roccia 3x3
            List<Position> rockShape = List.of(
                new Position(0, 0), new Position(1, 0), new Position(2, 0),
                new Position(0, 1), new Position(1, 1), new Position(2, 1),
                new Position(0, 2), new Position(1, 2), new Position(2, 2)
            );

            // Controlla se lo spazio per la roccia è libero
            boolean canPlace = true;
            for (Position relative : rockShape) {
                if (!land.isCellFree(new Position(p.getX() + relative.getX(), p.getY() + relative.getY()))) {
                    canPlace = false;
                    break;
                }
            }

            // Se è libero, creiamo l'entità Roccia
            if (canPlace) {
                int entityId = em.createEntity();

                em.positionComponents[entityId] = new PositionComponent(p.getX(), p.getY());
                em.shapeComponents[entityId] = new ShapeComponent(rockShape);

                // Le diamo un colore grigio
                em.graphicsComponents[entityId] = new GraphicsComponent(javafx.scene.paint.Color.DARKSLATEGRAY);

                // IMPORTANTE: NON le diamo il TargetComponent né il ThinkingComponent!
                // Così il MovementController la ignorerà in automatico.

                // La registriamo nella mappa
                ((com.ricky.metalbox.model.Land.AbstractLand)land).registerEntity(entityId);
                return true;
            }
            return false;
        }
    }
}
