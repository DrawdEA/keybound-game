package lib.objects.spells;

import java.awt.Graphics2D;
import lib.objects.*;
import lib.render.Direction;

public abstract class Spell extends GameObject {
    protected final double PLAYER_X_WIDTH = 100;
    protected final double PLAYER_Y_WIDTH = 100;

    protected int casterId;
    protected Direction direction;

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

    // public double adjustToPlayerHand(double x){
        
    // }

    // Abstract update method: must be implemented by subclasses
    public abstract void update();

    // A method to check if the spell should be removed
    public abstract boolean isExpired();

    // A method to get spell data for networking
    public abstract String getDataString();

    // TODO: Collisions
    public abstract void onHit();

    public abstract void drawSprite(Graphics2D g2d);
}
