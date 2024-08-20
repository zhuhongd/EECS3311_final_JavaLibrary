package data;

import java.io.Serializable;
import java.time.Instant;

public class Record<T> implements Serializable {
    private final String key;
    private Instant timestamp;
    private T entry;

    public Record(String key, T entry) {
        this.key = key;
        this.timestamp = Instant.now();
        this.entry = entry;
    }

    public Record(String key, T entry, Instant timestamp) {
        this.timestamp = timestamp;
        this.entry = entry;
        this.key = key;
    }


    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public T getEntry() {
        return entry;
    }

    public void setEntry(T entry) {
        this.entry = entry;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", timestamp=" + timestamp +
                ", entry=" + entry +
                '}';
    }

}
