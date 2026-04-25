package Carcassone.gui;

import javafx.scene.image.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Betolti es gyorsitotarazba helyezi a kartya kepeket
 */
public class TileImageCache {

    private static final Map<String, Image> cache = new HashMap<>();
    private static final String BASE_PATH =
            "E:\\programozás\\JAVA\\Carcassone\\resources\\tiles\\" ;

    /**
     * Visszaadja a megadott fajlnevhez tartozo kepet
     * Ha meg nem volt betoltve, most toltodik be
     *
     * @param filename a kepfajl neve
     * @return a betoltott kep
     */
    public static Image get(String filename) {
        if (filename == null || filename.isEmpty()) return null;
        return cache.computeIfAbsent(filename, f -> {
            try {
                File file = new File(BASE_PATH + f);
                if (!file.exists()) {
                    System.err.println("Nem talalhato: " + file.getAbsolutePath());
                    return null;
                }
                return new Image(file.toURI().toString());
            } catch (Exception e) {
                System.err.println("Betoltes sikertelen: " + f + " — " + e.getMessage());
                return null;
            }
        });
    }

    /**
     * Elore betolti az osszes megadott kepet
     *
     * @param filenames a betoltendo fajlnevek
     */
    public static void preload(String... filenames) {
        for (String f : filenames) get(f);
    }
}
