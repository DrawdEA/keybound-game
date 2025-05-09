package lib.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import lib.*;
import lib.objects.spells.*;
import lib.render.Direction;

public class GameServer {
    private ServerSocket ss;
    private int players;

    private ReadFromClient p1ReadRunnable, p2ReadRunnable;
    private WriteToClient p1WriteRunnable, p2WriteRunnable;

    private ArrayList<double[]> playerPositions;

    private CopyOnWriteArrayList<Spell> activeSpells;

    public GameServer() {
        System.out.println("==== GAME SERVER ====");
        players = 0;

        playerPositions = new ArrayList<>();
        playerPositions.add(new double[]{50, 50});
        playerPositions.add(new double[]{50, 500});

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
                out.writeInt(players);
                System.out.println("Player #" + players + " has connected.");
                
                ReadFromClient rfc = new ReadFromClient(players, in);
                WriteToClient wtc = new WriteToClient(players, out);

                if (players == 1) {
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                } else {
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;

                    // Start the game.
                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();

                    // Start the read threads first.
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();

                    // Start the write threads first.
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }
            }

            System.out.println("No longer accepting connections");
        } catch(IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
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

                    // Catch all spells
                    String[] data = dataRaw.split(" ");

                    // Update Positions
                    String[] positionData = data[1].split("-");

                    playerPositions.get(playerID-1)[0] = Double.parseDouble(positionData[1]);
                    playerPositions.get(playerID-1)[1] = Double.parseDouble(positionData[2]);

                    // Implement spells
                    for (String entity : data){
                        
                        // FIRE_SPELL 
                        if (entity.startsWith("FIRE_SPELL")) {
                            String[] params = entity.split("-");
                            activeSpells.add(new FireSpell(
                                playerID,
                                Double.parseDouble(params[1]), 
                                Double.parseDouble(params[2]), 
                                Direction.valueOf(params[3]))
                            );
                        
                        // WATER_SPELL
                        } else if (entity.startsWith("WATER_SPELL")) {
                            String[] params = entity.split("-");
                            // If we only have the basic parameters (without endingBar) meaning first initialization of the spell
                            if (params.length == 4) {
                                // Use the first constructor which calculates endingBar internally
                                activeSpells.add(new WaterSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]))
                                );
                            } 
                            // If we have all parameters including endingBar
                            else if (params.length >= 5) {
                                activeSpells.add(new WaterSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]),
                                    Double.parseDouble(params[4]),
                                    Integer.parseInt(params[5]),
                                    Integer.parseInt(params[6])
                                ));
                            }
                        
                        // WIND_SPELL
                        } else if (entity.startsWith("WIND_SPELL")) {
                            String[] params = entity.split("-");
                            
                            // Update the player positions
                            playerPositions.get(playerID-1)[0] = Double.parseDouble(params[1]);
                            playerPositions.get(playerID-1)[1] = Double.parseDouble(params[2]);

                            activeSpells.add(new WindSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]))
                                );
                        
                        // EARTH SPELL
                        } else if (entity.startsWith("EARTH_SPELL")) {
                            String[] params = entity.split("-");
                            
                            // Update the player positions
                            playerPositions.get(playerID-1)[0] = Double.parseDouble(params[1]);
                            playerPositions.get(playerID-1)[1] = Double.parseDouble(params[2]);

                            activeSpells.add(new EarthSpell(
                                    playerID,
                                    Double.parseDouble(params[1]), 
                                    Double.parseDouble(params[2]), 
                                    Direction.valueOf(params[3]))
                                );
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

                    String spellString = "";
                    for (Spell spell : activeSpells) {
                        spellString += spell.getDataString();
                        spellString += " ";
                    }
                    
                    // Send the enemy positions
                    if (playerID == 1) {
                        dataOut.writeUTF(
                            String.format("%d POSITION-%f-%f %s", 
                                playerID, 
                                playerPositions.get(1)[0], 
                                playerPositions.get(1)[1], 
                                spellString
                            )
                        );
                    } else if (playerID == 2) {
                        dataOut.writeUTF(
                            String.format("%d POSITION-%f-%f %s", 
                                playerID, 
                                playerPositions.get(0)[0], 
                                playerPositions.get(0)[1], 
                                spellString
                            )
                        );
                    }
                    
                   
                    dataOut.flush();
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException from WTC run()");
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