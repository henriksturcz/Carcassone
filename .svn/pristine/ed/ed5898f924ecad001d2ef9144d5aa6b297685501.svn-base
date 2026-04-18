package Carcassone.logic;

import Carcassone.model.*;

import java.util.*;

/** A pontozasi logika */
public class ScoringEngine {

    /**
     * befejezett kolostor eseten pontot ad a tulajdonos jatekosnak es visszaadja a meeple-t
     * Egyelore csak kolostort kezel
     * @param state          aktualis jatekallapat
     * @param lastPlacedPos  lerakott kartya pozicioja
     */
    public void scoreAfterPlacement(GameState state, Position lastPlacedPos) {
        Board board = state.getBoard();
        Map<Position, PlacedTile> allTiles = board.getAllTiles();

        // ellenorzes minden lerakott kolostoros kartya
        for (Map.Entry<Position, PlacedTile> entry : allTiles.entrySet()) {
            Position pos = entry.getKey();
            PlacedTile pt = entry.getValue();

            if (!pt.getTile().isHasMonastery()) continue;
            if (!pt.hasMeeple()) continue;
            if (pt.getMeeple().getFeature() != TerrainFeature.MONASTERY) continue;

            int neighbours = countNeighbours(board, pos);
            if (neighbours == 8) {
                // Egy befejezett kolostor az 9 pont
                Player owner = pt.getMeeple().getOwner();
                owner.addScore(9);
                owner.returnMeeple();
                pt.removeMeeple();
            }
        }
    }

    /**
     * Zaro ertekeles a jatek vegen minden befejezetlen teruletet is pontoz
     *
     * @param state az aktualis jatekallapat
     */
    public void scoreFinal(GameState state) {
        Board board = state.getBoard();
        Map<Position, PlacedTile> allTiles = board.getAllTiles();

        for (Map.Entry<Position, PlacedTile> entry : allTiles.entrySet()) {
            Position pos = entry.getKey();
            PlacedTile pt = entry.getValue();

            if (!pt.hasMeeple()) continue;

            TerrainFeature feature = pt.getMeeple().getFeature();
            Player owner = pt.getMeeple().getOwner();

            if (feature == TerrainFeature.MONASTERY) {
                // Egy befejezetlen kolostor az 1 + szomszedok szama
                int score = 1 + countNeighbours(board, pos);
                owner.addScore(score);
                owner.returnMeeple();
                pt.removeMeeple();
            }
            // Utak kotodessenek vizsgalata meg hatra van
        }
    }

    /**
     * Megkeresi a legtobb meeple-lel rendelkezo jatekost egy teruleten
     *
     * @param players      osszes jatekos
     * @param meepleCount  meeple szamok jatekos indexek szerint
     * @param score        pontszam
     */
    public void awardPoints(List<Player> players, int[] meepleCount, int score) {
        int max = 0;
        for (int count : meepleCount) {
            if (count > max) max = count;
        }
        for (int i = 0; i < players.size(); i++) {
            if (meepleCount[i] == max && max > 0) {
                players.get(i).addScore(score);
            }
        }
    }

    /**
     * Megszamolja egy pozicio korul a lerakott szomszedokat
     *
     * @param board
     * @param pos
     * @return a szomszedos lerakott kartyak szama max 8
     */
    public int countNeighbours(Board board, Position pos) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (board.getTileAt(new Position(pos.x() + dx, pos.y() + dy)) != null) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Kiszamolja egy ut pontszamat a benne levo kartyak szama alapjan
     * Befejezett ut 1 pont/kartya
     * Befejezetlen 1 pont/kartya.
     *
     * @param tileCount a teruleten levo kartyak szama
     * @return a pontszam
     */
    public int calcRoadScore(int tileCount) {
        return tileCount;
    }

    /**
     * Kiszamolja egy varos pontszamat
     * Befejezett varos 2 pont/kartya + 2 pont/badge.
     * Befejezetlen varos 1 pont/kartya + 1 pont/badge.
     *
     * @param tileCount  varosban levo kartyak szama
     * @param badgeCount vadoscimerek szama
     * @param completed  igaz ha a varos be van fejezve
     * @return pontszam
     */
    public int calcCityScore(int tileCount, int badgeCount, boolean completed) {
        if (completed) {
            return tileCount * 2 + badgeCount * 2;
        } else {
            return tileCount + badgeCount;
        }
    }

    /**
     * Visszaadja a jatekot nyero jatekosokat
     * Egyenloseg eseten mindenki gyoztes aki a legmagasabb pontszamot erte el
     *
     * @return a gyoztes vagy gyoztesek listaja
     */
    public List<Player> getWinners(List<Player> players) {
        int max = players.stream()
                .mapToInt(Player::getScore)
                .max()
                .orElse(0);

        List<Player> winners = new ArrayList<>();
        for (Player p : players) {
            if (p.getScore() == max) winners.add(p);
        }
        return winners;
    }
}