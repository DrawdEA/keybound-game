/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib;

import java.awt.*;
import java.awt.geom.*;

public class PlayerVisuals extends GameObject {
    public final int screenX, screenY;
    private boolean isPlayer;
    private Color color;

    public PlayerVisuals(double xPosition, double yPosition, double s, Color c, boolean iP) {
        super(xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        color = c;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
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
