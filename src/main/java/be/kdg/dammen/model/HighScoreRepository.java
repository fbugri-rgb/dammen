package be.kdg.dammen.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bewaart de topscores in het bestand highscores.txt en leest ze terug in.
 * Elke regel in het bestand ziet er zo uit: naam;aantalZetten;datum
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HighScoreRepository {

    private static final String FILE_NAME = "highscores.txt";
    private static final String SEPARATOR = ";";
    private static final int MAX_HIGH_SCORES = 10;
    private static final int NUMBER_OF_FIELDS = 3;

    private final File file;

    public HighScoreRepository() {
        this(DataFolder.file(FILE_NAME));
    }

    /**
     * Laat toe om een ander bestand te gebruiken. Zo kunnen de testklassen met een
     * eigen bestand werken en blijven de echte topscores ongemoeid.
     */
    public HighScoreRepository(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Er is een bestand nodig voor de topscores.");
        }
        this.file = file;
    }

    /**
     * Leest de topscores in, gesorteerd van beste naar slechtste. Bestaat het bestand
     * nog niet, dan is de lijst gewoon leeg.
     *
     * @return de lijst met topscores
     * @throws DraughtsException als het bestand bestaat maar niet gelezen kan worden
     */
    public List<HighScore> readHighScores() {
        List<HighScore> highScores = new ArrayList<>();
        if (!file.exists()) {
            return highScores;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                addLine(highScores, line);
                line = reader.readLine();
            }
        } catch (IOException exception) {
            throw new DraughtsException("De topscores konden niet gelezen worden.", exception);
        }
        Collections.sort(highScores);
        return highScores;
    }

    /**
     * Zet een lijn uit het bestand om naar een topscore. Onleesbare lijnen worden
     * overgeslagen, zo blijft de applicatie werken bij een beschadigd bestand.
     */
    private void addLine(List<HighScore> highScores, String line) {
        String[] parts = line.split(SEPARATOR);
        if (parts.length != NUMBER_OF_FIELDS) {
            return;
        }
        try {
            highScores.add(new HighScore(parts[0], Integer.parseInt(parts[1]), parts[2]));
        } catch (IllegalArgumentException exception) {
            // Een lijn met foute gegevens laten we bewust weg uit de lijst.
        }
    }

    /**
     * Voegt een nieuwe topscore toe en bewaart enkel de beste tien.
     *
     * @throws DraughtsException als de topscores niet weggeschreven kunnen worden
     */
    public void addHighScore(String playerName, int numberOfMoves) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Een topscore heeft een spelernaam nodig.");
        }
        List<HighScore> highScores = readHighScores();
        String safeName = playerName.replace(SEPARATOR, " ");
        highScores.add(new HighScore(safeName, numberOfMoves, getTodayAsText()));
        Collections.sort(highScores);
        while (highScores.size() > MAX_HIGH_SCORES) {
            highScores.remove(highScores.size() - 1);
        }
        writeHighScores(highScores);
    }

    private void writeHighScores(List<HighScore> highScores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < highScores.size(); i++) {
                HighScore highScore = highScores.get(i);
                writer.println(highScore.getPlayerName() + SEPARATOR
                        + highScore.getNumberOfMoves() + SEPARATOR + highScore.getDate());
            }
        } catch (IOException exception) {
            throw new DraughtsException("De topscores konden niet bewaard worden.", exception);
        }
    }

    private String getTodayAsText() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
