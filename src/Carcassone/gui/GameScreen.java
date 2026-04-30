package Carcassone.gui;

import Carcassone.network.client.MessageListener;
import Carcassone.network.client.ServerConnection;
import Carcassone.network.shared.Message;
import Carcassone.network.shared.MessageType;
import Carcassone.model.*;
import Carcassone.logic.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A jatekpalya kepernyo
 * Megjeleníti a palyt a jatekosokat, az aktualis kartyat
 * Halozaton keresztul kommunikal a szerverrel
 */
public class GameScreen {

    private static final int TILE_SIZE = 80;
    private static final int PADDING = 5;

    private final BorderPane root;
    private final Canvas boardCanvas;
    private final GraphicsContext gc;
    private final VBox playerPanel;
    private final Canvas previewCanvas;
    private final Label currentPlayerLabel;
    private final Label phaseLabel;
    private final Button rotateButton;
    private final Button placeButton;
    private final Button meepleButton;
    private final Button skipMeepleButton;

    /** Allapotok */
    private final Map<Position, PlacedTile> placedTiles = new HashMap<>();
    private final Map<Position, Integer> meepleOnTile = new HashMap<>();
    private final PlacementValidator validator = new PlacementValidator();

    /** Osszes tile prototipus — ID alapjan keresheto */
    private static final List<Tile> ALL_TILE_PROTOTYPES = TileDeck.getAllPrototypes();

    private Tile currentTile;
    private int currentRotation = 0;
    private Position pendingPosition = null;
    private boolean meeplePhase = false;
    private boolean isObserver = false;
    private boolean isMyTurn = false;

    /** Meeple elhelyezesi zona kivalasztas: null=nincs kivalasztva */
    private String selectedMeepleZone = null; // "NORTH","SOUTH","EAST","WEST","CENTER"

    private int minX = 0, maxX = 0, minY = 0, maxY = 0;

    /** Jatekosok adatai */
    private String[] playerNames = {};
    private final Color[] playerColors = {
            Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, Color.PURPLE
    };
    private int[] playerScores = {};
    private int[] playerMeeples = {};
    private int currentPlayerIndex = 0;

    /** Halozati listener referencia az eltavolitashoz */
    private MessageListener gameListener;

    /**
     * Letrehozza a jatekpalya kepernyo osszes elemeit az elso allapot uzenettel
     *
     * @param initialState az elso GAME_STATE uzenet amit a lobby kapott, vagy null
     */
    public GameScreen(Carcassone.network.shared.Message initialState) {
        boardCanvas = new Canvas(800, 600);
        gc = boardCanvas.getGraphicsContext2D();

        ScrollPane boardScroll = new ScrollPane(boardCanvas);
        boardScroll.setPannable(true);
        boardScroll.setFitToWidth(false);
        boardScroll.setFitToHeight(false);
        boardScroll.setStyle("-fx-background: #5C3A1E; -fx-background-color: #5C3A1E;");

        boardCanvas.setOnMouseClicked(e -> {
            handleBoardClickRaw(e.getX(), e.getY());
        });

        /** Jobb oldali panel */
        currentPlayerLabel = new Label("Varakozas...");
        currentPlayerLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        phaseLabel = new Label("Csatlakozas...");
        phaseLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");

        Label previewLabel = new Label("Aktualis kartya:");
        previewLabel.setStyle("-fx-text-fill: white;");

        previewCanvas = new Canvas(120, 120);

        rotateButton = new Button("Forgatas");
        rotateButton.setMaxWidth(Double.MAX_VALUE);
        rotateButton.setDisable(true);
        rotateButton.setOnAction(e -> rotateTile());

        placeButton = new Button("Lerak");
        placeButton.setMaxWidth(Double.MAX_VALUE);
        placeButton.setDisable(true);
        placeButton.setOnAction(e -> placeTile());

        meepleButton = new Button("Meeple lerak");
        meepleButton.setMaxWidth(Double.MAX_VALUE);
        meepleButton.setDisable(true);
        meepleButton.setOnAction(e -> placeMeeple());

        skipMeepleButton = new Button("Meeple kihagyasa");
        skipMeepleButton.setMaxWidth(Double.MAX_VALUE);
        skipMeepleButton.setDisable(true);
        skipMeepleButton.setOnAction(e -> skipMeeple());

        Button endGameButton = new Button("Jatek befejezese");
        endGameButton.setMaxWidth(Double.MAX_VALUE);
        endGameButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
        endGameButton.setOnAction(e -> SceneManager.showResult(playerNames, playerScores));

        Button rulesButton = new Button("Jatekszabalyok");
        rulesButton.setMaxWidth(Double.MAX_VALUE);
        rulesButton.setOnAction(e -> SceneManager.showRules());

        VBox rightPanel = new VBox(12,
                currentPlayerLabel, phaseLabel,
                new Separator(), previewLabel, previewCanvas,
                rotateButton, placeButton,
                new Separator(), meepleButton, skipMeepleButton,
                new Separator(), rulesButton, endGameButton
        );
        rightPanel.setPadding(new Insets(15));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setStyle("-fx-background-color: #3B2010;");
        rightPanel.setPrefWidth(170);

        /** Bal oldali panel */
        playerPanel = new VBox(10);
        playerPanel.setPadding(new Insets(15));
        playerPanel.setStyle("-fx-background-color: #3B2010;");
        playerPanel.setPrefWidth(180);

        root = new BorderPane();
        root.setCenter(boardScroll);
        root.setLeft(playerPanel);
        root.setRight(rightPanel);
        root.setStyle("-fx-background-color: #5C3A1E;");

        setupNetworkListener();
        if (initialState != null) {
            handleGameState(initialState);
        }

        refreshPlayerPanel();
        drawPreview();
        drawBoard();
    }

