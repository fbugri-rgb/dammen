package be.kdg.dammen.model;

/**
 * Eén zet van een speler: van welk veld naar welk veld, welke schijf er eventueel
 * geslagen werd en of de speler met deze zet een dam haalt.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Move {

    private final Position from;
    private final Position to;
    private final Position capturedPosition;
    private final PieceColor color;
    private final boolean promotion;

    public Move(Position from, Position to, PieceColor color) {
        this(from, to, null, color, false);
    }

    public Move(Position from, Position to, Position capturedPosition,
                PieceColor color, boolean promotion) {
        if (from == null || to == null || color == null) {
            throw new IllegalArgumentException("Een zet heeft een begin, een einde en een kleur nodig.");
        }
        this.from = from;
        this.to = to;
        this.capturedPosition = capturedPosition;
        this.color = color;
        this.promotion = promotion;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    /**
     * @return de positie van de geslagen schijf, of null als er niet geslagen werd
     */
    public Position getCapturedPosition() {
        return capturedPosition;
    }

    public PieceColor getColor() {
        return color;
    }

    public boolean isCapture() {
        return capturedPosition != null;
    }

    public boolean isPromotion() {
        return promotion;
    }

    /**
     * Vergelijkt enkel het begin- en eindveld. Zo kunnen we nakijken of de zet die
     * de speler wil doen bij de geldige zetten zit.
     *
     * @return true als beide zetten van hetzelfde veld naar hetzelfde veld gaan
     */
    public boolean hasSameSquares(Position otherFrom, Position otherTo) {
        return from.equals(otherFrom) && to.equals(otherTo);
    }

    /**
     * @return de zet in damnotatie, bijvoorbeeld "Wit 32-28" of "Zwart 19x30"
     */
    @Override
    public String toString() {
        String separator = "-";
        if (isCapture()) {
            separator = "x";
        }
        return color.getDutchName() + " " + from.getSquareNumber() + separator + to.getSquareNumber();
    }
}
