package com.cs.finalproject.core.blocks;

import com.badlogic.gdx.math.Vector3;

public abstract class Block {
    private Vector3 position;
    private byte size;
    private BlockType type;

    public Block(Vector3 position, byte size,  BlockType type) {
        this.position = position;
        this.size = size;
        this.type = type;
    }

    public Block() {

    }

    public Vector3[] getVertices() {
        // ideally dont use float?
        // not sure if something can be done, ideally want to use smaller primitives.

        float half = size / 2f;

        return new Vector3[]{
            new Vector3(position.x - half, position.y - half, position.z - half),
            new Vector3(position.x - half, position.y - half, position.z + half),
            new Vector3(position.x - half, position.y + half, position.z - half),
            new Vector3(position.x - half, position.y + half, position.z + half),

            new Vector3(position.x + half, position.y - half, position.z - half),
            new Vector3(position.x + half, position.y - half, position.z + half),
            new Vector3(position.x + half, position.y + half, position.z - half),
            new Vector3(position.x + half, position.y + half, position.z + half)
        };
    }
}
