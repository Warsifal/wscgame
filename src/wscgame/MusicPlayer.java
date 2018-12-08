package wscgame;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MusicPlayer {
    private URL url;
    AudioInputStream audioStream;
    Clip clip;

    public MusicPlayer(URL url) {
        this.url = url;
        try {
            audioStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            clip.setMicrosecondPosition(0);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        try {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
