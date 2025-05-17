package lib.audio;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import javax.sound.sampled.*;

public class AudioPlayer  
{ 
    private Long currentTimestamp; 
    private Clip clip; 
      
    // current status of clip 
    private String status;
    private int trackNum;
    private int numOfTracks;
    
    private AudioInputStream audioInputStream;
    private File[] allMusicFiles;
    private LineListener endOfTrackListener;
  
    /**
     * AudioPlayer constructor to import all the music
     * 
     * @param folderPath the relative path TO the folder with music FROM where the AudioPlayer instance was made
     */
    public AudioPlayer(String folderPath) throws UnsupportedAudioFileException, IOException, LineUnavailableException  { 
        trackNum = 0;
        allMusicFiles = new File(folderPath).listFiles();
        numOfTracks = allMusicFiles.length;
        
        // Shuffle the array with all music
        Collections.shuffle(Arrays.asList(allMusicFiles));

        // If Littleroot Town is in the folder set it as the first song
        for (int i = 0; i < numOfTracks; i++){
            if (allMusicFiles[i].getName().equals("Littleroot Town.wav")){
                File temp = allMusicFiles[0];
                allMusicFiles[0] = allMusicFiles[i];
                allMusicFiles[i] = temp;
            }
        }

        // Place the first song in the playlist as the 
        audioInputStream = AudioSystem.getAudioInputStream(allMusicFiles[0].getAbsoluteFile());
          
        clip = AudioSystem.getClip(); 
        clip.open(audioInputStream);

        // Initialize the listener once
        endOfTrackListener = new LineListener() {
            @Override
            public void update(LineEvent evt) {
                if (evt.getType() == LineEvent.Type.STOP) {
                    try {
                        // Check if we're at end of track (within 50ms margin)
                        if (status.equals("play") && clip.getMicrosecondPosition() >= clip.getMicrosecondLength() - 50000) {
                            skip();
                            play();
                        }
                    } catch (Exception ex) { 
                        System.out.println("Error with auto-skip functionality."); 
                        ex.printStackTrace(); 
                    } 
                }
            }
        };

        clip.addLineListener(endOfTrackListener);
    } 
    
    /**
     * A method to convert microseconds to the more understandable mm:ss format
     * 
     * @param microsecond the microseconds that is to be converted to minutes and seconds
     */
    public String convertToTimestamp(long microsecond){
        int seconds = (int) (microsecond / 1000000);
        return String.format("%d:%2d", seconds/60, seconds%60).replace(" ", "0");
    }
    
    /**
     * A method to start the audio player and play whatever song is in the playlist
     */
    public void play() {
        status = "play";
        clip.start();

        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent evt) {
                if (evt.getType() == LineEvent.Type.STOP){
                    try {
                        if (clip.getMicrosecondPosition() >= clip.getMicrosecondLength()) {
                            System.out.printf("%d / %d", clip.getMicrosecondPosition(), clip.getMicrosecondLength());
                            skip();
                            play();
                        }
                    } catch (Exception ex) { 
                        System.out.println("Error with playing sound."); 
                        ex.printStackTrace(); 
                    } 
                }
            }
        });
    } 
    
    /**
     * A method to pause the audio player and save the current position
     */
    public void pause()  
    { 
        if (status.equals("paused"))  
        { 
            System.out.println("audio is already paused"); 
            return; 
        } 
        currentTimestamp = clip.getMicrosecondPosition(); 
        clip.stop(); 
        status = "paused"; 
    } 
    
    /**
     * A method to play the current song based on the last saved time
     */
    public void resume() throws UnsupportedAudioFileException, IOException, LineUnavailableException  { 
        if (status.equals("play")) { 
            return; 
        } 
        clip.close(); 
        resetAudioStream(); 
        clip.setMicrosecondPosition(currentTimestamp); 
        this.play();
        status = "play";
    } 
      
    /**
     * A method to stop the current song (different from pause as the pause is stop and save)
     */
    public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
        currentTimestamp = 0L; 
        clip.stop(); 
        clip.close(); 
    } 
      
    /**
     * A method to reset the audio stream
     */ 
    public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        clip.stop(); 
        clip.close();
        audioInputStream = AudioSystem.getAudioInputStream(allMusicFiles[trackNum].getAbsoluteFile()); 
        clip.open(audioInputStream);
    } 

    /**
     * A method to skip to the next song in the playlist
     */
    public void skip() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        clip.stop();
        if (trackNum + 1 == numOfTracks){
            trackNum = 0;
        } else {
            trackNum++;
        }
        resetAudioStream();
        
        if (status.equals("play"))
            play();
    }

    /**
     * A method to go back to the previous song in the playlist
     */
    public void previous() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        clip.stop();
        if (trackNum == 0){
            trackNum = numOfTracks - 1;
        } else {
            trackNum--;
        }
        resetAudioStream();

        if (status.equals("play"))
            play();
    }

    /**
     * A method to jump to a specific timestamp in the song
     */
    public void jump(long c) throws UnsupportedAudioFileException, IOException, LineUnavailableException { 
        if (c > 0 && c < clip.getMicrosecondLength()) { 
            clip.stop();
            resetAudioStream(); 
            currentTimestamp = c; 
            clip.setMicrosecondPosition(c); 
            this.play(); 
        } 
    } 

    /**
     * A method to shuffle all the songs in front of the currently playing song
     */
    public void shuffle(){
        // If we are not on the last track then we shuffle
        if (trackNum != numOfTracks - 1){
            File[] tracksAhead = new File[numOfTracks - trackNum - 1];

            // Place all the tracks ahead of the current track in our new array
            for (int i = 0; i < tracksAhead.length; i++) {
                tracksAhead[i] = allMusicFiles[trackNum + i + 1];
            }

            // Shuffle the array with all the tracks we need to shuffle
            Collections.shuffle(Arrays.asList(tracksAhead));
            
            // Place the shuffled list back into the playlist
            for (int i = 0; i < tracksAhead.length; i++) {
                allMusicFiles[trackNum + i + 1] = tracksAhead[i];
            }
        }
    }

    /**
     * A method to get the current track length in mm:ss format
     */
    public String getTrackLength(){
        return convertToTimestamp(clip.getMicrosecondLength());
    }

    /**
     * A method to get the timestamp of where the player is at on the current song
     */
    public String getCurrentTrackTime(){
        return convertToTimestamp(clip.getMicrosecondPosition());
    }

    /**
     * A method to get the completion rate of the currently playing song from 0 to 1
     */
    public float getCompletionRate() {
        return (float) (clip.getMicrosecondPosition()) / clip.getMicrosecondLength();
    }

    /**
     * A method to get the name of the current song and remove the .wav file extension
     */
    public String getName() {
        String fileName = allMusicFiles[trackNum].getName(); 
        return fileName.substring(0, fileName.length()-4);
    }

    /**
     * A method to return the current playlist of music to be played
     */
    public String getPlaylist() {
        String playlist = "";

        for (int i = 0; i < allMusicFiles.length; i++) {
            String fileName = allMusicFiles[i].getName();
            String name = fileName.substring(0, fileName.length()-4);
            
            if (i == trackNum) { // Add the indicator for the current song
                playlist += String.format(">\t%d. %s\n", i+1, name);
            } else {
                playlist += String.format("\t%d. %s\n", i+1, name);
            }
        }

        return playlist;
    }
} 