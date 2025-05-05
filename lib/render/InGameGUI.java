package lib.render;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import lib.GameConfig;
import lib.input.KeyBindings;

public class InGameGUI {
    private BufferedImage fullHeart, emptyHeart;
    private BufferedImage frame;

    private Font vcrOsdFont, comicRunesFont;

    private KeyBindings keyBindings;
    private PlayerObject player;

    public InGameGUI(KeyBindings kb, PlayerObject p) {
        try {
            keyBindings = kb;
            player = p;

            // Render the GUI elements.
            BufferedImage hearts = ImageIO.read(getClass().getResourceAsStream("/resources/gui/hearts.png"));
            fullHeart = hearts.getSubimage(0, 0, 16, 16);
            emptyHeart = hearts.getSubimage(0, 48, 16, 16);
            frame = ImageIO.read(getClass().getResourceAsStream("/resources/gui/frame.png"));

            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/VCR_OSD_MONO_1.001.ttf");
            vcrOsdFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(24f);

            InputStream stream2 = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/fonts/ComicRunes.otf");
            comicRunesFont = Font.createFont(Font.TRUETYPE_FONT, stream2).deriveFont(16f);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void renderGUI(Graphics2D g2d) {
        // Render the main GUI.
        for (int i = 0; i < 3; i++) {
            g2d.drawImage(frame, 916, 660 - i * 98, 88, 88, null);
        }

        // Render the health bar.
        int playerHealth = 4; // Replace with instant updates
        for (int i = 0; i < 5; i++) {
            if (i < playerHealth) {
                g2d.drawImage(fullHeart, 10 + 48 * i, 10, 48, 48, null);
            } else {
                g2d.drawImage(emptyHeart, 10 + 48 * i, 10, 48, 48, null);
            }
            
        }

        // Render the input text.
        g2d.setColor(Color.WHITE);
        g2d.setFont(comicRunesFont);
        String inputText = keyBindings.getLetters();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(inputText);
        int textAscent = fm.getAscent();
        g2d.drawString(inputText, (GameConfig.SCREEN_LENGTH / 2) - (textWidth / 2), (GameConfig.SCREEN_HEIGHT / 2) - (textAscent / 2) - 20);

        // Render the time.
        String timeString = "3:18";
        Font timeFont = vcrOsdFont.deriveFont(64f);
        g2d.setFont(timeFont);
        FontMetrics fm2 = g2d.getFontMetrics();
        int textWidth2 = fm2.stringWidth(timeString);
        int textAscent2 = fm2.getAscent();
        g2d.drawString(timeString, GameConfig.SCREEN_LENGTH - textWidth2 - 20, textAscent2 + 10);

        // Render the gamemode.
        String gamemode = "Deathmatch";
        Font gamemodeFont = vcrOsdFont.deriveFont(32f);
        g2d.setFont(gamemodeFont);
        FontMetrics fm3 = g2d.getFontMetrics();
        int textWidth3 = fm3.stringWidth(gamemode);
        int textAscent3 = fm3.getAscent();
        g2d.drawString(gamemode, GameConfig.SCREEN_LENGTH - textWidth3 - 20, textAscent3 + textAscent2 + 20);
    }
}
