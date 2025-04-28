package lib.objects.spells;

import lib.objects.*;
import lib.render.Direction;

import java.awt.Graphics2D;

public abstract class Spell extends GameObject {
    protected int casterId;
    protected Direction direction;
    protected boolean expired = false; // For removal logic

    // Constructor for directed spells (e.g., fireball)
    public Spell(String name, int casterId, double x, double y, double width, double height, Direction direction) {
        super(name, x, y, width, height);
        this.casterId = casterId;
        this.direction = direction;
    }

    // Constructor for radial spells (e.g., explosion)
    public Spell(String name, int casterId, double x, double y, double width, double height) {
        super(name, x, y, width, height);
        this.casterId = casterId;
        this.direction = null;
    }

    // Abstract update method: must be implemented by subclasses
    public abstract void update();

    // A method to check if the spell should be removed
    public boolean isExpired() {
        return expired;
    }

    // A method to get spell data for networking
    public abstract String getDataString();

    // TODO: Collisions
    public abstract void onHit();

    @Override
    public abstract void drawSprite(Graphics2D g2d);
}
