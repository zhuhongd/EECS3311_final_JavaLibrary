package data.databases;


import data.Record;
import data.binary.datums.Datum;
import data.parsers.binary.BinaryParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;

// T is the object we are storing in *binary* form
public class HashBasedDatabase<T extends Datum> implements IDatabase<T> {

    private static final int ENTRY_FLAG_SIZE = Byte.BYTES;

    private static final Logger LOGGER = Logger.getLogger(HashBasedDatabase.class.getName());

    private static final int MAX_HASH_FAILS = 100;

    private static final int MAX_RETRIES = 30;

    private static final int CHANGE_THRESHOLD = 100;
    private final String path;
    private final ConcurrentHashMap<String, Record<T>> changes = new ConcurrentHashMap<>();
    private final AtomicInteger entryNum = new AtomicInteger(0);
    private final int maxRecords; //should be prime for hash-collision reasons
    BinaryParser<T> binaryParser;
    private MappedByteBuffer mappedByteBuffer;


    /**
     * Constructs a UserDatabase instance and initializes the database file.
     *
     * @param path The file system path where the database file is located.
     */

    public HashBasedDatabase(String path, int maxRecords, BinaryParser<T> binaryParser) {
        this.path = path;
        this.maxRecords = maxRecords;
        this.binaryParser = binaryParser;
        initializeDatabaseFile();
    }

