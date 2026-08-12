package be.kdg.dammen.view.herhalingscherm;

import be.kdg.dammen.view.spelscherm.BoardPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Het herhalingsscherm: hier wordt het gespeelde spel zet na zet opnieuw afgespeeld,
 * met een schuivende schijf bij elke zet.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HerhalingschermView extends BorderPane {

    private static final double SLIDER_WIDTH = 220;
    private static final double MINIMUM_SPEED = 150;
    private static final double MAXIMUM_SPEED = 1500;
    private static final double START_SPEED = 600;

    private BoardPane boardPane;
    private Label titleLabel;
    private Label stepLabel;
    private Label moveLabel;
    private Button playPauseButton;
    private Button previousButton;
    private Button nextButton;
    private Button restartButton;
    private Button closeButton;
    private Slider speedSlider;

    public HerhalingschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        boardPane = new BoardPane();
        titleLabel = new Label("Het spel herbekijken");
        titleLabel.getStyleClass().add("screen-title");
        stepLabel = new Label();
        stepLabel.getStyleClass().add("turn-label");
        moveLabel = new Label();
        moveLabel.getStyleClass().add("message-label");
        playPauseButton = new Button("Afspelen");
        playPauseButton.getStyleClass().add("main-button");
        previousButton = new Button("Vorige zet");
        nextButton = new Button("Volgende zet");
        restartButton = new Button("Opnieuw vanaf het begin");
        closeButton = new Button("Sluiten");
        speedSlider = new Slider(MINIMUM_SPEED, MAXIMUM_SPEED, START_SPEED);
        speedSlider.setPrefWidth(SLIDER_WIDTH);
        speedSlider.setShowTickMarks(true);
    }

    private void layoutNodes() {
        setTop(createTopBox());
        setCenter(createBoardBox());
        setBottom(createBottomBox());
        getStyleClass().add("replay-screen");
    }

    private VBox createTopBox() {
        VBox topBox = new VBox(6, titleLabel, stepLabel, moveLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(15, 10, 5, 10));
        return topBox;
    }

    private StackPane createBoardBox() {
        StackPane boardBox = new StackPane(boardPane);
        boardBox.setPadding(new Insets(10, 20, 10, 20));
        return boardBox;
    }

    private VBox createBottomBox() {
        HBox buttonBox = new HBox(10, previousButton, playPauseButton, nextButton, restartButton);
        buttonBox.setAlignment(Pos.CENTER);
        Label speedLabel = new Label("Traag");
        Label fastLabel = new Label("Snel");
        HBox speedBox = new HBox(10, fastLabel, speedSlider, speedLabel, closeButton);
        speedBox.setAlignment(Pos.CENTER);
        VBox bottomBox = new VBox(10, buttonBox, speedBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(5, 15, 20, 15));
        return bottomBox;
    }

    /**
     * Toont bij welke zet de herhaling nu staat.
     */
    void showProgress(int currentStep, int totalSteps, String moveText) {
        stepLabel.setText("Zet " + currentStep + " van " + totalSteps);
        moveLabel.setText(moveText);
    }

    /**
     * Zet het opschrift van de knop op "Afspelen" of "Pauze".
     */
    void showPlaying(boolean playing) {
        if (playing) {
            playPauseButton.setText("Pauze");
        } else {
            playPauseButton.setText("Afspelen");
        }
    }

    /**
     * Zet de knoppen aan of uit naargelang er nog zetten voor of na deze zet zijn.
     */
    void enableStepButtons(boolean canGoBack, boolean canGoForward) {
        previousButton.setDisable(!canGoBack);
        nextButton.setDisable(!canGoForward);
        playPauseButton.setDisable(!canGoForward);
    }

    public BoardPane getBoardPane() {
        return boardPane;
    }

    double getSpeedInMilliseconds() {
        return speedSlider.getValue();
    }

    Button getPlayPauseButton() {
        return playPauseButton;
    }

    Button getPreviousButton() {
        return previousButton;
    }

    Button getNextButton() {
        return nextButton;
    }

    Button getRestartButton() {
        return restartButton;
    }

    Button getCloseButton() {
        return closeButton;
    }
}
