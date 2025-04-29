/**
 * The PlayerVisuals class is responsible for controlling the sprite of the player.
 * This includes the player's idle, charging, running, and dying animation.
 */
package lib.render;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.*;
import lib.objects.*;

public class PlayerVisuals extends GameObject {
    public final int screenX, screenY;
    private boolean isPlayer;
    private BufferedImage[] playerSprites;
    private BufferedImage[] playerSpritesLeft;

    private String currentAnimation;
    private int currentFrame;
    private int animationCounter;
    private int animationIndex;

    private boolean lookingLeft;

    public PlayerVisuals(double xPosition, double yPosition, double s, boolean iP) {
        super(xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        // Set up the animation logic.
        currentFrame = 0;
        animationCounter = 0;
        animationIndex = 0;
        lookingLeft = false;

        // Generate the sprites.
        playerSprites = new BufferedImage[53];
        playerSpritesLeft = new BufferedImage[53];
        try {
            BufferedImage playerMovements = ImageIO.read(getClass().getResourceAsStream("/resources/player/Elite Mage Sprite Sheet.png"));

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
            //playerSprites[9] = playerMovements.getSubimage(0, 32, 64, 32);
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

            BufferedImage playerMovementsLeft = ImageIO.read(getClass().getResourceAsStream("/resources/player/Elite Mage Sprite Sheet_flipped.png"));

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
            //playerSprites[9] = playerMovements.getSubimage(0, 32, 64, 32);
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

            // Charging movements.
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
        } catch (IOException e) { 
            System.out.println("IOException from PlayerVisuals.java");
        }
        
    }

    public int getScreenX() {
        return screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    // Update the current frame and the animation of the player.
    public void updatePlayerAnimation(String animationType, String direction) {
        if (direction == "Right") {
            lookingLeft = false;
        } else if (direction == "Left") {
            lookingLeft = true;
        }

        if (animationCounter == GameConfig.ANIMATION_COUNTER) {
            if (animationType != currentAnimation) {
                currentFrame = 0;
            } else {
                currentFrame++;
            }

            int limit = 0;
            if (animationType == "Idle") {
                animationIndex = 0;
                limit = 7;
            } else if (animationType == "Running") {
                animationIndex = 8;
                limit = 7;
            }

            if (currentFrame >= limit) {
                currentFrame = 0;
            }

            animationIndex += currentFrame;
            currentAnimation = animationType;
            animationCounter = 0;
        }
        animationCounter++;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        if (lookingLeft) {
            if (isPlayer) {
                g2d.drawImage(playerSpritesLeft[animationIndex], screenX, screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            } else {
                g2d.drawImage(playerSpritesLeft[animationIndex], (int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            }
        } else {
            if (isPlayer) {
                g2d.drawImage(playerSprites[animationIndex], screenX, screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            } else {
                g2d.drawImage(playerSprites[animationIndex], (int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
            }
        }
        
    }
}
