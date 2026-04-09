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

**GUI reteg:**
- `MainApp` — JavaFX belepo pont, alapablak mukodik
- `SceneManager` — kepernyo-valtasok kezelese
- `LoginScreen` — felhasznalonev es szerver cim megadasa, validacioval
- `LobbyScreen` — jatekszobak listaja, csatlakozas / letrehozas / vissza gomb

### Meg nem kezdett

**Model reteg:**
- `GameState` — teljes jatekallapat (palya, jatekosok, fazis, aktualis kartya)
- `TileDeck` — huzopakli (72 kartya definicioja)

**Logika reteg:**
- `PlacementValidator` — el-illesztes ellenorzese
- `FeatureConnector` — terulet-osszekotes flood-fill alapon
- `ScoringEngine` — pontozas
- `GameEngine` — jatekiranyitas

**Halozati reteg:**
- `Server`, `ClientHandler`, `GameRoom` — szerver oldal
- `ServerConnection`, `MessageListener` — kliens oldal
- `Message`, `MessageType` — kozos uzenetformatom

**GUI reteg:**
- `GameScreen` — jatekpalya Canvas alapu rajzolassal
- `ResultScreen` — vegeredmeny megjelenites

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

A kartyasablon. Nem konkret lerakott peldany, hanem a kartya tipus leiroja.
Tartalmazza a negy el tipusat, hogy van-e kolostor vagy varoscimer,
es hogy melyik elek tartoznak ugyanahhoz a teruletszigethez (connectedEdges).

A `rotated()` metodus uj peldanyt ad vissza 90 fokkal elforgatva (oramutatoval megegyezo iranyban).
Az elek es az el-osszekotetesek is forognak.

### Position

Racs koordinata (x, y) record tipuskent — az equals() es hashCode() automatikus,
igy HashMap kulcskent biztonsagosan hasznalhato.

Tartalmaz szomszed navigacios metodusokat: `north()`, `south()`, `east()`, `west()`.

### Meeple

Egy konkret figura peldany. Tarolja hogy melyik jatekose es melyik teruletre raktak.

### Player

Egy jatekost reprezental. Tarolja a nevet, a pontszamot es a szabad figurak szamat.
Maximum 7 figura lehet egy jatekosnal (`MAX_MEEPLES = 7`).

Fontosabb metodusok:
- `placeMeeple()` — csokkenti a szabad figurak szamat
- `returnMeeple()` — visszaad egy figurat (pontozas utan)
- `addScore(int)` — pontot ad hozza

### PlacedTile

Egy konkretan lerakott kartya a palyan. Tartalmazza a kartyasablont,
a poziciot es az esetleges figurat.

Fontosabb metodusok:
- `placeMeeple(Meeple)` — figurat helyez a kartyara
- `removeMeeple()` — eltavolitja es visszaadja a figurat

### Board

A jatekpalya. HashMap-ben tarolja a lerakott kartyakat pozicio szerint indexelve.

Fontosabb metodusok:
- `placeTile(PlacedTile)` — lerak egy kartyat
- `getTileAt(Position)` — visszaadja a pozicion levo kartyat
- `isOccupied(Position)` — foglalt-e a pozicio
- `hasNeighbour(Position)` — van-e szomszed
- `getAllTiles()` — osszes kartya, modosithatatlan Map-kent

---

## GUI — JavaFX

Az alkalmazas JavaFX 26 alapu grafikus felulettel rendelkezik.

### Kepernyo folyam

```
MainApp → LoginScreen → LobbyScreen → (GameScreen — kesobb)
                ↑____________|
                   vissza gomb
```

### LoginScreen

Felhasznalonev es szerver cim megadasara szolgal.
Ures mezo eseten hibauzenet jelenik meg, nem crash.
Sikeres kitoltes utan atlepunk a LobbyScreen-re.

### LobbyScreen

Megjeleníti a nyitott jatekszobakat.
Tartalmaz csatlakozas, uj szoba es vissza gombot.
A szobak listaja jelenleg statikus — halozati bekotes kesobb tortenik.

### SceneManager

Egyetlen helyen kezeli a Stage-et es a Scene-valtasokat.
Minden kepernyo-valtas ezen keresztul tortenik.

### Szalszabalyok

| Muvelet | Szal |
|---|---|
| GUI elem modositasa | JavaFX Application Thread (JAT) |
| Halozati kommunikacio | Hatter szal (Task / new Thread) |
| Halozati valasz megjelenítese | Platform.runLater() |

---

## Tervezett fejlesztesi sorrend

### 1. fazis — Hatro levo modell es logika

- `GameState`, `TileDeck` megirasa
- Kartyapakli: mind a 72 kartya definicioja
- `PlacementValidator`, `FeatureConnector`, `ScoringEngine`, `GameEngine`

### 2. fazis — GUI befejezese

- `GameScreen` — Canvas alapu palyarajzolas
- `ResultScreen` — vegeredmeny megjelenites
- Halozati bekotes a meglevo kepernykhoz

### 3. fazis — Halozat

- Szerver oldal: `Server`, `ClientHandler`, `GameRoom`
- Kliens oldal: `ServerConnection`, `MessageListener`
- Kozos uzenetformatom: `Message`, `MessageType`

---

## Csomagstruktura

```
src/
└── Carcassone/
    ├── gui/
    │   ├── MainApp.java
    │   ├── SceneManager.java
    │   ├── LoginScreen.java
    │   └── LobbyScreen.java
    ├── logic/
    ├── model/
    │   ├── Board.java
    │   ├── EdgeType.java
    │   ├── Meeple.java
    │   ├── PlacedTile.java
    │   ├── Player.java
    │   ├── Position.java
    │   ├── TerrainFeature.java
    │   └── Tile.java
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
