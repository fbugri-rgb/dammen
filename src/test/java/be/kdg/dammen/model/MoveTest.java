package be.kdg.dammen.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de klasse Move: de notatie van een zet en het slaan.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class MoveTest {

    @Test
    @DisplayName("Een gewone zet wordt getoond met een streepje")
    void simpleMoveUsesADash() {
        Move move = new Move(new Position(6, 3), new Position(5, 2), PieceColor.WHITE);

        assertEquals("Wit 32-27", move.toString());
        assertFalse(move.isCapture());
        assertFalse(move.isPromotion());
        assertNull(move.getCapturedPosition());
    }

    @Test
    @DisplayName("Een slagzet wordt getoond met een kruisje")
    void captureUsesACross() {
        Move move = new Move(new Position(5, 2), new Position(3, 4),
                new Position(4, 3), PieceColor.WHITE, false);

        assertEquals("Wit 27x18", move.toString());
        assertTrue(move.isCapture());
        assertEquals(new Position(4, 3), move.getCapturedPosition());
    }

    @Test
    @DisplayName("Een zet die een dam oplevert, onthoudt dat")
    void promotionIsRemembered() {
        Move move = new Move(new Position(1, 2), new Position(0, 1),
                null, PieceColor.WHITE, true);

        assertTrue(move.isPromotion());
    }

    @Test
    @DisplayName("Een zet weet van welk veld naar welk veld ze gaat")
    void moveKnowsItsSquares() {
        Move move = new Move(new Position(6, 3), new Position(5, 2), PieceColor.BLACK);

        assertTrue(move.hasSameSquares(new Position(6, 3), new Position(5, 2)));
        assertFalse(move.hasSameSquares(new Position(6, 3), new Position(5, 4)));
        assertEquals(PieceColor.BLACK, move.getColor());
    }

    @Test
    @DisplayName("Een zet zonder begin, einde of kleur wordt geweigerd")
    void moveWithoutSquaresOrColorIsRefused() {
        Position position = new Position(5, 2);
        assertThrows(IllegalArgumentException.class,
                () -> new Move(null, position, PieceColor.WHITE));
        assertThrows(IllegalArgumentException.class,
                () -> new Move(position, null, PieceColor.WHITE));
        assertThrows(IllegalArgumentException.class,
                () -> new Move(position, position, null));
    }
}
