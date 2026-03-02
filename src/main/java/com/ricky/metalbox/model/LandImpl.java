package com.ricky.metalbox.model;

public class LandImpl implements Land{
    private static final int landSize = 250;
    private final Cell[][] grid = new CellImpl[landSize][landSize];

    public LandImpl() {
        for (int i = 0; i < landSize; i++) {
            for (int j = 0; j < landSize; j++) {
                this.grid[j][i] = new CellImpl(new Position(j, i));
            }
        }
    }

    @Override
    public boolean addEntity(Entity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addEntity'");
    }

    @Override
    public boolean moveEntity(Entity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'moveEntity'");
    }

    private boolean isPositionValid(Position p) {
        return p.getX() >= 0 && p.getX() < landSize && p.getY() >= 0 && p.getY() < landSize;
    }

    /*
    Passo 3: Sviluppare addEntity(Entity e)
L'obiettivo di questo metodo in LandImpl è posizionare l'entità per la prima volta. La logica sequenziale richiede due fasi: prima la verifica, poi l'azione.

Fase di verifica: Richiedi all'entità la sua lista di getOccupiedPositions(). Controlla tramite un ciclo se ognuna di queste posizioni è valida (dentro i bordi) e se la cella corrispondente nella griglia non è già occupata. Se anche solo una cella è invalida o occupata, interrompi tutto e restituisci false.

Fase di azione: Se il ciclo di verifica termina senza problemi, esegui un secondo ciclo sulle stesse posizioni. Questa volta, prendi ogni cella della grid e chiama setOccupied(true). Restituisci true.

Passo 4: Sviluppare moveEntity
Affinché l'entità possa muoversi in futuro in modo autonomo, la mappa deve ricevere la sua destinazione. Attualmente l'interfaccia Land definisce boolean moveEntity(Entity e). Modifica la firma in Land e in LandImpl affinché prenda anche la nuova ancora desiderata: boolean moveEntity(Entity e, Position newAnchor).

La sequenza logica per un movimento sicuro è la seguente:

Liberare il vecchio spazio: Recupera le attuali posizioni occupate dall'entità (prima di spostarla) e imposta temporaneamente quelle celle su setOccupied(false) nella tua grid. Questo è vitale perché, se l'umano si sposta solo di 1 quadratino, la sua nuova forma potrebbe sovrapporsi in parte alla vecchia.

Salvare il backup: Salva l'attuale ancora dell'entità in una variabile (es. Position oldAnchor = e.getAnchorPosition();).

Provare il movimento: Cambia l'ancora dell'entità con la nuova (e.setAnchorPosition(newAnchor)). Ora l'entità "crede" di essere nella nuova posizione.

Verificare lo spazio: Esegui esattamente la stessa Fase di verifica spiegata nel Passo 3, usando le nuove posizioni dell'entità.

Confermare o Annullare:

Se lo spazio è libero: Esegui la Fase di azione (imposta le nuove celle su true) e restituisci true.

Se lo spazio è bloccato (ostacolo/bordo): Il movimento non è valido. Ripristina la vecchia ancora dell'entità (e.setAnchorPosition(oldAnchor)), ripristina le vecchie celle su true e restituisci false.

Questa struttura garantisce che, quando in futuro programmerai l'intelligenza artificiale per il movimento casuale, le entità non si incastreranno mai tra loro e non usciranno mai dalla mappa, gestendo autonomamente la propria complessa maschera di collisione. */

}
