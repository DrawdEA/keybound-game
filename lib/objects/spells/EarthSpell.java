package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;

import lib.render.CollisionManager;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.render.Direction;

public class EarthSpell extends Spell {
    private final Color COLOR = Color.ORANGE;
    private final double WALL_OFFSET = 30;
    private final double TILE = GameConfig.TILE_SIZE;

    private double wallXLength;
    private double wallYLength;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private int wallHp;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 200;

    private int animationCounter;
    private boolean alive;

    private static BufferedImage[] wall;

    public EarthSpell(int casterId, double x, double y, Direction dir, int aC, boolean a) {
        super("EARTH_SPELL", casterId, x, y, 25, 25);
        animationCounter = aC;

        this.dir = dir;
        wallHp = 1;
        expired = false;
        alive = a;

        if (dir == Direction.LEFT || dir == Direction.RIGHT){
            wallXLength = 40;
            wallYLength = 75;
        } else {
            wallXLength = 40;
            wallYLength = 30;
        }

        if (dir == Direction.LEFT) {
            this.x = x - WALL_OFFSET;
            this.y = y + TILE * 1.5 - wallYLength/2;
        } else if (dir == Direction.RIGHT) {
            this.x = x + TILE * 2 + WALL_OFFSET;
            this.y = y + TILE * 1.5 - wallYLength/2;
        } else if (dir == Direction.DOWN) {
            this.x = x + TILE * 1.75 - wallXLength/2;
            this.y = y + TILE * 1.55 + WALL_OFFSET;
        } else if (dir == Direction.UP) {
            this.x = x + TILE * 1.75 - wallXLength/2;
            this.y = y - WALL_OFFSET;
        } 
    }

    public static void initializeSprites() {
        wall = new BufferedImage[16];

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
  
        } catch (IOException e) { 
            System.out.println("IOException from FireSpell.java");
        }
    }

    @Override
    public void update() {
        if (wallHp <= 0) {
            expired = true;
        }

        System.out.println("REALLY" + animationCounter);
        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks && alive) {
            
            alive = false;
            animationCounter = 32;
        }

        if (alive && animationCounter >= 32) {
            animationCounter = 32;
        }

        if (animationCounter >= 64) {
            System.out.println("EXPIRED!");
            expired = true;
        }

        animationCounter++;
    }

    @Override
    public String getDataString() {
        return String.format("EARTH_SPELL-%f-%f-%s-%d-%d-%s", x, y, dir.toString(), casterId, animationCounter, alive);
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        int currentFrame = (animationCounter / 4) % 16;
        if (currentFrame <= 7 || !alive) {
            g2d.drawImage(wall[currentFrame], (int) x - 50, (int) y - 40, 144, 144, null);
        } else {
            g2d.drawImage(wall[7], (int) x - 50, (int) y - 40, 144, 144, null);
        }
        
        g2d.setColor(COLOR);
        g2d.draw(new Rectangle2D.Double(x, y, wallXLength, wallYLength));
    }    
}