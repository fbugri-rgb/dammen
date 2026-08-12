package be.kdg.dammen.model;

/**
 * De instellingen die de speler zelf kan kiezen: de kleuren van het bord en van de
 * schijven, en of het geluid aan staat.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class Settings {

    public static final String DEFAULT_LIGHT_SQUARE = "#E8D5B7";
    public static final String DEFAULT_DARK_SQUARE = "#8B5E3C";
    public static final String DEFAULT_WHITE_PIECE = "#FBF6EC";
    public static final String DEFAULT_BLACK_PIECE = "#2B2B2B";

    private static final int COLOR_TEXT_LENGTH = 7;

    private String lightSquareColor;
    private String darkSquareColor;
    private String whitePieceColor;
    private String blackPieceColor;
    private boolean soundOn;

    /**
     * Maakt de instellingen aan zoals ze standaard zijn.
     */
    public Settings() {
        this.lightSquareColor = DEFAULT_LIGHT_SQUARE;
        this.darkSquareColor = DEFAULT_DARK_SQUARE;
        this.whitePieceColor = DEFAULT_WHITE_PIECE;
        this.blackPieceColor = DEFAULT_BLACK_PIECE;
        this.soundOn = true;
    }

    public String getLightSquareColor() {
        return lightSquareColor;
    }

    public void setLightSquareColor(String color) {
        this.lightSquareColor = checkColor(color);
    }

    public String getDarkSquareColor() {
        return darkSquareColor;
    }

    public void setDarkSquareColor(String color) {
        this.darkSquareColor = checkColor(color);
    }

    public String getWhitePieceColor() {
        return whitePieceColor;
    }

    public void setWhitePieceColor(String color) {
        this.whitePieceColor = checkColor(color);
    }

    public String getBlackPieceColor() {
        return blackPieceColor;
    }

    public void setBlackPieceColor(String color) {
        this.blackPieceColor = checkColor(color);
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    /**
     * Een kleur wordt bewaard als tekst, bijvoorbeeld "#8B5E3C".
     *
     * @return dezelfde kleur, in hoofdletters
     * @throws IllegalArgumentException als de tekst geen geldige kleur is
     */
    private String checkColor(String color) {
        if (color == null || color.length() != COLOR_TEXT_LENGTH || !color.startsWith("#")) {
            throw new IllegalArgumentException("Dit is geen geldige kleur: " + color);
        }
        for (int i = 1; i < color.length(); i++) {
            if (Character.digit(color.charAt(i), 16) < 0) {
                throw new IllegalArgumentException("Dit is geen geldige kleur: " + color);
            }
        }
        return color.toUpperCase();
    }
}
