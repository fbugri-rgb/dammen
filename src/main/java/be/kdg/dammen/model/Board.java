package be.kdg.dammen.model;

/**
 * Het dambord. De volledige spelsituatie wordt bijgehouden in een tweedimensionale
 * array van 10 op 10. Een leeg veld bevat null. Er wordt enkel op de donkere velden
 * gespeeld.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Board {

    public static final int SIZE = 10;
    private static final int ROWS_WITH_PIECES = 4;

    private final Piece[][] squares;

    public Board() {
        this.squares = new Piece[SIZE][SIZE];
        placeStartingPieces();
    }

    /**
     * Maakt een volledige kopie van een ander bord. Elke schijf wordt opnieuw
     * aangemaakt, zodat beide borden los van elkaar gewijzigd kunnen worden.
     */
    public Board(Board other) {
        if (other == null) {
            throw new IllegalArgumentException("Er is een bord nodig om te kopiëren.");
        }
        this.squares = new Piece[SIZE][SIZE];
        copyFrom(other);
    }

    /**
     * Vervangt alle schijven op dit bord door die van een ander bord.
     */
    public void copyFrom(Board other) {
        if (other == null) {
            throw new IllegalArgumentException("Er is een bord nodig om te kopiëren.");
        }
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                squares[row][column] = copyOf(other.squares[row][column]);
            }
        }
    }

    private Piece copyOf(Piece piece) {
        if (piece == null) {
            return null;
        }
        return new Piece(piece.getColor(), piece.getType());
    }

    /**
     * Zet de 20 zwarte schijven bovenaan en de 20 witte schijven onderaan het bord.
     */
    private void placeStartingPieces() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (isDarkSquare(row, column)) {
                    if (row < ROWS_WITH_PIECES) {
                        squares[row][column] = new Piece(PieceColor.BLACK);
                    } else if (row >= SIZE - ROWS_WITH_PIECES) {
                        squares[row][column] = new Piece(PieceColor.WHITE);
                    }
                }
            }
        }
    }

    /**
     * Zet het bord terug klaar voor een nieuw spel.
     */
    public void reset() {
        clear();
        placeStartingPieces();
    }

    /**
     * Haalt alle schijven van het bord. Wordt gebruikt om een opgeslagen spel te laden.
     */
    public void clear() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                squares[row][column] = null;
            }
        }
    }

    /**
     * Er wordt enkel op de donkere velden gespeeld.
     *
     * @return true als dit een donker veld is
     */
    public static boolean isDarkSquare(int row, int column) {
        return (row + column) % 2 == 1;
    }

    /**
     * @return de schijf op deze positie, of null als het veld leeg is
     */
    public Piece getPiece(Position position) {
        checkPosition(position);
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Legt een schijf op een veld. De schijf mag null zijn om het veld leeg te maken.
     */
    public void setPiece(Position position, Piece piece) {
        checkPosition(position);
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Neemt de schijf van het bord en geeft ze terug.
     *
     * @return de schijf die weggenomen werd, of null als het veld al leeg was
     */
    public Piece removePiece(Position position) {
        Piece piece = getPiece(position);
        setPiece(position, null);
        return piece;
    }

    public boolean isEmpty(Position position) {
        return getPiece(position) == null;
    }

    /**
     * @return het aantal schijven dat deze kleur nog op het bord heeft
     */
    public int countPieces(PieceColor color) {
        int total = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                Piece piece = squares[row][column];
                if (piece != null && piece.getColor() == color) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * De damlijn is de basislijn van de tegenstander.
     *
     * @return de rij waarop een schijf van deze kleur een dam wordt
     */
    public static int getPromotionRow(PieceColor color) {
        if (color == PieceColor.WHITE) {
            return 0;
        }
        return SIZE - 1;
    }

    private void checkPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Positie mag niet leeg zijn.");
        }
    }
}
