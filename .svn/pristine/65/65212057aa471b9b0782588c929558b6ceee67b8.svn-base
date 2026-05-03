# Carcassonne

A Carcassonne tarsasjatek halozaton jatszahato Java verzioja, JavaFX grafikus felhasznaloi felulettel es TCP socket alapu kommunikacioval.

---

## Technologiak

- OpenJDK 25
- JavaFX 26
- TCP Socket (halozati kommunikacio)
- Gson (JSON szerializacio)

---

## Futtatás GitHub-ról letöltve (fontos!)

Ha a projektet GitHub-ról töltötted le, a JavaFX SDK **nem része a repository-nak** — azt külön kell beszerezned és konfigurálnod, mert a JavaFX a JDK 11 óta nem része az alaptelepítésnek.

### 1. JavaFX SDK letöltése

Töltsd le a JavaFX 26 SDK-t az alábbi oldalról:
[https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)

Válaszd ki az operációs rendszerednek és az architektúrádnak megfelelő verziót, majd csomagold ki egy tetszőleges helyre.

### 2. Resources mappa elhelyezése

A kártyaképeket tartalmazó `resources/` mappa szintén nem része a repository-nak — azt külön kell letölteni és a projekt mellé helyezni.

**Fontos:** a `resources/` mappának és a JavaFX SDK mappájának **ugyanabba a könyvtárba kell kerülnie**, mint ahol a projekt gyökere van. Például:

```
C:\projektek\Carcassone\
├── src\
├── resources\
│   └── tiles\
└── javafx-sdk-26\
    └── lib\
```

### 3. TileImageCache elérési út beállítása

Nyisd meg a `src/Carcassone/gui/TileImageCache.java` fájlt, és az osztályon belüli `BASE_PATH` konstanst írd át a saját `resources/tiles/` mappád teljes elérési útjára:

```java
private static final String BASE_PATH =
        "C:\\projektek\\Carcassone\\resources\\tiles\\";
```

Windows alatt dupla backslash-t (`\\`) használj, vagy írj forward slash-t (`/`) — mindkettő működik Java-ban.

### 4. VM Options beállítása (IntelliJ IDEA)

A projekt csak megfelelő VM-argumentumokkal indul el. IntelliJ IDEA-ban:

1. Nyisd meg a **Run > Edit Configurations...** menüt
2. Kattints a futtatási konfigurációdra (vagy hozz létre újat)
3. A **VM options** mezőbe írd be az alábbi sort, a saját JavaFX SDK elérési útjával:

```
--module-path "C:\projektek\Carcassone\javafx-sdk-26\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics
```

### 5. Fő osztály beállítása

Ugyanebben a konfigurációban a **Main class** mező értéke legyen:

```
Carcassone.gui.MainApp
```

Ezután a projekt indítható és minden képernyő megfelelően jelenik meg.

---

## Aktuális állapot

### Elkészült

**Model réteg:**
- `EdgeType` — él típusok (CITY, ROAD, FIELD)
- `TerrainFeature` — területtípusok (CITY, ROAD, FIELD, MONASTERY)
- `Tile` — kártyasablon, forgatással (`rotated()`)
- `Position` — rács koordináta, record típus, szomszéd navigációval
- `Meeple` — játékos figurája, tulajdonos és területtípus tárolásával
- `Player` — játékos neve, pontszáma, szabad figurák száma (MAX = 7)
- `PlacedTile` — lerakott kártya pozícióval és figurával együtt
- `Board` — játékpálya, lerakott kártyák pozíció szerint indexelve
- `GameState` — teljes játékállapot (pálya, játékosok, fázis, aktuális kártya)
- `TileDeck` — húzópakli, mind a 72 kártya definíciójával, `draw()` és `shuffle()` működik

**Logika réteg:**
- `PlacementValidator` — él-illesztés ellenőrzése
- `FeatureConnector` — flood-fill alapú terület-összekötés, befejezettség vizsgálat
- `ScoringEngine` — kolostor, város, út és mező pontozás, győztes meghatározás
- `GameEngine` — játékot irányít, körsorrendet, fázisváltást és pontozást kezel

