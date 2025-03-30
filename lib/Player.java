package lib;

import java.io.*;
import java.net.*;

public class Player {
    public Player() {
        try {
            Socket s = new Socket("localhost", 1000);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF("Hello");
            out.flush();
            String msg = in.readUTF();
            System.out.println("Server says " + msg);
        } catch(IOException ex) {
            System.out.println("Client error!");
        }
    }

    public static void main(String[] args) {
        Player p = new Player();
    }
}
