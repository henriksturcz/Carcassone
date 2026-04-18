# Carcassonne

A Carcassonne tarsasjatek halozaton jatszahato Java verzioja, JavaFX grafikus felhasznaloi felulettel es TCP socket alapu kommunikacioval.

---

## Technologiak

- OpenJDK 25
- JavaFX 26
- TCP Socket (halozati kommunikacio)
- Gson (JSON szerializacio)

---

## Aktualis allapot

### Kesz

**Model reteg:**
- `EdgeType` — el tipusok (CITY, ROAD, FIELD)
- `TerrainFeature` — terulettipusok (CITY, ROAD, FIELD, MONASTERY)
- `Tile` — kartyasablon, forgatassal (rotated())
- `Position` — racs koordinata, record tipus, szomszed navigacioval
- `Meeple` — jatekos figuraja, tulajdonos es terulettipus tarolasaval
- `Player` — jatekos neve, pontszama, szabad figurak szama (MAX = 7)
- `PlacedTile` — lerakott kartya pozicioval es figurával egyutt
- `Board` — jatekpalya, lerakott kartyak pozicio szerint indexelve
- `GameState` — teljes jatekallapat (palya, jatekosok, fazis, aktualis kartya)
- `TileDeck` — huzopakli vazlata, draw() es shuffle() mukodik

**Logika reteg:**
- `PlacementValidator` — el-illesztes ellenorzese
- `ScoringEngine` — kolostor pontozas, varos/ut pontszam szamitas, gyoztes meghatarozas

**GUI reteg:**
- `MainApp` — JavaFX belepo pont
- `SceneManager` — kepernyo-valtasok kezelese
- `LoginScreen` — felhasznalonev es szerver cim megadasa
- `LobbyScreen` — jatekszobak listaja, Teszt jatek gombbal
- `GameScreen` — teljes jatekpalya kepernyo validacioval es meeple kezelesssel
- `ResultScreen` — vegeredmeny kepernyo

### Meg szukseges munka

**TileDeck — kartyapakli feltoltese:**
A `TileDeck` osztaly vazlata megvan, a `draw()`, `remaining()` es `shuffle()` metodusok mukodnek.
Ami meg hianyzik: a `buildDeck()` metodus tartalma, azaz mind a 72 kartya definicioja
a helyes el-konfiguraciokkal es connectedEdges beallitasokkal.
Ez onallo, nagy munka — referencia: boardgamegeek.com/image/115467/carcassonne.
A kezdolap (`drawStartTile()`) egyelore egy egyszeru teszt kartyat ad vissza,
ezt is le kell cserelni a valodi kezdolarapra.

**FeatureConnector — meg nem kezdett:**
Az ut es varos befejezesnek ellenorzese flood-fill alapon meg hianyzik.
A `ScoringEngine` ut/varos pontozasa ennek elkeszulte utan kothetо be teljesen.

**GameEngine — meg nem kezdett:**
A jatekiranyito osztaly amely osszekoti a modellt es a logikат.
Felelos a korsorrendert, a fazisok valtasaert es a pontozas meghivasaert.
Nem tud GUI-rol es nem tud Socketrol.

**Halozati reteg — meg nem kezdett:**
- `Server`, `ClientHandler`, `GameRoom` — szerver oldal
- `ServerConnection`, `MessageListener` — kliens oldal
- `Message`, `MessageType` — kozos uzenetformatom

### Ismert hianyzossagok / meg nem mukodik

- **A kartyak veletlenszeruek** — a GameScreen meg teszt kartyakat hasznal,
  a buildDeck() elkeszulte utan kothetok be a valodi kartyak
- **Ut es varos pontozas** — a ScoringEngine szamitja de a FeatureConnector nelkul
  nem tudja meghatározni hogy egy terulet be van-e fejezve
- **Halozat** — a Login es Lobby kepernyo TCP kapcsolat nelkul mukodik

---

## Modell osztalyok

### EdgeType
Egy kartya elinek tipusat irja le — ezt hasznalja az illesztesvalidalas.
```
CITY   — varosfal el
ROAD   — ut el
FIELD  — mezo/ret el
```

### TerrainFeature
Egy kartyán beluli terulettipus — erre lehet figurat rakni, ez alapjan tortenik a pontozas.
```
CITY       — varosresz
ROAD       — ut
FIELD      — mezo (csak jatek vegen ertekkel)
MONASTERY  — kolostor
```

### Tile
A kartyasablon. Tartalmazza a negy el tipusat, kolostor/varoscimer flageket,
es az osszetartozo el-csoportokat (connectedEdges).
A `rotated()` metodus uj peldanyt ad vissza 90 fokkal elforgatva — az elek is forognak.

### Position
Racs koordinata (x, y) record tipuskent — equals() es hashCode() automatikus.
Szomszed navigacios metodusok: `north()`, `south()`, `east()`, `west()`.

### Meeple
Egy konkret figura peldany — tulajdonos jatekos es terulettipus tarolasaval.

### Player
Jatekos neve, pontszama, szabad figurak szama (MAX = 7).
- `placeMeeple()` — figura lerakasa
- `returnMeeple()` — figura visszavetele
- `addScore(int)` — pont hozzaadasa

