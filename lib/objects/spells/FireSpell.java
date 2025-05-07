package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import lib.GameConfig;
import lib.render.Direction;

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
        } else if (dir == Direction.DOWN){
            y += SPEED;
        } else if (dir == Direction.LEFT){
            x -= SPEED;
        } else if (dir == Direction.RIGHT){
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
    public void onHit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(COLOR);
        g2d.fill(new Rectangle2D.Double(x, y, width, height));
    } 

}