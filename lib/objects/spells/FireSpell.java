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
    private final int TILE = GameConfig.TILE_SIZE;

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

    private static BufferedImage[] fireballLeft;
    private static BufferedImage[] impactLeft;

    private static BufferedImage[] fireballUp;
    private static BufferedImage[] impactUp;

    private static BufferedImage[] fireballDown;
    private static BufferedImage[] impactDown;

    public FireSpell(int casterId, double x, double y, Direction dir, int aC, boolean f) {
        super("FIRE_SPELL", casterId, x, y, 50, 25, dir);
        this.dir = dir;
        expired = false;
        id = casterId;
        finished = f;

        this.x = x;
        this.y = y;
        
        // Adjust to the spell to the right edge
        if (dir == Direction.UP){
            //this.x = x + TILE - 8;
            //this.y = y;
            width = 25;
            height = 50;
            hitbox = new Rectangle((int) (x - 5 + TILE - 8), (int) y, width, height);
        } else if (dir == Direction.DOWN){
            //this.x = x + TILE - 8;
            //this.y = y + TILE;
            width = 25;
            height = 50;
            hitbox = new Rectangle((int) (x - 5 + TILE - 8), (int) (y + TILE), width, height);
        } else if (dir == Direction.LEFT){
            //this.x = x;
            //this.y = y + TILE * 0.75 - 25/2;
            hitbox = new Rectangle((int) x - 5, (int) (y + TILE * 0.75 - 25/2), width, height);
        } else if (dir == Direction.RIGHT){
            //this.x = x + TILE * 2 - 25/2;
            //this.y = y + TILE * 0.75 - 25/2;
            hitbox = new Rectangle((int) (x - 5 + TILE * 2 - 25/2), (int) (y + TILE * 0.75 - 25/2), width, height);
        }

        //hitbox = new Rectangle((int) x - 5, (int) y, width, height);

        animationCounter = aC;
    }

    public static void initializeSprites() {
        fireball = new BufferedImage[4];
        impact = new BufferedImage[6];

        fireballLeft = new BufferedImage[4];
        impactLeft = new BufferedImage[6];

        fireballUp = new BufferedImage[4];
        impactUp = new BufferedImage[6];

        fireballDown = new BufferedImage[4];
        impactDown = new BufferedImage[6];

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

            BufferedImage fireImageLeft = ImageIO.read(FireSpell.class.getResourceAsStream("/resources/spells/fire_left.png"));

            fireballLeft[0] = fireImageLeft.getSubimage(0, 0, 48, 48);
            fireballLeft[1] = fireImageLeft.getSubimage(48, 0, 48, 48);
            fireballLeft[2] = fireImageLeft.getSubimage(96, 0, 48, 48);
            fireballLeft[3] = fireImageLeft.getSubimage(144, 0, 48, 48);

            impactLeft[0] = fireImageLeft.getSubimage(240, 0, 48, 48);
            impactLeft[1] = fireImageLeft.getSubimage(288, 0, 48, 48);
            impactLeft[2] = fireImageLeft.getSubimage(336, 0, 48, 48);
            impactLeft[3] = fireImageLeft.getSubimage(384, 0, 48, 48);
            impactLeft[4] = fireImageLeft.getSubimage(432, 0, 48, 48);
            impactLeft[5] = fireImageLeft.getSubimage(480, 0, 48, 48);

            BufferedImage fireImageUp = ImageIO.read(FireSpell.class.getResourceAsStream("/resources/spells/fire_up.png"));

            fireballUp[0] = fireImageUp.getSubimage(0, 0, 48, 48);
            fireballUp[1] = fireImageUp.getSubimage(48, 0, 48, 48);
            fireballUp[2] = fireImageUp.getSubimage(96, 0, 48, 48);
            fireballUp[3] = fireImageUp.getSubimage(144, 0, 48, 48);

            impactUp[0] = fireImageUp.getSubimage(240, 0, 48, 48);
            impactUp[1] = fireImageUp.getSubimage(288, 0, 48, 48);
            impactUp[2] = fireImageUp.getSubimage(336, 0, 48, 48);
            impactUp[3] = fireImageUp.getSubimage(384, 0, 48, 48);
            impactUp[4] = fireImageUp.getSubimage(432, 0, 48, 48);
            impactUp[5] = fireImageUp.getSubimage(480, 0, 48, 48);

            BufferedImage fireImageDown = ImageIO.read(FireSpell.class.getResourceAsStream("/resources/spells/fire_down.png"));

            fireballDown[0] = fireImageDown.getSubimage(0, 0, 48, 48);
            fireballDown[1] = fireImageDown.getSubimage(48, 0, 48, 48);
            fireballDown[2] = fireImageDown.getSubimage(96, 0, 48, 48);
            fireballDown[3] = fireImageDown.getSubimage(144, 0, 48, 48);

            impactDown[0] = fireImageDown.getSubimage(240, 0, 48, 48);
            impactDown[1] = fireImageDown.getSubimage(288, 0, 48, 48);
            impactDown[2] = fireImageDown.getSubimage(336, 0, 48, 48);
            impactDown[3] = fireImageDown.getSubimage(384, 0, 48, 48);
            impactDown[4] = fireImageDown.getSubimage(432, 0, 48, 48);
            impactDown[5] = fireImageDown.getSubimage(480, 0, 48, 48);
  
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
        if (dir == Direction.UP){
            hitbox = new Rectangle((int) (x - 5 + TILE - 8 + 32), (int) y, width, height);
        } else if (dir == Direction.DOWN){
            hitbox = new Rectangle((int) (x - 5 + TILE - 8 + 32), (int) (y + TILE - 24), width, height);
        } else if (dir == Direction.LEFT){
            hitbox = new Rectangle((int) x - 5, (int) (y + TILE * 0.75 - 25/2), width, height);
        } else if (dir == Direction.RIGHT){
            hitbox = new Rectangle((int) (x - 5 + TILE * 2 - 25/2), (int) (y + TILE * 0.75 - 25/2), width, height);
        }
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

            System.out.println("FIRE HIT!");
            playerHit.damagePlayer(1); // request to server for damage
        }
    }

    
    @Override
    public void drawSprite(Graphics2D g2d) {
        if (dir == Direction.LEFT) {
            if (finished) {
                int currentFrame = (animationCounter / 4) % 6;
                g2d.drawImage(impactLeft[currentFrame], (int) x - 4, (int) y - 48 + 12, 96, 96, null);
            } else {
                int currentFrame = (animationCounter / 4) % 4;
                g2d.drawImage(fireballLeft[currentFrame], (int) x - 4, (int) y - 48 + 12, 96, 96, null);
            }
        } else if (dir == Direction.UP) {
            if (finished) {
                int currentFrame = (animationCounter / 4) % 6;
                g2d.drawImage(impactUp[currentFrame], (int) (x - 48 + TILE - 8 + 28), (int) y, 96, 96, null);
            } else {
                int currentFrame = (animationCounter / 4) % 4;
                g2d.drawImage(fireballUp[currentFrame], (int) (x - 48 + TILE - 8 + 28), (int) y, 96, 96, null);
            }
        } else if (dir == Direction.DOWN) {
            if (finished) {
                int currentFrame = (animationCounter / 4) % 6;
                g2d.drawImage(impactDown[currentFrame], (int) x - 24 + TILE - 8 + 28, (int) (y - 48 + TILE * 0.75 - 25/2), 96, 96, null);
            } else {
                int currentFrame = (animationCounter / 4) % 4;
                g2d.drawImage(fireballDown[currentFrame], (int) x - 24 + TILE - 8 + 28, (int) (y - 48 + TILE * 0.75 - 25/2), 96, 96, null);
            }
        } else {
            if (finished) {
                int currentFrame = (animationCounter / 4) % 6;
                g2d.drawImage(impact[currentFrame], (int) x - 48 + 52, (int) (y - 48 + TILE * 0.75 - 25/2), 96, 96, null);
            } else {
                int currentFrame = (animationCounter / 4) % 4;
                g2d.drawImage(fireball[currentFrame], (int) x - 48 + 52, (int) (y - 48 + TILE * 0.75 - 25/2), 96, 96, null);
            }
        }
        

        // Hitboxing.
        // g2d.setColor(COLOR);
        // g2d.draw(hitbox);
    }
}