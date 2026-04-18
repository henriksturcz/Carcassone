package Carcassone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A jatek teljes allapotat tarolja egy adott pillanatban
 * Tartalmazza a palyat, jatekosokat, aktualis allapotot es a huzott kartyat
 */
public class GameState {

    /** A jatek alapotai egy koron belul. */
    public enum Phase {
        WAITING,
        PLACE_TILE,
        PLACE_MEEPLE,
        GAME_OVER
    }

    private final Board board;
    private final List<Player> players;
    private Phase currentPhase;
    private int currentPlayerIndex;
    private Tile currentTile;
    private Position lastPlacedPosition;

    /**
     * Letrehoz egy uj jatekallapotot a megadott jatekosokkal
     * @param players a jatekosok listaja legalabb 2
     */
    public GameState(List<Player> players) {
        this.board = new Board();
        this.players = new ArrayList<>(players);
        this.currentPhase = Phase.WAITING;
        this.currentPlayerIndex = 0;
        this.currentTile = null;
        this.lastPlacedPosition = null;
    }

    /**
     * Lepteti az aktualis jatekost a kovetkezo jatekosra
     */
    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /** Get fuggvenyek */
    public Board getBoard() { return board; }

    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }

    public Phase getCurrentPhase() { return currentPhase; }

    /**
     * Beallitja az aktualis jatekallapotot
     * @param phase az uj alapot
     */
    public void setCurrentPhase(Phase phase) { this.currentPhase = phase; }

    /** Get fuggvenyek */
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }

    public Tile getCurrentTile() { return currentTile; }

    /**
     * Beallitja az aktualisan huzott kartyat
     *
     * @param tile a huzott kartya
     */
    public void setCurrentTile(Tile tile) { this.currentTile = tile; }

    /** Get fuggveny */
    public Position getLastPlacedPosition() { return lastPlacedPosition; }

    /**
     * Beallitja az utoljara lerakott kartya poziciojat
     *
     * @param pos a lerakasi pozicio
     */
    public void setLastPlacedPosition(Position pos) { this.lastPlacedPosition = pos; }

    /** Get fuggveny */
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
}