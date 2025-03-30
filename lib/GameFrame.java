package lib;

import java.awt.*;
import javax.swing.*;

public class GameFrame {
    JFrame mainFrame;
    MainMenu gameMainMenu;

    /**
     * Instantiates a new frame.
    */
    public GameFrame(){
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
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        // Pack the frame so that the frame will size itself based on its contents.
        mainFrame.pack();
    }
}
