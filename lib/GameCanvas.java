/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for setting up the game's timer and the player's keybindings.
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
        gameObjects.add(new Environment(0, 0)); 
        
        // Set the game timer and key bindings.
        keyBindings = new KeyBindings(this);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyBindings.updatePlayerPosition((PlayerVisuals) gameObjects.get(1), GameConfig.PLAYER_SPEED);
                repaint();
            }
        };
        animationTimer = new Timer(10, al);
        animationTimer.start();

        // Set the player ID.
    }

    public void addPlayers(int id) {
        if (id == 1) {
            gameObjects.add(new PlayerVisuals(50, 50, GameConfig.TILE_SIZE, Color.RED));
            gameObjects.add(new PlayerVisuals(50, 500, GameConfig.TILE_SIZE, Color.BLUE));
        } else {
            gameObjects.add(new PlayerVisuals(50, 500, GameConfig.TILE_SIZE, Color.BLUE));
            gameObjects.add(new PlayerVisuals(50, 50, GameConfig.TILE_SIZE, Color.RED));
        }

        repaint();
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
