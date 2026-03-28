package com.ricky.metalbox.controller;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Land.AbstractLand;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Terrain.TerrainType;
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

    private double lastMouseX = -1;
    private double lastMouseY = -1;

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
                    startX = random.nextInt(this.land.getSize() - 40) + 20;
                    startY = random.nextInt(this.land.getSize() - 40) + 20;
                    spawnPos = new Position(startX, startY);
                } while (!land.isCellFree(spawnPos.getX(), spawnPos.getY()));

                // creazione id per il nuovo umano
                EntityManager em = ((AbstractLand)land).getEntityManager();
                int entityId = em.createEntity();

                // configurazione tramite array primitivi
                em.type[entityId] = (byte) com.ricky.metalbox.model.ECS.EntityType.HUMAN.ordinal();
                em.posX[entityId] = spawnPos.getX();
                em.posY[entityId] = spawnPos.getY();

                // genera un'età gaussiana (media 95, dev standard 10) e la restringe tra 65 e 115
                int lifespanYears = (int) Math.max(65, Math.min(115, 95 + ThreadLocalRandom.current().nextGaussian() * 10));

                // assegna la vita basandosi sulla scala temporale
                em.maxLifespanInTicks[entityId] = lifespanYears * EntityManager.TICKS_PER_YEAR;

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
                            startX = random.nextInt(this.land.getSize() - 40) + 20;
                            startY = random.nextInt(this.land.getSize() - 40) + 20;
                            spawnPos = new Position(startX, startY);
                            attempts++;
                        } while (!land.isCellFree(spawnPos.getX(), spawnPos.getY()) && attempts < 50);

                        // Se ha trovato una cella libera, aggiunge l'umano
                        if (attempts < 50) {
                            EntityManager em = land.getEntityManager();
                            int entityId = em.createEntity();

                            // Configurazione tramite array primitivi
                            em.type[entityId] = (byte) com.ricky.metalbox.model.ECS.EntityType.HUMAN.ordinal();
                            em.posX[entityId] = spawnPos.getX();
                            em.posY[entityId] = spawnPos.getY();

                            // genera un'età gaussiana (media 85, dev standard 10) e la restringe tra 65 e 115
                            int lifespanYears = (int) Math.max(65, Math.min(115, 95 + ThreadLocalRandom.current().nextGaussian() * 10));

                            // assegna la vita basandosi sulla scala temporale
                            em.maxLifespanInTicks[entityId] = lifespanYears * EntityManager.TICKS_PER_YEAR;

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

        // gestione zoom con rotellina mouse (zoom al verso il cursore)
        this.view.getCanvas().setOnScroll(event -> {
            double oldZoom = this.view.getZoom();
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            double newZoom = Math.max(0.1, Math.min(10.0, oldZoom * zoomFactor));

            if (oldZoom == newZoom) return; // limite massimo o minimo

            // coordinate attuali del cursore
            double mouseX = event.getX();
            double mouseY = event.getY();

            // coordinata assoluta nel "mondo"
            double worldX = (mouseX + this.view.getCameraX()) / oldZoom;
            double worldY = (mouseY + this.view.getCameraY()) / oldZoom;

            // applico zoom
            this.view.setZoom(newZoom);

            // sposto la telecamera sotto i pixel dove puntavamo
            double newCameraX = (worldX * newZoom) - mouseX;
            double newCameraY = (worldY * newZoom) - mouseY;
            this.view.setCameraX(newCameraX);
            this.view.setCameraY(newCameraY);
        });

        // gestione click e trascinamento(panning e costruzione)
        this.view.getCanvas().setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            if (isBuildingRock && event.isPrimaryButtonDown()) handleMapClickOrDrag(event);
        });

        this.view.getCanvas().setOnMouseDragged(event -> {
            // Tasto Destro (o Sinistro se NON stiamo costruendo) = Muovi Telecamera (Panning)
            if (event.isSecondaryButtonDown() || (!isBuildingRock && event.isPrimaryButtonDown())) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;
                this.view.setCameraX(this.view.getCameraX() - deltaX);
                this.view.setCameraY(this.view.getCameraY() - deltaY);
            }
            // Tasto Sinistro (Mentre costruiamo) = Piazza Rocce
            else if (isBuildingRock && event.isPrimaryButtonDown()) {
                handleMapClickOrDrag(event);
            }
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });

        this.view.getCanvas().setOnMouseReleased(event -> {
            if (isBuildingRock && event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                handleMapClickOrDrag(event);
            }
        });
    }

    private void handleMapClickOrDrag(MouseEvent event) {
        if (!isBuildingRock) return;

        // trasformazione dei pixel dello schermo in coordinate assolute del mondo
        double worldX = (event.getX() + this.view.getCameraX()) / this.view.getZoom();
        double worldY = (event.getY() + this.view.getCameraY()) / this.view.getZoom();

        int gridX = (int) (worldX / GameView.TILE_SIZE);
        int gridY = (int) (worldY / GameView.TILE_SIZE);
        Position currentPos = new Position(gridX, gridY);

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            lastDragPos = currentPos;
            applyBrush(currentPos);
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (lastDragPos != null) drawLineOfRocks(lastDragPos, currentPos);
            lastDragPos = currentPos;
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            lastDragPos = null;
        }
    }

    private void applyBrush(Position p) {
        // leggiamo la grandezza del pennello dallo slider
        int radius = (int) this.view.getBrushSizeSlider().getValue();
        int cx = p.getX();
        int cy = p.getY();

        // ciclo su un box e applico il teorema di Pitagora per tagliare gli angoli e fare un cerchio
        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                // formula cerchio
                if ((x - cx) * (x - cx) + (y - cy) * (y - cy) <= radius * radius) {
                    // controllo bordi
                    if (x >= 0 && x < land.getSize() && y >= 0 && y < land.getSize()) {
                        // sovrascrive solo se la cella è vuota (non calpesta gli umani)
                        if (land.isCellFree(x, y)) {
                            land.setTerrainAt(x, y, TerrainType.ROCK);
                            view.drawTerrainPixelCache(x, y, TerrainType.ROCK);
                        }
                    }
                }
            }
        }
    }

    // Algoritmo di Bresenham (per interpolazione della linea)
    private void drawLineOfRocks(Position start, Position end) {
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
            // ad ogni passo della linea stampo un cerchio con il pennello
            applyBrush(new Position(x0, y0));

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
    }

}
