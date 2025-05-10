package lib.menus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import lib.GameConfig;
import lib.network.*;

public class LocalPlayMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title, subtitle;
    private JButton hostBtn, joinBtn, practiceBtn, backBtn;
    private JPanel content;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color buttonBg2 = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;

    // Fonts
    private Font Jacquard, Pixelify;

    public LocalPlayMenu() {
        title = new JLabel("Keybound", SwingConstants.CENTER);
        subtitle = new JLabel("Local Play", SwingConstants.CENTER);
        backBtn = new JButton("< Back");
        hostBtn = new JButton("Host Lobby");
        joinBtn = new JButton("Join Lobby");
        practiceBtn = new JButton("Practice Solo");

        // Load Fonts and Bg image
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(30f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/LocalPlayMenuBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        content = new JPanel();
        content.setLayout(null);
        content.setOpaque(false);

        // Back Button
        backBtn.setFont(Pixelify);
        backBtn.setOpaque(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusable(false);
        backBtn.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.1) + 10, 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1) + 15,  
            150, 50
        );

        // Title
        title.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.2), 
            (int) (GameConfig.SCREEN_LENGTH * 0.5), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        title.setFont(Jacquard);
        title.setForeground(buttonBg2);

        // Subtitle
        subtitle.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.275), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.325), 
            (int) (GameConfig.SCREEN_LENGTH * 0.45), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.05)
        );
        subtitle.setFont(Pixelify);
        subtitle.setForeground(buttonBg2);

        // Host Lobby Button
        hostBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.475), 
            (int) (GameConfig.SCREEN_LENGTH * 0.2), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        hostBtn.setFont(Pixelify);
        hostBtn.setForeground(buttonTextColor);
        hostBtn.setBackground(buttonBg1);

        // Join Lobby Button
        joinBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.6), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.475), 
            (int) (GameConfig.SCREEN_LENGTH * 0.2), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        joinBtn.setFont(Pixelify);
        joinBtn.setForeground(buttonTextColor);
        joinBtn.setBackground(buttonBg1);

        // Practice Button
        practiceBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.35), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.65), 
            (int) (GameConfig.SCREEN_LENGTH * 0.3), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        practiceBtn.setFont(Pixelify);
        practiceBtn.setForeground(buttonTextColor);
        practiceBtn.setBackground(buttonBg1);

        // Add Event Listeners
        backBtn.addActionListener(this);
        hostBtn.addActionListener(this);
        joinBtn.addActionListener(this);
        practiceBtn.addActionListener(this);

        JPanel menuBackground = new JPanel();
        menuBackground.setBackground(new Color(234, 212, 170, 225));
        menuBackground.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.1), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1), 
            (int) (GameConfig.SCREEN_LENGTH * 0.8), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.8)
        );

        content.add(title);
        content.add(subtitle);
        content.add(backBtn);
        content.add(hostBtn);
        content.add(joinBtn);
        content.add(practiceBtn);
        content.add(menuBackground);

        // ---- Set up frame ----- // 
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // ----- Add components ----- //
        this.add(content, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JPanel mainFrame = (JPanel) this.getParent();

        if (e.getSource() == backBtn) {
            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new MainMenu());
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == hostBtn) {
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

            // joinBtn the created server as a new player
            Player p = new Player();
            mainFrame.add(p.getCanvas());
            p.connectToServer();

            mainFrame.revalidate();
            mainFrame.repaint();
        } else if (e.getSource() == joinBtn) {
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
