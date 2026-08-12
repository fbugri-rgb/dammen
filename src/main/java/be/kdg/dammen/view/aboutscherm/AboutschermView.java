package be.kdg.dammen.view.aboutscherm;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Het aboutscherm: wie maakte deze applicatie en waarover gaat ze.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class AboutschermView extends BorderPane {

    private Label titleLabel;
    private Button closeButton;

    public AboutschermView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        titleLabel = new Label("Over dit spel");
        titleLabel.getStyleClass().add("screen-title");
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
        setCenter(createInformationBox());
        setBottom(buttonBox);
        setPadding(new Insets(10, 25, 10, 25));
        getStyleClass().add("about-screen");
    }

    private VBox createInformationBox() {
        VBox informationBox = new VBox(10);
        informationBox.setAlignment(Pos.CENTER_LEFT);
        informationBox.setPadding(new Insets(10, 20, 10, 20));
        informationBox.getChildren().addAll(
                createSubtitle("Dammen"),
                new Label("Een damspel voor twee spelers op een bord van 10 op 10 velden."),
                new Separator(),
                createSubtitle("Gemaakt door"),
                new Label("Farok Bugri"),
                new Label("Karel de Grote Hogeschool - Toegepaste Informatica"),
                new Label("Project Java, tweede zittijd"),
                new Separator(),
                createSubtitle("Over deze versie"),
                new Label("Versie 1.0"),
                new Label("Gebouwd met Java 21 en JavaFX, volgens het Model-View-Presenter patroon."),
                new Label("De meerslagregel is bewust niet ingebouwd: je kiest zelf welke slagzet je speelt."));
        return informationBox;
    }

    private Label createSubtitle(String text) {
        Label subtitle = new Label(text);
        subtitle.getStyleClass().add("panel-title");
        return subtitle;
    }

    Button getCloseButton() {
        return closeButton;
    }
}