    /**
     * Frissiti a palya allapotat a szerver altal kuldott tiles JSON alapjan.
     * Format: {"x,y":"tileId:rotation", ...}
     *
     * @param tilesJson  a szerver altal kuldott tiles JSON map
     * @param meeplesJson a szerver altal kuldott meeples JSON map (lehet null)
     */
    private void updateBoardFromServer(String tilesJson, String meeplesJson) {
        placedTiles.clear();
        meepleOnTile.clear();
        minX = 0; maxX = 0; minY = 0; maxY = 0;

        if (tilesJson == null || tilesJson.isBlank()) return;

        // Gson format: {"x,y":"tileId:rotation", ...}
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "\"(-?\\d+),(-?\\d+)\":\"([^:\"]+):(\\d+)\"");
        java.util.regex.Matcher m = p.matcher(tilesJson);

        while (m.find()) {
            try {
                int x        = Integer.parseInt(m.group(1));
                int y        = Integer.parseInt(m.group(2));
                String tileId= m.group(3);
                int rotation = Integer.parseInt(m.group(4));

                Position pos = new Position(x, y);
                Tile tile = findTileById(tileId, rotation);
                if (tile != null) {
                    placedTiles.put(pos, new PlacedTile(tile, pos));
                    updateBounds(pos);
                } else {
                    System.err.println("Ismeretlen tile ID: " + tileId);
                }
            } catch (Exception e) {
                System.err.println("Tiles parsing hiba: " + e.getMessage());
            }
        }

