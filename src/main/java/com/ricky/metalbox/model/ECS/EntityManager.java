package com.ricky.metalbox.model.ECS;

import com.ricky.metalbox.model.ECS.Components.TargetComponent;
import com.ricky.metalbox.model.ECS.Components.ThinkingComponent;
import com.ricky.metalbox.model.ECS.Components.ShapeComponent;
import com.ricky.metalbox.model.ECS.Components.GraphicsComponent;
import com.ricky.metalbox.model.ECS.Components.PositionComponent;

public class EntityManager {

    public static final int MAX_ENTITIES = 50000;

    private int nextEntityId = 0;

    //serie di array paralleli in cui l'indice corrisponde all'id dell'entità
    public final PositionComponent[] positionComponents = new PositionComponent[MAX_ENTITIES];
    public final ShapeComponent[] shapeComponents = new ShapeComponent[MAX_ENTITIES];
    public final TargetComponent[] targetComponents = new TargetComponent[MAX_ENTITIES];
    public final ThinkingComponent[] thinkingComponents = new ThinkingComponent[MAX_ENTITIES];
    public final GraphicsComponent[] graphicsComponents = new GraphicsComponent[MAX_ENTITIES];

    public final boolean[] isAlive = new boolean[MAX_ENTITIES];

    public EntityManager() {
        //tutto empty
    }

    public int createEntity() {
        if (nextEntityId >= MAX_ENTITIES) {
            throw new RuntimeException("max entities limit reached");
        }
        int id = nextEntityId++; // per crearla basta muoversi avanti nell'array preallocato
        isAlive[id] = true;
        return id;
    }

    public void destroyEntity(final int id) {
        isAlive[id] = false;
        positionComponents[id] = null;
        shapeComponents[id] = null;
        targetComponents[id] = null;
        thinkingComponents[id] = null;
        graphicsComponents[id] = null;
    }
}
