package com.ricky.metalbox.view;

import java.util.Random;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Entity.Human;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Obstacle.Obstacle;
import com.ricky.metalbox.model.Obstacle.ObstacleImpl;
import com.ricky.metalbox.model.Utilities.Position;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class GameView extends StackPane{
    private static final int TILE_SIZE = 3; // Dimensione in pixel di ogni cella
    private static final int MAP_SIZE = 250; // In base alla grandezza definita nel tuo LandImpl

    private final Land land;
    private final Canvas canvas;
    private final Button pauseButton;

    public GameView(Land land) {
        this.land = land;
        this.canvas = new Canvas(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);

        // Creazione del bottone per aggiungere le entità
        Button addEntityButton = createAddEntityButton();
        Button addBoulderButton = createAddObstacleButton();

        this.pauseButton = new Button("pause simulation");
        this.pauseButton.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: #ffcccc; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        // Creiamo un contenitore verticale per impilare i due bottoni
        VBox buttonContainer = new VBox(10); // 10 è lo spazio (gap) tra i bottoni
        buttonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        buttonContainer.getChildren().addAll(pauseButton, addEntityButton, addBoulderButton);

        // Aggiungiamo Canvas e VBox al layout principale
        this.getChildren().addAll(canvas, buttonContainer);

        // Posizioniamo il contenitore in basso a destra con un po' di margine
        StackPane.setAlignment(buttonContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonContainer, new Insets(20));
    }

    public Button getPauseButton() {
        return this.pauseButton;
    }

    private Button createAddEntityButton() {
        Button btnAddEntity = new Button("add human");

        // Stile CSS integrato per renderlo leggermente trasparente e visivamente gradevole
        btnAddEntity.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        btnAddEntity.setOnAction(event -> {
            Random rand = new Random();
            // Genera coordinate casuali per l'ancora, tenendosi lontano dai bordi
            int startX = rand.nextInt(200) + 20;
            int startY = rand.nextInt(200) + 20;

            // Crea un nuovo umano e prova ad aggiungerlo alla mappa
            Human newHuman = new Human(new Position(startX, startY));
            land.addEntity(newHuman);
        });

        return btnAddEntity;
    }

    private Button createAddObstacleButton() {
        Button btnAddObstacle = new Button("add boulder");

        // Stile CSS integrato per renderlo leggermente trasparente e visivamente gradevole
        btnAddObstacle.setStyle("-fx-opacity: 0.7; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-background-color: red; -fx-border-color: black; -fx-border-radius: 3px; -fx-background-radius: 3px;");

        btnAddObstacle.setOnAction(event -> {
            Random rand = new Random();
            // Genera coordinate casuali per l'ancora, tenendosi lontano dai bordi
            int startX = rand.nextInt(200) + 20;
            int startY = rand.nextInt(200) + 20;

            // Crea un nuovo ostacolo e prova ad aggiungerlo alla mappa
            Obstacle newBoulder = new ObstacleImpl(new Position(startX, startY));
            land.addObstacle(newBoulder);
        });

        return btnAddObstacle;
    }

    // Questo metodo verrà chiamato dal GameController ad ogni "tick"
    public void renderMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Sfondo verde per rappresentare la pianura
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderEntities(gc);
        renderObstacles(gc);
    }

    private void renderEntities(final GraphicsContext gc) {
        // 2. Disegna tutte le entità in nero leggendo le posizioni occupate
        gc.setFill(Color.BLACK);
        for (Entity e : land.getEntities()) {

            if (!e.getFriends().isEmpty()) {
                gc.setFill(Color.PINK);
            } else {
                gc.setFill(Color.BLACK);
            }

            for (Position p : e.getOccupiedPositions()) {
                // Moltiplichiamo le coordinate del modello per la TILE_SIZE per avere i pixel a schermo
                gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void renderObstacles(final GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        for (Obstacle o : land.getObstacles()) {
            gc.setFill(Color.BLUE);

            for (Position p : o.getOccupiedPositions()) {
                // Moltiplichiamo le coordinate del modello per la TILE_SIZE per avere i pixel a schermo
                gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

}
