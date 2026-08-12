package be.kdg.dammen.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test of de spelregels uit het tekstbestand gelezen kunnen worden.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class RulesReaderTest {

    @Test
    @DisplayName("De spelregels worden uit het bestand gelezen")
    void rulesAreReadFromTheFile() {
        RulesReader reader = new RulesReader();

        String rules = reader.readRules();

        assertFalse(rules.isBlank(), "de spelregels mogen niet leeg zijn");
        assertTrue(rules.contains("SLAAN IS VERPLICHT"),
                "de belangrijkste regel moet erin staan");
        assertTrue(rules.contains("DE DAM"));
        assertTrue(rules.length() > 500, "het bestand bevat meer dan een paar regels tekst");
    }
}
