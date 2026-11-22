package models;

import play.mvc.*;

import java.util.UUID;

public class Player<T> {

    private WebSocket.Out<T> connection;
    private UUID uuid;
    
    public Player(WebSocket.Out<T> out, UUID uuid) {
        this.connection = out;
        this.uuid = uuid;
    }

    public WebSocket.Out<T> connection() {
        return this.connection;
    }
    public UUID uuid() {
        return this.uuid;
    }

    public String uuidAsString() {
        return String.valueOf(this.uuid);
    }
}