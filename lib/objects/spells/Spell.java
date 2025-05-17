package lib.objects.spells;

import java.awt.Graphics2D;
import lib.GameConfig;
import lib.objects.*;
import lib.render.CollisionManager;
import lib.render.Direction;

public abstract class Spell extends GameObject {
    protected final double PLAYER_X_WIDTH = GameConfig.TILE_SIZE * 4;
    protected final double PLAYER_Y_WIDTH = GameConfig.TILE_SIZE * 2;

    protected int casterId;
    protected Direction direction;

    // Constructor for directed spells (e.g., fireball)
    public Spell(String name, int casterId, double x, double y, double width, double height, Direction direction) {
        super(name, x, y, width, height);

        this.x = x;
        this.y = y;

        this.casterId = casterId;
        this.direction = direction;
    }

    // Constructor for radial spells (e.g., explosion)
    public Spell(String name, int casterId, double x, double y, double width, double height) {
        super(name, x, y, width, height);
        this.casterId = casterId;
        this.direction = null;
    }

    public int getCasterId() {
        return casterId;
    }

    // Abstract update method: must be implemented by subclasses
    public abstract void update();

    // A method to check if the spell should be removed
    public abstract boolean isExpired();

    // A method to get spell data for networking
    public abstract String getDataString();

    public abstract int handleCollisions(CollisionManager cm);

    public abstract void drawSprite(Graphics2D g2d);
}
