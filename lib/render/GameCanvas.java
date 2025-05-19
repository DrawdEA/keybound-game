/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for setting up the game's timer and the player's keybindings.
 */
package lib.render;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import lib.*;
import lib.input.*;
import lib.network.Player;
import lib.objects.*;
import lib.objects.spells.*;

public class GameCanvas extends JComponent {
    // Game objects.
    private ArrayList<GameObject> gameObjects;
    private ArrayList<Spell> spells;
    private PlayerObject self, enemy;
    private Environment environment;
    private Player selfPlayerClient;

    // The Game GUI.
    private InGameGUI gui;

    // Miscellaneous.
    private Timer animationTimer;
    private KeyBindings keyBindings;
    private CollisionManager collisionManager;

    private ActionListener al;

    private Font Jacquard, Pixelify;

    String[] colorPaths = {"purple", "red", "green", "gray", "yellow", "blue", "orange"};

    public GameCanvas() {
        // Initialize the spells.
        FireSpell.initializeSprites();
        WindSpell.initializeSprites();
        EarthSpell.initializeSprites();
        WaterSpell.initializeSprites();
        
        // Initialize object to hold all gameObjects.
        gameObjects = new ArrayList<>();
        spells = new ArrayList<>();

        
        // Load Fonts and Bg image
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(15f);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void setPlayerClient(Player player) {
        selfPlayerClient = player;
    }

    public void addPlayers(int ownPlayerId, String[] serverData) {
        for (int i = 1; i < serverData.length; i++){
            String[] params = serverData[i].split("-");
            int id = Integer.parseInt(params[0]);
            if (ownPlayerId == id) {
                self = new PlayerObject(
                    Double.parseDouble(params[1]), 
                    Double.parseDouble(params[2]), 
                    GameConfig.TILE_SIZE, 
                    true,
                    id
                );
            } else {
                enemy = new PlayerObject(                    
                    Double.parseDouble(params[1]), 
                    Double.parseDouble(params[2]), 
                    GameConfig.TILE_SIZE, 
                    false,
                    id
                );
            }
        }

        // Initialize the environment (it's here cause it needs the player coords)
        environment = new Environment(0, 0, this);

        // Set the game timer, key bindings, and collisions.
        keyBindings = new KeyBindings(this);
        collisionManager = new CollisionManager(environment);

        // Render the GUI.
        gui = new InGameGUI(keyBindings);

        al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyBindings.movePlayer(collisionManager, self);
                keyBindings.castPlayerSpells(selfPlayerClient);
                repaint();
            }
        };
        animationTimer = new Timer(10, al);
        animationTimer.start();

        gui.updatePlayerObject(self);
        collisionManager.addPlayer(enemy);
        collisionManager.addPlayer(self);
        repaint();
    }

    public PlayerObject getOwnPlayer() {
        return self;
    }

    public PlayerObject getEnemy() {
        return enemy;
    }

    public void clearSpells() {
        spells.clear();
    }

    public void addSpell(Spell spell) {
        spells.add(spell);
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

        // Draw the environment.
        if (environment != null) {
            environment.drawSprite(g2d);
        }

        // Draw every object.
        for (GameObject object : gameObjects) {
            object.drawSprite(g2d);
        }

        if (self != null && enemy != null) {
            // Draw the players.
            self.updatePlayerAnimation(keyBindings.getPlayerAction(), keyBindings.getPlayerDirection());
            enemy.drawSprite(g2d);
            self.drawSprite(g2d);

            // Render the GUI.
            gui.updatePlayerObject(self);
            gui.renderGUI(g2d);
        }

        for (Spell spell : spells) {
            spell.update();
            spell.drawSprite(g2d);
            //spell.handleCollisions(collisionManager);
        }

        // Show the scoreboard if the player is pressing tab
        if (self != null && enemy != null) {
            if (keyBindings.isScoreBoardAsked()) {
                
                // Background
                g2d.setColor(new Color(234, 212, 170, 200));
                g2d.fillRect(
                    (int) (GameConfig.SCREEN_LENGTH * 0.2), 
                    (int) (GameConfig.SCREEN_HEIGHT * 0.2), 
                    (int) (GameConfig.SCREEN_LENGTH * 0.6), 
                    (int) (GameConfig.SCREEN_HEIGHT * 0.6)
                );

                // Title
                g2d.setColor(new Color(30, 30, 30, 200));
                g2d.setFont(Jacquard);
                g2d.drawString("Leaderbound", (int) (GameConfig.SCREEN_LENGTH * 0.265), (int) (GameConfig.SCREEN_LENGTH * 0.25));

                // Header
                g2d.setFont(Pixelify);
                g2d.drawString("Player", (int) (GameConfig.SCREEN_LENGTH * 0.25), (int) (GameConfig.SCREEN_LENGTH * 0.3));
                g2d.drawString("Kills", (int) (GameConfig.SCREEN_LENGTH * 0.55), (int) (GameConfig.SCREEN_LENGTH * 0.3));
                g2d.drawString("Deaths", (int) (GameConfig.SCREEN_LENGTH * 0.65), (int) (GameConfig.SCREEN_LENGTH * 0.3));

                try {
                    // Self Player
                    int[] selfPlayerStats = selfPlayerClient.getSelfStats();
                    BufferedImage selfPlayerImage = ImageIO.read(getClass().getResourceAsStream(String.format("/resources/player/%s.png", 
                    colorPaths[selfPlayerStats[0] - 1]))).getSubimage(0, 0, 64, 32);

                    g2d.drawImage(selfPlayerImage, (int) (GameConfig.SCREEN_LENGTH * 0.25), (int) (GameConfig.SCREEN_LENGTH * 0.3) + 15, this);
                    g2d.drawString(
                        String.format("Player %d",selfPlayerStats[0]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.25) + 75, 
                        (int) (GameConfig.SCREEN_LENGTH * 0.3) + 16 + 20
                    );
                    g2d.drawString(
                        String.format("%d",selfPlayerStats[1]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.55), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.3) + 16 + 20 
                    );
                    g2d.drawString(
                        String.format("%d",selfPlayerStats[2]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.65), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.3) + 16 + 20
                    );

                    // Enemy Player
                    int[] enemyPlayerStats = selfPlayerClient.getEnemyStats();
                    BufferedImage enemyPlayerImage = ImageIO.read(getClass().getResourceAsStream(String.format("/resources/player/%s.png", 
                    colorPaths[enemyPlayerStats[0] - 1]))).getSubimage(0, 0, 64, 32);

                    g2d.drawImage(enemyPlayerImage, (int) (GameConfig.SCREEN_LENGTH * 0.25), (int) (GameConfig.SCREEN_LENGTH * 0.35) + 15, this);
                    g2d.drawString(
                        String.format("Player %d",enemyPlayerStats[0]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.25) + 75, 
                        (int) (GameConfig.SCREEN_LENGTH * 0.35) + 16 + 20
                    );
                    g2d.drawString(
                        String.format("%d",enemyPlayerStats[1]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.55), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.35) + 16 + 20 
                    );
                    g2d.drawString(
                        String.format("%d",enemyPlayerStats[2]), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.65), 
                        (int) (GameConfig.SCREEN_LENGTH * 0.35) + 16 + 20
                    );

                } catch (Exception ex) {
                    System.err.println(ex);
                }

            }
        }
    }
}
