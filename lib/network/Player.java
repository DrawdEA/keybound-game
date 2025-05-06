/**
 * The Player class is responsible for generating the player's canvas and appearance.
 * It also handles the client-side of the game.
 */
package lib.network;

import java.io.*;
import java.net.*;
import lib.objects.spells.*;
import lib.render.*;

public class Player {
    private Socket socket;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;

    private GameCanvas gameCanvas;
    
    private String wantsToCast = "";
    
    public Player() {
        gameCanvas = new GameCanvas();
        gameCanvas.setPlayerClient(this);
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
                    PlayerObject enemy = gameCanvas.getEnemy();
                    if (enemy != null) {
                        PlayerObject player = gameCanvas.getOwnPlayer();
                        
                        // Enemy position
                        String enemyDataRaw = dataIn.readUTF();
                        String[] enemyData = enemyDataRaw.split(" ");
                        
                        // enemyData[0] = enemy's ID
                        // enemyData[1] = enemy's position token
                        // enemyData[2::] = enemy's objects
                        
                        // Set position
                        String[] enemyPosition = enemyData[1].split("-");
                        enemy.setX(Double.parseDouble(enemyPosition[1]) - player.getX() + player.getScreenX());
                        enemy.setY(Double.parseDouble(enemyPosition[2]) - player.getY() + player.getScreenY());
                        
                        gameCanvas.clearSpells();
                        for (int i = 2; i < enemyData.length; i++) {
                            String[] spellData = enemyData[i].split("-");
                            
                            double x;
                            double y;
                            Direction dir;

                            if (spellData[0].contains("_SPELL")){
                                // Transform the x and the y based on the POV of the player 
                                x = Double.parseDouble(spellData[1]) - player.getX() + player.getScreenX();
                                y = Double.parseDouble(spellData[2]) - player.getY() + player.getScreenY();
                                dir = Direction.valueOf(spellData[3]);

                                // FIRE SPELL
                                if (spellData[0].equals("FIRE_SPELL")) {
                                    gameCanvas.addSpell(new FireSpell(playerID, x, y, dir));
                                
                                // WATER SPELL
                                } else if (spellData[0].equals("WATER_SPELL")) {
                                    if (spellData.length > 4) {
                                        double serverEndingBar = Double.parseDouble(spellData[4]);
                                        
                                        // Transform the endingBar to screen coordinates
                                        double transformedEndingBar = 0;
                                        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                                            transformedEndingBar = serverEndingBar - player.getX() + player.getScreenX();
                                        } else if (dir == Direction.UP || dir == Direction.DOWN) {
                                            transformedEndingBar = serverEndingBar - player.getY() + player.getScreenY();
                                        }
                                        
                                        gameCanvas.addSpell(new WaterSpell(playerID, x, y, dir, transformedEndingBar));
                                    }
                                
                                // WIND SPELL
                                } else if (spellData[0].equals("WIND_SPELL")){
                                    int spellCasterId = Integer.parseInt(spellData[4]);

                                    if (spellCasterId == playerID) {
                                        gameCanvas.getOwnPlayer().setNewPosition(
                                            Double.parseDouble(spellData[1]), 
                                            Double.parseDouble(spellData[2])
                                        );
                                    }
                                    
                                    gameCanvas.addSpell(new WindSpell(playerID, x, y, dir));
                                // EARTH SPELL
                                } else if (spellData[0].equals("EARTH_SPELL")){
                                    
                                
                                }
                            }
                        }
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
                        // Written data will be in the form of "Player_ID OBJECT/PROPERTY_OF_PLAYER-X-Y OBJECT/PROPERTY_OF_PLAYER-X-Y ..."
                        
                        // Add Player ID
                        String dataString = String.format("%d ", playerID);
                        
                        // Add Player Position
                        dataString += String.format("POSITION-%f-%f ", gameCanvas.getOwnPlayer().getX(), gameCanvas.getOwnPlayer().getY());

                        // Add spell request by the Player
                        if (!wantsToCast.equals("")) {
                            dataString += String.format(wantsToCast + "-" + gameCanvas.getOwnPlayer().getPositionDataString() + " ");
                            wantsToCast = "";
                        }

                        dataOut.writeUTF(dataString);
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted Exception from WTS run()");
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from WTS run()");
            }
        }
    }

    public void requestToCast(String spellName) {
        gameCanvas.getOwnPlayer().overrideAnimation("Attacking1");
        wantsToCast = spellName;
    }
}
