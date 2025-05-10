package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.render.CollisionManager;
import lib.render.Direction;

public class WaterSpell extends Spell {
    // Notice though that the final size of the spell is SPEED * maxAgeInTicks 
    private final double SPEED = 5;
    private final Color COLOR = Color.CYAN;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private double width = 25, height = 25;
    private Direction dir;

    private double endingBar;
    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 60;

    private static BufferedImage waterSprites[] = new BufferedImage[16];
    private int animationCounter;
    private int currentFrame;

    public static void initializeSprites() {
        /* try {
            BufferedImage waterImage = ImageIO.read(getClass().getResourceAsStream("/resources/spells/water.png"));

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
        } catch (IOException e) { 
            System.out.println("IOException from WaterSpell.java");
        } */
    }

    // Initial Creation
    public WaterSpell(int casterId, double x, double y, Direction dir) {
        super("WATER_SPELL", casterId, x, y, 25, 25, dir);

        this.x = adjustToPlayerEdgeCenterX(x, dir);
        this.y = adjustToPlayerEdgeCenterY(y, dir);
        this.dir = dir;

        // Initialize endingBar to match axis of initial direction
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            endingBar = adjustToPlayerEdgeCenterX(x, dir);
        } else if (dir == Direction.UP || dir == Direction.DOWN) {
            endingBar = adjustToPlayerEdgeCenterY(y, dir);
        }

        expired = false;
    }

    // Static Rendering
    public WaterSpell(int casterId, double x, double y, Direction dir, double endingBar, int animationCounter, int currentFrame) {
        super("WATER_SPELL", casterId, x, y, 25, 25, dir);

        this.x = x;
        this.y = y;
        this.dir = dir;
        this.endingBar = endingBar;

        this.animationCounter = animationCounter;
        this.currentFrame = currentFrame;

        expired = false;
    }

    @Override
    public void update() {
        // Grow the triangle based on the direction facing in and axis
        if (dir == Direction.RIGHT || dir == Direction.DOWN) {
            endingBar += SPEED;
        } else if (dir == Direction.LEFT || dir == Direction.UP) {
            endingBar -= SPEED;
        }

        // Set the spell to expire if it's too old
        currAgeInTicks++;
        if (currAgeInTicks >= maxAgeInTicks) {
            expired = true;
        }
    }

    @Override
    public String getDataString() {
        return String.format("WATER_SPELL-%f-%f-%s-%f-%d-%d", x, y, dir.toString(), endingBar, animationCounter, currentFrame);
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        System.out.println(animationCounter);
        if (animationCounter == 10) {
            animationCounter = 0;
            g2d.drawImage(waterSprites[currentFrame], (int) x, (int) y, 64, 64, null);
            currentFrame++;
        }

        System.out.println(animationCounter);
        animationCounter++;

        g2d.setColor(COLOR);
        
        // Calculate the triangle dimensions for an equilateral triangle
        double triangleLength = Math.abs(endingBar - (dir == Direction.LEFT || dir == Direction.RIGHT ? x : y));
        
        // For an equilateral triangle, the height-to-base ratio is sqrt(3)/2
        // So base = 2 * height / sqrt(3)
        double triangleBase = 2 * triangleLength / Math.sqrt(3);
        
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        switch (dir) {
            case RIGHT:
                // Vertex at player, flaring to the right
                xPoints[0] = (int) x;
                yPoints[0] = (int) y;
                xPoints[1] = (int) endingBar;
                yPoints[1] = (int) (y - triangleBase/2);
                xPoints[2] = (int) endingBar;
                yPoints[2] = (int) (y + triangleBase/2);
                break;
                
            case LEFT:
                // Vertex at player, flaring to the left
                xPoints[0] = (int) x;
                yPoints[0] = (int) y;
                xPoints[1] = (int) endingBar;
                yPoints[1] = (int) (y - triangleBase/2);
                xPoints[2] = (int) endingBar;
                yPoints[2] = (int) (y + triangleBase/2);
                break;
                
            case DOWN:
                // Vertex at player, flaring downward
                xPoints[0] = (int) x;
                yPoints[0] = (int) y;
                xPoints[1] = (int) (x - triangleBase/2);
                yPoints[1] = (int) endingBar;
                xPoints[2] = (int) (x + triangleBase/2);
                yPoints[2] = (int) endingBar;
                break;
                
            case UP:
                // Vertex at player, flaring upward
                xPoints[0] = (int) x;
                yPoints[0] = (int) y;
                xPoints[1] = (int) (x - triangleBase/2);
                yPoints[1] = (int) endingBar;
                xPoints[2] = (int) (x + triangleBase/2);
                yPoints[2] = (int) endingBar;
                break;
        }
        
        g2d.fill(new Polygon(xPoints, yPoints, 3));
    }

    
    public final double adjustToPlayerEdgeCenterX(double x, Direction dir){
        if (dir == Direction.DOWN || dir == direction.UP) {
            return x + PLAYER_X_WIDTH/2; 
        } else if (dir == Direction.RIGHT){
            return x + PLAYER_X_WIDTH;
        } else {
            return x;
        }
    }

    public final double adjustToPlayerEdgeCenterY(double y, Direction dir){
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            return y + PLAYER_Y_WIDTH / 2;
        } else if (dir == Direction.DOWN) {
            return y + PLAYER_Y_WIDTH;
        } else {
            return y;
        }
    }
}
