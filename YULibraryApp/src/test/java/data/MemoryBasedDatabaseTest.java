package data;

import data.binary.datums.ItemDatum;
import data.databases.MemoryBasedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryBasedDatabaseTest {

    private MemoryBasedDatabase<ItemDatum> database;

    @BeforeEach
    void setUp() {
        database = new MemoryBasedDatabase<>();
    }

    @Test
    void testAddAndRead() {
        String key = "TestItem";
        ItemDatum itemDatum = new ItemDatum(key, "A test item", 1, true);
        Record<ItemDatum> record = new Record<>(key, itemDatum);

        database.add(record);

        Record<ItemDatum> fetchedRecord = database.read(key);
        assertNotNull(fetchedRecord);
        assertEquals(itemDatum, fetchedRecord.getEntry());
    }

    @Test
    void testUpdate() {
        String key = "TestItem";
        ItemDatum originalItemDatum = new ItemDatum(key, "Original item", 1, true);
        Record<ItemDatum> originalRecord = new Record<>(key, originalItemDatum);
        database.add(originalRecord);

        ItemDatum updatedItemDatum = new ItemDatum(key, "Updated item", 2, false);
        Record<ItemDatum> updatedRecord = new Record<>(key, updatedItemDatum);
        database.update(key, updatedRecord);

        Record<ItemDatum> fetchedRecord = database.read(key);
        assertNotNull(fetchedRecord);
        assertEquals(updatedItemDatum, fetchedRecord.getEntry());
    }

    @Test
    void testDelete() {
        String key = "TestItem";
        ItemDatum itemDatum = new ItemDatum(key, "A test item", 1, true);
        Record<ItemDatum> record = new Record<>(key, itemDatum);
        database.add(record);

        database.delete(key);
        assertNull(database.read(key));
    }

    @Test
    void testSearch() {
        database.add(new Record<>("Apple", new ItemDatum("Apple", "Fruit", 10, true)));
        database.add(new Record<>("Banana", new ItemDatum("Banana", "Fruit", 20, false)));
        database.add(new Record<>("Carrot", new ItemDatum("Carrot", "Vegetable", 15, true)));

        List<Record<ItemDatum>> results = database.search("Banana");

        // Check if any record matches the condition
        boolean hasBanana = results.stream()
                .anyMatch(r -> r.getEntry().getId().equals("Banana"));

        // Assert that the condition is true
        assertTrue(hasBanana);
    }

    @Test
    void testFlushAndLoad() throws IOException {
        String key = "TestItem";
        ItemDatum itemDatum = new ItemDatum(key, "A test item", 1, true);
        Record<ItemDatum> record = new Record<>(key, itemDatum);
        database.add(record);

        String filePath = Files.createTempFile("testDatabase", ".dat").toString();
        database.flush(filePath);

        MemoryBasedDatabase<ItemDatum> newDatabase = new MemoryBasedDatabase<>();
        newDatabase.load(filePath);

        Record<ItemDatum> fetchedRecord = newDatabase.read(key);
        assertNotNull(fetchedRecord);
        assertEquals(itemDatum, fetchedRecord.getEntry());

        Files.deleteIfExists(Path.of(filePath)); // Cleanup the temporary file
    }
}
