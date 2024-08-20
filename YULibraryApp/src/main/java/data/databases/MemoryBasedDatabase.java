package data.databases;

import data.Record;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A database implementation for storing and manipulating records of type Datum.
 * Utilizes a ConcurrentSkipListSet for thread-safe operations. Supports adding,
 * reading, updating, deleting, searching, and persisting item records.
 */
public class MemoryBasedDatabase<T extends Serializable> implements IDatabase<T>, Serializable {

    private static final Logger LOGGER = Logger.getLogger(MemoryBasedDatabase.class.getName());
    private static final long serialVersionUID = 1L;
    private transient Comparator<Record<T>> comparator = new RecordComparator<>();
    private ConcurrentSkipListSet<Record<T>> records;
    public MemoryBasedDatabase() {
        this.records = new ConcurrentSkipListSet<>(comparator);
    }

    public MemoryBasedDatabase(Comparator<Record<T>> comparator) {
        this.comparator = comparator;
        this.records = new ConcurrentSkipListSet<>(comparator);
    }

    @Override
    public void add(Record<T> record) {
        records.add(record);
    }

    @Override
    public void update(String key, Record<T> updatedRecord) {
        Record<T> existingRecord = read(key);
        if (existingRecord != null) {
            records.remove(existingRecord);
            records.add(updatedRecord);
        }
    }

    @Override
    public void delete(String key) {
        Record<T> record = read(key);
        if (record != null) {
            records.remove(record);
        }
    }

    @Override
    public Record<T> read(String key) {
        Record<T> searchRecord = new Record<>(key, null); // Dummy record for comparison
        return records.ceiling(searchRecord);
    }

    @Override
    public List<Record<T>> readAll() {
        return new ArrayList<>(records);
    }

    public List<Record<T>> search(String key) {
        List<Record<T>> closestMatches = new ArrayList<>();
        Record<T> searchRecord = new Record<>(key, null);

        Record<T> closest = findClosestMatch(searchRecord);
        if (closest != null) {
            populateClosestMatches(closestMatches, closest);
        }
        return closestMatches;
    }

    private Record<T> findClosestMatch(Record<T> searchRecord) {
        Record<T> closest = records.ceiling(searchRecord);
        return closest != null ? closest : records.floor(searchRecord);
    }

    private void populateClosestMatches(List<Record<T>> closestMatches, Record<T> closest) {
        closestMatches.add(closest);
        findSurroundingMatches(closestMatches, closest);
    }

    private void findSurroundingMatches(List<Record<T>> closestMatches, Record<T> referenceRecord) {
        Record<T> lower = records.lower(referenceRecord);
        Record<T> higher = records.higher(referenceRecord);

        while (closestMatches.size() < 5 && (lower != null || higher != null)) {
            if (lower != null) {
                closestMatches.add(lower);
                lower = records.lower(lower);
            }
            if (closestMatches.size() < 5 && higher != null) {
                closestMatches.add(higher);
                higher = records.higher(higher);
            }
        }
    }

    public void flush(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(records);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to flush data to file: " + filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void load(String filepath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            records = (ConcurrentSkipListSet<Record<T>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load data from file: " + filepath, e);
        }
    }

    private static class RecordComparator<T> implements Comparator<Record<T>>, Serializable {
        @Override
        public int compare(Record<T> o1, Record<T> o2) {
            // Assuming getKey() returns a Comparable object that is also Serializable
            return o1.getKey().compareTo(o2.getKey());
        }
    }
}
