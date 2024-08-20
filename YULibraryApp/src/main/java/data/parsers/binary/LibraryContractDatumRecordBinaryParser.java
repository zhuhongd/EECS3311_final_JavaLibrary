package data.parsers.binary;

import data.Record;
import data.binary.datums.LibraryContractDatum;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

public class LibraryContractDatumRecordBinaryParser implements BinaryParser<LibraryContractDatum> {

    public static final int TIMESTAMP_SIZE = 2 * Long.BYTES; // 16 bytes
    public static final int KEY_SIZE = 4; // a unique key represented by an int , 4 bytes

    public static final int RECORD_SIZE = KEY_SIZE + TIMESTAMP_SIZE + LibraryContractDatum.LIBRARY_CONTRACT_DATUM_SIZE;

    @Override
    public byte[] getData(Record<LibraryContractDatum> obj) {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);

        buffer.putInt(Integer.parseInt(obj.getKey()));
        buffer.putLong(obj.getTimestamp().getEpochSecond());
        buffer.putLong(obj.getTimestamp().getNano());


        LibraryContractDatum contractDatum = obj.getEntry();
        buffer.putInt(Integer.parseInt(contractDatum.id));
        buffer.putInt(Integer.parseInt(contractDatum.userId));
        buffer.putLong(Long.parseLong(contractDatum.itemId));
        buffer.put(contractDatum.enabled);

        return buffer.array();
    }

    @Override
    public Record<LibraryContractDatum> parseData(byte[] data) {

        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Directly parse the key, seconds, and nano from the buffer
        int key = buffer.getInt();
        long seconds = buffer.getLong();
        long nano = buffer.getLong();


        String id = String.valueOf(buffer.getInt());

        String userId = String.valueOf(buffer.getInt());
        String itemId = String.valueOf(buffer.getLong());
        boolean enabled = buffer.get() == 1;

        LibraryContractDatum contractDatum = new LibraryContractDatum(id, userId, itemId, enabled);

        return new Record<>(String.valueOf(key), contractDatum, Instant.ofEpochSecond(seconds, nano));
    }

    @Override
    public int getSize() {
        return RECORD_SIZE;
    }
}
