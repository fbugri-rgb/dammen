package be.kdg.dammen.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Toont de dialoogvensters van de applicatie. Alle presenters gebruiken deze klasse,
 * zo zien alle boodschappen aan de speler er hetzelfde uit.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class DialogHelper {

    private DialogHelper() {
        // Deze klasse bevat enkel hulpmethodes en wordt nooit aangemaakt.
    }

    /**
     * Toont een foutboodschap, bijvoorbeeld bij een ongeldige zet.
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Toont een gewone mededeling, bijvoorbeeld dat er een dam gehaald werd.
     */
    public static void showInformation(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Stelt een ja/nee-vraag aan de speler.
     *
     * @return true als de speler op OK klikt
     */
    public static boolean askConfirmation(String title, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(question);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == ButtonType.OK;
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
