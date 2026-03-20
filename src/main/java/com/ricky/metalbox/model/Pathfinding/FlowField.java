package com.ricky.metalbox.model.Pathfinding;

import com.ricky.metalbox.model.Land.Land;
import java.util.Arrays;

/**
 * struttura DOD per gestire il movimento(pathfinding massivo) di decine/centinaia di migliaia di entità
 */
public class FlowField {
    private final Land land;
    private final int size;
    private final int totalCells;

    // DOD arrays per massima efficienza in memoria
    private final byte[] costField;
    private final int[] integrationField;

    // i vettori risultanti che le entità leggeranno (valori da -1 a 1)
    public final byte[] vectorX;
    public final byte[] vectorY;

    private static final int MAX_COST = 65535; // rappresenta l'infinito/invalicabile

    public FlowField(Land land) {
        this.land = land;
        this.size = land.getSize();
        this.totalCells = size * size;

        this.costField = new byte[totalCells];
        this.integrationField = new int[totalCells];
        this.vectorX = new byte[totalCells];
        this.vectorY = new byte[totalCells];
    }

    /**
     * helper, conversione 2d a 1d
     */
    private int getIndex(int x, int y) {
        return (y * size) + x;
    }

    /**
     * 1: Mappatura dei costi (da chiamare quando si costruiscono/distruggono ostacoli)
     */
    public void updateCostField() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int index = getIndex(x, y);
                // 255 = Muro impenetrabile, 1 = Pianura
                costField[index] = land.isCellFree(x, y) ? (byte) 1 : (byte) 255;
            }
        }
    }

    /**
     * 2 + 3: calcola il campo vettoriale verso una coordinata bersaglio,
     * andrà chiamato in un thread separato (async) per non bloccare i TPS
     */
    public void generate(int targetX, int targetY) {
        // reset Integration Field
        Arrays.fill(integrationField, MAX_COST);
        Arrays.fill(vectorX, (byte) 0);
        Arrays.fill(vectorY, (byte) 0);

        int targetIndex = getIndex(targetX, targetY);
        if (targetIndex < 0 || targetIndex >= totalCells) return;

        integrationField[targetIndex] = 0;

        // Algoritmo di Dijkstra
        int[] queue = new int[totalCells];
        int head = 0, tail = 0;

        queue[tail++] = targetIndex;

        // offset cardinali per l'espansione
        int[] cardinalOffsets = {-size, size, -1, 1};

        // CALCOLO INTEGRATION FIELD
        while (head < tail) {
            int currentIdx = queue[head++];
            int cx = currentIdx % size; // Coordinata X attuale

            for (int offset : cardinalOffsets) {
                // evitiamo wrap-around orizzontale usando cx (NIENTE DIVISIONI)
                if (offset == -1 && cx == 0) continue; // Muro invisibile a sinistra
                if (offset == 1 && cx == size - 1) continue; // Muro invisibile a destra

                int neighborIdx = currentIdx + offset;

                // evitiamo wrap-around verticale (fuori dall'array)
                if (neighborIdx < 0 || neighborIdx >= totalCells) continue;

                int cost = costField[neighborIdx] & 0xFF; // Unsigned byte
                if (cost == 255) continue; // Ostacolo insormontabile

                int newCost = integrationField[currentIdx] + cost;
                if (newCost < integrationField[neighborIdx]) {
                    integrationField[neighborIdx] = newCost;
                    queue[tail++] = neighborIdx;
                }
            }
        }

        // CALCOLO VECTOR FIELD (Gradiente Discreto)
        int[] allOffsets = {-size-1, -size, -size+1, -1, 1, size-1, size, size+1};

        for (int i = 0; i < totalCells; i++) {
            if ((costField[i] & 0xFF) == 255) continue; // un muro non ha direzione

            int minCost = integrationField[i];
            byte bestDx = 0;
            byte bestDy = 0;
            int cx = i % size;
            int cy = i / size;

            for (int offset : allOffsets) {
                int nIdx = i + offset;
                if (nIdx < 0 || nIdx >= totalCells) continue;

                int nx = nIdx % size;
                int ny = nIdx / size;
                if (Math.abs(cx - nx) > 1 || Math.abs(cy - ny) > 1) continue;

                if (integrationField[nIdx] < minCost) {
                    minCost = integrationField[nIdx];
                    bestDx = (byte) (nx - cx);
                    bestDy = (byte) (ny - cy);
                }
            }
            vectorX[i] = bestDx;
            vectorY[i] = bestDy;
        }
    }

    // getter veloci per muovere le masse
    public byte getVectorX(int x, int y) { return vectorX[getIndex(x, y)]; }
    public byte getVectorY(int x, int y) { return vectorY[getIndex(x, y)]; }
}
