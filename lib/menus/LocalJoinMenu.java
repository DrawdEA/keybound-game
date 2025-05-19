/**
 * The LocalJoinMenu is responsible for handling the GUI for setting up which host a player will join.
 * These include being able to type what ip he will join in.
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


public class LocalJoinMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title, subtitle;
    private JButton joinBtn, backBtn;
    private JPanel content;
    private JLabel ipLabel;
    private JTextField ipInput;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color titleTextColor = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;

    // Fonts.
    private Font Jacquard, Pixelify;

    public LocalJoinMenu() {
        title = new JLabel("Keybound", SwingConstants.CENTER);
        subtitle = new JLabel("Local Join", SwingConstants.CENTER);
        backBtn = new JButton("< Back");
        ipLabel = new JLabel("Local Host's IP:");
        ipInput = new JTextField("localhost");
        joinBtn = new JButton("Join Lobby");

        // Load Fonts and background image.
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(30f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/LocalJoinMenuBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Setup JPanel that holds everything.
        content = new JPanel();
        content.setLayout(null);
        content.setOpaque(false);

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

        // IP Label.
        ipLabel.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.39), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.4), 
            (int) (GameConfig.SCREEN_LENGTH * 0.3), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.05)
        );
        ipLabel.setFont(Pixelify.deriveFont(30f));
        ipLabel.setForeground(titleTextColor);

        // IP Input.
        ipInput.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.475), 
            (int) (GameConfig.SCREEN_LENGTH * 0.5), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        ipInput.setFont(Pixelify.deriveFont(45f));
        ipInput.setBackground(new Color(139, 155, 180));
        ipInput.setForeground(titleTextColor);
        ipInput.setMargin(new Insets(5,10,5,10));

        // Join Button.
        joinBtn.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.4), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.675), 
            (int) (GameConfig.SCREEN_LENGTH * 0.2), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1)
        );
        joinBtn.setFont(Pixelify);
        joinBtn.setForeground(buttonTextColor);
        joinBtn.setBackground(buttonBg1);
        joinBtn.setFocusable(false);

        // Add Event Listeners.
        backBtn.addActionListener(this);
        joinBtn.addActionListener(this);

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
        content.add(ipLabel);
        content.add(ipInput);
        content.add(joinBtn);
        content.add(menuBackground);

        // Set up frame.
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // Add components.
        this.add(content, BorderLayout.CENTER);

        // Add hover feedback.
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        joinBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
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

        } else if (e.getSource() == joinBtn) {
            Sound openSound = new Sound(0);
            openSound.play();

            mainFrame.remove(this);
            mainFrame.repaint();
            mainFrame.add(new LocalJoinLobbyMenu(ipInput.getText()));
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }
}
