package data.binary.datums;

import java.util.Objects;

/**
 * Represents an item's data within a system, encapsulating details about
 * the item such as its unique identifier, title, author, copies available,
 * lost status, location, and whether it is enabled.
 * This class is designed to efficiently store and manage data related to items,
 * which can include books, resources, or any other categorized entity.
 */
public class ItemDatum implements Datum {
    public static final int TITLE_LENGTH = 50;
    public static final int AUTHOR_LENGTH = 30;
    public static final int LOCATION_LENGTH = 256;
    public static final int ITEM_ID_SIZE = 8; // Long.SIZE / Byte.SIZE
    public static final int ENABLED_SIZE = 1;
    public static final int ITEM_DATUM_SIZE = 345; // Updated size including new fields
    public long itemId; // Unique identifier for the item : 8 Bytes
    public String title; // Title of the item : 50 characters, 50 bytes
    public String author; // Author of the item : 30 characters, 30 bytes
    public boolean enabled; // Flag indicating if the item is enabled or not 1 byte
    private int copiesAvailable; // Number of copies available
    private boolean isLost; // Flag indicating if the item is lost
    private String location; // Location of the item : 256 characters, 256 bytes

    /**
     * Constructs an instance of ItemDatum with default values.
     * This default constructor initializes the object without setting
     * the attributes, leaving them to be populated separately.
     */
    public ItemDatum() {
    }

    public ItemDatum(String title, String author, long itemId, boolean enabled, int copiesAvailable, boolean isLost, String location) {
        this.title = title;
        this.author = author;
        this.itemId = itemId;
        this.enabled = enabled;
        this.copiesAvailable = copiesAvailable;
        this.isLost = isLost;
        this.location = location;
    }

    public ItemDatum(String title, String author, int i, boolean b) {
        this.title = title;
        this.author = author;
        this.copiesAvailable = i;
        this.enabled = b;

    }

    // Getters and setters for the new fields
    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ItemDatum{" +
                "itemId=" + itemId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", enabled=" + enabled +
                ", copiesAvailable=" + copiesAvailable +
                ", isLost=" + isLost +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDatum itemDatum = (ItemDatum) o;
        return itemId == itemDatum.itemId &&
                enabled == itemDatum.enabled &&
                copiesAvailable == itemDatum.copiesAvailable &&
                isLost == itemDatum.isLost &&
                Objects.equals(title, itemDatum.title) &&
                Objects.equals(author, itemDatum.author) &&
                Objects.equals(location, itemDatum.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, title, author, enabled, copiesAvailable, isLost, location);
    }

    @Override
    public boolean getFlag() {
        return false;
    }

    @Override
    public void setFlag(boolean b) {
    }

    @Override
    public String getId() {
        return this.title;
    }
}
