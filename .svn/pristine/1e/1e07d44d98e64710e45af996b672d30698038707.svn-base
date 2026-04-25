package Carcassone.logic;

import Carcassone.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** A jatekiranyito osztaly osszekoti a modellt es a logikai retegeket */
public class GameEngine {

    private final GameState state;
    private final TileDeck deck;
    private final PlacementValidator validator;
    private final ScoringEngine scoring;
    private final FeatureConnector connector;

    /**
     * Letrehoz egy uj GameEnginet a megadott jatekosokkal
     *
     * @param players a jatekosok listaja (legalabb 2, legfeljebb 5)
     */
    public GameEngine(List<Player> players) {
        this.state = new GameState(players);
        this.deck = new TileDeck();
        this.validator = new PlacementValidator();
        this.scoring = new ScoringEngine();
        this.connector = new FeatureConnector();
    }

    /** Elindítja a jatekot lerakja a kezdolarapot 0,0 ra */
    public void startGame() {
        Tile startTile = deck.drawStartTile();
        PlacedTile placed = new PlacedTile(startTile, new Position(0, 0));
        state.getBoard().placeTile(placed);
        state.setCurrentTile(deck.draw());
        state.setCurrentPhase(GameState.Phase.PLACE_TILE);
    }

    /**
     * Az aktualis jatekos lerakja a kartyat a megadott poziciora
     *
     * @param pos      a kivalasztott pozicio
     * @param rotation a forgatas foka
     * @return igaz ha a lerakas sikeres volt
     */
    public boolean placeTile(Position pos, int rotation) {
        if (state.getCurrentPhase() != GameState.Phase.PLACE_TILE) return false;

        Tile tile = state.getCurrentTile();
        int turns = rotation / 90;
        for (int i = 0; i < turns; i++) {
            tile = tile.rotated();
        }

        if (!validator.isValid(state.getBoard().getAllTiles(), tile, pos)) {
            return false;
        }

        PlacedTile placedTile = new PlacedTile(tile, pos);
        state.getBoard().placeTile(placedTile);
        state.setLastPlacedPosition(pos);
        state.setCurrentPhase(GameState.Phase.PLACE_MEEPLE);
        return true;
    }

    /**
     * Az aktualis jatekos meeplet rak le a legutobb lerakott kartyara
     *
     * @param feature  a terulet tipusa amelyre a meeplet rakja
     * @param direction az irany amelyre a meeplet rakja
     * @return igaz ha a meeple lerakasa sikeres volt
     */
    public boolean placeMeeple(TerrainFeature feature, String direction) {
        if (state.getCurrentPhase() != GameState.Phase.PLACE_MEEPLE) return false;

        Player current = state.getCurrentPlayer();
        if (!current.hasMeeple()) return false;

        Position pos = state.getLastPlacedPosition();
        PlacedTile placedTile = state.getBoard().getTileAt(pos);
        if (placedTile == null || placedTile.hasMeeple()) return false;

        // Ellenorzi hogy a terulet szabade
        if (feature != TerrainFeature.MONASTERY) {
            if (!connector.canPlaceMeeple(state.getBoard(), pos, direction)) {
                return false;
            }
        }

        Meeple meeple = new Meeple(current, feature);
        placedTile.placeMeeple(meeple);
        current.placeMeeple();

        endTurn();
        return true;
    }

    /** Az aktualis jatekos kihagyja a meeple lerakast */
    public void skipMeeple() {
        if (state.getCurrentPhase() != GameState.Phase.PLACE_MEEPLE) return;
        endTurn();
    }

    /** Lezarja az aktualis kort */
    private void endTurn() {
        // Kozbenso pontozas befejezett teruletek
        Position lastPos = state.getLastPlacedPosition();
        List<FeatureConnector.FeatureResult> completed =
                connector.findCompletedFeatures(state.getBoard(), lastPos);

        for (FeatureConnector.FeatureResult result : completed) {
            scoreCompletedFeature(result, lastPos);
        }

        // Kovetkezo jatekos
        state.nextPlayer();

        if (deck.isEmpty()) {
            scoreFinalFeatures();
            state.setCurrentPhase(GameState.Phase.GAME_OVER);
        } else {
            Tile next = deck.draw();
            while (!canPlaceAnywhere(next) && !deck.isEmpty()) {
                next = deck.draw();
            }
            state.setCurrentTile(next);
            state.setCurrentPhase(GameState.Phase.PLACE_TILE);
        }
    }

