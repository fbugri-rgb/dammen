package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de spelregels: wie mag wanneer spelen, welke zetten zijn toegelaten,
 * wanneer is slaan verplicht, wanneer wordt een schijf een dam en wanneer is het
 * spel gewonnen.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class DraughtsGameRulesTest {

    private DraughtsGame game;

    @BeforeEach
    void setUp() {
        game = new DraughtsGame("Wit", "Zwart");
    }

    /**
     * Maakt het bord leeg zodat een test zelf een stelling kan opzetten.
     */
    private void clearBoard() {
        game.getBoard().clear();
    }

    private void put(int row, int column, PieceColor color) {
        game.getBoard().setPiece(new Position(row, column), new Piece(color));
    }

    private void putKing(int row, int column, PieceColor color) {
        game.getBoard().setPiece(new Position(row, column), new Piece(color, PieceType.KING));
    }

    private Move move(int fromRow, int fromColumn, int toRow, int toColumn) {
        return game.movePiece(new Position(fromRow, fromColumn), new Position(toRow, toColumn));
    }

    @Test
    @DisplayName("Wit begint en er hoeft nog niet geslagen te worden")
    void whiteStarts() {
        assertEquals(PieceColor.WHITE, game.getCurrentColor());
        assertEquals("Wit", game.getCurrentPlayer().getName());
        assertEquals(GameStatus.PLAYING, game.getStatus());
        assertFalse(game.mustCapture());
        assertEquals(0, game.getMoveCount());
    }

    @Test
    @DisplayName("Een schijf vooraan heeft twee zetten, een schijf achteraan geen enkele")
    void numberOfMovesAtTheStart() {
        assertEquals(2, game.getValidMovesFrom(new Position(6, 3)).size());
        assertTrue(game.getValidMovesFrom(new Position(9, 0)).isEmpty(),
                "een schijf op de achterste rij zit ingesloten");
    }

    @Test
    @DisplayName("Een gewone zet wordt uitgevoerd en de beurt gaat over")
    void simpleMoveIsExecutedAndTurnChanges() {
        Move played = move(6, 3, 5, 2);

        assertTrue(game.getBoard().isEmpty(new Position(6, 3)));
        assertNotNull(game.getBoard().getPiece(new Position(5, 2)));
        assertEquals(PieceColor.BLACK, game.getCurrentColor());
        assertEquals(1, game.getMoveCount());
        assertEquals("Wit 32-27", played.toString());
        assertFalse(played.isCapture());
    }

    @Test
    @DisplayName("Achteruit spelen met een gewone schijf mag niet")
    void manCannotMoveBackwards() {
        assertThrows(DraughtsException.class, () -> move(6, 3, 7, 2));
    }

    @Test
    @DisplayName("Je mag geen schijf van de tegenstander verzetten")
    void playerCannotMoveOpponentPiece() {
        DraughtsException exception = assertThrows(DraughtsException.class, () -> move(3, 2, 4, 1));
        assertTrue(exception.getMessage().contains("beurt"),
                "de boodschap moet uitleggen wie aan zet is");
    }

    @Test
    @DisplayName("Van een leeg veld kan je niet spelen")
    void cannotMoveFromEmptySquare() {
        assertThrows(DraughtsException.class, () -> move(4, 1, 3, 0));
    }

    @Test
    @DisplayName("Een schijf mag niet op een bezet veld landen")
    void cannotMoveOntoAnotherPiece() {
        clearBoard();
        put(6, 3, PieceColor.WHITE);
        put(5, 2, PieceColor.WHITE);
        assertThrows(DraughtsException.class, () -> move(6, 3, 5, 2));
    }

    @Test
    @DisplayName("Slaan is verplicht: een gewone zet wordt dan geweigerd")
    void capturingIsMandatory() {
        clearBoard();
        put(5, 2, PieceColor.WHITE);
        put(4, 3, PieceColor.BLACK);
        put(6, 7, PieceColor.WHITE);
        put(0, 1, PieceColor.BLACK);

        assertTrue(game.mustCapture());
        assertTrue(game.getValidMovesFrom(new Position(6, 7)).isEmpty(),
                "met een andere schijf spelen mag niet als er geslagen kan worden");
        DraughtsException exception = assertThrows(DraughtsException.class, () -> move(6, 7, 5, 6));
        assertTrue(exception.getMessage().toLowerCase().contains("slaan"));
    }

    @Test
    @DisplayName("Bij het slaan verdwijnt de geslagen schijf van het bord")
    void capturedPieceIsRemoved() {
        clearBoard();
        put(5, 2, PieceColor.WHITE);
        put(4, 3, PieceColor.BLACK);
        put(0, 1, PieceColor.BLACK);

        Move played = move(5, 2, 3, 4);

        assertTrue(played.isCapture());
        assertTrue(game.getBoard().isEmpty(new Position(4, 3)));
        assertNotNull(game.getBoard().getPiece(new Position(3, 4)));
        assertEquals(PieceColor.BLACK, game.getCurrentColor());
    }

    @Test
    @DisplayName("Wie nog eens kan slaan, moet doorslaan met dezelfde schijf")
    void playerMustContinueCapturing() {
        clearBoard();
        put(7, 2, PieceColor.WHITE);
        put(6, 3, PieceColor.BLACK);
        put(4, 3, PieceColor.BLACK);
        put(9, 8, PieceColor.BLACK);

        move(7, 2, 5, 4);

        assertEquals(PieceColor.WHITE, game.getCurrentColor(), "wit blijft aan zet");
        assertNotNull(game.getChainPosition());
        assertTrue(game.mustCapture());
        assertThrows(DraughtsException.class, () -> move(9, 8, 8, 7),
                "de tegenstander mag niet tussendoor spelen");

        move(5, 4, 3, 2);

        assertEquals(1, game.getBoard().countPieces(PieceColor.BLACK), "beide schijven geslagen");
        assertNull(game.getChainPosition());
        assertEquals(PieceColor.BLACK, game.getCurrentColor());
    }

    @Test
    @DisplayName("Tijdens een slagreeks moet je met dezelfde schijf verder")
    void mustContinueWithTheSamePiece() {
        clearBoard();
        put(7, 2, PieceColor.WHITE);
        put(6, 3, PieceColor.BLACK);
        put(4, 3, PieceColor.BLACK);
        put(9, 0, PieceColor.WHITE);

        move(7, 2, 5, 4);

        DraughtsException exception = assertThrows(DraughtsException.class, () -> move(9, 0, 8, 1));
        assertTrue(exception.getMessage().contains("dezelfde schijf"));
    }

    @Test
    @DisplayName("Een schijf die de overkant haalt, wordt een dam")
    void manBecomesKingOnTheLastRow() {
        clearBoard();
        put(1, 2, PieceColor.WHITE);
        put(5, 0, PieceColor.BLACK);

        Move played = move(1, 2, 0, 1);

        assertTrue(played.isPromotion());
        assertTrue(game.getBoard().getPiece(new Position(0, 1)).isKing());
    }

    @Test
    @DisplayName("Een dam mag over meerdere velden en ook achteruit")
    void kingMovesFarAndBackwards() {
        clearBoard();
        putKing(0, 1, PieceColor.WHITE);
        put(6, 1, PieceColor.BLACK);

        List<Move> kingMoves = game.getValidMovesFrom(new Position(0, 1));
        assertTrue(kingMoves.size() > 2, "een dam heeft meer dan twee zetten");

        move(0, 1, 4, 5);
        assertNotNull(game.getBoard().getPiece(new Position(4, 5)));

        move(6, 1, 7, 0);
        move(4, 5, 2, 3);
        assertNotNull(game.getBoard().getPiece(new Position(2, 3)), "de dam mag achteruit");
    }

    @Test
    @DisplayName("Een dam slaat op afstand en mag achter de schijf landen waar ze wil")
    void kingCapturesFromADistance() {
        clearBoard();
        putKing(9, 0, PieceColor.WHITE);
        put(5, 4, PieceColor.BLACK);
        put(0, 9, PieceColor.BLACK);

        assertTrue(game.mustCapture());
        assertEquals(4, game.getValidMovesFrom(new Position(9, 0)).size(),
                "achter de geslagen schijf liggen vier lege velden");

        move(9, 0, 2, 7);

        assertTrue(game.getBoard().isEmpty(new Position(5, 4)));
        assertNotNull(game.getBoard().getPiece(new Position(2, 7)));
    }

    @Test
    @DisplayName("Wie geen schijven meer heeft, verliest")
    void playerWithoutPiecesLoses() {
        clearBoard();
        put(5, 2, PieceColor.WHITE);
        put(4, 3, PieceColor.BLACK);

        move(5, 2, 3, 4);

        assertEquals(GameStatus.WHITE_WON, game.getStatus());
        assertTrue(game.getStatus().isFinished());
        assertEquals("Wit", game.getWinner().getName());
    }

    @Test
    @DisplayName("Wie niet meer kan spelen, verliest")
    void playerWithoutMovesLoses() {
        clearBoard();
        put(5, 2, PieceColor.WHITE);
        put(4, 3, PieceColor.BLACK);
        // deze twee zwarte schijven staan op de onderste rij en kunnen niet meer vooruit
        put(9, 0, PieceColor.BLACK);
        put(9, 2, PieceColor.BLACK);

        move(5, 2, 3, 4);

        assertEquals(GameStatus.WHITE_WON, game.getStatus());
    }

    @Test
    @DisplayName("Na het einde van het spel kan er niet meer gespeeld worden")
    void noMovesAfterTheGameHasFinished() {
        clearBoard();
        put(5, 2, PieceColor.WHITE);
        put(4, 3, PieceColor.BLACK);
        move(5, 2, 3, 4);

        DraughtsException exception = assertThrows(DraughtsException.class, () -> move(3, 4, 2, 3));
        assertTrue(exception.getMessage().contains("afgelopen"));
    }

    @Test
    @DisplayName("Alle zetten van beide spelers worden bijgehouden")
    void allMovesAreRemembered() {
        move(6, 3, 5, 2);
        move(3, 2, 4, 1);
        move(6, 5, 5, 4);

        List<Move> history = game.getMoveHistory();
        assertEquals(3, history.size());
        assertEquals(PieceColor.WHITE, history.get(0).getColor());
        assertEquals(PieceColor.BLACK, history.get(1).getColor());
        assertEquals(PieceColor.WHITE, history.get(2).getColor());
    }

    @Test
    @DisplayName("Het aantal geslagen schijven wordt geteld")
    void capturedPiecesAreCounted() {
        assertEquals(0, game.getCapturedCount(PieceColor.WHITE));
        assertEquals(0, game.getCapturedCount(PieceColor.BLACK));

        game.getBoard().removePiece(new Position(0, 1));
        assertEquals(1, game.getCapturedCount(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Een nieuw spel zet alles terug op nul")
    void newGameResetsEverything() {
        move(6, 3, 5, 2);
        move(3, 2, 4, 1);

        game.startNewGame();

        assertEquals(0, game.getMoveCount());
        assertEquals(PieceColor.WHITE, game.getCurrentColor());
        assertEquals(GameStatus.PLAYING, game.getStatus());
        assertEquals(20, game.getBoard().countPieces(PieceColor.WHITE));
        assertFalse(game.canUndo());
    }

    /**
     * De twee dammen schuiven hier heen en weer in een vast rondje. Ze staan zo dat ze
     * elkaar nooit kunnen slaan: telkens ligt het veld achter de andere dam buiten het
     * bord. Zo kunnen we het spel echt tot een remise laten komen.
     */
    @Test
    @DisplayName("Lang schuiven met dammen zonder te slaan eindigt in remise")
    void shufflingWithKingsEndsInADraw() {
        clearBoard();
        putKing(0, 1, PieceColor.WHITE);
        putKing(0, 3, PieceColor.BLACK);

        int playedMoves = 0;
        while (!game.getStatus().isFinished() && playedMoves < 60) {
            move(0, 1, 1, 0);
            move(0, 3, 1, 2);
            if (game.getStatus().isFinished()) {
                break;
            }
            move(1, 0, 0, 1);
            move(1, 2, 0, 3);
            playedMoves += 4;
        }

        assertEquals(GameStatus.DRAW, game.getStatus());
        assertNull(game.getWinner(), "bij een remise is er geen winnaar");
        assertTrue(game.getStatus().isFinished());
    }

    @Test
    @DisplayName("Slaan of schuiven met een gewone schijf brengt geen remise")
    void normalPlayDoesNotEndInADraw() {
        move(6, 3, 5, 2);
        move(3, 2, 4, 1);
        move(6, 5, 5, 4);

        assertEquals(GameStatus.PLAYING, game.getStatus());
    }

    @Test
    @DisplayName("Een speler zonder naam wordt geweigerd")
    void playerWithoutNameIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new DraughtsGame("", "Zwart"));
        assertThrows(IllegalArgumentException.class, () -> new DraughtsGame("Wit", "   "));
        assertThrows(IllegalArgumentException.class, () -> new DraughtsGame(null, "Zwart"));
    }

    @Test
    @DisplayName("Een naam die te lang is, wordt geweigerd")
    void veryLongNameIsRefused() {
        String tooLong = "Een naam die veel te lang is voor het scherm";
        assertThrows(IllegalArgumentException.class, () -> new DraughtsGame(tooLong, "Zwart"));
    }
}
