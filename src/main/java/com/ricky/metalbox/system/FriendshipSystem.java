package com.ricky.metalbox.system;

import com.ricky.metalbox.model.Land.Land;

public class FriendshipSystem implements EntitySystem{

    private final Land land;

    public FriendshipSystem(final Land land) {
        this.land = land;
    }

    public void update() {
        // DA IMPLEMENTARE IN FUTURO UTILIZZANDO ECS(FriendshipComponent)
        /*
        // per ogni entità controlliamo se nel 10x10 attorno al suo anchor sono presenti altre entità
        // se non c'è alcuna entità si va avanti
        // se le entità attorno rimangono vicine alla centrale per più di 10 secondi allora diventano amiche
        // altrimenti si va avanti
        // le entità con amici e le entità amiche diventano rosa

        for (final Entity e1 : this.land.getEntities()) {
            this.proximityTimers.putIfAbsent(e1, new HashMap<>());
            Map<Entity, Integer> timersForE1 = this.proximityTimers.get(e1);

            Position e1Anchor = e1.getAnchorPosition();

            // richiesta delle entità in un raggio di 5 celle.
            // il sistema scarta in automatico il 99% della mappa senza nemmeno controllarlo
            List<Entity> nearbyCandidates = this.land.getEntitiesNear(e1Anchor, 5);

            for (final Entity e2 : nearbyCandidates) {
                if (e1 == e2) continue;

                Position e2Anchor = e2.getAnchorPosition();

                int distanceX = Math.abs(e1Anchor.getX() - e2Anchor.getX());
                int distanceY = Math.abs(e1Anchor.getY() - e2Anchor.getY());

                if (distanceX <= 5 && distanceY <= 5) {
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

            //rimozione delle entità troppo lontane che non cadono nemmeno nelle nearbycandidates
            timersForE1.keySet().removeIf(e2 -> !nearbyCandidates.contains(e2));
        }
            */
    }
}
