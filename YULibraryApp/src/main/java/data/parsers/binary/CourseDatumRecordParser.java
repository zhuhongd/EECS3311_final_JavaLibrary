package data.parsers.binary;

import data.Record;
import data.binary.datums.CourseDatum;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CourseDatumRecordParser implements BinaryParser<CourseDatum> {
    private static final int RECORD_SIZE = CourseDatum.COURSE_DATUM_SIZE;

    @Override
    public byte[] getData(Record<CourseDatum> obj) {
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);

        CourseDatum courseDatum = obj.getEntry();
        buffer.put(courseDatum.flag);
        buffer.putInt(courseDatum.courseId);
        buffer.putLong(courseDatum.textbookId);

        byte[] titleBytes = courseDatum.title.getBytes(StandardCharsets.UTF_8);
        byte[] paddedTitleBytes = new byte[CourseDatum.TITLE_SIZE];
        System.arraycopy(titleBytes, 0, paddedTitleBytes, 0, Math.min(titleBytes.length, CourseDatum.TITLE_SIZE));
        buffer.put(paddedTitleBytes);

        buffer.putLong(courseDatum.endDate);

        for (int studentId : courseDatum.students) {
            buffer.putInt(studentId);
        }

        return buffer.array();
    }

    @Override
    public Record<CourseDatum> parseData(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        CourseDatum courseDatum = new CourseDatum();
        courseDatum.flag = buffer.get();
        courseDatum.courseId = buffer.getInt();
        courseDatum.textbookId = buffer.getLong();

        byte[] titleBytes = new byte[CourseDatum.TITLE_SIZE];
        buffer.get(titleBytes);
        courseDatum.title = new String(titleBytes, StandardCharsets.UTF_8).trim();

        courseDatum.endDate = buffer.getLong();

        for (int i = 0; i < courseDatum.students.length; i++) {
            courseDatum.students[i] = buffer.getInt();
        }

        return new Record<>(String.valueOf(courseDatum.courseId), courseDatum, null); // Assuming null for timestamp in this context
    }

    @Override
    public int getSize() {
        return RECORD_SIZE;
    }
}
