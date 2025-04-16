/**
 * The KeyBindings class deals with the player key bindings, as well as the consequences of doing such bindings. 
 * This class and its functionality is separated from the GameCanvas class to remove code bloat.
 */
package lib.input;

import java.awt.event.*;
import javax.swing.*;
import lib.render.*;

public class KeyBindings {
    public boolean up, down, left, right;

    /**
     * Sets up the key bindings of the player.
     * 
     * @param gc the GameCanvas at which this class is under
     */
    public KeyBindings(GameCanvas gc) {
        up = false;
        down = false;
        left = false;
        right = false;

        // Get the InputMap and ActionMap. WHEN_IN_FOCUSED_WINDOW is used to always have the keybind whenever the frame is focused.
        InputMap inputMap = gc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = gc.getActionMap();

        // Bind movement keys
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("released UP"), "stopUp");
        inputMap.put(KeyStroke.getKeyStroke("released DOWN"), "stopDown");
        inputMap.put(KeyStroke.getKeyStroke("released LEFT"), "stopLeft");
        inputMap.put(KeyStroke.getKeyStroke("released RIGHT"), "stopRight");

        // Define movement actions
        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                up = true;
            }
        });
        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                down = true;
            }
        });
        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                left = true;
            }
        });
        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                right = true;
            }
        });
        actionMap.put("stopUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                up = false;
            }
        });
        actionMap.put("stopDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                down = false;
            }
        });
        actionMap.put("stopLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                left = false;
            }
        });
        actionMap.put("stopRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                right = false;
            }
        });
    }

    /**
     * Updates the players position depending on the player's current key.
     * 
     * @param player the player moving
     * @param speed the speed at which the player is moving
     */
    public void updatePlayerPosition(PlayerVisuals player, double speed) {
        if (up) {
            player.setY(player.getY() - speed);
        } else if (down) {
            player.setY(player.getY() + speed);
        } else if (left) {
            player.setX(player.getX() - speed);
        } else if (right) {
            player.setX(player.getX() + speed);
        }
    }
}