    /**
     * Megvizsgalja hogy a megadott kartya lerakahtoe legalabb egy helyre
     *
     * @param tile a vizsgalt kartya
     * @return igaz ha van ervenyes hely
     */
    private boolean canPlaceAnywhere(Tile tile) {
        Map<Position, PlacedTile> allTiles = state.getBoard().getAllTiles();
        for (Position placed : allTiles.keySet()) {
            for (Position candidate : List.of(
                    placed.north(), placed.south(),
                    placed.east(), placed.west())) {
                if (allTiles.containsKey(candidate)) { continue; }
                Tile t = tile;
                for (int rot = 0; rot < 4; rot++) {
                    if (validator.isValid(allTiles, t, candidate)) return true;
                    t = t.rotated();
                }
            }
        }
        return false;
    }

    /**
     * Pontoz egy befejezett teruletet es visszaadja a meepleket
     *
     * @param result  a befejezett terulet adatai
     * @param lastPos az utoljara lerakott kartya pozicioja
     */
    private void scoreCompletedFeature(FeatureConnector.FeatureResult result,
                                       Position lastPos) {
        if (result.meeples.isEmpty()) return;

        List<Player> winners = getMeepleWinners(result.meeples,
                state.getPlayers());

        int score;
        PlacedTile firstTile = result.tiles.isEmpty() ? null : result.tiles.getFirst();

        if (firstTile != null && firstTile.getTile().isHasMonastery()) {
            score = 9; // befejezett kolostor
        } else {
            int tileCount = result.positions.size();
            int badgeCount = (int) result.tiles.stream()
                    .filter(pt -> pt.getTile().isHasCityBadge())
                    .count();

            boolean isCity = result.tiles.stream()
                    .anyMatch(pt -> pt.getTile().getNorth() == EdgeType.CITY
                            || pt.getTile().getEast() == EdgeType.CITY
                            || pt.getTile().getSouth() == EdgeType.CITY
                            || pt.getTile().getWest() == EdgeType.CITY);

            score = isCity
                    ? scoring.calcCityScore(tileCount, badgeCount, true)
                    : scoring.calcRoadScore(tileCount);
        }

        for (Player winner : winners) {
            winner.addScore(score);
        }

        for (PlacedTile pt : result.tiles) {
            if (pt.hasMeeple()) {
                Meeple meeple = pt.getMeeple();
                if (meeple.getFeature() != TerrainFeature.FIELD) {
                    meeple.getOwner().returnMeeple();
                    pt.removeMeeple();
                }
            }
        }
    }

    /** Jatek vegi zaro ertekeles befejezetlen teruletek es meeplek */
    private void scoreFinalFeatures() {
        Map<Position, PlacedTile> allTiles = state.getBoard().getAllTiles();
        Set<String> alreadyScored = new HashSet<>();

        for (Map.Entry<Position, PlacedTile> entry : allTiles.entrySet()) {
            Position pos = entry.getKey();
            PlacedTile pt = entry.getValue();

            if (!pt.hasMeeple()) continue;

            Meeple meeple = pt.getMeeple();
            TerrainFeature feature = meeple.getFeature();

            if (feature == TerrainFeature.MONASTERY) {
                // Befejezetlen kolostor
                int score = 1 + scoring.countNeighbours(state.getBoard(), pos);
                meeple.getOwner().addScore(score);
                meeple.getOwner().returnMeeple();
                pt.removeMeeple();
                continue;
            }

            if (feature == TerrainFeature.FIELD) {
                continue;
            }

            String[] directions = {"NORTH", "EAST", "SOUTH", "WEST"};
            for (String dir : directions) {
                String featureKey = pos.x() + "," + pos.y() + "," + dir;
                if (alreadyScored.contains(featureKey)) continue;

                FeatureConnector.FeatureResult result =
                        connector.exploreFeature(state.getBoard(), pos, dir);

                for (Position p : result.positions) {
                    for (String d : new String[]{"NORTH", "EAST", "SOUTH", "WEST"}) {
                        alreadyScored.add(p.x() + "," + p.y() + "," + d);
                    }
                }

                if (result.meeples.isEmpty()) continue;

                List<Player> winners = getMeepleWinners(
                        result.meeples, state.getPlayers());

                int tileCount = result.positions.size();
                int badgeCount = (int) result.tiles.stream()
                        .filter(t -> t.getTile().isHasCityBadge()).count();

                boolean isCity = result.tiles.stream()
                        .anyMatch(t -> t.getTile().getNorth() == EdgeType.CITY
                                || t.getTile().getEast() == EdgeType.CITY
                                || t.getTile().getSouth() == EdgeType.CITY
                                || t.getTile().getWest() == EdgeType.CITY);

                int score = isCity
                        ? scoring.calcCityScore(tileCount, badgeCount, false)
                        : scoring.calcRoadScore(tileCount);

                for (Player w : winners) w.addScore(score);

                // Meeplek visszaadasa
                for (PlacedTile t : result.tiles) {
                    if (t.hasMeeple()) {
                        t.getMeeple().getOwner().returnMeeple();
                        t.removeMeeple();
                    }
                }
            }
        }

        scoreFields();
    }

