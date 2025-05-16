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
    Thread writeThread;
    Thread readThread;

    private int numOfConnectedPlayers; 
    private boolean isInGame;

    private GameCanvas gameCanvas;
    
    private String wantsToCast = "";
    
    public Player() {
        isInGame = false;
        gameCanvas = new GameCanvas();
        gameCanvas.setPlayerClient(this);
    }

    public void connectToServer(String ip) {
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

            // Start the read thread immediately to receive lobby updates
            System.out.println("Starting ReadFromServer thread...");
            readThread = new Thread(rfsRunnable);
            readThread.start();

            // Initialize the write thread
            writeThread = new Thread(wtsRunnable);
            
            System.out.println("ReadFromServer thread started");
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

                    String serverDataRaw = dataIn.readUTF();
                    String[] serverData = serverDataRaw.split(" ");

                    // Process Lobby Data
                    if (serverData[0].equals("0")){
                        isInGame = false;
                        numOfConnectedPlayers = Integer.parseInt(serverData[1]);

                    // Process In Game Data
                    } else if (serverData[0].equals("1")) {                        
                        // Start the write thread if it already isn't alive
                        if (!isInGame) { // Only set isInGame and start writeThread once
                            isInGame = true;
                            System.out.println("Game has started. Starting WriteToServer thread for Player " + playerID);
                            writeThread.start();
                        }

                        PlayerObject enemy = gameCanvas.getEnemy();

                        if (enemy != null) {
                            PlayerObject player = gameCanvas.getOwnPlayer();

                            // Iterate over the server data and process all player data that isn't your own
                            for (int i = 1; i < numOfConnectedPlayers + 1; i++){
                                // If the Player ID (the first character of each player data string) is NOT your own player ID then process it to your Game Canvas
                                if (Integer.parseInt(serverData[i].substring(0, 1)) != playerID) {
                                    String[] enemyPlayerData = serverData[i].split("-");
                                    
                                    enemy.setX(Double.parseDouble(enemyPlayerData[1]) - player.getX() + player.getScreenX());
                                    enemy.setY(Double.parseDouble(enemyPlayerData[2]) - player.getY() + player.getScreenY());
                                    //enemy.updatePlayerAnimation(something, something); // TODO: handle this
                                }
                            }

                            // Iterate over the spells after the player positions
                            gameCanvas.clearSpells();
                            
                            for (int i = numOfConnectedPlayers + 1; i < serverData.length; i++) {
                                String[] spellData = serverData[i].split("-");
                                double x;
                                double y;
                                Direction dir;
                                int animationCounter;
                                boolean finished;
                                boolean alive;

                                if (spellData[0].contains("_SPELL")) {
                                    // Transform the x and the y based on the POV of the player 
                                    x = Double.parseDouble(spellData[1]) - player.getX() + player.getScreenX();
                                    y = Double.parseDouble(spellData[2]) - player.getY() + player.getScreenY();
                                    dir = Direction.valueOf(spellData[3]);
                                    animationCounter = Integer.parseInt(spellData[5]);
                                    

                                    // FIRE SPELL
                                    if (spellData[0].equals("FIRE_SPELL")) {
                                        finished = Boolean.parseBoolean(spellData[6]);
                                        gameCanvas.addSpell(new FireSpell(playerID, x, y, dir, animationCounter, finished));
                                    
                                    // WATER SPELL
                                    } else if (spellData[0].equals("WATER_SPELL")) {
                                        gameCanvas.addSpell(new WaterSpell(playerID, x, y, dir, animationCounter));
                                    
                                    // WIND SPELL
                                    } else if (spellData[0].equals("WIND_SPELL")){
                                        int spellCasterId = Integer.parseInt(spellData[4]);
                                        double originalX = Double.parseDouble(spellData[6]);
                                        double originalY = Double.parseDouble(spellData[7]);

                                        if (spellCasterId == playerID) {
                                            gameCanvas.getOwnPlayer().setNewPosition(
                                                Double.parseDouble(spellData[1]), 
                                                Double.parseDouble(spellData[2])
                                            );
                                        }
                                        
                                        gameCanvas.addSpell(new WindSpell(playerID, x, y, dir, animationCounter, originalX, originalY));
                                    // EARTH SPELL
                                    } else if (spellData[0].equals("EARTH_SPELL")){
                                        alive = Boolean.parseBoolean(spellData[6]);
                                        gameCanvas.addSpell(new EarthSpell(playerID, x, y, dir, animationCounter, alive));
                                    }
                                }
                            }   
                        }
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from RFS run()");
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
                        
                        // Basic Player info id-x-y-facing-animationIndex
                        String dataString = String.format("%s ", gameCanvas.getOwnPlayer().getPlayerDataString());
                        
                        // TODO: Remove the redundant code
                        // Add spell request by the Player
                        if (!wantsToCast.equals("")) {
                            dataString += String.format("%s-%f-%f-%s-0", 
                                wantsToCast,
                                gameCanvas.getOwnPlayer().getX(), 
                                gameCanvas.getOwnPlayer().getY(),
                                gameCanvas.getOwnPlayer().getDirection().toString()
                            );
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

    public int getNumOfConnectedPlayers() {
        return numOfConnectedPlayers;
    }

    public boolean getIsInGame() {
        return isInGame;
    }
}
