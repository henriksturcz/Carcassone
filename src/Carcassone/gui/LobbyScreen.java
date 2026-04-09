package Carcassone.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A lobby kepernyo
 * vagy uj szoba letrehozasa vagy meglevohoz csatlakozas
 */
public class LobbyScreen {

    private final VBox root;
    private final ListView<String> roomList;

    /**
     * A lobby kepernyo elemei
     *
     * @param username a bejelentkezett felhasznalonev
     */
    public LobbyScreen(String username) {
        Label title = new Label("Lobby");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Udvozlunk, " + username + "!");
        welcomeLabel.setStyle("-fx-font-size: 14px;");

        Label roomsLabel = new Label("Nyitott jatekszobak:");

        roomList = new ListView<>();
        roomList.setPrefHeight(300);
        roomList.setPrefWidth(400);

        // TODO: halozatrol toltodik fel
        roomList.getItems().addAll(
                "Szoba #1 — 2/5 jatekos",
                "Szoba #2 — 4/5 jatekos"
        );

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: red;");

        Button joinButton = new Button("Csatlakozas");
        joinButton.setOnAction(e -> {
            String selected = roomList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Valassz ki egy szobat!");
                return;
            }
            // TODO: csatlakozas kuldese a szervernek
            statusLabel.setText("Csatlakozas: " + selected);
        });

        Button createButton = new Button("Uj szoba");
        createButton.setOnAction(e -> {
            // TODO: uj szoba letrehozasa
            statusLabel.setText("Uj szoba letrehozasa...");
        });

        Button backButton = new Button("Vissza");
        backButton.setOnAction(e -> SceneManager.showLogin());

        HBox buttons = new HBox(10, joinButton, createButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getChildren().addAll(
                title,
                welcomeLabel,
                roomsLabel,
                roomList,
                statusLabel,
                buttons
        );
    }

    /** Get fuggveny */
    public VBox getRoot() {
        return root;
    }
}
