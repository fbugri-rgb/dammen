package be.kdg.dammen.view.startscherm;

import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.DraughtsGame;
import be.kdg.dammen.model.GameStorage;
import be.kdg.dammen.view.DialogHelper;
import be.kdg.dammen.view.ScreenHelper;
import be.kdg.dammen.view.spelscherm.GameFileChooser;
import be.kdg.dammen.view.spelscherm.SpelschermPresenter;
import be.kdg.dammen.view.spelscherm.SpelschermView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;

import java.io.File;

/**
 * Handelt het startscherm af: de namen nakijken, een spel starten of openen en de
 * andere schermen tonen. Dit scherm heeft zelf geen model nodig, het maakt er een
 * aan zodra er gespeeld wordt.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class StartschermPresenter {

    private static final String COMPUTER_NAME = "Computer";

    private final StartschermView view;

    public StartschermPresenter(StartschermView view) {
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers() {
        addButtonHandlers();
        addMenuHandlers();
        addOpponentHandlers();
    }

    /**
     * Zodra de speler kiest tegen wie hij speelt, past het scherm zich aan.
     */
    private void addOpponentHandlers() {
        view.getAgainstFriendRadioButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showOpponent();
            }
        });
        view.getAgainstComputerRadioButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showOpponent();
            }
        });
    }

    private void showOpponent() {
        view.showComputerOpponent(view.isAgainstComputerSelected(), COMPUTER_NAME);
    }

    private void addButtonHandlers() {
        view.getNewGameButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startNewGame();
            }
        });
        view.getLoadGameButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadGame();
            }
        });
        view.getRulesButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openRules(view.getScene().getWindow());
            }
        });
        view.getHighScoresButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openHighScores(view.getScene().getWindow());
            }
        });
        view.getAboutButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openAbout(view.getScene().getWindow());
            }
        });
        view.getSettingsButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScreenHelper.openSettings(view.getScene().getWindow());
            }
        });
        view.getQuitButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quit();
            }
        });
    }

    /**
     * De menubalk biedt dezelfde mogelijkheden aan als de knoppen op het scherm.
     */
    private void addMenuHandlers() {
        view.getMenuItemNewGame().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startNewGame();
            }
        });
        view.getMenuItemLoad().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadGame();
            }
        });
        view.getMenuItemQuit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                quit();
            }
        });
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

    /**
     * Vraagt bevestiging als de speler het venster sluit. Deze methode wordt vanuit
     * de Main opgeroepen, nadat het scherm op de stage staat.
     */
    public void addWindowEventHandlers() {
        view.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!DialogHelper.askConfirmation("Afsluiten", "Wil je het damspel afsluiten?")) {
                    event.consume();
                }
            }
        });
    }

    /**
     * Maakt een nieuw spel met de ingevulde namen. Het model kijkt de namen na.
     */
    private void startNewGame() {
        if (haveSameName()) {
            DialogHelper.showError("Dezelfde naam",
                    "Geef de twee spelers een verschillende naam.");
            return;
        }
        try {
            DraughtsGame game = new DraughtsGame(view.getWhitePlayerName(), getBlackPlayerName());
            game.setAgainstComputer(view.isAgainstComputerSelected());
            showGameScreen(game);
        } catch (IllegalArgumentException exception) {
            DialogHelper.showError("Namen niet in orde", exception.getMessage());
        }
    }

    /**
     * Speelt de speler tegen de computer, dan heet de tegenstander altijd "Computer".
     */
    private String getBlackPlayerName() {
        if (view.isAgainstComputerSelected()) {
            return COMPUTER_NAME;
        }
        return view.getBlackPlayerName();
    }

    private boolean haveSameName() {
        String whiteName = view.getWhitePlayerName().trim();
        String blackName = getBlackPlayerName().trim();
        return whiteName.equalsIgnoreCase(blackName);
    }

    /**
     * Opent een eerder bewaard spel en speelt daarmee verder.
     */
    private void loadGame() {
        File file = new GameFileChooser().askOpenFile(view.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            showGameScreen(new GameStorage().load(file));
        } catch (DraughtsException exception) {
            DialogHelper.showError("Openen mislukt", exception.getMessage());
        }
    }

    /**
     * Vervangt het startscherm door het spelscherm in hetzelfde venster.
     */
    private void showGameScreen(DraughtsGame game) {
        SpelschermView gameView = new SpelschermView();
        new SpelschermPresenter(game, gameView);
        // eerst de scene onthouden: zodra het spelscherm de nieuwe root is, hoort dit
        // startscherm niet meer bij de scene en geeft view.getScene() null terug
        Scene scene = view.getScene();
        scene.setRoot(gameView);
        scene.getWindow().sizeToScene();
    }

    private void quit() {
        if (DialogHelper.askConfirmation("Afsluiten", "Wil je het damspel afsluiten?")) {
            Platform.exit();
        }
    }
}
