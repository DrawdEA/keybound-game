/**
 * The GameFrame class is responsible for loading the entire game through a GUI.
 * It creates a JFrame and loads the main menu from there.
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

import java.awt.*;
import javax.swing.*;
import lib.menus.*;

public class GameFrame {
    JFrame mainFrame;
    MainMenu gameMainMenu;

    /**
     * Instantiates a new frame.
    */
    public GameFrame() {
        mainFrame = new JFrame();
        gameMainMenu = new MainMenu();
    }


    /**
     * Sets up the GUI.
     */
    public void setUpGUI() {
        // Set up the GUI hierarchy.
        mainFrame.add(gameMainMenu, BorderLayout.CENTER);

        // Set up the miscellaneous details.
        mainFrame.setTitle("Final Project - Diesta - Uy, C.");
        mainFrame.setFocusTraversalKeysEnabled(false); // This allows the Tab key to be used in keybindings
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        // Pack the frame so that the frame will size itself based on its contents.
        mainFrame.pack();
    }
}
