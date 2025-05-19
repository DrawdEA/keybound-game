/**
 * The CollisionManager is responsible for checking all collision mechanics in the game.
 * These include checking for environment collision and projectile collision.
 * 
 * @author Edward Joshua M. Diesta (241571), Charles Joshua T. Uy (244644)
 * @version May 20, 2025
 * 
 * We have not discussed the Java language code in our program 
 * with anyone other than our instructor or the teaching assistants 
 * assigned to this course.
 * 
 * We have not used Java language code obtained from another student, 
 * or any other unauthorized source, either modified or unmodified.
 * 
 * If any Java language code or documentation used in our program 
 * was obtained from another source, such as a textbook or website, 
 * that has been clearly noted with a proper citation in the comments 
 * of our program.
 */
package lib.render;

import java.awt.Rectangle;
import java.util.ArrayList;
import lib.GameConfig;
import lib.objects.*;

public class CollisionManager {
    private Environment environment;
    private ArrayList<PlayerObject> players;
    
    /**
     * Instantiates a new collision manager.
     * 
     * @param e the map
     */
    public CollisionManager(Environment e) {
        players = new ArrayList<>();
        environment = e;
    }

    /**
     * Instantiates a new collision manager but without the environment.
     * This is used for the server.
     */
    public CollisionManager() {
        players = new ArrayList<>();
    }

    /**
     * Adds in a player to be tracked for collisions.
     * 
     * @param p the player to be added
     */
    public void addPlayer(PlayerObject p) {
        players.add(p);
    }

    /**
     * Checks if the player is currently moving towards a solid tile.
     * 
     * @param playerObject the player to be checked
     * @param direction the direction he is moving in
     * @return if the player is currently moving towards a solid tile
     */
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
                if (projectileHitbox.x < playerHitbox.x + playerHitbox.width &&
                    projectileHitbox.x + projectileHitbox.width > playerHitbox.x &&
                    projectileHitbox.y < playerHitbox.y + playerHitbox.height &&
                    projectileHitbox.y + projectileHitbox.height > playerHitbox.y) { 
                    /* System.out.println("PLAYER: " + player.getId());
                    System.out.println(player.getRelativeHitbox().x + player.getRelativeHitbox().width);
                    System.out.println("PROJECTILE: " + player.getId());
                    System.out.println(projectileHitbox.x); */
                    return player;
                }
            }
        }
        
        return null;
    }
}
