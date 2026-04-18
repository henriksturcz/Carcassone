package Carcassone.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * A bejelentkezo kepernyo
 */
public class LoginScreen {

    private final VBox root;

    /**
     * A bejelentkezo kepernyo elemei
     */
    public LoginScreen() {
        Label title = new Label("Carcassonne");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label nameLabel = new Label("Felhasznalonev:");
        TextField nameField = new TextField();
        nameField.setPromptText("Add meg a neved...");
        nameField.setMaxWidth(300);

        Label serverLabel = new Label("Szerver cim:");
        TextField serverField = new TextField("localhost");
        serverField.setMaxWidth(300);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red;");

        Button connectButton = new Button("Csatlakozas");
        connectButton.setDefaultButton(true);
        connectButton.setOnAction(e -> {
            String username = nameField.getText().trim();
            String server = serverField.getText().trim();

            if (username.isEmpty()) {
                errorLabel.setText("A felhasznalonev nem lehet ures!");
                return;
            }
            if (server.isEmpty()) {
                errorLabel.setText("A szerver cim nem lehet ures!");
                return;
            }

            //halozati kapcsolat letrehozasa
            // Egyelore csak atlepek a lobbyba
            SceneManager.showLobby(username);
        });

        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getChildren().addAll(
                title,
                nameLabel, nameField,
                serverLabel, serverField,
                errorLabel,
                connectButton
        );
    }

    /** Get fuggveny */
    public VBox getRoot() {
        return root;
    }
}