**Hálózati réteg:**
- `Server` — szerver indítás, párhuzamos játékok kezelése
- `ClientHandler` — egyes kliensekkel való kommunikáció
- `GameRoom` — egy játékszoba állapota, szabályok érvényesítése, pontszám számon tartása
- `ServerConnection` — kliens oldali TCP kapcsolat
- `MessageListener` — kliens oldali üzenet-fogadó interfész
- `Message`, `MessageType` — közös üzenetformátum (JSON alapú)

**GUI réteg:**
- `MainApp` — JavaFX belépőpont
- `SceneManager` — képernyő-váltások kezelése
- `LoginScreen` — felhasználónév és szerver cím megadása
- `LobbyScreen` — játékszobák listája, csatlakozás, létrehozás, megfigyelés
- `GameScreen` — teljes játékpálya képernyő, validációval és meeple kezeléssel
- `ResultScreen` — végeredmény képernyő
- `RulesScreen` — játékszabályok megjelenítése
- `TileImageCache` — kártyaképek cache-elése

---

## Modell osztályok

### EdgeType
Egy kártya élének típusát írja le — ezt használja az illesztésvalidálás.
```
CITY   — városfal él
ROAD   — út él
FIELD  — mező/rét él
```

### TerrainFeature
Egy kártyán belüli területtípus — erre lehet figurát rakni, ez alapján történik a pontozás.
```
CITY       — városrész
ROAD       — út
FIELD      — mező (csak játék végén értékel)
MONASTERY  — kolostor
```

### Tile
A kártyasablon. Tartalmazza a négy él típusát, kolostor/városacímer flageket,
és az összetartozó él-csoportokat (connectedEdges).
A `rotated()` metódus új példányt ad vissza 90 fokkal elforgatva — az élek is forognak.

### Position
Rács koordináta (x, y) record típusként — `equals()` és `hashCode()` automatikus.
Szomszéd navigációs metódusok: `north()`, `south()`, `east()`, `west()`.

### Meeple
Egy konkrét figura példány — tulajdonos játékos és területtípus tárolásával.

### Player
Játékos neve, pontszáma, szabad figurák száma (MAX = 7).
- `placeMeeple()` — figura lerakása
- `returnMeeple()` — figura visszavétele
- `addScore(int)` — pont hozzáadása

### PlacedTile
Lerakott kártya a pályán — kártyasablon + pozíció + esetleges figura.
- `placeMeeple(Meeple)` — figura lerakása
- `removeMeeple()` — figura eltávolítása

### Board
A játékpálya, HashMap alapon pozíció szerint indexelve.
- `placeTile(PlacedTile)` — kártya lerakása
- `getTileAt(Position)` — kártya lekérdezése
- `getAllTiles()` — összes kártya, módosíthatatlan Map-ként

### GameState
A játék teljes állapota: pálya, játékosok, fázis, aktuális kártya, utolsó lerakási pozíció.
Fázisok: `WAITING → PLACE_TILE → PLACE_MEEPLE → GAME_OVER`

### TileDeck
A húzópakli. A `draw()` húz egy kártyát, `remaining()` megadja a maradékot.
A `buildDeck()` tartalmazza mind a 72 kártya definícióját a helyes él-konfigurációkkal.

---

## Logika osztályok

### PlacementValidator
Ellenőrzi hogy egy kártya lerakható-e egy adott pozícióra.
Ellenőrzési sorrend:
1. Üres pálya: csak (0,0) érvényes
2. Foglalt pozíció: érvénytelen
3. Nincs szomszéd: érvénytelen
4. Él-egyezés minden szomszéd irányában

### FeatureConnector
Flood-fill alapú területösszekötés és befejezettség-vizsgálat.
Meghatározza hogy egy út, város vagy kolostor be van-e fejezve,
valamint összegyűjti az adott területhez tartozó összes meeple-t.

### ScoringEngine
A pontozási logika.
- Befejezett kolostor: 9 pont (maga + 8 szomszéd)
- Befejezetlen kolostor (játék végén): 1 + szomszédok száma
- Befejezett város: 2 pont/kártya + 2 pont/badge
- Befejezetlen város: 1 pont/kártya + 1 pont/badge
- Befejezett/befejezetlen út: 1 pont/kártya
- Mező (játék végén): 3 pont/szomszédos befejezett városért
- Egyenlőség esetén mindenki kap pontot
- `getWinners()` — legtöbb ponttal rendelkező játékos(ok)

