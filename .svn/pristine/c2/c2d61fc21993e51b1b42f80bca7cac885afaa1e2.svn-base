package Carcassone.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * A jatekszabalyokat megjelenito kepernyo
 * Az eredeti Carcassonne szabalyok alapjan
 */
public class RulesScreen {

    private final VBox root;

    /** Letrehozza a jatekszabalyok kepernyo elemeit */
    public RulesScreen() {
        Label title = new Label("Carcassonne — Jatekszabalyok");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        VBox content = new VBox(14);
        content.setPadding(new Insets(10, 20, 20, 20));

        content.getChildren().addAll(
                section("A jatek celja",
                        "Minden korben egy teruletkartya kerul lerakasra. Utak, varosok, retek tunnek fel, " +
                                "kolostorok kerulnek jatekba. Ezekre helyezhetjuk el alattvaloinkat (koronkent egyet), " +
                                "hogy pontokhoz jussunk. Mivel nem csak jatek kozben szerzunk pontokat, hanem a vegso " +
                                "ertekeleskor is, a nyertest csak az osszesites utan tudjuk meg."),

                section("A jatek elokeszitese",
                        "A kezdo teruletkartya (sotetsebb hatu) a tabla kozepere kerul. A tobbi kartya megkeverve " +
                                "paclikba kerul. Mindenki valaszt egy szint, felveszi a 8 alattvalojat, es egyet az " +
                                "ertekelo tabla 0 mezojere allit — a masikat het foghatja munkara a jatekban."),

                section("A jatek menete",
                        "A jatekosok az oramutato jarasaval egyezo iranyban kovetkeznek. A soros jatekos:\n" +
                                "  1. Huz egy uj teruletkartya, es azt a szabalyoknak megfeleloen a mar lerakottakhoz illeszti.\n" +
                                "  2. Egy alattvalot rahet az eppen elhelyezett kartyara (nem kotelezo).\n" +
                                "  3. Ha a letett kartyaval befejezett egy vagy tobb utat, varost vagy kolostort, azokat ertekeli.\n" +
                                "A pontok kiosztasa utan a kovetkezo jatekos ker sorra."),

                section("Teruletkartya elhelyezese",
                        "Az uj kartya legalabb egy oldalanak a mar lerakott kartykhoz kell illeszkednie (sarok nem eleg). " +
                                "Minden ret-, ut- es varosreszletnek folytatodnia kell: laphataron ret csak rettel, varos csak " +
                                "varossal erintkezhet, es a kartya szelehez futo utnak a masik kartyan folytatodnia kell.\n\n" +
                                "Ha egy felhuzott kartya sehogyan sem illesztheto, ki kell venni a jatekbol, es uj kartya huzandó."),

                section("Alattvalok elhelyezese",
                        "Miutan elhelyeztuk a kartya, feltehetunk ra egy alattvalot is:\n" +
                                "  - Soha nem tehetunk le egynél tobb alattvalot.\n" +
                                "  - Csak saját alattvalónkat tehetjük le.\n" +
                                "  - Az alattvalot csak az eppen elhelyezett kartyara allithatjuk.\n" +
                                "  - Az uj alattvalot nem tehetjuk olyan varosreszre, retre, utra, amin mar all alattvaló " +
                                "(sajat sem).\n" +
                                "Szerepek: Lovag (varos), Utonallo (ut), Paraszt (ret), Szerzetes (kolostor)."),

                section("Kész utak pontozasa",
                        "Egy ut akkor kesz, ha mindket vege keresztezodessel, varossal vagy kolostorral van lezarva, " +
                                "vagy korbe van zarva. A kész utért annyi pontot kap a tulajdonos, ahány kartyaból all az ut."),

                section("Kész varosok pontozasa",
                        "Egy varos akkor kesz, ha hianytalanul korbezarja egy varosfal. A kesz varosert annyiszor 2 " +
                                "pontot kap a lovag tulajdonosa, ahany kartyabol all, plusz 2 pont minden badge-ert. " +
                                "Ha tobb alattvaló is all egy varsoban, a legtobb lovaggal rendelkezo jatekos kapja a pontot " +
                                "(egyenloseg eseten mindenki)."),

                section("Kész kolostorok pontozasa",
                        "Egy kolostor akkor kesz, ha 8 teruletkartya veszi korul. A szerzetes tulajdonosa 9 pontot kap."),

                section("Az alattvalok visszaszerzese",
                        "Miutan egy ut, varos vagy kolostor elkeszult, az ertekeles utan az alattvalok " +
                                "visszaternek tulajdonosukhoz. A kovetkezo lepestol ujra felhasznalhatok."),

                section("Jatek vege es zaro ertekeles",
                        "A jatek akkor er veget, amikor lerakjak az utolso kartya. Ezutan kezdodik a zaro ertekeles:\n\n" +
                                "  - Befejezetlen utak, varosok, kolostorok: kartyankent 1 pont (badge is csak 1 pont).\n" +
                                "  - Mezok: minden befejezett varos utan, amely a mezOn all vagy azzal hataros, " +
                                "a mezo tulajdonosa 3 pontot kap.\n\n" +
                                "A legtobb pontot osszegyujtott jatekos nyeri. Egyenloseg eseten mindket jatekos nyer."),

                section("Egyenloseg szabalya",
                        "Ha tobb alattvalő all azonos varsoban, uton vagy reten, a legtobb alattvaloval rendelkezo " +
                                "jatekos kapja a pontot. Egyenloseg eseten mindket jatekos megkapja a teljes pontszamot."),

                section("Kartyak szama",
                        "Az alap jatekban 72 teruletkartya van (1 sotetsebb hatu kezdolap + 71 normal kartya). " +
                                "5 jatekosig jatszahto, mindenkinek 7 alattvaloja van.")
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #3B2010; -fx-background: #3B2010;");

        Button closeButton = new Button("Bezaras");
        closeButton.setOnAction(e -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        root = new VBox(10, title, scroll, closeButton);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #3B2010;");
    }

    /**
     * Letrehoz egy formazott szekciocimket es szoveget
     *
     * @param title   a szekció cime
     * @param body    a szekció szovege
     * @return a VBox tartalom
     */
    private VBox section(String title, String body) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        Label bodyLabel = new Label(body);
        bodyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #D4C4A0;");
        bodyLabel.setWrapText(true);

        VBox box = new VBox(4, titleLabel, bodyLabel);
        box.setStyle("-fx-border-color: #5C3A1E; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 8 0;");
        return box;
    }

    /** Get fuggveny */
    public VBox getRoot() { return root; }
}