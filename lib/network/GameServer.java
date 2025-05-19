/**
 * The GameServer class is responsible for handling the server side of the game. 
 * It is also responsible for generating spells, and keeping things secure by handling hitboxing, health and timer in the server.
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
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import lib.*;
import lib.objects.*;
import lib.objects.spells.*;
import lib.render.CollisionManager;
import lib.render.Direction;

public class GameServer {
    private ServerSocket ss;
    private int numOfPlayers;
    private boolean isGameStarted;
    private Random random;

    private ReadFromClient p1ReadRunnable, p2ReadRunnable;
    private WriteToClient p1WriteRunnable, p2WriteRunnable;

    private CopyOnWriteArrayList<double[]> playerData;
    private CopyOnWriteArrayList<PlayerObject> playerObjects;
    private CopyOnWriteArrayList<Spell> activeSpells;
    private CollisionManager collisionManager;

    private int time;

    /**
     * Instantiates the server and initializes server resources.
     */
    public GameServer() {
        System.out.println("==== GAME SERVER ====");
        numOfPlayers = 0;
        isGameStarted = false;
        random = new Random();
 
        /**
         * Player Data is composed of new double[] { x, y, animationIndex, lastHorizontalFacing, HP, Kills }.
         * lastHorizontalFacing -> 0 = Direction.LEFT ; 1 = Direction.RIGHT.
         */
        playerData = new CopyOnWriteArrayList<>();

        // Initialize sprites.
        FireSpell.initializeSprites();
        WindSpell.initializeSprites();
        EarthSpell.initializeSprites();
        WaterSpell.initializeSprites(); 

        // Initialize collision manager.
        collisionManager = new CollisionManager();

        // Initialize the numOfPlayers.
        playerObjects = new CopyOnWriteArrayList<>();
        activeSpells = new CopyOnWriteArrayList<>();

        // Initialize the game timer.
        time = GameConfig.GAME_DURATION;

        try {
            ss = new ServerSocket(10000);

        } catch(IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
    }

    // ----- Network and Connection ----- // 

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            while (numOfPlayers < GameConfig.MAX_PLAYERS) {
                Socket s = ss.accept();

                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                
                numOfPlayers++;
                System.out.println("Player #" + numOfPlayers + " has connected.");
                
                double[] spawnLocation = getRandomSpawnLocation();
                playerData.add(new double[]{
                    spawnLocation[0], // x
                    spawnLocation[1], // y
                    0, // animation index
                    0, // lastHorizontalFacing -> 0 = Direction.LEFT ; 1 = Direction.RIGHT
                    5, // hp
                    0, // kills
                    0, // deaths
                });
                
                PlayerObject player = new PlayerObject(spawnLocation[0], spawnLocation[1], GameConfig.TILE_SIZE, false, numOfPlayers);
                playerObjects.add(player);
                collisionManager.addPlayer(player);
                out.writeInt(numOfPlayers);

                ReadFromClient rfc = new ReadFromClient(numOfPlayers, in);
                WriteToClient wtc = new WriteToClient(numOfPlayers, out);

                if (numOfPlayers == 1) {
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;

                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    readThread1.start();
                    writeThread1.start();

                } else {
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;

                    Thread readThread2 = new Thread(p2ReadRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    readThread2.start();
                    writeThread2.start();
                }
            }

            System.out.println("No longer accepting connections");
        } catch(IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }

    public void startGame() {
        isGameStarted = true;
        startGameLoop();

        System.out.println("Started the Game Successfully");
    }

    public void closeConnections() {
        System.out.println("Initiating server shutdown...");
        try {
            if (ss != null && !ss.isClosed()) {
                ss.close();
                System.out.println("ServerSocket closed.");
            }
        } catch (IOException ex) {
            System.err.println("IOException while closing server socket: " + ex.getMessage());
        }
    }

    public int getNumPlayersInLobby() {
        return numOfPlayers;
    }

    /**
     * The ReadFromClient is responsible for reading data from the client.
     * This is done constantly.
     */
    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;

        /**
         * Instantiates the class with a DataInputStream
         * 
         * @param pid the ID of the player
         * @param out the DataInputStream
         */
        public ReadFromClient(int pid, DataInputStream in) {
            playerID = pid;
            dataIn = in;
            System.out.println("RFC" + playerID + " Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String dataRaw = dataIn.readUTF();

                    // Split data into their properties.
                    String[] data = dataRaw.split(" ");

                    // Update Positions.
                    String[] basicPlayerInfo = data[0].split("-");
                    int id = Integer.parseInt(basicPlayerInfo[0]);

                    // Update the server's records on player positions.
                    playerData.get(id-1)[0] = Double.parseDouble(basicPlayerInfo[1]); // X.
                    playerData.get(id-1)[1] = Double.parseDouble(basicPlayerInfo[2]); // Y.
                    // basicPlayerInfo[3] is current direction Facing                 // Current facing direction.
                    playerData.get(id-1)[2] = Double.parseDouble(basicPlayerInfo[4]); // Animation index.
                    if (basicPlayerInfo[5].equals("LEFT")) { // Last faced horizontal direction.
                        playerData.get(id-1)[3] = 0;
                    } else if (basicPlayerInfo[5].equals("RIGHT")) {
                        playerData.get(id-1)[3] = 1;
                    }
                    
                    // Implement spells.
                    for (String entity : data){
                        
                        // The fire spell. 
                        if (entity.startsWith("FIRE_SPELL")) {
                            String[] params = entity.split("-");
                            if (params.length == 7) {
                                activeSpells.add(new FireSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[5]),
                                    Boolean.parseBoolean(params[6]))
                                );
                            // The fire spell for initialization.
                            } else {
                                activeSpells.add(new FireSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[4]),
                                    false)
                                );
                            }
                            
                        
                        // The water spell.
                        } else if (entity.startsWith("WATER_SPELL")) {
                            String[] params = entity.split("-");
                            
                            activeSpells.add(new WaterSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[4])
                                ));
                        

                        // The wind spell.
                        } else if (entity.startsWith("WIND_SPELL")) {
                            String[] params = entity.split("-");
                            
                            // Update the player positions.
                            playerData.get(playerID-1)[0] = Double.parseDouble(params[1]);
                            playerData.get(playerID-1)[1] = Double.parseDouble(params[2]);

                            if (params.length == 8) {
                                activeSpells.add(new WindSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[4]),
                                    Double.parseDouble(params[5]),
                                    Double.parseDouble(params[6])
                                    )
                                );
                            } else { // The wind spell for initializations.
                                activeSpells.add(new WindSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    0, 0, 0)
                                );
                            }
                            
                        
                        // The earth spell.
                        } else if (entity.startsWith("EARTH_SPELL")) {
                            String[] params = entity.split("-");

                            if (params.length == 7) {
                                activeSpells.add(new EarthSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[4]),
                                    Boolean.parseBoolean(params[5]))
                                );
                            } else { // The earth spell for initializations.
                                activeSpells.add(new EarthSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    0, true)
                                );
                            }
                        
                        // Respawns if dead.
                        } else if (entity.startsWith("RESPAWN")){
                            // Respawn in a new location.
                            double[] newSpawnLocation = getRandomSpawnLocation();
                            playerData.get(playerID-1)[0] = newSpawnLocation[0]; // Set X coordinate.
                            playerData.get(playerID-1)[1] = newSpawnLocation[1]; // Set Y coordinate.
                            playerData.get(playerID-1)[4] = 5; // Reset HP.
                        }
                    }

                    // Update all player objects to the updates positions.
                    for (int i = 0; i < 2; i++){
                        playerObjects.get(i).setX(playerData.get(i)[0]);
                        playerObjects.get(i).setY(playerData.get(i)[1]);
                        playerObjects.get(i).setSprite(
                            (int) playerData.get(i)[2], 
                            (int) playerData.get(i)[3]
                        );
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from RFC run()");
            }
        }
    }

    /**
     * The WriteToClient is responsible for passing data to the client.
     * This is done constantly.
     */
    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;

        /**
         * Instantiates the class with a WriteToClient
         * 
         * @param pid the ID of the player
         * @param out the DataOutputStream
         */
        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID + " Runnable created");
        }

        /**
         * Starts the game loop once there is enough players.
         */
        public void sendStartMsg() {
            try {
                dataOut.writeUTF("We now have 2 numOfPlayers. Go!");
                startGameLoop();
            } catch (IOException ex) {
                System.out.println("IOException from sendStartMsg()");
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    /*
                        Data formatting:
                        All first level properties of data is separated by a space " "
                        All second level (nested) properties of the data is separated by a dash "-"
                        This allows us to treat the data like 2D Array by splitting it into levels  
                        It follows the following format:
                            [MESSAGE_TYPE] [DATA_NAME]-[VALUE_1]-[VALUE_2]

                        [MESSAGE_TYPE]
                        0 -> Lobby Data 
                        1 -> In Game Data

                        LOBBY DATA
                        0 [NUMBER_OF_PLAYERS_CONNECTED] [PLAYER_NAMES...]

                        IN GAME DATA
                        1 [PLAYER_ID]-[X]-[Y]-[FRAME]-[HP]-[KILLS]-[DEATHS] [SPELL_CASTER'S_ID]-[SPELL_NAME]-[X]-[Y]-[Other Spell Parameters...] ...Other Spells...
                    */

                    // Sending Lobby Data.
                    if (!isGameStarted) {
                        dataOut.writeUTF(String.format("0 %d", numOfPlayers));
                        dataOut.flush();

                        // Add a sleep to avoid overwhelming the connection.
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println("InterruptedException from WTC run()");
                        }

                    // Sending In Game Data.
                    } else {
                        String gameStateData = String.format("1-%d ", time);

                        // Add all player data in order.
                        for (int i = 0; i < numOfPlayers; i++) {
                            gameStateData += String.format("%d-%f-%f-%d-%d-%d-%d-%d ", 
                                i+1, // Player's ID.
                                playerData.get(i)[0], // Player's X Coordinate stored in the server.
                                playerData.get(i)[1], // Player's Y Coordinate stored in the server.
                                (int) playerData.get(i)[2], // Player's animation index stored in the server.
                                (int) playerData.get(i)[3], // Player's last horizontally faced direction. (0:left ; 1:Right)
                                (int) playerData.get(i)[4], // Player's HP.
                                (int) playerData.get(i)[5], // Player's Kills.
                                (int) playerData.get(i)[6] // Player's Deaths.
                            );
                        }
                        
                        // Add all spells.
                        for (Spell spell : activeSpells) {
                            gameStateData += spell.getDataString();
                            gameStateData += " ";
                        }

                        // Send data to client.
                        dataOut.writeUTF(gameStateData);
                        dataOut.flush();
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException ex) {
                            System.out.println("InterruptedException from WTC run()");
                        } 
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from WTC run()");
            }
        }
    }

    /**
     * Returns a random double from a minimum and a maximum.
     * 
     * @param min the minimum value
     * @param max the maximum value
     * @return the random double
     */
    private double randomDoubleFromRange(double min, double max) {
        return GameConfig.TILE_SIZE * min + (GameConfig.TILE_SIZE * (max-min)) * random.nextDouble();
    }

    /**
     * Returns a random spawning location.
     * 
     * @return a random spawning location
     */
    public double[] getRandomSpawnLocation() {
        // Choose a random spawning zone.
        int spawningZone = random.nextInt(1, 4 + 1); // 4 total spawning zones.

        if (spawningZone == 1){
            return new double[]{
                randomDoubleFromRange(24, 57),
                randomDoubleFromRange(44,46),
            };
        } else if (spawningZone == 2){
            return new double[]{
                randomDoubleFromRange(26, 38),
                randomDoubleFromRange(45, 67),
            };
        } else if (spawningZone == 3){
            return new double[]{
                randomDoubleFromRange(38, 65),
                randomDoubleFromRange(60, 72),
            };
        } else if (spawningZone == 4){
            return new double[]{
                randomDoubleFromRange(81, 103),
                randomDoubleFromRange(68, 74),
            };
        } else {
            return new double[]{0,0};
        }
    }

    /**
     * Handles ending the game and showing the final stats to the player.
     */
    private void endGame() {
        System.out.println("END GAME");
    }

    /**
     * Creates the game loop.
     */
    public void startGameLoop() {
        Thread gameLoop = new Thread(() -> {
            while (true) {
                // Update all spells.
                for (Spell spell : activeSpells) {
                    spell.update();
                    
                    int resultOfCollisionCheck = spell.handleCollisions(collisionManager);
                    if (resultOfCollisionCheck != 0){
                        playerData.get(resultOfCollisionCheck-1)[4] -= 1; // Decrease player hit HP.
                        
                        // If Player died. (hp = 0)
                        if (playerData.get(resultOfCollisionCheck-1)[4] <= 0) { 
                            playerData.get(spell.getCasterId()-1)[5] += 1; // Increase kill count of caster.
                            playerData.get(resultOfCollisionCheck-1)[6] += 1; // Increment death count of dying player.
                        }
                    }
                }
                
                // Remove all expired spells.
                activeSpells.removeIf((spell) -> spell.isExpired());

                try {
                    Thread.sleep(12);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameLoop.start();

        Thread gameTimer = new Thread(() -> {
            while (time >= 0) {
                try {
                    Thread.sleep(1000);
                    time = time - 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // End the game after the timer is done.
            endGame();
        });
        gameTimer.start();
    }
}