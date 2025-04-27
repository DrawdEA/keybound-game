package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import lib.objects.*;
import lib.render.Direction;

public class FireSpell extends GameObject {
    // DEFAULT CONSTANTS
    public final int SPEED = 20;
    public final Color COLOR = Color.RED;

    double x, y;
    int width = 25, height = 25;
    Direction directionPointing;

    public FireSpell(double x, double y, Direction directionPointing) {
        super("FIRE_SPELL", x, y, 25, 25);
        
        this.x = x;
        this.y = y;

        this.directionPointing = directionPointing;
    }

    public void update() {
        if (directionPointing == Direction.UP){
            y -= SPEED;
        } else if (directionPointing == Direction.DOWN){
            y += SPEED;
        } else if (directionPointing == Direction.LEFT){
            x -= SPEED;
        } else if (directionPointing == Direction.RIGHT){
            x += SPEED;
        }
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(COLOR);
        g2d.fill(new Rectangle2D.Double(x, y, width, height));
    } 
}