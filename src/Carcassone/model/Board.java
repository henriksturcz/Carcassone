package Carcassone.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maga a palya tarolasara szolgal
 * A lerakott kartyákat pozicio szerint indexeli
 */
public class Board {

    private final Map<Position, PlacedTile> tiles = new HashMap<>();

    /**
     * Lerak egy kartyat a megadott poziciora
     *
     * @param placedTile a lerakando kartya pozocioval egyutt
     * @throws IllegalArgumentException ha a pozocio mar foglalt
     */
    public void placeTile(PlacedTile placedTile) {
        Position pos = placedTile.getPosition();
        if (tiles.containsKey(pos)) {
            throw new IllegalArgumentException("A pozíció már foglalt: " + pos);
        }
        tiles.put(pos, placedTile);
    }

    /**
     * Visszaadja az adott poziciora lerakott kartyat
     */
    public PlacedTile getTileAt(Position position) {
        return tiles.get(position);
    }

    /**
     * Igazat ad ha az adott pozicion van mar kartya
     */
    public boolean isOccupied(Position position) {
        return tiles.containsKey(position);
    }

    /**
     * Megnezi hogy az adott kayrtyahoz tartozik-e szomszed
     *
     * @param position a pozicio
     * @return igaz ha legalabb egy szomszed pozicion van kartya
     */
    public boolean hasNeighbour(Position position) {
        return tiles.containsKey(position.north()) || tiles.containsKey(position.south()) || tiles.containsKey(position.east()) || tiles.containsKey(position.west());
    }

    /** Get fuggvenyek */
    public Map<Position, PlacedTile> getAllTiles() {
        return Collections.unmodifiableMap(tiles);
    }

    public int getTileCount() {
        return tiles.size();
    }

    /**
     * Ures palyapocizio eseten igazat ad vissza
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
