package be.kdg.dammen.model;

import java.io.File;

/**
 * Bepaalt in welke map het spel zijn bestanden (topscores, instellingen) bewaart.
 * <p>
 * Standaard is dat de huidige map, zoals bij het starten vanuit IntelliJ. De
 * kant-en-klare versies voor Windows, macOS en Linux geven de eigenschap
 * {@code dammen.data.dir} mee (bijvoorbeeld {@code ~/Dammen}), omdat de map waarin
 * zo'n programma start niet altijd beschrijfbaar is. Een {@code ~} vooraan staat voor
 * de thuismap van de gebruiker.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public final class DataFolder {

    private static final String PROPERTY = "dammen.data.dir";

    private DataFolder() {
    }

    public static File file(String fileName) {
        String folder = System.getProperty(PROPERTY);
        if (folder == null || folder.isBlank()) {
            return new File(fileName);
        }
        if (folder.startsWith("~")) {
            folder = System.getProperty("user.home") + folder.substring(1);
        }
        File directory = new File(folder);
        directory.mkdirs();
        return new File(directory, fileName);
    }
}
