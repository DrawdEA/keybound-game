/**
 * The Player class is responsible for generating the player's canvas and appearance.
 * It also handles the client-side of the game.
 */
package lib;

import java.io.*;
import java.net.*;

public class Player {
    private Socket socket;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;

    private GameCanvas gameCanvas;
    
    public Player() {
        gameCanvas = new GameCanvas();
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

            // Set up the canvas.
            gameCanvas.addPlayers(playerID);

            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStartMsg();
        } catch (IOException ex) {
            System.out.println("IOException from connectToServer()");
        }
    }

    public GameCanvas getCanvas() {
        return gameCanvas;
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
                    PlayerVisuals enemy = gameCanvas.getEnemy();
                    if (enemy != null) {
                        PlayerVisuals player = gameCanvas.getOwnPlayer();
                        double screenX = dataIn.readDouble() - player.getX() + player.getScreenX();
                        double screenY = dataIn.readDouble() - player.getY() + player.getScreenY();
                        enemy.setX(screenX);
                        enemy.setY(screenY);
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
                    if (gameCanvas.getOwnPlayer() != null) {
                        dataOut.writeDouble(gameCanvas.getOwnPlayer().getX());
                        dataOut.writeDouble(gameCanvas.getOwnPlayer().getY());
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
}