    /** Mezo pontozas a jatek vegen */
    private void scoreFields() {
        Map<Position, PlacedTile> allTiles = state.getBoard().getAllTiles();

        for (Map.Entry<Position, PlacedTile> entry : allTiles.entrySet()) {
            PlacedTile pt = entry.getValue();
            if (!pt.hasMeeple()) continue;
            if (pt.getMeeple().getFeature() != TerrainFeature.FIELD) continue;

            // Szomszedos befejezett varosok szamlalasa
            Player owner = pt.getMeeple().getOwner();
            Set<Position> scoredCities = new HashSet<>();

            Position pos = entry.getKey();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Position neighbour = new Position(pos.x() + dx, pos.y() + dy);
                    PlacedTile neighbourTile = allTiles.get(neighbour);
                    if (neighbourTile == null) continue;

                    if (isCityTile(neighbourTile) && !scoredCities.contains(neighbour)) {
                        FeatureConnector.FeatureResult cityResult =
                                connector.exploreFeature(state.getBoard(), neighbour, "NORTH");
                        if (cityResult.completed) {
                            scoredCities.addAll(cityResult.positions);
                            owner.addScore(3);
                        }
                    }
                }
            }

            owner.returnMeeple();
            pt.removeMeeple();
        }
    }

    /**
     * Megvizsgalja hogy egy PlacedTile varos tipusu e
     *
     * @param pt a vizsgalt kartya
     * @return igaz ha legalabb egy ele CITY
     */
    private boolean isCityTile(PlacedTile pt) {
        Tile t = pt.getTile();
        return t.getNorth() == EdgeType.CITY || t.getEast() == EdgeType.CITY
                || t.getSouth() == EdgeType.CITY || t.getWest() == EdgeType.CITY;
    }

    /**
     * Meghatározza a meeplek alapjan a nyertes jatekosokat
     *
     * @param meeples a teruleten levo meeple-k
     * @param players az osszes jatekos
     * @return a nyertes jatekosok listaja
     */
    private List<Player> getMeepleWinners(List<Meeple> meeples, List<Player> players) {
        Map<Player, Integer> counts = new HashMap<>();
        for (Meeple m : meeples) {
            counts.merge(m.getOwner(), 1, Integer::sum);
        }

        int max = counts.values().stream().mapToInt(i -> i).max().orElse(0);

        List<Player> winners = new ArrayList<>();
        for (Map.Entry<Player, Integer> e : counts.entrySet()) {
            if (e.getValue() == max) winners.add(e.getKey());
        }
        return winners;
    }

    /** Get fuggvenyek */
    public GameState getState() { return state; }

    public List<Player> getWinners() {
        return scoring.getWinners(state.getPlayers());
    }

    /**
     * Megvizsgalja hogy a jatek veget ert e
     *
     * @return igaz ha GAME_OVER fazisban van
     */
    public boolean isGameOver() {
        return state.getCurrentPhase() == GameState.Phase.GAME_OVER;
    }
}