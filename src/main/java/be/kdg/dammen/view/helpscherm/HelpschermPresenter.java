package be.kdg.dammen.view.helpscherm;

import be.kdg.dammen.model.DraughtsException;
import be.kdg.dammen.model.RulesReader;
import be.kdg.dammen.view.DialogHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * Vult het helpscherm met de spelregels uit het tekstbestand en handelt de knop af.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HelpschermPresenter {

    private final RulesReader model;
    private final HelpschermView view;

    public HelpschermPresenter(RulesReader model, HelpschermView view) {
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

    /**
     * Leest de spelregels in. Lukt dat niet, dan krijgt de speler een boodschap in
     * plaats van een lege pagina.
     */
    private void updateView() {
        try {
            view.getRulesTextArea().setText(model.readRules());
        } catch (DraughtsException exception) {
            view.getRulesTextArea().setText("De spelregels konden niet geladen worden.");
            DialogHelper.showError("Spelregels", exception.getMessage());
        }
    }
}