    /**
     * Initializes the database file, mapping it into memory for read/write operations.
     * The file is created if it does not exist, and is opened with read and write capabilities.
     */
    private void initializeDatabaseFile() {
        try (FileChannel channel = FileChannel.open(Paths.get(path),
                READ, WRITE, CREATE)) {
            //  If needed, we can map multiple files later on (perhaps based on the first two hash letters)
            this.mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, (long) maxRecords * binaryParser.getSize());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database file", e);
        }
    }

    /**
     * Adds a new record to the database. If the cache has reached the CHANGE_THRESHOLD, triggers a flush to disk.
     *
     * @param record The record to add.
     */

    @Override
    public void add(Record<T> record) {
        LOGGER.info("ADD RECORD: " + record.getKey());
        changes.compute(record.getKey(), (key, existingRecord) -> record);
        flushIfRequired();
    }


    /*
     * Reads a record from the database based on the provided key. If no record exists for the given key, null is returned.
     * If the record is not found in the cache, it is then read from the disk-based storage.
     * <p>The read operation involves calculating the hash-based position of the record in the file, followed by linear probing in case of hash collisions. This process is repeated until the record is found or the maximum number of hash fails is reached.</p>
     *
     * @param keyStr The key of the record to read.
     * @return The record associated with the given key, or `null` if no such record exists.
     *
     */
    public synchronized Record<T> read(String keyStr) {
        LOGGER.info("START READ , KEY:" + keyStr);
        flush();
        MappedByteBuffer mbb = this.mappedByteBuffer;

        // Parse the key to an integer as before.
        int positionInFile = linearProbe(mbb, keyStr, true, false);

        if (positionInFile >= 0) {
            // Found the key, proceed to read the record.
            mbb.position(positionInFile);
            byte[] entryBytes = new byte[binaryParser.getSize()];
            mbb.get(entryBytes); // Read the entry into a byte array

            Record<T> fileEntry = binaryParser.parseData(entryBytes);
            return fileEntry;
        } else {
            // Key not found or EOF reached.
            return null;
        }
    }


    /**
     * Updates an existing record in the database. If the cache has reached the CHANGE_THRESHOLD, triggers a flush to disk.
     *
     * @param key    The key of the record to update.
     * @param record The new record to replace the existing one.
     */
    @Override
    public void update(String key, Record<T> record) {
        add(record);
    }

    /**
     * Deletes a record from the database by marking it for deletion in the cache. If the cache has reached the CHANGE_THRESHOLD,
     * triggers a flush to disk.
     *
     * @param key The key of the record to delete.
     */
    @Override
    public void delete(String key) {
        changes.compute(key, (k, existingRecord) -> new Record<>(key, null));
        flushIfRequired();
    }


    /**
     * Reads all records from the database. This method iterates over the entire memory-mapped file, deserializing and returning all valid records.
     * It is important to note that this operation might be resource-intensive and should be used with caution on large databases.
     *
     * @return A list of all records in the database.
     * @throws IllegalStateException If the file does not contain the expected number of valid entries.
     */
    @Override
    public synchronized List<Record<T>> readAll() throws IllegalStateException {
        flush();
        ArrayList<Record<T>> retList = new ArrayList<>();
        int counter = entryNum.get();
        mappedByteBuffer.position(0);

        byte[] entryBytes = new byte[binaryParser.getSize()];
        while (counter > 0 && mappedByteBuffer.remaining() >= binaryParser.getSize()) {
            mappedByteBuffer.get(entryBytes);
            Record<T> record = binaryParser.parseData(entryBytes);
            if (record.getEntry().getFlag()) {
                counter--;
                retList.add(record);
            }

        }

        return retList;
    }

    public void close() {
        mappedByteBuffer.force();
        mappedByteBuffer = null;

    }

    private void flushIfRequired() {
        // Only try to acquire write lock if CHANGE_THRESHOLD is reached, but do it outside readLock block
        if (entryNum.getAndIncrement() % CHANGE_THRESHOLD == 0) {
            flush(mappedByteBuffer);
        }
    }

    /**
     * Flushes the in-memory cache to the disk. This method is called automatically when the number of changes since the last flush reaches the configured threshold.
     * It can also be called explicitly to force a flush of the cache to disk. The flush operation involves serializing the records to a binary format and writing them to the correct positions in the file.
     *
     * <p>This operation is synchronized to prevent concurrent modifications to the cache or the memory-mapped file during the flushing process. It includes error handling to log failures and prevent partial updates from corrupting the database file.</p>
     */

    //default version
    public synchronized void flush() {
        flush(mappedByteBuffer);
    }

    public synchronized void flush(MappedByteBuffer mbb) {
        LOGGER.info("START FLUSH");
        boolean success = true;
        HashMap<Integer, Record<T>> backup = new HashMap<>();
        HashMap<String, Record<T>> copy = new HashMap<>(changes); // allows us to still add changes
        for (Map.Entry<String, Record<T>> entry : copy.entrySet()) {
            //Get the correct position of the entry
            int positionInFile = linearProbe(mappedByteBuffer, entry.getValue().getKey(), false, true);                    //Get Previous
            mbb.position(positionInFile);
            byte[] previousBytes = new byte[binaryParser.getSize()];
            Record<T> previous = binaryParser.parseData(previousBytes);
            backup.put(positionInFile, previous);

            //Attempt write
            try {
                Record<T> record = entry.getValue();
                writeRecordToDisk(mbb, positionInFile, record);
            } catch (IOException e) {
                success = false;
                break;
            }
        }

        if (success) {
            changes.clear();
        } else {
            for (Map.Entry<Integer, Record<T>> entry : backup.entrySet()) {
                try {
                    writeRecordToDisk(mbb, entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    LOGGER.severe("COULD NOT FLUSH. FAILED AT DATABASE TO PREVIOUS STATE.");
                    throw new RuntimeException(e);
                }
            }
            LOGGER.severe("COULD NOT FLUSH. RESTORED DATABASE TO PREVIOUS STATE.");
        }
    }


    private void writeRecordToDisk(MappedByteBuffer mbb, int positionInFile, Record<T> record) throws IOException {
        //Delete Record
        if (record.getEntry() == null) {
            LOGGER.info("DELETING RECORD : " + record.getKey());
            byte[] zeros = new byte[binaryParser.getSize()];
            mbb.position(positionInFile);
            mbb.put(zeros);
            return;
        }
        record.getEntry().setFlag(true);
        ByteBuffer writeBuffer = ByteBuffer.allocate(binaryParser.getSize());
        writeBuffer.put(binaryParser.getData(record));

        mbb.position(positionInFile);
        writeBuffer.flip(); // Prepare the buffer for reading
        mbb.put(writeBuffer);
        LOGGER.info("RECORD WRITTEN : " + record.getKey());

    }

    public synchronized int linearProbe(MappedByteBuffer mbb, String keyStr, boolean searchForKey, boolean allowWrap) {
        int key = Integer.parseInt(keyStr);
        int pos = calculateInitialPosition(keyStr);
        byte[] entryBytes = new byte[binaryParser.getSize()];

        for (int i = 0; i < MAX_HASH_FAILS; i++) {
            if (pos + binaryParser.getSize() > mbb.limit()) {
                if (allowWrap) {
                    pos = 0; // Wrap to the beginning if allowed
                } else {
                    return -1; // If not allowed to wrap, fail the operation
                }
            }

            mbb.position(pos);
            mbb.get(entryBytes);
            Record<T> fileEntry = binaryParser.parseData(entryBytes);

            if (searchForKey) {
                // Searching for a specific key
                if (Integer.parseInt(fileEntry.getKey()) == key) {
                    return pos; // Found the key, return position
                }
            } else {
                // Searching for an empty or matching slot
                if (!fileEntry.getEntry().getFlag() || Integer.parseInt(fileEntry.getKey()) == key) {
                    return pos; // Found a suitable slot, return position
                }
            }

            pos += binaryParser.getSize(); // Move to the next position
            if (pos >= mbb.limit() && allowWrap) pos = 0; // Optionally wrap around
        }

        return -1; // If loop exits, no suitable position was found
    }


    /**
     * Calculates the initial file position for a given key. This is based on the hash code of the key modulo the maximum number of records. This position serves as the starting point for reading or writing a record.
     *
     * @param key The key for which to calculate the file position.
     * @return The calculated position in the file.
     */
    private int calculateInitialPosition(String key) {
        int hashCode = Math.abs(key.hashCode());
        int index = hashCode % maxRecords;
        return index * binaryParser.getSize();
    }

}