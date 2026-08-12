package be.kdg.dammen.model;

/**
 * Een speler van het damspel: een naam en de kleur waarmee hij speelt.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Player {

    private static final int MAX_NAME_LENGTH = 20;

    private final String name;
    private final PieceColor color;

    public Player(String name, PieceColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Een speler heeft een kleur nodig.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Vul een naam in voor beide spelers.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Een naam mag maximaal " + MAX_NAME_LENGTH + " tekens lang zijn.");
        }
        this.name = name.trim();
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public PieceColor getColor() {
        return color;
    }
}
