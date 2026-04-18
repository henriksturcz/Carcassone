package Carcassone.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** A vegeredmeny kepernyo */
public class ResultScreen {

    private final VBox root;

    /**
     * A vegeredmeny kepernyo elemei
     *
     * @param playerNames  a jatekosok nevei
     * @param playerScores a jatekosok vegso pontszamai
     */
    public ResultScreen(String[] playerNames, int[] playerScores) {
        Label title = new Label("Jatek vege!");
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        int maxScore = 0;
        String winner = "";
        for (int i = 0; i < playerScores.length; i++) {
            if (playerScores[i] > maxScore) {
                maxScore = playerScores[i];
                winner = playerNames[i];
            }
        }

        Label winnerLabel = new Label("Gyoztes: " + winner + " (" + maxScore + " pont)");
        winnerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        VBox scoreList = new VBox(8);
        scoreList.setAlignment(Pos.CENTER);
        for (int i = 0; i < playerNames.length; i++) {
            Label row = new Label((i + 1) + ".  " + playerNames[i] + "  —  " + playerScores[i] + " pont");
            row.setStyle("-fx-font-size: 15px; -fx-text-fill: #D4C4A0;");
            scoreList.getChildren().add(row);
        }

        Button newGameButton = new Button("Uj jatek");
        newGameButton.setStyle("-fx-font-size: 14px;");
        newGameButton.setOnAction(e -> SceneManager.showLobby("Jatekos"));

        Button exitButton = new Button("Kilepes");
        exitButton.setStyle("-fx-font-size: 14px;");
        exitButton.setOnAction(e -> javafx.application.Platform.exit());

        HBox buttons = new HBox(20, newGameButton, exitButton);
        buttons.setAlignment(Pos.CENTER);

        root = new VBox(25, title, winnerLabel, scoreList, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #3B2010;");
    }

    /** Get fuggveny */
    public VBox getRoot() {
        return root;
    }
}
