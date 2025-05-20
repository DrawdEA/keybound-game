/**
 * The GameStarter class is responsible for setting up the main frame of the game.
 * It handles and starts the game proper and contains the main method.
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
import lib.*;

public class GameStarter {
    public static void main(String[] args) {
        GameFrame frame = new GameFrame();
        frame.setUpGUI();
    }
}
