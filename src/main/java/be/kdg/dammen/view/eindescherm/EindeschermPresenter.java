package be.kdg.dammen.view.eindescherm;

import be.kdg.dammen.model.DraughtsGame;
import be.kdg.dammen.model.GameStatus;
import be.kdg.dammen.model.PieceColor;
import be.kdg.dammen.model.Player;
import be.kdg.dammen.view.ScreenHelper;
import be.kdg.dammen.view.spelscherm.SpelschermPresenter;
import be.kdg.dammen.view.spelscherm.SpelschermView;
import be.kdg.dammen.view.startscherm.StartschermPresenter;
import be.kdg.dammen.view.startscherm.StartschermView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Toont de uitslag van het spel en brengt de speler naar het volgende scherm.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class EindeschermPresenter {

    private final DraughtsGame model;
    private final EindeschermView view;

    public EindeschermPresenter(DraughtsGame model, EindeschermView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        view.getReplayButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openReplay(view.getScene().getWindow(), model);
            }
        });
        view.getNewGameButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startNewGame();
            }
        });
        view.getHighScoresButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openHighScores(view.getScene().getWindow());
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
     * Speelt opnieuw met dezelfde twee spelers.
     */
    private void startNewGame() {
        model.startNewGame();
        SpelschermView gameView = new SpelschermView();
        new SpelschermPresenter(model, gameView);
        view.getScene().setRoot(gameView);
    }

    private void goToMainMenu() {
        StartschermView startView = new StartschermView();
        new StartschermPresenter(startView);
        view.getScene().setRoot(startView);
    }

    private void updateView() {
        String movesText = "Het spel duurde " + model.getMoveCount() + " zetten.";
        if (model.getStatus() == GameStatus.DRAW) {
            view.showResult("Remise!", "Er werd lang niet meer geslagen, "
                    + "dus eindigt het spel onbeslist.", movesText);
            return;
        }
        Player winner = model.getWinner();
        if (winner == null) {
            view.showResult("Het spel is nog bezig.", "", "");
            return;
        }
        view.showResult(winner.getName() + " wint met de "
                        + winner.getColor().getDutchName().toLowerCase() + "te schijven!",
                getReasonText(winner.getColor().getOpponent()), movesText);
    }

    /**
     * @return de reden waarom de verliezer het spel verloren heeft
     */
    private String getReasonText(PieceColor loserColor) {
        String loserName = model.getPlayer(loserColor).getName();
        if (model.getBoard().countPieces(loserColor) == 0) {
            return loserName + " heeft geen schijven meer op het bord.";
        }
        return loserName + " kan geen enkele zet meer doen.";
    }
}
