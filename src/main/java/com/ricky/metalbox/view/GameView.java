package com.ricky.metalbox.view;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Cell.TerrainType;
import com.ricky.metalbox.model.Utilities.Position;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class GameView extends StackPane {

    // public, così l'InputController può usarle per calcolare le coordinate del mouse
    public static final int TILE_SIZE = 3;
    public static final int MAP_SIZE = 500;

    private final Land land;
    private final Canvas canvas;

    // bottoni come variabili di classe
    private final Button pauseButton;
    private final Button addHumanButton;
    private final ToggleButton addRockButton;

    public GameView(Land land) {
        this.land = land;
        this.canvas = new Canvas(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);

        // canvas nel group per consentire lo zoom
        Group canvasGroup = new Group(this.canvas);

        // aggiunta del group nello scrollpane navigabile
        ScrollPane scrollPane = new ScrollPane(canvasGroup);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPannable(false); // false per non interferire col disegno

        // logica di Zoom (CTRL + rotellina mouse)
        scrollPane.setOnScroll(event -> {
            if (event.isControlDown()) { // zoom solo con CTRL premuto
                event.consume(); // blocco dello scorrimento standard della pagina

                //calcolo del fattore di zoom
                double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;

                //applichiamo il factor al group
                canvasGroup.setScaleX(canvasGroup.getScaleX() * zoomFactor);
                canvasGroup.setScaleY(canvasGroup.getScaleY() * zoomFactor);
            }
        });

        this.pauseButton = new Button("pause simulation");
        this.pauseButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addHumanButton = new Button("add human");
        this.addHumanButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        this.addRockButton = new ToggleButton("build rock");
        this.addRockButton.setStyle("-fx-opacity: 0.8; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.getChildren().addAll(pauseButton, addHumanButton, addRockButton);
        buttonContainer.setPickOnBounds(false);

        this.getChildren().addAll(scrollPane, buttonContainer);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonContainer, new Insets(20));
    }

    // ---> NUOVI GETTER: Espongono i componenti per fargli agganciare gli eventi dal Controller
    public Canvas getCanvas() { return this.canvas; }
    public Button getPauseButton() { return this.pauseButton; }
    public Button getAddHumanButton() { return this.addHumanButton; }
    public ToggleButton getAddRockButton() { return this.addRockButton; }


    // Questo metodo (il rendering) rimane invariato, perché è il vero e unico scopo della View!
    public void renderMap() {
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

        for (Entity e : land.getEntities()) {
            if (!e.getFriends().isEmpty()) {
                gc.setFill(Color.PINK);
            } else {
                gc.setFill(Color.BLACK);
            }
            for (Position p : e.getOccupiedPositions()) {
                gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }
}
