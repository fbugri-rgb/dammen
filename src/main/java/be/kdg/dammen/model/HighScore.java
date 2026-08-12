package be.kdg.dammen.model;

/**
 * Eén regel uit de topscores: wie won een spel, in hoeveel zetten en op welke datum.
 * Wie met de minste zetten wint, staat bovenaan.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HighScore implements Comparable<HighScore> {

    private final String playerName;
    private final int numberOfMoves;
    private final String date;

    public HighScore(String playerName, int numberOfMoves, String date) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Een topscore heeft een spelernaam nodig.");
        }
        if (numberOfMoves <= 0) {
            throw new IllegalArgumentException("Een topscore heeft minstens één zet nodig.");
        }
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Een topscore heeft een datum nodig.");
        }
        this.playerName = playerName;
        this.numberOfMoves = numberOfMoves;
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public String getDate() {
        return date;
    }

    /**
     * Minder zetten is een betere score.
     */
    @Override
    public int compareTo(HighScore other) {
        return this.numberOfMoves - other.numberOfMoves;
    }
}
