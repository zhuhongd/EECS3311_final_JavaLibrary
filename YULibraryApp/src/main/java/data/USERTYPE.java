package data;

public enum USERTYPE {
    STUDENT(0),
    FACULTY(1),
    STAFF(2),
    VISITOR(3),
    MANAGEMENT(4);

    private final byte value;

    USERTYPE(int value) {
        this.value = (byte) value;
    }

    public byte toByte() {
        return this.value;
    }

    public static USERTYPE fromByte(byte value) {
        for (USERTYPE userType : USERTYPE.values()) {
            if (userType.value == value) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Unknown byte value for USERTYPE: " + value);
    }
}
