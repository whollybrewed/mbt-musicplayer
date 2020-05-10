package com.mbt;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.File;

public class MusicPlayer {
    private Clip clip;
    private String state = null;
    private Long timePos;
    private boolean fileExist = false;

/*    public static void main(String[] args) throws InterruptedException {
        MusicPlayer musicObj = new MusicPlayer();
        musicObj.open("C:\\MBT_HW2\\StarWars60.wav");
        musicObj.start();
        Thread.sleep(2000);
        musicObj.pause();
        Thread.sleep(2000);
        musicObj.resume();
        Thread.sleep(2000);
        musicObj.stop();
        JOptionPane.showMessageDialog(null,"Click OK to exit");
    }*/

    public void open(String filepath)
    {
        try {
            timePos = 0L;
            File musicFile = new File(filepath);
            fileExist = musicFile.exists();
            AudioInputStream musicInput = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(musicInput);
            state = "ready";
        }
        catch(Exception e) {
            System.out.println("File not found");
        }
    }

    public void start()
    {
        if (state == "ready") {
            timePos = 0L;
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            state = "playing";
        }
    }

    public void stop()
    {
        if (state!= "ready"){
            clip.stop();
            timePos = 0L;
            clip.setMicrosecondPosition(timePos);
            state = "ready";
        }
    }

    public void pause()
    {
        if (state == "playing") {
            timePos = clip.getMicrosecondPosition();
            clip.stop();
            state = "paused";
        }
    }

    public void resume()
    {
        if (state == "paused") {
            clip.setMicrosecondPosition(timePos);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            state = "playing";
        }
    }

    public boolean getFileExist(){
        return fileExist;
    }

    public String getState(){
        return state;
    }

    public Long getTimePos(){
        return timePos;
    }
}
