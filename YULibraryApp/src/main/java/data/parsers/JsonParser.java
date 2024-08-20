package data.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import data.InstantAdapter;

import java.lang.reflect.Type;

/**
 * A generic class for parsing objects to and from JSON strings.
 * It uses Gson for serialization and deserialization, handling generics by preserving type information.
 *
 * @param <T> the type of objects this parser works with
 */


// TODO : This class currently serializes objects in a non-space efficient manner, fix this
public class JsonParser<T> implements IDataParser<String, T> {
    private final Gson gson; // Gson instance for handling the serialization and deserialization
    private final Type typeOfT; // Type information of T to overcome type erasure

    /**
     * Constructs a JsonParser for the specified type.
     * It initializes Gson with custom type adapters if needed and stores the type of T.
     *
     * @param typeToken a TypeToken representing the type T. Necessary to preserve type information at runtime.
     */
    public JsonParser(TypeToken<T> typeToken) {
        GsonBuilder builder = new GsonBuilder();
        InstantAdapter.registerTypeAdapter(builder); // Register custom type adapters if needed
        this.gson = builder.create();
        this.typeOfT = typeToken.getType(); // Capture and preserve the type information of T
    }

    /**
     * Serializes an object of type T to its JSON representation.
     *
     * @param obj the object to serialize
     * @return a JSON string representing the object
     */
    @Override
    public String getData(T obj) {
        return gson.toJson(obj); // Convert the object to JSON string
    }

    /**
     * Deserializes a JSON string to an object of type T.
     *
     * @param data the JSON string to deserialize
     * @return an object of type T represented by the input JSON string
     */
    @Override
    public T parseData(String data) {
        return gson.fromJson(data, typeOfT); // Convert the JSON string back to an object of type T
    }
}
