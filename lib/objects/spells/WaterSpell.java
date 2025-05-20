/**
 * The WaterSpell class is a spell in the game.
 * It spawns a water explosion at the player's wand.
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
package lib.objects.spells;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lib.GameConfig;
import lib.Sound;
import lib.objects.PlayerObject;
import lib.render.CollisionManager;
import lib.render.Direction;

public class WaterSpell extends Spell {
    private double x, y;
    private int width = 120, height = 140;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 60;

    private Rectangle hitbox;

    private int animationCounter;
    private static BufferedImage waterSprites[] = new BufferedImage[16];
    private static BufferedImage waterSpritesLeft[] = new BufferedImage[16];
    private static BufferedImage waterSpritesUp[] = new BufferedImage[16];
    private static BufferedImage waterSpritesDown[] = new BufferedImage[16];

    /**
     * Initializes the sprites of the class for faster loading.
     */
    public static void initializeSprites() {
        try {
            BufferedImage waterImage = ImageIO.read(WaterSpell.class.getResourceAsStream("/resources/spells/water.png"));

            waterSprites[0] = waterImage.getSubimage(0, 0, 64, 64);
            waterSprites[1] = waterImage.getSubimage(64, 0, 64, 64);
            waterSprites[2] = waterImage.getSubimage(128, 0, 64, 64);
            waterSprites[3] = waterImage.getSubimage(192, 0, 64, 64);
            waterSprites[4] = waterImage.getSubimage(0, 64, 64, 64);
            waterSprites[5] = waterImage.getSubimage(64, 64, 64, 64);
            waterSprites[6] = waterImage.getSubimage(128, 64, 64, 64);
            waterSprites[7] = waterImage.getSubimage(192, 64, 64, 64);
            waterSprites[8] = waterImage.getSubimage(0, 128, 64, 64);
            waterSprites[9] = waterImage.getSubimage(64, 128, 64, 64);
            waterSprites[10] = waterImage.getSubimage(128, 128, 64, 64);
            waterSprites[11] = waterImage.getSubimage(192, 128, 64, 64);
            waterSprites[12] = waterImage.getSubimage(0, 192, 64, 64);
            waterSprites[13] = waterImage.getSubimage(64, 192, 64, 64);
            waterSprites[14] = waterImage.getSubimage(128, 192, 64, 64);
            waterSprites[15] = waterImage.getSubimage(192, 192, 64, 64);    

            BufferedImage waterImageLeft = ImageIO.read(WaterSpell.class.getResourceAsStream("/resources/spells/water_left.png"));

            waterSpritesLeft[0] = waterImageLeft.getSubimage(0, 0, 64, 64);
            waterSpritesLeft[1] = waterImageLeft.getSubimage(64, 0, 64, 64);
            waterSpritesLeft[2] = waterImageLeft.getSubimage(128, 0, 64, 64);
            waterSpritesLeft[3] = waterImageLeft.getSubimage(192, 0, 64, 64);
            waterSpritesLeft[4] = waterImageLeft.getSubimage(0, 64, 64, 64);
            waterSpritesLeft[5] = waterImageLeft.getSubimage(64, 64, 64, 64);
            waterSpritesLeft[6] = waterImageLeft.getSubimage(128, 64, 64, 64);
            waterSpritesLeft[7] = waterImageLeft.getSubimage(192, 64, 64, 64);
            waterSpritesLeft[8] = waterImageLeft.getSubimage(0, 128, 64, 64);
            waterSpritesLeft[9] = waterImageLeft.getSubimage(64, 128, 64, 64);
            waterSpritesLeft[10] = waterImageLeft.getSubimage(128, 128, 64, 64);
            waterSpritesLeft[11] = waterImageLeft.getSubimage(192, 128, 64, 64);
            waterSpritesLeft[12] = waterImageLeft.getSubimage(0, 192, 64, 64);
            waterSpritesLeft[13] = waterImageLeft.getSubimage(64, 192, 64, 64);
            waterSpritesLeft[14] = waterImageLeft.getSubimage(128, 192, 64, 64);
            waterSpritesLeft[15] = waterImageLeft.getSubimage(192, 192, 64, 64);  

            BufferedImage waterImageUp = ImageIO.read(WaterSpell.class.getResourceAsStream("/resources/spells/water_up.png"));

            waterSpritesUp[0] = waterImageUp.getSubimage(0, 0, 64, 64);
            waterSpritesUp[1] = waterImageUp.getSubimage(64, 0, 64, 64);
            waterSpritesUp[2] = waterImageUp.getSubimage(128, 0, 64, 64);
            waterSpritesUp[3] = waterImageUp.getSubimage(192, 0, 64, 64);
            waterSpritesUp[4] = waterImageUp.getSubimage(0, 64, 64, 64);
            waterSpritesUp[5] = waterImageUp.getSubimage(64, 64, 64, 64);
            waterSpritesUp[6] = waterImageUp.getSubimage(128, 64, 64, 64);
            waterSpritesUp[7] = waterImageUp.getSubimage(192, 64, 64, 64);
            waterSpritesUp[8] = waterImageUp.getSubimage(0, 128, 64, 64);
            waterSpritesUp[9] = waterImageUp.getSubimage(64, 128, 64, 64);
            waterSpritesUp[10] = waterImageUp.getSubimage(128, 128, 64, 64);
            waterSpritesUp[11] = waterImageUp.getSubimage(192, 128, 64, 64);
            waterSpritesUp[12] = waterImageUp.getSubimage(0, 192, 64, 64);
            waterSpritesUp[13] = waterImageUp.getSubimage(64, 192, 64, 64);
            waterSpritesUp[14] = waterImageUp.getSubimage(128, 192, 64, 64);
            waterSpritesUp[15] = waterImageUp.getSubimage(192, 192, 64, 64);  

            BufferedImage waterImageDown = ImageIO.read(WaterSpell.class.getResourceAsStream("/resources/spells/water_down.png"));

            waterSpritesDown[0] = waterImageDown.getSubimage(0, 0, 64, 64);
            waterSpritesDown[1] = waterImageDown.getSubimage(64, 0, 64, 64);
            waterSpritesDown[2] = waterImageDown.getSubimage(128, 0, 64, 64);
            waterSpritesDown[3] = waterImageDown.getSubimage(192, 0, 64, 64);
            waterSpritesDown[4] = waterImageDown.getSubimage(0, 64, 64, 64);
            waterSpritesDown[5] = waterImageDown.getSubimage(64, 64, 64, 64);
            waterSpritesDown[6] = waterImageDown.getSubimage(128, 64, 64, 64);
            waterSpritesDown[7] = waterImageDown.getSubimage(192, 64, 64, 64);
            waterSpritesDown[8] = waterImageDown.getSubimage(0, 128, 64, 64);
            waterSpritesDown[9] = waterImageDown.getSubimage(64, 128, 64, 64);
            waterSpritesDown[10] = waterImageDown.getSubimage(128, 128, 64, 64);
            waterSpritesDown[11] = waterImageDown.getSubimage(192, 128, 64, 64);
            waterSpritesDown[12] = waterImageDown.getSubimage(0, 192, 64, 64);
            waterSpritesDown[13] = waterImageDown.getSubimage(64, 192, 64, 64);
            waterSpritesDown[14] = waterImageDown.getSubimage(128, 192, 64, 64);
            waterSpritesDown[15] = waterImageDown.getSubimage(192, 192, 64, 64);  
        } catch (IOException e) { 
            System.out.println("IOException from WaterSpell.java");
        }
    }

    /**
     * Instantiates the water spell.
     * 
     * @param casterId the id of the caster
     * @param x the x-value position of the spell
     * @param y the y-value position of the spell
     * @param dir the direction of the spell
     * @param aC the animationCounter of the spell
     */
    public WaterSpell(int casterId, double x, double y, Direction dir, int aC) {
        super("WATER_SPELL", casterId, x, y, 25, 25, dir);
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.animationCounter = aC;
        expired = false;

        if (dir == Direction.UP){
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE / 2 - 23, (int) y - GameConfig.TILE_SIZE + 10 - 90, height, width);
        } else if (dir == Direction.DOWN){
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE / 2 - 23, (int) y - GameConfig.TILE_SIZE + 10 + 105, height, width);
        } else if (dir == Direction.LEFT){
            hitbox = new Rectangle((int) x - GameConfig.TILE_SIZE * 3 - GameConfig.TILE_SIZE / 2 + 10, (int) y - GameConfig.TILE_SIZE + 10, width, height);
        } else {
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE * 3 + GameConfig.TILE_SIZE / 2, (int) y - GameConfig.TILE_SIZE + 10, width, height);
        }
    }

    @Override
    public void update() {
        // Set the spell to expire if it's too old
        currAgeInTicks++;
        if (currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }

        animationCounter++;
    }

    @Override
    public String getDataString() {
        return String.format("WATER_SPELL-%f-%f-%s-%d-%d", x, y, dir.toString(), casterId, animationCounter);
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public int handleCollisions(CollisionManager cm) {
        PlayerObject playerHit = cm.checkProjectileCollision(hitbox, casterId);
        if (playerHit != null && playerHit.isDamageable()) {
            Sound hitSound = new Sound(4);
            hitSound.play();
            return playerHit.getId();
        } else {
            return 0;
        }
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        int currentFrame = (animationCounter / 4) % 16;
        if (dir == Direction.UP){
            g2d.drawImage(waterSpritesUp[currentFrame], (int) x - 28, (int) y - GameConfig.TILE_SIZE - GameConfig.TILE_SIZE / 2 - 64, 192, 192, null);
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE / 2 - 23, (int) y - GameConfig.TILE_SIZE + 10 - 90, height, width);
        } else if (dir == Direction.DOWN){
            g2d.drawImage(waterSpritesDown[currentFrame], (int) x - 36, (int) y - GameConfig.TILE_SIZE - GameConfig.TILE_SIZE / 2 + 60, 192, 192, null);
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE / 2 - 23, (int) y - GameConfig.TILE_SIZE + 10 + 105, height, width);
        } else if (dir == Direction.LEFT){
            g2d.drawImage(waterSpritesLeft[currentFrame], (int) x - GameConfig.TILE_SIZE * 3 + 5, (int) y - GameConfig.TILE_SIZE - GameConfig.TILE_SIZE / 2, 192, 192, null);
            hitbox = new Rectangle((int) x - GameConfig.TILE_SIZE * 3 - GameConfig.TILE_SIZE / 2 + 10, (int) y - GameConfig.TILE_SIZE + 10, width, height);
        } else {
            g2d.drawImage(waterSprites[currentFrame], (int) x + GameConfig.TILE_SIZE, (int) y - GameConfig.TILE_SIZE - GameConfig.TILE_SIZE / 2, 192, 192, null);
            hitbox = new Rectangle((int) x + GameConfig.TILE_SIZE * 3 + GameConfig.TILE_SIZE / 2, (int) y - GameConfig.TILE_SIZE + 10, width, height);
        }
    }

}
