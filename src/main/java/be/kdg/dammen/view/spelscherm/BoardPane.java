package be.kdg.dammen.view.spelscherm;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Tekent het dambord: tien rijen van tien velden. Deze klasse hoort bij het scherm en
 * kent het model niet: de presenter zegt met eenvoudige gegevens welke schijf waar
 * getekend moet worden.
 * <p>
 * De kleuren komen uit de instellingen en worden rechtstreeks op de velden en de
 * schijven gezet, zodat de speler ze zelf kan kiezen.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class BoardPane extends GridPane {

    private static final int BOARD_SIZE = 10;
    private static final double SQUARE_SIZE = 56;
    private static final double PIECE_RADIUS = 21;
    private static final double KING_MARK_RADIUS = 11;
    private static final String SELECTED_STYLE =
            "-fx-background-color: #D9B310; -fx-border-color: #8A6F00; -fx-border-width: 3;";
    private static final String TARGET_STYLE =
            "-fx-background-color: #7FBF7F; -fx-border-color: #2F7A2F; -fx-border-width: 3;";
    private static final String WHITE_PIECE_STROKE = "#8A7B63";
    private static final String BLACK_PIECE_STROKE = "#000000";
    private static final String KING_MARK_STYLE =
            "-fx-fill: #D9B310; -fx-stroke: #8A6F00; -fx-stroke-width: 2;";

    private final StackPane[][] squares;
    private String lightSquareColor;
    private String darkSquareColor;
    private String whitePieceColor;
    private String blackPieceColor;

    public BoardPane() {
        this.squares = new StackPane[BOARD_SIZE][BOARD_SIZE];
        this.lightSquareColor = "#E8D5B7";
        this.darkSquareColor = "#8B5E3C";
        this.whitePieceColor = "#FBF6EC";
        this.blackPieceColor = "#2B2B2B";
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                StackPane square = new StackPane();
                square.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
                square.setMinSize(SQUARE_SIZE, SQUARE_SIZE);
                squares[row][column] = square;
            }
        }
        paintAllSquares();
    }

    private void layoutNodes() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                add(squares[row][column], column, row);
            }
        }
        setAlignment(Pos.CENTER);
        getStyleClass().add("board");
    }

    /**
     * Neemt de kleuren over die de speler in de instellingen gekozen heeft.
     * De schijven worden daarna door de presenter opnieuw getekend.
     */
    public void applyColors(String lightSquare, String darkSquare, String whitePiece, String blackPiece) {
        this.lightSquareColor = lightSquare;
        this.darkSquareColor = darkSquare;
        this.whitePieceColor = whitePiece;
        this.blackPieceColor = blackPiece;
        paintAllSquares();
    }

    private void paintAllSquares() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                paintSquare(row, column);
            }
        }
    }

    private void paintSquare(int row, int column) {
        squares[row][column].setStyle("-fx-background-color: " + getSquareColor(row, column) + ";");
    }

    /**
     * Er wordt enkel op de donkere velden gespeeld.
     */
    private String getSquareColor(int row, int column) {
        if ((row + column) % 2 == 1) {
            return darkSquareColor;
        }
        return lightSquareColor;
    }

    /**
     * Geeft een veld terug zodat de presenter er event handlers aan kan hangen.
     */
    public StackPane getSquare(int row, int column) {
        return squares[row][column];
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public double getSquareSize() {
        return SQUARE_SIZE;
    }

    /**
     * Tekent een schijf op een veld.
     *
     * @param white true voor een witte schijf, false voor een zwarte
     * @param king  true als het om een dam gaat
     */
    public void showPiece(int row, int column, boolean white, boolean king) {
        StackPane square = squares[row][column];
        square.getChildren().clear();
        StackPane pieceBox = new StackPane(createPiece(white));
        if (king) {
            Circle kingMark = new Circle(KING_MARK_RADIUS);
            kingMark.setStyle(KING_MARK_STYLE);
            pieceBox.getChildren().add(kingMark);
        }
        square.getChildren().add(pieceBox);
    }

    /**
     * Geeft de getekende schijf van een veld terug. Het herhalingsscherm laat die
     * schijf naar haar nieuwe veld schuiven.
     *
     * @return de schijf, of null als er op dat veld niets ligt
     */
    public Node getPieceNode(int row, int column) {
        StackPane square = squares[row][column];
        if (square.getChildren().isEmpty()) {
            return null;
        }
        return square.getChildren().get(0);
    }

    /**
     * Maakt een schijf in de gekozen kleur.
     */
    public Circle createPiece(boolean white) {
        Circle piece = new Circle(PIECE_RADIUS);
        if (white) {
            piece.setStyle("-fx-fill: " + whitePieceColor + "; -fx-stroke: "
                    + WHITE_PIECE_STROKE + "; -fx-stroke-width: 2;");
        } else {
            piece.setStyle("-fx-fill: " + blackPieceColor + "; -fx-stroke: "
                    + BLACK_PIECE_STROKE + "; -fx-stroke-width: 2;");
        }
        return piece;
    }

    /**
     * Haalt alle schijven van het getekende bord.
     */
    public void clearPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                squares[row][column].getChildren().clear();
            }
        }
    }

    /**
     * Duidt de schijf aan die de speler gekozen heeft.
     */
    public void markSelected(int row, int column) {
        squares[row][column].setStyle(SELECTED_STYLE);
    }

    /**
     * Duidt een veld aan waar de gekozen schijf naartoe mag.
     */
    public void markTarget(int row, int column) {
        squares[row][column].setStyle(TARGET_STYLE);
    }

    /**
     * Haalt alle gele en groene aanduidingen weer weg.
     */
    public void clearMarks() {
        paintAllSquares();
    }
}
