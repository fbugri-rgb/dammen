package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor het terugnemen van zetten (de undo-uitbreiding).
 *
 * @author Farok Bugri
 * @version 1.0
 */
class DraughtsGameUndoTest {

    private DraughtsGame game;

    @BeforeEach
    void setUp() {
        game = new DraughtsGame("Wit", "Zwart");
    }

    private void move(int fromRow, int fromColumn, int toRow, int toColumn) {
        game.movePiece(new Position(fromRow, fromColumn), new Position(toRow, toColumn));
    }

    @Test
    @DisplayName("Bij het begin valt er niets terug te nemen")
    void nothingToUndoAtTheStart() {
        assertFalse(game.canUndo());
        assertEquals(0, game.getUndoableMoveCount());
        assertThrows(DraughtsException.class, () -> game.undoMoves(1));
    }

    @Test
    @DisplayName("Eén zet terugnemen zet het bord en de beurt terug")
    void undoOneMove() {
        move(6, 3, 5, 2);

        assertTrue(game.canUndo());
        game.undoMoves(1);

        assertEquals(0, game.getMoveCount());
        assertNotNull(game.getBoard().getPiece(new Position(6, 3)), "de schijf staat terug");
        assertTrue(game.getBoard().isEmpty(new Position(5, 2)));
        assertEquals(PieceColor.WHITE, game.getCurrentColor());
    }

    @Test
    @DisplayName("Meerdere zetten in één keer terugnemen")
    void undoSeveralMovesAtOnce() {
        move(6, 3, 5, 2);
        move(3, 2, 4, 1);
        move(6, 5, 5, 4);
        assertEquals(3, game.getUndoableMoveCount());

        game.undoMoves(3);

        assertEquals(0, game.getMoveCount());
        assertEquals(20, game.getBoard().countPieces(PieceColor.WHITE));
        assertEquals(20, game.getBoard().countPieces(PieceColor.BLACK));
        assertEquals(PieceColor.WHITE, game.getCurrentColor());
    }

    @Test
    @DisplayName("Een geslagen schijf komt terug op het bord")
    void undoBringsCapturedPieceBack() {
        game.getBoard().clear();
        game.getBoard().setPiece(new Position(5, 2), new Piece(PieceColor.WHITE));
        game.getBoard().setPiece(new Position(4, 3), new Piece(PieceColor.BLACK));
        game.getBoard().setPiece(new Position(0, 1), new Piece(PieceColor.BLACK));

        move(5, 2, 3, 4);
        assertEquals(1, game.getBoard().countPieces(PieceColor.BLACK));

        game.undoMoves(1);

        assertEquals(2, game.getBoard().countPieces(PieceColor.BLACK));
        assertNotNull(game.getBoard().getPiece(new Position(4, 3)));
        assertNotNull(game.getBoard().getPiece(new Position(5, 2)));
    }

    @Test
    @DisplayName("Een dam die teruggenomen wordt, is weer een gewone schijf")
    void undoTakesTheKingBack() {
        game.getBoard().clear();
        game.getBoard().setPiece(new Position(1, 2), new Piece(PieceColor.WHITE));
        game.getBoard().setPiece(new Position(5, 0), new Piece(PieceColor.BLACK));

        move(1, 2, 0, 1);
        assertTrue(game.getBoard().getPiece(new Position(0, 1)).isKing());

        game.undoMoves(1);

        assertFalse(game.getBoard().getPiece(new Position(1, 2)).isKing(),
                "de schijf mag geen dam meer zijn");
    }

    @Test
    @DisplayName("Een hele slagreeks kan teruggenomen worden")
    void undoWholeCaptureChain() {
        game.getBoard().clear();
        game.getBoard().setPiece(new Position(7, 2), new Piece(PieceColor.WHITE));
        game.getBoard().setPiece(new Position(6, 3), new Piece(PieceColor.BLACK));
        game.getBoard().setPiece(new Position(4, 3), new Piece(PieceColor.BLACK));
        game.getBoard().setPiece(new Position(9, 8), new Piece(PieceColor.BLACK));

        move(7, 2, 5, 4);
        move(5, 4, 3, 2);
        assertEquals(1, game.getBoard().countPieces(PieceColor.BLACK));

        game.undoMoves(2);

        assertEquals(3, game.getBoard().countPieces(PieceColor.BLACK));
        assertNotNull(game.getBoard().getPiece(new Position(7, 2)));
        assertEquals(PieceColor.WHITE, game.getCurrentColor());
    }

    @Test
    @DisplayName("Meer zetten terugnemen dan er gespeeld zijn, wordt geweigerd")
    void undoingTooManyMovesIsRefused() {
        move(6, 3, 5, 2);

        DraughtsException exception = assertThrows(DraughtsException.class, () -> game.undoMoves(5));
        assertTrue(exception.getMessage().contains("hoogstens"));
        assertEquals(1, game.getMoveCount(), "het spel mag niet gewijzigd zijn");
    }

    @Test
    @DisplayName("Nul of minder zetten terugnemen heeft geen zin")
    void undoingZeroMovesIsRefused() {
        move(6, 3, 5, 2);
        assertThrows(IllegalArgumentException.class, () -> game.undoMoves(0));
        assertThrows(IllegalArgumentException.class, () -> game.undoMoves(-2));
    }

    @Test
    @DisplayName("Na het terugnemen kan je gewoon verder spelen")
    void playingContinuesAfterUndo() {
        move(6, 3, 5, 2);
        game.undoMoves(1);

        move(6, 3, 5, 4);

        assertEquals(1, game.getMoveCount());
        assertNotNull(game.getBoard().getPiece(new Position(5, 4)));
    }
}
