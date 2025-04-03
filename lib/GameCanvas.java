package lib;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class GameCanvas extends JComponent {
    public static final int SCALE = 2;
    public static final int TILE_SIZE = 16 * SCALE;
    public static final int MAX_SCREEN_TILE_COLUMNS = 25;
    public static final int MAX_SCREEN_TILE_ROWS = 18;

    ArrayList<GameObject> gameObjects;
    Timer gameTimer;

    public GameCanvas() {
        // Initialize object to hold all gameObjects
        gameObjects = new ArrayList<>();

        // Initialize the environment.
        gameObjects.add(new Environment(0, 0, 16, 16, this));
        
        // Set the game timer to be 100 FPS
        gameTimer = new Timer(10, e -> {
            repaint();
        });
    }

    /**
     * Draws every shape in the drawingObjects array list.
     * 
     * @param g main graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Cast Graphics to Graphics2D and apply anti-aliasing key.
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        // Draw every object.
        for (GameObject object : gameObjects) {
            object.drawSprite(g2d);
        }
    }
}
