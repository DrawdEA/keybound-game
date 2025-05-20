 /**
 * The EarthSpell class is a spell in the game.
 * It spawns a stationary rock that deals damage when touched.
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

public class EarthSpell extends Spell {
    private final int WALL_OFFSET = 30;
    private final int TILE = GameConfig.TILE_SIZE;

    private int wallXLength;
    private int wallYLength;

    private double x, y;
    private Direction dir;

    private int wallHp;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 200;

    private int animationCounter;
    private boolean alive;

    private Rectangle hitbox;

    private static BufferedImage[] wall;
    private static BufferedImage[] wallLeft;
    private static BufferedImage[] wallUpdown;

    /**
     * Initializes the sprites of the class for faster loading.
     */
    public static void initializeSprites() {
        wall = new BufferedImage[16];
        wallLeft = new BufferedImage[16];
        wallUpdown = new BufferedImage[9];

        try {
            BufferedImage earthImage = ImageIO.read(EarthSpell.class.getResourceAsStream("/resources/spells/earth.png"));

            wall[0] = earthImage.getSubimage(0, 0, 48, 48);
            wall[1] = earthImage.getSubimage(48, 0, 48, 48);
            wall[2] = earthImage.getSubimage(96, 0, 48, 48);
            wall[3] = earthImage.getSubimage(144, 0, 48, 48);
            wall[4] = earthImage.getSubimage(0, 48, 48, 48);
            wall[5] = earthImage.getSubimage(48, 48, 48, 48);
            wall[6] = earthImage.getSubimage(96, 48, 48, 48);
            wall[7] = earthImage.getSubimage(144, 48, 48, 48);
            wall[8] = earthImage.getSubimage(0, 96, 48, 48);
            wall[9] = earthImage.getSubimage(48, 96, 48, 48);
            wall[10] = earthImage.getSubimage(96, 96, 48, 48);
            wall[11] = earthImage.getSubimage(144, 96, 48, 48);
            wall[12] = earthImage.getSubimage(0, 144, 48, 48);
            wall[13] = earthImage.getSubimage(48, 144, 48, 48);
            wall[14] = earthImage.getSubimage(96, 144, 48, 48);
            wall[15] = earthImage.getSubimage(144, 144, 48, 48);

            BufferedImage earthImageLeft = ImageIO.read(EarthSpell.class.getResourceAsStream("/resources/spells/earth_left.png"));

            wallLeft[0] = earthImageLeft.getSubimage(0, 0, 48, 48);
            wallLeft[1] = earthImageLeft.getSubimage(48, 0, 48, 48);
            wallLeft[2] = earthImageLeft.getSubimage(96, 0, 48, 48);
            wallLeft[3] = earthImageLeft.getSubimage(144, 0, 48, 48);
            wallLeft[4] = earthImageLeft.getSubimage(0, 48, 48, 48);
            wallLeft[5] = earthImageLeft.getSubimage(48, 48, 48, 48);
            wallLeft[6] = earthImageLeft.getSubimage(96, 48, 48, 48);
            wallLeft[7] = earthImageLeft.getSubimage(144, 48, 48, 48);
            wallLeft[8] = earthImageLeft.getSubimage(0, 96, 48, 48);
            wallLeft[9] = earthImageLeft.getSubimage(48, 96, 48, 48);
            wallLeft[10] = earthImageLeft.getSubimage(96, 96, 48, 48);
            wallLeft[11] = earthImageLeft.getSubimage(144, 96, 48, 48);
            wallLeft[12] = earthImageLeft.getSubimage(0, 144, 48, 48);
            wallLeft[13] = earthImageLeft.getSubimage(48, 144, 48, 48);
            wallLeft[14] = earthImageLeft.getSubimage(96, 144, 48, 48);
            wallLeft[15] = earthImageLeft.getSubimage(144, 144, 48, 48);

            BufferedImage earthImageUpdown = ImageIO.read(EarthSpell.class.getResourceAsStream("/resources/spells/earth_updown.png"));

            wallUpdown[0] = earthImageUpdown.getSubimage(448, 0, 64, 64);
            wallUpdown[1] = earthImageUpdown.getSubimage(448, 64, 64, 64);
            wallUpdown[2] = earthImageUpdown.getSubimage(448, 128, 64, 64);
            wallUpdown[3] = earthImageUpdown.getSubimage(448, 192, 64, 64);
            wallUpdown[4] = earthImageUpdown.getSubimage(448, 256, 64, 64);
            wallUpdown[5] = earthImageUpdown.getSubimage(448, 320, 64, 64);
            wallUpdown[6] = earthImageUpdown.getSubimage(448, 384, 64, 64);
            wallUpdown[7] = earthImageUpdown.getSubimage(448, 448, 64, 64);
            wallUpdown[8] = earthImageUpdown.getSubimage(448, 512, 64, 64);

        } catch (IOException e) { 
            System.out.println("IOException from EarthSpell.java");
        }
    }

    /**
     * Instantiates the earth spell.
     * 
     * @param casterId the id of the caster
     * @param x the x-value position of the spell
     * @param y the y-value position of the spell
     * @param dir the direction of the spell
     * @param aC the animationCounter of the spell
     * @param a a boolean whether the wall is still alive or not
     */
    public EarthSpell(int casterId, double x, double y, Direction dir, int aC, boolean a) {
        super("EARTH_SPELL", casterId, x, y, 25, 25);
        animationCounter = aC;

        this.x = x;
        this.y = y;

        this.dir = dir;
        wallHp = 1;
        expired = false;
        alive = a;

        // Initialize the wall's size and hitbox depending on the direction.
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            wallXLength = 40;
            wallYLength = 75;
        } else {
            wallXLength = 124;
            wallYLength = 62;
        }
        
        if (dir == Direction.LEFT) {
            hitbox = new Rectangle((int) x - WALL_OFFSET - TILE / 2, (int) (y  + TILE * 1.5 - wallYLength/2 - 16), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.RIGHT) {
            hitbox = new Rectangle((int) x + TILE * 3 + WALL_OFFSET + 7, (int) (y + TILE * 1.5 - wallYLength/2 - 16), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.DOWN) {
            hitbox = new Rectangle((int) (x - 68 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) (y - 84 + TILE * 1.55 + WALL_OFFSET + 16 + 64), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.UP) {
            hitbox = new Rectangle((int) (x - 68 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) (y - 16 - WALL_OFFSET - 16 - 8), (int) wallXLength, (int) wallYLength);
        } 
    }

    @Override
    public void update() {
        if (wallHp <= 0) {
            expired = true;
        }

        // Make the animationCounter stay at 32 or 21 depending on the direction to make it stay at the state where it is standing.
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            currAgeInTicks++;
            if(currAgeInTicks >= maxAgeInTicks && alive) {
                alive = false;
                animationCounter = 32;
            }

            if (alive && animationCounter >= 32) {
                animationCounter = 32;
            }

            if (animationCounter >= 64) {
                expired = true;
            }
        } else {
            currAgeInTicks++;
            if(currAgeInTicks >= maxAgeInTicks && alive) {
                alive = false;
                animationCounter = 32;
            }

            if (alive && animationCounter >= 21) {
                animationCounter = 21;
            }

            if (animationCounter >= 63) {
                expired = true;
            }
        }
        
        animationCounter++;
    }

    @Override
    public String getDataString() {
        return String.format("EARTH_SPELL-%f-%f-%s-%d-%d-%s", x, y, dir.toString(), casterId, animationCounter, alive);
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
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        if (dir == Direction.LEFT) {
            int currentFrame = (animationCounter / 4) % 16;
            if (currentFrame <= 7 || !alive) {
                g2d.drawImage(wallLeft[currentFrame], (int) this.x - 50 - WALL_OFFSET - TILE / 2, (int) (this.y - 40 + TILE * 1.5 - wallYLength/2 - 16), 144, 144, null);
            } else {
                g2d.drawImage(wallLeft[7], (int) x - 50 - WALL_OFFSET - TILE / 2, (int) (y - 40 + TILE * 1.5 - wallYLength/2 - 16), 144, 144, null);
            }
            hitbox = new Rectangle((int) x - WALL_OFFSET - TILE / 2, (int) (y + TILE * 1.5 - wallYLength/2 - 16), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.RIGHT) {
            int currentFrame = (animationCounter / 4) % 16;
            if (currentFrame <= 7 || !alive) {
                g2d.drawImage(wall[currentFrame], (int) x - 50 + TILE * 3 + WALL_OFFSET + 7, (int) (y - 40 + TILE * 1.5 - wallYLength/2 - 16), 144, 144, null);
            } else {
                g2d.drawImage(wall[7], (int) x - 50 + TILE * 3 + WALL_OFFSET + 7, (int) (y - 40 + TILE * 1.5 - wallYLength/2 - 16), 144, 144, null);
            }
            hitbox = new Rectangle((int) (x + TILE * 3 + WALL_OFFSET + 7), (int) (y + TILE * 1.5 - wallYLength/2 - 16), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.DOWN) {
            int currentFrame = (animationCounter / 7) % 9;
            if (currentFrame <= 2 || !alive) {
                g2d.drawImage(wallUpdown[currentFrame], (int) (x - 80 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) (y - 164 + TILE * 1.55 + WALL_OFFSET + 16 + 64), 144, 144, null);
            } else {
                g2d.drawImage(wallUpdown[2], (int) (x - 80 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) (y - 164 + TILE * 1.55 + WALL_OFFSET + 16 + 64), 144, 144, null);
            }
            hitbox = new Rectangle((int) (x - 68 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) (y - 84 + TILE * 1.55 + WALL_OFFSET + 16 + 64), (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.UP) {
            int currentFrame = (animationCounter / 7) % 9;
            if (currentFrame <= 2 || !alive) {
                g2d.drawImage(wallUpdown[currentFrame], (int) (x - 80 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) y - 96 - WALL_OFFSET - 16 - 8, 144, 144, null);
            } else {
                g2d.drawImage(wallUpdown[2], (int) (x - 80 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) y - 96 - WALL_OFFSET - 16 - 8, 144, 144, null);
            }
            hitbox = new Rectangle((int) (x - 68 + TILE * 1.75 - wallXLength/2 + 41 + 35), (int) y - 16 - WALL_OFFSET - 16 - 8, (int) wallXLength, (int) wallYLength);
        }
    }    
}