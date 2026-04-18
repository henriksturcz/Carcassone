package Carcassone.gui;

import Carcassone.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;

import java.util.HashMap;
import java.util.Map;

/**
 * A jatekpalya kepernyo
 * Az aktualis kartyat kezeli a lerakas, forgatas, passz muveleteket.
 */
public class GameScreen {

    private static final int TILE_SIZE = 80;
    private static final int CANVAS_PADDING = 5;

    private final BorderPane root;

    /** Palya */
    private final Canvas boardCanvas;
    private final GraphicsContext gc;

    private final VBox playerPanel;

    private final Canvas previewCanvas;
    private final Label currentPlayerLabel;
    private final Button rotateButton;
    private final Button placeButton;
    private final Button passButton;

    /**
     * Allapotok
     * A forgatas eseten 0, 90, 180, 270 ertekek vannak
     */
    private final Map<Position, Tile> placedTiles = new HashMap<>();
    private Tile currentTile;
    private int currentRotation = 0;
    private Position pendingPosition = null;
    private int minX = 0, maxX = 0, minY = 0, maxY = 0;

    /** Test jatekosok */
    private final String[] playerNames = {"Player 1", "Player 2", "Player 3"};
    private final Color[] playerColors = {Color.BLUE, Color.RED, Color.GREEN};
    private final int[] playerScores = {0, 0, 0};
    private final int[] playerMeeples = {7, 7, 7};
    private int currentPlayerIndex = 0;

    /** A jatekpalya kepernyo osszes eleme */
    public GameScreen() {
        boardCanvas = new Canvas(800, 600);
        gc = boardCanvas.getGraphicsContext2D();

        ScrollPane boardScroll = new ScrollPane(boardCanvas);
        boardScroll.setPannable(true);
        boardScroll.setFitToWidth(false);
        boardScroll.setFitToHeight(false);
        boardScroll.setStyle("-fx-background: #5C3A1E;");

        boardCanvas.setOnMouseClicked(e -> {
            int col = (int) (e.getX() / TILE_SIZE) - CANVAS_PADDING + minX;
            int row = (int) (e.getY() / TILE_SIZE) - CANVAS_PADDING + minY;
            handleBoardClick(new Position(col, row));
        });

        /** Jobb oldali panel tartzalmazza a kartyakat es az akcio lehetosegeket */
        currentPlayerLabel = new Label("Soron: " + playerNames[currentPlayerIndex]);
        currentPlayerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        previewCanvas = new Canvas(120, 120);
        Label previewLabel = new Label("Aktualis kartya:");
        previewLabel.setStyle("-fx-text-fill: white;");

        rotateButton = new Button("Forgatas");
        rotateButton.setMaxWidth(Double.MAX_VALUE);
        rotateButton.setOnAction(e -> rotateTile());

        placeButton = new Button("Lerak");
        placeButton.setMaxWidth(Double.MAX_VALUE);
        placeButton.setDisable(true);
        placeButton.setOnAction(e -> placeTile());

        passButton = new Button("Passz");
        passButton.setMaxWidth(Double.MAX_VALUE);
        passButton.setOnAction(e -> passTurn());

        VBox rightPanel = new VBox(12,
                currentPlayerLabel,
                previewLabel,
                previewCanvas,
                rotateButton,
                placeButton,
                passButton
        );
        rightPanel.setPadding(new Insets(15));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #3B2010;");
        rightPanel.setPrefWidth(160);

        /**
         * Bal oldali panel
         * Itt vannak a jatekosok
         */
        playerPanel = new VBox(10);
        playerPanel.setPadding(new Insets(15));
        playerPanel.setStyle("-fx-background-color: #3B2010;");
        playerPanel.setPrefWidth(180);
        refreshPlayerPanel();

        root = new BorderPane();
        root.setCenter(boardScroll);
        root.setLeft(playerPanel);
        root.setRight(rightPanel);
        root.setStyle("-fx-background-color: #5C3A1E;");

        /**
         * Test kezdo kartya
         */
        currentTile = createTestTile("START");
        drawPreview();
        drawBoard();
    }

    //TODO eredmenyekezeles

    /**
     * A palyara valo kattintassal eltarolja a pociciot
     *
     * @param pos a kattintott racs pozicio
     */
    private void handleBoardClick(Position pos) {
        if (placedTiles.containsKey(pos)) return;
        if (!placedTiles.isEmpty() && !hasNeighbour(pos)) return;

        pendingPosition = pos;
        placeButton.setDisable(false);
        drawBoard();
        highlightPosition(pos);
    }

