package Carcassone.network.server;

import Carcassone.network.shared.Message;
import Carcassone.network.shared.MessageType;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

/**
 * Egy csatlakozott klienst kezel kulon szalon fut
 *  Felelos az uzenet fogadasaert feldolgozasaert es a valasz kuldesert
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private GameRoom currentRoom;
    private boolean observer = false;
    private final Gson gson = new Gson();

    /**
     * Letrehoz egy ClientHandlert a megadott sockethez
     *
     * @param socket a kliens socketje
     * @param server a fo szerver referencia
     */
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /** Olvassa az uzenetek es feldolgozza oket */
    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Kliens kapcsolat megszakadt: " +
                    (username != null ? username : "ismeretlen"));
        } finally {
            cleanup();
        }
    }

    /**
     * Feldolgoz egy beerkeo JSON uzenetet
     *
     * @param json a beerkezo uzenet
     */
    private void handleMessage(String json) {
        Message msg;
        try {
            msg = gson.fromJson(json, Message.class);
        } catch (Exception e) {
            sendError("Ervenytelen uzenet formatum");
            return;
        }

        MessageType type = msg.getMessageType();
        if (type == null) {
            sendError("Ismeretlen uzenet tipus");
            return;
        }

        switch (type) {
            case LOGIN             -> handleLogin(msg);
            case CREATE_ROOM       -> handleCreateRoom();
            case JOIN_ROOM         -> handleJoinRoom(msg);
            case JOIN_AS_OBSERVER  -> handleJoinAsObserver(msg);
            case START_GAME        -> handleStartGame();
            case PLACE_TILE        -> handlePlaceTile(msg);
            case PLACE_MEEPLE      -> handlePlaceMeeple(msg);
            case SKIP_MEEPLE       -> handleSkipMeeple();
            default                -> sendError("Ismeretlen uzenet: " + type);
        }
    }

    /**
     * Kezeli a bejelentkezesi kerelmet
     * Ellenőrzi hogy a felhasznalonev szabad e
     *
     * @param msg a bejelentkezesi uzenet
     */
    private void handleLogin(Message msg) {
        String name = msg.getString("username");
        if (name == null || name.isBlank()) {
            sendError("Ervenytelen felhasznalonev");
            return;
        }

        if (server.registerUser(name, this)) {
            this.username = name;
            Message ok = new Message(MessageType.LOGIN_OK);
            ok.put("username", name);
            send(gson.toJson(ok));
            server.broadcastRoomList();
        } else {
            sendError("A felhasznalonev mar foglalt: " + name);
        }
    }

    /** Kezeli az uj szoba letrehozasi kerelmet */
    private void handleCreateRoom() {
        if (!isLoggedIn()) return;
        if (currentRoom != null) {
            sendError("Mar bent vagy egy szobaban");
            return;
        }

        GameRoom room = server.createRoom(this);
        room.addPlayer(this);
        currentRoom = room;
        server.broadcastRoomList();
    }

    /**
     * Kezeli a szobaba csatlakozasi kerelmet
     *
     * @param msg a csatlakozasi uzenet
     */
    private void handleJoinRoom(Message msg) {
        if (!isLoggedIn()) return;
        if (currentRoom != null) {
            sendError("Mar bent vagy egy szobaban");
            return;
        }

        int roomId = msg.getInt("roomId");
        GameRoom room = server.getRoom(roomId);
        if (room == null) {
            sendError("Nem letezik ilyen szoba: " + roomId);
            return;
        }

        if (room.addPlayer(this)) {
            currentRoom = room;
            server.broadcastRoomList();
        } else {
            sendError("Nem lehet csatlakozni a szobához");
        }
    }

    /**
     * Kezeli a megfigyelokent valo csatlakozast
     *
     * @param msg a csatlakozasi uzenet
     */
    private void handleJoinAsObserver(Message msg) {
        if (!isLoggedIn()) return;

        int roomId = msg.getInt("roomId");
        GameRoom room = server.getRoom(roomId);
        if (room == null) {
            sendError("Nem letezik ilyen szoba: " + roomId);
            return;
        }

        room.addObserver(this);
        currentRoom = room;
        observer = true;
    }

    /** Kezeli a jatek inditasi kerelmet */
    private void handleStartGame() {
        if (!isLoggedIn() || currentRoom == null || observer) return;
        currentRoom.startGame(this);
    }

    /** Kezeli a kartya lerakasi kérelmet */
    private void handlePlaceTile(Message msg) {
        if (!isLoggedIn() || currentRoom == null || observer) return;
        int x = msg.getInt("x");
        int y = msg.getInt("y");
        int rotation = msg.getInt("rotation");
        currentRoom.handlePlaceTile(this, x, y, rotation);
    }

    /** Kezeli a meeple lerakasi kérelmet */
    private void handlePlaceMeeple(Message msg) {
        if (!isLoggedIn() || currentRoom == null || observer) return;
        String feature = msg.getString("feature");
        String direction = msg.getString("direction");
        currentRoom.handlePlaceMeeple(this, feature, direction);
    }

    /** Kezeli a meeple kihagyasi kérelmet */
    private void handleSkipMeeple() {
        if (!isLoggedIn() || currentRoom == null || observer) return;
        currentRoom.handleSkipMeeple(this);
    }

    /**
     * Elkuldi egy uzenetet a kliensnek
     *
     * @param message a kuldenő uzenet JSON formaban
     */
    public synchronized void send(String message) {
        if (out != null) out.println(message);
    }

    /**
     * Elkuldi egy hibaüzenetet a kliensnek
     *
     * @param errorMessage a hibaleírás
     */
    private void sendError(String errorMessage) {
        Message err = new Message(MessageType.ERROR);
        err.put("msg", errorMessage);
        send(gson.toJson(err));
    }

    /**
     * Megvizsgalja hogy a kliens be van e jelentkezve
     *
     * @return igaz ha be van jelentkezve
     */
    private boolean isLoggedIn() {
        if (username == null) {
            sendError("Nincs bejelentkezve");
            return false;
        }
        return true;
    }

    /**
     * Kitakarit kilepes vagy kapcsolat megszakadas eseten
     */
    private void cleanup() {
        if (username != null) {
            server.unregisterUser(username);
        }
        if (currentRoom != null) {
            currentRoom.handlePlayerLeft(this);
            if (currentRoom.isEmpty()) {
                server.removeRoom(currentRoom.getRoomId());
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Socket zaras hiba: " + e.getMessage());
        }
    }

    public String getUsername() { return username; }

    public boolean isObserver() { return observer; }
}