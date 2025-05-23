/**
 * The Player class is responsible for handling the client side of the game. 
 * It is also responsible for generating the player's canvas and appearance.
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
package lib.network;

import java.io.*;
import java.net.*;
import lib.Sound;
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
    PlayerObject enemy;
    
    private String wantsToCast;
    private boolean selfPlayerIsDead;
    private boolean respawnRequestSent;
    
    /**
     * Instantiates the client, a "player".
     */
    public Player() {
        isInGame = false;
        
        gameCanvas = new GameCanvas();
        gameCanvas.setPlayerClient(this);

        wantsToCast = "";
        selfPlayerIsDead = false;
        respawnRequestSent = false;
    }

    /**
     * Attempts to connect to the server given the ip.
     * 
     * @param ip the ip that the player will connect to
     */
    public void connectToServer(String ip) {
        try {
            socket = new Socket(ip, 10000);
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

    /**
     * Gets the GameCanvas of the client.
     * 
     * @return the GameCanvas of the client
     */
    public GameCanvas getCanvas() {
        return gameCanvas;
    }

    /**
     * The ReadFromServer class constantly receives data from the client.
     * This updates data accordingly.
     */
    private class ReadFromServer implements Runnable {
        private DataInputStream dataIn;

        /**
         * Instantiates the class given a DataInputStream.
         * 
         * @param in the DataInputStream
         */
        public ReadFromServer(DataInputStream in) {
            dataIn = in;
            System.out.println("RFS Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) { 

                    String serverDataRaw = dataIn.readUTF();
                    String[] serverData = serverDataRaw.split(" ");

                    // Process Lobby Data
                    if (serverData[0].equals("0")){
                        isInGame = false;
                        numOfConnectedPlayers = Integer.parseInt(serverData[1]);

                    // Process In Game Data.
                    } else if (serverData[0].startsWith("1-")) {
                        int gameTime = Integer.parseInt(serverData[0].split("-")[1]);
                        gameCanvas.updateGameTimer(gameTime);

                        // Game Proper Initializations.
                        if (!isInGame) {
                            isInGame = true;
                            System.out.println("Game has started. Starting WriteToServer thread for Player " + playerID);
                            writeThread.start();

                            // Set up the canvas.
                            gameCanvas.addPlayers(playerID, serverData);
                           
                            // Initialize all players in the gameCanvas
                            selfPlayer = gameCanvas.getOwnPlayer();
                            enemy = gameCanvas.getEnemy();
                        }

                        if (enemy != null && selfPlayer != null) {
                            // Iterate over the server data and process all player data
                            for (int i = 1; i < numOfConnectedPlayers + 1; i++){
                                
                                String[] playerDataParts = serverData[i].split("-");
                                int currentPlayerDataID = Integer.parseInt(playerDataParts[0]);

                                // If the Player ID is NOT your own player ID then process it for the enemy.
                                if (currentPlayerDataID != playerID) {
                                    enemy.setX(Double.parseDouble(playerDataParts[1]) - selfPlayer.getX() + selfPlayer.getScreenX()); // X
                                    enemy.setY(Double.parseDouble(playerDataParts[2]) - selfPlayer.getY() + selfPlayer.getScreenY()); // Y
                                    enemy.setSprite(
                                        Integer.parseInt(playerDataParts[3]), // The animationIndex.
                                        Integer.parseInt(playerDataParts[4]) // Last horizontally faced direction. (0:LEFT; 1:RIGHT)
                                    );
                                  
                                    enemy.setHP(Integer.parseInt(playerDataParts[5]));
                                    enemy.setKills(Integer.parseInt(playerDataParts[6]));
                                    enemy.setDeaths(Integer.parseInt(playerDataParts[7]));
                                
                                // if it is your Player ID then process its data
                                } else {
                                    double serverReportedX = Double.parseDouble(playerDataParts[1]);
                                    double serverReportedY = Double.parseDouble(playerDataParts[2]);
                                    int serverReportedHP = Integer.parseInt(playerDataParts[5]);

                                    int oldClientHP = selfPlayer.getPlayerHealth();

                                    // Only updates position from the server if player has died and server gave it a new position to respawn
                                    if (selfPlayerIsDead && serverReportedHP > 0) {
                                        selfPlayer.setX(serverReportedX);
                                        selfPlayer.setY(serverReportedY);
                                    }

                                    // Always trust server player stats
                                    selfPlayer.setHP(serverReportedHP); 
                                    selfPlayer.setHP(Integer.parseInt(playerDataParts[5]));
                                    selfPlayer.setKills(Integer.parseInt(playerDataParts[6]));
                                    selfPlayer.setDeaths(Integer.parseInt(playerDataParts[7]));

                                    if (serverReportedHP > 0) { // Player is alive according to server.
                                        if (selfPlayerIsDead) { // Client thought it was dead, but now it's alive.
                                            selfPlayerIsDead = false;
                                            respawnRequestSent = false; // Reset flag as respawn is complete.
                                        }
                                    } else { // Player is dead or still dead according to server.
                                        if (!selfPlayerIsDead) { // Client thought it was alive, but server says it's dead.
                                            selfPlayerIsDead = true;
                                            Sound killSound = new Sound(11);
                                            killSound.play();
                                            if (!selfPlayer.isOverridingAnimationOfType("Dying")) {
                                                selfPlayer.overrideAnimation("Dying");
                                            }
                                        }
                                    }

                                    // Trigger "Damaged" animation if HP decreased AND player is not dead.
                                    if (serverReportedHP < oldClientHP && serverReportedHP > 0) {
                                        if (!selfPlayer.isOverridingAnimation() || selfPlayer.isOverridingAnimationOfType("Damaged")) {
                                            selfPlayer.overrideAnimation("Damaged");
                                        }
                                    }
                                }
                            }

                            // Iterate over the spells after the player positions.
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
                                    // Transform the x and the y based on the POV of the player.
                                    x = Double.parseDouble(spellData[1]) - selfPlayer.getX() + selfPlayer.getScreenX();
                                    y = Double.parseDouble(spellData[2]) - selfPlayer.getY() + selfPlayer.getScreenY();
                                    dir = Direction.valueOf(spellData[3]);
                                    animationCounter = Integer.parseInt(spellData[5]);
                                    

                                    // Fire spell.
                                    if (spellData[0].equals("FIRE_SPELL")) {
                                        finished = Boolean.parseBoolean(spellData[6]);
                                        gameCanvas.addSpell(new FireSpell(playerID, x, y, dir, animationCounter, finished));
                                    
                                    // Water spell.
                                    } else if (spellData[0].equals("WATER_SPELL")) {
                                        gameCanvas.addSpell(new WaterSpell(playerID, x, y, dir, animationCounter));
                                    
                                    // Wind spell.
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
                                    
                                    // Earth spell.
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

    /**
     * The WriteToServer class constantly writes data from the client.
     * This sends data constantly.
     */
    private class WriteToServer implements Runnable {
        private DataOutputStream dataOut;

        /**
         * Instantiates the class given a DataOutputStream.
         * 
         * @param out the DataOutputStream
         */
        public WriteToServer(DataOutputStream out) {
            dataOut = out;
            System.out.println("RFS Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (selfPlayer != null) {
                        
                        // Basic Player info. Format: id-x-y-facing-animationIndex-lastHorizontalFacing-playerHealth
                        String dataString = String.format("%s ", selfPlayer.getPlayerDataString());
                    
                        // Add spell request by the Player with base parameters.
                        if (!wantsToCast.equals("")) {
                            dataString += String.format("%s-%f-%f-%s-0 ", 
                                wantsToCast,
                                selfPlayer.getX(), 
                                selfPlayer.getY(),
                                selfPlayer.getDirection().toString()
                            );
                            wantsToCast = "";
                        }

                        // Send a respawn request to the server.
                        if (selfPlayerIsDead && !respawnRequestSent) {
                            dataString += String.format("RESPAWN "); 
                            respawnRequestSent = true;
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

    /**
     * Asks for a spell to be cast, immediately being put in the queue.
     * 
     * @param spellName the name of the spell to be casted
     */
    public void requestToCast(String spellName) {
        selfPlayer.overrideAnimation("Attacking1");
        wantsToCast = spellName;

        Sound spellSound;
        if (spellName == "FIRE_SPELL") {
            spellSound = new Sound(6);
        } else if (spellName == "WATER_SPELL") {
            spellSound = new Sound(3);
        } else if (spellName == "EARTH_SPELL") {
            spellSound = new Sound(5);
        } else {
            spellSound = new Sound(7);
        }
        spellSound.play();
    }

    /**
     * Returns the number of connected players in the server.
     * 
     * @return the number of connected players in the server
     */
    public int getNumOfConnectedPlayers() {
        return numOfConnectedPlayers;
    }

    /**
     * Returns whether or not the player is in game.
     * @return boolean whether or not the player is in game.
     */
    public boolean getIsInGame() {
        return isInGame;
    }

    /**
     * Returns the needed stats of the self player for the scoreboard
     * @return int[3] with the id, num of kills, and num of death, stats 
     */
    public int[] getSelfStats() {
        return new int[]{
            selfPlayer.getId(),
            selfPlayer.getKills(),
            selfPlayer.getDeaths()
        };
    }

    /**
     * Returns the needed stats of the enemy player for the scoreboard
     * @return int[3] with the id, num of kills, and num of death, stats 
     */
    public int[] getEnemyStats() {
        return new int[]{
            enemy.getId(),
            enemy.getKills(),
            enemy.getDeaths()
        };
    }
}
