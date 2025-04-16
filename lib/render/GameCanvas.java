/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for setting up the game's timer and the player's keybindings.
 */
package lib.render;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import lib.*;
import lib.input.*;
import lib.objects.*;

public class GameCanvas extends JComponent {
    ArrayList<GameObject> gameObjects;
    Timer animationTimer;
    KeyBindings keyBindings;
    PlayerVisuals self, enemy;

    public GameCanvas() {
        // Initialize object to hold all gameObjects.
        gameObjects = new ArrayList<>();

        // Initialize the environment.
        gameObjects.add(new Environment(0, 0, this)); 
        
        // Set the game timer and key bindings.
        keyBindings = new KeyBindings(this);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyBindings.updatePlayerPosition((PlayerVisuals) self, GameConfig.PLAYER_SPEED);
                keyBindings.castPlayerSpells((PlayerVisuals) self); // Add a cooldown thread?
                repaint();
            }
        };
        animationTimer = new Timer(10, al);
        animationTimer.start();

        // Set the player ID.
    }

    public void addPlayers(int id) {
        if (id == 1) {
            self = new PlayerVisuals(GameConfig.TILE_SIZE * 20, GameConfig.TILE_SIZE * 10, GameConfig.TILE_SIZE, Color.RED, true);
            enemy = new PlayerVisuals(GameConfig.TILE_SIZE * 20, GameConfig.TILE_SIZE * 15, GameConfig.TILE_SIZE + 2, Color.BLUE, false);
        } else {
            self = new PlayerVisuals(GameConfig.TILE_SIZE * 20, GameConfig.TILE_SIZE * 15, GameConfig.TILE_SIZE + 2, Color.BLUE, true);
            enemy = new PlayerVisuals(GameConfig.TILE_SIZE * 20, GameConfig.TILE_SIZE * 10, GameConfig.TILE_SIZE, Color.RED, false);
            
        }

        repaint();
    }

    public PlayerVisuals getOwnPlayer() {
        return self;
    }

    public PlayerVisuals getEnemy() {
        return enemy;
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
        
        // Draw the players.
        enemy.drawSprite(g2d);
        self.drawSprite(g2d);
    }
}
