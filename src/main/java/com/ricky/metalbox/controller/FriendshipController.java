package com.ricky.metalbox.controller;

import java.util.HashMap;
import java.util.Map;

import com.ricky.metalbox.model.Entity.Entity;
import com.ricky.metalbox.model.Land.Land;
import com.ricky.metalbox.model.Utilities.Position;

public class FriendshipController {

    private final Land land;
    //mappa dei timer
    private final Map<Entity, Map<Entity, Integer>> proximityTimers;

    public FriendshipController(final Land land) {
        this.land = land;
        this.proximityTimers = new HashMap<>();
    }

    public void updateFriendships() {
        // per ogni entità controlliamo se nel 10x10 attorno al suo anchor sono presenti altre entità
        // se non c'è alcuna entità si va avanti
        // se le entità attorno rimangono vicine alla centrale per più di 10 secondi allora diventano amiche
        // altrimenti si va avanti
        // le entità con amici e le entità amiche diventano rosa

        for (final Entity e1 : this.land.getEntities()) {
            this.proximityTimers.putIfAbsent(e1, new HashMap<>());
            Map<Entity, Integer> timersForE1 = this.proximityTimers.get(e1);

            Position e1Anchor = e1.getAnchorPosition();

            for (final Entity e2 : this.land.getEntities()) {
                if (e1 == e2) continue; // salto l'entità stessa

                Position e2Anchor = e2.getAnchorPosition();
                boolean isNear = false;

                //per il 10x10 attorno ad e1 calcoliamo la distanza assoluta
                //tra le x e le y dei due punti che siano quindi minori o = a 5
                int distanceX = Math.abs(e1Anchor.getX() - e2Anchor.getX());
                int distanceY = Math.abs(e1Anchor.getY() - e2Anchor.getY());

                if (distanceX <= 5 && distanceY <= 5) {
                    isNear = true;
                }

                //se sono vicini allora aggiungo ai frame passati +1 se sono ancora vicini attualmente
                //altrimenti rimuovo la vicinanza totalmente
                if (isNear) {
                    int ticks = timersForE1.getOrDefault(e2, 0) + 1;
                    timersForE1.put(e2, ticks);

                    if (ticks == 50) {
                        e1.addFriend(e2);
                        e2.addFriend(e1);

                        System.out.println("new friendship");
                    }

                } else {
                    timersForE1.remove(e2);
                }
            }
        }
    }
}
