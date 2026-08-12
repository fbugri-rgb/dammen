package be.kdg.dammen.view.aboutscherm;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * Handelt de knop van het aboutscherm af. Dit scherm toont enkel vaste tekst en
 * heeft daarom geen modelklasse nodig.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class AboutschermPresenter {

    private final AboutschermView view;

    public AboutschermPresenter(AboutschermView view) {
        this.view = view;
        addEventHandlers();
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
}
