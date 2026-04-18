package Carcassone.logic;

import Carcassone.model.*;

import java.util.Map;

/**
 * Ellenorzi hogy egy kartya lerakahto-e egy adott poziciora a jatekpalyan
 * Az ellenorzesi sorrend: ures palya, foglalt pozicio, szomszed meglete, el-egyezes
 */
public class PlacementValidator {

    /**
     * Megvizsgalja hogy a megadott kartya lerakahto-e a poziciora.
     *
     * @param placedTiles a mar lerakott kartyak pozicioja
     * @param pos         a kivant pozicio
     * @return igaz ha az elhelyezes szabalyos
     */
    public boolean isValid(Map<Position, Tile> placedTiles, Tile tile, Position pos) {
        if (placedTiles.isEmpty()) {
            return pos.x() == 0 && pos.y() == 0;
        }

        if (placedTiles.containsKey(pos)) {
            return false;
        }

        if (!hasNeighbour(placedTiles, pos)) {
            return false;
        }

        return edgeMatches(placedTiles, tile, pos);
    }

    /**
     * Ellenorzi hogy a pozicionak van legalabb egy lerakott szomszedja
     *
     * @param placedTiles a lerakott kartyak
     * @param pos         a vizsgalt pozicio
     * @return igaz ha van szomszed
     */
    private boolean hasNeighbour(Map<Position, Tile> placedTiles, Position pos) {
        return placedTiles.containsKey(pos.north())
                || placedTiles.containsKey(pos.south())
                || placedTiles.containsKey(pos.east())
                || placedTiles.containsKey(pos.west());
    }

    /**
     * Ellenorzi hogy a kartya elei egyeznek az osszes szomszed megfelelo eleivel
     *
     * @param placedTiles a lerakott kartyak
     * @param tile        a lerakni kivant kartya
     * @param pos         a kivant pozicio
     * @return igaz ha minden szomszedos el egyezik
     */
    private boolean edgeMatches(Map<Position, Tile> placedTiles, Tile tile, Position pos) {
        Tile north = placedTiles.get(pos.north());
        if (north != null && north.getSouth() != tile.getNorth()) return false;

        Tile south = placedTiles.get(pos.south());
        if (south != null && south.getNorth() != tile.getSouth()) return false;

        Tile east = placedTiles.get(pos.east());
        if (east != null && east.getWest() != tile.getEast()) return false;

        Tile west = placedTiles.get(pos.west());
        if (west != null && west.getEast() != tile.getWest()) return false;

        return true;
    }
}
