package Carcassone.logic;

import Carcassone.model.*;

import java.util.*;

/**
 * Megkeresi hogy egy adott kartya adott elehez csatlako terulet
 * Melyik mas kartyakat foglalja magaban vagy be van-e fejezve
 * Es melyek a rajta allo meeplek
 */
public class FeatureConnector {

    /** Egy terület bejárás eredmenye */
    public static class FeatureResult {
        public final Set<Position> positions;
        public final boolean completed;
        public final List<Meeple> meeples;
        public final List<PlacedTile> tiles;

        /**
         * Letrehoz egy FeatureResult peldanyt
         *
         * @param positions  a teruleten levo poziciok
         * @param completed  igaz ha befejezett
         * @param meeples    a teruleten levo meeplek
         * @param tiles      a teruleten levo kartyak
         */
        public FeatureResult(Set<Position> positions, boolean completed,
                             List<Meeple> meeples, List<PlacedTile> tiles) {
            this.positions = positions;
            this.completed = completed;
            this.meeples = meeples;
            this.tiles = tiles;
        }
    }

    /**
     * Bejarja a megadott pozicion es iranyban kezdodo teruletet
     *
     * @param board     a jatekpalya
     * @param startPos  a kezdo pozicio
     * @param startDir  a kezdo irany ("NORTH", "EAST", "SOUTH", "WEST")
     * @return a bejart terulet eredmenye
     */
    public FeatureResult exploreFeature(Board board, Position startPos, String startDir) {
        Map<Position, PlacedTile> allTiles = board.getAllTiles();
        PlacedTile startTile = allTiles.get(startPos);
        if (startTile == null) {
            return new FeatureResult(Set.of(), false, List.of(), List.of());
        }

        Set<String> visitedKeys = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Set<Position> positions = new HashSet<>();
        List<Meeple> meeples = new ArrayList<>();
        List<PlacedTile> tiles = new ArrayList<>();
        boolean completed = true;

        String startKey = key(startPos, startDir);
        queue.add(startKey);
        visitedKeys.add(startKey);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            String dir = parts[2];
            Position pos = new Position(x, y);

            PlacedTile pt = allTiles.get(pos);
            if (pt == null) {
                completed = false;
                continue;
            }

            if (positions.add(pos)) {
                tiles.add(pt);
                if (pt.hasMeeple()) {
                    meeples.add(pt.getMeeple());
                }
            }

            Set<String> connectedDirs = getConnectedDirections(pt.getTile(), dir);

            for (String connDir : connectedDirs) {
                Position neighbour = getNeighbour(pos, connDir);
                String oppositeDir = getOpposite(connDir);
                String neighbourKey = key(neighbour, oppositeDir);

                if (!visitedKeys.contains(neighbourKey)) {
                    visitedKeys.add(neighbourKey);

                    PlacedTile neighbourTile = allTiles.get(neighbour);
                    if (neighbourTile == null) {
                        completed = false;
                    } else {
                        queue.add(neighbourKey);
                    }
                }
            }
        }

