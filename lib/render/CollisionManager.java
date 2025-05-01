package lib.render;

import java.awt.Rectangle;

import lib.GameConfig;
import lib.input.KeyBindings;
import lib.objects.Environment;

public class CollisionManager {
    private Environment environment;
    private KeyBindings keyBindings;
    
    public CollisionManager(Environment e, KeyBindings kB) {
        environment = e;
        keyBindings = kB;
    }

    public boolean checkWorldCollision(PlayerObject playerObject, String direction) {
        Rectangle hitbox = playerObject.getHitbox();

        int playerLeftWorldX = (int) playerObject.getX() + hitbox.x;
        int playerRightWorldX = (int) playerObject.getX() + hitbox.x + hitbox.width;
        int playerTopWorldY = (int) playerObject.getY() + hitbox.y;
        int playerBottomWorldY = (int) playerObject.getY() + hitbox.y + hitbox.height;

        int playerLeftColumn = playerLeftWorldX / GameConfig.TILE_SIZE;
        int playerRightColumn = playerRightWorldX / GameConfig.TILE_SIZE;
        int playerTopRow = playerTopWorldY / GameConfig.TILE_SIZE;
        int playerBottomRow = playerBottomWorldY / GameConfig.TILE_SIZE;

        int tileNum1, tileNum2;
        boolean collided = false;

        if (direction == "Left") {
            playerLeftColumn = (int) Math.floor(((double) playerLeftWorldX - GameConfig.PLAYER_SPEED) / GameConfig.TILE_SIZE);;
            tileNum1 = environment.getMapNumbers()[playerLeftColumn][playerTopRow];
            tileNum2 = environment.getMapNumbers()[playerLeftColumn][playerBottomRow];
            if (environment.getTiles()[tileNum1].canCollide || environment.getTiles()[tileNum2].canCollide) {
                collided = true;
            }
        } else if (direction == "Right") {
            playerRightColumn = (int) Math.floor(((double) playerRightWorldX + GameConfig.PLAYER_SPEED) / GameConfig.TILE_SIZE);
                tileNum1 = environment.getMapNumbers()[playerRightColumn][playerTopRow];
                tileNum2 = environment.getMapNumbers()[playerRightColumn][playerBottomRow];
                if (environment.getTiles()[tileNum1].canCollide || environment.getTiles()[tileNum2].canCollide) {
                    collided = true;
                }
        } else if (direction == "Up") {
            playerTopRow = (int) Math.floor(((double) playerTopWorldY - GameConfig.PLAYER_SPEED) / GameConfig.TILE_SIZE);
                tileNum1 = environment.getMapNumbers()[playerLeftColumn][playerTopRow];
                tileNum2 = environment.getMapNumbers()[playerRightColumn][playerTopRow];
                if (environment.getTiles()[tileNum1].canCollide || environment.getTiles()[tileNum2].canCollide) {
                    collided = true;
                }
        } else if (direction == "Down") {
            playerBottomRow = (int) Math.floor(((double) playerBottomWorldY + GameConfig.PLAYER_SPEED) / GameConfig.TILE_SIZE);
                tileNum1 = environment.getMapNumbers()[playerLeftColumn][playerBottomRow];
                tileNum2 = environment.getMapNumbers()[playerRightColumn][playerBottomRow];
                if (environment.getTiles()[tileNum1].canCollide || environment.getTiles()[tileNum2].canCollide) {
                    collided = true;
                }
        }

        return collided;
    }

    public boolean checkProjectileCollision() {
        return false;
    }
}
