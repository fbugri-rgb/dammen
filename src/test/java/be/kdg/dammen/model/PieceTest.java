package be.kdg.dammen.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de klassen Piece en PieceColor.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class PieceTest {

    @Test
    @DisplayName("Een nieuwe schijf is een gewone schijf")
    void newPieceIsAMan() {
        Piece piece = new Piece(PieceColor.WHITE);
        assertEquals(PieceColor.WHITE, piece.getColor());
        assertEquals(PieceType.MAN, piece.getType());
        assertFalse(piece.isKing());
    }

    @Test
    @DisplayName("Een schijf kan een dam worden")
    void pieceCanBecomeKing() {
        Piece piece = new Piece(PieceColor.BLACK);
        piece.promoteToKing();
        assertTrue(piece.isKing());
        assertEquals(PieceType.KING, piece.getType());
        assertEquals(PieceColor.BLACK, piece.getColor(), "de kleur mag niet wijzigen");
    }

    @Test
    @DisplayName("Een schijf zonder kleur of type wordt geweigerd")
    void pieceWithoutColorOrTypeIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Piece(null));
        assertThrows(IllegalArgumentException.class, () -> new Piece(PieceColor.WHITE, null));
    }

    @Test
    @DisplayName("Elke kleur heeft de andere kleur als tegenstander")
    void colorHasAnOpponent() {
        assertEquals(PieceColor.BLACK, PieceColor.WHITE.getOpponent());
        assertEquals(PieceColor.WHITE, PieceColor.BLACK.getOpponent());
    }

    @Test
    @DisplayName("Wit speelt naar boven, zwart naar beneden")
    void colorsPlayInOppositeDirections() {
        assertEquals(-1, PieceColor.WHITE.getForwardDirection());
        assertEquals(1, PieceColor.BLACK.getForwardDirection());
    }

    @Test
    @DisplayName("Elke kleur heeft een nederlandse naam voor op het scherm")
    void colorHasADutchName() {
        assertEquals("Wit", PieceColor.WHITE.getDutchName());
        assertEquals("Zwart", PieceColor.BLACK.getDutchName());
    }
}
