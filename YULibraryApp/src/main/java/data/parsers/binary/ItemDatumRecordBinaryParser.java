package data.parsers.binary;

import data.Record;
import data.binary.datums.ItemDatum;

import java.nio.ByteBuffer;
import java.time.Instant;

public class ItemDatumRecordBinaryParser implements BinaryParser<ItemDatum> {
    // Constants for record structure
    public static final int TIMESTAMP_SIZE = 2 * Long.BYTES; // 16 bytes for timestamp (epoch seconds + nanoseconds)
    public static final int KEY_SIZE = Integer.BYTES; // 4 bytes for the record key

    // Assuming ItemDatum.ITEM_DATUM_SIZE is correctly defined in your ItemDatum class
    public static final int RECORD_SIZE = KEY_SIZE + TIMESTAMP_SIZE + ItemDatum.ITEM_DATUM_SIZE;

    private final ItemDatumBinaryParser itemDatumBinaryParser = new ItemDatumBinaryParser();

    @Override
    public byte[] getData(Record<ItemDatum> obj) {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);

        byte[] itemBytes = itemDatumBinaryParser.getData(obj.getEntry());
        buffer.putInt(Integer.parseInt(obj.getKey()));
        buffer.putLong(obj.getTimestamp().getEpochSecond());
        buffer.putLong(obj.getTimestamp().getNano());
        buffer.put(itemBytes);

        return buffer.array();
    }

    @Override
    public Record<ItemDatum> parseData(byte[] data) {
        if (data.length != RECORD_SIZE) {
            throw new IllegalArgumentException("Invalid data length: " + data.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Parse key, timestamp seconds, and nanoseconds from the buffer
        int key = buffer.getInt();
        long seconds = buffer.getLong();
        long nano = buffer.getLong();

        // Allocate the remaining bytes for item data
        byte[] itemBytes = new byte[buffer.remaining()];
        buffer.get(itemBytes); // Reads the rest of the buffer into itemBytes

        // Parse the item data
        ItemDatum itemDatum = itemDatumBinaryParser.parseData(itemBytes);

        // Construct and return the Record object
        return new Record<>(String.valueOf(key), itemDatum, Instant.ofEpochSecond(seconds, nano));
    }

    @Override
    public int getSize() {
        return RECORD_SIZE;
    }
}
