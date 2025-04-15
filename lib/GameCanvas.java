/**
 * The GameCanvas class is responsible for rendering the entities, environment and other objects within the game.
 * It is also responsible for setting up the game's timer and the player's keybindings.
 */
package lib;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class GameCanvas extends JComponent {
    ArrayList<GameObject> gameObjects;
    Timer animationTimer;
    KeyBindings keyBindings;

    // Server stuff
    Socket socket;
    int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;

    public GameCanvas() {
        // Initialize object to hold all gameObjects
        gameObjects = new ArrayList<>();

        // Initialize the environment.
        gameObjects.add(new Environment(0, 0, 16, 16)); 
        
        // Set the game timer and key bindings.
        keyBindings = new KeyBindings(this);
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                keyBindings.updatePlayerPosition((Player) gameObjects.get(1), GameConfig.PLAYER_SPEED);
                repaint();
            }
        };
        animationTimer = new Timer(10, al);
        animationTimer.start();
    }

    public void addPlayers() {
        if (playerID == 1) {
            gameObjects.add(new Player(50, 50, GameConfig.TILE_SIZE, Color.RED));
            gameObjects.add(new Player(50, 500, GameConfig.TILE_SIZE, Color.BLUE));
        } else {
            gameObjects.add(new Player(50, 500, GameConfig.TILE_SIZE, Color.BLUE));
            gameObjects.add(new Player(50, 50, GameConfig.TILE_SIZE, Color.RED));
        }
        
        repaint();
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", 10000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            System.out.println("You are player # " + playerID);
            if (playerID == 1) {
                System.out.println("Waiting for Player #2 to connect...");
            }
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStartMsg();
        } catch (IOException ex) {
            System.out.println("IOException from connectToServer()");
        }
    }

    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable created");
        }

        public void run() {
            try {
                while (true) { 
                    if (gameObjects.size() >= 3) { // TODO: get a better condition.
                        gameObjects.get(2).setX(dataIn.readDouble());
                        gameObjects.get(2).setY(dataIn.readDouble());
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from RFS run()");
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server: " + startMsg);

                // Start the threads.
                Thread readThread = new Thread(rfsRunnable);
                readThread.start();
                Thread writeThread = new Thread(wtsRunnable);
                writeThread.start();
            } catch (IOException ex) {
                System.out.println("IOException from waitForStartMsg()");
            }
        }
    }

    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            System.out.println("RFS Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    if (gameObjects.size() >= 2) {
                        dataOut.writeDouble(gameObjects.get(1).getX());
                        dataOut.writeDouble(gameObjects.get(1).getY());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterrptedException from WTS run()");
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from WTS run()");
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Cast Graphics to Graphics2D and apply anti-aliasing key.
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        // Draw every object.
        for (GameObject object : gameObjects) {
            object.drawSprite(g2d);
        }
    }
}
