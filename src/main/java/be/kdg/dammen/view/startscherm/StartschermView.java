package be.kdg.dammen.view.startscherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Het startscherm. Hier vullen de twee spelers hun naam in en kiezen ze wat ze
 * willen doen: spelen, een bewaard spel openen, de regels lezen, ...
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class StartschermView extends BorderPane {

    private static final int FIELD_WIDTH = 180;
    private static final int BUTTON_WIDTH = 200;

    private MenuBar menuBar;
    private MenuItem menuItemNewGame;
    private MenuItem menuItemLoad;
    private MenuItem menuItemQuit;
    private MenuItem menuItemRules;
    private MenuItem menuItemHighScores;
    private MenuItem menuItemAbout;
    private RadioButton againstFriendRadioButton;
    private RadioButton againstComputerRadioButton;
    private ToggleGroup opponentGroup;
    private TextField whiteNameField;
    private TextField blackNameField;
    private Button newGameButton;
    private Button loadGameButton;
    private Button rulesButton;
    private Button highScoresButton;
    private Button aboutButton;
    private Button settingsButton;
    private Button quitButton;

    public StartschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        initialiseMenu();
        initialiseOpponentChoice();
        whiteNameField = new TextField("Speler 1");
        whiteNameField.setPrefWidth(FIELD_WIDTH);
        blackNameField = new TextField("Speler 2");
        blackNameField.setPrefWidth(FIELD_WIDTH);
        newGameButton = new Button("Nieuw spel starten");
        loadGameButton = new Button("Bewaard spel openen");
        rulesButton = new Button("Spelregels");
        highScoresButton = new Button("Topscores");
        aboutButton = new Button("Over dit spel");
        settingsButton = new Button("Instellingen");
        quitButton = new Button("Afsluiten");
        setButtonWidths();
    }

    /**
     * De speler kiest tegen wie hij speelt: tegen iemand anders aan hetzelfde scherm,
     * of tegen de computer.
     */
    private void initialiseOpponentChoice() {
        opponentGroup = new ToggleGroup();
        againstFriendRadioButton = new RadioButton("Tegen een vriend");
        againstFriendRadioButton.setToggleGroup(opponentGroup);
        againstFriendRadioButton.setSelected(true);
        againstComputerRadioButton = new RadioButton("Tegen de computer");
        againstComputerRadioButton.setToggleGroup(opponentGroup);
    }

    private void initialiseMenu() {
        menuItemNewGame = new MenuItem("Nieuw spel");
        menuItemLoad = new MenuItem("Spel openen...");
        menuItemQuit = new MenuItem("Afsluiten");
        menuItemRules = new MenuItem("Spelregels");
        menuItemHighScores = new MenuItem("Topscores");
        menuItemAbout = new MenuItem("Over dit spel");
        Menu gameMenu = new Menu("Spel");
        gameMenu.getItems().addAll(menuItemNewGame, menuItemLoad,
                new SeparatorMenuItem(), menuItemQuit);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(menuItemRules, menuItemHighScores, menuItemAbout);
        menuBar = new MenuBar(gameMenu, helpMenu);
    }

    private void setButtonWidths() {
        Button[] buttons = {newGameButton, loadGameButton, rulesButton,
                highScoresButton, aboutButton, settingsButton, quitButton};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPrefWidth(BUTTON_WIDTH);
        }
        newGameButton.getStyleClass().add("main-button");
    }

    private void layoutNodes() {
        VBox centerBox = new VBox(20, createTitleBox(), createOpponentBox(),
                createNameGrid(), createButtonBox());
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(30));
        setTop(menuBar);
        setCenter(centerBox);
        getStyleClass().add("start-screen");
    }

    private VBox createTitleBox() {
        Label titleLabel = new Label("DAMMEN");
        titleLabel.getStyleClass().add("game-title");
        Label subtitleLabel = new Label("Een spel voor twee spelers op 100 velden");
        subtitleLabel.getStyleClass().add("message-label");
        VBox titleBox = new VBox(8, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        return titleBox;
    }

    private HBox createOpponentBox() {
        HBox opponentBox = new HBox(20, againstFriendRadioButton, againstComputerRadioButton);
        opponentBox.setAlignment(Pos.CENTER);
        return opponentBox;
    }

    /**
     * De twee invulvakjes voor de namen van de spelers.
     */
    private GridPane createNameGrid() {
        GridPane nameGrid = new GridPane();
        nameGrid.setHgap(15);
        nameGrid.setVgap(12);
        nameGrid.setAlignment(Pos.CENTER);
        nameGrid.setPadding(new Insets(15));
        nameGrid.getStyleClass().add("name-grid");
        nameGrid.add(new Label("Speler met de witte schijven:"), 0, 0);
        nameGrid.add(whiteNameField, 1, 0);
        nameGrid.add(new Label("Speler met de zwarte schijven:"), 0, 1);
        nameGrid.add(blackNameField, 1, 1);
        return nameGrid;
    }

    private VBox createButtonBox() {
        HBox firstRow = new HBox(15, newGameButton, loadGameButton);
        firstRow.setAlignment(Pos.CENTER);
        HBox secondRow = new HBox(15, rulesButton, highScoresButton);
        secondRow.setAlignment(Pos.CENTER);
        HBox thirdRow = new HBox(15, settingsButton, aboutButton);
        thirdRow.setAlignment(Pos.CENTER);
        HBox fourthRow = new HBox(15, quitButton);
        fourthRow.setAlignment(Pos.CENTER);
        VBox buttonBox = new VBox(12, firstRow, secondRow, thirdRow, fourthRow);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    MenuItem getMenuItemNewGame() {
        return menuItemNewGame;
    }

    MenuItem getMenuItemLoad() {
        return menuItemLoad;
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

    String getWhitePlayerName() {
        return whiteNameField.getText();
    }

    String getBlackPlayerName() {
        return blackNameField.getText();
    }

    boolean isAgainstComputerSelected() {
        return againstComputerRadioButton.isSelected();
    }

    /**
     * Vult de naam van de zwarte speler in en blokkeert dat vakje wanneer de computer
     * meespeelt: die naam ligt dan vast.
     */
    void showComputerOpponent(boolean againstComputer, String computerName) {
        if (againstComputer) {
            blackNameField.setText(computerName);
        }
        blackNameField.setDisable(againstComputer);
    }

    RadioButton getAgainstFriendRadioButton() {
        return againstFriendRadioButton;
    }

    RadioButton getAgainstComputerRadioButton() {
        return againstComputerRadioButton;
    }

    Button getNewGameButton() {
        return newGameButton;
    }

    Button getLoadGameButton() {
        return loadGameButton;
    }

    Button getRulesButton() {
        return rulesButton;
    }

    Button getHighScoresButton() {
        return highScoresButton;
    }

    Button getAboutButton() {
        return aboutButton;
    }

    Button getSettingsButton() {
        return settingsButton;
    }

    Button getQuitButton() {
        return quitButton;
    }
}
