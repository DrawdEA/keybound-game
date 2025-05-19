/**
 * The MainMenu class is responsible for showing the main visuals of the game.
 * It is also responsible for showing the options the player has for the game.
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

public class MainMenu extends JPanel implements ActionListener {
    // Components.
    private JLabel title, names;
    private JButton onlinePlayBtn, localPlayBtn, tutorialBtn;
    private JPanel content;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color titleTextColor = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;

    // Fonts.
    private Font Jacquard, Pixelify;

    public MainMenu() {
        title = new JLabel("Keybound", SwingConstants.CENTER);
        onlinePlayBtn = new JButton("Online");
        localPlayBtn = new JButton("Local");
        tutorialBtn = new JButton("Tutorial");
        names = new JLabel("By: Diesta, Edward & Uy, Charles Joshua");

        // Initialize the sounds.
        Sound.initializeSounds();

        // Load Fonts and background image.
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(130f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(50f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/MainMenuBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Style components.
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

        // Styling Components.
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

        // Button listeners added.
        onlinePlayBtn.addActionListener(this);
        localPlayBtn.addActionListener(this);
        tutorialBtn.addActionListener(this);
        
        // Set up frame.
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // Add components.
        this.add(content, BorderLayout.CENTER);

        // Add mouse listeners for hover.
        localPlayBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        tutorialBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        onlinePlayBtn.addMouseListener(new MouseAdapter() {
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
        if (e.getSource() == onlinePlayBtn) {
             Sound openSound = new Sound(0);
            openSound.play();
          
            mainFrame.remove(this);
            mainFrame.add(new OnlinePlayMenu());
            mainFrame.revalidate();
            mainFrame.repaint();
          
        } else if (e.getSource() == localPlayBtn) {
            Sound openSound = new Sound(0);
            openSound.play();

            mainFrame.remove(this);
            mainFrame.add(new LocalPlaySelectionMenu());
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == tutorialBtn) {
            Sound openSound = new Sound(0);
            openSound.play();

            mainFrame.remove(this);
            mainFrame.add(new TutorialMenu());
            mainFrame.revalidate();
            mainFrame.repaint();
        } 
    }
}
