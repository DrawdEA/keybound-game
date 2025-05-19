/**
 * The Player class is responsible for generating the player's canvas and appearance.
 * It also handles the client-side of the game.
 */
package lib.network;

import java.io.*;
import java.net.*;
import lib.objects.PlayerObject;
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
    private PlayerObject selfPlayer;
    
    private String wantsToCast;
    private boolean selfPlayerIsDead;
    private boolean respawnRequestSent;
    
    public Player() {
        isInGame = false;
        
        gameCanvas = new GameCanvas();
        gameCanvas.setPlayerClient(this);

        wantsToCast = "";
        selfPlayerIsDead = false;
        respawnRequestSent = false;
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

                        // ----- Game Proper Initializations -----
                        if (!isInGame) {
                            isInGame = true;
                            System.out.println("Game has started. Starting WriteToServer thread for Player " + playerID);
                            writeThread.start();

                            // Set up the canvas.
                            gameCanvas.addPlayers(playerID, serverData);
                           
                            // Initialize selfPlayer to the main player in the gameCanvas
                            selfPlayer = gameCanvas.getOwnPlayer();
                        }

                        PlayerObject enemy = gameCanvas.getEnemy();

                        if (enemy != null && selfPlayer != null) { // Ensure selfPlayer is also not null
                            // Iterate over the server data and process all player data
                            for (int i = 1; i < numOfConnectedPlayers + 1; i++){
                                
                                String[] playerDataParts = serverData[i].split("-");
                                int currentPlayerDataID = Integer.parseInt(playerDataParts[0]);

                                // If the Player ID is NOT your own player ID then process it for the enemy
                                if (currentPlayerDataID != playerID) {
                                    enemy.setX(Double.parseDouble(playerDataParts[1]) - selfPlayer.getX() + selfPlayer.getScreenX()); // X
                                    enemy.setY(Double.parseDouble(playerDataParts[2]) - selfPlayer.getY() + selfPlayer.getScreenY()); // Y
                                    enemy.setSprite(
                                        Integer.parseInt(playerDataParts[3]), // Animation Index
                                        Integer.parseInt(playerDataParts[4]) // Last horizontally faced direction (0:LEFT; 1:RIGHT)
                                    );
                                
                                // if it is your Player ID then process its data
                                } else {
                                    double serverReportedX = Double.parseDouble(playerDataParts[1]);
                                    double serverReportedY = Double.parseDouble(playerDataParts[2]);
                                    int serverReportedHP = Integer.parseInt(playerDataParts[5]);

                                    int oldClientHP = selfPlayer.getPlayerHealth();

                                    // --- Position Update Logic ---
                                    // Only update client's position from server if:
                                    // 1. Client thought it was dead, but server says it's alive (respawn confirmed).
                                    // This ensures the teleport happens.
                                    if (selfPlayerIsDead && serverReportedHP > 0) {
                                        selfPlayer.setX(serverReportedX);
                                        selfPlayer.setY(serverReportedY);
                                    }

                                    // HP and Death Update Logic
                                    selfPlayer.setHP(serverReportedHP); // Always trust server HP

                                    if (serverReportedHP > 0) { // Player is alive according to server
                                        if (selfPlayerIsDead) { // Client thought it was dead, but now it's alive
                                            selfPlayerIsDead = false;
                                            respawnRequestSent = false; // Reset flag as respawn is complete
                                        }
                                    } else { // Player is dead or still dead according to server
                                        if (!selfPlayerIsDead) { // Client thought it was alive, but server says it's dead
                                            selfPlayerIsDead = true;
                                            if (!selfPlayer.isOverridingAnimationOfType("Dying")) {
                                                selfPlayer.overrideAnimation("Dying");
                                            }
                                        }
                                    }

                                    // Trigger "Damaged" animation if HP decreased AND player is not dead 
                                    if (serverReportedHP < oldClientHP && serverReportedHP > 0) {
                                        if (!selfPlayer.isOverridingAnimation() || selfPlayer.isOverridingAnimationOfType("Damaged")) {
                                             selfPlayer.overrideAnimation("Damaged");
                                        }
                                    }
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
                                    x = Double.parseDouble(spellData[1]) - selfPlayer.getX() + selfPlayer.getScreenX();
                                    y = Double.parseDouble(spellData[2]) - selfPlayer.getY() + selfPlayer.getScreenY();
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
                    if (selfPlayer != null) {
                        
                        // Basic Player info id-x-y-facing-animationIndex-lastHorizontalFacing-playerHealth
                        String dataString = String.format("%s ", selfPlayer.getPlayerDataString());
                    
                        // Add spell request by the Player with base parameters
                        if (!wantsToCast.equals("")) {
                            dataString += String.format("%s-%f-%f-%s-0 ", 
                                wantsToCast,
                                selfPlayer.getX(), 
                                selfPlayer.getY(),
                                selfPlayer.getDirection().toString()
                            );
                            wantsToCast = "";
                        }

                        // Send a respawn request to the server
                        if (selfPlayerIsDead && !respawnRequestSent) {
                            dataString += String.format("RESPAWN "); 
                            respawnRequestSent = true;
                        }
                        
                        // Position relative to game tiles as coordinates
                        // System.out.printf("x: %f ; y: %f\n", 
                        //     selfPlayer.getX()/GameConfig.TILE_SIZE,
                        //     selfPlayer.getY()/GameConfig.TILE_SIZE
                        // );

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
        selfPlayer.overrideAnimation("Attacking1");
        wantsToCast = spellName;
    }

    public int getNumOfConnectedPlayers() {
        return numOfConnectedPlayers;
    }

    public boolean getIsInGame() {
        return isInGame;
    }
}
