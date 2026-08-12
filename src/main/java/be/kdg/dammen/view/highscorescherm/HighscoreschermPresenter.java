package be.kdg.dammen.view.highscorescherm;

import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.HighScore;
import be.kdg.dammen.model.HighScoreRepository;
import be.kdg.dammen.view.DialogHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Haalt de topscores op uit het bestand en zet ze in de tabel van het topscorescherm.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HighscoreschermPresenter {

    private final HighScoreRepository model;
    private final HighscoreschermView view;

    public HighscoreschermPresenter(HighScoreRepository model, HighscoreschermView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        view.getCloseButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) view.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void updateView() {
        List<HighScore> highScores = readHighScores();
        if (highScores.isEmpty()) {
            view.showEmptyMessage();
            return;
        }
        for (int i = 0; i < highScores.size(); i++) {
            HighScore highScore = highScores.get(i);
            view.addHighScoreRow(i + 1, highScore.getPlayerName(),
                    highScore.getNumberOfMoves(), highScore.getDate());
        }
    }

    /**
     * Kan het bestand niet gelezen worden, dan tonen we een lege lijst in plaats van
     * de applicatie te laten vastlopen.
     */
    private List<HighScore> readHighScores() {
        try {
            return model.readHighScores();
        } catch (DraughtsException exception) {
            DialogHelper.showError("Topscores", exception.getMessage());
            return new ArrayList<>();
        }
    }
}
