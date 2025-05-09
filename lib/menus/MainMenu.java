package lib.menus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import lib.GameConfig;
import lib.network.*;

public class MainMenu extends JPanel implements ActionListener {
    JLabel title;
    JButton host, join;

    public MainMenu() {
        title = new JLabel("KEYBOUND");
        host = new JButton("Host Lobby");
        join = new JButton("Join Lobby");
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(title);
        content.add(host);
        content.add(join);

        // ----- Button listeners added ---- //
        host.addActionListener(this);
        join.addActionListener(this);
        
        // ---- Set up frame ----- // 
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // ----- Add components ----- //
        this.add(content, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel mainFrame = (JPanel) this.getParent();
        if (e.getSource() == host) {
            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new ServerMenu(), BorderLayout.CENTER);

            // Start a Game Server using an anonymous Thread
            new Thread() {
                public void run() {
                    // Game Server Stuff for sir choob
                    // Create a server
                    GameServer gs = new GameServer();
                    gs.acceptConnections();
                }
            }.start();

            // Join the created server as a new player
            Player p = new Player();
            mainFrame.add(p.getCanvas());
            p.connectToServer();

            mainFrame.revalidate();
            mainFrame.repaint();
        } else if (e.getSource() == join) {
            mainFrame.remove(this);

            // Player stuff for sir choob
            Player p = new Player();
            mainFrame.add(p.getCanvas());
            p.connectToServer();
            

            // legacy canvas
            //GameCanvas gc = new GameCanvas();
            //mainFrame.add(gc);
            //gc.connectToServer();
            //gc.addPlayers();

            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}
