package lib.menus;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.swing.*;
import lib.GameConfig;
import lib.network.*;
import org.ietf.jgss.GSSContext;

public class LocalHostLobbyMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title, subtitle;
    private JTextArea connectionDetails;
    private JButton startBtn, backBtn;
    private JRadioButton arcaneModeRadioButton, apprenticeModeRadioButton;
    private ButtonGroup modeRadioButtonGroup;
    private JPanel players, content;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color buttonTextColor = Color.WHITE;
    private final Color titleTextColor = new Color(38,43,68);

    // Fonts
    private Font Jacquard, Pixelify;

    // Server Networking
    private GameServer gs;
    private String ip;
    private int port;
    private Player p;

    // Constructor for lobby hosts
    public LocalHostLobbyMenu() {
        // Collection connection details
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (Exception ex){
            System.err.println(ex);
        }
        port = 10000;

        title = new JLabel("Keybound", SwingConstants.CENTER);
        subtitle = new JLabel("Local Lobby", SwingConstants.CENTER);
        backBtn = new JButton("< Back");
        startBtn = new JButton("Start!");
        arcaneModeRadioButton = new JRadioButton("Arcane Mode", true);
        apprenticeModeRadioButton = new JRadioButton("Apprentice Mode", false);

        modeRadioButtonGroup = new ButtonGroup();
        modeRadioButtonGroup.add(arcaneModeRadioButton);
        modeRadioButtonGroup.add(apprenticeModeRadioButton);

        players = new JPanel();
        players.setLayout(new GridLayout(5,1));

        connectionDetails = new JTextArea(String.format(
            "IP Address: %s\nPort: %d"
            , ip, port
        ));
        connectionDetails.setEditable(false);

        // Load Fonts and Bg image
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

        // Setup JPanel that holds everything
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
        title.setForeground(titleTextColor);

        // Subtitle
        subtitle.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.275), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.325), 
            (int) (GameConfig.SCREEN_LENGTH * 0.45), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.05)
        );
        subtitle.setFont(Pixelify);
        subtitle.setForeground(titleTextColor);

        // Players
        players.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.45), 
            (int) (GameConfig.SCREEN_LENGTH * 0.35), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.4)
        );
        players.setOpaque(false);

        // Connection Details
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

        // Mode Selection
        arcaneModeRadioButton.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.625), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.575), 
            (int) (GameConfig.SCREEN_LENGTH * 0.3), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        arcaneModeRadioButton.setFont(Pixelify.deriveFont(25f));
        arcaneModeRadioButton.setForeground(titleTextColor);
        arcaneModeRadioButton.setOpaque(false);
        arcaneModeRadioButton.setFocusable(false);

        apprenticeModeRadioButton.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.625), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.625), 
            (int) (GameConfig.SCREEN_LENGTH * 0.3), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        apprenticeModeRadioButton.setFont(Pixelify.deriveFont(25f));
        apprenticeModeRadioButton.setForeground(titleTextColor);
        apprenticeModeRadioButton.setOpaque(false);
        apprenticeModeRadioButton.setFocusable(false);

        // Start Button
        startBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.7), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.75), 
            (int) (GameConfig.SCREEN_LENGTH * 0.15), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        startBtn.setFont(Pixelify);
        startBtn.setForeground(buttonTextColor);
        startBtn.setBackground(buttonBg1);
        startBtn.setFocusable(false);

        backBtn.addActionListener(this);
        startBtn.addActionListener(this);

        content.add(title);
        content.add(subtitle);
        content.add(backBtn);
        content.add(players);
        content.add(connectionDetails);
        content.add(arcaneModeRadioButton);
        content.add(apprenticeModeRadioButton);
        content.add(startBtn);
        content.add(menuBackground);

        // ---- Set up frame ----- // 
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // ----- Add components ----- //
        this.add(content, BorderLayout.CENTER);


        // ----- Server Thread ----- // 
        // Start game server on host machine
        gs = new GameServer();

        // Thread to show all new players connected to the lobby
        new Thread() {
            public void run() {
                int displayedPlayers = 0;
                String[] colorPaths = {"purple", "red", "green", "gray", "yellow", "blue", "orange"};

                while (true) {
                    int currentPlayersInLobby = gs.getNumPlayersInLobby();
                    if (currentPlayersInLobby != displayedPlayers) {
                        players.removeAll();
                        // Add new player JLabels
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

                                System.out.printf("Player %d has joined the lobby", i+1);

                            } catch (Exception ex) {
                                System.err.println(ex);
                            }
                        }
                        displayedPlayers = currentPlayersInLobby;
                    }

                    // Prevent this loop from consuming 100% CPU
                    try {
                        Thread.sleep(500); // Check every 500ms
                    } catch (InterruptedException ex) {
                        System.err.println(ex);
                    }
                }
            }
        }.start();
        
        // Thread to accept connections
        new Thread() {
            public void run() {
                gs.acceptConnections();
            }
        }.start();

        // Join the created host's lobby as a player
        p = new Player();
        p.connectToServer(ip);
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
            gs.closeConnections();
            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new LocalPlaySelectionMenu(), BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        } else if (e.getSource() == startBtn) {
            gs.startGame();
            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(p.getCanvas());
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}