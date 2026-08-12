package be.kdg.dammen.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Bewaart de instellingen in het bestand instellingen.properties en leest ze terug in
 * bij de volgende keer dat het spel opstart.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class SettingsRepository {

    private static final String FILE_NAME = "instellingen.properties";
    private static final String LIGHT_SQUARE_KEY = "kleur.veld.licht";
    private static final String DARK_SQUARE_KEY = "kleur.veld.donker";
    private static final String WHITE_PIECE_KEY = "kleur.schijf.wit";
    private static final String BLACK_PIECE_KEY = "kleur.schijf.zwart";
    private static final String SOUND_KEY = "geluid.aan";

    private final File file;

    public SettingsRepository() {
        this(new File(FILE_NAME));
    }

    /**
     * Laat toe om een ander bestand te gebruiken. Zo kunnen de testklassen met een
     * eigen bestand werken en blijven de echte instellingen ongemoeid.
     */
    public SettingsRepository(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Er is een bestand nodig voor de instellingen.");
        }
        this.file = file;
    }

    /**
     * Leest de bewaarde instellingen. Bestaat het bestand nog niet, of staat er iets
     * fout in, dan krijgt de speler gewoon de standaardinstellingen.
     *
     * @return de instellingen
     */
    public Settings readSettings() {
        Settings settings = new Settings();
        if (!file.exists()) {
            return settings;
        }
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (IOException exception) {
            return settings;
        }
        fillSettings(settings, properties);
        return settings;
    }

    /**
     * Zet de gelezen waarden in de instellingen. Elke kleur wordt apart nagekeken,
     * zodat één foute kleur in het bestand de andere kleuren niet meesleept.
     */
    private void fillSettings(Settings settings, Properties properties) {
        settings.setLightSquareColor(
                readColor(properties, LIGHT_SQUARE_KEY, Settings.DEFAULT_LIGHT_SQUARE));
        settings.setDarkSquareColor(
                readColor(properties, DARK_SQUARE_KEY, Settings.DEFAULT_DARK_SQUARE));
        settings.setWhitePieceColor(
                readColor(properties, WHITE_PIECE_KEY, Settings.DEFAULT_WHITE_PIECE));
        settings.setBlackPieceColor(
                readColor(properties, BLACK_PIECE_KEY, Settings.DEFAULT_BLACK_PIECE));
        settings.setSoundOn(Boolean.parseBoolean(properties.getProperty(SOUND_KEY, "true")));
    }

    /**
     * Leest één kleur uit het bestand.
     *
     * @return de kleur uit het bestand, of de standaardkleur als ze niet klopt
     */
    private String readColor(Properties properties, String key, String defaultColor) {
        String color = properties.getProperty(key, defaultColor);
        if (Settings.isValidColor(color)) {
            return color;
        }
        return defaultColor;
    }

    /**
     * Schrijft de instellingen weg zodat ze bij de volgende run terug ingelezen worden.
     *
     * @throws DraughtsException als het bestand niet geschreven kan worden
     */
    public void saveSettings(Settings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Er zijn instellingen nodig om te bewaren.");
        }
        Properties properties = new Properties();
        properties.setProperty(LIGHT_SQUARE_KEY, settings.getLightSquareColor());
        properties.setProperty(DARK_SQUARE_KEY, settings.getDarkSquareColor());
        properties.setProperty(WHITE_PIECE_KEY, settings.getWhitePieceColor());
        properties.setProperty(BLACK_PIECE_KEY, settings.getBlackPieceColor());
        properties.setProperty(SOUND_KEY, String.valueOf(settings.isSoundOn()));
        try (FileWriter writer = new FileWriter(file)) {
            properties.store(writer, "Instellingen van het damspel");
        } catch (IOException exception) {
            throw new DraughtsException("De instellingen konden niet bewaard worden.", exception);
        }
    }
}
