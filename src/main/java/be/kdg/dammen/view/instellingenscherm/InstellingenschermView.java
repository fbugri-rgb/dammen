package be.kdg.dammen.view.instellingenscherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Het instellingenscherm. Hier kiest de speler de kleuren van het bord en van de
 * schijven, en zet hij het geluid aan of uit.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class InstellingenschermView extends BorderPane {

    private static final int LABEL_WIDTH = 200;

    private ColorPicker lightSquarePicker;
    private ColorPicker darkSquarePicker;
    private ColorPicker whitePiecePicker;
    private ColorPicker blackPiecePicker;
    private CheckBox soundCheckBox;
    private Button saveButton;
    private Button defaultsButton;
    private Button cancelButton;

    public InstellingenschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        lightSquarePicker = new ColorPicker();
        darkSquarePicker = new ColorPicker();
        whitePiecePicker = new ColorPicker();
        blackPiecePicker = new ColorPicker();
        soundCheckBox = new CheckBox("Geluid aan");
        saveButton = new Button("Bewaren");
        saveButton.getStyleClass().add("main-button");
        defaultsButton = new Button("Standaardkleuren");
        cancelButton = new Button("Annuleren");
    }

    private void layoutNodes() {
        Label titleLabel = new Label("Instellingen");
        titleLabel.getStyleClass().add("screen-title");
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        setTop(titleBox);
        setCenter(createCenterBox());
        setBottom(createButtonBox());
        setPadding(new Insets(10, 25, 10, 25));
        getStyleClass().add("settings-screen");
    }

    private VBox createCenterBox() {
        Label explanationLabel = new Label(
                "Deze instellingen worden bewaard en blijven ook na het afsluiten behouden.");
        explanationLabel.getStyleClass().add("message-label");
        explanationLabel.setWrapText(true);
        VBox centerBox = new VBox(15, createColorGrid(), soundCheckBox, explanationLabel);
        centerBox.setPadding(new Insets(10, 20, 10, 20));
        return centerBox;
    }

    private GridPane createColorGrid() {
        GridPane colorGrid = new GridPane();
        colorGrid.setHgap(20);
        colorGrid.setVgap(12);
        colorGrid.setPadding(new Insets(15));
        colorGrid.getStyleClass().add("name-grid");
        addColorRow(colorGrid, 0, "Lichte velden:", lightSquarePicker);
        addColorRow(colorGrid, 1, "Donkere velden:", darkSquarePicker);
        addColorRow(colorGrid, 2, "Witte schijven:", whitePiecePicker);
        addColorRow(colorGrid, 3, "Zwarte schijven:", blackPiecePicker);
        return colorGrid;
    }

    private void addColorRow(GridPane grid, int row, String text, ColorPicker picker) {
        Label label = new Label(text);
        label.setPrefWidth(LABEL_WIDTH);
        picker.setPrefWidth(LABEL_WIDTH);
        grid.add(label, 0, row);
        grid.add(picker, 1, row);
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(12, saveButton, defaultsButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));
        return buttonBox;
    }

    /**
     * Zet de vier kleurkiezers en het vinkje op de meegegeven waarden.
     */
    void showSettings(Color lightSquare, Color darkSquare, Color whitePiece,
                      Color blackPiece, boolean soundOn) {
        lightSquarePicker.setValue(lightSquare);
        darkSquarePicker.setValue(darkSquare);
        whitePiecePicker.setValue(whitePiece);
        blackPiecePicker.setValue(blackPiece);
        soundCheckBox.setSelected(soundOn);
    }

    Color getLightSquareColor() {
        return lightSquarePicker.getValue();
    }

    Color getDarkSquareColor() {
        return darkSquarePicker.getValue();
    }

    Color getWhitePieceColor() {
        return whitePiecePicker.getValue();
    }

    Color getBlackPieceColor() {
        return blackPiecePicker.getValue();
    }

    boolean isSoundSelected() {
        return soundCheckBox.isSelected();
    }

    Button getSaveButton() {
        return saveButton;
    }

    Button getDefaultsButton() {
        return defaultsButton;
    }

    Button getCancelButton() {
        return cancelButton;
    }
}
