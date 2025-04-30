/**
 * The KeyBindings class deals with the player key bindings, as well as the consequences of doing such bindings. 
 * This class and its functionality is separated from the GameCanvas class to remove code bloat.
 */
package lib.input;

import java.awt.event.*;
import javax.swing.*;
import lib.network.Player;
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
     * @param speed the speed at which the player is moving
     */
    public void updatePlayerPosition(PlayerObject player, double speed) {
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

    public String getPlayerAction() {
        if (up || down || left || right) {
            return "Running";
        } else {
            return "Idle";
        }
    }

    public String getPlayerDirection() {
        if (left) {
            return "Left";
        } else if (right) {
            return "Right";
        } else if (up) {
            return "Up";
        } else if (down) {
            return "Down";
        } else {
            return "None";
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
            System.out.println("FIRE");
            player.requestToCast("FIRE_SPELL");
            resetLetters("fire");
        } else if (isStringPressedDown("water")) {
            System.out.println("WATER");
            resetLetters("water");
        } else if (isStringPressedDown("earth")) {
            System.out.println("EARTH");
            resetLetters("earth");
        } else if (isStringPressedDown("wind")) {
            System.out.println("WIND");
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
}
