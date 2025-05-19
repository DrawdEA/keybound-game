/**
 * The LocalJoinLobbyMenu is responsible for handling the GUI for when a player joins.
 * These include having the information about the server.
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
package lib.menus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import lib.GameConfig;
import lib.Sound;
import lib.network.*;

public class LocalJoinLobbyMenu extends JPanel implements ActionListener {
    // Components.
    private JLabel title, subtitle;
    private JTextArea connectionDetails;
    private JButton backBtn;
    private JPanel players, content;

    private BufferedImage bgImage;

    private final Color titleTextColor = new Color(38,43,68);

    // Fonts.
    private Font Jacquard, Pixelify;

    // Server Networking.
    private String ip;
    private int port;
    private Player p;
    
    final LocalJoinLobbyMenu thisMenu = this;

    // Constructor for lobby hosts.
    public LocalJoinLobbyMenu(String ip) {
        // Collection connection details.
        this.ip = ip;
        port = 10000;

        title = new JLabel("Keybound", SwingConstants.CENTER);
        subtitle = new JLabel("Local Lobby", SwingConstants.CENTER);
        backBtn = new JButton("< Back");

        players = new JPanel();
        players.setLayout(new GridLayout(5,1));

        connectionDetails = new JTextArea(String.format(
            "IP Address: %s\nPort: %d"
            , ip, port
        ));
        connectionDetails.setEditable(false);

        // Load Fonts and background image.
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(30f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/LobbyMenuBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Setup JPanel that holds everything.
        content = new JPanel();
        content.setLayout(null);
        content.setOpaque(false);

        JPanel menuBackground = new JPanel();
        menuBackground.setBackground(new Color(234, 212, 170, 225));
        menuBackground.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.1), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1), 
            (int) (GameConfig.SCREEN_LENGTH * 0.8), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.8)
        );

        // Back Button.
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

        // Title.
        title.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.2), 
            (int) (GameConfig.SCREEN_LENGTH * 0.5), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        title.setFont(Jacquard);
        title.setForeground(titleTextColor);

        // Subtitle.
        subtitle.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.275), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.325), 
            (int) (GameConfig.SCREEN_LENGTH * 0.45), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.05)
        );
        subtitle.setFont(Pixelify);
        subtitle.setForeground(titleTextColor);

        // Players.
        players.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.45), 
            (int) (GameConfig.SCREEN_LENGTH * 0.35), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.4)
        );
        players.setOpaque(false);

        // Connection Details.
        connectionDetails.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.55), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.45), 
            (int) (GameConfig.SCREEN_LENGTH * 0.3), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        connectionDetails.setFont(Pixelify.deriveFont(20f));
        connectionDetails.setForeground(titleTextColor);
        connectionDetails.setBackground(new Color(228, 166, 114));
        connectionDetails.setMargin(new Insets(15,15,15,15));

        backBtn.addActionListener(this);

        content.add(title);
        content.add(subtitle);
        content.add(backBtn);
        content.add(players);
        content.add(connectionDetails);
        content.add(menuBackground);

        // Set up frame.
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // Add components.
        this.add(content, BorderLayout.CENTER);

        /**
         * Server Thread.
         * Join the created host's lobby as a player.
         */
        p = new Player();
        p.connectToServer(ip);

        // Thread to show all new players connected to the lobby.
        new Thread() {
            public void run() {
                int displayedPlayers = 0;
                String[] colorPaths = {"purple", "red", "green", "gray", "yellow", "blue", "orange"};

                while (true) {
                    if (!p.getIsInGame()) {
                        int currentPlayersInLobby = p.getNumOfConnectedPlayers();

                        if (currentPlayersInLobby != displayedPlayers) {
                            players.removeAll();
                            // Add new player JLabels.
                            for (int i = 0; i < currentPlayersInLobby; i++) {

                                try {
                                    Box box = Box.createHorizontalBox();
                                    BufferedImage image = ImageIO.read(getClass().getResourceAsStream(String.format("/resources/player/%s.png", colorPaths[i]))).getSubimage(0, 0, 64, 32);
                                
                                    JLabel imageLabel = new JLabel();
                                    imageLabel.setIcon(new ImageIcon(image));

                                    box.add(imageLabel);
                                    box.add(new JLabel("Player " + (i+1)), SwingConstants.CENTER);

                                    players.add(box, SwingConstants.CENTER);
                                    players.revalidate();
                                    players.repaint();

                                    System.out.printf("Player %d has joined the lobby\n", i+1);

                                } catch (Exception ex) {
                                    System.err.println(ex);
                                }
                            }
                            displayedPlayers = currentPlayersInLobby;
                        }
                    } else {
                        JPanel mainFrame = (JPanel) thisMenu.getParent();
                        mainFrame.remove(thisMenu);
                        mainFrame.repaint();
                        mainFrame.add(p.getCanvas());
                        mainFrame.revalidate();
                        mainFrame.repaint();
                        break;
                    }

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        System.err.println(ex);
                    }
                }
            }
        }.start();
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
            Sound openSound = new Sound(2);
            openSound.play();
            
            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new LocalPlaySelectionMenu(), BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}