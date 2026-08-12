package be.kdg.dammen;

import be.kdg.dammen.view.ScreenHelper;
import be.kdg.dammen.view.startscherm.StartschermPresenter;
import be.kdg.dammen.view.startscherm.StartschermView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Start de damapplicatie op met het startscherm.
 *
 * @author Farok Bugri
 * @version 1.0
 */
public class DammenMain extends Application {

    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 780;
    private static final double MINIMUM_WIDTH = 720;
    private static final double MINIMUM_HEIGHT = 560;

    @Override
    public void start(Stage primaryStage) {
        StartschermView view = new StartschermView();
        StartschermPresenter presenter = new StartschermPresenter(view);
        Scene scene = new Scene(view, WINDOW_WIDTH, WINDOW_HEIGHT);
        ScreenHelper.applyStyleSheet(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dammen");
        primaryStage.setMinWidth(MINIMUM_WIDTH);
        primaryStage.setMinHeight(MINIMUM_HEIGHT);
        presenter.addWindowEventHandlers();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
