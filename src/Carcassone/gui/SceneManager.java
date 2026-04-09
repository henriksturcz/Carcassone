package Carcassone.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A kepernyo valtasokat kezeli
 */
public class SceneManager {

    private static Stage primaryStage;

    /**
     * Beallitja a fo ablakot
     * A MainApp start() metodusabol hivom
     *
     * @param stage a JavaFX fo ablak
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Atvallt a bejelentkezo kepernyore
     */
    public static void showLogin() {
        LoginScreen screen = new LoginScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
        primaryStage.setTitle("Carcassonne");
        primaryStage.show();
    }

    /**
     * Atvallt a lobby kepernyore
     *
     * @param username a bejelentkezett felhasznalonev
     */
    public static void showLobby(String username) {
        LobbyScreen screen = new LobbyScreen(username);
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
        primaryStage.setTitle("Lobbys");
        primaryStage.show();
    }
}