### PlacedTile
Lerakott kartya a palyan — kartyasablon + pozicio + esetleges figura.
- `placeMeeple(Meeple)` — figura lerakasa
- `removeMeeple()` — figura eltavolitasa

### Board
A jatekpalya, HashMap alapon pozicio szerint indexelve.
- `placeTile(PlacedTile)` — kartya lerakasa
- `getTileAt(Position)` — kartya lekerdezese
- `getAllTiles()` — osszes kartya, modosithatatlan Map-kent

### GameState
A jatek teljes allapota: palya, jatekosok, fazis, aktualis kartya, utolso lerakasi pozicio.
Fazisok: WAITING → PLACE_TILE → PLACE_MEEPLE → GAME_OVER

### TileDeck
A huzopakli. A `draw()` huz egy kartyat, `remaining()` megadja a maradekot.
A `buildDeck()` tartalma meg hianyzik — ez tartalmazza majd a 72 kartya definiciojat.

---

## Logika osztalyok

### PlacementValidator
Ellenorzi hogy egy kartya lerakahto-e egy adott poziciora.
Ellenorzesi sorrend:
1. Ures palya: csak (0,0) ervenyes
2. Foglalt pozicio: ervenytelen
3. Nincs szomszed: ervenytelen
4. El-egyezes minden szomszed iranyaban

### ScoringEngine
A pontozasi logika.
- Befejezett kolostor: 9 pont (maga + 8 szomszed)
- Befejezetlen kolostor (jatek vegen): 1 + szomszedok szama
- Befejezett varos: 2 pont/kartya + 2 pont/badge
- Befejezetlen varos: 1 pont/kartya + 1 pont/badge
- Befejezett/befejezetlen ut: 1 pont/kartya
- Egyenloseg eseten mindenki kap pontot
- `getWinners()` — legtobb ponttal rendelkezo jatekos(ok)

---

## GUI — JavaFX

### Kepernyo folyam

```
MainApp → LoginScreen → LobbyScreen → GameScreen → ResultScreen
                ↑____________|                          |
                   vissza gomb                    Uj jatek → Lobby
```

### LoginScreen
Felhasznalonev es szerver cim megadasara szolgal.
Ures mezo eseten hibauzenet jelenik meg, nem crash.

### LobbyScreen
Megjeleníti a nyitott jatekszobakat.
A Teszt jatek gomb TCP/szerver nelkul kozvetlenul a GameScreen-re dob.

### GameScreen
Canvas alapu jatekpalya kepernyo, dinamikusan novo.

| Elem | Leiras |
|---|---|
| Bal panel | Jatekos kartyak nevvel, figurak szamával, pontszammal |
| Kozep | Scrollozhato Canvas palya |
| Jobb panel | Aktualis kartya elonetezet, forgatas, lerak, meeple gombok |
| Jatek befejezese gomb | Atdob a ResultScreen-re (teszt cel) |

Jatek menete a kepernyon:
1. Kattints egy ervenyes (kiemelt) helyre a palyan
2. Forgasd el a kartyat ha kell
3. Nyomd meg a Lerak gombot
4. Rakj le meeple-t vagy kattints a Meeple kihagyasa gombra

### ResultScreen
Megjeleníti a vegso pontszamokat es a gyoztest.
Tartalmaz Uj jatek es Kilepes gombot.

### Szalszabalyok

| Muvelet | Szal |
|---|---|
| GUI elem modositasa | JavaFX Application Thread (JAT) |
| Halozati kommunikacio | Hatter szal (Task / new Thread) |
| Halozati valasz megjelenítese | Platform.runLater() |

---

## Tervezett fejlesztesi sorrend

### 1. fazis — Hatro levo logika

- `TileDeck.buildDeck()` — mind a 72 kartya definicioja
- `FeatureConnector` — flood-fill alapu terulet-osszekotes
- `GameEngine` — jatekiranyitas, korsorolrend, faziskezeles

### 2. fazis — GUI + logika osszekotese

- Valodi kartyapakli bekotese a GameScreen-be
- Helyes pontozas megjelenítese
- Meeple szabalyok teljes ervenyesítese

### 3. fazis — Halozat

- Szerver oldal: `Server`, `ClientHandler`, `GameRoom`
- Kliens oldal: `ServerConnection`, `MessageListener`
- Kozos uzenetformatom: `Message`, `MessageType`
- Login es Lobby halozati bekotese

---

## Csomagstruktura

```
src/
└── Carcassone/
    ├── gui/
    │   ├── MainApp.java
    │   ├── SceneManager.java
    │   ├── LoginScreen.java
    │   ├── LobbyScreen.java
    │   ├── GameScreen.java
    │   └── ResultScreen.java
    ├── logic/
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
        ├── server/
        └── shared/
```

---

## Szabalyok amiket betartunk

| Szabaly | Indok |
|---|---|
| GUI modositas csak JavaFX Application Thread-en | JavaFX nem szalbiztos |
| Halozati hivas soha nem a GUI szalon | A GUI lefagy tole |
| GameRoom metodusai synchronized | Tobb szal eri el egyszerre |
| Port 10000 felett | Rendszerportok jogosultsagot igenyelnek |
| JSON kommunikacio (nem Java szerializacio) | Biztonsag, olvashatosag |
| Javadoc minden publikus osztalyra es metodusra | Kotelezo kovetelmeny |

---