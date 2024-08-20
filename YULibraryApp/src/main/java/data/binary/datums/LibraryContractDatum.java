package data.binary.datums;

import java.util.Objects;

public class LibraryContractDatum implements Datum {
    // Define constants for sizes
    public static final int ID_SIZE = Integer.BYTES;
    public static final int USER_ID_SIZE = Integer.BYTES;
    public static final int ITEM_ID_SIZE = Long.BYTES;

    private static final int ENABLED_FLAG_SIZE = Byte.BYTES;
    public static final int LIBRARY_CONTRACT_DATUM_SIZE = ENABLED_FLAG_SIZE + ID_SIZE + USER_ID_SIZE + ITEM_ID_SIZE; // Total size

    public String id;
    public String userId;
    public byte enabled;
    public String itemId;

    public LibraryContractDatum() {

    }

    public LibraryContractDatum(String id, String userId, String itemId, boolean enabled) {
        this.id = id; // Fix was applied here
        this.userId = userId;
        this.itemId = itemId;
        this.enabled = enabled ? (byte) 1 : 0;
    }


    @Override
    public String toString() {
        return "LibraryContractDatum{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", itemId='" + itemId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryContractDatum that = (LibraryContractDatum) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, itemId);
    }

    @Override
    public boolean getFlag() {
        return enabled == 1;
    }

    @Override
    public void setFlag(boolean b) {
        enabled = b ? (byte) 1 : 0;
    }

    @Override
    public String getId() {
        return id;
    }
}
