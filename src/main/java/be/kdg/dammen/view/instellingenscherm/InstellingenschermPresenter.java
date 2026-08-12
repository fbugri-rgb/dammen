package be.kdg.dammen.view.instellingenscherm;

import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.Settings;
import be.kdg.dammen.model.SettingsRepository;
import be.kdg.dammen.view.DialogHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Vult het instellingenscherm met de bewaarde instellingen en schrijft de keuzes van
 * de speler terug naar het bestand.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class InstellingenschermPresenter {

    private static final int COLOR_MAXIMUM = 255;

    private final SettingsRepository model;
    private final InstellingenschermView view;

    public InstellingenschermPresenter(SettingsRepository model, InstellingenschermView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        view.getSaveButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveSettings();
            }
        });
        view.getDefaultsButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSettings(new Settings());
            }
        });
        view.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeWindow();
            }
        });
    }

    private void updateView() {
        showSettings(model.readSettings());
    }

    private void showSettings(Settings settings) {
        view.showSettings(toColor(settings.getLightSquareColor()),
                toColor(settings.getDarkSquareColor()),
                toColor(settings.getWhitePieceColor()),
                toColor(settings.getBlackPieceColor()),
                settings.isSoundOn());
    }

    /**
     * Bewaart de gekozen instellingen en sluit het venster.
     */
    private void saveSettings() {
        try {
            model.saveSettings(readSettingsFromView());
            closeWindow();
        } catch (DraughtsException | IllegalArgumentException exception) {
            DialogHelper.showError("Bewaren mislukt", exception.getMessage());
        }
    }

    private Settings readSettingsFromView() {
        Settings settings = new Settings();
        settings.setLightSquareColor(toText(view.getLightSquareColor()));
        settings.setDarkSquareColor(toText(view.getDarkSquareColor()));
        settings.setWhitePieceColor(toText(view.getWhitePieceColor()));
        settings.setBlackPieceColor(toText(view.getBlackPieceColor()));
        settings.setSoundOn(view.isSoundSelected());
        return settings;
    }

    /**
     * Zet een kleur van de kleurkiezer om naar tekst, bijvoorbeeld "#8B5E3C".
     */
    private String toText(Color color) {
        return String.format("#%02X%02X%02X",
                Math.round(color.getRed() * COLOR_MAXIMUM),
                Math.round(color.getGreen() * COLOR_MAXIMUM),
                Math.round(color.getBlue() * COLOR_MAXIMUM));
    }

    private Color toColor(String text) {
        return Color.web(text);
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
