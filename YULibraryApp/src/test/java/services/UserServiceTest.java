package services;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.LocalDateAdapter;
import data.binary.datums.*;
import data.databases.HashBasedDatabase;
import data.databases.IDatabase;
import data.databases.MemoryBasedDatabase;
import data.parsers.binary.CourseDatumRecordParser;
import data.parsers.binary.LibraryContractDatumRecordBinaryParser;
import data.parsers.binary.UserDatumRecordBinaryParser;
import events.EventBus;
import events.QueryEvents.QueryEvent;
import events.QueryEvents.QueryEventHandler;
import model.assets.Course;
import model.assets.Item;
import model.assets.PhysicalItem;
import model.clients.Faculty;
import model.clients.Student;
import model.contracts.LibraryContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.data.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserServiceTest {
    private final EventBus eventBus = new EventBus(1);
    private final File userDB;
    private final File contractDB;
    private final File courseDB;

    HashBasedDatabase<UserDatum> userDatabase;
    HashBasedDatabase<CourseDatum> courseDatabase;
    HashBasedDatabase<LibraryContractDatum> contractDatabase;
    MemoryBasedDatabase<ItemDatum> itemDatabase;

    public UserServiceTest() throws IOException {
        userDB = File.createTempFile("userDB", ".bin");
        contractDB = File.createTempFile("contractDB", ".bin");
        courseDB = File.createTempFile("itemDB", ".bin");
    }


    @BeforeEach
    public void setUp() throws IOException {
        userDatabase = new HashBasedDatabase<>(userDB.getPath(), 100, new UserDatumRecordBinaryParser());
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
        // Close or delete user database
        Files.deleteIfExists(userDB.toPath());

        // Close or delete course database
        Files.deleteIfExists(courseDB.toPath());

        // Close or delete contract database
        Files.deleteIfExists(contractDB.toPath());

    }

    @Test
    public void getStudentTest() {
        UserService us = new UserService(eventBus);
        Student student = new Student("email", "username", "passwordHash");

        PhysicalItem textbook = new PhysicalItem("Title", "Author");
        LibraryContract contract = new LibraryContract(student.getId(), "123");

        student.addTextbook(textbook);
        student.addContract(contract);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(LocalDate.class,new LocalDateAdapter()) .create();

        // Add user first
        CompletableFuture<Void> testSequence = us.addUser(student)
                .thenCompose(v -> {
                    // After adding user, publish event to add contract
                    String contractJson = gson.toJson(contract);
                    String itemJson = gson.toJson(textbook);
                    return eventBus.publish(new QueryEvent("itemDB:add::" + itemJson, Item.class))
                            // After publishing the first event, publish the second event
                            .thenCompose(event -> eventBus.publish(new QueryEvent("contractDB:add::" + contractJson, LibraryContract.class)))
                            // After the second event, get the user
                            .thenCompose(event -> us.getUser(student.getId()));
                })
                .thenAccept(retStudent -> {
                    assertEquals(student, retStudent);
                });

        // Wait for all async operations to complete
        testSequence.join();
    }

    @Test
    public void getFacultyTest() {
        UserService us = new UserService(eventBus);
        Faculty faculty = new Faculty("email", "username", "passwordHash");

        PhysicalItem textbook = new PhysicalItem("Title", "Author");
        PhysicalItem prev_textbook = new PhysicalItem("PTitle", "PAuthor");

        Course course = new Course(textbook,"Course", LocalDate.now());

        faculty.addCourse(course);
        faculty.addPreviousBook(prev_textbook);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.registerTypeAdapter(LocalDate.class,new LocalDateAdapter()) .create();

        // Add user first
        CompletableFuture<Void> testSequence = us.addUser(faculty)
                .thenCompose(v -> {
                    // After adding user, publish event to add course
                    String courseJson = gson.toJson(course);
                    String textbookJson = gson.toJson(textbook);
                    String prevJson = gson.toJson(prev_textbook);

                    return eventBus.publish(new QueryEvent("itemDB:add::" + textbookJson, Item.class))
                            // After publishing the first event, publish the second event
                            .thenCompose(event -> eventBus.publish(new QueryEvent("courseDB:add::" + courseJson, Course.class)))
                            .thenCompose(event -> eventBus.publish(new QueryEvent("itemDB:add::" + prevJson, Item.class)))
                            .thenCompose(event -> us.getUser(faculty.getId()));
                })
                .thenAccept(retFaculty -> {
                    assertEquals(faculty, retFaculty);
                });

        // Wait for all async operations to complete
        testSequence.join();
    }
}