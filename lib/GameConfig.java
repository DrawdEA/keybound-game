/**
 * The GameConfig class is responsible for keeping the important values of the game.
 */
package lib;

public class GameConfig {
    public static final int TILE_SIZE = 32;
    public static final int MAX_WORLD_COLUMNS = 128;
    public static final int MAX_WORLD_ROWS = 128;
    public static final int PLAYER_SPEED = 4;
    public static final int MAX_PLAYERS = 2;
    public static final int SCREEN_HEIGHT = 768;
    public static final int SCREEN_LENGTH = 1024;
    public static final int ANIMATION_COUNTER = 10; // Adjust for a slower/faster animation.
    public static final int CAST_ANIMATION_COUNTER = 5;
    public static final int SCREEN_X = (GameConfig.SCREEN_LENGTH / 2) - (GameConfig.TILE_SIZE * 2);
    public static final int SCREEN_Y = (GameConfig.SCREEN_HEIGHT / 2) - (GameConfig.TILE_SIZE);
}
