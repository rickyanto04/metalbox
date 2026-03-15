package com.ricky.metalbox.view;

import java.util.Random;

import com.ricky.metalbox.model.Cell.TerrainType;
import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Entity.Human;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Obstacle.Rock;
import com.ricky.metalbox.model.Utilities.Position;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class GameView extends StackPane {
    private static final int TILE_SIZE = 3;
    private static final int MAP_SIZE = 250;

    private final Land land;
    private final Canvas canvas;
    private final Button pauseButton;

    public GameView(Land land) {
        this.land = land;
        this.canvas = new Canvas(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);

        Button addButton = createAddEntityButton();
        Button addRockButton = createAddObstacleButton();

        this.pauseButton = new Button("pause simulation");
        this.pauseButton.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.getChildren().addAll(pauseButton, addButton, addRockButton);

        this.getChildren().addAll(canvas, buttonContainer);

        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonContainer, new Insets(20));
    }

    public Button getPauseButton() {
        return this.pauseButton;
    }

    private Button createAddEntityButton() {
        Button btnAdd = new Button("add human");
        btnAdd.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        btnAdd.setOnAction(event -> {
            Random rand = new Random();
            int startX, startY;
            Position spawnPos;

            // Ciclo finché non trova una cella libera (ora non spawnerà nell'acqua!)
            do {
                startX = rand.nextInt(200) + 20;
                startY = rand.nextInt(200) + 20;
                spawnPos = new Position(startX, startY);
            } while (!land.isCellFree(spawnPos));

            land.addEntity(new Human(spawnPos));
        });

        return btnAdd;
    }

    private Button createAddObstacleButton() {
        Button btnAdd = new Button("add rock");
        btnAdd.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: lightgray; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        btnAdd.setOnAction(event -> {
            Random rand = new Random();
            int startX, startY;
            Position spawnPos;

            do {
                startX = rand.nextInt(200) + 20;
                startY = rand.nextInt(200) + 20;
                spawnPos = new Position(startX, startY);
            } while (!land.isCellFree(spawnPos));

            land.addObstacle(new Rock(spawnPos));
        });

        return btnAdd;
    }

    // Questo metodo verrà chiamato dal GameController ad ogni "tick"
    public void renderMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. DISEGNA I BIOMI DELLA MAPPA (Pixel per Pixel in base al TerrainType)
        for (int y = 0; y < land.getSize(); y++) {
            for (int x = 0; x < land.getSize(); x++) {
                TerrainType type = land.getTerrainAt(new Position(x, y));

                // Seleziona il colore giusto!
                switch (type) {
                    case WATER: gc.setFill(Color.CORNFLOWERBLUE); break;
                    case SAND: gc.setFill(Color.NAVAJOWHITE); break;
                    case MOUNTAIN: gc.setFill(Color.SLATEGRAY); break;
                    case GRASS:
                    default: gc.setFill(Color.LIGHTGREEN); break;
                }

                // Disegna la cella
                gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // ---> 2. DISEGNA GLI OSTACOLI (Rocce grigie)
        gc.setFill(Color.DARKSLATEGRAY);
        for (Obstacle o : land.getObstacles()) {
            for (Position p : o.getOccupiedPositions()) {
                gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // 3. DISEGNA LE ENTITA' (Umani Neri o Rosa)
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
