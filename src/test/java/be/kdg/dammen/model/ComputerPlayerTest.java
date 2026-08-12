package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de computertegenstander: speelt hij een geldige zet, kiest hij de beste
 * slagzet en loopt hij niet zomaar in de val?
 *
 * @author Farok Bugri
 * @version 1.0
 */
class ComputerPlayerTest {

    private DraughtsGame game;
    private ComputerPlayer computer;

    @BeforeEach
    void setUp() {
        game = new DraughtsGame("Mens", "Computer");
        game.setAgainstComputer(true);
        computer = new ComputerPlayer(PieceColor.BLACK);
    }

    private void put(int row, int column, PieceColor color) {
        game.getBoard().setPiece(new Position(row, column), new Piece(color));
    }

    private void move(int fromRow, int fromColumn, int toRow, int toColumn) {
        game.movePiece(new Position(fromRow, fromColumn), new Position(toRow, toColumn));
    }

    @Test
    @DisplayName("De computer speelt de zwarte schijven")
    void computerPlaysBlack() {
        assertEquals(PieceColor.BLACK, computer.getColor());
        assertEquals(PieceColor.BLACK, DraughtsGame.COMPUTER_COLOR);
    }

    @Test
    @DisplayName("De computer is pas aan zet nadat de speler gespeeld heeft")
    void computerIsOnlyToMoveAfterThePlayer() {
        assertFalse(game.isComputerToMove(), "wit begint, dus de computer wacht");
        move(6, 3, 5, 2);
        assertTrue(game.isComputerToMove());
    }

    @Test
    @DisplayName("De computer kiest een zet die volgens de regels mag")
    void computerChoosesALegalMove() {
        move(6, 3, 5, 2);

        Move chosen = computer.chooseMove(game);

        assertNotNull(chosen);
        assertEquals(PieceColor.BLACK, chosen.getColor());
        // het model weigert een zet die niet mag, dus als deze zet lukt was hij geldig
        assertDoesNotThrow(() -> game.movePiece(chosen.getFrom(), chosen.getTo()),
                "de computer koos een zet die niet toegelaten is");
        assertEquals(2, game.getMoveCount());
    }

    @Test
    @DisplayName("De computer kiest de slagzet waarbij hij zelf veilig staat")
    void computerChoosesTheSafeCapture() {
        game.getBoard().clear();
        // zwart op 4,3 kan twee kanten op slaan:
        //  - naar 6,1 : daar slaat wit op 7,0 meteen terug
        //  - naar 6,5 : daar staat zwart veilig
        put(4, 3, PieceColor.BLACK);
        put(3, 2, PieceColor.BLACK);
        put(3, 4, PieceColor.BLACK);
        put(5, 2, PieceColor.WHITE);
        put(5, 4, PieceColor.WHITE);
        put(7, 0, PieceColor.WHITE);
        put(9, 8, PieceColor.WHITE);
        move(9, 8, 8, 7);

        Move chosen = computer.chooseMove(game);

        assertTrue(chosen.isCapture());
        assertEquals(new Position(6, 5), chosen.getTo(), "de veilige slagzet is de beste");
    }

    @Test
    @DisplayName("De computer zet zijn schijf niet zomaar voor een slagzet")
    void computerDoesNotWalkIntoACapture() {
        game.getBoard().clear();
        // zwart kan naar 5,2 (wordt dan geslagen) of veilig ergens anders naartoe
        put(4, 1, PieceColor.BLACK);
        put(4, 5, PieceColor.BLACK);
        put(6, 3, PieceColor.WHITE);
        put(9, 8, PieceColor.WHITE);
        move(9, 8, 8, 7);

        Move chosen = computer.chooseMove(game);

        assertNotEquals(new Position(5, 2), chosen.getTo(),
                "op veld 5,2 zou de schijf meteen geslagen worden");
    }

    @Test
    @DisplayName("De computer slaat als slaan verplicht is")
    void computerCapturesWhenItMust() {
        game.getBoard().clear();
        put(4, 3, PieceColor.BLACK);
        put(5, 4, PieceColor.WHITE);
        // de zwarte schijf op 3,2 blokkeert het veld achter 4,3, zodat wit zelf
        // niet mag slaan en dus een gewone zet kan doen
        put(3, 2, PieceColor.BLACK);
        put(9, 8, PieceColor.WHITE);
        move(9, 8, 8, 7);

        assertTrue(game.mustCapture(), "zwart moet nu slaan");
        Move chosen = computer.chooseMove(game);

        assertTrue(chosen.isCapture());
        assertEquals(new Position(6, 5), chosen.getTo());
    }

    @Test
    @DisplayName("De computer weigert te spelen als hij niet aan zet is")
    void computerRefusesWhenItIsNotItsTurn() {
        DraughtsException exception = assertThrows(DraughtsException.class,
                () -> computer.chooseMove(game));
        assertTrue(exception.getMessage().contains("niet aan zet"));
    }

    @Test
    @DisplayName("De computer heeft een spel nodig om over na te denken")
    void computerNeedsAGame() {
        assertThrows(IllegalArgumentException.class, () -> computer.chooseMove(null));
        assertThrows(IllegalArgumentException.class, () -> new ComputerPlayer(null));
    }

    @Test
    @DisplayName("De computer denkt niet te lang na")
    void computerThinksFastEnough() {
        move(6, 3, 5, 2);

        long start = System.currentTimeMillis();
        computer.chooseMove(game);
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 5000, "de computer deed er " + duration + " ms over");
    }

    /**
     * De computer speelt hier tegen zichzelf. Zo komen er heel wat stellingen voorbij
     * en zien we of er ergens een zet gekozen wordt die niet mag. We spelen maar een
     * twintigtal zetten, anders duurt deze test te lang.
     */
    @Test
    @DisplayName("De computer speelt tegen zichzelf zonder ooit een foute zet te kiezen")
    void computerPlaysAgainstItselfWithoutErrors() {
        ComputerPlayer white = new ComputerPlayer(PieceColor.WHITE);
        int playedMoves = 0;
        while (!game.getStatus().isFinished() && playedMoves < 20) {
            ComputerPlayer current = white;
            if (game.getCurrentColor() == PieceColor.BLACK) {
                current = computer;
            }
            Move chosen = current.chooseMove(game);
            game.movePiece(chosen.getFrom(), chosen.getTo());
            playedMoves++;
        }
        assertEquals(20, game.getMoveCount(), "er zijn twintig zetten gespeeld");
        assertEquals(GameStatus.PLAYING, game.getStatus());
    }
}
