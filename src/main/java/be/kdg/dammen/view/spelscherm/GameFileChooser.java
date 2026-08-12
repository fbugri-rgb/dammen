package be.kdg.dammen.view.spelscherm;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Vraagt aan de speler in welk bestand een spel bewaard of uit welk bestand een spel
 * geopend moet worden. Zo staat de instelling van het bestandsvenster op één plaats.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class GameFileChooser {

    private static final String EXTENSION_DESCRIPTION = "Damspel (*.dam)";
    private static final String EXTENSION = "*.dam";
    private static final String DEFAULT_FILE_NAME = "mijn-damspel.dam";

    /**
     * @return het gekozen bestand, of null als de speler annuleert
     */
    public File askSaveFile(Window owner) {
        FileChooser fileChooser = createFileChooser("Spel opslaan");
        fileChooser.setInitialFileName(DEFAULT_FILE_NAME);
        return fileChooser.showSaveDialog(owner);
    }

    /**
     * @return het gekozen bestand, of null als de speler annuleert
     */
    public File askOpenFile(Window owner) {
        return createFileChooser("Spel openen").showOpenDialog(owner);
    }

    private FileChooser createFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(EXTENSION_DESCRIPTION, EXTENSION));
        return fileChooser;
    }
}
