package Carcassone.model;

/**
 * Egy kartya a palyan mar kijatszva
 * Benne van a sablonja, a pozicioja es a meeple szama marmint ha van rajta
 */
public class PlacedTile {

    private final Tile tile;
    private final Position position;
    private Meeple meeple;

    /**
     * Konstruktor
     * @param tile a kartya sablonja (mar elforgatva)
     * @param position lerakas pozicioja a palyan
     */
    public PlacedTile(Tile tile, Position position) {
        this.tile = tile;
        this.position = position;
        this.meeple = null;
    }

    /**
     * Meeplet rak az adott teruletre
     * @param meeple a meeple ami lehelyezesre kerul
     * @throws IllegalStateException hibat dob ha mar szerepel a kartyan meeple
     */
    public void placeMeeple(Meeple meeple) {
        if (this.meeple != null) {
            throw new IllegalStateException("Mar van meeple ezen a kartyan.");
        }
        this.meeple = meeple;
    }

    /**
     * Pontozas utan eltavolitja es visszaadja a meeplet
     */
    public Meeple removeMeeple() {
        Meeple removed = this.meeple;
        this.meeple = null;
        return removed;
    }

    /** Get fuggvenyek */
    public Tile getTile() { return tile; }

    public Position getPosition() { return position; }

    public Meeple getMeeple() { return meeple; }

    public boolean hasMeeple() { return meeple != null; }
}