### GameEngine
A játékot irányító osztály — összekötve a modellt és a logikát.
Felelős a körsorrendért, a fázisváltásokért és a pontozás meghívásáért.
Nem tud GUI-ról és nem tud Socketről.

---

## GUI — JavaFX

### Képernyő folyam

```
MainApp → LoginScreen → LobbyScreen → GameScreen → ResultScreen
                ↑____________|                          |
                   vissza gomb                    Új játék → Lobby
```

### LoginScreen
Felhasználónév és szerver cím megadására szolgál.
Üres mező esetén hibaüzenet jelenik meg, nem crash.

### LobbyScreen
Megjeleníti a nyitott játékszobákat.
Lehetőségek: csatlakozás meglévő szobához, új szoba létrehozása,
játék indítása (ha legalább 2 játékos csatlakozott), megfigyelőként csatlakozás.

### GameScreen
Canvas alapú játékpálya képernyő, dinamikusan növő.

| Elem | Leírás |
|---|---|
| Bal panel | Játékos kártyák névvel, figurák számával, pontszámmal |
| Közép | Scrollozható Canvas pálya |
| Jobb panel | Aktuális kártya előnézet, forgatás, lerak, meeple gombok |
| Játékszabályok gomb | Megnyitja a szabályok képernyőt |

Játék menete a képernyőn:
1. Kattints egy érvényes (kiemelt) helyre a pályán
2. Forgasd el a kártyát ha kell
3. Nyomd meg a Lerak gombot
4. Kattints a kártyára a meeple-zóna kiválasztásához, majd rakj le meeple-t — vagy kattints a Meeple kihagyása gombra

### ResultScreen
Megjeleníti a végső pontszámokat és a győztest.
Tartalmaz Új játék és Kilépés gombot.

### RulesScreen
Összefoglalja a legfontosabb játékszabályokat az alkalmazáson belül.

### Szálszabályok

| Művelet | Szál |
|---|---|
| GUI elem módosítása | JavaFX Application Thread (JAT) |
| Hálózati kommunikáció | Háttér szál (`new Thread`) |
| Hálózati válasz megjelenítése | `Platform.runLater()` |

---

## Csomagstruktúra

```
src/
└── Carcassone/
    ├── gui/
    │   ├── MainApp.java
    │   ├── SceneManager.java
    │   ├── LoginScreen.java
    │   ├── LobbyScreen.java
    │   ├── GameScreen.java
    │   ├── ResultScreen.java
    │   ├── RulesScreen.java
    │   └── TileImageCache.java
    ├── logic/
    │   ├── FeatureConnector.java
    │   ├── GameEngine.java
    │   ├── PlacementValidator.java
    │   └── ScoringEngine.java
    ├── model/
    │   ├── Board.java
    │   ├── EdgeType.java
    │   ├── GameState.java
    │   ├── Meeple.java
    │   ├── PlacedTile.java
    │   ├── Player.java
    │   ├── Position.java
    │   ├── TerrainFeature.java
    │   ├── Tile.java
    │   └── TileDeck.java
    └── network/
        ├── client/
        │   ├── MessageListener.java
        │   └── ServerConnection.java
        ├── server/
        │   ├── ClientHandler.java
        │   ├── GameRoom.java
        │   └── Server.java
        └── shared/
            ├── Message.java
            └── MessageType.java
```

---

## Betartott szabályok

| Szabály | Indok |
|---|---|
| GUI módosítás csak JavaFX Application Thread-en | JavaFX nem szálbiztos |
| Hálózati hívás soha nem a GUI szálon | A GUI lefagy tőle |
| GameRoom metódusai synchronized | Több szál éri el egyszerre |
| Port 10000 felett | Rendszerportok jogosultságot igényelnek |
| JSON kommunikáció (nem Java szerializáció) | Biztonság, olvashatóság |
| Javadoc minden publikus osztályra és metódusra | Kötelező követelmény |
