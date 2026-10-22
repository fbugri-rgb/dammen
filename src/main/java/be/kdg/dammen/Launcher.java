package be.kdg.dammen;

/**
 * Startpunt dat overal werkt zonder extra VM-opties.
 * <p>
 * Java weigert een klasse die overerft van {@code Application} rechtstreeks te
 * starten zolang JavaFX niet als module geladen is ("JavaFX runtime components are
 * missing"). Deze klasse erft niet over van {@code Application}, dus die controle
 * gebeurt niet, en JavaFX wordt gewoon van het klassenpad geladen. Zo kan iedereen
 * het spel starten met de groene pijl, op Windows, macOS en Linux.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Launcher {

    public static void main(String[] args) {
        Main.main(args);
    }
}
