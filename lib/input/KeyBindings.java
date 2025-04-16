/**
 * The KeyBindings class deals with the player key bindings, as well as the consequences of doing such bindings. 
 * This class and its functionality is separated from the GameCanvas class to remove code bloat.
 */
package lib.input;

import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import lib.render.*;

public class KeyBindings {
    public boolean up, down, left, right;
    public boolean letters[];

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

        letters = new boolean[26]; // Initializing boolean[] defaults it all to false (Boolean[] sets them to null)

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

        // Bind letter keys
        // ASCII value of a=97 and z=122
        for (int i = 97; i < 122 + 1; i++) {
            inputMap.put(KeyStroke.getKeyStroke((char) i), (char) i);

            final int temp = i - 97;
            actionMap.put((char) i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    letters[temp] = true;
                }
            });
        }

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

    /**
     * Casts the spell of a player if they press down the right combination of keys
     * 
     * @param player the player that casts the spell
     */
    public void castPlayerSpells(PlayerVisuals player) {
        // Else if is important since if someone pressed "waterh" we don't want to activate both earth and water
        // BASIC SPELLS
        if (isStringPressedDown("fire")){
            System.out.println("FIRE");
        } else if (isStringPressedDown("water")){
            System.out.println("WATER");
        } else if (isStringPressedDown("earth")){
            System.out.println("EARTH");
        } else if (isStringPressedDown("wind")){
            System.out.println("WIND");
        }
    }

    /**
     * A helper function that return true or false if all the letters in a given string are known to be pressed down
     * 
     * @param checkString 
     */
    public boolean isStringPressedDown(String checkString) {
        ArrayList<Integer> asciiCodes = new ArrayList<>();
        
        for (int i = 0; i < checkString.length(); i++){
            // ASCII value a=97 and z=122
            asciiCodes.add((int) checkString.charAt(i) - 97);
        }

        for (int code : asciiCodes){
            if (letters[code] == false) // If even one of the keys in the string isn't pressed then the combination isn't active
                return false;
        }

        // Reset Letters to false
        letters = new boolean[26];

        return true;
    }
}
