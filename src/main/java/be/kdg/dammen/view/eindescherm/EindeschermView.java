package be.kdg.dammen.view.eindescherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Het eindscherm dat verschijnt zodra een spel gewonnen is. Het toont wie won en
 * geeft de speler de keuze om opnieuw te spelen.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class EindeschermView extends BorderPane {

    private Label winnerLabel;
    private Label reasonLabel;
    private Label movesLabel;
    private Button replayButton;
    private Button newGameButton;
    private Button highScoresButton;
    private Button mainMenuButton;

    public EindeschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        winnerLabel = new Label();
        winnerLabel.getStyleClass().add("winner-label");
        reasonLabel = new Label();
        reasonLabel.getStyleClass().add("message-label");
        movesLabel = new Label();
        movesLabel.getStyleClass().add("message-label");
        replayButton = new Button("Spel herbekijken");
        replayButton.getStyleClass().add("main-button");
        newGameButton = new Button("Nog een spel");
        highScoresButton = new Button("Topscores");
        mainMenuButton = new Button("Naar hoofdmenu");
    }

    private void layoutNodes() {
        Label titleLabel = new Label("Einde van het spel");
        titleLabel.getStyleClass().add("screen-title");
        VBox centerBox = new VBox(15, titleLabel, winnerLabel, reasonLabel, movesLabel);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(30));
        HBox buttonBox = new HBox(15, replayButton, newGameButton, highScoresButton, mainMenuButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 20, 40, 20));
        setCenter(centerBox);
        setBottom(buttonBox);
        getStyleClass().add("end-screen");
    }

    /**
     * Vult het scherm met de uitslag van het spel.
     */
    void showResult(String winnerText, String reasonText, String movesText) {
        winnerLabel.setText(winnerText);
        reasonLabel.setText(reasonText);
        movesLabel.setText(movesText);
    }

    Button getReplayButton() {
        return replayButton;
    }

    Button getNewGameButton() {
        return newGameButton;
    }

    Button getHighScoresButton() {
        return highScoresButton;
    }

    Button getMainMenuButton() {
        return mainMenuButton;
    }
}
