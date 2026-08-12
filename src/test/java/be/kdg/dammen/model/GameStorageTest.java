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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testen voor het opslaan en terug inlezen van een spelsituatie.
 * De bestanden worden in een tijdelijke map gezet, zodat de echte bestanden van de
 * speler niet overschreven worden.
 *
 * @author Farok Bugri
 * @version 1.0
 */
class GameStorageTest {

    @TempDir
    Path temporaryFolder;

    private GameStorage storage;
    private DraughtsGame game;

    @BeforeEach
    void setUp() {
        storage = new GameStorage();
        game = new DraughtsGame("Farok", "Jan");
    }

    private File newFile(String name) {
        return temporaryFolder.resolve(name).toFile();
    }

    @Test
    @DisplayName("Een bewaard spel wordt exact terug ingelezen")
    void savedGameIsLoadedBack() {
        game.movePiece(new Position(6, 3), new Position(5, 2));
        game.movePiece(new Position(3, 2), new Position(4, 1));
        File file = newFile("spel.dam");

        storage.save(game, file);
        DraughtsGame loaded = storage.load(file);

        assertTrue(file.exists());
        assertEquals("Farok", loaded.getPlayer(PieceColor.WHITE).getName());
        assertEquals("Jan", loaded.getPlayer(PieceColor.BLACK).getName());
        assertEquals(PieceColor.WHITE, loaded.getCurrentColor());
        assertEquals(2, loaded.getMoveCount());
        assertNotNull(loaded.getBoard().getPiece(new Position(5, 2)));
        assertTrue(loaded.getBoard().isEmpty(new Position(6, 3)));
        assertEquals(20, loaded.getBoard().countPieces(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Na het inlezen kan er gewoon verder gespeeld worden")
    void loadedGameCanBePlayedFurther() {
        game.movePiece(new Position(6, 3), new Position(5, 2));
        File file = newFile("verder.dam");
        storage.save(game, file);

        DraughtsGame loaded = storage.load(file);
        loaded.movePiece(new Position(3, 2), new Position(4, 1));

        assertEquals(2, loaded.getMoveCount());
        assertEquals(PieceColor.WHITE, loaded.getCurrentColor());
    }

    @Test
    @DisplayName("Ook dammen worden juist bewaard")
    void kingsAreSavedCorrectly() {
        game.getBoard().clear();
        game.getBoard().setPiece(new Position(0, 1), new Piece(PieceColor.WHITE, PieceType.KING));
        game.getBoard().setPiece(new Position(9, 8), new Piece(PieceColor.BLACK));
        File file = newFile("dam.dam");

        storage.save(game, file);
        DraughtsGame loaded = storage.load(file);

        assertTrue(loaded.getBoard().getPiece(new Position(0, 1)).isKing());
        assertFalse(loaded.getBoard().getPiece(new Position(9, 8)).isKing());
    }

    @Test
    @DisplayName("Er wordt onthouden of het spel tegen de computer ging")
    void againstComputerIsRemembered() {
        game.setAgainstComputer(true);
        File file = newFile("computer.dam");

        storage.save(game, file);
        DraughtsGame loaded = storage.load(file);

        assertTrue(loaded.isAgainstComputer());
    }

    @Test
    @DisplayName("Een bestand van een ander programma wordt geweigerd")
    void fileFromAnotherProgramIsRefused() throws IOException {
        File file = newFile("fout.txt");
        PrintWriter writer = new PrintWriter(file);
        writer.println("dit is geen damspel");
        writer.close();

        DraughtsException exception = assertThrows(DraughtsException.class,
                () -> storage.load(file));
        assertTrue(exception.getMessage().contains("geen bestand van dit damspel"));
    }

    @Test
    @DisplayName("Een half bestand wordt geweigerd")
    void incompleteFileIsRefused() throws IOException {
        File file = newFile("half.dam");
        PrintWriter writer = new PrintWriter(file);
        writer.println("Dammen-spelbestand v1");
        writer.println("Farok");
        writer.close();

        assertThrows(DraughtsException.class, () -> storage.load(file));
    }

    @Test
    @DisplayName("Een bestand met een onbekend teken op het bord wordt geweigerd")
    void fileWithStrangeCharacterIsRefused() throws IOException {
        File file = newFile("raar.dam");
        PrintWriter writer = new PrintWriter(file);
        writer.println("Dammen-spelbestand v1");
        writer.println("Farok");
        writer.println("Jan");
        writer.println("WHITE");
        for (int row = 0; row < Board.SIZE; row++) {
            writer.println("XXXXXXXXXX");
        }
        writer.println("0");
        writer.close();

        assertThrows(DraughtsException.class, () -> storage.load(file));
    }

    @Test
    @DisplayName("Een bestand dat niet bestaat, geeft een duidelijke fout")
    void missingFileGivesAClearError() {
        File file = newFile("bestaatniet.dam");
        assertThrows(DraughtsException.class, () -> storage.load(file));
    }

    @Test
    @DisplayName("Opslaan zonder spel of zonder bestand wordt geweigerd")
    void savingWithoutGameOrFileIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> storage.save(null, newFile("x.dam")));
        assertThrows(IllegalArgumentException.class, () -> storage.save(game, null));
        assertThrows(IllegalArgumentException.class, () -> storage.load(null));
    }
}
