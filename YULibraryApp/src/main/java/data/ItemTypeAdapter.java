package data;

import com.google.gson.*;
import model.assets.Item;
import model.assets.PhysicalItem;

import java.lang.reflect.Type;

public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Item.class, new ItemTypeAdapter());
        Gson gson = gsonBuilder.create();

        // Example serialization
        PhysicalItem item = new PhysicalItem("Title", "Author");
        item.setLocation("Library");
        item.setCopiesAvailable(5);
        item.setLost(false);

        String json = gson.toJson(item);
        // Example deserialization
        Item deserializedItem = gson.fromJson(json, Item.class);
    }

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        if (src instanceof PhysicalItem) {
            result.addProperty("type", "PhysicalItem");
            // Serialize common and specific properties
            result.addProperty("title", src.getTitle());
            result.addProperty("author", src.getAuthor());
            result.addProperty("itemId", src.getId());
            result.addProperty("enabled", src.isEnabled());
            // Specific to PhysicalItem
            result.addProperty("copiesAvailable", ((PhysicalItem) src).getCopiesAvailable());
            result.addProperty("isLost", ((PhysicalItem) src).isLost());
            result.addProperty("location", ((PhysicalItem) src).getLocation());
        }
        // Handle other Item types similarly
        return result;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Check if the JSON object has the "copiesAvailable" field to determine if it's a PhysicalItem
        if (jsonObject.has("copiesAvailable")) {
            // Deserialize common properties
            String title = jsonObject.get("title").getAsString();
            String author = jsonObject.get("author").getAsString();
            String itemId = jsonObject.get("itemId").getAsString();
            boolean enabled = jsonObject.get("enabled").getAsBoolean();

            // Deserialize PhysicalItem specific properties
            int copiesAvailable = jsonObject.get("copiesAvailable").getAsInt();
            boolean isLost = jsonObject.get("isLost").getAsBoolean();
            String location = jsonObject.has("location") ? jsonObject.get("location").getAsString() : ""; // Optional field

            PhysicalItem physicalItem = new PhysicalItem(title, author);
            physicalItem.setEnabled(enabled);
            physicalItem.setCopiesAvailable(copiesAvailable);
            physicalItem.setLost(isLost);
            physicalItem.setLocation(location);

            return physicalItem;
        }

        // If the JSON does not represent a PhysicalItem, handle other Item types or return null
        // For the sake of this example, let's return null if it's not a PhysicalItem
        return null;
    }
}
