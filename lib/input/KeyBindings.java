/**
 * The KeyBindings class deals with the player key bindings, as well as the consequences of doing such bindings. 
 * This class and its functionality is separated from the GameCanvas class to remove code bloat.
 */
package lib.input;

import java.awt.event.*;
import java.util.stream.IntStream;
import javax.swing.*;
import lib.GameConfig;
import lib.network.Player;
import lib.render.*;

public class KeyBindings {
    public boolean up, down, left, right;
    public boolean attacking1, attacking2;
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
        for (int keyCode = KeyEvent.VK_A; keyCode <= KeyEvent.VK_Z; keyCode++){
            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, false), "press" + (char) keyCode);
            inputMap.put(KeyStroke.getKeyStroke(keyCode, 0, true), "release" + (char) keyCode);

            final int temp = keyCode - 65; // ASCII value of VK_A = 65
            actionMap.put("press" + (char) keyCode, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    letters[temp] = true;
                }
            });
            actionMap.put("release" + (char) keyCode, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    letters[temp] = false;
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
     * @param displacement the speed at which the player is moving
     */
    public void updatePlayerPosition(PlayerObject player, double displacement, String direction) {
        if (direction == "Up") {
            player.setY(player.getY() - displacement);
        } else if (direction == "Down") {
            player.setY(player.getY() + displacement);
        } else if (direction == "Left") {
            player.setX(player.getX() - displacement);
        } else if (direction == "Right") {
            player.setX(player.getX() + displacement);
        }
    }

    public String getPlayerAction() {
        if (getPlayerDirection() != "None") {
            return "Running";
        } else if (IntStream.range(0, letters.length).anyMatch(i -> letters[i])) { // Checks if any of the letters are true.
            return "Casting";
        } else if (attacking1) {
            return "Attacking1";
        } else if (attacking2) {
            return "Attacking2";
        } else {
            return "Idle";
        }
    }

    public String getPlayerDirection() {
        // Calculate the total number of active directions
        int activeCount = (up ? 1 : 0) + (down ? 1 : 0) + (left ? 1 : 0) + (right ? 1 : 0);

        // Rule 5 & 4: If three or more directions are active, or opposites are active, cancel
        if (activeCount >= 3 || (up && down) || (left && right)) {
            return "None";
        }
        // Rule 3: Check for adjacent two-direction combinations (Vertical then Horizontal)
        else if (up && left) {
            return "Up Left";
        } else if (up && right) {
            return "Up Right";
        } else if (down && left) {
            return "Down Left";
        } else if (down && right) {
            return "Down Right";
        }
        // Rule 2: Check for single directions
        else if (left) {
            return "Left";
        } else if (right) {
            return "Right";
        } else if (up) {
            return "Up";
        } else if (down) {
            return "Down";
        }
        // Rule 1: If none of the above, no directions are active
        else {
            return "None";
        }
    }

    public void movePlayer(CollisionManager cm, PlayerObject player) {
        String direction = getPlayerDirection();

        if (direction == "Up Right") {
            if (!cm.checkWorldCollision(player, "Up")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Up");
            }
            if (!cm.checkWorldCollision(player, "Right")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Right");
            }
        } else if (direction == "Up Left") {
            // Handle Up Left diagonal movement
            if (!cm.checkWorldCollision(player, "Up")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Up");
            }
            if (!cm.checkWorldCollision(player, "Left")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Left");
            }
        } else if (direction == "Down Right") {
            // Handle Down Right diagonal movement
             if (!cm.checkWorldCollision(player, "Down")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Down");
            }
            if (!cm.checkWorldCollision(player, "Right")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Right");
            }
        } else if (direction == "Down Left") {
            // Handle Down Left diagonal movement
             if (!cm.checkWorldCollision(player, "Down")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Down");
            }
            if (!cm.checkWorldCollision(player, "Left")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED * 0.7, "Left");
            }
        } else if (direction == "Up") {
            // Handle Up single movement
            if (!cm.checkWorldCollision(player, "Up")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED, "Up");
            }
        } else if (direction == "Down") {
            // Handle Down single movement
             if (!cm.checkWorldCollision(player, "Down")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED, "Down");
            }
        } else if (direction == "Left") {
            // Handle Left single movement
             if (!cm.checkWorldCollision(player, "Left")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED, "Left");
            }
        } else if (direction == "Right") {
            // Handle Right single movement
             if (!cm.checkWorldCollision(player, "Right")) {
                updatePlayerPosition(player, GameConfig.PLAYER_SPEED, "Right");
            }
        }        
    }

    /**
     * Casts the spell of a player if they press down the right combination of keys
     * 
     * @param player the player that casts the spell
     */
    public void castPlayerSpells(Player player) {
        // Else if is important since if someone pressed "waterh" we don't want to activate both earth and water
        // BASIC SPELLS
        if (isStringPressedDown("fire")) {
            attacking1 = true;
            player.requestToCast("FIRE_SPELL");
            attacking1 = false;
            resetLetters("fire");
        } else if (isStringPressedDown("water")) {
            attacking1 = true;
            player.requestToCast("WATER_SPELL");
            attacking1 = false;
            resetLetters("water");
        } else if (isStringPressedDown("earth")) {
            attacking1 = true;
            player.requestToCast("EARTH_SPELL");
            attacking1 = false;
            resetLetters("earth");
        } else if (isStringPressedDown("wind")) {
            attacking1 = true;
            player.requestToCast("WIND_SPELL");
            attacking1 = false;
            resetLetters("wind");
        }
    }

    /**
     * A helper function that return true or false if all the letters in a given string are known to be pressed down
     * 
     * @param checkString 
     */
    public boolean isStringPressedDown(String checkString) {
        // Build a boolean array for the spell
        boolean[] spellLetters = new boolean[26];
        for (int i = 0; i < checkString.length(); i++) {
            int code = checkString.charAt(i) - 97;
            spellLetters[code] = true;
        }

        // Now check that letters[] matches spellLetters exactly
        for (int i = 0; i < 26; i++) {
            if (letters[i] != spellLetters[i]) {
                return false;
            }
        }
        return true;
    }

    private void resetLetters(String spell) {
        for (int i = 0; i < spell.length(); i++) {
            letters[spell.charAt(i) - 97] = false;
        }
    }

    public String getLetters() {
        String outputString = "";

        for (int i = 0; i < letters.length; i++) {
            if (letters[i]) {
                char correspondingLetter = (char) ('A' + i);
                if (!outputString.isEmpty()) {
                    outputString = outputString + " ";
                }
                outputString = outputString + correspondingLetter; 
            }
        }

        return outputString;
    }
}

    
