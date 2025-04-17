/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib.render;

import java.awt.*;
import java.awt.geom.*;
import lib.*;
import lib.objects.*;
import lib.objects.spells.*;

public class PlayerVisuals extends GameObject {
    public final int screenX, screenY;
    private boolean isPlayer;
    private Color color;
    private Direction facing;
    private GameObject activeSpell;

    public PlayerVisuals(double xPosition, double yPosition, double s, Color c, boolean iP) {
        super(xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        color = c;
        facing = Direction.RIGHT;
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public void castSpell(String spellName){
        if (spellName.equals("fire")){
            activeSpell = new FireSpell((int) screenX, (int) screenY, facing);
        }
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

        if (activeSpell != null){
            activeSpell.drawSprite(g2d);
        }
    }
}
