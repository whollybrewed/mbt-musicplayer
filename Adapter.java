package com.mbt;

public class Adapter {
    private MusicPlayer musicObj = new MusicPlayer();

    public void open(String filePath) {
        musicObj.open(filePath);
    }

    public void start() {
        musicObj.start();
    }

    public void stop() {
        musicObj.stop();
    }

    public void pause() {
        musicObj.pause();
    }

    public void resume() {
        musicObj.resume();
    }

    public boolean getFileExist() {
        return musicObj.getFileExist();
    }

    public String getState() {
        return musicObj.getState();
    }

    public Long getTimePos() {
        return musicObj.getTimePos();
    }

    public void init() {
        System.out.println("Adapter: init SUT");
    }

    public void reset() {
        System.out.println("Adapter: reset SUT");
    }
}