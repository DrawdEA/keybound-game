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

    private int animationCounter;
    private static BufferedImage[] wind;

    public static void initializeSprites() {
        wind = new BufferedImage[9];

        try {
            BufferedImage fireImage = ImageIO.read(WindSpell.class.getResourceAsStream("/resources/spells/wind.png"));

            wind[0] = fireImage.getSubimage(0, 0, 48, 48);
            wind[1] = fireImage.getSubimage(48, 0, 48, 48);
            wind[2] = fireImage.getSubimage(96, 0, 48, 48);
            wind[3] = fireImage.getSubimage(0, 48, 48, 48);
            wind[4] = fireImage.getSubimage(48, 48, 48, 48);
            wind[5] = fireImage.getSubimage(96, 48, 48, 48);
            wind[6] = fireImage.getSubimage(0, 96, 48, 48);
            wind[7] = fireImage.getSubimage(48, 96, 48, 48);
            wind[8] = fireImage.getSubimage(96, 96, 48, 48);

  
        } catch (IOException e) { 
            System.out.println("IOException from FireSpell.java");
        }
    }

    public WindSpell(int casterId, double x, double y, Direction dir, int aC) {
        super("WIND_SPELL", casterId, x, y, 25, 25);

        this.x = x;
        this.y = y;

        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE;
            this.y = y + TILE * 1.5;
        } else if (dir == Direction.DOWN){
            this.x = x + TILE;
            this.y = y - TILE * 2.5;
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
        } else if (dir == Direction.DOWN){
            y += SPEED;
        } else if (dir == Direction.LEFT){
            x -= SPEED;
        } else if (dir == Direction.RIGHT){
            x += SPEED;
        }

        currAgeInTicks++;
        if(currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }

        animationCounter++;
    }

    @Override
    public String getDataString() {
        String data = String.format("WIND_SPELL-%f-%f-%s-%d", x, y, dir.toString(), animationCounter);
        System.out.println(data);
        return String.format("WIND_SPELL-%f-%f-%s-%d", x, y, dir.toString(), animationCounter);
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
        int currentFrame = (animationCounter / 5) % 9;
        g2d.drawImage(wind[currentFrame], (int) x - 48, (int) y - 48, 96, 96, null);

        g2d.setColor(COLOR);
        if (dir == Direction.DOWN || dir == Direction.UP){
            g2d.fill(new Ellipse2D.Double(x, y, TILE * 2, TILE * 4));
        } else {
            g2d.fill(new Ellipse2D.Double(x, y, TILE * 4, TILE * 2));
        }
    }    
}