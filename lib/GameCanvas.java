/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for keeping the game values and setting up the game's timer and the player's keybindings.
 */
package lib;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class GameCanvas extends JComponent {
    ArrayList<GameObject> gameObjects;
    Timer animationTimer;
    KeyBindings keyBindings;

    public GameCanvas() {
        // Initialize object to hold all gameObjects
        gameObjects = new ArrayList<>();

        // Initialize the environment.
        gameObjects.add(new Environment(0, 0, 16, 16)); 
        gameObjects.add(new Player(50, 50, 50, Color.RED));
        gameObjects.add(new Player(50, 500, 50, Color.BLUE));
        
        // Set the game timer and key bindings.
        keyBindings = new KeyBindings(this);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyBindings.updatePlayerPosition((Player) gameObjects.get(1), GameConfig.PLAYER_SPEED);
                repaint();
            }
        };
        animationTimer = new Timer(10, al);
        animationTimer.start();
    }

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
