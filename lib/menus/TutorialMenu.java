/**
 * The TutorialMenu is responsible for showcasing the about and how to play the the game
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
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.imageio.ImageIO;
import javax.swing.*;
import lib.GameConfig;
import lib.Sound;
import lib.network.*;

public class TutorialMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title, keyboardDemo;
    private JButton backBtn;
    private JButton movementTab, fireTab, waterTab, earthTab, windTab;
    private JTextArea description;
    private JPanel content;

    private BufferedImage bgImage;

    private final Color buttonBg1 = new Color(58,68,102);
    private final Color titleTextColor = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;
    
    // Fonts.
    private Font Jacquard, Pixelify;

    // Descriptions
    private final String MOVEMENT_DESCRIPTION = "Use the arrow keys to move around and TAB to view the score board";
    private final String FIRE_DESCRIPTION = "Shoot a fast moving projectile in the direction of the last arrow key";
    private final String WATER_DESCRIPTION = "Shoot a wide area attack a short distance in the direction of the last arrow key";
    private final String WIND_DESCRIPTION = "Dash forward quickly in the direction of the last arrow key";
    private final String EARTH_DESCRIPTION = "Arise a structure that deals damage upon contact with the enemy";


    public TutorialMenu() {
        title = new JLabel("Movement", SwingConstants.CENTER);
        backBtn = new JButton("< Back");
        movementTab = new JButton("Movement");
        fireTab = new JButton("Fire Spell");
        waterTab = new JButton("Water Spell");
        earthTab = new JButton("Earth Spell");
        windTab = new JButton("Wind Spell");
        keyboardDemo = new JLabel();
        description = new JTextArea();
        
        // Load Fonts and background image.
        try {
            // Fonts
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(15f);

            // Images
            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/TutorialBg.png");
            bgImage = ImageIO.read(imgStream);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // Setup JPanel that holds everything.
        content = new JPanel();
        content.setLayout(null);
        content.setOpaque(false);

        // Back Button.
        backBtn.setFont(Pixelify.deriveFont(30f));
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

        // Tab Buttons
        // Movement Button
        movementTab.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.075)
        );
        movementTab.setFont(Pixelify);
        movementTab.setBackground(buttonBg1);
        movementTab.setForeground(buttonTextColor);
        movementTab.setFocusable(false);

        // Fire Spell Button
        fireTab.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.28125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.075)
        );
        fireTab.setFont(Pixelify);
        fireTab.setBackground(buttonBg1);
        fireTab.setForeground(buttonTextColor);
        fireTab.setFocusable(false);

        // Water Spell Button
        waterTab.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.4375), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.075)
        );
        waterTab.setFont(Pixelify);
        waterTab.setBackground(buttonBg1);
        waterTab.setForeground(buttonTextColor);
        waterTab.setFocusable(false);

        // Wind Spell Button
        windTab.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.59375), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.075)
        );
        windTab.setFont(Pixelify);
        windTab.setBackground(buttonBg1);
        windTab.setForeground(buttonTextColor);
        windTab.setFocusable(false);

        // Earth Spell Button
        earthTab.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.75), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.125), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.075)
        );
        earthTab.setFont(Pixelify);
        earthTab.setBackground(buttonBg1);
        earthTab.setForeground(buttonTextColor);
        earthTab.setFocusable(false);

        // Keyboard Demo
        replaceKeyboardDemoImage("Movement_Keys");
        keyboardDemo.setBounds(
            (int) ((GameConfig.SCREEN_LENGTH * 0.8 - 502)/2 + GameConfig.SCREEN_LENGTH * 0.1), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.5), 
            502,
            155
        );

        // Description
        description.setText(MOVEMENT_DESCRIPTION);
        description.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.2), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.5) + 200, 
            (int) (GameConfig.SCREEN_LENGTH * 0.6),
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(Pixelify.deriveFont(20f));
        description.setBackground(new Color(0,0,0,0));
        description.setDisabledTextColor(new Color(30, 30, 30));
        description.setEnabled(false);
        description.setHighlighter(null);

        // Add Event Listeners.
        backBtn.addActionListener(this);
        movementTab.addActionListener(this);
        fireTab.addActionListener(this);
        waterTab.addActionListener(this);
        windTab.addActionListener(this);
        earthTab.addActionListener(this);

        JPanel menuBackground = new JPanel();
        menuBackground.setBackground(new Color(234, 212, 170, 225));
        menuBackground.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.1), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1), 
            (int) (GameConfig.SCREEN_LENGTH * 0.8), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.8)
        );

        content.add(title);
        content.add(backBtn);
        content.add(movementTab);
        content.add(fireTab);
        content.add(waterTab);
        content.add(windTab);
        content.add(earthTab);
        content.add(keyboardDemo);
        content.add(description);
        content.add(menuBackground);

        // Set up frame.
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // Add components.
        this.add(content, BorderLayout.CENTER);

        // Handle hover effects.
        waterTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        windTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        earthTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        fireTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Sound hoverSound = new Sound(1);
                hoverSound.play();
            }
        });
        movementTab.addMouseListener(new MouseAdapter() {
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
            mainFrame.add(new MainMenu(), BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == movementTab) {
            Sound openSound = new Sound(0);
            openSound.play();

            title.setText("Movement");
            replaceKeyboardDemoImage("Movement_Keys");
            description.setText(MOVEMENT_DESCRIPTION);
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == fireTab) {
            Sound openSound = new Sound(0);
            openSound.play();

            title.setText("Fire Spell");
            replaceKeyboardDemoImage("Fire_Keys");
            description.setText(FIRE_DESCRIPTION);
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == waterTab) {
            Sound openSound = new Sound(0);
            openSound.play();

            title.setText("Water Spell");
            replaceKeyboardDemoImage("Water_Keys");
            description.setText(WATER_DESCRIPTION);
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == windTab) {
            Sound openSound = new Sound(0);
            openSound.play();

            title.setText("Wind Spell");
            replaceKeyboardDemoImage("Wind_Keys");
            description.setText(WIND_DESCRIPTION);
            mainFrame.revalidate();
            mainFrame.repaint();

        } else if (e.getSource() == earthTab) {
            Sound openSound = new Sound(0);
            openSound.play();

            title.setText("Earth Spell");
            replaceKeyboardDemoImage("Earth_Keys");
            description.setText(EARTH_DESCRIPTION);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

    private void replaceKeyboardDemoImage(String name) {
        String path = "resources/images/Keyboards/" + name + ".png";
        InputStream keyboarDemoStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        try {
            BufferedImage keyboardImg = ImageIO.read(keyboarDemoStream);
            keyboardDemo.setIcon(new ImageIcon(keyboardImg));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}