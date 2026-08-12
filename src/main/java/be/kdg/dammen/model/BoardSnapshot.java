package be.kdg.dammen.model;

/**
 * Een foto van de spelsituatie vlak voor een zet: het bord, wie aan zet was, of er een
 * slagreeks bezig was en de stand van het spel. Met deze foto's kunnen we zetten
 * terugnemen ("undo").
 *
 * @author Farok Bugri
 * @version 1.0
 */
class BoardSnapshot {

    private final Board board;
    private final PieceColor colorToMove;
    private final Position chainPosition;
    private final GameStatus status;
    private final int quietMoveCount;

    BoardSnapshot(Board board, PieceColor colorToMove, Position chainPosition,
                  GameStatus status, int quietMoveCount) {
        if (board == null || colorToMove == null || status == null) {
            throw new IllegalArgumentException("Een foto van het spel is onvolledig.");
        }
        this.board = new Board(board);
        this.colorToMove = colorToMove;
        this.chainPosition = chainPosition;
        this.status = status;
        this.quietMoveCount = quietMoveCount;
    }

    Board getBoard() {
        return board;
    }

    PieceColor getColorToMove() {
        return colorToMove;
    }

    Position getChainPosition() {
        return chainPosition;
    }

    GameStatus getStatus() {
        return status;
    }

    int getQuietMoveCount() {
        return quietMoveCount;
    }
}
