package Carcassone.model;

/**
 * Egy jatekos a pontjaival es a meeplek szamaval ami rendelkezesere all
 */
public class Player {

    /** Max meeple szam */
    public static final int MAX_MEEPLES = 7;

    private final String name;
    private int score;
    private int availableMeeples;

    /**
     * Konstruktor
     * @param name a jatekos neve
     * @param score jatekos pontjai
     * @param availableMeeples megmaradt meeplek szama
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.availableMeeples = MAX_MEEPLES;
    }

    /**
     * Hozzaad pontokat a jatekos pontjaihoz
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Mikor a jatekos felhasznal egy meeplet akkor csokkenti a szamat
     *
     * @throws IllegalStateException ha nincs szabad meeple
     */
    public void placeMeeple() {
        if (availableMeeples <= 0) {
            throw new IllegalStateException("Nincs szabad meeple: " + name);
        }
        availableMeeples--;
    }

    /**
     * Pontozas utan visszaad meepleket
     */
    public void returnMeeple() {
        if (availableMeeples < MAX_MEEPLES) {
            availableMeeples++;
        }
    }

    /** Get fuggvenyek */
    public String getName() { return name; }

    public int getScore() { return score; }

    public int getAvailableMeeples() { return availableMeeples; }

    /** Igazat ad ha a játékosnak van meg meepleje */
    public boolean hasMeeple() { return availableMeeples > 0; }
}
