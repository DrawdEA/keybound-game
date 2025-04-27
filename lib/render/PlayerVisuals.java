/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib.render;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import lib.*;
import lib.objects.*;
import lib.objects.spells.*;

public class PlayerVisuals extends GameObject {
    public final double screenX, screenY;
    private boolean isPlayer;
    private Color color;
    private Direction facing;
    private ArrayList<GameObject> spells;

    public PlayerVisuals(double xPosition, double yPosition, double s, Color c, boolean iP) {
        super("PLAYER", xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        color = c;
        facing = Direction.RIGHT;

        spells = new ArrayList<>();
    }

    public double getScreenX() {
        return screenX;
    }

    public double getScreenY() {
        return screenY;
    }

    public void castSpell(String spellName){
        if (spellName.equals("fire")){
            spells.add(new FireSpell(screenX, screenY, facing));
        }
    }

    public String getSpellsAsDataString() {
        String spellDataString = "";
        for (GameObject spell : spells){
            spellDataString += String.format("%s-%f-%f-%s ", spell.getName(), spell.getX(), spell.getY(), facing.toString());
        }
        return spellDataString;
    }

    public void clearSpells(){
        spells.clear();
    }

    public void addRemoteSpell(String name, double x, double y, String direction){
        if (name.equals("FIRE_SPELL")){
            spells.add(new FireSpell(x,y, Direction.valueOf(direction)));
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

        if (!spells.isEmpty()){
            for (GameObject spell : spells){
                    // if (isPlayer) {
                    //     ((FireSpell) spell).update();
                    // }

                    if (spell instanceof FireSpell fireSpell){
                        fireSpell.update();
                    }
                    
                    spell.drawSprite(g2d);
                }
            }
    }
}
