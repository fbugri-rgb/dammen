package be.kdg.dammen.view.highscorescherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Het topscorescherm. Toont in een tabel wie een spel won en in hoeveel zetten.
 * De rijen worden door de presenter toegevoegd.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HighscoreschermView extends BorderPane {

    private static final int NUMBER_OF_HEADERS = 4;

    private Label titleLabel;
    private Label emptyLabel;
    private GridPane scoreGrid;
    private ScrollPane scrollPane;
    private Button closeButton;

    public HighscoreschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        titleLabel = new Label("Topscores");
        titleLabel.getStyleClass().add("screen-title");
        emptyLabel = new Label("Er zijn nog geen topscores. Speel een spel uit!");
        emptyLabel.getStyleClass().add("empty-label");
        scoreGrid = new GridPane();
        scrollPane = new ScrollPane(scoreGrid);
        scrollPane.setFitToWidth(true);
        closeButton = new Button("Sluiten");
    }

    private void layoutNodes() {
        scoreGrid.setHgap(25);
        scoreGrid.setVgap(8);
        scoreGrid.setPadding(new Insets(15));
        addHeaders();
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 15, 15, 15));
        setTop(titleBox);
        setCenter(scrollPane);
        setBottom(buttonBox);
        setPadding(new Insets(10, 20, 10, 20));
        getStyleClass().add("highscore-screen");
    }

    private void addHeaders() {
        String[] headers = {"Plaats", "Speler", "Aantal zetten", "Datum"};
        for (int column = 0; column < NUMBER_OF_HEADERS; column++) {
            Label header = new Label(headers[column]);
            header.getStyleClass().add("table-header");
            scoreGrid.add(header, column, 0);
        }
    }

    /**
     * Voegt één topscore toe aan de tabel.
     *
     * @param rank de plaats in de rangschikking, te beginnen bij 1
     */
    void addHighScoreRow(int rank, String playerName, int numberOfMoves, String date) {
        scoreGrid.add(new Label(rank + "."), 0, rank);
        scoreGrid.add(new Label(playerName), 1, rank);
        scoreGrid.add(new Label(String.valueOf(numberOfMoves)), 2, rank);
        scoreGrid.add(new Label(date), 3, rank);
    }

    /**
     * Laat weten dat er nog geen topscores bewaard zijn.
     */
    void showEmptyMessage() {
        scoreGrid.add(emptyLabel, 0, 1, NUMBER_OF_HEADERS, 1);
    }

    Button getCloseButton() {
        return closeButton;
    }
}
