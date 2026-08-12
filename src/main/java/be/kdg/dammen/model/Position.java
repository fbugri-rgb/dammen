package be.kdg.dammen.model;

/**
 * Een positie op het dambord: een rij en een kolom, beide van 0 tot en met 9.
 * Een positie wordt nooit gewijzigd nadat ze aangemaakt is.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Position {

    private final int row;
    private final int column;

    public Position(int row, int column) {
        if (!isInsideBoard(row, column)) {
            throw new IllegalArgumentException(
                    "Positie ligt buiten het bord: rij " + row + ", kolom " + column);
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Controleert of een rij en kolom op het bord liggen.
     *
     * @return true als de positie geldig is
     */
    public static boolean isInsideBoard(int row, int column) {
        return row >= 0 && row < Board.SIZE && column >= 0 && column < Board.SIZE;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Maakt een nieuwe positie die een aantal rijen en kolommen verder ligt.
     *
     * @return de nieuwe positie, of null als die buiten het bord valt
     */
    public Position move(int rowSteps, int columnSteps) {
        int newRow = row + rowSteps;
        int newColumn = column + columnSteps;
        if (!isInsideBoard(newRow, newColumn)) {
            return null;
        }
        return new Position(newRow, newColumn);
    }

    /**
     * Op een dambord zijn enkel de donkere velden genummerd, van 1 tot en met 50.
     * Dit nummer gebruiken we om de zetten leesbaar weer te geven.
     *
     * @return het veldnummer van deze positie
     */
    public int getSquareNumber() {
        return row * (Board.SIZE / 2) + column / 2 + 1;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position)) {
            return false;
        }
        Position otherPosition = (Position) other;
        return row == otherPosition.row && column == otherPosition.column;
    }

    @Override
    public int hashCode() {
        return row * Board.SIZE + column;
    }

    @Override
    public String toString() {
        return String.valueOf(getSquareNumber());
    }
}
