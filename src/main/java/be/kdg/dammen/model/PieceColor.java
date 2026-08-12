package be.kdg.dammen.model;

/**
 * De kleur van een damschijf en dus ook van een speler.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public enum PieceColor {
    WHITE("Wit"),
    BLACK("Zwart");

    private final String dutchName;

    PieceColor(String dutchName) {
        this.dutchName = dutchName;
    }

    /**
     * @return de naam van de kleur zoals ze aan de speler getoond wordt
     */
    public String getDutchName() {
        return dutchName;
    }

    /**
     * @return de kleur van de tegenstander
     */
    public PieceColor getOpponent() {
        if (this == WHITE) {
            return BLACK;
        }
        return WHITE;
    }

    /**
     * Witte schijven spelen naar boven (rij 9 naar rij 0), zwarte naar beneden.
     *
     * @return -1 voor wit, +1 voor zwart
     */
    public int getForwardDirection() {
        if (this == WHITE) {
            return -1;
        }
        return 1;
    }
}
