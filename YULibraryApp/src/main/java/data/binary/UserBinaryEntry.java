package data.binary;

import data.Record;
import data.binary.datums.UserDatum;
import data.parsers.binary.UserDatumRecordBinaryParser;

import java.nio.ByteBuffer;
import java.time.Instant;

public class UserBinaryEntry {
    private static final int FLAG_SIZE = Byte.BYTES; // 1 byte for the flag
    private static final int TOTAL_SIZE = FLAG_SIZE + UserDatumRecordBinaryParser.RECORD_SIZE; // Include flag size in total
    public static final ByteBuffer EMPTY_ENTRY = ByteBuffer.allocate(TOTAL_SIZE);
    // The parser is used as a tool for conversion and can remain static
    private static final UserDatumRecordBinaryParser parser = new UserDatumRecordBinaryParser();
    private final int key;
    private final Instant timestamp;
    private final UserDatum userData;
    private byte flag; // Flag indicating the status of the record

    public UserBinaryEntry(byte flag, int key, Instant timestamp, UserDatum userData) {


        this.flag = flag;
        this.key = key;
        this.timestamp = timestamp;
        this.userData = userData;
    }

    // Factory method for creating an instance from a ByteBuffer
    public static UserBinaryEntry fromByteBuffer(ByteBuffer buffer) {

        byte flag = buffer.get();
        byte[] data = new byte[UserDatumRecordBinaryParser.RECORD_SIZE];
        buffer.get(data);
        Record<UserDatum> record = parser.parseData(data);

        return new UserBinaryEntry(flag, Integer.parseInt(record.getKey()), record.getTimestamp(), record.getEntry());
    }

    // Static utility method for providing total size, suitable for buffer allocation
    public static int getTotalSize() {
        return TOTAL_SIZE;
    }

    // Instance method for converting this object's state to ByteBuffer
    public void toByteBuffer(ByteBuffer buffer) {
        buffer.put(flag);
        buffer.put(parser.getData(new Record<>(String.valueOf(key), userData, timestamp)));
    }

    // Getters and setter for manipulating instance state
    public byte getFlag() {
        return this.flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getKey() {
        return key;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UserDatum getUserData() {
        return userData;
    }
}
