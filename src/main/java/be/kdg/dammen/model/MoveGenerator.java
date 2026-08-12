package be.kdg.dammen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Zoekt op welke zetten er volgens de spelregels mogelijk zijn op een bord.
 * Deze klasse wijzigt het bord nooit, ze kijkt enkel welke zetten toegelaten zijn.
 * <p>
 * Gewone schijven schuiven en slaan enkel schuin naar voren. Een dam mag over
 * meerdere velden, in alle vier de richtingen.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class MoveGenerator {

    private static final int[] DIAGONAL_ROWS = {-1, -1, 1, 1};
    private static final int[] DIAGONAL_COLUMNS = {-1, 1, -1, 1};

    private final Board board;

    public MoveGenerator(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Er is een bord nodig om zetten te zoeken.");
        }
        this.board = board;
    }

    /**
     * Geeft alle zetten die deze speler nu mag doen. Slaan is verplicht: zodra er
     * ergens geslagen kan worden, zijn enkel de slagzetten geldig.
     *
     * @return de lijst met geldige zetten, leeg als de speler niet meer kan spelen
     */
    public List<Move> getValidMoves(PieceColor color) {
        List<Move> captureMoves = getAllCaptureMoves(color);
        if (!captureMoves.isEmpty()) {
            return captureMoves;
        }
        return getAllSimpleMoves(color);
    }

    /**
     * @return alle slagzetten van deze speler, over al zijn schijven heen
     */
    public List<Move> getAllCaptureMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        List<Position> positions = getPositionsOf(color);
        for (int i = 0; i < positions.size(); i++) {
            moves.addAll(getCaptureMovesFrom(positions.get(i)));
        }
        return moves;
    }

    private List<Move> getAllSimpleMoves(PieceColor color) {
        List<Move> moves = new ArrayList<>();
        List<Position> positions = getPositionsOf(color);
        for (int i = 0; i < positions.size(); i++) {
            moves.addAll(getSimpleMovesFrom(positions.get(i)));
        }
        return moves;
    }

    /**
     * Zoekt de slagzetten van één bepaalde schijf. Dit gebruiken we ook om te zien
     * of een speler na een slagzet moet doorslaan met dezelfde schijf.
     *
     * @return de slagzetten vanaf deze positie
     */
    public List<Move> getCaptureMovesFrom(Position from) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPiece(from);
        if (piece == null) {
            return moves;
        }
        if (piece.isKing()) {
            addKingCaptures(from, piece, moves);
        } else {
            addManCaptures(from, piece, moves);
        }
        return moves;
    }

    /**
     * @return de gewone (niet-slaande) zetten vanaf deze positie
     */
    public List<Move> getSimpleMovesFrom(Position from) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPiece(from);
        if (piece == null) {
            return moves;
        }
        if (piece.isKing()) {
            addKingSimpleMoves(from, piece, moves);
        } else {
            addManSimpleMoves(from, piece, moves);
        }
        return moves;
    }

    /**
     * Een gewone schijf springt schuin naar voren over een aangrenzende tegenstander,
     * op voorwaarde dat het veld daarachter leeg is.
     */
    private void addManCaptures(Position from, Piece piece, List<Move> moves) {
        int rowStep = piece.getColor().getForwardDirection();
        for (int side = -1; side <= 1; side += 2) {
            Position over = from.move(rowStep, side);
            Position target = from.move(rowStep * 2, side * 2);
            if (over != null && target != null
                    && hasOpponentOn(over, piece.getColor()) && board.isEmpty(target)) {
                moves.add(createMove(from, target, over, piece));
            }
        }
    }

    /**
     * Een gewone schijf schuift één veld schuin naar voren naar een leeg veld.
     */
    private void addManSimpleMoves(Position from, Piece piece, List<Move> moves) {
        int rowStep = piece.getColor().getForwardDirection();
        for (int side = -1; side <= 1; side += 2) {
            Position target = from.move(rowStep, side);
            if (target != null && board.isEmpty(target)) {
                moves.add(createMove(from, target, null, piece));
            }
        }
    }

    private void addKingCaptures(Position from, Piece piece, List<Move> moves) {
        for (int direction = 0; direction < DIAGONAL_ROWS.length; direction++) {
            addKingCapturesInDirection(from, piece, direction, moves);
        }
    }

    /**
     * Een dam schuift over de lijn tot ze een schijf tegenkomt. Is dat een schijf van
     * de tegenstander en is het veld erachter leeg, dan mag de dam die schijf slaan en
     * op eender welk leeg veld daarachter landen.
     */
    private void addKingCapturesInDirection(Position from, Piece piece, int direction, List<Move> moves) {
        int rowStep = DIAGONAL_ROWS[direction];
        int columnStep = DIAGONAL_COLUMNS[direction];
        Position current = from.move(rowStep, columnStep);
        while (current != null && board.isEmpty(current)) {
            current = current.move(rowStep, columnStep);
        }
        if (current == null || !hasOpponentOn(current, piece.getColor())) {
            return;
        }
        Position landing = current.move(rowStep, columnStep);
        while (landing != null && board.isEmpty(landing)) {
            moves.add(createMove(from, landing, current, piece));
            landing = landing.move(rowStep, columnStep);
        }
    }

    private void addKingSimpleMoves(Position from, Piece piece, List<Move> moves) {
        for (int direction = 0; direction < DIAGONAL_ROWS.length; direction++) {
            Position target = from.move(DIAGONAL_ROWS[direction], DIAGONAL_COLUMNS[direction]);
            while (target != null && board.isEmpty(target)) {
                moves.add(createMove(from, target, null, piece));
                target = target.move(DIAGONAL_ROWS[direction], DIAGONAL_COLUMNS[direction]);
            }
        }
    }

    /**
     * Maakt de zet aan en kijkt meteen na of de speler er een dam mee haalt.
     */
    private Move createMove(Position from, Position to, Position captured, Piece piece) {
        boolean promotion = !piece.isKing()
                && to.getRow() == Board.getPromotionRow(piece.getColor());
        return new Move(from, to, captured, piece.getColor(), promotion);
    }

    private boolean hasOpponentOn(Position position, PieceColor color) {
        Piece piece = board.getPiece(position);
        return piece != null && piece.getColor() == color.getOpponent();
    }

    /**
     * @return de posities van alle schijven van deze kleur
     */
    private List<Position> getPositionsOf(PieceColor color) {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Position position = new Position(row, column);
                Piece piece = board.getPiece(position);
                if (piece != null && piece.getColor() == color) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }
}
