package lib;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerMenu extends JPanel implements ActionListener {
    JLabel title;
    JButton back, start;

    public ServerMenu() {
        title = new JLabel("Game Lobby");
        back = new JButton("Back");
        start = new JButton("Start");
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(title);
        content.add(back);
        content.add(start);

        // ----- Button listeners added ---- //
        back.addActionListener(this);
        start.addActionListener(this);
        
        // ---- Set up frame ----- // 
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(800, 600));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // ----- Add components ----- //
        this.add(content, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel mainFrame = (JPanel) this.getParent();
        if (e.getSource() == back) {
            mainFrame.remove(this);
            mainFrame.add(new MainMenu());
            mainFrame.revalidate();
            mainFrame.repaint();
        } else if (e.getSource() == start) {
            mainFrame.remove(this);

            // Player stuff
            //GameCanvas gc = new GameCanvas();
            //mainFrame.add(gc);
            //gc.connectToServer();
            //gc.addPlayers();
            
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}