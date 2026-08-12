package be.kdg.dammen.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Slaat een spelsituatie op in een extern tekstbestand en leest ze terug in, zodat
 * een spel later verder gespeeld kan worden.
 * <p>
 * Opbouw van het bestand: eerst een kenlijn, dan de namen van de spelers en de kleur
 * die aan zet is, daarna de tien rijen van het bord en tenslotte alle gespeelde zetten.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class GameStorage {

    private static final String FILE_HEADER = "Dammen-spelbestand v1";
    private static final char EMPTY_SQUARE = '.';
    private static final char WHITE_MAN = 'w';
    private static final char WHITE_KING = 'W';
    private static final char BLACK_MAN = 'z';
    private static final char BLACK_KING = 'Z';

    /**
     * Schrijft de volledige spelsituatie weg naar een bestand.
     *
     * @throws DraughtsException als het bestand niet geschreven kan worden
     */
    public void save(DraughtsGame game, File file) {
        if (game == null || file == null) {
            throw new IllegalArgumentException("Er is een spel en een bestand nodig om op te slaan.");
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(FILE_HEADER);
            writer.println(game.getPlayer(PieceColor.WHITE).getName());
            writer.println(game.getPlayer(PieceColor.BLACK).getName());
            writer.println(game.getCurrentColor().name());
            writeBoard(writer, game.getBoard());
            writeMoves(writer, game.getMoveHistory());
            writer.println(game.isAgainstComputer());
        } catch (IOException exception) {
            throw new DraughtsException("Het spel kon niet opgeslagen worden in dat bestand.", exception);
        }
    }

    private void writeBoard(PrintWriter writer, Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            StringBuilder line = new StringBuilder();
            for (int column = 0; column < Board.SIZE; column++) {
                line.append(toCharacter(board.getPiece(new Position(row, column))));
            }
            writer.println(line);
        }
    }

    private void writeMoves(PrintWriter writer, List<Move> moves) {
        writer.println(moves.size());
        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Position captured = move.getCapturedPosition();
            int capturedRow = -1;
            int capturedColumn = -1;
            if (captured != null) {
                capturedRow = captured.getRow();
                capturedColumn = captured.getColumn();
            }
            writer.println(move.getFrom().getRow() + " " + move.getFrom().getColumn()
                    + " " + move.getTo().getRow() + " " + move.getTo().getColumn()
                    + " " + capturedRow + " " + capturedColumn
                    + " " + move.getColor().name() + " " + move.isPromotion());
        }
    }

    /**
     * Leest een eerder opgeslagen spel terug in.
     *
     * @return het geladen spel
     * @throws DraughtsException als het bestand niet gelezen kan worden of niet klopt
     */
    public DraughtsGame load(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Er is een bestand nodig om te laden.");
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            checkHeader(reader.readLine());
            DraughtsGame game = new DraughtsGame(readLine(reader), readLine(reader));
            PieceColor colorToMove = PieceColor.valueOf(readLine(reader));
            readBoard(reader, game.getBoard());
            game.restoreState(colorToMove, readMoves(reader));
            game.setAgainstComputer(readAgainstComputer(reader));
            return game;
        } catch (IOException exception) {
            throw new DraughtsException("Het bestand kon niet gelezen worden.", exception);
        } catch (IllegalArgumentException exception) {
            throw new DraughtsException("Dit bestand bevat geen geldige damstelling.", exception);
        }
    }

    private void checkHeader(String firstLine) {
        if (!FILE_HEADER.equals(firstLine)) {
            throw new DraughtsException("Dit is geen bestand van dit damspel.");
        }
    }

    private void readBoard(BufferedReader reader, Board board) throws IOException {
        board.clear();
        for (int row = 0; row < Board.SIZE; row++) {
            String line = readLine(reader);
            if (line.length() < Board.SIZE) {
                throw new DraughtsException("Dit bestand bevat geen geldige damstelling.");
            }
            for (int column = 0; column < Board.SIZE; column++) {
                Piece piece = toPiece(line.charAt(column));
                if (piece != null) {
                    board.setPiece(new Position(row, column), piece);
                }
            }
        }
    }

    private List<Move> readMoves(BufferedReader reader) throws IOException {
        List<Move> moves = new ArrayList<>();
        int numberOfMoves = Integer.parseInt(readLine(reader).trim());
        for (int i = 0; i < numberOfMoves; i++) {
            moves.add(toMove(readLine(reader).split(" ")));
        }
        return moves;
    }

    /**
     * Leest of het spel tegen de computer gespeeld werd. Oudere bestanden hebben deze
     * regel nog niet; die spellen zijn gewoon spellen tussen twee spelers.
     *
     * @return true als de computer meespeelde
     */
    private boolean readAgainstComputer(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return line != null && Boolean.parseBoolean(line.trim());
    }

    private Move toMove(String[] parts) {
        if (parts.length < 8) {
            throw new DraughtsException("Dit bestand bevat geen geldige zetten.");
        }
        Position from = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        Position to = new Position(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        int capturedRow = Integer.parseInt(parts[4]);
        int capturedColumn = Integer.parseInt(parts[5]);
        Position captured = null;
        if (capturedRow >= 0 && capturedColumn >= 0) {
            captured = new Position(capturedRow, capturedColumn);
        }
        return new Move(from, to, captured, PieceColor.valueOf(parts[6]),
                Boolean.parseBoolean(parts[7]));
    }

    /**
     * @return de volgende lijn uit het bestand
     * @throws DraughtsException als het bestand vroeger stopt dan verwacht
     */
    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new DraughtsException("Het bestand is onvolledig.");
        }
        return line;
    }

    private char toCharacter(Piece piece) {
        if (piece == null) {
            return EMPTY_SQUARE;
        }
        if (piece.getColor() == PieceColor.WHITE) {
            if (piece.isKing()) {
                return WHITE_KING;
            }
            return WHITE_MAN;
        }
        if (piece.isKing()) {
            return BLACK_KING;
        }
        return BLACK_MAN;
    }

    private Piece toPiece(char character) {
        switch (character) {
            case WHITE_MAN:
                return new Piece(PieceColor.WHITE, PieceType.MAN);
            case WHITE_KING:
                return new Piece(PieceColor.WHITE, PieceType.KING);
            case BLACK_MAN:
                return new Piece(PieceColor.BLACK, PieceType.MAN);
            case BLACK_KING:
                return new Piece(PieceColor.BLACK, PieceType.KING);
            case EMPTY_SQUARE:
                return null;
            default:
                throw new DraughtsException("Dit bestand bevat een onbekend teken op het bord.");
        }
    }
}
