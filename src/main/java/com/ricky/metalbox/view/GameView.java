package com.ricky.metalbox.view;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class GameView extends StackPane {

    // public, così l'InputController può usarle per calcolare le coordinate del mouse
    public static final int TILE_SIZE = 3;

    private final Land land;
    private final Canvas canvas;

    // variabili telecamera
    private double cameraX = 0;
    private double cameraY = 0;
    private double zoom = 1.0;

    // bottoni come variabili di classe
    private final Button pauseButton;
    private final Button addHumanButton;
    private final ToggleButton addRockButton;
    private final TextField spawnCountField;
    private final Button spawnMultipleButton;

    public GameView(Land land) {
        this.land = land;
        this.canvas = new Canvas(800, 600);

        Pane canvasContainer = new Pane(this.canvas);
        this.canvas.widthProperty().bind(canvasContainer.widthProperty());
        this.canvas.heightProperty().bind(canvasContainer.heightProperty());

        this.pauseButton = new Button("pause simulation");
        this.pauseButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addHumanButton = new Button("add human");
        this.addHumanButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addRockButton = new ToggleButton("build rock");
        this.addRockButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.spawnCountField = new TextField("100");
        this.spawnCountField.setPrefWidth(60);
        this.spawnCountField.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.spawnMultipleButton = new Button("spawn X humans");
        this.spawnMultipleButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightblue; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        HBox multiSpawnContainer = new HBox(5);
        multiSpawnContainer.setAlignment(Pos.CENTER_RIGHT);
        multiSpawnContainer.getChildren().addAll(spawnCountField, spawnMultipleButton);

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.getChildren().addAll(pauseButton, addHumanButton, multiSpawnContainer, addRockButton);
        buttonContainer.setPickOnBounds(false);

        this.setStyle("-fx-background-color: #0F5E9C;");
        this.getChildren().addAll(canvasContainer, buttonContainer);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonContainer, new Insets(20));
    }

    // getter e setter per la telecamera
    public double getCameraX() { return cameraX; }
    public void setCameraX(double cameraX) { this.cameraX = cameraX; }
    public double getCameraY() { return cameraY; }
    public void setCameraY(double cameraY) { this.cameraY = cameraY; }
    public double getZoom() { return zoom; }
    public void setZoom(double zoom) { this.zoom = zoom; }

    // getter generali
    public Canvas getCanvas() { return this.canvas; }
    public Button getPauseButton() { return this.pauseButton; }
    public Button getAddHumanButton() { return this.addHumanButton; }
    public ToggleButton getAddRockButton() { return this.addRockButton; }
    public TextField getSpawnCountField() { return this.spawnCountField; }
    public Button getSpawnMultipleButton() { return this.spawnMultipleButton; }


    // rendering (Frustum Culling)
    public void renderMap() {
        // blocco della mappa durante il disegno per evitare crash se le entità si muovono nel frattempo
        synchronized (this.land) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            double cw = canvas.getWidth();
            double ch = canvas.getHeight();

            // sfondo oceano globale
            gc.clearRect(0, 0, cw, ch);
            gc.setFill(Color.web("#0F5E9C"));
            gc.fillRect(0, 0, cw, ch);

            // posizionamento telecamera e applicazione zoom
            gc.save();
            gc.translate(-cameraX, -cameraY);
            gc.scale(zoom, zoom);

            // calcolo del quadrato della mappa attualmente visibile
            int startX = Math.max(0, (int) (cameraX / (TILE_SIZE * zoom)));
            int startY = Math.max(0, (int) (cameraY / (TILE_SIZE * zoom)));
            int endX = Math.min(land.getSize(), (int) ((cameraX + cw) / (TILE_SIZE * zoom)) + 1);
            int endY = Math.min(land.getSize(), (int) ((cameraY + ch) / (TILE_SIZE * zoom)) + 1);

            // disegno delle sole celle visibili
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    TerrainType type = land.getTerrainAt(new Position(x, y));
                    switch (type) {
                        case WATER: gc.setFill(Color.CORNFLOWERBLUE); break;
                        case SAND: gc.setFill(Color.NAVAJOWHITE); break;
                        case MOUNTAIN: gc.setFill(Color.SLATEGRAY); break;
                        case GRASS:
                        default: gc.setFill(Color.LIGHTGREEN); break;
                    }
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            // disegno delle sole entità visibili nelle celle visibili
            EntityManager em = land.getEntityManager();
            for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
                if (em.isAlive[i] && em.positionComponents[i] != null && em.shapeComponents[i] != null && em.graphicsComponents[i] != null) {
                    int ax = em.positionComponents[i].x;
                    int ay = em.positionComponents[i].y;

                    // margine di tolleranza di 5 celle per le rocce grandi
                    if (ax >= startX - 5 && ax <= endX + 5 && ay >= startY - 5 && ay <= endY + 5) {
                        gc.setFill(em.graphicsComponents[i].color);
                        for (Position relative : em.shapeComponents[i].relativePositions) {
                            int drawX = ax + relative.getX();
                            int drawY = ay + relative.getY();
                            gc.fillRect(drawX * TILE_SIZE, drawY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }

            gc.restore(); // reset telecamera per frame successivo
        }
    }
}
