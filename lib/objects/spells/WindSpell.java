/**
 * The WindSpell class is a spell in the game.
 * It dashes the player in a specific direction.
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lib.GameConfig;
import lib.render.CollisionManager;
import lib.render.Direction;

public class WindSpell extends Spell {
    private final double SPEED = 10;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 30;

    private int incrementX;
    private int incrementY;

    private int animationCounter;
    private static BufferedImage[] wind;
    private static BufferedImage[] windLeft;
    private static BufferedImage[] windUp;
    private static BufferedImage[] windDown;

    /**
     * Initializes the sprites of the class for faster loading.
     */
    public static void initializeSprites() {
        wind = new BufferedImage[9];
        windLeft = new BufferedImage[9];
        windUp = new BufferedImage[9];
        windDown = new BufferedImage[9];

        try {
            BufferedImage windImage = ImageIO.read(WindSpell.class.getResourceAsStream("/resources/spells/wind.png"));

            wind[0] = windImage.getSubimage(0, 0, 48, 48);
            wind[1] = windImage.getSubimage(48, 0, 48, 48);
            wind[2] = windImage.getSubimage(96, 0, 48, 48);
            wind[3] = windImage.getSubimage(0, 48, 48, 48);
            wind[4] = windImage.getSubimage(48, 48, 48, 48);
            wind[5] = windImage.getSubimage(96, 48, 48, 48);
            wind[6] = windImage.getSubimage(0, 96, 48, 48);
            wind[7] = windImage.getSubimage(48, 96, 48, 48);
            wind[8] = windImage.getSubimage(96, 96, 48, 48);

            BufferedImage windImageLeft = ImageIO.read(WindSpell.class.getResourceAsStream("/resources/spells/wind_left.png"));

            windLeft[0] = windImageLeft.getSubimage(0, 0, 48, 48);
            windLeft[1] = windImageLeft.getSubimage(48, 0, 48, 48);
            windLeft[2] = windImageLeft.getSubimage(96, 0, 48, 48);
            windLeft[3] = windImageLeft.getSubimage(0, 48, 48, 48);
            windLeft[4] = windImageLeft.getSubimage(48, 48, 48, 48);
            windLeft[5] = windImageLeft.getSubimage(96, 48, 48, 48);
            windLeft[6] = windImageLeft.getSubimage(0, 96, 48, 48);
            windLeft[7] = windImageLeft.getSubimage(48, 96, 48, 48);
            windLeft[8] = windImageLeft.getSubimage(96, 96, 48, 48);

            BufferedImage windImageUp = ImageIO.read(WindSpell.class.getResourceAsStream("/resources/spells/wind_up.png"));

            windUp[0] = windImageUp.getSubimage(0, 0, 48, 48);
            windUp[1] = windImageUp.getSubimage(48, 0, 48, 48);
            windUp[2] = windImageUp.getSubimage(96, 0, 48, 48);
            windUp[3] = windImageUp.getSubimage(0, 48, 48, 48);
            windUp[4] = windImageUp.getSubimage(48, 48, 48, 48);
            windUp[5] = windImageUp.getSubimage(96, 48, 48, 48);
            windUp[6] = windImageUp.getSubimage(0, 96, 48, 48);
            windUp[7] = windImageUp.getSubimage(48, 96, 48, 48);
            windUp[8] = windImageUp.getSubimage(96, 96, 48, 48);

            BufferedImage windImageDown = ImageIO.read(WindSpell.class.getResourceAsStream("/resources/spells/wind_down.png"));

            windDown[0] = windImageDown.getSubimage(0, 0, 48, 48);
            windDown[1] = windImageDown.getSubimage(48, 0, 48, 48);
            windDown[2] = windImageDown.getSubimage(96, 0, 48, 48);
            windDown[3] = windImageDown.getSubimage(0, 48, 48, 48);
            windDown[4] = windImageDown.getSubimage(48, 48, 48, 48);
            windDown[5] = windImageDown.getSubimage(96, 48, 48, 48);
            windDown[6] = windImageDown.getSubimage(0, 96, 48, 48);
            windDown[7] = windImageDown.getSubimage(48, 96, 48, 48);
            windDown[8] = windImageDown.getSubimage(96, 96, 48, 48);

  
        } catch (IOException e) { 
            System.out.println("IOException from WindSpell.java");
        }
    }

    /**
     * Instantiates the wind spell.
     * 
     * @param casterId the id of the caster
     * @param x the x-value position of the spell
     * @param y the y-value position of the spell
     * @param dir the direction of the spell
     * @param aC the animationCounter of the spell
     * @param iX the x-value amount that the player has moved in total
     * @param iY the y-value amount that the player has moved in total
     */
    public WindSpell(int casterId, double x, double y, Direction dir, int aC, double iX, double iY) {
        super("WIND_SPELL", casterId, x, y, 25, 25);

        this.x = x;
        this.y = y;

        incrementX = (int) iX;
        incrementY = (int) iY;

        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE * 0.5;
            this.y = y + TILE * 1.5;
        } else if (dir == Direction.DOWN){
            this.x = x + TILE * 0.5;
            this.y = y - TILE * 1.5;
        } else if (dir == Direction.LEFT){
            this.x = x + TILE * 2;
            this.y = y;
        } else if (dir == Direction.RIGHT){
            this.x = x - TILE * 2;
            this.y = y;
        }

        this.dir = dir;
        expired = false;
        animationCounter = aC;
    }

    @Override
    public void update() {
        if (dir == Direction.UP){
            y -= SPEED;
            incrementY += SPEED;
        } else if (dir == Direction.DOWN){
            y += SPEED;
            incrementY += SPEED;
        } else if (dir == Direction.LEFT){
            x -= SPEED;
            incrementX += SPEED;
        } else if (dir == Direction.RIGHT){
            x += SPEED;
            incrementX += SPEED;
        }

        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }

        animationCounter++;
    }

    @Override
    public String getDataString() {
        return String.format("WIND_SPELL-%f-%f-%s-%d-%d-%d-%d", x, y, dir.toString(), casterId, animationCounter, incrementX, incrementY);
    }

    @Override
    public int handleCollisions(CollisionManager cm) {
        return 0; // Wind Spell does not deal damage.
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        int currentFrame = (animationCounter / 4) % 9;
        
        // Set up the location where the sprite will be drawn.
        int tmp = 0;
        int tmp2 = 0;
        if (dir == Direction.UP) {
            tmp2 = -incrementY;
        } else {
            tmp2 = incrementY;
        }
        if (dir == Direction.LEFT) {
            tmp = -incrementX;
        } else {
            tmp = incrementX;
        }

        if (dir == Direction.UP){
            g2d.drawImage(windUp[currentFrame], (int) x - tmp, (int) y - tmp2, 96, 96, null);
        } else if (dir == Direction.DOWN){
            g2d.drawImage(windDown[currentFrame], (int) x - tmp, (int) y - tmp2, 96, 96, null);
        } else if (dir == Direction.LEFT){
            g2d.drawImage(windLeft[currentFrame], (int) x - tmp, (int) y - tmp2, 96, 96, null);
        } else if (dir == Direction.RIGHT){
            g2d.drawImage(wind[currentFrame], (int) x - tmp, (int) y - tmp2, 96, 96, null);
        }

        
    }    
}