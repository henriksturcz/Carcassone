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

**Logika reteg:**
- `PlacementValidator` — el-illesztes ellenorzese, ervenytelen poziciora nem rakahto kartya

**GUI reteg:**
- `MainApp` — JavaFX belepo pont, alapablak mukodik
- `SceneManager` — kepernyo-valtasok kezelese (showLogin, showLobby, showGame, showResult)
- `LoginScreen` — felhasznalonev es szerver cim megadasa, validacioval
- `LobbyScreen` — jatekszobak listaja, Teszt jatek gombbal
- `GameScreen` — teljes jatekpalya kepernyo:
  - Canvas alapu dinamikusan novo palya
  - El-illesztes validacio bekotve (PlacementValidator)
  - Kartya forgatas gombbal (90 fok, oramutatoval)
  - Meeple lerakasa kulon gombbal, meeple kihagyasa gomb
  - Jatekos panelek (nev, figurak szama, pontszam, aktualis jatekos kiemelve)
  - Jatek befejezese gomb a ResultScreen tesztelesehez
- `ResultScreen` — vegeredmeny kepernyo, gyoztes kiemelese, uj jatek / kilepes gomb

### Ismert hianyzossagok / meg nem mukodik

> Ezek tudatos hianyzossagok, nem bugok — a logikai reteg meg nincs teljesen megirva.

- **A kartyak veletlenszeruek es nem helyesek** — a `GameScreen` jelenleg teszt kartyakat general
  veletlen el-konfiguracioval, nem a valodi 72 lapos paklibol huz
- **Pontozas nem mukodik** — a `ScoringEngine` meg nincs megirva, a pontszam csak meeple
  lerakaskor no 1-gyel (placeholder)
- **Meeple szabalyok nem ervenyesulnek** — nem ellenorzi hogy a teruleten mar van-e meeple,
  es nem kerulnek vissza a figurak pontozas utan
- **Halozat nem mukodik** — a Login es Lobby kepernyo TCP kapcsolat nelkul mukodik,
  a szerver/kliens reteg meg hianyzik

### Meg nem kezdett

**Model reteg:**
- `GameState` — teljes jatekallapat (palya, jatekosok, fazis, aktualis kartya)
- `TileDeck` — huzopakli (72 kartya definicioja)

**Logika reteg:**
- `FeatureConnector` — terulet-osszekotes flood-fill alapon
- `ScoringEngine` — pontozas
- `GameEngine` — jatekiranyitas

**Halozati reteg:**
- `Server`, `ClientHandler`, `GameRoom` — szerver oldal
- `ServerConnection`, `MessageListener` — kliens oldal
- `Message`, `MessageType` — kozos uzenetformatom

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

## Logika osztalyok

### PlacementValidator

Ellenorzi hogy egy kartya lerakahto-e egy adott poziciora.

Ellenorzesi sorrend:
1. Ures palya eseten csak (0,0) ervenyes
2. Foglalt pozicio ervenytelen
3. Ha nincs szomszed, ervenytelen
4. Minden szomszed iranyaban el-egyezes ellenorzese

A `GameScreen` hasznalja: ervenytelen poziciora nem lehet kartyat rakni,
es csak az ervenyes helyek vannak kiemelve a palyan.

---

## GUI — JavaFX

Az alkalmazas JavaFX 26 alapu grafikus felulettel rendelkezik.

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
A **Teszt jatek** gomb TCP/szerver nelkul kozvetlenul a GameScreen-re dob.

### GameScreen

Canvas alapu jatekpalya kepernyo.

| Elem | Leiras |
|---|---|
| Bal panel | Jatekos kartyak nevvel, figurak szamával, pontszammal |
| Kozep | Scrollozhato Canvas palya, dinamikusan novo |
| Jobb panel | Aktualis kartya elonetezet, forgatas, lerak, meeple gombok |
| Jatek befejezese gomb | Atdob a ResultScreen-re (teszt cel) |

Jatek menete a kepernyon:
1. Kattints egy ervenyes (kiemelt) helyre a palyan
2. Forgasd el a kartyat ha kell
3. Nyomd meg a Lerak gombot
4. Rakj le meeple-t vagy kattints a Meeple kihagyasa gombra

### ResultScreen

Megjeleníti a vegso pontszamokat es a gyoztest.
Tartalmaz Uj jatek (→ Lobby) es Kilepes gombot.

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
- `FeatureConnector` — flood-fill alapu terulet-osszekotes
- `ScoringEngine` — pontozas
- `GameEngine` — jatekiranyitas

### 2. fazis — GUI + logika osszekotese

- Valodi kartyapakli bekotese a GameScreen-be
- Helyes pontozas megjelenítese
- Meeple szabalyok ervenyesítese

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
    │   └── PlacementValidator.java
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
