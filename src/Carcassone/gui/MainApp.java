package Carcassone.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * A JavaFX alkalmazas belepo pontja
 * Elinditja az ablakot es betolti az elso kepernyot
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Carcassonne");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Carcassonne");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
