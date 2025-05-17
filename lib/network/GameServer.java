package lib.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import lib.*;
import lib.objects.*;
import lib.objects.spells.*;
import lib.render.CollisionManager;
import lib.render.Direction;

public class GameServer {
    private ServerSocket ss;
    private int players;
    private boolean isGameStarted;

    private ReadFromClient p1ReadRunnable, p2ReadRunnable;
    private WriteToClient p1WriteRunnable, p2WriteRunnable;

    private ArrayList<double[]> playerData;

    private CopyOnWriteArrayList<Spell> activeSpells;

    private CollisionManager collisionManager;

    private ArrayList<PlayerObject> playerObjects;

    public GameServer() {
        System.out.println("==== GAME SERVER ====");
        players = 0;
        isGameStarted = false;

        playerData = new ArrayList<>();
        // new double[] { x, y, animationIndex, lastHorizontalFacing, HP }
        // lastHorizontalFacing -> 0 = Direction.LEFT ; 1 = Direction.RIGHT
        playerData.add(new double[]{0, 0, 0, 0, 0});
        playerData.add(new double[]{0, 0, 0, 0, 0});

        // Initialize sprites.
        FireSpell.initializeSprites();
        WindSpell.initializeSprites();
        EarthSpell.initializeSprites();
        WaterSpell.initializeSprites(); 

        // Initialize collision manager.
        collisionManager = new CollisionManager();

        // Initialize the players.
        playerObjects = new ArrayList<>();

        activeSpells = new CopyOnWriteArrayList<>();

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

            while (players < GameConfig.MAX_PLAYERS) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                players++;
                PlayerObject player = new PlayerObject(GameConfig.TILE_SIZE * 64, GameConfig.TILE_SIZE * 43, GameConfig.TILE_SIZE, false, players);
                playerObjects.add(player);
                collisionManager.addPlayer(player);
                out.writeInt(players);
                System.out.println("Player #" + players + " has connected.");
                ReadFromClient rfc = new ReadFromClient(players, in);
                WriteToClient wtc = new WriteToClient(players, out);

                if (players == 1) {
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

        // Start the game.
        p1WriteRunnable.sendStartMsg();
        p2WriteRunnable.sendStartMsg();

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
        return players;
    }

    private class ReadFromClient implements Runnable {
        private int playerID;
        private DataInputStream dataIn;

        public ReadFromClient(int pid, DataInputStream in) {
            playerID = pid;
            dataIn = in;
            System.out.println("RFC" + playerID + " Runnable created");
        }

        public void run() {
            try {
                while (true) {
                    String dataRaw = dataIn.readUTF();

                    // Split data into their properties
                    String[] data = dataRaw.split(" ");

                    // Update Positions
                    String[] basicPlayerInfo = data[0].split("-");
                    int id = Integer.parseInt(basicPlayerInfo[0]);

                    // Update the server's records on player positions
                    playerData.get(id-1)[0] = Double.parseDouble(basicPlayerInfo[1]); // X
                    playerData.get(id-1)[1] = Double.parseDouble(basicPlayerInfo[2]); // Y
                        // basicPlayerInfo[3] is current direction Facing                  // Current facing direction
                    playerData.get(id-1)[2] = Double.parseDouble(basicPlayerInfo[4]); // Animation index
                    if (basicPlayerInfo[5].equals("LEFT")) {                               // Last faced horizontal direction
                        playerData.get(id-1)[3] = 0;
                    } else if (basicPlayerInfo[5].equals("RIGHT")) {
                        playerData.get(id-1)[3] = 1;
                    }

                    // Update all player objects to the updates positions
                    for (int i = 0; i < 2; i++){
                        playerObjects.get(i).setX(playerData.get(i)[0]);
                        playerObjects.get(i).setY(playerData.get(i)[1]);
                        playerObjects.get(i).setSprite(
                            (int) playerData.get(i)[2], 
                            (int) playerData.get(i)[3]
                        );
                    }
                    
                    // Implement spells
                    for (String entity : data){
                        
                        // FIRE_SPELL 
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
                            // FIRE_SPELL Initialization
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
                            
                        
                        // WATER_SPELL
                        } else if (entity.startsWith("WATER_SPELL")) {
                            String[] params = entity.split("-");
                            
                            activeSpells.add(new WaterSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Integer.parseInt(params[4])
                                ));
                        

                        // WIND_SPELL
                        } else if (entity.startsWith("WIND_SPELL")) {
                            String[] params = entity.split("-");
                            
                            // Update the player positions
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
                            } else {
                                activeSpells.add(new WindSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    0, 0, 0)
                                );
                            }
                            
                        
                        // EARTH SPELL
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
                            } else {
                                activeSpells.add(new EarthSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    0, true)
                                );
                            }
                        }
                    }
                }
            } catch(IOException ex) {
                System.out.println("IOException from RFC run()");
            }
        }
    }

    private class WriteToClient implements Runnable {
        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out) {
            playerID = pid;
            dataOut = out;
            System.out.println("WTC" + playerID + " Runnable created");
        }

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
                        1 [PLAYER_ID]-[X]-[Y]-[FRAME]-[HP]-[KILLS] [SPELL_CASTER'S_ID]-[SPELL_NAME]-[X]-[Y]-[Other Spell Parameters...] ...Other Spells...
                    */

                    // Sending Lobby Data
                    if (!isGameStarted) {
                        dataOut.writeUTF(String.format("0 %d", players));
                        dataOut.flush();

                        // Add a sleep to avoid overwhelming the connection
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            System.out.println("InterruptedException from WTC run()");
                        }

                    // Sending In Game Data
                    } else {
                        String gameStateData = "1 ";

                        // Add all player data in order
                        for (int i = 0; i < players; i++) {
                            gameStateData += String.format("%d-%f-%f-%d-%d ", 
                                i+1, // Player's ID
                                playerData.get(i)[0], // Player's X Coordinate stored in the server
                                playerData.get(i)[1], // Player's Y Coordinate stored in the server
                                (int) playerData.get(i)[2], // Player's animation index stored in the server
                                (int) playerData.get(i)[3] // Player's last horizontally faced direction (0:left ; 1:Right)
                            );
                        }
                        
                        // Add all spells 
                        for (Spell spell : activeSpells) {
                            gameStateData += spell.getDataString();
                            gameStateData += " ";
                        }

                        // Send data to client
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

        public void sendStartMsg() {
            try {
                dataOut.writeUTF("We now have 2 players. Go!");
                startGameLoop();
            } catch (IOException ex) {
                System.out.println("IOException from sendStartMsg()");
            }
        }
    }

    // ----- Game Loop Logic ----- //
    public void startGameLoop() {
        Thread gameLoop = new Thread(() -> {
            while (true) {
                // Update all spells
                for (Spell spell : activeSpells) {
                    spell.update();
                    spell.handleCollisions(collisionManager);
                }
                
                // Remove all expired spells
                activeSpells.removeIf((spell) -> spell.isExpired());

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        gameLoop.setDaemon(true);
        gameLoop.start();
    }
}