package be.kdg.dammen.model;

/**
 * De toestand waarin een damspel zich bevindt.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public enum GameStatus {
    PLAYING,
    WHITE_WON,
    BLACK_WON,
    DRAW;

    /**
     * @return true zolang het spel nog bezig is
     */
    public boolean isFinished() {
        return this != PLAYING;
    }
}
