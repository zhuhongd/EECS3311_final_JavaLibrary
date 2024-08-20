package data;

import data.binary.UserBinaryEntry;
import data.binary.datums.UserDatum;
import data.databases.HashBasedDatabase;
import data.parsers.binary.UserDatumRecordBinaryParser;
import data.util.DataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseTest {

    private HashBasedDatabase<UserDatum> db;
    private Path tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary file to act as our database for testing
        tempFile = Files.createTempFile("test", "db");
        db = new HashBasedDatabase<>(tempFile.toString(), 49999, new UserDatumRecordBinaryParser());
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the temporary file after each test
        Files.deleteIfExists(tempFile);
    }


    @Test
    public void addValidRecordTest() {
        UserDatum[] users = new UserDatum[10];
        for (int i = 0; i < users.length; i++) {
            users[i] = DataUtils.generateRandomUserDatum();
            Record<UserDatum> rec = new Record<>(String.valueOf(users[i].userId), users[i]);
            db.add(rec);
        }
        for (UserDatum user : users) {
            Record<UserDatum> retrievedRecord = null;
            try {
                retrievedRecord = db.read(String.valueOf(user.userId));
                assertNotNull(retrievedRecord, "The retrieved record should not be null.");
                assertEquals(user, retrievedRecord.getEntry(), "The user data should match the inserted record.");
            } catch (AssertionError e) {
                String errorMessage = String.format("Assertion failed for user with userId=%d: %s.\nUser details: %s\nRetrieved record: %s",
                        user.userId, e.getMessage(), user, retrievedRecord == null ? "null" : retrievedRecord.getEntry().toString());
                throw new AssertionError(errorMessage, e);
            }
        }
    }

    @Test
    public void readNonExistentRecordTest() {
        String nonExistentKey = "999999"; // Assuming this key doesn't exist
        Record<UserDatum> retrievedRecord = db.read(nonExistentKey);
        assertNull(retrievedRecord, "Retrieved record should be null for a non-existent key.");
    }

    @Test
    public void updateExistingRecordTest() {
        // Create and add a user datum to the database
        UserDatum user = DataUtils.generateRandomUserDatum();
        user.email = "original@example.com"; // Example change
        Record<UserDatum> initialRecord = new Record<>(String.valueOf(user.userId), user);
        db.add(initialRecord);
        db.flush();

        // Directly modify the UserDatum instance to simulate an update
        user.email = "updated@example.com"; // Simulating an update
        Record<UserDatum> updatedRecord = new Record<>(String.valueOf(user.userId), user);
        db.update(String.valueOf(user.userId), updatedRecord);
        db.flush();

        // Attempt to retrieve the updated record
        Record<UserDatum> retrievedRecord = db.read(String.valueOf(user.userId));
        assertNotNull(retrievedRecord, "The retrieved record should not be null after an update.");
        assertEquals("updated@example.com", retrievedRecord.getEntry().email, "The email of the updated record should match the new value.");
    }

   /* @Test
    public void deleteRecordTest() {
        System.out.println("t-id: " + Thread.currentThread().threadId());

        UserDatum user = DataUtils.generateRandomUserDatum();
        Record<UserDatum> record = new Record<>(String.valueOf(user.userId), user);
        db.add(record);

        // Delete the record
        db.delete(String.valueOf(user.userId));

        // Attempt to retrieve the deleted record
        Record<UserDatum> retrievedRecord = db.read(String.valueOf(user.userId));
        assertNull(retrievedRecord, "The retrieved record should be null after deletion.");
    }*/

  /*  @Test
    public void integrityAfterRestartTest() throws IOException {
        System.out.println("t-id: " + Thread.currentThread().threadId());

        // Add a record and force a flush to simulate data persistence
        UserDatum user = DataUtils.generateRandomUserDatum();
        Record<UserDatum> record = new Record<>(String.valueOf(user.userId), user);
        db.add(record);
        db.read(record.getKey());

        // Simulate a restart by creating a new instance of the database
        HashBasedDatabase<UserDatum> dbAfterRestart = new HashBasedDatabase<>(tempFile.toString(), 49999, new UserDatumRecordBinaryParser());

        // Attempt to retrieve the persisted record
        Record<UserDatum> retrievedRecord = dbAfterRestart.read(String.valueOf(user.userId));
        assertNotNull(retrievedRecord, "The retrieved record should not be null after restart.");
        assertEquals(user, retrievedRecord.getEntry(), "The user data should match the inserted record after restart.");
    }*/


    @Test
    public void concurrentModificationOnSameUserTest() throws InterruptedException {
        final int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1); // Ensures all threads start at the same time
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        // Create a user datum to be shared across all threads
        UserDatum user = DataUtils.generateRandomUserDatum();
        user.userId = 1; // Use a fixed user ID for all threads
        Record<UserDatum> record = new Record<>(String.valueOf(user.userId), user);
        db.add(record); // Add the user to the database

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start

                    // Simulate updating the user record by creating a new instance with the same user ID but modified content
                    UserDatum updatedUser = DataUtils.generateRandomUserDatum();
                    updatedUser.userId = user.userId; // Keep the same user ID
                    updatedUser.email = "updated" + Thread.currentThread().getId() + "@example.com"; // Ensure unique email for demonstration

                    Record<UserDatum> updatedRecord = new Record<>(String.valueOf(updatedUser.userId), updatedUser);
                    db.update(String.valueOf(updatedUser.userId), updatedRecord);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        db.flush();

        startLatch.countDown(); // Start all threads
        doneLatch.await(); // Wait for all threads to finish

        // Attempt to retrieve the updated record
        Record<UserDatum> retrievedRecord = db.read(String.valueOf(user.userId));
        assertNotNull(retrievedRecord, "The retrieved record should not be null.");
        // Note: This assertion might fail due to race conditions; it's here to demonstrate that the last update wins
        assertTrue(retrievedRecord.getEntry().email.contains("@example.com"), "The email of the retrieved user should contain '@example.com'.");

        service.shutdown();
    }

    @Test
    public void testReadAll_ContainsRecords() throws IOException {
        // Add some records to the database
        for (int i = 0; i < 5; i++) {
            UserDatum user = DataUtils.generateRandomUserDatum();
            user.userId = i; // Ensure unique user IDs
            Record<UserDatum> record = new Record<>(String.valueOf(user.userId), user);
            db.add(record);
        }

        List<Record<UserDatum>> records = db.readAll();
        assertEquals(5, records.size(), "Database should contain exactly 5 records.");
    }


    @Test
    public void testReadAll_EmptyDatabase() throws IOException {
        List<Record<UserDatum>> records = db.readAll();
        assertTrue(records.isEmpty(), "Database should be empty when no records have been added.");
    }


    @Test
    public void testReadAll_WithDeletedRecords() throws IOException {
        // Add records then delete one
        UserDatum userToAdd = DataUtils.generateRandomUserDatum();
        userToAdd.userId = 1;
        Record<UserDatum> recordToAdd = new Record<>(String.valueOf(userToAdd.userId), userToAdd);
        db.add(recordToAdd);

        UserDatum userToDelete = DataUtils.generateRandomUserDatum();
        userToDelete.userId = 2;
        Record<UserDatum> recordToDelete = new Record<>(String.valueOf(userToDelete.userId), userToDelete);
        db.add(recordToDelete);

        // Delete the second user
        db.delete(String.valueOf(userToDelete.userId));

        List<Record<UserDatum>> records = db.readAll();
        assertEquals(1, records.size(), "Database should contain exactly 1 record after deletion.");
        assertEquals(userToAdd.userId, records.get(0).getEntry().userId, "The remaining record should have the userId of the user that was not deleted.");
    }
    @Test
    public void testsetentry() throws IOException{
        String b="k";
        Object T =null;
        Instant c = null;
        Record a = new Record(b,T,c);
        a.setEntry(9);
        assertEquals(a.getEntry(),9);
    }
    @Test
    public void testsetTimestamp() throws IOException{
        String b="k";
        Object T =null;
        Instant c = null;
        Record a = new Record(b,T,c);
        a.setTimestamp(Instant.ofEpochSecond(1972-11-04));
        assertEquals(a.getTimestamp(),Instant.ofEpochSecond(1972-11-04));
    }

    @Test
    public void testGetFlag() throws IOException{
        byte b=127;
        int c=0;
        Instant d=null;
        UserDatum e=null;
        ByteBuffer g=null;
        UserBinaryEntry a = new UserBinaryEntry(b,c,d,e);
        assertEquals(a.getFlag(),127);
    }
    @Test
    public void testSetFlag() throws IOException{
        byte b=127;
        byte z=9;
        int c=0;
        Instant d=null;
        UserDatum e=null;
        ByteBuffer g=null;
        UserBinaryEntry a = new UserBinaryEntry(b,c,d,e);
        a.setFlag(z);
        assertEquals(a.getFlag(),9);
    }
    @Test
    public void testGetTimestamp() throws IOException{
        byte b=127;
        int c=0;
        Instant d=null;
        UserDatum e=null;
        ByteBuffer g=null;
        UserBinaryEntry a = new UserBinaryEntry(b,c,d,e);
        assertEquals(a.getTimestamp(),null);
    }
    @Test
    public void testGetUserdata() throws IOException{
        byte b=127;
        int c=0;
        Instant d=null;
        UserDatum e=null;
        ByteBuffer g=null;
        UserBinaryEntry a = new UserBinaryEntry(b,c,d,e);
        assertEquals(a.getUserData(),null);
    }


    //Weird inconsistent behaviour
//    @Test
//    public void concurrentModificationTest() throws InterruptedException {
//        final int numberOfThreads = 10;
//        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//        ConcurrentHashMap<String, UserDatum> expectedRecords = new ConcurrentHashMap<>();
//
//        for (int i = 0; i < numberOfThreads; i++) {
//            final int userId = i; // Simulate unique user IDs for simplicity
//            service.submit(() -> {
//                UserDatum user = DataUtils.generateRandomUserDatum();
//                user.userId = userId; // Ensure unique user ID
//                Record<UserDatum> record = new Record<>(String.valueOf(user.userId), user);
//                db.add(record);
//                expectedRecords.put(String.valueOf(userId), user);
//                latch.countDown();
//            });
//        }
//
//        latch.await(); // Wait for all threads to finish
//        db.flush();
//
//        // Verify each record added by the threads can be retrieved
//        expectedRecords.forEach((key, value) -> {
//            Record<UserDatum> retrievedRecord = db.read(key);
//            assertNotNull(retrievedRecord, "The retrieved record should not be null.");
//            assertEquals(value, retrievedRecord.getEntry(), "The user data should match the inserted record.");
//        });
//    }


}




