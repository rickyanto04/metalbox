package com.ricky.metalbox.view;

import com.ricky.metalbox.model.ECS.EntityManager;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Terrain.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class GameView extends StackPane {

    // public, così l'InputController può usarle per calcolare le coordinate del mouse
    public static final int TILE_SIZE = 3;
    public static final int MAP_SIZE = 250;

    private final Land land;
    private final Canvas canvas;

    private final ScrollPane scrollPane;

    // bottoni come variabili di classe
    private final Button pauseButton;
    private final Button addHumanButton;
    private final ToggleButton addRockButton;
    private final TextField spawnCountField;
    private final Button spawnMultipleButton;

    public GameView(Land land) {
        this.land = land;
        this.canvas = new Canvas(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);

        javafx.scene.transform.Scale scaleTransform = new javafx.scene.transform.Scale(1, 1, 0, 0);
        this.canvas.getTransforms().add(scaleTransform);

        Group scrollContent = new Group(this.canvas);

        // centraggio del dezoom, stackpane intermedio
        StackPane centerPane = new StackPane(scrollContent);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setStyle("-fx-background-color: #0F5E9C;");

        // Inseriamo il Group DIRETTAMENTE nello ScrollPane
        this.scrollPane = new ScrollPane(centerPane);

        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // oceano esterno
        scrollPane.setStyle("-fx-background: #0F5E9C; -fx-background-color: #0F5E9C; -fx-border-color: transparent;");

        // logica di Zoom (CTRL + rotellina mouse) + eventfilter per zoom intermittente
        this.scrollPane.addEventFilter(javafx.scene.input.ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                event.consume(); // Fermiamo lo scroll nativo della pagina

                // Calcolo fluido e reattivo ogni singola volta che la rotellina gira
                double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
                double currentScale = scaleTransform.getX();
                double newScale = Math.max(0.5, Math.min(10.0, currentScale * zoomFactor));

                scaleTransform.setX(newScale);
                scaleTransform.setY(newScale);
            }
        });

        this.pauseButton = new Button("pause simulation");
        this.pauseButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addHumanButton = new Button("add human");
        this.addHumanButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addRockButton = new ToggleButton("build rock");
        this.addRockButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        // sezione BOTTONI TESTING
        this.spawnCountField = new TextField("100");
        this.spawnCountField.setPrefWidth(60);
        this.spawnCountField.setStyle("-fx-padding: 10px; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.spawnMultipleButton = new Button("spawn X humans");
        this.spawnMultipleButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightblue; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        HBox multiSpawnContainer = new HBox(5);
        multiSpawnContainer.setAlignment(Pos.CENTER_RIGHT);
        multiSpawnContainer.getChildren().addAll(spawnCountField, spawnMultipleButton);
        // fine sezione TESTING

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.getChildren().addAll(pauseButton, addHumanButton, multiSpawnContainer, addRockButton);
        buttonContainer.setPickOnBounds(false);

        // sfondo stackpane base
        this.setStyle("-fx-background-color: #0F5E9C;");
        this.getChildren().addAll(scrollPane, buttonContainer);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonContainer, new Insets(20));
    }

    // ---> NUOVI GETTER: Espongono i componenti per fargli agganciare gli eventi dal Controller
    public Canvas getCanvas() { return this.canvas; }
    public ScrollPane getScrollPane() { return this.scrollPane; }
    public Button getPauseButton() { return this.pauseButton; }
    public Button getAddHumanButton() { return this.addHumanButton; }
    public ToggleButton getAddRockButton() { return this.addRockButton; }


    // Questo metodo (il rendering) rimane invariato, perché è il vero e unico scopo della View!
    public void renderMap() {
        // blocco della mappa durante il disegno per evitare crash se le entità si muovono nel frattempo
        synchronized (this.land) {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            for (int y = 0; y < land.getSize(); y++) {
                for (int x = 0; x < land.getSize(); x++) {
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

            gc.setFill(Color.DARKSLATEGRAY);
            for (Obstacle o : land.getObstacles()) {
                for (Position p : o.getOccupiedPositions()) {
                    gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            EntityManager em = land.getEntityManager();
            // Per ora le dipingiamo tutte di nero (il Friendship System andrà riadattato all'ECS in futuro)
            gc.setFill(Color.BLACK);

            for (int i = 0; i < EntityManager.MAX_ENTITIES; i++) {
                if (em.isAlive[i] && em.positionComponents[i] != null && em.shapeComponents[i] != null) {

                    int anchorX = em.positionComponents[i].x;
                    int anchorY = em.positionComponents[i].y;

                    // Disegniamo ogni blocco della forma relativa
                    for (Position relative : em.shapeComponents[i].relativePositions) {
                        int drawX = anchorX + relative.getX();
                        int drawY = anchorY + relative.getY();
                        gc.fillRect(drawX * TILE_SIZE, drawY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }

    public TextField getSpawnCountField() { return this.spawnCountField; }
    public Button getSpawnMultipleButton() { return this.spawnMultipleButton; }
}
