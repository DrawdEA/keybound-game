package lib.network;

import java.io.*;
import java.net.*;
import lib.*;

public class GameServer {
    private ServerSocket ss;
    private int players;

    private ReadFromClient p1ReadRunnable, p2ReadRunnable;
    private WriteToClient p1WriteRunnable, p2WriteRunnable;

    private double p1x, p1y, p2x, p2y;

    public GameServer() {
        System.out.println("==== GAME SERVER ====");
        players = 0;
        p1x = 50;
        p1y = 50;
        p2x = 50;
        p2y = 500;

        try {
            ss = new ServerSocket(10000);

        } catch(IOException ex) {
            System.out.println("IOException from GameServer constructor");
        }
    }

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
                    if (playerID == 1) {
                        p1x = dataIn.readDouble();
                        p1y = dataIn.readDouble();
                    } else {
                        p2x = dataIn.readDouble();
                        p2y = dataIn.readDouble();
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
                    if (playerID == 1) {
                        dataOut.writeDouble(p2x);
                        dataOut.writeDouble(p2y);
                    } else {
                        dataOut.writeDouble(p1x);
                        dataOut.writeDouble(p1y);
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
            } catch (IOException ex) {
                System.out.println("IOException from sendStartMsg()");
            }
        }
    }
}