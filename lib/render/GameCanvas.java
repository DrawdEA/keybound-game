/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for setting up the game's timer and the player's keybindings.
 * 
 * @author Edward Joshua M. Diesta (241571), Charles Joshua T. Uy (244644)
 * @version May 20, 2025
 * 
 * We have not discussed the Java language code in our program 
 * with anyone other than our instructor or the teaching assistants 
 * assigned to this course.
 * 
 * We have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in our program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of our program.
 */
package lib.render;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.Timer;
import lib.*;
import lib.input.*;
import lib.menus.MainMenu;
import lib.network.Player;
import lib.objects.*;
import lib.objects.spells.*;

public class GameCanvas extends JComponent implements ActionListener{
    // Game objects.
    private ArrayList<GameObject> gameObjects;
    private ArrayList<Spell> spells;
    private PlayerObject self, enemy;
    private Environment environment;
    private Player selfPlayerClient;

    // The Game GUI.
    private InGameGUI gui;

    // End Game Return Button
    private JButton backToMainBtn;
    private final Color buttonBg1 = new Color(58,68,102);
    private final Color buttonTextColor = Color.WHITE;

    // Miscellaneous.
    private boolean gameHasEnded;
    private Timer animationTimer;
    private KeyBindings keyBindings;
    private CollisionManager collisionManager;

    private ActionListener al;

    private Font Jacquard, Pixelify;
    String[] colorPaths = {"purple", "red", "green", "gray", "yellow", "blue", "orange"};

    /**
     * Instantiates a game canvas and initializes much of the needed resources in the game.
     */
    public GameCanvas() {
        // Initialize the spells.
        FireSpell.initializeSprites();
        WindSpell.initializeSprites();
        EarthSpell.initializeSprites();
        WaterSpell.initializeSprites();
        
        // Initialize object to hold all gameObjects.
        gameObjects = new ArrayList<>();
        spells = new ArrayList<>();
        gameHasEnded = false;       
        
        // Load Fonts and Bg image
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(15f);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Initialize return button
        this.setLayout(null);
        backToMainBtn = new JButton("DONE");
        backToMainBtn.setFont(Pixelify.deriveFont(30f));
        backToMainBtn.setBackground(buttonBg1);
        backToMainBtn.setForeground(buttonTextColor);
        backToMainBtn.setFocusable(false);
        backToMainBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.4), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.675), 
            (int) (GameConfig.SCREEN_LENGTH * 0.2), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        backToMainBtn.addActionListener(this);
        backToMainBtn.setVisible(false); // Initially hidden then unhide at end of game
        this.add(backToMainBtn);
    }

    /**
     * Sets the player as the self in the canvas.
     * 
     * @param player the client
     */
    public void setPlayerClient(Player player) {
        selfPlayerClient = player;
    }

    /**
     * Initializes the players and other mechanics in the game.
     * 
     * @param ownPlayerId the id of the playaer
     * @param serverData the server data for all players in the server
     */
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

    /**
     * Returns the PlayerObject of the client running the GameCanvas.
     * 
     * @return the PlayerObject of the client running the GameCanvas
     */
    public PlayerObject getOwnPlayer() {
        return self;
    }

    /**
     * Returns the PlayerObject of the enemy.
     * 
     * @return the PlayerObject of the enemy
     */
    public PlayerObject getEnemy() {
        return enemy;
    }

    /**
     * Removes all of the spells in the GameCanvas.
     */
    public void clearSpells() {
        spells.clear();
    }

    /**
     * Adds a spell in the GameCanvas.
     * 
     * @param spell the spell to be added
     */
    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    /**
     * Updates the game canvas' game timer  
     * 
     * @param seconds left for the game from the server
     */
    public void updateGameTimer(int seconds) {
        if (gui != null) {
            gui.setGameTimer(seconds);

            if (seconds == 0) {
                gameHasEnded = true;
                backToMainBtn.setVisible(true);
                this.revalidate();
                this.repaint();
            }
        }
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

        // Draw the spells.
        for (Spell spell : spells) {
            spell.update();
            spell.drawSprite(g2d);
        }
      
        // Show the scoreboard if the player is pressing tab
        if (self != null && enemy != null) {
            if (keyBindings.isScoreBoardAsked() || gameHasEnded) {
                
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
                g2d.drawString("Score Board", (int) (GameConfig.SCREEN_LENGTH * 0.265), (int) (GameConfig.SCREEN_LENGTH * 0.25));
                
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

    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel mainFrame = (JPanel) this.getParent();
        
        if (e.getSource() == backToMainBtn) {
            Sound openSound = new Sound(2);
            openSound.play();

            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new MainMenu(), BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}
