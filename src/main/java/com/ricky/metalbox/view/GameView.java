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
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
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

    // buffer per terreno statico in memoria
    private WritableImage backgroundCache;

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

        this.prerenderBackground();
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

    // pre-render dell'intera mappa
    private void prerenderBackground() {
        int size = this.land.getSize();
        int imageWidth = size * TILE_SIZE;
        int imageHeight = size * TILE_SIZE;

        // immagine vuota grande quanto la mappa (ovviamente il x3 delle tile)
        this.backgroundCache = new WritableImage(imageWidth, imageHeight);
        PixelWriter pW = this.backgroundCache.getPixelWriter();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TerrainType tType = this.land.getTerrainAt(new Position(j, i));
                Color color;

                switch (tType) {
                    case WATER: color = Color.CORNFLOWERBLUE; break;
                    case SAND: color = Color.NAVAJOWHITE; break;
                    case MOUNTAIN: color = Color.SLATEGRAY; break;
                    case GRASS:
                    default: color = Color.LIGHTGREEN; break;
                }

                for (int di = 0; di < TILE_SIZE; di++) {
                    for (int dj = 0; dj < TILE_SIZE; dj++) {
                        pW.setColor(j * TILE_SIZE + dj, i * TILE_SIZE + di, color);
                    }
                }
            }
        }
        System.out.println("land pre-rendered succesfully");
    }

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

            if (this.backgroundCache != null) {
                gc.drawImage(this.backgroundCache, 0, 0);
            }

            // calcolo del quadrato della mappa attualmente visibile
            int startX = Math.max(0, (int) (cameraX / (TILE_SIZE * zoom)));
            int startY = Math.max(0, (int) (cameraY / (TILE_SIZE * zoom)));
            int endX = Math.min(land.getSize(), (int) ((cameraX + cw) / (TILE_SIZE * zoom)) + 1);
            int endY = Math.min(land.getSize(), (int) ((cameraY + ch) / (TILE_SIZE * zoom)) + 1);

            // disegno delle sole entità visibili nelle celle visibili
            EntityManager em = land.getEntityManager();
            for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
                if (em.isAlive[i]) {
                    int ax = em.posX[i];
                    int ay = em.posY[i];

                    if (ax >= startX - 5 && ax <= endX + 5 && ay >= startY - 5 && ay <= endY + 5) {
                        // Recupero dati statici visivi dall'Enum tramite l'ID del tipo
                        com.ricky.metalbox.model.ECS.EntityType type = com.ricky.metalbox.model.ECS.EntityType.values()[em.type[i]];

                        gc.setFill(type.getColor());
                        for (Position relative : type.getShape()) {
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
