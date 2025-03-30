package lib;

import java.io.*;
import java.net.*;

public class GameServer {
    ServerSocket ss;

    // A client thread for when a client is created.
    public class CThread extends Thread {
        private Socket s;

        public CThread(Socket s) {
            this.s = s;
        }

        public void run() {
            try {
                System.out.println("Accepted.");
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                String msg = in.readUTF();
                System.out.println("Client says " + msg);
                out.writeUTF("Hi");
                out.flush();
            } catch (IOException ex) {
                System.out.println("Error in the client thread!");
            }
        }
    }

    public GameServer() {
        try {
            ss = new ServerSocket(1000);

            while (true) {
                Socket s = ss.accept();
                CThread ct = new CThread(s);
                ct.start();
            }
        } catch(IOException ex) {
            System.out.println("Server error!");
        }
    }
    public static void main(String[] args) {
        GameServer gs = new GameServer();
    }
}