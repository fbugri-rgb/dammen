package be.kdg.dammen.model;

/**
 * Eigen unchecked exception voor alle fouten die specifiek zijn voor het damspel:
 * een ongeldige zet, een spel dat niet opgeslagen of geladen kan worden, ...
 * De boodschap is steeds leesbaar voor de speler.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class DraughtsException extends RuntimeException {

    public DraughtsException(String message) {
        super(message);
    }

    public DraughtsException(String message, Throwable cause) {
        super(message, cause);
    }
}
