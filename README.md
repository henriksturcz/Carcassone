# Carcassonne

A Carcassonne tarsasjatek halozaton jatszahato Java verzioja, JavaFX grafikus felhasznaloi felulettel es TCP socket alapu kommunikacioval.

---

## Technologiak

- OpenJDK 25
- TCP Socket (halozati kommunikacio)
- Gson (JSON szerializacio)
- Maven (build)

---

## Aktualis allapot

### Kesz

- `EdgeType` — el tipusok (CITY, ROAD, FIELD)
- `TerrainFeature` — terulettipusok (CITY, ROAD, FIELD, MONASTERY)
- `Tile` — kartyasablon, forgatassal (rotated())
- `Position` — racs koordinata, record tipus, szomszed navigacioval
- `Meeple` — jatekos figuraja, tulajdonos es terulettipus tarolasaval
- `Player` — jatekos neve, pontszama, szabad figurak szama (MAX = 7)
- `PlacedTile` — lerakott kartya pozicioval es figurával egyutt

### Meg nem kezdett

- Jateklogika (PlacementValidator, FeatureConnector, ScoringEngine, GameEngine)
- Halozati reteg (Server, ClientHandler, ServerConnection)
- JavaFX GUI (LoginScreen, LobbyScreen, GameScreen, ResultScreen)

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

---

## Tervezett fejlesztesi sorrend

### 1. fazis — Modell es jateklogika

Az alapja mindennek. Semmi Socket, semmi JavaFX — csak sima Java osztalyok.

- Hatra levo adatmodellek: Board, GameState, TileDeck
- Kartyapakli: mind a 72 kartya definicioja
- Elhelyezesi szabalyok validalasa (PlacementValidator)
- Terulet-osszekotesi logika flood-fill alapon (FeatureConnector)
- Pontozas (ScoringEngine)
- Jatekiranyitas (GameEngine)

### 2. fazis — GUI

Ha a logika stabil, a grafikus felulet csak megjeleníti amit kap.

- Bejelentkezo kepernyo (LoginScreen)
- Lobby — jatekszobak listaja (LobbyScreen)
- Jatekkepernyo — Canvas alapu palyarajzolas (GameScreen, BoardView)
- Eredmenykepernyo (ResultScreen)

### 3. fazis — Halozat

- Szerver: tobb parhuzamos jatekszoba kezelese
- Kliens: csatlakozas, uzenetek fogadasa es kuldese
- Szalkezeles es szinkronizacio

---

## Csomagstruktura

```
src/
└── carcassonne/
    ├── model/
    │   ├── EdgeType.java
    │   ├── TerrainFeature.java
    │   ├── Tile.java
    │   ├── Position.java
    │   ├── Meeple.java
    │   ├── Player.java
    │   └── PlacedTile.java
    ├── logic/
    ├── network/
    │   ├── shared/
    │   ├── server/
    │   └── client/
    └── gui/
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
