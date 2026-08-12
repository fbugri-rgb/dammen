package be.kdg.dammen.view.helpscherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Het helpscherm met de spelregels. De regels zelf worden door de presenter uit een
 * tekstbestand gehaald.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class HelpschermView extends BorderPane {

    private Label titleLabel;
    private TextArea rulesTextArea;
    private Button closeButton;

    public HelpschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        titleLabel = new Label("Spelregels");
        titleLabel.getStyleClass().add("screen-title");
        rulesTextArea = new TextArea();
        rulesTextArea.setEditable(false);
        rulesTextArea.setWrapText(true);
        closeButton = new Button("Sluiten");
    }

    private void layoutNodes() {
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(15));
        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 15, 15, 15));
        setTop(titleBox);
        setCenter(rulesTextArea);
        setBottom(buttonBox);
        setPadding(new Insets(10, 20, 10, 20));
        getStyleClass().add("help-screen");
    }

    TextArea getRulesTextArea() {
        return rulesTextArea;
    }

    Button getCloseButton() {
        return closeButton;
    }
}
