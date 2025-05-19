/**
 * The PlayerObject class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation as the actual object found in their own game canvas.
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
package lib.objects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lib.*;
import lib.render.*;

public class PlayerObject extends GameObject {
    public static String[] colorPaths = {"purple", "red", "green", "gray", "yellow", "blue", "orange"};

    // Basic Position and Sprites.
    private int id;
    public final double screenX, screenY;
    private boolean isPlayer;
    private BufferedImage[] playerSprites;
    private BufferedImage[] playerSpritesLeft;
    private BufferedImage shadow;

    // Animation.
    private String currentAnimation;
    private int currentFrame;
    private int animationCounter;
    private int animationIndex;

    private Direction lastHorizontalFacing;
    private Direction facing;

    private boolean overridingAnimation;
    private int overridingIndex;

    // Collisions.
    private Rectangle hitbox;
    private long lastDamagedTime;
    private int playerHealth, playerDeaths, playerKills;

    // Invulnerability frames.
    private int iframeCounter;
    private boolean isFlickering;

    /**
     * Instantiates a player in the game.
     * 
     * @param xPosition the x-value position of the player
     * @param yPosition the y-value position of the player
     * @param s the size of the player
     * @param iP a boolean to check if the player object is the one playing in the client
     * @param id the id of the player object
     */
    public PlayerObject(double xPosition, double yPosition, int s, boolean iP, int id) {
        super("PLAYER", xPosition, yPosition, s, s);
        isPlayer = iP;
        this.id = id;
        
        screenX = (GameConfig.SCREEN_LENGTH / 2) - (GameConfig.TILE_SIZE * 2);
        screenY = (GameConfig.SCREEN_HEIGHT / 2) - (GameConfig.TILE_SIZE);

        // Set up the animation logic.
        currentFrame = 0;
        animationCounter = 0;
        animationIndex = 0;
        facing = Direction.RIGHT;
        lastHorizontalFacing = Direction.RIGHT;

        // Set up the hitbox.
        hitbox = new Rectangle();
        hitbox.x = GameConfig.TILE_SIZE + GameConfig.TILE_SIZE / 2;
        hitbox.y = GameConfig.TILE_SIZE;
        hitbox.width = GameConfig.TILE_SIZE;
        hitbox.height = GameConfig.TILE_SIZE;

        // Create the player's stats
        playerHealth = 5;
        lastDamagedTime = System.currentTimeMillis();
        playerDeaths = 0;
        playerKills = 0;

        // Generate the sprites.
        playerSprites = new BufferedImage[53];
        playerSpritesLeft = new BufferedImage[53];

        // Set up invulnerability frames.
        iframeCounter = 0;
        isFlickering = false;

        try {
            BufferedImage playerMovements = ImageIO.read(getClass().getResourceAsStream(String.format("/resources/player/%s.png", colorPaths[id - 1])));

            // Idle movements.
            playerSprites[0] = playerMovements.getSubimage(0, 0, 64, 32);
            playerSprites[1] = playerMovements.getSubimage(64, 0, 64, 32);
            playerSprites[2] = playerMovements.getSubimage(128, 0, 64, 32);
            playerSprites[3] = playerMovements.getSubimage(192, 0, 64, 32);
            playerSprites[4] = playerMovements.getSubimage(256, 0, 64, 32);
            playerSprites[5] = playerMovements.getSubimage(320, 0, 64, 32);
            playerSprites[6] = playerMovements.getSubimage(384, 0, 64, 32);
            playerSprites[7] = playerMovements.getSubimage(448, 0, 64, 32);

            // Running movements.
            playerSprites[8] = playerMovements.getSubimage(64, 32, 64, 32);
            playerSprites[9] = playerMovements.getSubimage(128, 32, 64, 32);
            playerSprites[10] = playerMovements.getSubimage(192, 32, 64, 32);
            playerSprites[11] = playerMovements.getSubimage(256, 32, 64, 32);
            playerSprites[12] = playerMovements.getSubimage(320, 32, 64, 32);
            playerSprites[13] = playerMovements.getSubimage(384, 32, 64, 32);
            playerSprites[14] = playerMovements.getSubimage(448, 32, 64, 32);

            // Attacking movements.
            playerSprites[16] = playerMovements.getSubimage(0, 96, 64, 32);
            playerSprites[17] = playerMovements.getSubimage(64, 96, 64, 32);
            playerSprites[18] = playerMovements.getSubimage(128, 96, 64, 32);
            playerSprites[19] = playerMovements.getSubimage(192, 96, 64, 32);
            playerSprites[20] = playerMovements.getSubimage(256, 96, 64, 32);
            playerSprites[21] = playerMovements.getSubimage(320, 96, 64, 32);
            playerSprites[22] = playerMovements.getSubimage(384, 96, 64, 32);
            playerSprites[23] = playerMovements.getSubimage(448, 96, 64, 32);

            // Charging movements.
            playerSprites[24] = playerMovements.getSubimage(0, 160, 64, 32);
            playerSprites[25] = playerMovements.getSubimage(64, 160, 64, 32);
            playerSprites[26] = playerMovements.getSubimage(128, 160, 64, 32);
            playerSprites[27] = playerMovements.getSubimage(192, 160, 64, 32);
            playerSprites[28] = playerMovements.getSubimage(256, 160, 64, 32);
            playerSprites[29] = playerMovements.getSubimage(320, 160, 64, 32);
            playerSprites[30] = playerMovements.getSubimage(384, 160, 64, 32);
            playerSprites[31] = playerMovements.getSubimage(448, 160, 64, 32);

            // More attacking movements.
            playerSprites[32] = playerMovements.getSubimage(0, 224, 64, 32);
            playerSprites[33] = playerMovements.getSubimage(64, 224, 64, 32);
            playerSprites[34] = playerMovements.getSubimage(128, 224, 64, 32);
            playerSprites[35] = playerMovements.getSubimage(192, 224, 64, 32);
            playerSprites[36] = playerMovements.getSubimage(256, 224, 64, 32);
            playerSprites[37] = playerMovements.getSubimage(320, 224, 64, 32);
            playerSprites[38] = playerMovements.getSubimage(384, 224, 64, 32);
            playerSprites[39] = playerMovements.getSubimage(448, 224, 64, 32);

            // Damaged movements.
            playerSprites[40] = playerMovements.getSubimage(0, 256, 64, 32);
            playerSprites[41] = playerMovements.getSubimage(64, 256, 64, 32);
            playerSprites[42] = playerMovements.getSubimage(128, 256, 64, 32);
            playerSprites[43] = playerMovements.getSubimage(192, 256, 64, 32);
            playerSprites[44] = playerMovements.getSubimage(256, 256, 64, 32);

            // Death movements.
            playerSprites[45] = playerMovements.getSubimage(0, 288, 64, 32);
            playerSprites[46] = playerMovements.getSubimage(64, 288, 64, 32);
            playerSprites[47] = playerMovements.getSubimage(128, 288, 64, 32);
            playerSprites[48] = playerMovements.getSubimage(192, 288, 64, 32);
            playerSprites[49] = playerMovements.getSubimage(256, 288, 64, 32);
            playerSprites[50] = playerMovements.getSubimage(320, 288, 64, 32);
            playerSprites[51] = playerMovements.getSubimage(384, 288, 64, 32);
            playerSprites[52] = playerMovements.getSubimage(448, 288, 64, 32);

            BufferedImage playerMovementsLeft = ImageIO.read(getClass().getResourceAsStream(String.format("/resources/player/%s_flipped.png", colorPaths[id - 1])));

            // Idle movements.
            playerSpritesLeft[0] = playerMovementsLeft.getSubimage(0, 0, 64, 32);
            playerSpritesLeft[1] = playerMovementsLeft.getSubimage(64, 0, 64, 32);
            playerSpritesLeft[2] = playerMovementsLeft.getSubimage(128, 0, 64, 32);
            playerSpritesLeft[3] = playerMovementsLeft.getSubimage(192, 0, 64, 32);
            playerSpritesLeft[4] = playerMovementsLeft.getSubimage(256, 0, 64, 32);
            playerSpritesLeft[5] = playerMovementsLeft.getSubimage(320, 0, 64, 32);
            playerSpritesLeft[6] = playerMovementsLeft.getSubimage(384, 0, 64, 32);
            playerSpritesLeft[7] = playerMovementsLeft.getSubimage(448, 0, 64, 32);

            // Running movements.
            playerSpritesLeft[8] = playerMovementsLeft.getSubimage(64, 32, 64, 32);
            playerSpritesLeft[9] = playerMovementsLeft.getSubimage(128, 32, 64, 32);
            playerSpritesLeft[10] = playerMovementsLeft.getSubimage(192, 32, 64, 32);
            playerSpritesLeft[11] = playerMovementsLeft.getSubimage(256, 32, 64, 32);
            playerSpritesLeft[12] = playerMovementsLeft.getSubimage(320, 32, 64, 32);
            playerSpritesLeft[13] = playerMovementsLeft.getSubimage(384, 32, 64, 32);
            playerSpritesLeft[14] = playerMovementsLeft.getSubimage(448, 32, 64, 32);

            // Attacking movements.
            playerSpritesLeft[16] = playerMovementsLeft.getSubimage(0, 96, 64, 32);
            playerSpritesLeft[17] = playerMovementsLeft.getSubimage(64, 96, 64, 32);
            playerSpritesLeft[18] = playerMovementsLeft.getSubimage(128, 96, 64, 32);
            playerSpritesLeft[19] = playerMovementsLeft.getSubimage(192, 96, 64, 32);
            playerSpritesLeft[20] = playerMovementsLeft.getSubimage(256, 96, 64, 32);
            playerSpritesLeft[21] = playerMovementsLeft.getSubimage(320, 96, 64, 32);
            playerSpritesLeft[22] = playerMovementsLeft.getSubimage(384, 96, 64, 32);
            playerSpritesLeft[23] = playerMovementsLeft.getSubimage(448, 96, 64, 32);

            // Casting movements.
            playerSpritesLeft[24] = playerMovementsLeft.getSubimage(0, 160, 64, 32);
            playerSpritesLeft[25] = playerMovementsLeft.getSubimage(64, 160, 64, 32);
            playerSpritesLeft[26] = playerMovementsLeft.getSubimage(128, 160, 64, 32);
            playerSpritesLeft[27] = playerMovementsLeft.getSubimage(192, 160, 64, 32);
            playerSpritesLeft[28] = playerMovementsLeft.getSubimage(256, 160, 64, 32);
            playerSpritesLeft[29] = playerMovementsLeft.getSubimage(320, 160, 64, 32);
            playerSpritesLeft[30] = playerMovementsLeft.getSubimage(384, 160, 64, 32);
            playerSpritesLeft[31] = playerMovementsLeft.getSubimage(448, 160, 64, 32);

            // More attacking movements.
            playerSpritesLeft[32] = playerMovementsLeft.getSubimage(0, 224, 64, 32);
            playerSpritesLeft[33] = playerMovementsLeft.getSubimage(64, 224, 64, 32);
            playerSpritesLeft[34] = playerMovementsLeft.getSubimage(128, 224, 64, 32);
            playerSpritesLeft[35] = playerMovementsLeft.getSubimage(192, 224, 64, 32);
            playerSpritesLeft[36] = playerMovementsLeft.getSubimage(256, 224, 64, 32);
            playerSpritesLeft[37] = playerMovementsLeft.getSubimage(320, 224, 64, 32);
            playerSpritesLeft[38] = playerMovementsLeft.getSubimage(384, 224, 64, 32);
            playerSpritesLeft[39] = playerMovementsLeft.getSubimage(448, 224, 64, 32);

            // Damaged movements.
            playerSpritesLeft[40] = playerMovementsLeft.getSubimage(0, 256, 64, 32);
            playerSpritesLeft[41] = playerMovementsLeft.getSubimage(64, 256, 64, 32);
            playerSpritesLeft[42] = playerMovementsLeft.getSubimage(128, 256, 64, 32);
            playerSpritesLeft[43] = playerMovementsLeft.getSubimage(192, 256, 64, 32);
            playerSpritesLeft[44] = playerMovementsLeft.getSubimage(256, 256, 64, 32);

            // Death movements.
            playerSpritesLeft[45] = playerMovementsLeft.getSubimage(0, 288, 64, 32);
            playerSpritesLeft[46] = playerMovementsLeft.getSubimage(64, 288, 64, 32);
            playerSpritesLeft[47] = playerMovementsLeft.getSubimage(128, 288, 64, 32);
            playerSpritesLeft[48] = playerMovementsLeft.getSubimage(192, 288, 64, 32);
            playerSpritesLeft[49] = playerMovementsLeft.getSubimage(256, 288, 64, 32);
            playerSpritesLeft[50] = playerMovementsLeft.getSubimage(320, 288, 64, 32);
            playerSpritesLeft[51] = playerMovementsLeft.getSubimage(384, 288, 64, 32);
            playerSpritesLeft[52] = playerMovementsLeft.getSubimage(448, 288, 64, 32);

            // The player's shadow.
            shadow = ImageIO.read(getClass().getResourceAsStream("/resources/player/shadow.png"));
        } catch (IOException e) { 
            System.out.println("IOException from PlayerVisuals.java");
        }
    }

    /**
     * Returns the x-value position relative to the player's screen.
     * 
     * @return the x-value position relative to the player's screen
     */
    public double getScreenX() {
        return screenX;
    }

    /**
     * Returns the y-value position relative to the player's screen.
     * 
     * @return the y-value position relative to the player's screen
     */
    public double getScreenY() {
        return screenY;
    }

    /**
     * Returns the hitbox of the player.
     * 
     * @return the hitbox of the player
     */
    public Rectangle getHitbox() {
        return hitbox;
    }

    /**
     * Returns the id of the player.
     * 
     * @return the id of the player
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the hitbox of the player that is relative with the world.
     * 
     * @return the hitbox of the player that is relative with the world
     */
    public Rectangle getRelativeHitbox() {
        Rectangle relativeHitbox = new Rectangle((int) x + hitbox.x, (int) y + hitbox.y, hitbox.width, hitbox.height);
        return relativeHitbox;
    }

    /**
     * Returns the direction the player is currently facing.
     * 
     * @return the direction the player is currently facing
     */
    public Direction getDirection() {
        return facing;
    }

    /**
     * Overrides the animation of the player.
     * 
     * @param animationType the type of animation the player will be playing
     */
    public void overrideAnimation(String animationType) {
        overridingAnimation = true;
        currentFrame = 0;
        animationCounter = 0;
        if (animationType.equals("Attacking1")) {
            overridingIndex = 16;
        } else if (animationType.equals("Attacking2")) {
            overridingIndex = 32;
        } else if (animationType.equals("Damaged")) { // Handle iframes in this section too.
            overridingIndex = 40;
            flickerPlayer();
        } else if (animationType.equals("Dying")) {
            overridingIndex = 45;
        }
    }

    /**
     * Flickers the player.
     */
    private void flickerPlayer() {
        isFlickering = true;
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                isFlickering = false;
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted.");
            }
        });
        thread.start();
    }

    /**
     * Checks if the player is overriding a specific animation type.
     * 
     * @param animationType the type of animation
     * @return a boolean if the player is overriding a specific animation type
     */
    public boolean isOverridingAnimationOfType(String animationType) {
        if (!overridingAnimation) {
            return false;
        }
        int targetIndex = -1;
        if (animationType.equals("Attacking1")) targetIndex = 16;
        else if (animationType.equals("Attacking2")) targetIndex = 32;
        else if (animationType.equals("Damaged")) targetIndex = 40;
        else if (animationType.equals("Dying")) targetIndex = 45;
        
        return overridingIndex == targetIndex;
    }

    /**
     * Removes the override animation.
     */
    public void clearOverrideAnimation() {
        overridingAnimation = false;
    }  

    /**
     * Checks if the player is currently overriding any animation.
     * 
     * @return true if the player is currently overriding any animation
     */
    public boolean isOverridingAnimation() {
        return overridingAnimation;
    }

    /**
     * Update the current frame and the animation of the player.
     * Also checks for any override and adjusts accordingly.
     * 
     * @param animationType the type of animation to animate
     * @param direction the new direction that the player will be facing
     */
    public void updatePlayerAnimation(String animationType, String direction) {
        if (overridingAnimation) { // If there is an overriding animation, render it.
            animationCounter++;
            if (animationCounter >= GameConfig.CAST_ANIMATION_COUNTER) {
                animationIndex = overridingIndex + currentFrame;
                currentFrame++;
                animationCounter = 0;

                int totalFramesInThisOverride = 0;

                if (overridingIndex == 40) { // Damaged animation only has 5 frames.
                    totalFramesInThisOverride = 5;
                } else { // All other animations have 8.
                    totalFramesInThisOverride = 8;
                }

                if (totalFramesInThisOverride > 0 && currentFrame >= totalFramesInThisOverride) {
                    overridingAnimation = false;
                }
            }
        } else { // Otherwise, render the animation normally.
            if (direction.contains("Right")) {
                facing = Direction.RIGHT;
                lastHorizontalFacing = Direction.RIGHT;
            } else if (direction.contains("Left")) {
                facing = Direction.LEFT;
                lastHorizontalFacing = Direction.LEFT;
            } else if (direction.contains("Up")) {
                facing = Direction.UP;
            } else if (direction.contains("Down")){
                facing = Direction.DOWN;
            }
    
            if (animationCounter == GameConfig.ANIMATION_COUNTER) {
                if (animationType != currentAnimation) {
                    currentFrame = 0;
                } else {
                    currentFrame++;
                }

                int totalAnimationFrames = 0;
                if (animationType.equals("Idle")) {
                    animationIndex = 0;
                    totalAnimationFrames = 8;
                } else if (animationType.equals("Running")) {
                    animationIndex = 8;
                    totalAnimationFrames = 7;
                } else if (animationType.equals("Casting")) {
                    animationIndex = 24;
                    totalAnimationFrames = 8;
                }
    
                if (currentFrame >= totalAnimationFrames) {
                    currentFrame = 0;
                }
    
                animationIndex += currentFrame;
                currentAnimation = animationType;
                animationCounter = 0;
            }
            animationCounter++;
        }
    }

    /**
     * Returns the player data string to be parsed.
     * 
     * @return the player data string to be parsed
     */
    public String getPlayerDataString() {
        return String.format("%d-%f-%f-%s-%d-%s", 
            id, 
            x, 
            y, 
            facing.toString(), 
            animationIndex, 
            lastHorizontalFacing.toString(),
            playerHealth
        );
    }

    /**
     * Make the player invulnerable for 1 second after getting damaged.
     * 
     * @return whether or not the player can be damaged
     */
    public boolean isDamageable() {
        long elapsedTimeSinceLastDamage = (System.currentTimeMillis() - lastDamagedTime) / 1000;
        if (elapsedTimeSinceLastDamage >= 2) {
            lastDamagedTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
   
    /**
     * Set the new position of the player.
     * 
     * @param x the new x-value position of the player
     * @param y the new y-value position of the player
     */
    public void setNewPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the health of the player.
     * 
     * @return the health of the player
     */
    public int getPlayerHealth() {
        return playerHealth;
    }

    /**
     * Sets the new HP of the player.
     * 
     * @param newhp the new HP of the player.
     */
    public void setHP(int newhp) {
        playerHealth = newhp;
    }

    /**
     * Returns the number of deaths of the player.
     * 
     * @return the num of deaths of the player
     */
    public int getDeaths() {
        return playerDeaths;
    }

    /**
     * Sets the new num of deaths of the player.
     * 
     * @param newDeaths the new num of deaths of the player.
     */
    public void setDeaths(int newDeaths){
        playerDeaths = newDeaths;
    }

    /**
     * Returns the number of kills of the player.
     * 
     * @return the num of kills of the player
     */
    public int getKills() {
        return playerKills;
    }

    /**
     * Sets the new num of kills of the player.
     * 
     * @param newKills the new num of kills of the player.
     */
    public void setKills(int newKills){
        playerKills = newKills;
    }

    /**
     * Sets the sprite of the player.
     * 
     * @param animationIndex the index of the animation
     * @param horizontalFacing whether or not it is facing left or right
     */
    public void setSprite(int animationIndex, int horizontalFacing) {
        if (horizontalFacing == 0){
            lastHorizontalFacing = Direction.LEFT;
        } else if (horizontalFacing == 1) {
            lastHorizontalFacing = Direction.RIGHT;
        }
        this.animationIndex = animationIndex; 

        if (!isFlickering && animationIndex >= 40 && animationIndex < 45) {
            flickerPlayer();
        }
    }


    /**
     * Private helper function to draw the player.
     * 
     * @param g2d the Graphics2D of the GameCanvas
     */
    private void drawPlayer(Graphics2D g2d) {
        Composite originalComposite = g2d.getComposite();
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
        g2d.setComposite(alphaComposite);
        if (isPlayer) {
            g2d.drawImage(shadow, (int) screenX + 40, (int) screenY + 59, GameConfig.TILE_SIZE * 2, GameConfig.TILE_SIZE * 2, null);
        } else {
            g2d.drawImage(shadow, (int) x + 40, (int) y + 59, GameConfig.TILE_SIZE * 2, GameConfig.TILE_SIZE * 2, null);
        }
        g2d.setComposite(originalComposite);

        // Draw the actual player.
        if (lastHorizontalFacing == Direction.LEFT) {
            if (isPlayer) {
                g2d.drawImage(playerSpritesLeft[animationIndex], (int) screenX, (int) screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            } else {
                g2d.drawImage(playerSpritesLeft[animationIndex], (int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            }
        } else {
            if (isPlayer) {
                g2d.drawImage(playerSprites[animationIndex], (int) screenX, (int) screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            } else {
                g2d.drawImage(playerSprites[animationIndex], (int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            }
        }
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        // Update iframeCounter.
        if (iframeCounter == 40) {
            iframeCounter = 0;
        } else {
            iframeCounter++;
        }

        
        
        if (!isFlickering) {
            drawPlayer(g2d);
        } else {
            if (iframeCounter >= 25 && iframeCounter <= 40) {
                drawPlayer(g2d);
            }
        }

        // Only uncomment if wanna see the hitbox.
        // g2d.setColor(Color.CYAN);
        // g2d.drawRect((int) screenX + hitbox.x, (int) screenY + hitbox.y, hitbox.width, hitbox.height);
        // g2d.drawRect((int) screenX, (int) screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2);

        // g2d.drawRect((int) x + hitbox.x, (int) y + hitbox.y, hitbox.width, hitbox.height);
        // g2d.drawRect((int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2);
    }
}
