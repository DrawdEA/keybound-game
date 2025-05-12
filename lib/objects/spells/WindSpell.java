package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;

import lib.render.CollisionManager;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.render.Direction;

public class WindSpell extends Spell {
    // Notice though that the final size of the spell is SPEED * maxAgeInTicks 
    private final double SPEED = 20;
    private final Color COLOR = Color.GRAY;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 15;

    private int incrementX;
    private int incrementY;

    private int animationCounter;
    private static BufferedImage[] wind;

    public static void initializeSprites() {
        wind = new BufferedImage[9];

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

  
        } catch (IOException e) { 
            System.out.println("IOException from FireSpell.java");
        }
    }

    public WindSpell(int casterId, double x, double y, Direction dir, int cId, int aC, double iX, double iY) {
        super("WIND_SPELL", casterId, x, y, 25, 25);

        this.x = x;
        this.y = y;

        incrementX = (int) iX;
        incrementY = (int) iY;

        casterId = cId;

        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE;
            incrementX = (int) incrementX + (int) TILE;
            this.y = y + TILE * 1.5;
            incrementY = (int) (incrementY + TILE * 1.5);
        } else if (dir == Direction.DOWN){
            this.x = x + TILE;
            incrementX = (int) incrementX + (int) TILE;
            this.y = y - TILE * 2.5;
            incrementY = (int) (incrementY - TILE * 2.5);
        } else if (dir == Direction.LEFT){
            this.x = x + TILE * 2;
            incrementX = (int) (incrementX + TILE * 2);
            this.y = y;
        } else if (dir == Direction.RIGHT){
            this.x = x - TILE * 2;
            incrementX = (int) (incrementX - TILE * 2);
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
            incrementY -= SPEED;
        } else if (dir == Direction.DOWN){
            y += SPEED;
            incrementY += SPEED;
        } else if (dir == Direction.LEFT){
            x -= SPEED;
            incrementX -= SPEED;
        } else if (dir == Direction.RIGHT){
            x += SPEED;
            incrementX += SPEED;
        }

        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }

        //System.out.println(animationCounter);
        animationCounter++;
    }

    @Override
    public String getDataString() {
        return String.format("WIND_SPELL-%f-%f-%s-%d-%d-%d-%d", x, y, dir.toString(), casterId, animationCounter, incrementX, incrementY);
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
        int currentFrame = (animationCounter / 2) % 9;
        System.out.println(currentFrame);
        g2d.drawImage(wind[currentFrame], (int) x - incrementX, (int) y - incrementY, 96, 96, null);

        g2d.setColor(COLOR);
        if (dir == Direction.DOWN || dir == Direction.UP){
            g2d.fill(new Ellipse2D.Double(incrementX, incrementY, TILE * 2, TILE * 4));
        } else {
            g2d.fill(new Ellipse2D.Double(incrementX, incrementY, TILE * 4, TILE * 2));
        }
    }    
}