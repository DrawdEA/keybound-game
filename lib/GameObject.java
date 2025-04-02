package lib;

import java.awt.*;

public abstract class GameObject {
    // Shared properties for all game objects
    protected int x, y; // Position
    protected int width, height; // Size

    // Constructor to initialize position and size
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Abstract method for updating game logic (to be implemented by subclasses)
    public abstract void update();

    // Concrete method for drawing the object (can be overridden by subclasses)
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW); // Default color
        g2d.fillRect(x, y, width, height); // Default rendering logic
    }

    // Getters and setters for position and size
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}