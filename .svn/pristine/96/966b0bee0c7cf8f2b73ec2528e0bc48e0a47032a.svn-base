package Carcassone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tile {

    /**
     * @param id             egyedi azonosito
     * @param north          eszaki el tipusa
     * @param south          deli el tipusa
     * @param east           keleti el tipusa
     * @param west           yugati el tipusa
     * @param hasMonastery   ha az adott kartya rendelkezik kolostorral
     * @param hasCityBadge   ha az adott kartya rendelkezik varos elemmel
     * @param connectedEdges az el-csoportok listaja
     */
    private final String id;
    private final EdgeType north;
    private final EdgeType south;
    private final EdgeType east;
    private final EdgeType west;
    private final boolean hasMonastery;
    private final boolean hasCityBadge;

    private final List<Set<String>> EdgeConnected;

    public Tile(String id, EdgeType north, EdgeType south, EdgeType east, EdgeType west, boolean hasMonastery, boolean hasCityBadge, List<Set<String>> EdgeConnected) {
        this.id = id;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.hasCityBadge = hasCityBadge;
        this.hasMonastery = hasMonastery;
        this.EdgeConnected = List.copyOf(EdgeConnected);
    }

    /**
     * Visszaadja az adott irányhoz tartozó él típusát.
     *
     * @param direction "NORTH" "EAST" "SOUTH" vagy "WEST"
     * @throws IllegalArgumentException ismeretlen irany eseten
     */
    public EdgeType getEdge(String direction){
        return switch(direction){
            case "NORTH" -> north;
            case "SOUTH" -> south;
            case "EAST" -> east;
            case "WEST"  -> west;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }

    /**
     * Uj kartyat ad vissza ami oramutato jarasaval megegyezoen el van 90 fokkal forditva
     * @return az elforgatott kartya
     */
    public Tile rotated() {
        return new Tile(id, west, east, north, south, hasMonastery, hasCityBadge, rotateConnections(EdgeConnected));
    }

    /**
     * A csatlakozas halmazokat 90 fokkal elforgatja
     * @param original az eredeti csatlakozas
     * @return az elforgatott csatlakozas
     */
    public List<Set<String>> rotateConnections(List<Set<String>>original){
        List<Set<String>> rotated  = new ArrayList<>();
        for(Set<String> group : original){
            Set<String> rotatedGroup = new HashSet<>();
            for (String dir : group) {
                rotatedGroup.add(rotateDirection(dir));
            }
            rotated.add(rotatedGroup);
        }
        return rotated;
    }

    /**
     * Segedfuggvenye a forgatasnak magat a forgatast vegzi
     * @return az elforgatott irány
     */
    private String rotateDirection(String dir) {
        return switch (dir) {
            case "NORTH" -> "EAST";
            case "EAST"  -> "SOUTH";
            case "SOUTH" -> "WEST";
            case "WEST"  -> "NORTH";
            default -> throw new IllegalArgumentException("Ismeretlen irány: " + dir);
        };
    }

    /**
     * Get fuggvenyek tulajdonkeppen csak minden erteket returnolok
     */
    public String getId() {
        return id;
    }

    public EdgeType getNorth() {
        return north;
    }

    public EdgeType getSouth() {
        return south;
    }

    public EdgeType getEast() {
        return east;
    }

    public EdgeType getWest() {
        return west;
    }

    public boolean isHasMonastery() {
        return hasMonastery;
    }

    public boolean isHasCityBadge() {
        return hasCityBadge;
    }

    public List<Set<String>> getEdgeConnected() {
        return EdgeConnected;
    }

}
