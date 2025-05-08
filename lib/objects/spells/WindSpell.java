package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;

import lib.render.CollisionManager;
import java.awt.geom.Ellipse2D;
import lib.GameConfig;
import lib.render.Direction;

public class WindSpell extends Spell {
    // Notice though that the final size of the spell is SPEED * maxAgeInTicks 
    private final double SPEED = 20;
    private final Color COLOR = Color.GRAY;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 15;

    public WindSpell(int casterId, double x, double y, Direction dir) {
        super("WIND_SPELL", casterId, x, y, 25, 25);

        this.x = x;
        this.y = y;

        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE;
            this.y = y + TILE * 1.5;
        } else if (dir == Direction.DOWN){
            this.x = x + TILE;
            this.y = y - TILE * 2.5;
        } else if (dir == Direction.LEFT){
            this.x = x + TILE * 2;
            this.y = y;
        } else if (dir == Direction.RIGHT){
            this.x = x - TILE * 2;
            this.y = y;
        }

        this.dir = dir;
        expired = false;
    }

    @Override
    public void update() {
        if (dir == Direction.UP){
            y -= SPEED;
        } else if (dir == Direction.DOWN){
            y += SPEED;
        } else if (dir == Direction.LEFT){
            x -= SPEED;
        } else if (dir == Direction.RIGHT){
            x += SPEED;
        }

        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }
    }

    @Override
    public String getDataString() {
        return String.format("WIND_SPELL-%f-%f-%s-%d", x, y, dir.toString(), casterId);
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(COLOR);
        if (dir == Direction.DOWN || dir == Direction.UP){
            g2d.fill(new Ellipse2D.Double(x, y, TILE * 2, TILE * 4));
        } else {
            g2d.fill(new Ellipse2D.Double(x, y, TILE * 4, TILE * 2));
        }
    }    
}