# METALBOX

cose da ottimizzare:
    0. al momento la LandImpl ovvero il terreno è composto da un array primitivo di Celle che sono oggetti
    1. game loop
    2. grid del mondo
    3. tile system
    4. rendering base
    5. ECS
    6. entity systems (movement, hunger, ecc.)
    7. AI semplice
    8. pathfinding
    9. spatial partitioning
    10. simulation tick
    11. event system
    12. region system
    13. multithreading
    14. save system

FASE 1: FONDAMENTA DATI E STRUTTURA

1.  Ristrutturazione della Grid del Mondo
    Azione:
    Elimina l’oggetto CellImpl. Trasforma LandImpl per usare un array
    primitivo (es. byte[] terrain = new byte[width * height]). Crea una
    classe statica o un Enum per decodificare il byte in un TerrainType.
    Questo azzererà il peso sulla memoria.

    **COMPLETATO: Abbiamo sostituito la pesante matrice di oggetti con array primitivi unidimensionali (byte[] e boolean[]), azzerando l'overhead sulla RAM. Questo approccio orientato ai dati sfrutta la cache della CPU, rendendo i calcoli spaziali ed i controlli di collisione praticamente istantanei.**

2.  Game Loop Custom e Simulation Tick Indipendente
    Azione: Sostituisci la Timeline di JavaFX con un thread separato dedicato esclusivamente alla simulazione (Game Loop). Implementa un pattern “Fixed Timestep” che aggiorna la logica (es. 60 volte al secondo) e dice a JavaFX di renderizzare solo quando c’è tempo.

    **COMPLETATO: Abbiamo isolato la fisica del gioco su un thread dedicato a 30 TPS costanti, lasciando JavaFX libero di renderizzare l'interfaccia a 60 FPS senza mai bloccarsi. Per evitare crash o conflitti tra i due processi, abbiamo blindato l'accesso alla mappa tramite blocchi di sincronizzazione (synchronized).**

FASE 2: ARCHITETTURA PER LA SCALABILITÀ (CRUCIALE)

3.  Spatial Partitioning
    Azione: Prima di aggiungere nuove
    logiche, risolvi il problema O(N²) nel FriendshipController.
    Implementa una griglia spaziale (Spatial Hash Grid) o un QuadTree.
    Le entità devono registrarsi nella “regione” in cui si trovano.
    Quando un umano cerca amici vicini, dovrà controllare solo le entità
    registrate nella sua stessa cella spaziale o in quelle adiacenti.

    **COMPLETATO, ora i controlli nel caso di 500 entità sono scesi da 250000 a 2500, 10000% di boost di prestazioni**
    **TENERE A MENTE, max_entities è settato a 50000 ma tiene conto anche delle rocce, non solo degli umani, conta le entità totali; quando viene tirata eccezione infatti non sarà più possibile creare rocce**

4.  Passaggio a ECS - Entity Component System
    Azione:
    Abbandona AbstractEntity. Un’entità non dovrebbe essere un oggetto,
    ma solo un ID (un intero). I “Componenti” sono solo dati (es.
    PositionComponent, HealthComponent), e i “Sistemi” contengono la
    logica (es. MovementSystem, RenderSystem). Questo migliorerà
    drasticamente le performance (grazie alla linearità in memoria) e
    renderà facilissimo aggiungere nuovi comportamenti.

    **COMPLETATO, manca il friendship system ma andrà cambiato del tutto quindi lo bypassiamo**

    4.1. FRUSTUM CULLING

    **COMPLETATO, aggiunta di motore grafico con telecamera per poter renderizzare solamente ciò che viene visto direttamente da essa**
    **TENERE A MENTE, quando si vede la mappa intera fino a metà il lag c'è (mappa 1000x1000 con chunk 32x32) con zoom più importanti di metà mappa non ha problemi**

FASE 3: OTTIMIZZAZIONE VISIVA

5.  Rendering Avanzato / Cambio Libreria Grafica
    Azione:
    Ottimizza il rendering. Invece di ridisegnare tutta la mappa,
    disegna la mappa statica su un’immagine in memoria (Buffer) una sola
    volta e aggiorna (Dirty Rectangles) solo le parti di mappa
    modificate (es. se piazzi un sasso). Disegna le entità dinamiche
    sopra questo livello.

Consiglio Critico: Valuta ora se abbandonare JavaFX. JavaFX è eccellente
per le UI desktop, ma per disegnare decine di migliaia di pixel a 60 FPS
in un sandbox, librerie basate su OpenGL come LibGDX (Java) ti
offriranno performance imparagonabili, pur rimanendo nell’ecosistema
Java.

FASE 4: MECCANICHE DI GIOCO

6.  Entity Systems e AI
    Azione: Ora che hai ECS e Spatial Partitioning, puoi creare logiche complesse
    (fame, riproduzione, combattimento) implementandole come Sistemi isolati che elaborano
    array di componenti.

7.  Pathfinding
    Azione: Sostituisci il movimento casuale del
    MovementController con l’algoritmo A* (A-Star). Se gestirai folle
    immense dirette verso lo stesso punto, valuta algoritmi basati su
    Flow Fields / Vector Fields.

8.  Event System
    Azione: Crea un Event Bus (o Pattern Observer) disaccoppiato.
    Se un umano muore, non chiamare
    direttamente le funzioni, ma lancia un EntityDeathEvent.
    L’interfaccia utente o il sistema dei suoni ascolteranno
    quell’evento.

FASE 5: ESPANSIONE MASSIVA

9.  Region System & Multithreading
    Azione: Dividi la
    mappa in “Chunk” (es. 32x32). Il multithreading in un gioco ECS è
    potente se aggiorni chunk diversi in thread separati, a patto di
    gestire le entità che attraversano i confini dei chunk.

10. Save System
    Azione: Serializzare i dati. Con l’ECS,
    salvare una partita è banale: basta iterare su tutti gli ID delle
    entità e scrivere i loro componenti in un file JSON o binario.