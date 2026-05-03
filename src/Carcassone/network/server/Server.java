package Carcassone.network.server;

import Carcassone.network.shared.Message;
import Carcassone.network.shared.MessageType;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A fo szerver osztaly
 * Egy porton figyel minden klienshez uj szalat indit
 * Kezeli a csatlakozott felhasznalokat es a jatekszobakat
 */
public class Server {

    public static final int PORT = 8080;

    private final Map<String, ClientHandler> connectedUsers = new ConcurrentHashMap<>();

    private final Map<Integer, GameRoom> gameRooms = new ConcurrentHashMap<>();

    private final AtomicInteger roomIdCounter = new AtomicInteger(1);

    final Gson gson = new Gson();

    /**
     * Elindítja a szervert a megadott porton
     * Vegtelen ciklusban fogadja az uj klienseket
     *
     * @throws IOException ha a port nem nyithato meg
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Szerver elindult a " + PORT + " porton");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Uj kliens csatlakozott: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        }
    }

    /**
     * Regisztralja a felhasznalot ha a nev nem foglalt
     *
     * @param username a felhasznalonev
     * @param handler  a kliens kezeloje
     * @return igaz ha a regisztracio sikeres volt
     */
    public synchronized boolean registerUser(String username, ClientHandler handler) {
        if (connectedUsers.containsKey(username)) return false;
        connectedUsers.put(username, handler);
        return true;
    }

    /** Torol egy felhasznalot a nyilvantartasbol */
    public synchronized void unregisterUser(String username) {
        connectedUsers.remove(username);
    }

    /**
     * Letrehoz egy uj jatekszobat
     *
     * @param creator a letrehozo kliens kezeloje
     * @return az uj szoba
     */
    public synchronized GameRoom createRoom(ClientHandler creator) {
        int id = roomIdCounter.getAndIncrement();
        GameRoom room = new GameRoom(id, this);
        gameRooms.put(id, room);
        return room;
    }

    /** Visszaadja a megadott idju szobat */
    public GameRoom getRoom(int roomId) {
        return gameRooms.get(roomId);
    }

    /**
     * Torol egy szobat ha minden jatekos kileepett.
     *
     * @param roomId a szoba idja
     */
    public synchronized void removeRoom(int roomId) {
        gameRooms.remove(roomId);
        System.out.println("Szoba torolve: " + roomId);
    }

    /**
     * Elkuldi minden csatlakozott kliensnek a szobak listajat
     */
    public synchronized void broadcastRoomList() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (GameRoom room : gameRooms.values()) {
            if (!first) sb.append(",");
            sb.append(gson.toJson(room.getRoomInfo()));
            first = false;
        }
        sb.append("]");

        Message msg = new Message(MessageType.ROOM_LIST);
        msg.put("rooms", sb.toString());
        String json = gson.toJson(msg);

        for (ClientHandler handler : connectedUsers.values()) {
            handler.send(json);
        }
    }

    /** Elinditja a szervert */
    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (IOException e) {
            System.err.println("Szerver hiba: " + e.getMessage());
        }
    }
}