package lib;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.geom.*;

public class Player extends GameObject {
    private Color color;

    public Player(double xPosition, double yPosition, double s, Color c) {
        super(xPosition, yPosition, s, s);
        color = c;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        Rectangle2D.Double square = new Rectangle2D.Double(x, y, width, height);
        g2d.setColor(color);
        g2d.fill(square);
    }

    public static void main(String[] args) {
        
    }
}
