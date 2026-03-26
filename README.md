# METALBOX

### FASE 1: FONDAMENTA DATI E STRUTTURA

1.  Ristrutturazione della Grid del Mondo
    Azione:
    Elimina l’oggetto CellImpl. Trasforma LandImpl per usare un array
    primitivo (es. byte[] terrain = new byte[width * height]). Crea una
    classe statica o un Enum per decodificare il byte in un TerrainType.
    Questo azzererà il peso sulla memoria.

    **COMPLETATO: Abbiamo sostituito la pesante matrice di oggetti con array primitivi unidimensionali (byte[] e boolean[]), azzerando l'overhead sulla RAM. Questo approccio orientato ai dati sfrutta la cache della CPU, rendendo i calcoli spaziali ed i controlli di collisione praticamente istantanei.**

2.  Game Loop Custom e Simulation Tick Indipendente
    Azione: Sostituisci la Timeline di JavaFX con un thread separato dedicato esclusivamente alla simulazione (Game Loop). Implementa un pattern “Fixed Timestep” che aggiorna la logica (es. 60 volte al secondo) e dice a JavaFX di renderizzare solo quando c’è tempo.

    **COMPLETATO: Abbiamo isolato la fisica del gioco su un thread dedicato a 30 TPS costanti, lasciando JavaFX libero di renderizzare l'interfaccia a 60 FPS senza mai bloccarsi. Al momento è rimasto un solo blocco (synchronized) in InputController che avevo predisposto per evitare crash, da capire se si può eliminare o meno.**

### FASE 2: ARCHITETTURA PER LA SCALABILITÀ (CRUCIALE)

3.  Spatial Partitioning
    **COMPLETATO, predisposto e pronto all'uso per quando verrà implementato il friendship system, entità già registrate nei chunk**

4.  Passaggio a ECS - Entity Component System
    **COMPLETATO, manca il friendship system ma andrà cambiato del tutto quindi lo bypassiamo**

        4.1. FRUSTUM CULLING
        **COMPLETATO, aggiunta di motore grafico con telecamera per poter renderizzare solamente ciò che viene visto direttamente da essa**

        4.1. LOD ("Level of Detail")
        **COMPLETATO, ora 200000 entità causano un lag minimo e sopportabile quando il dezoom è al minimo**

        4.2. Object Pooling (Riciclo degli ID)
        **COMPLETATO, ora ogni volta che c'è una nascita da generazione l'id nel nascituro viene prima pescato, se c'è, dallo stack degli id dei morti precedentemente**

### FASE 3: OTTIMIZZAZIONE VISIVA

5.  Rendering Avanzato / Cambio Libreria Grafica
    **NON COMPLETATO (al momento NON NECESSARIO), per ora con tutte le ottimizzazioni fatte e il LOD non è necessario, inoltre non si raggiungeranno mai 100.000 o 200.000 unità contemporaneamente, nel caso in futuro si debba cambiare si passerà a LibGDX**

        5.1. Motore grafico e telecamera
        **COMPLETATO, fixato il panning per impedire allo user di perdere la mappa**

        5.2. Byte Injection dei colori in Screen Space
        **COMPLETATO**

### FASE 4: MECCANICHE DI GIOCO

6.  Entity Systems e AI
    Azione: Ora che hai ECS e Spatial Partitioning, puoi creare logiche complesse
    (fame, riproduzione, combattimento) implementandole come Sistemi isolati che elaborano
    array di componenti.
    **TODO**

7.  Pathfinding
    **COMPLETATO, per il movimento delle singole entità non ho utilizzato l'algoritmo A-star ma Campionamento locale e Decomposizione del vettore per lo sliding, per muovere le masse ho predisposto una classe Flow Fields in cui utilizzo l'algoritmo di Dijkstra (integration field e vector field)**

8.  Event System
    Azione: Crea un Event Bus (o Pattern Observer) disaccoppiato.
    Se un umano muore, non chiamare
    direttamente le funzioni, ma lancia un EntityDeathEvent.
    L’interfaccia utente o il sistema dei suoni ascolteranno
    quell’evento.
    **TODO**

### FASE 5: ESPANSIONE MASSIVA

9.  Region System & Multithreading
    **COMPLETATO, c'è divisione thread logica e thread javafx e mondo diviso in chunk 32x32 con multithreading su tutti gli spatial-chunks e Lock Ordering per evitare deadlock**

10. Save System
    Azione: Serializzare i dati. Con l’ECS, salvare una partita è banale: basta iterare su tutti gli ID delle
    entità e scrivere i loro componenti in un file JSON o binario.
    **TODO**