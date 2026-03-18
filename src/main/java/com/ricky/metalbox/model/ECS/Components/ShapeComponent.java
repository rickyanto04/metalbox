package com.ricky.metalbox.model.ECS.Components;

import java.util.List;
import com.ricky.metalbox.model.Utilities.Position;

public class ShapeComponent {
    public List<Position> relativePositions;

    public ShapeComponent(List<Position> shape) {
        this.relativePositions = shape;
    }
}
