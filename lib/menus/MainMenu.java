package lib.menus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import lib.GameConfig;
import lib.network.*;

public class MainMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title, names;
    private JButton onlinePlayBtn, localPlayBtn, tutorialBtn;
    private JPanel content;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color titleTextColor = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;

    // Fonts
    private Font Jacquard, Pixelify;

    public MainMenu() {
        title = new JLabel("Keybound", SwingConstants.CENTER);
        onlinePlayBtn = new JButton("Online");
        localPlayBtn = new JButton("Local");
        tutorialBtn = new JButton("Tutorial");
        names = new JLabel("By: Diesta, Edward & Uy, Charles Joshua");

        // Load Fonts and Bg image
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(110f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(50f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/MainMenuBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Style components
        onlinePlayBtn.setFont(Pixelify);
        onlinePlayBtn.setBackground(buttonBg1);
        onlinePlayBtn.setForeground(buttonTextColor);
        onlinePlayBtn.setFocusable(false);
        
        localPlayBtn.setFont(Pixelify);
        localPlayBtn.setBackground(buttonBg1);
        localPlayBtn.setForeground(buttonTextColor);
        localPlayBtn.setFocusable(false);

        tutorialBtn.setFont(Pixelify);        
        tutorialBtn.setBackground(buttonBg1);
        tutorialBtn.setForeground(buttonTextColor);
        tutorialBtn.setFocusable(false);
        
        content = new JPanel();
        content.setLayout(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4,1,0,35));
        buttonsPanel.setOpaque(false);

        // ----- Styling Components ----- //
        title.setFont(Jacquard);
        title.setForeground(titleTextColor);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        content.add(title, gbc);

        buttonsPanel.add(onlinePlayBtn);
        buttonsPanel.add(localPlayBtn);
        buttonsPanel.add(tutorialBtn);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(100, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        content.add(buttonsPanel, gbc);

        names.setForeground(Color.white);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = new Insets(100, 0, 0, 0);
        content.add(names, gbc);

        // ----- Button listeners added ---- //
        onlinePlayBtn.addActionListener(this);
        localPlayBtn.addActionListener(this);
        
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
        if (e.getSource() == onlinePlayBtn) {
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

            // localPlayBtn the created server as a new player
            Player p = new Player();
            mainFrame.add(p.getCanvas());
            p.connectToServer();

            mainFrame.revalidate();
            mainFrame.repaint();
        } else if (e.getSource() == localPlayBtn) {
            mainFrame.remove(this);
            mainFrame.add(new LocalPlaySelectionMenu());

            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}
