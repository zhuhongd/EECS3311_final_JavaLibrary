package data.databases;

import data.Record;

import java.util.List;

/**
 * Represents a generic interface for a database that can store, read, update, and delete records of a specific type.
 *
 * @param <T> The type of the records managed by the database.
 */
public interface IDatabase<T> {
    /**
     * Adds a new record to the database.
     *
     * @param record The record to add.
     */
    void add(Record<T> record);

    /**
     * Reads a record by its key.
     *
     * @param key The key of the record to read.
     * @return The record associated with the given key, or null if no such record exists.
     */
    Record<T> read(String key);

    /**
     * Updates an existing record identified by its key.
     *
     * @param key    The key of the record to update.
     * @param record The new record to replace the existing one.
     */
    void update(String key, Record<T> record);

    /**
     * Deletes a record by its key.
     *
     * @param key The key of the record to delete.
     */
    void delete(String key);

    /**
     * Reads all records in the database.
     *
     * @return A list of all records.
     */
    List<Record<T>> readAll();
}
