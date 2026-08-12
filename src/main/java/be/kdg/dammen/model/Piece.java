package be.kdg.dammen.model;

/**
 * Eén damschijf op het bord. Een schijf heeft een kleur en is ofwel een gewone
 * schijf ofwel een dam.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Piece {

    private final PieceColor color;
    private PieceType type;

    public Piece(PieceColor color) {
        this(color, PieceType.MAN);
    }

    public Piece(PieceColor color, PieceType type) {
        if (color == null || type == null) {
            throw new IllegalArgumentException("Kleur en type van een schijf mogen niet leeg zijn.");
        }
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isKing() {
        return type == PieceType.KING;
    }

    /**
     * Maakt van deze schijf een dam.
     */
    public void promoteToKing() {
        this.type = PieceType.KING;
    }
}
