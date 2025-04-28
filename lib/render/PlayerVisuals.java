/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib.render;

import java.awt.*;
import java.awt.geom.*;
import lib.*;
import lib.objects.*;

public class PlayerVisuals extends GameObject {
    public final double screenX, screenY;
    private boolean isPlayer;
    private Color color;
    private Direction facing;

    public PlayerVisuals(double xPosition, double yPosition, double s, Color c, boolean iP) {
        super("PLAYER", xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        color = c;
        facing = Direction.RIGHT;
    }

    public double getScreenX() {
        return screenX;
    }

    public double getScreenY() {
        return screenY;
    }

    public String getPositionDataString(){
        return String.format("%f-%f-%s", x, y, facing.toString());
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        Rectangle2D.Double square;
        if (isPlayer) {
            square = new Rectangle2D.Double(screenX, screenY, width, height);
        } else {
            square = new Rectangle2D.Double(x, y, width, height);
        }
        
        g2d.setColor(color);
        g2d.fill(square);
    }
}
