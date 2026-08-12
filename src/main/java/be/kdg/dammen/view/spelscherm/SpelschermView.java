package be.kdg.dammen.view.spelscherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Het spelscherm: het dambord, de vakken met de geslagen schijven, de lijst met alle
 * gespeelde zetten, de knop om zetten terug te nemen en een menubalk.
 * Deze klasse bevat enkel schermopbouw.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class SpelschermView extends BorderPane {

    private static final double CAPTURED_PIECE_RADIUS = 10;
    private static final int SIDE_PANEL_WIDTH = 260;
    private static final int SPINNER_WIDTH = 70;

    private BoardPane boardPane;
    private MenuBar menuBar;
    private MenuItem menuItemNewGame;
    private MenuItem menuItemUndo;
    private MenuItem menuItemSave;
    private MenuItem menuItemLoad;
    private MenuItem menuItemSettings;
    private MenuItem menuItemReplay;
    private MenuItem menuItemMainMenu;
    private MenuItem menuItemQuit;
    private MenuItem menuItemRules;
    private MenuItem menuItemHighScores;
    private MenuItem menuItemAbout;
    private Label turnLabel;
    private Label messageLabel;
    private Label capturedWhiteLabel;
    private Label capturedBlackLabel;
    private FlowPane capturedWhitePane;
    private FlowPane capturedBlackPane;
    private ListView<String> moveListView;
    private Spinner<Integer> undoSpinner;
    private SpinnerValueFactory.IntegerSpinnerValueFactory undoValueFactory;
    private Button undoButton;
    private Button saveButton;
    private Button mainMenuButton;

    public SpelschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        boardPane = new BoardPane();
        initialiseMenu();
        turnLabel = new Label("Wit is aan zet");
        turnLabel.getStyleClass().add("turn-label");
        messageLabel = new Label("Veel plezier!");
        messageLabel.getStyleClass().add("message-label");
        capturedWhiteLabel = new Label("Geslagen witte schijven: 0");
        capturedBlackLabel = new Label("Geslagen zwarte schijven: 0");
        capturedWhitePane = new FlowPane();
        capturedBlackPane = new FlowPane();
        moveListView = new ListView<>();
        moveListView.setPrefHeight(220);
        initialiseButtons();
    }

    private void initialiseButtons() {
        undoValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
        undoSpinner = new Spinner<>(undoValueFactory);
        undoSpinner.setPrefWidth(SPINNER_WIDTH);
        undoButton = new Button("Zetten terugnemen");
        undoButton.setMaxWidth(Double.MAX_VALUE);
        saveButton = new Button("Spel opslaan");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        mainMenuButton = new Button("Naar hoofdmenu");
        mainMenuButton.setMaxWidth(Double.MAX_VALUE);
    }

    private void initialiseMenu() {
        menuItemNewGame = new MenuItem("Nieuw spel");
        menuItemUndo = new MenuItem("Zet terugnemen");
        menuItemSave = new MenuItem("Spel opslaan...");
        menuItemLoad = new MenuItem("Spel openen...");
        menuItemSettings = new MenuItem("Instellingen...");
        menuItemReplay = new MenuItem("Zetten herbekijken...");
        menuItemMainMenu = new MenuItem("Naar hoofdmenu");
        menuItemQuit = new MenuItem("Afsluiten");
        menuItemRules = new MenuItem("Spelregels");
        menuItemHighScores = new MenuItem("Topscores");
        menuItemAbout = new MenuItem("Over dit spel");
        Menu gameMenu = new Menu("Spel");
        gameMenu.getItems().addAll(menuItemNewGame, menuItemUndo, menuItemReplay,
                new SeparatorMenuItem(), menuItemSave, menuItemLoad, menuItemSettings,
                new SeparatorMenuItem(), menuItemMainMenu, menuItemQuit);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(menuItemRules, menuItemHighScores, menuItemAbout);
        menuBar = new MenuBar(gameMenu, helpMenu);
    }

    private void layoutNodes() {
        setTop(createTopBox());
        setCenter(createBoardBox());
        setRight(createSidePanel());
        setBottom(createBottomBox());
        getStyleClass().add("game-screen");
    }

    private VBox createTopBox() {
        HBox turnBox = new HBox(turnLabel);
        turnBox.setAlignment(Pos.CENTER);
        turnBox.setPadding(new Insets(10));
        return new VBox(menuBar, turnBox);
    }

    private StackPane createBoardBox() {
        StackPane boardBox = new StackPane(boardPane);
        boardBox.setPadding(new Insets(10, 20, 10, 20));
        return boardBox;
    }

    private VBox createSidePanel() {
        VBox sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(10, 15, 10, 5));
        sidePanel.setPrefWidth(SIDE_PANEL_WIDTH);
        sidePanel.setMinWidth(SIDE_PANEL_WIDTH);
        Label movesTitle = new Label("Gespeelde zetten");
        movesTitle.getStyleClass().add("panel-title");
        sidePanel.getChildren().addAll(
                createCapturedBox(capturedBlackLabel, capturedBlackPane),
                createCapturedBox(capturedWhiteLabel, capturedWhitePane),
                movesTitle, moveListView, createUndoBox(),
                saveButton, mainMenuButton);
        return sidePanel;
    }

    /**
     * Het vakje waarin de speler kiest hoeveel zetten hij wil terugnemen.
     */
    private VBox createUndoBox() {
        Label undoTitle = new Label("Aantal zetten terug:");
        HBox spinnerBox = new HBox(10, undoSpinner, undoTitle);
        spinnerBox.setAlignment(Pos.CENTER_LEFT);
        VBox undoBox = new VBox(6, spinnerBox, undoButton);
        undoBox.setPadding(new Insets(4, 0, 4, 0));
        return undoBox;
    }

    /**
     * Bouwt een apart vak waarin de geslagen schijven van één kleur terechtkomen.
     */
    private VBox createCapturedBox(Label title, FlowPane piecesPane) {
        title.getStyleClass().add("panel-title");
        piecesPane.setHgap(4);
        piecesPane.setVgap(4);
        piecesPane.setPrefHeight(56);
        piecesPane.setMinHeight(56);
        piecesPane.setPadding(new Insets(6));
        piecesPane.getStyleClass().add("captured-pane");
        return new VBox(4, title, piecesPane);
    }

    private HBox createBottomBox() {
        HBox bottomBox = new HBox(messageLabel);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(8, 20, 12, 20));
        return bottomBox;
    }

    /**
     * Geeft de gekozen kleuren door aan het bord en aan de vakjes met geslagen schijven.
     */
    void applyColors(String lightSquare, String darkSquare, String whitePiece, String blackPiece) {
        boardPane.applyColors(lightSquare, darkSquare, whitePiece, blackPiece);
    }

    /**
     * Toont hoeveel schijven van elke kleur er al geslagen werden.
     */
    void showCapturedPieces(int capturedWhite, int capturedBlack) {
        capturedWhiteLabel.setText("Geslagen witte schijven: " + capturedWhite);
        capturedBlackLabel.setText("Geslagen zwarte schijven: " + capturedBlack);
        fillCapturedPane(capturedWhitePane, capturedWhite, true);
        fillCapturedPane(capturedBlackPane, capturedBlack, false);
    }

    private void fillCapturedPane(FlowPane pane, int numberOfPieces, boolean white) {
        pane.getChildren().clear();
        for (int i = 0; i < numberOfPieces; i++) {
            Circle piece = boardPane.createPiece(white);
            piece.setRadius(CAPTURED_PIECE_RADIUS);
            pane.getChildren().add(piece);
        }
    }

    /**
     * Zet hoeveel zetten er hoogstens teruggenomen kunnen worden.
     */
    void setMaximumUndoCount(int maximum) {
        undoValueFactory.setMax(Math.max(1, maximum));
        if (undoValueFactory.getValue() > undoValueFactory.getMax()) {
            undoValueFactory.setValue(undoValueFactory.getMax());
        }
        undoSpinner.setDisable(maximum < 1);
        undoButton.setDisable(maximum < 1);
    }

    int getUndoCount() {
        return undoValueFactory.getValue();
    }

    BoardPane getBoardPane() {
        return boardPane;
    }

    MenuItem getMenuItemNewGame() {
        return menuItemNewGame;
    }

    MenuItem getMenuItemUndo() {
        return menuItemUndo;
    }

    MenuItem getMenuItemSave() {
        return menuItemSave;
    }

    MenuItem getMenuItemLoad() {
        return menuItemLoad;
    }

    MenuItem getMenuItemSettings() {
        return menuItemSettings;
    }

    MenuItem getMenuItemReplay() {
        return menuItemReplay;
    }

    MenuItem getMenuItemMainMenu() {
        return menuItemMainMenu;
    }

    MenuItem getMenuItemQuit() {
        return menuItemQuit;
    }

    MenuItem getMenuItemRules() {
        return menuItemRules;
    }

    MenuItem getMenuItemHighScores() {
        return menuItemHighScores;
    }

    MenuItem getMenuItemAbout() {
        return menuItemAbout;
    }

    Label getTurnLabel() {
        return turnLabel;
    }

    Label getMessageLabel() {
        return messageLabel;
    }

    ListView<String> getMoveListView() {
        return moveListView;
    }

    Button getUndoButton() {
        return undoButton;
    }

    Button getSaveButton() {
        return saveButton;
    }

    Button getMainMenuButton() {
        return mainMenuButton;
    }
}