        return new FeatureResult(
                Collections.unmodifiableSet(positions),
                completed,
                Collections.unmodifiableList(meeples),
                Collections.unmodifiableList(tiles)
        );
    }

    /**
     * Ellenorzi hogy egy teruletre lehet e meeplet rakni
     * Akkor lehet ha a teruleten meg nincs meeple
     *
     * @param board    a jatekpalya
     * @param pos      a kartya pozicioja
     * @param dir      az irany ahol a meeplet raknak
     * @return igaz ha a terulet szabad
     */
    public boolean canPlaceMeeple(Board board, Position pos, String dir) {
        FeatureResult result = exploreFeature(board, pos, dir);
        return result.meeples.isEmpty();
    }

    /** Get fuggvenyek */
    private Set<String> getConnectedDirections(Tile tile, String dir) {
        List<Set<String>> connectedEdges = tile.getEdgeConnected();
        for (Set<String> group : connectedEdges) {
            if (group.contains(dir)) {
                return group;
            }
        }
        return Set.of(dir);
    }

    private Position getNeighbour(Position pos, String dir) {
        return switch (dir) {
            case "NORTH" -> pos.north();
            case "SOUTH" -> pos.south();
            case "EAST"  -> pos.east();
            case "WEST"  -> pos.west();
            default -> throw new IllegalArgumentException("Ismeretlen irany: " + dir);
        };
    }

    private String getOpposite(String dir) {
        return switch (dir) {
            case "NORTH" -> "SOUTH";
            case "SOUTH" -> "NORTH";
            case "EAST"  -> "WEST";
            case "WEST"  -> "EAST";
            default -> throw new IllegalArgumentException("Ismeretlen irany: " + dir);
        };
    }

    /**
     * Kulcsot general a flood fill visited halmazhoz
     *
     * @param pos a pozicio
     * @param dir az irany
     * @return a kulcs string formaban
     */
    private String key(Position pos, String dir) {
        return pos.x() + "," + pos.y() + "," + dir;
    }

    /**
     * Megkeresi az osszes befejezett teruletet amelyet az uj kartya lerakasa zart be
     *
     * @param board     a jatekpalya
     * @param lastPlaced az utoljara lerakott kartya pozicioja
     * @return a befejezett teruletek listaja
     */
    public List<FeatureResult> findCompletedFeatures(Board board, Position lastPlaced) {
        List<FeatureResult> completed = new ArrayList<>();
        PlacedTile pt = board.getTileAt(lastPlaced);
        if (pt == null) return completed;

        Set<String> alreadyChecked = new HashSet<>();
        String[] directions = {"NORTH", "EAST", "SOUTH", "WEST"};

        for (String dir : directions) {
            Set<String> connGroup = getConnectedDirections(pt.getTile(), dir);
            String groupKey = connGroup.stream().sorted().reduce("", String::concat);

            if (alreadyChecked.contains(groupKey)) continue;
            alreadyChecked.add(groupKey);

            // Kolostor kulon ellenorzese
            if (pt.getTile().isHasMonastery()) {
                FeatureResult monasteryResult = checkMonastery(board, lastPlaced);
                if (monasteryResult != null) completed.add(monasteryResult);
            }

            // Ut es varos ellenorzese
            EdgeType edgeType = pt.getTile().getEdge(dir);
            if (edgeType == EdgeType.FIELD) continue; // mezo nem ertekelodik kozben

            FeatureResult result = exploreFeature(board, lastPlaced, dir);
            if (result.completed && !result.meeples.isEmpty()) {
                completed.add(result);
            }
        }

        checkNeighbourMonasteries(board, lastPlaced, completed);

        return completed;
    }

    /**
     * Megvizsgalja hogy a megadott pozicion levo kolostor be van e fejezve
     *
     * @param board a jatekpalya
     * @param pos   a kolostor pozicioja
     */
    private FeatureResult checkMonastery(Board board, Position pos) {
        PlacedTile pt = board.getTileAt(pos);
        if (pt == null || !pt.getTile().isHasMonastery()) return null;
        if (!pt.hasMeeple()) return null;

        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (board.getTileAt(new Position(pos.x() + dx, pos.y() + dy)) != null) {
                    count++;
                }
            }
        }

        if (count == 8) {
            return new FeatureResult(
                    Set.of(pos), true,
                    List.of(pt.getMeeple()), List.of(pt)
            );
        }
        return null;
    }

    /**
     * Megvizsgalja hogy az uj lap lerakasaval befejezodott e valamelyik szomszedos kolostor
     *
     * @param board     a jatekpalya
     * @param newPos    az uj kartya pozicioja
     * @param results   ide kerulnek a befejezett kolostor eredmenyek
     */
    private void checkNeighbourMonasteries(Board board, Position newPos,
                                           List<FeatureResult> results) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position neighbour = new Position(newPos.x() + dx, newPos.y() + dy);
                FeatureResult result = checkMonastery(board, neighbour);
                if (result != null) results.add(result);
            }
        }
    }
}