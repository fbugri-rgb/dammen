package be.kdg.dammen.view.herhalingscherm;

import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.DraughtsGame;
import be.kdg.dammen.model.Move;
import be.kdg.dammen.model.Piece;
import be.kdg.dammen.model.PieceColor;
import be.kdg.dammen.model.Position;
import be.kdg.dammen.model.Settings;
import be.kdg.dammen.model.SettingsRepository;
import be.kdg.dammen.view.DialogHelper;
import be.kdg.dammen.view.spelscherm.BoardPane;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Speelt het gespeelde spel opnieuw af. De zetten worden bijgehouden in een lijst en
 * één voor één opnieuw gespeeld op een nieuw spel. Bij elke zet schuift de schijf van
 * haar oude naar haar nieuwe veld.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HerhalingschermPresenter {

    private static final double PAUSE_BETWEEN_MOVES = 150;

    private final List<Move> playedMoves;
    private final DraughtsGame model;
    private final HerhalingschermView view;
    private boolean playing;
    private boolean animating;

    public HerhalingschermPresenter(DraughtsGame playedGame, HerhalingschermView view) {
        if (playedGame == null) {
            throw new IllegalArgumentException("Er is een gespeeld spel nodig om te herbekijken.");
        }
        this.view = view;
        this.playedMoves = new ArrayList<>(playedGame.getMoveHistory());
        this.model = new DraughtsGame(playedGame.getPlayer(PieceColor.WHITE).getName(),
                playedGame.getPlayer(PieceColor.BLACK).getName());
        this.playing = false;
        this.animating = false;
        addEventHandlers();
        applySettings();
        updateView();
    }

    private void addEventHandlers() {
        view.getPlayPauseButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                togglePlaying();
            }
        });
        view.getNextButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showNextMove();
            }
        });
        view.getPreviousButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showPreviousMove();
            }
        });
        view.getRestartButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                restart();
            }
        });
        view.getCloseButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });
    }

    /**
     * Start of pauzeert het automatisch afspelen. Staat de herhaling helemaal op het
     * einde, dan beginnen we opnieuw vanaf het begin.
     */
    private void togglePlaying() {
        if (playing) {
            stopPlaying();
            return;
        }
        if (isAtEnd()) {
            restart();
        }
        playing = true;
        view.showPlaying(true);
        showNextMove();
    }

    /**
     * Speelt de volgende zet en laat de schijf naar haar nieuwe veld schuiven.
     */
    private void showNextMove() {
        if (animating || isAtEnd()) {
            return;
        }
        Move move = playedMoves.get(model.getMoveCount());
        Node piece = view.getBoardPane().getPieceNode(move.getFrom().getRow(),
                move.getFrom().getColumn());
        if (piece == null) {
            applyMove(move);
            return;
        }
        slidePiece(move, piece);
    }

    private void slidePiece(Move move, Node piece) {
        animating = true;
        BoardPane boardPane = view.getBoardPane();
        // het veld naar voren halen, anders schuift de schijf onder de andere velden
        boardPane.getSquare(move.getFrom().getRow(), move.getFrom().getColumn()).toFront();
        TranslateTransition slide = new TranslateTransition(
                Duration.millis(view.getSpeedInMilliseconds()), piece);
        slide.setByX(getColumnDistance(move) * boardPane.getSquareSize());
        slide.setByY(getRowDistance(move) * boardPane.getSquareSize());
        slide.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                animating = false;
                applyMove(move);
                playNextMoveLater();
            }
        });
        slide.play();
    }

    private int getColumnDistance(Move move) {
        return move.getTo().getColumn() - move.getFrom().getColumn();
    }

    private int getRowDistance(Move move) {
        return move.getTo().getRow() - move.getFrom().getRow();
    }

    /**
     * Speelt de zet echt op het bord en tekent alles opnieuw. Zou een zet uit de
     * geschiedenis toch niet meer kunnen, dan stoppen we het afspelen netjes in plaats
     * van de speler met een vastgelopen scherm achter te laten.
     */
    private void applyMove(Move move) {
        try {
            model.movePiece(move.getFrom(), move.getTo());
            updateView();
        } catch (DraughtsException exception) {
            stopPlaying();
            DialogHelper.showError("Herbekijken lukt niet",
                    "Deze zet kon niet opnieuw afgespeeld worden.");
        }
    }

    /**
     * Stopt het automatisch afspelen en zet de knop terug op "Afspelen".
     */
    private void stopPlaying() {
        playing = false;
        view.showPlaying(false);
    }

    /**
     * Wacht heel even en speelt dan de volgende zet, zolang het afspelen aan staat.
     */
    private void playNextMoveLater() {
        if (!playing) {
            return;
        }
        if (isAtEnd()) {
            stopPlaying();
            return;
        }
        PauseTransition pause = new PauseTransition(Duration.millis(PAUSE_BETWEEN_MOVES));
        pause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showNextMove();
            }
        });
        pause.play();
    }

    /**
     * Zet één zet terug. Dat gebeurt zonder animatie.
     */
    private void showPreviousMove() {
        if (animating || !model.canUndo()) {
            return;
        }
        try {
            model.undoMoves(1);
            updateView();
        } catch (DraughtsException exception) {
            DialogHelper.showError("Herbekijken lukt niet",
                    "Deze zet kon niet teruggenomen worden.");
        }
    }

    private void restart() {
        if (animating) {
            return;
        }
        model.startNewGame();
        updateView();
    }

    private boolean isAtEnd() {
        return model.getMoveCount() >= playedMoves.size();
    }

    private void applySettings() {
        Settings settings = new SettingsRepository().readSettings();
        view.getBoardPane().applyColors(settings.getLightSquareColor(),
                settings.getDarkSquareColor(), settings.getWhitePieceColor(),
                settings.getBlackPieceColor());
    }

    private void updateView() {
        showBoard();
        view.showProgress(model.getMoveCount(), playedMoves.size(), getMoveText());
        view.enableStepButtons(model.canUndo(), !isAtEnd());
        view.showPlaying(playing);
    }

    /**
     * Tekent het bord en duidt aan waar de laatste zet vandaan kwam en naartoe ging.
     */
    private void showBoard() {
        BoardPane boardPane = view.getBoardPane();
        boardPane.clearMarks();
        boardPane.clearPieces();
        markLastMove();
        for (int row = 0; row < boardPane.getBoardSize(); row++) {
            for (int column = 0; column < boardPane.getBoardSize(); column++) {
                Piece piece = model.getBoard().getPiece(new Position(row, column));
                if (piece != null) {
                    boardPane.showPiece(row, column, piece.getColor() == PieceColor.WHITE,
                            piece.isKing());
                }
            }
        }
    }

    private void markLastMove() {
        if (model.getMoveCount() == 0) {
            return;
        }
        Move lastMove = playedMoves.get(model.getMoveCount() - 1);
        view.getBoardPane().markTarget(lastMove.getTo().getRow(), lastMove.getTo().getColumn());
        view.getBoardPane().markSelected(lastMove.getFrom().getRow(),
                lastMove.getFrom().getColumn());
    }

    /**
     * @return de tekst van de zet die net gespeeld werd
     */
    private String getMoveText() {
        if (model.getMoveCount() == 0) {
            return "De beginstelling van het spel";
        }
        return playedMoves.get(model.getMoveCount() - 1).toString();
    }

    private void closeWindow() {
        playing = false;
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
