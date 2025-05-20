/**
 * The GameConfig class is responsible for keeping the important values of the game.
 * These are made for global and quick adjustments.
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
    public static final int GAME_DURATION = 180; // In seconds.
}
