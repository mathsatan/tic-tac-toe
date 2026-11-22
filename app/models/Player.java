package models;

import org.apache.pekko.stream.javadsl.SourceQueueWithComplete;
import java.util.UUID;

public class Player<T> {

    private final SourceQueueWithComplete<T> connection;
    private final UUID uuid;
    
    public Player(SourceQueueWithComplete<T> out, UUID uuid) {
        this.connection = out;
        this.uuid = uuid;
    }

    public SourceQueueWithComplete<T> connection() {
        return this.connection;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String uuidAsString() {
        return String.valueOf(this.uuid);
    }
}