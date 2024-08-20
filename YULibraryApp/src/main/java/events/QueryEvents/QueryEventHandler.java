package events.QueryEvents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.ItemTypeAdapter;
import data.LocalDateAdapter;
import data.ObjectToDatumUtil;
import data.Record;
import data.binary.datums.Datum;
import data.databases.IDatabase;
import events.IEventHandler;
import model.assets.Item;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles query events by performing specified actions (add, update, delete, read) on the database.
 * This handler supports asynchronous execution of database operations and uses Gson for JSON parsing.
 */
public class QueryEventHandler implements IEventHandler<QueryEvent> {

    private final ConcurrentHashMap<String, IDatabase<? extends Datum>> databases;

    private final Gson gson;

    /**
     * Constructs a QueryEventHandler with a map of database identifiers to database instances.
     *
     * @param databases A map where each key is a database identifier and the value is the database instance.
     */
    public QueryEventHandler(ConcurrentHashMap<String, IDatabase<? extends Datum>> databases) {
        this.databases = Objects.requireNonNull(databases, "databases cannot be null");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Item.class, new ItemTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = builder.create();
    }

    /**
     * Handles the given query event by executing the appropriate database action based on the event's query string.
     *
     * @param event The event containing the query to be processed.
     * @return A CompletableFuture that will complete with the result of the database operation.
     */
    @Override
    public CompletableFuture<Object> handleEvent(QueryEvent event) {
        Map<String, String> parsedQuery = parseQuery(event.getQuery());
        IDatabase<? extends Datum> database = databases.get(parsedQuery.get("destination"));
        Datum datum = null;
        if (parsedQuery.containsKey("jsonData")) {
            datum = parseDatum(parsedQuery.get("jsonData"), event.getType());
        }

        switch (parsedQuery.get("action").toLowerCase()) {
            case "add":
                return handleAddAction(database, datum);
            case "update":
                return handleUpdateAction(database, parsedQuery.get("key"), datum);
            case "delete":
                return handleDeleteAction(database, parsedQuery.get("key"));
            case "read":
                return handleReadAction(database, parsedQuery.get("key"));
            default:
                return handleSpecificAction(database, parsedQuery.get("action"), parsedQuery.get("key"), datum);
        }
    }

    /**
     * Parses the given query string into a map containing the parts of the query.
     * The query format is expected to be "destination:action:key:jsonData".
     *
     * @param query The query string to parse.
     * @return A map containing the parts of the query.
     * @throws IllegalArgumentException If the query is null, empty, or does not meet the expected format.
     */
    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty.");
        }

        String[] parts = query.split(":", 4);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid query format. Expected format: destination:action:key:jsonData.");
        }

        Map<String, String> result = new HashMap<>();
        result.put("destination", parts[0].trim());
        result.put("action", parts[1].trim());

        if (parts.length > 2 && !parts[2].trim().isEmpty()) {
            result.put("key", parts[2].trim());
        }
        if (parts.length > 3 && !parts[3].trim().isEmpty()) {
            result.put("jsonData", parts[3].trim());
        }

        if (!result.containsKey("key") && !result.containsKey("jsonData")) {
            throw new IllegalArgumentException("Query must include at least a key or jsonData.");
        }

        return result;
    }

    /**
     * Parses the given JSON data into a Datum object of the specified type.
     *
     * @param jsonData The JSON string representing the data.
     * @param type     The class of the type to which the data should be converted.
     * @return The Datum object parsed from the JSON data.
     * @throws IllegalArgumentException If no Datum can be created from the provided type.
     */
    private Datum parseDatum(String jsonData, Class<?> type) {

        Object source = gson.fromJson(jsonData, type);
        Datum result = ObjectToDatumUtil.getDatum(source);
        if (result == null) {
            throw new IllegalArgumentException("No Datum for the specified type found.");
        }
        return result;
    }

    // The following methods handle the specific actions (add, update, delete, read) by interacting with the database.
    // Each method returns a CompletableFuture that completes with the result of the operation.

    private <D extends Datum> CompletableFuture<Object> handleAddAction(IDatabase<D> database, Datum datum) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Record<D> record = new Record<>(datum.getId(), (D) datum); //safe cast I promise 🥺
                database.add(record);
                return true; // Action successful
            } catch (Exception e) {
                return false; // Action failed
            }
        });
    }

    private <D extends Datum> CompletableFuture<Object> handleUpdateAction(IDatabase<D> database, String key, Datum datum) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Record<D> record = new Record<>(key, (D) datum);
                database.update(key, record);
                return true; // Action successful
            } catch (Exception e) {
                return false; // Action failed
            }
        });
    }

    private CompletableFuture<Object> handleDeleteAction(IDatabase<? extends Datum> database, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                database.delete(key);
                return true; // Action successful
            } catch (Exception e) {
                return false; // Action failed
            }
        });
    }

    private <D extends Datum> CompletableFuture<Object> handleReadAction(IDatabase<D> database, String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Record<D> record = database.read(key);
                if (record != null) {
                    return record.getEntry(); // Return the entry directly
                } else {
                    return "Record not found"; // Or consider returning null or a custom error object
                }
            } catch (Exception e) {
                return "Error reading record"; // Error handling
            }
        });
    }

    private CompletableFuture<Object> handleSpecificAction(IDatabase<? extends Datum> database, String action, String key, Datum datum) {
        return null;
    }

}
