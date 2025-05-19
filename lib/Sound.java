/**
 * The Sound class is responsible for handling the game audio.
 * These include the music, button sounds, and input feedback.
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
package lib;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Sound {
    private Clip clip;
    private int repeats;
    private boolean isPlaying;
    private boolean isLooped;

    private static ArrayList<File> sounds;

    /**
     * Initializes the sound files for faster loading.
     */
    public static void initializeSounds() {
        sounds = new ArrayList<>();
        
        String pathname = "resources/sounds/";
        sounds.add(new File(pathname + "open.aiff"));
        sounds.add(new File(pathname + "hover.wav"));
        sounds.add(new File(pathname + "close.wav"));
    }

    /**
     * Instantiates a sound.
     * 
     * @param fileName the name of the file
     * @param isLooped boolean whether or not sound should be looped
     */
    public Sound(int fileIndex, boolean isLooped) {
        this.repeats = repeats > 0 ? repeats : 1;
        this.isPlaying = false;
        this.isLooped = isLooped;

        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sounds.get(fileIndex));
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error from Sound.java");
        }
    }

    /**
     * Instantiates a sound. If there is only 1 parameter, default looped to false.
     * 
     * @param fileName the name of the file
     */
    public Sound(int fileIndex) {
        this(fileIndex, false);
    }

    /**
     * Plays the sound.
     */
    public void play() {
        if (clip == null) return;
        isPlaying = true;
        clip.setFramePosition(0);

        // Loop clip if is continuous.
        if (isLooped) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        clip.start();
    }

    /**
     * Stops the sound.
     */
    public void stop() {
        if (clip != null && isPlaying) {
            clip.stop();
            clip.setFramePosition(0);
            isPlaying = false;
            clip.close();
        }
    }
}