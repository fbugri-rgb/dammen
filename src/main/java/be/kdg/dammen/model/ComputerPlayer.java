package be.kdg.dammen.model;

import java.util.List;

/**
 * De computertegenstander. Hij kiest zijn zet door een aantal zetten vooruit te
 * rekenen op een kopie van het spel.
 * <p>
 * Werkwijze (minimax): voor elke zet die hij kan doen, speelt hij die zet op een
 * kopie en kijkt dan wat de tegenstander daarna zou doen, en zo verder tot de
 * afgesproken diepte. Elke stelling krijgt een score. De computer gaat ervan uit dat
 * de tegenstander telkens zijn beste zet speelt, en kiest dus de zet die hem in het
 * slechtste geval nog het meeste oplevert.
 * <p>
 * Om niet nodeloos veel te rekenen wordt een tak niet verder onderzocht zodra ze het
 * eindresultaat toch niet meer kan verbeteren (dit heet "alpha-bèta snoeien").
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class ComputerPlayer {

    private static final int SEARCH_DEPTH = 6;
    private static final int WIN_SCORE = 100000;
    private static final int DRAW_SCORE = 0;
    private static final int MAN_VALUE = 100;
    private static final int KING_VALUE = 300;
    private static final int ROW_BONUS = 4;
    private static final int EDGE_BONUS = 3;
    private static final int BACK_ROW_BONUS = 5;

    private final PieceColor color;

    public ComputerPlayer(PieceColor color) {
        if (color == null) {
            throw new IllegalArgumentException("De computer heeft een kleur nodig.");
        }
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }

    /**
     * Zoekt de beste zet voor de computer in de huidige stelling.
     *
     * @return de gekozen zet
     * @throws DraughtsException als de computer niet aan zet is of niet kan spelen
     */
    public Move chooseMove(DraughtsGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Er is een spel nodig om een zet te kiezen.");
        }
        if (game.getCurrentColor() != color) {
            throw new DraughtsException("De computer is niet aan zet.");
        }
        List<Move> possibleMoves = game.getValidMovesForCurrentPlayer();
        if (possibleMoves.isEmpty()) {
            throw new DraughtsException("De computer kan geen enkele zet meer doen.");
        }
        return findBestMove(game, possibleMoves);
    }

    /**
     * Probeert elke mogelijke zet uit op een kopie van het spel en houdt de beste over.
     */
    private Move findBestMove(DraughtsGame game, List<Move> possibleMoves) {
        Move bestMove = possibleMoves.get(0);
        int bestScore = -WIN_SCORE * 2;
        for (int i = 0; i < possibleMoves.size(); i++) {
            Move move = possibleMoves.get(i);
            int score = scoreAfterMove(game, move, SEARCH_DEPTH, bestScore, WIN_SCORE * 2);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    /**
     * Berekent hoe goed een stelling is voor de computer, door verder vooruit te kijken.
     *
     * @param depth hoeveel halve zetten er nog onderzocht worden
     * @param alpha het beste dat de computer tot nu toe zeker kan bereiken
     * @param beta  het beste dat de tegenstander tot nu toe zeker kan bereiken
     * @return de score van de stelling, gezien door de ogen van de computer
     */
    private int searchScore(DraughtsGame game, int depth, int alpha, int beta) {
        if (game.getStatus().isFinished() || depth <= 0) {
            return scorePosition(game);
        }
        List<Move> possibleMoves = game.getValidMovesForCurrentPlayer();
        if (possibleMoves.isEmpty()) {
            return scorePosition(game);
        }
        if (game.getCurrentColor() == color) {
            return searchBestForComputer(game, possibleMoves, depth, alpha, beta);
        }
        return searchBestForOpponent(game, possibleMoves, depth, alpha, beta);
    }

    /**
     * De computer kiest de zet met de hoogste score.
     */
    private int searchBestForComputer(DraughtsGame game, List<Move> moves,
                                      int depth, int alpha, int beta) {
        int best = -WIN_SCORE * 2;
        for (int i = 0; i < moves.size(); i++) {
            int score = scoreAfterMove(game, moves.get(i), depth, alpha, beta);
            if (score > best) {
                best = score;
            }
            if (best > alpha) {
                alpha = best;
            }
            if (alpha >= beta) {
                return best;
            }
        }
        return best;
    }

    /**
     * De tegenstander kiest de zet met de laagste score voor de computer.
     */
    private int searchBestForOpponent(DraughtsGame game, List<Move> moves,
                                      int depth, int alpha, int beta) {
        int best = WIN_SCORE * 2;
        for (int i = 0; i < moves.size(); i++) {
            int score = scoreAfterMove(game, moves.get(i), depth, alpha, beta);
            if (score < best) {
                best = score;
            }
            if (best < beta) {
                beta = best;
            }
            if (alpha >= beta) {
                return best;
            }
        }
        return best;
    }

    private int scoreAfterMove(DraughtsGame game, Move move, int depth, int alpha, int beta) {
        DraughtsGame copy = game.copyForThinking();
        copy.movePiece(move.getFrom(), move.getTo());
        return searchScore(copy, depth - 1, alpha, beta);
    }

    /**
     * Geeft een stelling een cijfer. Hoe hoger, hoe beter voor de computer.
     * Gewonnen spellen wegen het zwaarst, daarna het materiaal (dammen tellen zwaarder
     * dan gewone schijven) en tenslotte de plaats van de schijven op het bord.
     */
    private int scorePosition(DraughtsGame game) {
        if (game.getStatus().isFinished()) {
            return scoreFinishedGame(game);
        }
        int score = 0;
        Board board = game.getBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = board.getPiece(new Position(row, column));
                if (piece != null) {
                    score += scorePiece(piece, row, column);
                }
            }
        }
        return score;
    }

    /**
     * Winnen is het beste, verliezen het slechtste, remise zit daar netjes tussenin.
     */
    private int scoreFinishedGame(DraughtsGame game) {
        Player winner = game.getWinner();
        if (winner == null) {
            return DRAW_SCORE;
        }
        if (winner.getColor() == color) {
            return WIN_SCORE;
        }
        return -WIN_SCORE;
    }

    /**
     * Berekent wat één schijf waard is. Schijven van de computer tellen positief,
     * die van de tegenstander negatief.
     */
    private int scorePiece(Piece piece, int row, int column) {
        int value = MAN_VALUE;
        if (piece.isKing()) {
            value = KING_VALUE;
        } else {
            value += ROW_BONUS * getStepsAdvanced(piece.getColor(), row);
            value += getBackRowBonus(piece.getColor(), row);
        }
        if (column == 0 || column == Board.SIZE - 1) {
            value += EDGE_BONUS;
        }
        if (piece.getColor() == color) {
            return value;
        }
        return -value;
    }

    /**
     * @return hoeveel rijen deze schijf al opgeschoven is richting de damlijn
     */
    private int getStepsAdvanced(PieceColor pieceColor, int row) {
        if (pieceColor == PieceColor.WHITE) {
            return Board.SIZE - 1 - row;
        }
        return row;
    }

    /**
     * Schijven op de eigen achterlijn houden de tegenstander tegen om dam te halen.
     */
    private int getBackRowBonus(PieceColor pieceColor, int row) {
        if (row == Board.getPromotionRow(pieceColor.getOpponent())) {
            return BACK_ROW_BONUS;
        }
        return 0;
    }
}
