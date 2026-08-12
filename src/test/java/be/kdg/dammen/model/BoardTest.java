package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de klasse Board: de beginstelling, de donkere velden en het kopiëren
 * van een bord.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    @DisplayName("Bij het begin staan er 20 witte en 20 zwarte schijven")
    void startingPositionHasTwentyPiecesEach() {
        assertEquals(20, board.countPieces(PieceColor.WHITE));
        assertEquals(20, board.countPieces(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Zwart staat bovenaan, wit onderaan, en de middelste rijen zijn leeg")
    void piecesStandOnTheCorrectRows() {
        assertEquals(PieceColor.BLACK, board.getPiece(new Position(0, 1)).getColor());
        assertEquals(PieceColor.BLACK, board.getPiece(new Position(3, 0)).getColor());
        assertEquals(PieceColor.WHITE, board.getPiece(new Position(6, 1)).getColor());
        assertEquals(PieceColor.WHITE, board.getPiece(new Position(9, 0)).getColor());
        assertTrue(board.isEmpty(new Position(4, 1)));
        assertTrue(board.isEmpty(new Position(5, 0)));
    }

    @Test
    @DisplayName("Er staan enkel schijven op de donkere velden")
    void piecesOnlyStandOnDarkSquares() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                if (!Board.isDarkSquare(row, column)) {
                    assertTrue(board.isEmpty(new Position(row, column)),
                            "op een licht veld mag geen schijf staan");
                }
            }
        }
    }

    @Test
    @DisplayName("Alle schijven beginnen als gewone schijf")
    void allPiecesStartAsMan() {
        assertFalse(board.getPiece(new Position(0, 1)).isKing());
        assertEquals(PieceType.MAN, board.getPiece(new Position(9, 0)).getType());
    }

    @Test
    @DisplayName("De donkere velden liggen waar rij plus kolom oneven is")
    void darkSquaresAreWhereRowPlusColumnIsOdd() {
        assertTrue(Board.isDarkSquare(0, 1));
        assertTrue(Board.isDarkSquare(1, 0));
        assertFalse(Board.isDarkSquare(0, 0));
        assertFalse(Board.isDarkSquare(1, 1));
    }

    @Test
    @DisplayName("De damlijn van wit is de bovenste rij, die van zwart de onderste")
    void promotionRowsAreOnTheOpponentSide() {
        assertEquals(0, Board.getPromotionRow(PieceColor.WHITE));
        assertEquals(Board.SIZE - 1, Board.getPromotionRow(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Een schijf wegnemen maakt het veld leeg")
    void removingAPieceEmptiesTheSquare() {
        Position position = new Position(6, 1);
        Piece removed = board.removePiece(position);
        assertNotNull(removed);
        assertTrue(board.isEmpty(position));
        assertEquals(19, board.countPieces(PieceColor.WHITE));
    }

    @Test
    @DisplayName("Het bord leegmaken haalt alle schijven weg")
    void clearRemovesEverything() {
        board.clear();
        assertEquals(0, board.countPieces(PieceColor.WHITE));
        assertEquals(0, board.countPieces(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Het bord opnieuw klaarzetten geeft weer de beginstelling")
    void resetGivesTheStartingPositionBack() {
        board.clear();
        board.reset();
        assertEquals(20, board.countPieces(PieceColor.WHITE));
        assertEquals(20, board.countPieces(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Een kopie van een bord staat volledig los van het origineel")
    void copyOfBoardIsIndependent() {
        Board copy = new Board(board);
        copy.removePiece(new Position(6, 1));
        copy.getPiece(new Position(6, 3)).promoteToKing();

        assertEquals(20, board.countPieces(PieceColor.WHITE), "het origineel mag niet wijzigen");
        assertEquals(19, copy.countPieces(PieceColor.WHITE));
        assertFalse(board.getPiece(new Position(6, 3)).isKing(),
                "een dam in de kopie mag geen dam maken in het origineel");
    }

    @Test
    @DisplayName("Een leeg veld opvragen geeft niets terug")
    void emptySquareGivesNull() {
        assertNull(board.getPiece(new Position(4, 1)));
    }

    @Test
    @DisplayName("Een positie die niet ingevuld is, wordt geweigerd")
    void missingPositionIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> board.getPiece(null));
        assertThrows(IllegalArgumentException.class, () -> new Board(null));
    }
}
