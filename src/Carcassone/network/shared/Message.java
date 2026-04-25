package Carcassone.network.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * Halozati uzenetet reprezental
 * Tartalmaz egy tipust es tetszoleges kulcs ertek parokat
 */
public class Message {

    private final String type;
    private final Map<String, Object> data = new HashMap<>();

    /**
     * Letrehoz egy uj uzenetet a megadott tipussal
     *
     * @param type az uzenet tipusa
     */
    public Message(MessageType type) {
        this.type = type.name();
    }

    /**
     * Hozzaad egy kulcs ertek part az uzenethez
     *
     * @param key   a kulcs
     * @param value az ertek
     * @return maga az uzenet
     */
    public Message put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    /** Get fuggevnyek */
    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        Object val = data.get(key);
        return val == null ? null : val.toString();
    }

    public int getInt(String key) {
        Object val = data.get(key);
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); }
        catch (NumberFormatException e) { return 0; }
    }

    public String getType() { return type; }

    public MessageType getMessageType() {
        try { return MessageType.valueOf(type); }
        catch (IllegalArgumentException e) { return null; }
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    @Override
    public String toString() {
        return "Message{type=" + type + ", data=" + data + "}";
    }
}