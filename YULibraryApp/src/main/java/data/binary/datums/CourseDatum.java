package data.binary.datums;

public class CourseDatum implements Datum {
    // Constants for sizes
    public static final int FLAG_SIZE = Byte.BYTES; // 1 byte
    public static final int COURSE_ID_SIZE = Integer.BYTES; // Assuming courseId is converted to an int for simplicity
    public static final int TEXTBOOK_ID_SIZE = Long.BYTES; // 8 bytes, assuming textbook is identified by a long ID
    public static final int TITLE_SIZE = 256; // 256 bytes, assuming fixed size for title
    public static final int END_DATE_SIZE = Long.BYTES; // 8 bytes, representing endDate as a timestamp (long)
    public static final int STUDENTS_SIZE = 20 * Integer.BYTES; // Example: 20 * 4 bytes, assuming up to 20 student IDs stored as integers

    // Total size of the datum
    public static final int COURSE_DATUM_SIZE = FLAG_SIZE + COURSE_ID_SIZE + TEXTBOOK_ID_SIZE + TITLE_SIZE +
            END_DATE_SIZE + STUDENTS_SIZE;

    public byte flag;
    public int courseId;
    public long textbookId;
    public String title;
    public long endDate; // Representing as timestamp
    public int[] students; // Assuming student IDs are integers for simplicity

    public CourseDatum() {
        students = new int[20]; // Allocate space for up to 20 students
    }

    @Override
    public boolean getFlag() {
        return flag == 1;
    }

    @Override
    public void setFlag(boolean b) {
        flag = b ? (byte) 1 : (byte) 0;
    }

    @Override
    public String getId() {
        return String.valueOf(courseId);
    }

    // toString, equals, hashCode, getFlag, setFlag, getId methods need to be implemented similar to UserDatum
}