    /** Elforgatja az aktualis kartyat 90 fokkal oramutatoval megegyezo iranyban */
    private void rotateTile() {
        if (currentTile == null) return;
        currentRotation = (currentRotation + 90) % 360;
        currentTile = currentTile.rotated();
        drawPreview();
        if (pendingPosition != null) {
            drawBoard();
            highlightPosition(pendingPosition);
        }
    }

    /** Lerakja az aktualis kartyat a kivalasztott poziciora */
    private void placeTile() {
        if (pendingPosition == null || currentTile == null) return;

        placedTiles.put(pendingPosition, currentTile);
        updateBounds(pendingPosition);

        pendingPosition = null;
        currentRotation = 0;
        placeButton.setDisable(true);

        nextTurn();
        drawBoard();
        drawPreview();
    }

    /** Atadja a kort a kovetkezo jatekosnak kartya lerakasa nelkul */
    private void passTurn() {
        pendingPosition = null;
        placeButton.setDisable(true);
        nextTurn();
        drawBoard();
        drawPreview();
    }

    /** Lep a kovetkezo jatekosra es uj teszt kartyat huz */
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.length;
        currentPlayerLabel.setText("Soron: " + playerNames[currentPlayerIndex]);
        currentTile = createTestTile("T" + (placedTiles.size() + 1));
        currentRotation = 0;
        refreshPlayerPanel();
    }

    private void drawBoard() {
        int cols = (maxX - minX + 1) + CANVAS_PADDING * 2;
        int rows = (maxY - minY + 1) + CANVAS_PADDING * 2;

        double canvasW = cols * TILE_SIZE;
        double canvasH = rows * TILE_SIZE;
        boardCanvas.setWidth(Math.max(canvasW, 800));
        boardCanvas.setHeight(Math.max(canvasH, 600));

        gc.setFill(Color.web("#5C3A1E"));
        gc.fillRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

        /** Grid vonalak amik a palyakat alkotjak */
        gc.setStroke(Color.web("#7A5230", 0.4));
        gc.setLineWidth(0.5);
        for (int c = 0; c <= cols; c++) {
            gc.strokeLine(c * TILE_SIZE, 0, c * TILE_SIZE, canvasH);
        }
        for (int r = 0; r <= rows; r++) {
            gc.strokeLine(0, r * TILE_SIZE, canvasW, r * TILE_SIZE);
        }

        /** Ures helyeket jelzo teruletek */
        for (int x = minX - CANVAS_PADDING; x <= maxX + CANVAS_PADDING; x++) {
            for (int y = minY - CANVAS_PADDING; y <= maxY + CANVAS_PADDING; y++) {
                Position pos = new Position(x, y);
                if (!placedTiles.containsKey(pos) && (placedTiles.isEmpty() || hasNeighbour(pos))) {
                    int cx = (x - minX + CANVAS_PADDING) * TILE_SIZE;
                    int cy = (y - minY + CANVAS_PADDING) * TILE_SIZE;
                    gc.setFill(Color.web("#7A5230", 0.3));
                    gc.fillRect(cx + 2, cy + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                }
            }
        }

        /** Mar lerakott kartyas teruletek */
        for (Map.Entry<Position, Tile> entry : placedTiles.entrySet()) {
            Position pos = entry.getKey();
            Tile tile = entry.getValue();
            int cx = (pos.x() - minX + CANVAS_PADDING) * TILE_SIZE;
            int cy = (pos.y() - minY + CANVAS_PADDING) * TILE_SIZE;
            drawTile(gc, tile, cx, cy, TILE_SIZE);
        }
    }

    /**
     * Rajzol egy kartyat a megadott poziciora es meretre
     *
     * @param gc     a grafikus kontextus
     * @param tile   a rajzolandó kartya
     * @param x      bal felso sarok x koordinataja pixelben
     * @param y      bal felso sarok y koordinataja pixelben
     * @param size   a kartya merete pixelben
     */
    private void drawTile(GraphicsContext gc, Tile tile, int x, int y, int size) {
        int third = size / 3;

        // Alap mezo szin
        gc.setFill(Color.web("#7DBF5A"));
        gc.fillRect(x, y, size, size);

        // Elek szinezese
        drawEdge(gc, tile.getNorth(), x, y, size, "NORTH");
        drawEdge(gc, tile.getSouth(), x, y, size, "SOUTH");
        drawEdge(gc, tile.getEast(),  x, y, size, "EAST");
        drawEdge(gc, tile.getWest(),  x, y, size, "WEST");

        // Kolostor jel
        if (tile.isHasMonastery()) {
            gc.setFill(Color.web("#D4A017"));
            gc.fillOval(x + third, y + third, third, third);
        }

        // Varoscimer jel
        if (tile.isHasCityBadge()) {
            gc.setFill(Color.GOLD);
            gc.fillText("★", x + size / 2.0 - 6, y + size / 2.0 + 4);
        }

        // Keret
        gc.setStroke(Color.web("#3B2010"));
        gc.setLineWidth(1.5);
        gc.strokeRect(x, y, size, size);
    }

    /** A kartyak egyik kifejezett elet szinezi meg */
    private void drawEdge(GraphicsContext gc, EdgeType edge, int x, int y, int size, String dir) {
        Color color = switch (edge) {
            case CITY  -> Color.web("#C8A064");
            case ROAD  -> Color.web("#E0D8C0");
            case FIELD -> Color.web("#7DBF5A");
        };
        gc.setFill(color);
        int t = size / 4;
        switch (dir) {
            case "NORTH" -> gc.fillRect(x + t, y,          size - t * 2, t);
            case "SOUTH" -> gc.fillRect(x + t, y + size - t, size - t * 2, t);
            case "EAST"  -> gc.fillRect(x + size - t, y + t, t, size - t * 2);
            case "WEST"  -> gc.fillRect(x,          y + t, t, size - t * 2);
        }
    }

    /** A kovetkezo kartyanak az elonezetet rajtolza */
    private void drawPreview() {
        GraphicsContext pgc = previewCanvas.getGraphicsContext2D();
        pgc.setFill(Color.web("#5C3A1E"));
        pgc.fillRect(0, 0, 120, 120);
        if (currentTile != null) {
            drawTile(pgc, currentTile, 5, 5, 110);
        }
    }

    /**
     * Kiemeli a kivalasztott poziciot sarga kerettel
     *
     * @param pos a kiemelendo pozicio
     */
    private void highlightPosition(Position pos) {
        int cx = (pos.x() - minX + CANVAS_PADDING) * TILE_SIZE;
        int cy = (pos.y() - minY + CANVAS_PADDING) * TILE_SIZE;
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(cx + 1, cy + 1, TILE_SIZE - 2, TILE_SIZE - 2);
    }

    /** Frissiti a bal oldali jatekos panelt az aktualis allapot alapjan */
    private void refreshPlayerPanel() {
        playerPanel.getChildren().clear();
        Label header = new Label("Jatekosok");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        playerPanel.getChildren().add(header);

        for (int i = 0; i < playerNames.length; i++) {
            VBox card = new VBox(4);
            String border = (i == currentPlayerIndex)
                    ? "-fx-border-color: yellow; -fx-border-width: 2;"
                    : "-fx-border-color: #7A5230; -fx-border-width: 1;";
            card.setStyle("-fx-background-color: #4A2C10; -fx-padding: 8; -fx-background-radius: 6;" + border);

            Label name = new Label(playerNames[i]);
            name.setStyle("-fx-text-fill: " + toHex(playerColors[i]) + "; -fx-font-weight: bold;");

            Label info = new Label("Figurak: " + playerMeeples[i] + "   Pont: " + playerScores[i]);
            info.setStyle("-fx-text-fill: #D4C4A0; -fx-font-size: 11px;");

            card.getChildren().addAll(name, info);
            playerPanel.getChildren().add(card);
        }
    }

    /** Segedfuggvenyek */

    /**
     * Szomszed vizsgalatat vegzi
     *
     * @param pos a vizsgalt pozicio
     * @return igaz ha van szomszed
     */
    private boolean hasNeighbour(Position pos) {
        return placedTiles.containsKey(pos.north())
                || placedTiles.containsKey(pos.south())
                || placedTiles.containsKey(pos.east())
                || placedTiles.containsKey(pos.west());
    }

    /**
     * Minden lerakott kartyaval bovul a palyameret igy a maximumot szamitja ez a fuggveny
     *
     * @param pos az uj lerakasi pozicio
     */
    private void updateBounds(Position pos) {
        minX = Math.min(minX, pos.x());
        maxX = Math.max(maxX, pos.x());
        minY = Math.min(minY, pos.y());
        maxY = Math.max(maxY, pos.y());
    }

    /**
     * Letrehoz egy teszt kartyat
     *
     * @param id a kartya azonositoja
     * @return az uj teszt kartya
     */
    private Tile createTestTile(String id) {
        EdgeType[] edges = {EdgeType.FIELD, EdgeType.CITY, EdgeType.ROAD, EdgeType.FIELD};
        int r = (int) (Math.random() * 4);
        return new Tile(id,
                edges[r % 4],
                edges[(r + 1) % 4],
                edges[(r + 2) % 4],
                edges[(r + 3) % 4],
                false, false,
                java.util.List.of()
        );
    }

    /**
     * A szint hex stringge alakitja
     *
     * @param color a szin
     * @return hex string pl #FF0000
     */
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /** Get fuggveny */
    public BorderPane getRoot() {
        return root;
    }
}