        // Meeple-k feldolgozasa ha van
        if (meeplesJson != null && !meeplesJson.isBlank()) {
            // Format: {"x,y":"playerName:feature", ...}
            java.util.regex.Pattern mp = java.util.regex.Pattern.compile(
                    "\"(-?\\d+),(-?\\d+)\":\"([^:\"]+):([^\"]+)\"");
            java.util.regex.Matcher mm = mp.matcher(meeplesJson);
            while (mm.find()) {
                try {
                    int x = Integer.parseInt(mm.group(1));
                    int y = Integer.parseInt(mm.group(2));
                    String playerName = mm.group(3);
                    Position pos = new Position(x, y);
                    // Megkeressuk a jatekos indexet
                    int idx = findPlayerIndex(playerName);
                    if (idx >= 0) {
                        meepleOnTile.put(pos, idx);
                    }
                } catch (Exception e) {
                    System.err.println("Meeples parsing hiba: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Visszaadja a jatekos indexet a neve alapjan, vagy -1 ha nem talalhato
     */
    private int findPlayerIndex(String name) {
        for (int i = 0; i < playerNames.length; i++) {
            if (playerNames[i] != null && playerNames[i].equals(name)) return i;
        }
        return -1;
    }

    /**
     * Alkalmazz a megadott forgatasi szoget egy kartyara
     *
     * @param tile     a kartya
     * @param rotation a forgatas foka (0, 90, 180, 270)
     * @return az elforgatott kartya
     */
    private Tile applyRotation(Tile tile, int rotation) {
        int turns = (rotation / 90) % 4;
        for (int i = 0; i < turns; i++) {
            tile = tile.rotated();
        }
        return tile;
    }

    /**
     * Megkeresi a megadott idu kartyat a statikus prototipus listabol es elforgatja.
     * Nem hoz letre uj TileDeck-et, igy konzisztens es gyors.
     *
     * @param tileId   a kartya azonositoja
     * @param rotation a forgatas foka
     * @return a megtalalt es elforgatott kartya vagy null
     */
    private Tile findTileById(String tileId, int rotation) {
        for (Tile proto : ALL_TILE_PROTOTYPES) {
            if (tileId.equals(proto.getId())) {
                return applyRotation(proto, rotation);
            }
        }
        return null;
    }

    /**
     * Megkeresi a megadott kepfajlnevu kartyat a statikus prototipus listabol.
     *
     * @param imageFile a keresett kepfajl neve
     * @return a megtalalt kartya (0 fokos forgatasban) vagy null
     */
    private Tile findTileByImage(String imageFile) {
        if (imageFile == null) return null;
        for (Tile proto : ALL_TILE_PROTOTYPES) {
            if (imageFile.equals(proto.getImageFile())) {
                return proto;
            }
        }
        return null;
    }

    /** Beallitja a halozati listenert a szerver uzenetek fogadasahoz */
    private void setupNetworkListener() {
        ServerConnection conn = SceneManager.getConnection();
        if (conn == null) return;

        gameListener = msg -> {
            if (msg.getMessageType() == null) return;
            Platform.runLater(() -> {
                switch (msg.getMessageType()) {
                    case GAME_STATE -> handleGameState(msg);
                    case YOUR_TURN  -> handleYourTurn();
                    case GAME_OVER  -> handleGameOver(msg);
                    case ERROR      -> {
                        phaseLabel.setText(msg.getString("msg"));
                        phaseLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                        // Hiba eseten a gombok visszaallnak hogy ne fagyjon le a jatek
                        if (!meeplePhase && isMyTurn) {
                            rotateButton.setDisable(false);
                            pendingPosition = null;
                            placeButton.setDisable(true);
                        } else if (meeplePhase && isMyTurn) {
                            meepleButton.setDisable(false);
                            skipMeepleButton.setDisable(false);
                        }
                    }
                    case PLAYER_LEFT -> {
                        phaseLabel.setText("Egy jatekos kileepett!");
                        phaseLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 12px;");
                    }
                    default -> {}
                }
            });
        };
        conn.addListener(gameListener);
    }

    /**
     * Feldolgozza a szerver altal kuldott jatekallapat uzenetet
     *
     * @param msg a GAME_STATE uzenet
     */
    private void handleGameState(Message msg) {
        String phase = msg.getString("phase");
        String currentPlayer = msg.getString("currentPlayer");

        isMyTurn = false;
        currentPlayerLabel.setText("Soron: " + currentPlayer);

        String playersJson = msg.getString("players");
        if (playersJson != null) {
            parsePlayersFromJson(playersJson);
        }

        if (SceneManager.getUsername() != null) {
            isObserver = !isPlayerInGame();
            if (!isObserver) {
                isMyTurn = SceneManager.getUsername().equals(currentPlayer);
            }
        }

        String tilesJson  = msg.getString("tiles");
        String meeplesJson = msg.getString("meeples");
        if (tilesJson != null) {
            updateBoardFromServer(tilesJson, meeplesJson);
        }

        // Az aktualis (kovetkezo huzando) kartya beallitasa
        String tileId    = msg.getString("currentTileId");
        String tileImage = msg.getString("currentTileImage");
        int tileRot      = msg.getInt("currentTileRotation"); // 0 ha nincs

        if (tileId != null) {
            // Preferalt: ID alapjan keressuk, a szerver altal kuldott forgatassal
            currentTile = findTileById(tileId, tileRot);
        } else if (tileImage != null) {
            currentTile = findTileByImage(tileImage);
        } else {
            currentTile = null;
        }

        // Forgatas resetelese (az uj kartya mindig 0-foku amig a jatekos nem forgatja)
        currentRotation = (currentTile != null) ? currentTile.getRotation() : 0;

        if ("PLACE_TILE".equals(phase)) {
            meeplePhase = false;
            pendingPosition = null;
            if (isMyTurn && !isObserver) {
                rotateButton.setDisable(false);
                phaseLabel.setText("Rakj le egy kartyat!");
                phaseLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 12px;");
            } else {
                rotateButton.setDisable(true);
                phaseLabel.setText("Varakozas: " + currentPlayer);
                phaseLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");
            }
            placeButton.setDisable(true);
            meepleButton.setDisable(true);
            skipMeepleButton.setDisable(true);
        } else if ("PLACE_MEEPLE".equals(phase)) {
            meeplePhase = true;
            selectedMeepleZone = null;
            if (isMyTurn && !isObserver) {
                meepleButton.setDisable(true); // zona kivalasztas utan aktiv
                skipMeepleButton.setDisable(false);
                phaseLabel.setText("Kattints a kartyara a zona kivalasztashoz!");
                phaseLabel.setStyle("-fx-text-fill: #00BFFF; -fx-font-size: 12px;");
            } else {
                meepleButton.setDisable(true);
                skipMeepleButton.setDisable(true);
                phaseLabel.setText("Varakozas: " + currentPlayer);
                phaseLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");
            }
            rotateButton.setDisable(true);
            placeButton.setDisable(true);
        }

        refreshPlayerPanel();
        drawBoard();
        drawPreview();
    }

    /** Kezeli a YOUR_TURN uzenetet aktivalja a gombokat */
    private void handleYourTurn() {
        isMyTurn = true;
        if (!meeplePhase) {
            rotateButton.setDisable(false);
            pendingPosition = null;
            placeButton.setDisable(true);
            phaseLabel.setText("Te kovetkezel! Rakj le egy kartyat!");
        } else {
            meepleButton.setDisable(false);
            skipMeepleButton.setDisable(false);
            phaseLabel.setText("Te kovetkezel! Rakj le meeple-t vagy hagyd ki!");
        }
        phaseLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 12px;");
    }

    /**
     * Kezeli a jatek vegi uzenetet
     *
     * @param msg a GAME_OVER uzenet
     */
    private void handleGameOver(Message msg) {
        if (gameListener != null && SceneManager.getConnection() != null) {
            SceneManager.getConnection().removeListener(gameListener);
        }
        // Frissiti a pontokat a szerver vegso adataival
        String scoresJson = msg.getString("scores");
        if (scoresJson != null) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                    "\"name\":\"([^\"]+)\",\"score\":(\\d+)");
            java.util.regex.Matcher m = p.matcher(scoresJson);
            java.util.List<String> names = new java.util.ArrayList<>();
            java.util.List<Integer> scores = new java.util.ArrayList<>();
            while (m.find()) {
                names.add(m.group(1));
                scores.add(Integer.parseInt(m.group(2)));
            }
            if (!names.isEmpty()) {
                playerNames  = names.toArray(new String[0]);
                playerScores = scores.stream().mapToInt(Integer::intValue).toArray();
            }
        }
        SceneManager.showResult(playerNames, playerScores);
    }

    /**
     * Megvizsgalja hogy a bejelentkezett felhasznalo jatekos e ebben a jatekban
     *
     * @return igaz ha jatekos
     */
    private boolean isPlayerInGame() {
        String myName = SceneManager.getUsername();
        if (myName == null) return false;
        for (String name : playerNames) {
            if (myName.equals(name)) return true;
        }
        return false;
    }

    /**
     * Feldolgozza a jatekosok JSON adatait
     *
     * @param playersJson a jatekosok JSON tombje
     */
    private void parsePlayersFromJson(String playersJson) {
        try {
            // Egyszerű parsing: [{"name":"X","score":0,"meeples":7},...]
            String[] entries = playersJson
                    .replace("[", "").replace("]", "")
                    .split("\\},\\{");

            playerNames = new String[entries.length];
            playerScores = new int[entries.length];
            playerMeeples = new int[entries.length];

            for (int i = 0; i < entries.length; i++) {
                String e = entries[i].replace("{", "").replace("}", "");
                playerNames[i] = extractString(e, "name");
                playerScores[i] = extractIntVal(e, "score");
                playerMeeples[i] = extractIntVal(e, "meeples");
            }

            // Aktualis jatekos indexenek meghatározása
            String currentPlayer = currentPlayerLabel.getText()
                    .replace("Soron: ", "").trim();
            for (int i = 0; i < playerNames.length; i++) {
                if (playerNames[i] != null && playerNames[i].equals(currentPlayer)) {
                    currentPlayerIndex = i;
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Jatekos adatok feldolgozasa sikertelen: " + e.getMessage());
        }
    }

    /** Kiszed egy String erteket a JSON reszletbol */
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";
        int start = idx + search.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }

    /** Kiszed egy int erteket a JSON reszletbol */
    private int extractIntVal(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return 0;
        int start = idx + search.length();
        int end = start;
        while (end < json.length() &&
                (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        try { return Integer.parseInt(json.substring(start, end)); }
        catch (NumberFormatException e) { return 0; }
    }

    /**
     * A palyara valo kattintassal eltarolja a poziciot, meeple fazisban zona kivalasztas
     *
     * @param rawX a kattintas x koordinataja pixelben
     * @param rawY a kattintas y koordinataja pixelben
     */
    private void handleBoardClickRaw(double rawX, double rawY) {
        int col = (int)(rawX / TILE_SIZE) - PADDING + minX;
        int row = (int)(rawY / TILE_SIZE) - PADDING + minY;
        Position pos = new Position(col, row);

        if (meeplePhase && isMyTurn && !isObserver && pendingPosition != null
                && pos.equals(pendingPosition)) {
            // Meeple zona kivalasztas a lerakott kartyan belul
            double tileOriginX = (pendingPosition.x() - minX + PADDING) * TILE_SIZE;
            double tileOriginY = (pendingPosition.y() - minY + PADDING) * TILE_SIZE;
            double lx = rawX - tileOriginX; // local x a kartyan belul
            double ly = rawY - tileOriginY; // local y a kartyan belul
            String zone = getMeepleZone(lx, ly, TILE_SIZE);
            if (isZoneAvailable(currentTile, zone)) {
                selectedMeepleZone = zone;
                phaseLabel.setText("Zona: " + zoneLabel(zone) + " — kattints a Meeple lerak gombra!");
                phaseLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-size: 12px;");
                meepleButton.setDisable(false);
                drawBoard();
                highlightMeepleZone(pendingPosition, zone);
            } else {
                phaseLabel.setText("Erre a zonara nem rakhatod le!");
                phaseLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            }
            return;
        }

        if (meeplePhase) return;
        if (isObserver) return;
        if (!isMyTurn) return;
        if (placedTiles.containsKey(pos)) return;
        if (!placedTiles.isEmpty() && !hasNeighbour(pos)) return;
        if (!validator.isValid(placedTiles, currentTile, pos)) {
            phaseLabel.setText("Ide nem rakhatod le!");
            phaseLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            return;
        }
        phaseLabel.setText("Rakj le egy kartyat!");
        phaseLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px;");
        pendingPosition = pos;
        placeButton.setDisable(false);
        drawBoard();
        highlightPosition(pos);
    }

    /**
     * Meghatarozza melyik meeple-zonat kattintottak a kartyan belul
     * A kartya 5 zonara van osztva: NORTH, SOUTH, EAST, WEST, CENTER
     *
     * @param lx   local x a kartyan belul
     * @param ly   local y a kartyan belul
     * @param size a kartya merete
     * @return a zona neve
     */
    private String getMeepleZone(double lx, double ly, int size) {
        double third = size / 3.0;
        // Kozepso harmad
        boolean midX = lx >= third && lx <= 2 * third;
        boolean midY = ly >= third && ly <= 2 * third;
        if (midX && midY) return "CENTER";
        // Szelso harmadok: melyik a legdominalobb
        double fromN = ly;
        double fromS = size - ly;
        double fromE = size - lx;
        double fromW = lx;
        double min = Math.min(Math.min(fromN, fromS), Math.min(fromE, fromW));
        if (min == fromN) return "NORTH";
        if (min == fromS) return "SOUTH";
        if (min == fromE) return "EAST";
        return "WEST";
    }

    /**
     * Ellenorzi hogy az adott zona elerheto-e meeple lerakashoz
     *
     * @param tile a kartya
     * @param zone a zona neve
     * @return igaz ha elerheto
     */
    private boolean isZoneAvailable(Tile tile, String zone) {
        if (tile == null) return false;
        if ("CENTER".equals(zone)) return tile.isHasMonastery() || hasFieldCenter(tile);
        EdgeType edge = tile.getEdge(zone);
        return edge == EdgeType.CITY || edge == EdgeType.ROAD || edge == EdgeType.FIELD;
    }

    /**
     * Igaz ha a kartya kozepen mezo van (azaz nem kolostor, de mezo zona)
     */
    private boolean hasFieldCenter(Tile tile) {
        return tile.getNorth() == EdgeType.FIELD
                || tile.getSouth() == EdgeType.FIELD
                || tile.getEast() == EdgeType.FIELD
                || tile.getWest() == EdgeType.FIELD;
    }

    /**
     * Magyar megnevezest ad a zona nevehez
     *
     * @param zone a zona neve
     * @return magyar megnevezes
     */
    private String zoneLabel(String zone) {
        return switch (zone) {
            case "NORTH"  -> "Eszak";
            case "SOUTH"  -> "Del";
            case "EAST"   -> "Kelet";
            case "WEST"   -> "Nyugat";
            case "CENTER" -> "Kozep (Kolostor/Mezo)";
            default       -> zone;
        };
    }

    /**
     * Kiemeli a kivalasztott meeple zonat a kartyan
     *
     * @param pos  a kartya pozicioja
     * @param zone a zona neve
     */
    private void highlightMeepleZone(Position pos, String zone) {
        int cx = (pos.x() - minX + PADDING) * TILE_SIZE;
        int cy = (pos.y() - minY + PADDING) * TILE_SIZE;
        int third = TILE_SIZE / 3;

        gc.setFill(Color.web("#FFFF00", 0.45));
        switch (zone) {
            case "NORTH"  -> gc.fillRect(cx + third, cy,               third, third);
            case "SOUTH"  -> gc.fillRect(cx + third, cy + 2 * third,   third, third);
            case "EAST"   -> gc.fillRect(cx + 2*third, cy + third,     third, third);
            case "WEST"   -> gc.fillRect(cx,           cy + third,     third, third);
            case "CENTER" -> gc.fillRect(cx + third,   cy + third,     third, third);
        }
        // Kartya kerete maradjon
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(cx + 1, cy + 1, TILE_SIZE - 2, TILE_SIZE - 2);
    }

    /**
     * A palyara valo kattintassal eltarolja a poziciot
     *
     * @param pos a kattintott racs pozicio
     */
    private void handleBoardClick(Position pos) {
        // Legacy - mar nem hasznalt, handleBoardClickRaw valtotta fel
    }

    /** Elforgatja az aktualis kartyat 90 fokkal */
    private void rotateTile() {
        if (currentTile == null || meeplePhase) return;
        currentRotation = (currentRotation + 90) % 360;
        currentTile = currentTile.rotated();
        drawPreview();
        if (pendingPosition != null) {
            if (!validator.isValid(placedTiles, currentTile, pendingPosition)) {
                pendingPosition = null;
                placeButton.setDisable(true);
            }
        }
        drawBoard();
        if (pendingPosition != null) highlightPosition(pendingPosition);
    }

    /** Lerakja az aktualis kartyat a kivalasztott poziciora */
    private void placeTile() {
        if (pendingPosition == null || currentTile == null) return;
        if (!validator.isValid(placedTiles, currentTile, pendingPosition)) {
            pendingPosition = null;
            placeButton.setDisable(true);
            phaseLabel.setText("Ide nem rakhatod le!");
            phaseLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            drawBoard();
            return;
        }

        isMyTurn = false;
        rotateButton.setDisable(true);
        placeButton.setDisable(true);
        final Position pos = pendingPosition;
        final int rot = currentRotation;
        new Thread(() -> {
            Message msg = new Message(MessageType.PLACE_TILE);
            msg.put("x", pos.x());
            msg.put("y", pos.y());
            msg.put("rotation", rot);
            SceneManager.getConnection().send(msg);
        }).start();
    }

    /** Lerakja az aktualis jatekos meeplejet a kivalasztott zonara */
    private void placeMeeple() {
        if (!meeplePhase) return;
        if (playerMeeples[currentPlayerIndex] <= 0) return;
        if (selectedMeepleZone == null) {
            phaseLabel.setText("Eloszor kattints a kartyara a zona kivalasztashoz!");
            phaseLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            return;
        }

        meepleButton.setDisable(true);
        skipMeepleButton.setDisable(true);
        String zone = selectedMeepleZone;
        String feature = zoneToFeature(currentTile, zone);
        String direction = "CENTER".equals(zone) ? "NORTH" : zone;
        new Thread(() -> {
            Message msg = new Message(MessageType.PLACE_MEEPLE);
            msg.put("feature", feature);
            msg.put("direction", direction);
            SceneManager.getConnection().send(msg);
        }).start();
    }

    /**
     * A zona neve alapjan meghatarozza a feature tipust a szerver szamara
     *
     * @param tile a kartya
     * @param zone a zona neve
     * @return a feature tipusa: CITY, ROAD, FIELD, MONASTERY
     */
    private String zoneToFeature(Tile tile, String zone) {
        if ("CENTER".equals(zone)) {
            return tile != null && tile.isHasMonastery() ? "MONASTERY" : "FIELD";
        }
        if (tile == null) return "FIELD";
        EdgeType edge = tile.getEdge(zone);
        return switch (edge) {
            case CITY  -> "CITY";
            case ROAD  -> "ROAD";
            default    -> "FIELD";
        };
    }

    /**
     * Megkeresi a legjobb teruletet meeple lerakashoz az aktualis kartyan
     * Elonyben reszesiti a varost, majd az utat, majd a kolostort, vegul a mezot
     *
     * @param tile az aktualis kartya
     * @return [feature, direction] tomb
     */
    private String[] bestMeepleFeature(Tile tile) {
        if (tile == null) return new String[]{"FIELD", "NORTH"};
        String[] directions = {"NORTH", "EAST", "SOUTH", "WEST"};
        for (String dir : directions) {
            if (tile.getEdge(dir) == Carcassone.model.EdgeType.CITY) {
                return new String[]{"CITY", dir};
            }
        }
        if (tile.isHasMonastery()) {
            return new String[]{"MONASTERY", "NORTH"};
        }
        for (String dir : directions) {
            if (tile.getEdge(dir) == Carcassone.model.EdgeType.ROAD) {
                return new String[]{"ROAD", dir};
            }
        }
        return new String[]{"FIELD", "NORTH"};
    }

    /** Kihagyja a meeple lerakast */
    private void skipMeeple() {
        meepleButton.setDisable(true);
        skipMeepleButton.setDisable(true);
        new Thread(() -> {
            Message msg = new Message(MessageType.SKIP_MEEPLE);
            SceneManager.getConnection().send(msg);
        }).start();
    }


    private void drawBoard() {
        int cols = (maxX - minX + 1) + PADDING * 2;
        int rows = (maxY - minY + 1) + PADDING * 2;
        double canvasW = Math.max(cols * TILE_SIZE, 800);
        double canvasH = Math.max(rows * TILE_SIZE, 600);
        boardCanvas.setWidth(canvasW);
        boardCanvas.setHeight(canvasH);

        gc.setFill(Color.web("#5C3A1E"));
        gc.fillRect(0, 0, canvasW, canvasH);

        gc.setStroke(Color.web("#7A5230", 0.4));
        gc.setLineWidth(0.5);
        for (int c = 0; c <= cols; c++)
            gc.strokeLine(c * TILE_SIZE, 0, c * TILE_SIZE, canvasH);
        for (int r = 0; r <= rows; r++)
            gc.strokeLine(0, r * TILE_SIZE, canvasW, r * TILE_SIZE);

        if (!meeplePhase && currentTile != null && isMyTurn) {
            for (int x = minX - PADDING; x <= maxX + PADDING; x++) {
                for (int y = minY - PADDING; y <= maxY + PADDING; y++) {
                    Position pos = new Position(x, y);
                    if (!placedTiles.containsKey(pos)
                            && (placedTiles.isEmpty() || hasNeighbour(pos))
                            && validator.isValid(placedTiles, currentTile, pos)) {
                        int cx = (x - minX + PADDING) * TILE_SIZE;
                        int cy = (y - minY + PADDING) * TILE_SIZE;
                        gc.setFill(Color.web("#FFFFFF", 0.15));
                        gc.fillRect(cx + 2, cy + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    }
                }
            }
        }

        for (Map.Entry<Position, PlacedTile> entry : placedTiles.entrySet()) {
            Position pos = entry.getKey();
            int cx = (pos.x() - minX + PADDING) * TILE_SIZE;
            int cy = (pos.y() - minY + PADDING) * TILE_SIZE;
            drawTile(gc, entry.getValue().getTile(), cx, cy, TILE_SIZE);

            if (meepleOnTile.containsKey(pos)) {
                int pi = meepleOnTile.get(pos);
                Color c = pi < playerColors.length ? playerColors[pi] : Color.WHITE;
                gc.setFill(c);
                gc.fillOval(cx + TILE_SIZE / 2.0 - 8,
                        cy + TILE_SIZE / 2.0 - 8, 16, 16);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(1.5);
                gc.strokeOval(cx + TILE_SIZE / 2.0 - 8,
                        cy + TILE_SIZE / 2.0 - 8, 16, 16);
            }
        }

        // Meeple zona kiemelese ha van kivalasztva
        if (meeplePhase && selectedMeepleZone != null && pendingPosition != null) {
            highlightMeepleZone(pendingPosition, selectedMeepleZone);
        } else if (meeplePhase && pendingPosition != null && isMyTurn) {
            // Zona kivalasztas elott vilago keret mutatja hogy melyik kartyara kell kattintani
            int hcx = (pendingPosition.x() - minX + PADDING) * TILE_SIZE;
            int hcy = (pendingPosition.y() - minY + PADDING) * TILE_SIZE;
            gc.setStroke(Color.web("#00BFFF"));
            gc.setLineWidth(3);
            gc.strokeRect(hcx + 1, hcy + 1, TILE_SIZE - 2, TILE_SIZE - 2);
        }
    }

    /**
     * Rajzol egy kartyat a megadott poziciora es meretre
     * Ha a kartyahoz van kep azt rajzolja ki, egyebkent fallback rajzolas
     *
     * @param gc   a grafikus kontextus
     * @param tile a rajzolandó kartya
     * @param x    bal felso sarok x koordinataja pixelben
     * @param y    bal felso sarok y koordinataja pixelben
     * @param size a kartya merete pixelben
     */
    private void drawTile(GraphicsContext gc, Tile tile, int x, int y, int size) {
        Image img = TileImageCache.get(tile.getImageFile());
        if (img != null) {
            if (tile.getRotation() == 0) {
                gc.drawImage(img, x, y, size, size);
            } else {
                gc.save();
                gc.translate(x + size / 2.0, y + size / 2.0);
                gc.rotate(tile.getRotation());
                gc.drawImage(img, -size / 2.0, -size / 2.0, size, size);
                gc.restore();
            }
        } else {
            int t = size / 4;
            gc.setFill(Color.web("#7DBF5A"));
            gc.fillRect(x, y, size, size);
            drawEdge(gc, tile.getNorth(), x, y, size, "NORTH");
            drawEdge(gc, tile.getSouth(), x, y, size, "SOUTH");
            drawEdge(gc, tile.getEast(),  x, y, size, "EAST");
            drawEdge(gc, tile.getWest(),  x, y, size, "WEST");
            if (tile.isHasMonastery()) {
                gc.setFill(Color.web("#D4A017"));
                gc.fillOval(x + t, y + t, t, t);
            }
            if (tile.isHasCityBadge()) {
                gc.setFill(Color.GOLD);
                gc.fillText("★", x + size / 2.0 - 6, y + size / 2.0 + 4);
            }
        }
        gc.setStroke(Color.web("#3B2010"));
        gc.setLineWidth(1.5);
        gc.strokeRect(x, y, size, size);
    }

    /**
     * Rajzolja egy kartya egyik eljet
     */
    private void drawEdge(GraphicsContext gc, EdgeType edge,
                          int x, int y, int size, String dir) {
        Color color = switch (edge) {
            case CITY  -> Color.web("#C8A064");
            case ROAD  -> Color.web("#E0D8C0");
            case FIELD -> Color.web("#7DBF5A");
        };
        gc.setFill(color);
        int t = size / 4;
        switch (dir) {
            case "NORTH" -> gc.fillRect(x + t, y, size - t * 2, t);
            case "SOUTH" -> gc.fillRect(x + t, y + size - t, size - t * 2, t);
            case "EAST"  -> gc.fillRect(x + size - t, y + t, t, size - t * 2);
            case "WEST"  -> gc.fillRect(x, y + t, t, size - t * 2);
        }
    }

    /**
     * Rajzolja az aktualis kartya elonetezetet.
     */
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
        int cx = (pos.x() - minX + PADDING) * TILE_SIZE;
        int cy = (pos.y() - minY + PADDING) * TILE_SIZE;
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(cx + 1, cy + 1, TILE_SIZE - 2, TILE_SIZE - 2);
    }

    /** Frissiti a bal oldali jatekos panelt. */
    private void refreshPlayerPanel() {
        playerPanel.getChildren().clear();
        Label header = new Label("Jatekosok");
        header.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        playerPanel.getChildren().add(header);

        if (isObserver) {
            Label obsLabel = new Label("[Megfigyelő]");
            obsLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 11px;");
            playerPanel.getChildren().add(obsLabel);
        }

        for (int i = 0; i < playerNames.length; i++) {
            VBox card = new VBox(4);
            String border = (i == currentPlayerIndex)
                    ? "-fx-border-color: yellow; -fx-border-width: 2;"
                    : "-fx-border-color: #7A5230; -fx-border-width: 1;";
            card.setStyle("-fx-background-color: #4A2C10; -fx-padding: 8;"
                    + "-fx-background-radius: 6;" + border);

            Color col = i < playerColors.length ? playerColors[i] : Color.WHITE;
            Label name = new Label(playerNames[i]);
            name.setStyle("-fx-text-fill: " + toHex(col)
                    + "; -fx-font-weight: bold;");

            int score   = i < playerScores.length  ? playerScores[i]  : 0;
            int meeples = i < playerMeeples.length ? playerMeeples[i] : 7;
            Label info = new Label("Figurak: " + meeples + "   Pont: " + score);
            info.setStyle("-fx-text-fill: #D4C4A0; -fx-font-size: 11px;");

            card.getChildren().addAll(name, info);
            playerPanel.getChildren().add(card);
        }
    }


    /**
     * Megvizsgalja hogy a poziciohoz van-e lerakott szomszed
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
     * Frissiti a palya hatarait az uj pozicio alapjan
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
    public BorderPane getRoot() { return root; }
}