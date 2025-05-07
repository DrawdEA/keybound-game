package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import lib.GameConfig;
import lib.render.Direction;

public class EarthSpell extends Spell {
    private final Color COLOR = Color.ORANGE;
    private final double WALL_OFFSET = 30;
    private final double TILE = GameConfig.TILE_SIZE;

    private double wallXLength;
    private double wallYLength;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private int wallHp;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 200;

    public EarthSpell(int casterId, double x, double y, Direction dir) {
        super("EARTH_SPELL", casterId, x, y, 25, 25);

        this.dir = dir;
        wallHp = 1;
        expired = false;

        if (dir == Direction.LEFT || dir == Direction.RIGHT){
            wallXLength = 10;
            wallYLength = 100;
        } else {
            wallXLength = 100;
            wallYLength = 10;
        }

        if (dir == Direction.LEFT) {
            this.x = x - WALL_OFFSET;
            this.y = y + TILE * 1.5 - wallYLength/2;
        } else if (dir == Direction.RIGHT) {
            this.x = x + TILE * 2 + WALL_OFFSET;
            this.y = y + TILE * 1.5 - wallYLength/2;
        } else if (dir == Direction.DOWN) {
            this.x = x + TILE * 1.75 - wallXLength/2;
            this.y = y + TILE * 1.55 + WALL_OFFSET;
        } else if (dir == Direction.UP) {
            this.x = x + TILE * 1.75 - wallXLength/2;
            this.y = y - WALL_OFFSET;
        } 
    }

    @Override
    public void update() {
        if (wallHp <= 0) {
            expired = true;
        }

        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }
    }

    @Override
    public String getDataString() {
        return String.format("EARTH_SPELL-%f-%f-%s-%d", x, y, dir.toString(), casterId);
    }

    @Override
    public void onHit() {
        // If a spell that isn't the caster hits it decreases hp by 1
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(COLOR);
        g2d.fill(new Rectangle2D.Double(x, y, wallXLength, wallYLength));
    }    
}