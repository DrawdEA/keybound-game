package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import lib.render.Direction;
public class WaterSpell extends Spell {
    public final double SPEED = 20;
    public final Color COLOR = Color.CYAN;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private double endingBar;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 20;

    // Initial Creation
    public WaterSpell(int casterId, double x, double y, Direction dir) {
        super("WATER_SPELL", casterId, x, y, 25, 25, dir);

        this.x = x;
        this.y = y;
        this.dir = dir;

        if (dir == Direction.LEFT || dir == Direction.RIGHT){
            endingBar = x;
        } else if (dir == Direction.UP || dir == Direction.DOWN) {
            endingBar = y;
        }

        expired = false;
    }

    // Static Rendering
    public WaterSpell(int casterId, double x, double y, Direction dir, double endingBar) {
        super("WATER_SPELL", casterId, x, y, 25, 25, dir);

        this.x = x;
        this.y = y;
        this.dir = dir;
        this.endingBar = endingBar;

        expired = false;
    }

    @Override
    public void update() {
        if (dir == Direction.RIGHT || dir == Direction.DOWN){
            endingBar += SPEED;
        } else if (dir == Direction.LEFT || dir == Direction.UP){
            endingBar -= SPEED;
        }

        currAgeInTicks++;

        if (currAgeInTicks >= maxAgeInTicks){
            expired = true;
        }
    }

    @Override
    public String getDataString() {
        String spellString = "";
        spellString += String.format("WATER_SPELL-%f-%f-%s-%f", x, y, dir.toString(), endingBar);
        return spellString;
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

        g2d.fill(new Rectangle2D.Double(endingBar, y, width, height));
        
        if (dir == Direction.LEFT || dir == Direction.RIGHT){
            double yDiff = (endingBar-x)/Math.sqrt(3);
            g2d.fill(new Polygon(
                new int[]{
                    (int) x, 
                    (int) endingBar, 
                    (int) endingBar
                }, new int[]{
                    (int) y, 
                    (int) (y - yDiff), 
                    (int) (y + yDiff)
                }, 3));
        } else if (dir == Direction.UP || dir == Direction.DOWN) {
            double xDiff = (endingBar-y)/Math.sqrt(3);
            g2d.fill(new Polygon(
                new int[]{
                    (int) x, 
                    (int) (x + xDiff), 
                    (int) (x - xDiff)
                }, new int[]{
                    (int) y, 
                    (int) endingBar, 
                    (int) endingBar
                }, 3));
        }
        
    }
}