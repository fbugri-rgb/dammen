package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de instellingen: de standaardwaarden, het bewaren en het terug inlezen
 * bij een volgende run.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class SettingsRepositoryTest {

    @TempDir
    Path temporaryFolder;

    private File file;
    private SettingsRepository repository;

    @BeforeEach
    void setUp() {
        file = temporaryFolder.resolve("instellingen.properties").toFile();
        repository = new SettingsRepository(file);
    }

    @Test
    @DisplayName("Nieuwe instellingen hebben de standaardkleuren en geluid aan")
    void newSettingsHaveDefaults() {
        Settings settings = new Settings();

        assertEquals(Settings.DEFAULT_LIGHT_SQUARE, settings.getLightSquareColor());
        assertEquals(Settings.DEFAULT_DARK_SQUARE, settings.getDarkSquareColor());
        assertEquals(Settings.DEFAULT_WHITE_PIECE, settings.getWhitePieceColor());
        assertEquals(Settings.DEFAULT_BLACK_PIECE, settings.getBlackPieceColor());
        assertTrue(settings.isSoundOn());
    }

    @Test
    @DisplayName("Zonder bestand krijgt de speler de standaardinstellingen")
    void defaultsAreUsedWithoutFile() {
        assertFalse(file.exists());

        Settings settings = repository.readSettings();

        assertEquals(Settings.DEFAULT_DARK_SQUARE, settings.getDarkSquareColor());
        assertTrue(settings.isSoundOn());
    }

    @Test
    @DisplayName("De instellingen worden bewaard en bij een volgende run terug ingelezen")
    void settingsAreSavedAndReadBack() {
        Settings settings = new Settings();
        settings.setLightSquareColor("#DFF0E4");
        settings.setDarkSquareColor("#2F6B4F");
        settings.setWhitePieceColor("#F5F5F5");
        settings.setBlackPieceColor("#B03030");
        settings.setSoundOn(false);

        repository.saveSettings(settings);
        Settings loaded = new SettingsRepository(file).readSettings();

        assertTrue(file.exists());
        assertEquals("#DFF0E4", loaded.getLightSquareColor());
        assertEquals("#2F6B4F", loaded.getDarkSquareColor());
        assertEquals("#F5F5F5", loaded.getWhitePieceColor());
        assertEquals("#B03030", loaded.getBlackPieceColor());
        assertFalse(loaded.isSoundOn());
    }

    @Test
    @DisplayName("Een kleur wordt altijd in hoofdletters bewaard")
    void colorIsStoredInCapitals() {
        Settings settings = new Settings();
        settings.setDarkSquareColor("#abcdef");

        assertEquals("#ABCDEF", settings.getDarkSquareColor());
    }

    @Test
    @DisplayName("Een kleur die niet klopt, wordt geweigerd")
    void invalidColorIsRefused() {
        Settings settings = new Settings();

        assertThrows(IllegalArgumentException.class, () -> settings.setLightSquareColor("rood"));
        assertThrows(IllegalArgumentException.class, () -> settings.setLightSquareColor("#12345"));
        assertThrows(IllegalArgumentException.class, () -> settings.setLightSquareColor("123456"));
        assertThrows(IllegalArgumentException.class, () -> settings.setLightSquareColor("#GGGGGG"));
        assertThrows(IllegalArgumentException.class, () -> settings.setLightSquareColor(null));
    }

    @Test
    @DisplayName("Een beschadigd bestand geeft gewoon de standaardinstellingen")
    void brokenFileGivesDefaults() throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.println("kleur.veld.donker=dit is geen kleur");
        writer.println("geluid.aan=misschien");
        writer.close();

        Settings settings = repository.readSettings();

        assertEquals(Settings.DEFAULT_DARK_SQUARE, settings.getDarkSquareColor());
        assertFalse(settings.isSoundOn(), "alles behalve 'true' betekent geluid uit");
    }

    @Test
    @DisplayName("Instellingen bewaren zonder instellingen wordt geweigerd")
    void savingWithoutSettingsIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveSettings(null));
        assertThrows(IllegalArgumentException.class, () -> new SettingsRepository(null));
    }
}
