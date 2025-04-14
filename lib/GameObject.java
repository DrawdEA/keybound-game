package lib;

import java.awt.*;

public abstract class GameObject {
    // Shared properties for all game objects. The protected accessor is used so that the subclasses can access it.
    protected double x, y;
    protected double width, height;

    // Constructor to initialize position and size.
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Abstract method for updating game logic (to be implemented by subclasses)
    //public abstract void update();

    // Concrete method for drawing the object (can be overridden by subclasses)
    public abstract void drawSprite(Graphics2D g2d); 

    // Getters and setters for position and size
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}