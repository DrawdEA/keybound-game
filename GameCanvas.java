import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    ArrayList<GameObject> gameObjects;

    public GameCanvas() {

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
            object.draw(g2d);
        }
    }
}
