package com.ricky.metalbox.controller;

import java.util.ArrayList;
import java.util.List;

import com.ricky.metalbox.system.EntitySystem;

import javafx.animation.AnimationTimer;

public class GameController implements Runnable{

    //sistemi ECS
    private final List<EntitySystem> systems;

    //thread di simulazione
    private Thread logicThread;
    private AnimationTimer renderTimer;

    private volatile boolean running = false;
    private volatile boolean paused = false;

    // definizione della velocità della logica: 30 ticks al secondo(1 tick/~33ms)
    // questo è il simulation tick indipendente
    private static final double TICK_RATE = 30.0;

    public GameController(final Runnable viewRepaintCallback) {
        this.systems = new ArrayList<>();

        this.renderTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    viewRepaintCallback.run();
                }
            }
        };
    }

    public void addSystem(final EntitySystem entitySystem) {
        this.systems.add(entitySystem);
    }

    public void start() {
        if (this.logicThread == null || !this.running) {
            this.running = true;
            this.paused = false;

            //thread dedicato solo alla logica del gioco, background
            this.logicThread = new Thread(this, "MetalBox-LogicThread");
            this.logicThread.setDaemon(true); //termina il thread quando l'app viene chiusa
            this.logicThread.start();

            this.renderTimer.start();
        } else if (this.paused) {
            this.paused = false; // ripresa dopo la pausa
        }
    }

    public void pause() {
        this.paused = true;
    }

    public boolean isRunning() {
        return this.running && !this.paused;
    }

    public void togglePause() {
        if (isRunning()) {
            pause();
        } else {
            start();
        }
    }

    // Algoritmo "Fixed Timestep"
    @Override
    public void run() {
        double timePerTick = 1_000_000_000.0 / TICK_RATE; //durata di un tick in nanosecondi
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();

            if (paused) {
                lastTime = now;
                try { Thread.sleep(10); } catch (final InterruptedException e) { e.printStackTrace(); }
                continue;
            }

            //calcolo di quanto tempo è passato dall'ultimo ciclo
            delta += (now - lastTime) / timePerTick;
            lastTime = now;

            // se delta >= 1 significa che è passato abbastanza tempo per eseguire uno o più tick
            while (delta >= 1) {
                tickLogic(); //eseguiamo la logica
                delta--;
            }

            // riposo per impedire il consumo del 100% della cpu da parte del while
            try {
                Thread.sleep(2);
            } catch (final InterruptedException e) { }
        }
    }

    // iterazione su tutti i sistemi per mantenere modularità
    private void tickLogic() {
        for (EntitySystem system : this.systems) {
            system.update();
        }
    }
}
