/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib;

import java.awt.*;
import java.awt.geom.*;

public class PlayerVisuals extends GameObject {
    private Color color;

    public PlayerVisuals(double xPosition, double yPosition, double s, Color c) {
        super(xPosition, yPosition, s, s);
        color = c;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        Rectangle2D.Double square = new Rectangle2D.Double(x, y, width, height);
        g2d.setColor(color);
        g2d.fill(square);
    }
}
