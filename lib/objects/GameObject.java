/**
 * The GameObject abstract class is the template for all game-related instances.
 * These include the spells, the environment, and the actual player.
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
package lib.objects;

import java.awt.*;

public abstract class GameObject {
    // Shared properties for all game objects. The protected accessor is used so that the subclasses can access it.
    protected double x, y;
    protected double width, height;
    protected String name;

    /**
     * Instantiate a game object.
     * 
     * @param name name of the object
     * @param x the x-value position of the object
     * @param y the y-value position of the object
     * @param width the width of the object
     * @param height with height of the object
     */
    public GameObject(String name, double x, double y, double width, double height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Draws the sprite of the game object.
     * 
     * @param g2d the Graphics2D of the GameCanvas.
     */
    public abstract void drawSprite(Graphics2D g2d); 

    /**
     * Returns the name of the game object.
     * 
     * @return the name of the game object.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the game object.
     * 
     * @param name the name of the game object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the x-value position of the game object.
     * 
     * @return the x-value position of the game object.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-value position of the game object.
     * 
     * @param x the x-value position of the game object.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-value position of the game object.
     * 
     * @return the y-value position of the game object.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-value position of the game object.
     * 
     * @param y y-value position of the game object.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the width of the game object.
     * 
     * @return the width of the game object.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width of the game object.
     * 
     * @param width the width of the game object.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Returns the height of the game object.
     * 
     * @return the height of the game object.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height of the game object.
     * 
     * @param height the height of the game object.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}