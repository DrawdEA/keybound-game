package lib.objects.spells;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.render.CollisionManager;
import lib.render.Direction;
import lib.render.PlayerObject;

public class FireSpell extends Spell {
    // Notice that SPEED * maxAgeInTicks is the distance the fireball can travel
    private final double SPEED = 7;
    private final Color COLOR = Color.RED;
    private final double TILE = GameConfig.TILE_SIZE;

    private double x, y;
    private int width = 50, height = 25;
    private Direction dir;

    private boolean expired;
    private int currAgeInTicks = 0;
    private final int maxAgeInTicks = 60;

    private Rectangle hitbox;

    private int id;

    boolean finished;

    private int animationCounter;

    private static BufferedImage[] fireball;
    private static BufferedImage[] impact;

    public FireSpell(int casterId, double x, double y, Direction dir, int aC, boolean f) {
        super("FIRE_SPELL", casterId, x, y, 50, 25, dir);
        this.dir = dir;
        expired = false;
        id = casterId;
        finished = f;
        
        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            this.x = x + TILE - 25/2;
            this.y = y;
        } else if (dir == Direction.DOWN){
            this.x = x + TILE - 25/2;
            this.y = y + TILE;
        } else if (dir == Direction.LEFT){
            this.x = x;
            this.y = y + TILE * 0.75 - 25/2;
        } else if (dir == Direction.RIGHT){
            this.x = x + TILE * 2 - 25/2;
            this.y = y + TILE * 0.75 - 25/2;
        }

        hitbox = new Rectangle((int) x - 5, (int) y, width, height);

        animationCounter = aC;
    }

    public static void initializeSprites() {
        fireball = new BufferedImage[4];
        impact = new BufferedImage[6];

        try {
            BufferedImage fireImage = ImageIO.read(FireSpell.class.getResourceAsStream("/resources/spells/fire.png"));

            fireball[0] = fireImage.getSubimage(0, 0, 48, 48);
            fireball[1] = fireImage.getSubimage(48, 0, 48, 48);
            fireball[2] = fireImage.getSubimage(96, 0, 48, 48);
            fireball[3] = fireImage.getSubimage(144, 0, 48, 48);

            impact[0] = fireImage.getSubimage(240, 0, 48, 48);
            impact[1] = fireImage.getSubimage(288, 0, 48, 48);
            impact[2] = fireImage.getSubimage(336, 0, 48, 48);
            impact[3] = fireImage.getSubimage(384, 0, 48, 48);
            impact[4] = fireImage.getSubimage(432, 0, 48, 48);
            impact[5] = fireImage.getSubimage(480, 0, 48, 48);
  
        } catch (IOException e) { 
            System.out.println("IOException from FireSpell.java");
        }
    }

    public String getDataString() {
        String spellString = "";
        spellString += String.format("FIRE_SPELL-%f-%f-%s-%d-%d-%s", x, y, dir.toString(), casterId, animationCounter, finished);
        return spellString; 
    }

    public void update() {
        // Move the fire ball in the direction the player is facing
        if (!finished) {
            if (dir == Direction.UP){
                y -= SPEED;
            } else if (dir == Direction.DOWN) {
                y += SPEED;
            } else if (dir == Direction.LEFT) {
                x -= SPEED;
            } else if (dir == Direction.RIGHT) {
                x += SPEED;
            }
        }
        

        // Set the spell to expire if it's too old or if the explosion animation has finished.
        currAgeInTicks++;
        if((currAgeInTicks == maxAgeInTicks) || (finished && animationCounter >= 20)) {
            expired = true;
        }

        animationCounter++;
        // Hitboxing.
        hitbox = new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void handleCollisions(CollisionManager cm) {
        

        PlayerObject playerHit = cm.checkProjectileCollision(hitbox, id);
        if (playerHit != null && !finished) {
            finished = true;
            animationCounter = 0;

            System.out.println("FIRE SPELL HIT!");
            playerHit.damagePlayer(1); // request to server for damage
        }
    }

    
    @Override
    public void drawSprite(Graphics2D g2d) {
        if (finished) {
            int currentFrame = (animationCounter / 4) % 6;
            g2d.drawImage(impact[currentFrame], (int) x - 48, (int) y - 48, 96, 96, null);
        } else {
            int currentFrame = (animationCounter / 4) % 4;
            g2d.drawImage(fireball[currentFrame], (int) x - 48, (int) y - 48, 96, 96, null);
        }

        // Hitboxing.
        g2d.setColor(COLOR);
        g2d.draw(hitbox);
    }
}