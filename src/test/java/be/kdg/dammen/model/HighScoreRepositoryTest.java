package be.kdg.dammen.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor de topscores: wegschrijven, terug inlezen en sorteren.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class HighScoreRepositoryTest {

    @TempDir
    Path temporaryFolder;

    private File file;
    private HighScoreRepository repository;

    @BeforeEach
    void setUp() {
        file = temporaryFolder.resolve("highscores.txt").toFile();
        repository = new HighScoreRepository(file);
    }

    @Test
    @DisplayName("Zonder bestand is de lijst met topscores leeg")
    void listIsEmptyWithoutFile() {
        assertFalse(file.exists());
        assertTrue(repository.readHighScores().isEmpty());
    }

    @Test
    @DisplayName("Een topscore wordt bewaard en terug ingelezen")
    void highScoreIsSavedAndReadBack() {
        repository.addHighScore("Farok", 42);

        List<HighScore> scores = repository.readHighScores();

        assertTrue(file.exists());
        assertEquals(1, scores.size());
        assertEquals("Farok", scores.get(0).getPlayerName());
        assertEquals(42, scores.get(0).getNumberOfMoves());
        assertFalse(scores.get(0).getDate().isBlank());
    }

    @Test
    @DisplayName("Wie met de minste zetten wint, staat bovenaan")
    void fewestMovesComesFirst() {
        repository.addHighScore("Farok", 45);
        repository.addHighScore("Jan", 30);
        repository.addHighScore("Ann", 60);

        List<HighScore> scores = repository.readHighScores();

        assertEquals(3, scores.size());
        assertEquals("Jan", scores.get(0).getPlayerName());
        assertEquals("Farok", scores.get(1).getPlayerName());
        assertEquals("Ann", scores.get(2).getPlayerName());
    }

    @Test
    @DisplayName("Er worden hoogstens tien topscores bewaard")
    void onlyTenBestScoresAreKept() {
        for (int i = 1; i <= 15; i++) {
            repository.addHighScore("Speler " + i, i);
        }

        List<HighScore> scores = repository.readHighScores();

        assertEquals(10, scores.size());
        assertEquals(1, scores.get(0).getNumberOfMoves());
        assertEquals(10, scores.get(9).getNumberOfMoves());
    }

    @Test
    @DisplayName("Een topscore zonder naam wordt geweigerd")
    void highScoreWithoutNameIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> repository.addHighScore("  ", 10));
        assertThrows(IllegalArgumentException.class, () -> repository.addHighScore(null, 10));
    }

    @Test
    @DisplayName("Een topscore zonder zetten wordt geweigerd")
    void highScoreWithoutMovesIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new HighScore("Farok", 0, "01/01/2026"));
        assertThrows(IllegalArgumentException.class, () -> new HighScore("Farok", -3, "01/01/2026"));
    }

    @Test
    @DisplayName("Een beschadigde lijn wordt overgeslagen, de rest blijft leesbaar")
    void brokenLineIsSkipped() throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.println("Farok;25;01/01/2026");
        writer.println("dit is geen topscore");
        writer.println("Jan;abc;01/01/2026");
        writer.println("Ann;15;02/01/2026");
        writer.close();

        List<HighScore> scores = repository.readHighScores();

        assertEquals(2, scores.size(), "enkel de twee goede lijnen blijven over");
        assertEquals("Ann", scores.get(0).getPlayerName());
    }

    @Test
    @DisplayName("Een puntkomma in de naam breekt het bestand niet")
    void semicolonInNameDoesNotBreakTheFile() {
        repository.addHighScore("Farok;Bugri", 20);

        List<HighScore> scores = repository.readHighScores();

        assertEquals(1, scores.size());
        assertEquals("Farok Bugri", scores.get(0).getPlayerName());
    }

    @Test
    @DisplayName("Een repository heeft een bestand nodig")
    void repositoryNeedsAFile() {
        assertThrows(IllegalArgumentException.class, () -> new HighScoreRepository(null));
    }
}
