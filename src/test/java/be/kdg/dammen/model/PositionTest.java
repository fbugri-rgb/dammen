package be.kdg.dammen.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de klasse Position: de veldnummers en het opschuiven over het bord.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class PositionTest {

    @Test
    @DisplayName("Een positie buiten het bord wordt geweigerd")
    void positionOutsideBoardIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new Position(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Position(10, 10));
    }

    @Test
    @DisplayName("De donkere velden zijn genummerd van 1 tot en met 50")
    void squareNumbersAreCorrect() {
        assertEquals(1, new Position(0, 1).getSquareNumber());
        assertEquals(5, new Position(0, 9).getSquareNumber());
        assertEquals(6, new Position(1, 0).getSquareNumber());
        assertEquals(32, new Position(6, 3).getSquareNumber());
        assertEquals(50, new Position(9, 8).getSquareNumber());
    }

    @Test
    @DisplayName("Opschuiven binnen het bord geeft de nieuwe positie")
    void moveInsideBoardGivesNewPosition() {
        Position start = new Position(5, 4);
        Position result = start.move(-1, 1);
        assertEquals(4, result.getRow());
        assertEquals(5, result.getColumn());
        assertEquals(5, start.getRow(), "de oorspronkelijke positie mag niet wijzigen");
    }

    @Test
    @DisplayName("Opschuiven buiten het bord geeft niets terug")
    void moveOutsideBoardGivesNull() {
        assertNull(new Position(0, 1).move(-1, 0));
        assertNull(new Position(9, 8).move(1, 0));
        assertNull(new Position(0, 9).move(0, 1));
    }

    @Test
    @DisplayName("Twee posities met dezelfde rij en kolom zijn gelijk")
    void positionsWithSameRowAndColumnAreEqual() {
        assertEquals(new Position(3, 4), new Position(3, 4));
        assertEquals(new Position(3, 4).hashCode(), new Position(3, 4).hashCode());
        assertNotEquals(new Position(3, 4), new Position(4, 3));
    }

    @Test
    @DisplayName("isInsideBoard kijkt na of een rij en kolom op het bord liggen")
    void isInsideBoardChecksRowAndColumn() {
        assertTrue(Position.isInsideBoard(0, 0));
        assertTrue(Position.isInsideBoard(9, 9));
        assertFalse(Position.isInsideBoard(10, 0));
        assertFalse(Position.isInsideBoard(0, -1));
    }
}
