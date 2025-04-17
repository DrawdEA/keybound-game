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
    private String currentAnimation;
    private int currentFrame;

    public PlayerVisuals(double xPosition, double yPosition, double s, boolean iP) {
        super(xPosition, yPosition, s, s);
        isPlayer = iP;
        
        screenX = GameConfig.SCREEN_LENGTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        // Set up the animation logic.
        currentAnimation = "Idle";
        currentFrame = 0;

        // Generate the sprites.
        playerSprites = new BufferedImage[53];
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
            playerSprites[8] = playerMovements.getSubimage(0, 32, 64, 32);
            playerSprites[9] = playerMovements.getSubimage(64, 32, 64, 32);
            playerSprites[10] = playerMovements.getSubimage(128, 32, 64, 32);
            playerSprites[11] = playerMovements.getSubimage(192, 32, 64, 32);
            playerSprites[12] = playerMovements.getSubimage(256, 32, 64, 32);
            playerSprites[13] = playerMovements.getSubimage(320, 32, 64, 32);
            playerSprites[14] = playerMovements.getSubimage(384, 32, 64, 32);
            playerSprites[15] = playerMovements.getSubimage(448, 32, 64, 32);

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

    @Override
    public void drawSprite(Graphics2D g2d) {
        if (isPlayer) {
            g2d.drawImage(playerSprites[0], screenX, screenY, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
        } else {
            g2d.drawImage(playerSprites[0], (int) x, (int) y, GameConfig.TILE_SIZE * 4, GameConfig.TILE_SIZE * 2, null);
        }
    }
}
