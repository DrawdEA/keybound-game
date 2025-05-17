package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import lib.GameConfig;
import lib.render.CollisionManager;
import lib.render.Direction;

public class WindSpell extends Spell {
    // Notice though that the final size of the spell is SPEED * maxAgeInTicks 
    private final double SPEED = 10;
    private final Color COLOR = Color.GRAY;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private double width = 25, height = 25;
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
        // Wind Spell will never deal damage
        return 0;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        int currentFrame = (animationCounter / 4) % 9;
        
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