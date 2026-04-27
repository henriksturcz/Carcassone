package Carcassone.network.client;

import Carcassone.network.shared.Message;
import Carcassone.network.shared.MessageType;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A szerver kapcsolatot kezeli a kliens oldalon
 * Egy hatter daemon szalon figyeli a beerkező uzeneteket
 */
public class ServerConnection {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();
    private final Gson gson = new Gson();
    private boolean connected = false;

    /**
     * Csatlakozik a szerverhez
     *
     * @param host a szerver cime
     * @param port a szerver portja
     * @throws IOException ha a csatlakozas sikertelen
     */
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;

        Thread listenerThread = new Thread(this::listenLoop);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * A figyelo szal fo ciklusa
     * Beolvassa az uzeneteket es ertesiti a listenereket
     */
    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    Message msg = gson.fromJson(line, Message.class);
                    for (MessageListener listener : listeners) {
                        listener.onMessage(msg);
                    }
                } catch (Exception e) {
                    System.err.println("Uzenet feldolgozas hiba: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Kapcsolat megszakadt: " + e.getMessage());
        } finally {
            connected = false;
            Message errorMsg = new Message(MessageType.ERROR);
            errorMsg.put("msg", "Kapcsolat megszakadt");
            for (MessageListener listener : listeners) {
                listener.onMessage(errorMsg);
            }
        }
    }

    /** Elkuld egy uzenetet a szervernek */
    public synchronized void send(Message message) {
        if (out != null && connected) {
            out.println(gson.toJson(message));
        }
    }

    /** Hozzaad egy uzenet listenert */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    /** Eltavolit egy uzenet listenert */
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /** Lezarja a kapcsolatot */
    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Disconnect hiba: " + e.getMessage());
        }
    }

    public boolean isConnected() { return connected; }
}