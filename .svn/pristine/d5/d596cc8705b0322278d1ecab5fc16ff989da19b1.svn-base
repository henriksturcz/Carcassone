package Carcassone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A huzopaklit reprezentalja
 * 23 kartya 1.png–23.png kepekkel
 */
public class TileDeck {

    private final List<Tile> deck;
    private Tile startTile;

    public TileDeck() {
        this.deck = new ArrayList<>(buildDeck());
        Collections.shuffle(deck);
    }

    public Tile draw() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("A pakli ures");
        }
        return deck.remove(deck.size() - 1);
    }

    public Tile drawStartTile() {
        return startTile;
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public int remaining() {
        return deck.size();
    }

    protected List<Tile> buildDeck() {
        List<Tile> tiles = new ArrayList<>();

        // KEZDOLAP — 5.png: varos eszakon, mezo kelet+del+nyugat
        startTile = new Tile("START",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.FIELD, EdgeType.FIELD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("EAST", "SOUTH", "WEST")),
                "5.png"
        );

        // 1.png — kolostor kozepen, mezo eszak+kelet+nyugat, ut delen
        // N=FIELD, E=FIELD, S=ROAD, W=FIELD
        tiles.add(new Tile("MON_ROAD_S",
                EdgeType.FIELD, EdgeType.ROAD, EdgeType.FIELD, EdgeType.FIELD,
                true, false,
                List.of(Set.of("NORTH", "EAST", "WEST"), Set.of("SOUTH")),
                "1.png"
        ));

        // 2.png — kolostor kozepen, mezo mind4 oldalon
        // N=FIELD, E=FIELD, S=FIELD, W=FIELD
        tiles.add(new Tile("MON_A",
                EdgeType.FIELD, EdgeType.FIELD, EdgeType.FIELD, EdgeType.FIELD,
                true, false,
                List.of(Set.of("NORTH", "EAST", "SOUTH", "WEST")),
                "2.png"
        ));

        // 3.png — varos mind4 oldalon + badge
        // N=CITY, E=CITY, S=CITY, W=CITY
        tiles.add(new Tile("C_BADGE",
                EdgeType.CITY, EdgeType.CITY, EdgeType.CITY, EdgeType.CITY,
                false, true,
                List.of(Set.of("NORTH", "EAST", "SOUTH", "WEST")),
                "3.png"
        ));

        // 4.png — varos eszakon, ut kelet+nyugat atmenő, mezo delen
        // N=CITY, E=ROAD, S=FIELD, W=ROAD
        tiles.add(new Tile("CITY_N_ROAD_EW",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.ROAD, EdgeType.ROAD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("EAST", "WEST"), Set.of("SOUTH")),
                "4.png"
        ));

        // 5.png — varos eszakon, mezo tobbi
        // N=CITY, E=FIELD, S=FIELD, W=FIELD
        tiles.add(new Tile("CITY_N_A",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.FIELD, EdgeType.FIELD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("EAST", "SOUTH", "WEST")),
                "5.png"
        ));

        // 6.png — varos kelet+nyugat osszefuggo atmenő, mezo eszak+del
        // N=FIELD, S=FIELD, E=CITY, W=CITY
        tiles.add(new Tile("CITY_EW",
                EdgeType.FIELD,  // north
                EdgeType.FIELD,  // south
                EdgeType.CITY,   // east
                EdgeType.CITY,   // west
                false, false,
                List.of(Set.of("EAST", "WEST"), Set.of("NORTH", "SOUTH")),
                "6.png"
        ));

        // 7.png — varos eszak+del atmenő, mezo kelet+nyugat
        // N=CITY, E=FIELD, S=CITY, W=FIELD
        tiles.add(new Tile("CITY_NS",
                EdgeType.CITY, EdgeType.CITY, EdgeType.FIELD, EdgeType.FIELD,
                false, false,
                List.of(Set.of("NORTH", "SOUTH"), Set.of("EAST", "WEST")),
                "7.png"
        ));

        // 8.png — varos eszak+nyugat sarok osszefuggo, mezo del+kelet
        // N=CITY, E=FIELD, S=FIELD, W=CITY
        tiles.add(new Tile("CITY_NW",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.FIELD, EdgeType.CITY,
                false, false,
                List.of(Set.of("NORTH", "WEST"), Set.of("EAST", "SOUTH")),
                "8.png"
        ));

        // 9.png — varos eszakon, ut del+kelet kanyar, mezo nyugat
        // N=CITY, E=ROAD, S=ROAD, W=FIELD
        tiles.add(new Tile("CITY_N_ROAD_SE",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.ROAD, EdgeType.FIELD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("EAST", "SOUTH"), Set.of("WEST")),
                "9.png"
        ));

        // 10.png — varos eszakon, ut del+nyugat kanyar, mezo kelet
        // N=CITY, E=FIELD, S=ROAD, W=ROAD
        tiles.add(new Tile("CITY_N_ROAD_SW",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.FIELD, EdgeType.ROAD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("SOUTH", "WEST"), Set.of("EAST")),
                "10.png"
        ));

        // 11.png — varos eszakon, ut kelet+nyugat+del T-keresztezo
        // N=CITY, E=ROAD, S=ROAD, W=ROAD
        tiles.add(new Tile("CITY_N_ROAD_EWS",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.ROAD, EdgeType.ROAD,
                false, false,
                List.of(Set.of("NORTH"), Set.of("EAST"), Set.of("SOUTH"), Set.of("WEST")),
                "11.png"
        ));

        // 12.png — varos eszak+kelet sarok osszefuggo, mezo del+nyugat
        // N=CITY, E=CITY, S=FIELD, W=FIELD
        tiles.add(new Tile("CITY_NE",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.CITY, EdgeType.FIELD,
                false, false,
                List.of(Set.of("NORTH", "EAST"), Set.of("SOUTH", "WEST")),
                "12.png"
        ));

        // 13.png — varos eszak+nyugat, ut del+kelet kanyar
        // N=CITY, E=ROAD, S=ROAD, W=CITY
        tiles.add(new Tile("CITY_NW_ROAD_SE",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.ROAD, EdgeType.CITY,
                false, false,
                List.of(Set.of("NORTH", "WEST"), Set.of("SOUTH", "EAST")),
                "13.png"
        ));

        // 14.png — varos eszak+kelet+nyugat+kozep 3 oldalon, mezo del
        // N=CITY, E=CITY, S=FIELD, W=CITY
        tiles.add(new Tile("CITY_NEW",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.CITY, EdgeType.CITY,
                false, false,
                List.of(Set.of("NORTH", "EAST", "WEST"), Set.of("SOUTH")),
                "14.png"
        ));

        // 15.png — ut eszak+del atmenő egyenes, mezo kelet+nyugat
        // N=ROAD, S=ROAD, E=FIELD, W=FIELD
        tiles.add(new Tile("ROAD_NS",
                EdgeType.ROAD,   // north
                EdgeType.ROAD,   // south
                EdgeType.FIELD,  // east
                EdgeType.FIELD,  // west
                false, false,
                List.of(Set.of("NORTH", "SOUTH"), Set.of("EAST", "WEST")),
                "15.png"
        ));

        // 16.png — ut del+nyugat kanyar, mezo eszak+kelet
        // N=FIELD, E=FIELD, S=ROAD, W=ROAD
        tiles.add(new Tile("ROAD_SW",
                EdgeType.FIELD, EdgeType.ROAD, EdgeType.FIELD, EdgeType.ROAD,
                false, false,
                List.of(Set.of("SOUTH", "WEST"), Set.of("NORTH", "EAST")),
                "16.png"
        ));

        // 17.png — ut del+kelet+nyugat T-keresztezo, mezo eszakon, kolostor kozepen
        // N=FIELD, E=ROAD, S=ROAD, W=ROAD
        tiles.add(new Tile("MON_ROAD_SEW",
                EdgeType.FIELD, EdgeType.ROAD, EdgeType.ROAD, EdgeType.ROAD,
                true, false,
                List.of(Set.of("NORTH"), Set.of("EAST"), Set.of("SOUTH"), Set.of("WEST")),
                "17.png"
        ));

        // 18.png — ut mind4 iranyba 4-es keresztezo, kolostor kozepen
        // N=ROAD, E=ROAD, S=ROAD, W=ROAD
        tiles.add(new Tile("MON_ROAD_NSEW",
                EdgeType.ROAD, EdgeType.ROAD, EdgeType.ROAD, EdgeType.ROAD,
                true, false,
                List.of(Set.of("NORTH"), Set.of("EAST"), Set.of("SOUTH"), Set.of("WEST")),
                "18.png"
        ));

        // 19.png — varos eszak+kelet+nyugat + badge, ut delen
        // N=CITY, E=CITY, S=ROAD, W=CITY
        tiles.add(new Tile("CITY_NEW_ROAD_S_BADGE",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.CITY, EdgeType.CITY,
                false, true,
                List.of(Set.of("NORTH", "EAST", "WEST"), Set.of("SOUTH")),
                "19.png"
        ));

        // 20.png — varos eszak+kelet+nyugat + badge, mezo delen
        // N=CITY, E=CITY, S=FIELD, W=CITY
        tiles.add(new Tile("CITY_NEW_BADGE",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.CITY, EdgeType.CITY,
                false, true,
                List.of(Set.of("NORTH", "EAST", "WEST"), Set.of("SOUTH")),
                "20.png"
        ));

        // 21.png — varos eszak+nyugat, kolostor, ut kelet+del, badge
        // N=CITY, E=ROAD, S=ROAD, W=CITY
        tiles.add(new Tile("CITY_NW_MON_ROAD_SE",
                EdgeType.CITY, EdgeType.ROAD, EdgeType.ROAD, EdgeType.CITY,
                true, true,
                List.of(Set.of("NORTH", "WEST"), Set.of("EAST", "SOUTH")),
                "21.png"
        ));

        // 22.png — varos eszak+kelet + badge, mezo del+nyugat
        // N=CITY, E=CITY, S=FIELD, W=FIELD
        tiles.add(new Tile("CITY_NE_BADGE",
                EdgeType.CITY, EdgeType.FIELD, EdgeType.CITY, EdgeType.FIELD,
                false, true,
                List.of(Set.of("NORTH", "EAST"), Set.of("SOUTH", "WEST")),
                "22.png"
        ));

        // 23.png — varos kelet+nyugat + badge, mezo eszak+del
        // N=FIELD, S=FIELD, E=CITY, W=CITY
        tiles.add(new Tile("CITY_EW_BADGE",
                EdgeType.FIELD,  // north
                EdgeType.FIELD,  // south
                EdgeType.CITY,   // east
                EdgeType.CITY,   // west
                false, true,
                List.of(Set.of("EAST", "WEST"), Set.of("NORTH", "SOUTH")),
                "23.png"
        ));

        return tiles;
    }
}