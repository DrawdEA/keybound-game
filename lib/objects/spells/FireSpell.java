package lib.objects.spells;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import lib.render.CollisionManager;
import lib.render.Direction;
import lib.render.PlayerObject;

public class FireSpell extends Spell {
    // DEFAULT CONSTANTS
    public final int SPEED = 15;
    public final Color COLOR = Color.RED;

    private double x, y;
    private int width = 25, height = 25;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 30;

    private Rectangle hitbox;

    public FireSpell(int casterId, double x, double y, Direction dir) {
        super("FIRE_SPELL", casterId, x, y, 25, 25, dir);
        
        this.x = x;
        this.y = y;

        this.dir = dir;
        expired = false;
    }

    public String getDataString() {
        String spellString = "";
        spellString += String.format("FIRE_SPELL-%f-%f-%s", x, y, dir.toString());
        return spellString; 
    }

    public void update() {
        if (dir == Direction.UP){
            y -= SPEED;
        } else if (dir == Direction.DOWN) {
            y += SPEED;
        } else if (dir == Direction.LEFT) {
            x -= SPEED;
        } else if (dir == Direction.RIGHT) {
            x += SPEED;
        }

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
        g2d.setColor(COLOR);
        hitbox = new Rectangle((int) x, (int) y, width, height);
        g2d.fill(hitbox);
    }
}