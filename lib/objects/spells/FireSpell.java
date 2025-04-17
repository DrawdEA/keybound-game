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

    int x, y;
    int width = 25, height = 25;
    Direction directionPointing;

    public FireSpell(int x, int y, Direction directionPointing) {
        super(x, y, 25, 25);
        
        this.x = x;
        this.y = y;

        this.directionPointing = directionPointing;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        if (directionPointing == Direction.UP){
            y -= SPEED;
        } else if (directionPointing == Direction.DOWN){
            y += SPEED;
        } else if (directionPointing == Direction.LEFT){
            x -= SPEED;
        } else if (directionPointing == Direction.RIGHT){
            x += SPEED;
        }

        System.out.println("fire: " + x + " " + y);

        g2d.setColor(COLOR);
        g2d.fill(new Rectangle2D.Double(x, y, width, height));
    }
    
}