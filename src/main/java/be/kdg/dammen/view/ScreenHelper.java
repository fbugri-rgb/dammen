package be.kdg.dammen.view;

import be.kdg.dammen.model.DraughtsGame;
import be.kdg.dammen.model.HighScoreRepository;
import be.kdg.dammen.model.RulesReader;
import be.kdg.dammen.model.SettingsRepository;
import be.kdg.dammen.view.aboutscherm.AboutschermPresenter;
import be.kdg.dammen.view.aboutscherm.AboutschermView;
import be.kdg.dammen.view.helpscherm.HelpschermPresenter;
import be.kdg.dammen.view.helpscherm.HelpschermView;
import be.kdg.dammen.view.herhalingscherm.HerhalingschermPresenter;
import be.kdg.dammen.view.herhalingscherm.HerhalingschermView;
import be.kdg.dammen.view.highscorescherm.HighscoreschermPresenter;
import be.kdg.dammen.view.highscorescherm.HighscoreschermView;
import be.kdg.dammen.view.instellingenscherm.InstellingenschermPresenter;
import be.kdg.dammen.view.instellingenscherm.InstellingenschermView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;

/**
 * Opent de schermen die in een eigen venster getoond worden: de spelregels, de
 * topscores en het aboutscherm. Zowel het startscherm als het spelscherm gebruiken
 * deze klasse, zo staat die code maar op één plaats.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class ScreenHelper {

    private static final String STYLE_SHEET = "/be/kdg/dammen/view/css/style.css";
    private static final double SECONDARY_WIDTH = 640;
    private static final double SECONDARY_HEIGHT = 580;
    private static final double REPLAY_WIDTH = 720;
    private static final double REPLAY_HEIGHT = 830;
    private static final double REPLAY_MINIMUM_WIDTH = 660;
    private static final double REPLAY_MINIMUM_HEIGHT = 800;
    private static final double MINIMUM_WIDTH = 400;
    private static final double MINIMUM_HEIGHT = 320;

    private ScreenHelper() {
        // Deze klasse bevat enkel hulpmethodes en wordt nooit aangemaakt.
    }

    /**
     * Hangt het stijlblad aan een scene. Ontbreekt het bestand, dan blijft de
     * applicatie gewoon werken zonder opmaak.
     */
    public static void applyStyleSheet(Scene scene) {
        URL styleSheet = ScreenHelper.class.getResource(STYLE_SHEET);
        if (styleSheet != null) {
            scene.getStylesheets().add(styleSheet.toExternalForm());
        }
    }

    /**
     * Opent het helpscherm met de spelregels in een nieuw venster.
     */
    public static void openRules(Window owner) {
        HelpschermView view = new HelpschermView();
        new HelpschermPresenter(new RulesReader(), view);
        showInNewStage(view, "Spelregels", owner);
    }

    /**
     * Opent het topscorescherm in een nieuw venster.
     */
    public static void openHighScores(Window owner) {
        HighscoreschermView view = new HighscoreschermView();
        new HighscoreschermPresenter(new HighScoreRepository(), view);
        showInNewStage(view, "Topscores", owner);
    }

    /**
     * Opent het instellingenscherm in een nieuw venster. Zodra de speler het sluit,
     * kan de oproeper de nieuwe instellingen inlezen en toepassen.
     */
    public static void openSettings(Window owner) {
        InstellingenschermView view = new InstellingenschermView();
        new InstellingenschermPresenter(new SettingsRepository(), view);
        showInNewStage(view, "Instellingen", owner);
    }

    /**
     * Opent het aboutscherm in een nieuw venster.
     */
    public static void openAbout(Window owner) {
        AboutschermView view = new AboutschermView();
        new AboutschermPresenter(view);
        showInNewStage(view, "Over dit spel", owner);
    }

    /**
     * Opent het herhalingsscherm in een nieuw venster, waarin het gespeelde spel
     * opnieuw afgespeeld wordt.
     */
    public static void openReplay(Window owner, DraughtsGame playedGame) {
        if (playedGame.getMoveCount() == 0) {
            DialogHelper.showInformation("Nog niets te herbekijken",
                    "Er is nog geen enkele zet gespeeld.");
            return;
        }
        HerhalingschermView view = new HerhalingschermView();
        new HerhalingschermPresenter(playedGame, view);
        showInNewStage(view, "Het spel herbekijken", owner, REPLAY_WIDTH, REPLAY_HEIGHT,
                REPLAY_MINIMUM_WIDTH, REPLAY_MINIMUM_HEIGHT);
    }

    /**
     * Toont een scherm in een nieuw venster bovenop het huidige venster. De speler
     * moet dit venster eerst sluiten voor hij verder kan spelen.
     */
    private static void showInNewStage(Parent view, String title, Window owner) {
        showInNewStage(view, title, owner, SECONDARY_WIDTH, SECONDARY_HEIGHT,
                MINIMUM_WIDTH, MINIMUM_HEIGHT);
    }

    /**
     * Het herhalingsscherm heeft een groter venster nodig dan de andere schermen,
     * want daar moet een volledig dambord op passen.
     */
    private static void showInNewStage(Parent view, String title, Window owner,
                                       double width, double height,
                                       double minimumWidth, double minimumHeight) {
        Stage stage = new Stage();
        Scene scene = new Scene(view, width, height);
        applyStyleSheet(scene);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setMinWidth(minimumWidth);
        stage.setMinHeight(minimumHeight);
        if (owner != null) {
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.showAndWait();
    }
}
