package be.kdg.dammen.view.spelscherm;

import be.kdg.dammen.model.ComputerPlayer;
import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.DraughtsGame;
import be.kdg.dammen.model.GameStorage;
import be.kdg.dammen.model.HighScoreRepository;
import be.kdg.dammen.model.Move;
import be.kdg.dammen.model.Piece;
import be.kdg.dammen.model.PieceColor;
import be.kdg.dammen.model.Player;
import be.kdg.dammen.model.Position;
import be.kdg.dammen.model.Settings;
import be.kdg.dammen.model.SettingsRepository;
import be.kdg.dammen.view.DialogHelper;
import be.kdg.dammen.view.ScreenHelper;
import be.kdg.dammen.view.SoundPlayer;
import be.kdg.dammen.view.eindescherm.EindeschermPresenter;
import be.kdg.dammen.view.eindescherm.EindeschermView;
import be.kdg.dammen.view.startscherm.StartschermPresenter;
import be.kdg.dammen.view.startscherm.StartschermView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

/**
 * Verbindt het damspel met het spelscherm: hij vangt de klikken en het slepen op,
 * geeft de zetten door aan het model en tekent daarna het bord opnieuw. Hij laat ook
 * de computer zijn zet spelen en neemt zetten terug.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class SpelschermPresenter {

    private static final int NO_SELECTION = -1;
    private static final String POSITION_SEPARATOR = ",";
    private static final int COMPUTER_PAUSE_IN_MILLISECONDS = 350;

    private final SpelschermView view;
    private final GameStorage gameStorage;
    private final HighScoreRepository highScoreRepository;
    private final SettingsRepository settingsRepository;
    private final GameFileChooser fileChooser;
    private final ComputerPlayer computerPlayer;
    private final SoundPlayer soundPlayer;
    private DraughtsGame model;
    private int selectedRow;
    private int selectedColumn;

    public SpelschermPresenter(DraughtsGame model, SpelschermView view) {
        this.model = model;
        this.view = view;
        this.gameStorage = new GameStorage();
        this.highScoreRepository = new HighScoreRepository();
        this.settingsRepository = new SettingsRepository();
        this.fileChooser = new GameFileChooser();
        this.computerPlayer = new ComputerPlayer(DraughtsGame.COMPUTER_COLOR);
        this.soundPlayer = new SoundPlayer(true);
        clearSelection();
        addEventHandlers();
        applySettings();
        updateView();
        letComputerPlayIfNeeded();
    }

    private void addEventHandlers() {
        addBoardHandlers();
        addMenuHandlers();
        addButtonHandlers();
    }

    /**
     * Elk veld van het bord luistert naar een muisklik en naar slepen.
     */
    private void addBoardHandlers() {
        BoardPane boardPane = view.getBoardPane();
        for (int row = 0; row < boardPane.getBoardSize(); row++) {
            for (int column = 0; column < boardPane.getBoardSize(); column++) {
                addSquareHandlers(row, column);
            }
        }
    }

    private void addSquareHandlers(final int row, final int column) {
        StackPane square = view.getBoardPane().getSquare(row, column);
        square.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleSquareClicked(row, column);
            }
        });
        square.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startDrag(row, column, event);
            }
        });
        square.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                acceptDrag(event);
            }
        });
        square.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                handleDrop(row, column, event);
            }
        });
    }

    private void addMenuHandlers() {
        view.getMenuItemNewGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startNewGame();
            }
        });
        view.getMenuItemUndo().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                undoMoves();
            }
        });
        view.getMenuItemSave().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveGame();
            }
        });
        view.getMenuItemLoad().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadGame();
            }
        });
        view.getMenuItemSettings().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openSettings();
            }
        });
        view.getMenuItemReplay().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openReplay(view.getScene().getWindow(), model);
            }
        });
        view.getMenuItemMainMenu().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goToMainMenu();
            }
        });
        view.getMenuItemQuit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quit();
            }
        });
        addHelpMenuHandlers();
    }

    private void addHelpMenuHandlers() {
        view.getMenuItemRules().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openRules(view.getScene().getWindow());
            }
        });
        view.getMenuItemHighScores().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openHighScores(view.getScene().getWindow());
            }
        });
        view.getMenuItemAbout().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openAbout(view.getScene().getWindow());
            }
        });
    }

    private void addButtonHandlers() {
        view.getUndoButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                undoMoves();
            }
        });
        view.getSaveButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveGame();
            }
        });
        view.getMainMenuButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                goToMainMenu();
            }
        });
    }

    /**
     * Eerste klik kiest een schijf, tweede klik kiest het veld waar ze naartoe gaat.
     * Klikken op de gekozen schijf maakt de keuze weer ongedaan.
     */
    private void handleSquareClicked(int row, int column) {
        if (hasSelection() && selectedRow == row && selectedColumn == column) {
            clearSelection();
            updateView();
            return;
        }
        if (hasSelection()) {
            movePiece(selectedRow, selectedColumn, row, column);
            return;
        }
        selectSquare(row, column);
    }

    /**
     * Het slepen start enkel bij een schijf die op dit moment mag spelen.
     */
    private void startDrag(int row, int column, MouseEvent event) {
        if (model.getValidMovesFrom(new Position(row, column)).isEmpty()) {
            return;
        }
        Dragboard dragboard = view.getBoardPane().getSquare(row, column)
                .startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(row + POSITION_SEPARATOR + column);
        dragboard.setContent(content);
        selectSquare(row, column);
        event.consume();
    }

    private void acceptDrag(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Vangt het loslaten van een gesleepte schijf op. Wordt er iets anders op het bord
     * gesleept, bijvoorbeeld tekst uit een ander programma, dan gebeurt er niets.
     */
    private void handleDrop(int row, int column, DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean dropped = false;
        if (dragboard.hasString()) {
            Position from = toPosition(dragboard.getString());
            if (from != null) {
                movePiece(from.getRow(), from.getColumn(), row, column);
                dropped = true;
            }
        }
        event.setDropCompleted(dropped);
        event.consume();
    }

    /**
     * Zet de tekst van het sleepvenster ("rij,kolom") om naar een positie.
     *
     * @return de positie, of null als de tekst niet van dit spel komt
     */
    private Position toPosition(String text) {
        String[] parts = text.split(POSITION_SEPARATOR);
        if (parts.length != 2) {
            return null;
        }
        try {
            return new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Kiest een schijf. Kan die schijf niet spelen, dan krijgt de speler uitleg.
     */
    private void selectSquare(int row, int column) {
        Position position = new Position(row, column);
        if (!model.getValidMovesFrom(position).isEmpty()) {
            selectedRow = row;
            selectedColumn = column;
            updateView();
            return;
        }
        Piece piece = model.getBoard().getPiece(position);
        if (piece != null) {
            view.getMessageLabel().setText(getRefusalText(piece));
        }
    }

    private String getRefusalText(Piece piece) {
        if (piece.getColor() != model.getCurrentColor()) {
            return "Die schijf is niet van jou: " + model.getCurrentPlayer().getName()
                    + " is aan zet.";
        }
        if (model.mustCapture()) {
            return "Met die schijf kan je niet slaan, en slaan is verplicht.";
        }
        return "Die schijf kan nergens naartoe.";
    }

    /**
     * Geeft de zet door aan het model en toont het gevolg aan de speler.
     */
    private void movePiece(int fromRow, int fromColumn, int toRow, int toColumn) {
        try {
            Move move = model.movePiece(new Position(fromRow, fromColumn),
                    new Position(toRow, toColumn));
            clearSelection();
            updateView();
            showMoveResult(move);
        } catch (DraughtsException exception) {
            clearSelection();
            updateView();
            view.getMessageLabel().setText(exception.getMessage());
            DialogHelper.showError("Die zet kan niet", exception.getMessage());
        }
    }

    /**
     * Laat het geluid horen, meldt een dam en kijkt na of het spel gedaan is.
     * Is het spel nog bezig, dan mag de computer zijn zet doen.
     */
    private void showMoveResult(Move move) {
        playMoveSound(move);
        if (move.isPromotion()) {
            DialogHelper.showInformation("Een dam erbij!",
                    model.getPlayer(move.getColor()).getName() + " haalt een dam op veld "
                            + move.getTo().getSquareNumber() + ".");
        }
        if (model.getStatus().isFinished()) {
            soundPlayer.playWin();
            saveHighScore();
            showEndScreen();
            return;
        }
        letComputerPlayIfNeeded();
    }

    private void playMoveSound(Move move) {
        if (move.isPromotion()) {
            soundPlayer.playKing();
        } else if (move.isCapture()) {
            soundPlayer.playCapture();
        } else {
            soundPlayer.playMove();
        }
    }

    /**
     * Is de computer aan zet, dan wacht hij heel even zodat de speler de vorige zet nog
     * ziet, en speelt daarna.
     */
    private void letComputerPlayIfNeeded() {
        if (!model.isComputerToMove()) {
            return;
        }
        view.getTurnLabel().setText("De computer denkt na...");
        PauseTransition thinkingPause =
                new PauseTransition(Duration.millis(COMPUTER_PAUSE_IN_MILLISECONDS));
        thinkingPause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playComputerMove();
            }
        });
        thinkingPause.play();
    }

    /**
     * Laat de computer één zet spelen. Kan hij daarna nog doorslaan, dan komt hij via
     * showMoveResult vanzelf opnieuw aan de beurt.
     */
    private void playComputerMove() {
        try {
            Move move = computerPlayer.chooseMove(model);
            model.movePiece(move.getFrom(), move.getTo());
            clearSelection();
            updateView();
            showMoveResult(move);
        } catch (DraughtsException exception) {
            DialogHelper.showError("De computer kan niet spelen", exception.getMessage());
        }
    }

    /**
     * Neemt het gekozen aantal zetten terug. Speelt de speler tegen de computer, dan
     * gaan we door tot de speler zelf weer aan zet is.
     */
    private void undoMoves() {
        try {
            model.undoMoves(view.getUndoCount());
            undoComputerMoves();
            clearSelection();
            updateView();
            view.getMessageLabel().setText("Zetten teruggenomen. Je mag opnieuw spelen.");
        } catch (DraughtsException exception) {
            DialogHelper.showError("Terugnemen lukt niet", exception.getMessage());
        }
    }

    private void undoComputerMoves() {
        while (model.isComputerToMove() && model.canUndo()) {
            model.undoMoves(1);
        }
    }

    private void saveHighScore() {
        Player winner = model.getWinner();
        if (winner == null) {
            return;
        }
        try {
            highScoreRepository.addHighScore(winner.getName(), model.getMoveCount());
        } catch (DraughtsException exception) {
            DialogHelper.showError("Topscores", exception.getMessage());
        }
    }

    /**
     * Vervangt de scene van het venster door het eindscherm.
     */
    private void showEndScreen() {
        EindeschermView endView = new EindeschermView();
        new EindeschermPresenter(model, endView);
        Scene endScene = new Scene(endView, view.getScene().getWidth(), view.getScene().getHeight());
        ScreenHelper.applyStyleSheet(endScene);
        Stage stage = (Stage) view.getScene().getWindow();
        stage.setScene(endScene);
    }

    private void startNewGame() {
        if (!DialogHelper.askConfirmation("Nieuw spel",
                "Wil je opnieuw beginnen? Het spel dat bezig is, gaat verloren.")) {
            return;
        }
        model.startNewGame();
        clearSelection();
        updateView();
        letComputerPlayIfNeeded();
    }

    private void saveGame() {
        File file = fileChooser.askSaveFile(view.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            gameStorage.save(model, file);
            view.getMessageLabel().setText("Het spel is bewaard in " + file.getName() + ".");
        } catch (DraughtsException exception) {
            DialogHelper.showError("Opslaan mislukt", exception.getMessage());
        }
    }

    private void loadGame() {
        File file = fileChooser.askOpenFile(view.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            model = gameStorage.load(file);
            clearSelection();
            updateView();
            view.getMessageLabel().setText("Het spel uit " + file.getName() + " is geladen.");
            letComputerPlayIfNeeded();
        } catch (DraughtsException exception) {
            DialogHelper.showError("Openen mislukt", exception.getMessage());
        }
    }

    /**
     * Opent het instellingenscherm en neemt de nieuwe kleuren meteen over.
     */
    private void openSettings() {
        ScreenHelper.openSettings(view.getScene().getWindow());
        applySettings();
        updateView();
    }

    private void applySettings() {
        Settings settings = settingsRepository.readSettings();
        view.applyColors(settings.getLightSquareColor(), settings.getDarkSquareColor(),
                settings.getWhitePieceColor(), settings.getBlackPieceColor());
        soundPlayer.setSoundOn(settings.isSoundOn());
    }

    private void goToMainMenu() {
        if (!DialogHelper.askConfirmation("Naar het hoofdmenu",
                "Wil je terug naar het hoofdmenu? Het spel dat bezig is, gaat verloren.")) {
            return;
        }
        StartschermView startView = new StartschermView();
        new StartschermPresenter(startView);
        view.getScene().setRoot(startView);
    }

    private void quit() {
        if (DialogHelper.askConfirmation("Afsluiten", "Wil je het spel afsluiten?")) {
            Platform.exit();
        }
    }

    private void updateView() {
        showBoard();
        showSelection();
        showTurn();
        showCapturedPieces();
        showMoves();
        showHint();
        view.setMaximumUndoCount(model.getUndoableMoveCount());
    }

    private void showBoard() {
        BoardPane boardPane = view.getBoardPane();
        boardPane.clearPieces();
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

    /**
     * Kleurt de gekozen schijf en de velden waar ze naartoe mag.
     */
    private void showSelection() {
        BoardPane boardPane = view.getBoardPane();
        boardPane.clearMarks();
        if (!hasSelection()) {
            return;
        }
        boardPane.markSelected(selectedRow, selectedColumn);
        List<Move> moves = model.getValidMovesFrom(new Position(selectedRow, selectedColumn));
        for (int i = 0; i < moves.size(); i++) {
            Position target = moves.get(i).getTo();
            boardPane.markTarget(target.getRow(), target.getColumn());
        }
    }

    private void showTurn() {
        view.getTurnLabel().setText("Aan zet: " + model.getCurrentPlayer().getName()
                + " (" + model.getCurrentColor().getDutchName().toLowerCase() + ")");
    }

    private void showCapturedPieces() {
        view.showCapturedPieces(model.getCapturedCount(PieceColor.WHITE),
                model.getCapturedCount(PieceColor.BLACK));
    }

    private void showMoves() {
        List<Move> moves = model.getMoveHistory();
        view.getMoveListView().getItems().clear();
        for (int i = 0; i < moves.size(); i++) {
            view.getMoveListView().getItems().add((i + 1) + ". " + moves.get(i));
        }
        if (!moves.isEmpty()) {
            view.getMoveListView().scrollTo(moves.size() - 1);
        }
    }

    /**
     * Zet onderaan het scherm wat de speler nu moet doen.
     */
    private void showHint() {
        if (model.getStatus().isFinished()) {
            view.getMessageLabel().setText("Het spel is afgelopen.");
        } else if (model.getChainPosition() != null) {
            view.getMessageLabel().setText("Doorslaan! Je moet verder slaan met dezelfde schijf.");
        } else if (model.mustCapture()) {
            view.getMessageLabel().setText("Opgelet: slaan is verplicht!");
        } else {
            view.getMessageLabel().setText("Kies een schijf en klik of sleep ze naar een groen veld.");
        }
    }

    private boolean hasSelection() {
        return selectedRow != NO_SELECTION;
    }

    private void clearSelection() {
        selectedRow = NO_SELECTION;
        selectedColumn = NO_SELECTION;
    }
}
