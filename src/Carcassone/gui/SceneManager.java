package Carcassone.gui;

import Carcassone.network.client.ServerConnection;
import Carcassone.network.shared.Message;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** A kepernyo valtasokat kezeli */
public class SceneManager {

    private static Stage primaryStage;
    private static ServerConnection connection;
    private static String username;

    /**
     * Beallitja a fo ablakot
     * A MainApp start() metodusabol hivom
     *
     * @param stage a JavaFX fo ablak
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /** Beallitja a szerver kapcsolatot */
    public static void setConnection(ServerConnection conn) {
        connection = conn;
    }

    /** Visszaadja a szerver kapcsolatot */
    public static ServerConnection getConnection() {
        return connection;
    }

    /** Beallitja a bejelentkezett felhasznalonevet */
    public static void setUsername(String name) {
        username = name;
    }

    /** Visszaadja a bejelentkezett felhasznalonevet */
    public static String getUsername() {
        return username;
    }

    /** Atvallt a bejelentkezo kepernyore */
    public static void showLogin() {
        LoginScreen screen = new LoginScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
        primaryStage.setTitle("Carcassonne");
        primaryStage.show();
    }

    /** Atvallt a lobby kepernyore */
    public static void showLobby(String username) {
        LobbyScreen screen = new LobbyScreen(username);
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
        primaryStage.setTitle("Carcassonne — Lobby");
        primaryStage.show();
    }



    /** Megjeleniti a jatekszabalyok kepernyo */
    public static void showRules() {
        RulesScreen screen = new RulesScreen();
        javafx.stage.Stage rulesStage = new javafx.stage.Stage();
        rulesStage.setTitle("Jatekszabalyok");
        rulesStage.setScene(new javafx.scene.Scene(screen.getRoot(), 750, 650));
        rulesStage.show();
    }

    /**
     * Atvallt a jatekpalya kepernyore az elso GAME_STATE uzenettel
     *
     * @param initialState az elso GAME_STATE uzenet amit a lobby kapott
     */
    public static void showGame(Message initialState) {
        GameScreen screen = new GameScreen(initialState);
        primaryStage.setScene(new Scene(screen.getRoot(), 1100, 700));
        primaryStage.setTitle("Carcassonne — Jatek");
        primaryStage.show();
    }

    /**
     * Atvallt a vegeredmeny kepernyore
     *
     * @param playerNames  a jatekosok nevei
     * @param playerScores a jatekosok pontszamai
     */
    public static void showResult(String[] playerNames, int[] playerScores) {
        ResultScreen screen = new ResultScreen(playerNames, playerScores);
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
        primaryStage.setTitle("Carcassonne — Eredmeny");
        primaryStage.show();
    }
}