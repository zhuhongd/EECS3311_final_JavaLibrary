package events;

import com.google.gson.Gson;
import data.ObjectToDatumUtil;
import data.binary.datums.*;
import data.databases.HashBasedDatabase;
import data.databases.IDatabase;
import data.databases.MemoryBasedDatabase;
import data.parsers.binary.CourseDatumRecordParser;
import data.parsers.binary.LibraryContractDatumRecordBinaryParser;
import data.parsers.binary.UserDatumRecordBinaryParser;
import events.QueryEvents.QueryEvent;
import events.QueryEvents.QueryEventHandler;
import model.clients.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class QueryEventTest {

    private final EventBus eventBus = new EventBus(1);
    private final File userDB;
    private final File contractDB;
    private final File courseDB;

    HashBasedDatabase<UserDatum> userDatabase;
    HashBasedDatabase<CourseDatum> courseDatabase;
    HashBasedDatabase<LibraryContractDatum> contractDatabase;
    MemoryBasedDatabase<ItemDatum> itemDatabase;

    public QueryEventTest() throws IOException {
        userDB = File.createTempFile("userDB", ".bin");
        contractDB = File.createTempFile("contractDB", ".bin");
        courseDB = File.createTempFile("itemDB", ".bin");
    }


    @BeforeEach
    public void setUp() throws IOException {
        userDatabase = new HashBasedDatabase<>(userDB.getPath(), 10000, new UserDatumRecordBinaryParser());
        courseDatabase = new HashBasedDatabase<>(courseDB.getPath(), 100, new CourseDatumRecordParser());
        contractDatabase = new HashBasedDatabase<>(contractDB.getPath(), 100, new LibraryContractDatumRecordBinaryParser());
        itemDatabase = new MemoryBasedDatabase<>();

        ConcurrentHashMap<String, IDatabase<? extends Datum>> dbMap = new ConcurrentHashMap<>();
        dbMap.put("userDB", userDatabase);
        dbMap.put("courseDB", courseDatabase);
        dbMap.put("contractDB", contractDatabase);
        dbMap.put("itemDB", itemDatabase);


        eventBus.registerHandler(QueryEvent.class, new QueryEventHandler(dbMap));
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Assuming HashBasedDatabase and MemoryBasedDatabase have close or similar methods to release resources
        // If not, adapt this part to whatever mechanism your databases use for cleanup

        // Close or delete user database
        Files.deleteIfExists(userDB.toPath());

        // Close or delete course database
        Files.deleteIfExists(courseDB.toPath());

        // Close or delete contract database
        Files.deleteIfExists(contractDB.toPath());

    }


    @RepeatedTest(1)
    public void testAddAndReadHash() throws Exception {
        Gson json = new Gson();
        Student addStudent = new Student("email", "username", "passwordHash");

        // Create a CompletableFuture for the test sequence
        CompletableFuture<Void> testSequence = eventBus.publish(new QueryEvent("userDB:add::" + json.toJson(addStudent), Student.class))
                .thenCompose(res -> eventBus.publish(new QueryEvent("userDB:read:" + addStudent.getId(), Student.class)))
                .thenAccept(ret -> assertEquals(ObjectToDatumUtil.getDatum(addStudent), ret))
                .exceptionally(ex -> {
                    fail("Test failed with exception: " + ex.getMessage());
                    return null;
                });

        // Wait for the test sequence to complete
        testSequence.get(); // This will wait for the completion and throw an exception if something went wrong
    }


    @Test
    public void testUpdateAction() throws Exception {
        Gson json = new Gson();
        Student originalStudent = new Student("email@example.com", "originalUsername", "originalPasswordHash");
        String originalJson = json.toJson(originalStudent);

        // Add the original student to the database
        eventBus.publish(new QueryEvent("userDB:add::" + originalJson, Student.class)).get();

        Student updatedStudent = new Student("newemail@example.com", "originalUsername", "newPasswordHash");
        String updatedJson = json.toJson(updatedStudent);

        // Update the student in the database
        CompletableFuture<Object> updateFuture = eventBus.publish(new QueryEvent("userDB:update:" + originalStudent.getId() + ":" + updatedJson, Student.class));
        assertTrue((Boolean) updateFuture.get());

        // Read the updated student from the database
        CompletableFuture<Object> readFuture = eventBus.publish(new QueryEvent("userDB:read:" + originalStudent.getId(), Student.class));
        assertEquals(ObjectToDatumUtil.getDatum(updatedStudent), readFuture.get());
    }

    @Test
    public void testDeleteAction() throws Exception {
        Gson json = new Gson();
        Student studentToDelete = new Student("delete@example.com", "deleteUsername", "deletePasswordHash");
        String studentJson = json.toJson(studentToDelete);

        // Add the student to be deleted to the database
        eventBus.publish(new QueryEvent("userDB:add::" + studentJson, Student.class)).get();

        // Delete the student from the database
        CompletableFuture<Object> deleteFuture = eventBus.publish(new QueryEvent("userDB:delete:" + studentToDelete.getId(), Student.class));
        assertTrue((Boolean) deleteFuture.get());

        // Attempt to read the deleted student
        CompletableFuture<Object> readFuture = eventBus.publish(new QueryEvent("userDB:read:" + studentToDelete.getId(), Student.class));
        assertNotEquals(ObjectToDatumUtil.getDatum(studentToDelete), readFuture.get());
    }

    @Test
    public void testInvalidQueryFormat() {
        CompletableFuture<Object> future = eventBus.publish(new QueryEvent("invalidFormat", Student.class));
        ExecutionException thrown = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }


}
