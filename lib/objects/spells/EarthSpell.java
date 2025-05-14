package lib.objects.spells;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import lib.render.CollisionManager;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.render.Direction;
import lib.render.PlayerObject;

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

    private Rectangle hitbox;

    private static BufferedImage[] wall;
    private static BufferedImage[] wallLeft;
    private static BufferedImage[] wallUpdown;

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
            wallXLength = 124;
            wallYLength = 62;
        }

        if (dir == Direction.LEFT) {
            this.x = x - WALL_OFFSET - TILE / 2;
            this.y = y + TILE * 1.5 - wallYLength/2 - 16;
            hitbox = new Rectangle((int) x, (int) y, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.RIGHT) {
            this.x = x + TILE * 2 + WALL_OFFSET;
            this.y = y + TILE * 1.5 - wallYLength/2 - 16;
            hitbox = new Rectangle((int) x, (int) y, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.DOWN) {
            this.x = x + TILE * 1.75 - wallXLength/2 + 41;
            this.y = y + TILE * 1.55 + WALL_OFFSET + 16;
            hitbox = new Rectangle((int) x - 68, (int) y - 84, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.UP) {
            this.x = x + TILE * 1.75 - wallXLength/2 + 41;
            this.y = y - WALL_OFFSET - 16;
            hitbox = new Rectangle((int) x - 68, (int) y - 16, (int) wallXLength, (int) wallYLength);
        } 
    }

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

    @Override
    public void update() {
        if (wallHp <= 0) {
            expired = true;
        }

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
    public void handleCollisions(CollisionManager cm) {
        PlayerObject playerHit = cm.checkProjectileCollision(hitbox, casterId);
        if (playerHit != null) {
            System.out.println("EARTH HIT!");
            playerHit.damagePlayer(1); // request to server for damage
        }
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void drawSprite(Graphics2D g2d) {
        g2d.setColor(COLOR);
        

        if (dir == Direction.LEFT) {
            int currentFrame = (animationCounter / 4) % 16;
            if (currentFrame <= 7 || !alive) {
                g2d.drawImage(wallLeft[currentFrame], (int) x - 50, (int) y - 40, 144, 144, null);
            } else {
                g2d.drawImage(wallLeft[7], (int) x - 50, (int) y - 40, 144, 144, null);
            }
            hitbox = new Rectangle((int) x, (int) y, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.RIGHT) {
            int currentFrame = (animationCounter / 4) % 16;
            if (currentFrame <= 7 || !alive) {
                g2d.drawImage(wall[currentFrame], (int) x - 50, (int) y - 40, 144, 144, null);
            } else {
                g2d.drawImage(wall[7], (int) x - 50, (int) y - 40, 144, 144, null);
            }
            hitbox = new Rectangle((int) x, (int) y, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.DOWN) {
            int currentFrame = (animationCounter / 7) % 9;
            if (currentFrame <= 2 || !alive) {
                g2d.drawImage(wallUpdown[currentFrame], (int) x - 80, (int) y - 164, 144, 144, null);
            } else {
                g2d.drawImage(wallUpdown[2], (int) x - 80, (int) y - 164, 144, 144, null);
            }
            hitbox = new Rectangle((int) x - 68, (int) y - 84, (int) wallXLength, (int) wallYLength);
        } else if (dir == Direction.UP) {
            int currentFrame = (animationCounter / 7) % 9;
            if (currentFrame <= 2 || !alive) {
                g2d.drawImage(wallUpdown[currentFrame], (int) x - 80, (int) y - 96, 144, 144, null);
            } else {
                g2d.drawImage(wallUpdown[2], (int) x - 80, (int) y - 96, 144, 144, null);
            }
            hitbox = new Rectangle((int) x - 68, (int) y - 16, (int) wallXLength, (int) wallYLength);
        } 
        
        g2d.draw(hitbox);
    }    
}