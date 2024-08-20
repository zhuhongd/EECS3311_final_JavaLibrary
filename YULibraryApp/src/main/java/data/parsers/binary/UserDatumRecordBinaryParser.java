package data.parsers.binary;

import data.Record;
import data.binary.datums.UserDatum;

import java.nio.ByteBuffer;
import java.time.Instant;

// | Key (4 bytes) | Epoch Seconds (8 bytes) | Nanoseconds (8 bytes) | User Data (`UserDatum.USER_DATUM_SIZE` bytes) |
public class UserDatumRecordBinaryParser implements BinaryParser<UserDatum> {
    public static final int TIMESTAMP_SIZE = 2 * Long.BYTES; // 16 bytes
    public static final int KEY_SIZE = 4; // a unique key represented by an int , 4 bytes

    public static final int RECORD_SIZE = KEY_SIZE + TIMESTAMP_SIZE + UserDatum.USER_DATUM_SIZE;

    private final UserDatumBinaryParser userDatumBinaryParser = new UserDatumBinaryParser();

    @Override
    public byte[] getData(Record<UserDatum> obj) {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);

        byte[] userBytes = userDatumBinaryParser.getData(obj.getEntry());
        buffer.putInt(Integer.parseInt(obj.getKey()));
        buffer.putLong(obj.getTimestamp().getEpochSecond());
        buffer.putLong(obj.getTimestamp().getNano());
        buffer.put(userBytes);
        return buffer.array();
    }


    @Override
    public Record<UserDatum> parseData(byte[] data) {
        // Ensure that the data array has the expected length
        if (data.length != RECORD_SIZE) {
            throw new IllegalArgumentException("Invalid data length: " + data.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);


        // Directly parse the key, seconds, and nano from the buffer
        int key = buffer.getInt();
        long seconds = buffer.getLong();
        long nano = buffer.getLong();

        // Instead of manually allocating a byte array for userBytes, calculate the remaining bytes for the user data
        byte[] userBytes = new byte[buffer.remaining()];
        buffer.get(userBytes); // Reads the rest of the buffer into userBytes

        // Parse the user data segment using the userDatumParser
        UserDatum userDatum = userDatumBinaryParser.parseData(userBytes);

        return new Record<>(String.valueOf(key), userDatum, Instant.ofEpochSecond(seconds, nano));
    }

    @Override
    public int getSize() {
        return RECORD_SIZE;
    }
}
