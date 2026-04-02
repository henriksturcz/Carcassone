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
- `Tile` — kartyasablon, forgatassal

### Folyamatban

- Tobbi modell osztaly (Position, Player, Board, GameState, stb.)

### Meg nem kezdett

- Jateklogika (PlacementValidator, FeatureConnector, ScoringEngine, GameEngine)
- Halozati reteg (Server, ClientHandler, ServerConnection)
- JavaFX GUI (LoginScreen, LobbyScreen, GameScreen, ResultScreen)

---

## Tervezett fejlesztesi sorrend

### 1. fazis — Modell es jateklogika

Az alapja mindennek. Semmi Socket, semmi JavaFX — csak sima Java osztalyok.

- Adatmodellek: kartyak, jatekosok, palya, jatekallapat
- Kartyapakli: mind a 72 kartya definicioja
- Elhelyezesi szabalyok validalasa
- Terulet-osszekotesi logika (flood-fill)
- Pontozas

### 2. fazis — GUI

Ha a logika stabil, a grafikus felulet csak megjeleníti amit kap.

- Bejelentkezo kepernyo
- Lobby (jatekszobak listaja)
- Jatekkeperyo (Canvas alapu palyarajzolas)
- Eredmenykepernyo

### 3. fazis — Halozat

- Szerver: tobb parhuzamos jatekszoba kezelese
- Kliens: csatlakozas, uzenetek fogadasa es kuldese
- Szalkezeles es szinkronizacio

---

## Csomagstruktura

```
src/
└── carcassonne/
    ├── model/       # adatmodellek
    ├── logic/       # jateklogika
    ├── network/     # halozati reteg
    │   ├── shared/
    │   ├── server/
    │   └── client/
    └── gui/         # JavaFX kepernyo es komponensek
```

---

## Szabalyok amiket betartok
| Szabaly | Indok |
|---|---|
| GUI modositas csak JavaFX Application Thread-en | JavaFX nem szalbiztos |
| Halozati hivas soha nem a GUI szalon | A GUI lefagy tole |
| GameRoom metodusai synchronized | Tobb szal eri el egyszerre |
| Port 10000 felett | Rendszerportok jogosultsagot igenyelnek |
| JSON kommunikacio (nem Java szerializacio) | Biztonsag, olvashatosag |
| Javadoc minden publikus osztalyra es metodusra | Kotelezo kovetelmeny |

---
