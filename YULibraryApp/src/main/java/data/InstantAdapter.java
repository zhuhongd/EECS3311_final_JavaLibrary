package data;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {
    public static void registerTypeAdapter(GsonBuilder builder) {
        Type type = new TypeToken<Instant>() {
        }.getType();
        builder.registerTypeAdapter(type, new InstantAdapter());
    }

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        return Instant.parse(in.nextString());
    }
}
