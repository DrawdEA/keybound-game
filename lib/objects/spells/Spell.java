 /**
 * The Spell abstract class serves as a template for the spells in the game.
 * It contains the spell's location, direction (if applicable), as well as the methods that needs to be implemented.
 * 
 * @author Edward Joshua M. Diesta (241571), Charles Joshua T. Uy (244644)
 * @version May 20, 2025
 * 
 * We have not discussed the Java language code in our program 
 * with anyone other than our instructor or the teaching assistants 
 * assigned to this course.
 * 
 * We have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in our program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of our program.
 */
package lib.objects.spells;

import java.awt.Graphics2D;
import lib.GameConfig;
import lib.objects.*;
import lib.render.CollisionManager;
import lib.render.Direction;

public abstract class Spell extends GameObject {
    protected final double PLAYER_X_WIDTH = GameConfig.TILE_SIZE * 4;
    protected final double PLAYER_Y_WIDTH = GameConfig.TILE_SIZE * 2;

    protected int casterId;
    protected Direction direction;

    /**
     * Spell constructor for spells that contain a direction.
     * 
     * @param name name of the spell
     * @param casterId id of the caster
     * @param x x-value position of the spell
     * @param y y-value position of the spell
     * @param width width of the spell
     * @param height height of the spell
     * @param direction direction of the spell
     */
    public Spell(String name, int casterId, double x, double y, double width, double height, Direction direction) {
        super(name, x, y, width, height);

        this.x = x;
        this.y = y;

        this.casterId = casterId;
        this.direction = direction;
    }

    /**
     * Constructor for spells that do not contain a direction.
     * 
     * @param name name of the spell
     * @param casterId id of the caster
     * @param x x-value position of the spell
     * @param y y-value position of the spell
     * @param width width of the spell
     * @param height height of the spell
     */
    public Spell(String name, int casterId, double x, double y, double width, double height) {
        super(name, x, y, width, height);
        this.casterId = casterId;
        this.direction = null;
    }

    /**
     * Returns the id of the caster.
     * 
     * @return the id of the caster
     */
    public int getCasterId() {
        return casterId;
    }

    /**
     * Updates the values of the spell. Will be called continuously.
     */
    public abstract void update();

    /**
     * Returns whether or not the spell is expired.
     * 
     * @return if the spell is expired or not.
     */
    public abstract boolean isExpired();

    /**
     * Returns the required networking data string that can be read by the client.
     * @return
     */
    public abstract String getDataString();

    /**
     * Damages the player once it detects any collisions with the player.
     * 
     * @param cm the client or server's CollisionManager
     * @return 
     */
    public abstract int handleCollisions(CollisionManager cm);

    /**
     * Draws the visuals of the spell in the canvas through Graphics2D.
     * 
     * @param g2d the Graphics2D of the client
     */
    public abstract void drawSprite(Graphics2D g2d);
}
