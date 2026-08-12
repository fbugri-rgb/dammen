package be.kdg.dammen.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Het volledige damspel. Deze klasse houdt het bord, de twee spelers, de speler die
 * aan zet is en alle gespeelde zetten bij. Ze bewaakt ook de spelregels: een zet die
 * niet mag, wordt geweigerd met een DraughtsException.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class DraughtsGame {

    public static final int PIECES_PER_PLAYER = 20;
    public static final PieceColor COMPUTER_COLOR = PieceColor.BLACK;

    /**
     * Worden er 50 halve zetten na elkaar enkel met dammen gespeeld, zonder dat er
     * geslagen wordt, dan is het spel remise. Zo blijft een eindspel niet eindeloos
     * doorgaan.
     */
    private static final int MAX_QUIET_MOVES = 50;

    private final Board board;
    private final MoveGenerator moveGenerator;
    private final List<Move> moveHistory;
    private final List<BoardSnapshot> snapshots;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private PieceColor currentColor;
    private GameStatus status;
    private Position chainPosition;
    private boolean againstComputer;
    private int quietMoveCount;

    public DraughtsGame(String whitePlayerName, String blackPlayerName) {
        this.board = new Board();
        this.moveGenerator = new MoveGenerator(board);
        this.moveHistory = new ArrayList<>();
        this.snapshots = new ArrayList<>();
        this.whitePlayer = new Player(whitePlayerName, PieceColor.WHITE);
        this.blackPlayer = new Player(blackPlayerName, PieceColor.BLACK);
        this.currentColor = PieceColor.WHITE;
        this.status = GameStatus.PLAYING;
        this.chainPosition = null;
        this.againstComputer = false;
        this.quietMoveCount = 0;
    }

    /**
     * Maakt een kopie van dit spel om zetten op uit te proberen. De kopie houdt geen
     * geschiedenis bij: ze dient enkel om de computer te laten vooruitrekenen.
     */
    private DraughtsGame(DraughtsGame other) {
        this.board = new Board(other.board);
        this.moveGenerator = new MoveGenerator(board);
        this.moveHistory = new ArrayList<>();
        this.snapshots = new ArrayList<>();
        this.whitePlayer = other.whitePlayer;
        this.blackPlayer = other.blackPlayer;
        this.currentColor = other.currentColor;
        this.status = other.status;
        this.chainPosition = other.chainPosition;
        this.againstComputer = other.againstComputer;
        this.quietMoveCount = other.quietMoveCount;
    }

    /**
     * @return een kopie waarop de computer zetten kan uitproberen zonder het echte
     * spel te wijzigen
     */
    DraughtsGame copyForThinking() {
        return new DraughtsGame(this);
    }

    /**
     * Zet alles terug klaar voor een nieuw spel met dezelfde spelers.
     */
    public void startNewGame() {
        board.reset();
        moveHistory.clear();
        snapshots.clear();
        currentColor = PieceColor.WHITE;
        status = GameStatus.PLAYING;
        chainPosition = null;
        quietMoveCount = 0;
    }

    /**
     * Legt vast of de zwarte schijven door de computer gespeeld worden.
     */
    public void setAgainstComputer(boolean againstComputer) {
        this.againstComputer = againstComputer;
    }

    public boolean isAgainstComputer() {
        return againstComputer;
    }

    /**
     * @return true als de computer nu aan zet is
     */
    public boolean isComputerToMove() {
        return againstComputer && currentColor == COMPUTER_COLOR && !status.isFinished();
    }

    /**
     * Voert een zet uit van het ene veld naar het andere. De zet wordt eerst getoetst
     * aan de spelregels.
     *
     * @return de zet die uitgevoerd werd
     * @throws DraughtsException als de zet niet toegelaten is
     */
    public Move movePiece(Position from, Position to) {
        checkGameIsRunning();
        checkPieceOfPlayerAtStart(from);
        Move move = findValidMove(from, to);
        executeMove(move);
        finishTurn(move);
        return move;
    }

    private void checkGameIsRunning() {
        if (status.isFinished()) {
            throw new DraughtsException("Het spel is afgelopen. Start een nieuw spel om verder te spelen.");
        }
    }

    private void checkPieceOfPlayerAtStart(Position from) {
        Piece piece = board.getPiece(from);
        if (piece == null) {
            throw new DraughtsException("Op dat veld ligt geen schijf.");
        }
        if (piece.getColor() != currentColor) {
            throw new DraughtsException("Het is de beurt aan " + getCurrentPlayer().getName()
                    + " met de " + currentColor.getDutchName().toLowerCase() + "te schijven.");
        }
        if (chainPosition != null && !chainPosition.equals(from)) {
            throw new DraughtsException("Je moet verder slaan met dezelfde schijf.");
        }
    }

    /**
     * Zoekt de gevraagde zet tussen de zetten die nu toegelaten zijn.
     *
     * @throws DraughtsException als de zet er niet bij zit
     */
    private Move findValidMove(Position from, Position to) {
        List<Move> validMoves = getValidMovesForCurrentPlayer();
        for (int i = 0; i < validMoves.size(); i++) {
            Move move = validMoves.get(i);
            if (move.hasSameSquares(from, to)) {
                return move;
            }
        }
        if (mustCapture()) {
            throw new DraughtsException("Slaan is verplicht: je moet een schijf van de tegenstander slaan.");
        }
        throw new DraughtsException("Die zet is niet toegelaten volgens de spelregels.");
    }

    private void executeMove(Move move) {
        snapshots.add(new BoardSnapshot(board, currentColor, chainPosition, status, quietMoveCount));
        Piece piece = board.removePiece(move.getFrom());
        boolean pieceWasKing = piece.isKing();
        if (move.isCapture()) {
            board.removePiece(move.getCapturedPosition());
        }
        board.setPiece(move.getTo(), piece);
        if (move.isPromotion()) {
            piece.promoteToKing();
        }
        countQuietMove(move, pieceWasKing);
        moveHistory.add(move);
    }

    /**
     * Kan er doorgeslagen worden met dezelfde schijf, dan blijft de speler aan zet.
     * Anders is de tegenstander aan de beurt en kijken we na of het spel gedaan is.
     */
    private void finishTurn(Move move) {
        if (canContinueCapture(move)) {
            chainPosition = move.getTo();
            return;
        }
        chainPosition = null;
        currentColor = currentColor.getOpponent();
        updateStatus();
    }

    /**
     * Wie een dam haalt tijdens een slagreeks, stopt daar zijn beurt.
     */
    private boolean canContinueCapture(Move move) {
        if (!move.isCapture() || move.isPromotion()) {
            return false;
        }
        return !moveGenerator.getCaptureMovesFrom(move.getTo()).isEmpty();
    }

    /**
     * De speler die aan zet is en niet meer kan spelen, verliest. Gebeurt er lange tijd
     * niets meer op het bord, dan eindigt het spel in een remise.
     */
    private void updateStatus() {
        if (board.countPieces(currentColor) == 0 || getValidMovesForCurrentPlayer().isEmpty()) {
            if (currentColor == PieceColor.WHITE) {
                status = GameStatus.BLACK_WON;
            } else {
                status = GameStatus.WHITE_WON;
            }
        } else if (quietMoveCount >= MAX_QUIET_MOVES) {
            status = GameStatus.DRAW;
        }
    }

    /**
     * Houdt bij hoe lang er niets meer gebeurt op het bord. Zodra er geslagen wordt of
     * een gewone schijf verzet wordt, begint de teller opnieuw.
     */
    private void countQuietMove(Move move, boolean movedPieceWasKing) {
        if (move.isCapture() || !movedPieceWasKing) {
            quietMoveCount = 0;
        } else {
            quietMoveCount++;
        }
    }

    /**
     * @return de zetten die de speler die aan zet is nu mag doen
     */
    public List<Move> getValidMovesForCurrentPlayer() {
        if (chainPosition != null) {
            return moveGenerator.getCaptureMovesFrom(chainPosition);
        }
        return moveGenerator.getValidMoves(currentColor);
    }

    /**
     * Neemt een aantal zetten terug. Het bord, de beurt en de stand van het spel
     * worden hersteld zoals ze waren voor die zetten.
     *
     * @param numberOfMoves het aantal zetten dat teruggenomen wordt
     * @throws DraughtsException als er zoveel zetten nog niet gespeeld zijn
     */
    public void undoMoves(int numberOfMoves) {
        if (numberOfMoves < 1) {
            throw new IllegalArgumentException("Neem minstens één zet terug.");
        }
        if (snapshots.isEmpty()) {
            throw new DraughtsException("Er is nog geen zet die je kan terugnemen.");
        }
        if (numberOfMoves > snapshots.size()) {
            throw new DraughtsException("Je kan hoogstens " + snapshots.size()
                    + " zetten terugnemen.");
        }
        restoreSnapshot(snapshots.get(snapshots.size() - numberOfMoves));
        removeLastMoves(numberOfMoves);
    }

    private void restoreSnapshot(BoardSnapshot snapshot) {
        board.copyFrom(snapshot.getBoard());
        currentColor = snapshot.getColorToMove();
        chainPosition = snapshot.getChainPosition();
        status = snapshot.getStatus();
        quietMoveCount = snapshot.getQuietMoveCount();
    }

    private void removeLastMoves(int numberOfMoves) {
        for (int i = 0; i < numberOfMoves; i++) {
            moveHistory.remove(moveHistory.size() - 1);
            snapshots.remove(snapshots.size() - 1);
        }
    }

    /**
     * @return true als er nog een zet teruggenomen kan worden
     */
    public boolean canUndo() {
        return !snapshots.isEmpty();
    }

    /**
     * Van een spel dat uit een bestand geladen werd, kunnen enkel de zetten van deze
     * speelbeurt teruggenomen worden.
     *
     * @return het aantal zetten dat teruggenomen kan worden
     */
    public int getUndoableMoveCount() {
        return snapshots.size();
    }

    /**
     * Wordt gebruikt door het spelscherm om de mogelijke velden aan te duiden.
     *
     * @return de toegelaten zetten die vertrekken vanaf dit veld
     */
    public List<Move> getValidMovesFrom(Position from) {
        List<Move> movesFromPosition = new ArrayList<>();
        List<Move> validMoves = getValidMovesForCurrentPlayer();
        for (int i = 0; i < validMoves.size(); i++) {
            Move move = validMoves.get(i);
            if (move.getFrom().equals(from)) {
                movesFromPosition.add(move);
            }
        }
        return movesFromPosition;
    }

    /**
     * @return true als de speler die aan zet is verplicht is te slaan
     */
    public boolean mustCapture() {
        if (chainPosition != null) {
            return true;
        }
        return !moveGenerator.getAllCaptureMoves(currentColor).isEmpty();
    }

    /**
     * @return het veld waarmee verder geslagen moet worden, of null als er geen
     * slagreeks bezig is
     */
    public Position getChainPosition() {
        return chainPosition;
    }

    /**
     * @return hoeveel schijven van deze kleur er al geslagen werden
     */
    public int getCapturedCount(PieceColor color) {
        return PIECES_PER_PLAYER - board.countPieces(color);
    }

    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentColor() {
        return currentColor;
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentColor);
    }

    public Player getPlayer(PieceColor color) {
        if (color == PieceColor.WHITE) {
            return whitePlayer;
        }
        return blackPlayer;
    }

    /**
     * @return de winnaar, of null zolang het spel bezig is
     */
    public Player getWinner() {
        if (status == GameStatus.WHITE_WON) {
            return whitePlayer;
        }
        if (status == GameStatus.BLACK_WON) {
            return blackPlayer;
        }
        return null;
    }

    public GameStatus getStatus() {
        return status;
    }

    /**
     * @return alle zetten van beide spelers, in volgorde
     */
    public List<Move> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    public int getMoveCount() {
        return moveHistory.size();
    }

    /**
     * Herstelt de beurt en de zetten van een spel dat uit een bestand geladen werd.
     * Enkel de klassen uit het model-package gebruiken deze methode.
     */
    void restoreState(PieceColor colorToMove, List<Move> playedMoves) {
        this.currentColor = colorToMove;
        this.moveHistory.clear();
        this.moveHistory.addAll(playedMoves);
        this.snapshots.clear();
        this.chainPosition = null;
        this.status = GameStatus.PLAYING;
        this.quietMoveCount = 0;
        updateStatus();
    }
}
