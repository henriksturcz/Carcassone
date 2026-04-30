package Carcassone.logic;

import Carcassone.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
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
     * @param players a jatekosok listaja (legalabb 2 legfeljebb 5)
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
        Position lastPos = state.getLastPlacedPosition();
        List<FeatureConnector.FeatureResult> completed =
                connector.findCompletedFeatures(state.getBoard(), lastPos);

        for (FeatureConnector.FeatureResult result : completed) {
            scoreCompletedFeature(result, lastPos);
        }

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
     * Meghatározza a feature tipusat a meeplek alapjan
     * Igy ut es varos teruletek pontosan megkulonboztethetok
     *
     * @param result a befejezett terulet adatai
     * @return a TerrainFeature tipusa vagy null ha nincs meeple
     */
    private TerrainFeature detectFeatureType(FeatureConnector.FeatureResult result) {
        for (Meeple m : result.meeples) {
            if (m.getFeature() != null) return m.getFeature();
        }
        return null;
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

        List<Player> winners = getMeepleWinners(result.meeples, state.getPlayers());

        TerrainFeature featureType = detectFeatureType(result);
        int score;

        if (featureType == TerrainFeature.MONASTERY) {
            score = 9;
        } else if (featureType == TerrainFeature.CITY) {
            int tileCount = result.positions.size();
            int badgeCount = (int) result.tiles.stream()
                    .filter(pt -> pt.getTile().isHasCityBadge())
                    .count();
            score = scoring.calcCityScore(tileCount, badgeCount, true);
        } else {
            score = scoring.calcRoadScore(result.positions.size());
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

                EdgeType edgeType = pt.getTile().getEdge(dir);
                if (feature == TerrainFeature.CITY && edgeType != EdgeType.CITY) continue;
                if (feature == TerrainFeature.ROAD && edgeType != EdgeType.ROAD) continue;

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
                int score;

                if (feature == TerrainFeature.CITY) {
                    int badgeCount = (int) result.tiles.stream()
                            .filter(t -> t.getTile().isHasCityBadge()).count();
                    score = scoring.calcCityScore(tileCount, badgeCount, false);
                } else {
                    score = scoring.calcRoadScore(tileCount);
                }

                for (Player w : winners) w.addScore(score);

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

    /**
     * Mezo pontozas a jatek vegen
     * Az osszefuggo mezoterulet teljes halmaza alapjan szamolja a szomszedos befejezett varosoka, nem csak a kozvetlenul szomszedos kartlyakat
     */
    private void scoreFields() {
        Map<Position, PlacedTile> allTiles = state.getBoard().getAllTiles();
        Set<String> alreadyScoredField = new HashSet<>();

        for (Map.Entry<Position, PlacedTile> entry : allTiles.entrySet()) {
            PlacedTile pt = entry.getValue();
            if (!pt.hasMeeple()) continue;
            if (pt.getMeeple().getFeature() != TerrainFeature.FIELD) continue;

            Position pos = entry.getKey();
            String fieldKey = pos.x() + "," + pos.y();
            if (alreadyScoredField.contains(fieldKey)) continue;

            Set<Position> fieldPositions = floodFillField(allTiles, pos);
            for (Position fp : fieldPositions) {
                alreadyScoredField.add(fp.x() + "," + fp.y());
            }

            List<Meeple> fieldMeeples = new ArrayList<>();
            for (Position fp : fieldPositions) {
                PlacedTile fpt = allTiles.get(fp);
                if (fpt != null && fpt.hasMeeple()
                        && fpt.getMeeple().getFeature() == TerrainFeature.FIELD) {
                    fieldMeeples.add(fpt.getMeeple());
                }
            }
            if (fieldMeeples.isEmpty()) continue;

            List<Player> winners = getMeepleWinners(fieldMeeples, state.getPlayers());

            Set<Position> scoredCityPositions = new HashSet<>();
            int completedCityCount = 0;

            for (Position fp : fieldPositions) {
                int[] dx = {0, 0, 1, -1};
                int[] dy = {-1, 1, 0, 0};
                for (int d = 0; d < 4; d++) {
                    Position neighbour = new Position(fp.x() + dx[d], fp.y() + dy[d]);
                    if (scoredCityPositions.contains(neighbour)) continue;
                    PlacedTile neighbourTile = allTiles.get(neighbour);
                    if (neighbourTile == null) continue;

                    if (isCityTile(neighbourTile)) {
                        FeatureConnector.FeatureResult cityResult =
                                connector.exploreFeature(state.getBoard(), neighbour, "NORTH");
                        if (cityResult.completed) {
                            boolean alreadyCounted = false;
                            for (Position cp : cityResult.positions) {
                                if (scoredCityPositions.contains(cp)) {
                                    alreadyCounted = true;
                                    break;
                                }
                            }
                            if (!alreadyCounted) {
                                scoredCityPositions.addAll(cityResult.positions);
                                completedCityCount++;
                            }
                        }
                    }
                }
            }

            for (Player winner : winners) {
                winner.addScore(completedCityCount * 3);
            }

            for (Position fp : fieldPositions) {
                PlacedTile fpt = allTiles.get(fp);
                if (fpt != null && fpt.hasMeeple()
                        && fpt.getMeeple().getFeature() == TerrainFeature.FIELD) {
                    fpt.getMeeple().getOwner().returnMeeple();
                    fpt.removeMeeple();
                }
            }
        }
    }

    /**
     * Bejarja az osszes osszefuggo mezo  kartlyat flood-fill algoritmossal
     * Ket mezo kartlya osszefuggo ha koznos oldalukon mindketto FIELD elu
     *
     * @param allTiles az osszes lerakott kartlya
     * @param start    a kiindulo pozicio
     * @return az osszefuggo mezo kartlyak pozicioinak halmaza
     */
    private Set<Position> floodFillField(Map<Position, PlacedTile> allTiles,
                                         Position start) {
        Set<Position> visited = new HashSet<>();
        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            PlacedTile curTile = allTiles.get(cur);
            if (curTile == null) continue;

            Position[] neighbours = {cur.north(), cur.south(), cur.east(), cur.west()};
            String[] myDirs     = {"NORTH", "SOUTH", "EAST", "WEST"};
            String[] theirDirs  = {"SOUTH", "NORTH", "WEST", "EAST"};

            for (int i = 0; i < 4; i++) {
                Position nb = neighbours[i];
                if (visited.contains(nb)) continue;
                PlacedTile nbTile = allTiles.get(nb);
                if (nbTile == null) continue;

                if (curTile.getTile().getEdge(myDirs[i]) == EdgeType.FIELD
                        && nbTile.getTile().getEdge(theirDirs[i]) == EdgeType.FIELD) {
                    visited.add(nb);
                    queue.add(nb);
                }
            }
        }
        return visited;
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
     * @param meeples a teruleten levo meeplek
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