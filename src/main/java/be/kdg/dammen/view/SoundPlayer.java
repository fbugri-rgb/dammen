package be.kdg.dammen.view;

import javafx.scene.media.AudioClip;

import java.net.URL;

/**
 * Speelt de geluidjes van het spel af: een schijf verzetten, slaan, een dam halen en
 * winnen. Staat het geluid uit in de instellingen, dan blijft alles stil.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class SoundPlayer {

    private static final String SOUND_FOLDER = "/be/kdg/dammen/view/sounds/";

    private final AudioClip moveSound;
    private final AudioClip captureSound;
    private final AudioClip kingSound;
    private final AudioClip winSound;
    private boolean soundOn;

    public SoundPlayer(boolean soundOn) {
        this.moveSound = loadSound("verzetten.wav");
        this.captureSound = loadSound("slaan.wav");
        this.kingSound = loadSound("dam.wav");
        this.winSound = loadSound("winnen.wav");
        this.soundOn = soundOn;
    }

    /**
     * Laadt één geluidsbestand. Lukt dat niet, bijvoorbeeld omdat het bestand ontbreekt
     * of omdat deze computer geen geluid kan afspelen, dan speelt het spel gewoon
     * verder zonder dat geluidje.
     *
     * @return het geluid, of null als het niet geladen kon worden
     */
    private AudioClip loadSound(String fileName) {
        URL url = SoundPlayer.class.getResource(SOUND_FOLDER + fileName);
        if (url == null) {
            return null;
        }
        try {
            return new AudioClip(url.toExternalForm());
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public void playMove() {
        play(moveSound);
    }

    public void playCapture() {
        play(captureSound);
    }

    public void playKing() {
        play(kingSound);
    }

    public void playWin() {
        play(winSound);
    }

    /**
     * Speelt een geluid af, tenminste als het geluid aan staat en het bestand er is.
     */
    private void play(AudioClip sound) {
        if (soundOn && sound != null) {
            sound.play();
        }
    }
}
