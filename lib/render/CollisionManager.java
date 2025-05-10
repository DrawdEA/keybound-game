package lib.render;

import java.awt.Rectangle;
import java.util.ArrayList;

import lib.GameConfig;
import lib.network.Player;
import lib.objects.Environment;

public class CollisionManager {
    private Environment environment;
    private ArrayList<PlayerObject> players;
    
    public CollisionManager(Environment e) {
        players = new ArrayList<>();
        environment = e;
    }

    public CollisionManager() {
        players = new ArrayList<>();
    }

    public void addPlayer(PlayerObject p) {
        players.add(p);
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
            playerLeftColumn = (int) Math.floor(((double) playerLeftWorldX - GameConfig.PLAYER_SPEED) / GameConfig.TILE_SIZE);
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

    /**
     * Checks if the projectile sent is intersecting with another player that isn't themselves.
     * 
     * @param projectileHitbox is the hitbox sent by the player
     * @param playerId the id of the player that owns the projectile
     * 
     * @return the player hit
     */
    public PlayerObject checkProjectileCollision(Rectangle projectileHitbox, int playerId) {
        for (PlayerObject player : players) {
            Rectangle playerHitbox = player.getRelativeHitbox();
                
            if (player.getId() != playerId) {
                // Manual intersection check using direct field access
                if (projectileHitbox.intersects(playerHitbox)) { 
                    System.out.println("PLAYER: " + player.getId());
                    System.out.println(player.getRelativeHitbox().x);
                    System.out.println("PROJECTILE: " + player.getId());
                    System.out.println(projectileHitbox.x);
                    return player;
                }
            }
        }
        
        return null;
    }
}
