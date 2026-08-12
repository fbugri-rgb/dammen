package be.kdg.dammen;

/**
 * Startklasse van het programma.
 * <p>
 * Java weigert een klasse te starten die overerft van Application zolang JavaFX niet
 * als module geladen is. Deze klasse erft niet over van Application en start het spel
 * daarom altijd op, ook gewoon met de groene pijl in IntelliJ.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class DammenLauncher {

    public static void main(String[] args) {
        DammenMain.main(args);
    }
}
