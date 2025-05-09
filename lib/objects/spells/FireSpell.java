package lib.objects.spells;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import lib.render.CollisionManager;
import lib.GameConfig;
import lib.render.Direction;
import lib.render.PlayerObject;

public class FireSpell extends Spell {
    // Notice that SPEED * maxAgeInTicks is the distance the fireball can travel
    private final double SPEED = 15;
    private final Color COLOR = Color.RED;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private int width = 25, height = 25;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 30;

    private int animationCounter;

    private Rectangle hitbox;
    private boolean firstTime = true;

    public FireSpell(int casterId, double x, double y, Direction dir) {
        super("FIRE_SPELL", casterId, x, y, 25, 25, dir);

        this.dir = dir;
        expired = false;
        
        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE - 25/2;
            this.y = y;
        } else if (dir == Direction.DOWN){
            this.x = x + TILE - 25/2;
            this.y = y + TILE;
        } else if (dir == Direction.LEFT){
            this.x = x;
            this.y = y + TILE * 0.75 - 25/2;
        } else if (dir == Direction.RIGHT){
            this.x = x + TILE * 2 - 25/2;
            this.y = y + TILE * 0.75 - 25/2;
        }

        animationCounter = 0;
    }

    public String getDataString() {
        String spellString = "";
        spellString += String.format("FIRE_SPELL-%f-%f-%s", x, y, dir.toString());
        return spellString; 
    }

    public void update() {
        // Move the fire ball in the direction the player is facing
        if (dir == Direction.UP){
            y -= SPEED;
        } else if (dir == Direction.DOWN) {
            y += SPEED;
        } else if (dir == Direction.LEFT) {
            x -= SPEED;
        } else if (dir == Direction.RIGHT) {
            x += SPEED;
        }

        // Set the spell to expire if it's too old
        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        
        PlayerObject playerHit = cm.checkProjectileCollision(hitbox);
        if (playerHit != null) {
            System.out.println("FIRE SPELL HIT!");
            playerHit.damagePlayer(1);
        }

        // Remove the firespell.
        expired = true;
    }

    
    @Override
    public void drawSprite(Graphics2D g2d) {
        if (firstTime) {
            System.out.println(firstTime);
            firstTime = false;
        }
        System.out.println(animationCounter);
        animationCounter++;
        g2d.setColor(COLOR);
        hitbox = new Rectangle((int) x, (int) y, width, height);
        g2d.fill(hitbox);
    }
}