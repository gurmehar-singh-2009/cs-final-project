package com.cs.finalproject.core.world.entities;

import com.badlogic.gdx.math.Vector3;

/**
 * Base entity class.
 */
public abstract class Entity {
    private Vector3 position;
    private Vector3 velocity;
    private EntityType type;

    /**
     * Gets the type of entity.
     * @return The entity type ({@link EntityType}).
     */
    public EntityType getType() {
        return type;
    }

    /**
     * Gets the entity position.
     * @return The position ({@link Vector3}).
     */
    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
}
