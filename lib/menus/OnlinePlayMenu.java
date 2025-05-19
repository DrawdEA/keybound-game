/**
 * The OnlinePlayMenu was supposed to allow players to play on a remote server
 * but it was sadly still coming soon...
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
import lib.network.*;

public class OnlinePlayMenu extends JPanel implements ActionListener {
    // Components
    private JLabel title1, title2;
    private JButton joinBtn, backBtn;
    private JPanel content;

    private BufferedImage bgImage;

    private final Color titleTextColor = new Color(38,43,68);
    private final Color buttonTextColor = Color.WHITE;
    
    // Fonts.
    private Font Jacquard, Pixelify;

    public OnlinePlayMenu() {
        title1 = new JLabel("Coming", SwingConstants.CENTER);
        title2 = new JLabel("Soon...", SwingConstants.CENTER);
        backBtn = new JButton("< Back");
        
        // Load Fonts and background image.
        try {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Jacquard12-Regular.ttf");
            Jacquard = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(100f);

            stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/Pixelify/PixelifySans-Regular.ttf");
            Pixelify = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(30f);

            InputStream imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/images/OnlinePlayBg.png");
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
        title1.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.35), 
            (int) (GameConfig.SCREEN_LENGTH * 0.5), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        title1.setFont(Jacquard);
        title1.setForeground(titleTextColor);

        title2.setBounds(            
            (int) (GameConfig.SCREEN_LENGTH * 0.25), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.45), 
            (int) (GameConfig.SCREEN_LENGTH * 0.5), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.15)
        );
        title2.setFont(Jacquard);
        title2.setForeground(titleTextColor);

        // Add Event Listeners.
        backBtn.addActionListener(this);
        
        JPanel menuBackground = new JPanel();
        menuBackground.setBackground(new Color(234, 212, 170, 225));
        menuBackground.setBounds(
            (int) (GameConfig.SCREEN_LENGTH * 0.1), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.1), 
            (int) (GameConfig.SCREEN_LENGTH * 0.8), 
            (int) (GameConfig.SCREEN_HEIGHT * 0.8)
        );

        content.add(title1);
        content.add(title2);
        content.add(backBtn);
        content.add(menuBackground);

        // Set up frame.
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(GameConfig.SCREEN_LENGTH, GameConfig.SCREEN_HEIGHT));
        this.requestFocusInWindow();
        this.setLayout(new BorderLayout());

        // Add components.
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
            mainFrame.add(new MainMenu(), BorderLayout.CENTER);
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

}