package be.kdg.dammen.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Leest de spelregels in uit het tekstbestand spelregels.txt. Zo staan de regels niet
 * in de code en kunnen ze aangepast worden zonder het programma te herschrijven.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class RulesReader {

    private static final String RULES_FILE = "/be/kdg/dammen/view/data/spelregels.txt";

    /**
     * @return de volledige tekst van de spelregels
     * @throws DraughtsException als het bestand ontbreekt of niet leesbaar is
     */
    public String readRules() {
        StringBuilder rules = new StringBuilder();
        InputStream stream = RulesReader.class.getResourceAsStream(RULES_FILE);
        if (stream == null) {
            throw new DraughtsException("Het bestand met de spelregels werd niet gevonden.");
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            while (line != null) {
                rules.append(line).append(System.lineSeparator());
                line = reader.readLine();
            }
        } catch (IOException exception) {
            throw new DraughtsException("De spelregels konden niet gelezen worden.", exception);
        }
        return rules.toString();
    }
}
