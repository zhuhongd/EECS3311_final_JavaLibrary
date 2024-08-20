package data.binary.datums;

import data.USERTYPE;

import java.util.Arrays;
import java.util.Objects;

public class UserDatum implements Datum {
    // Define constants for sizes

    public static final int PRESENT_SIZE = Byte.BYTES; // 1 Byte
    public static final int ENABLED_SIZE = Byte.BYTES; // 1 Byte
    public static final int TYPE_SIZE = Byte.BYTES; // 1 Byte
    public static final int USER_ID_SIZE = Integer.BYTES; // 4 bytes
    public static final int EMAIL_SIZE = 256; // 256 bytes
    public static final int USERNAME_SIZE = 64; // 64 bytes
    public static final int PASSWORD_HASH_SIZE = 64; // 64 bytes
    public static final int POSSESSIONS_SIZE = 10 * Long.BYTES; // 10 * 8 bytes
    public static final int VALIDATED_SIZE = Byte.BYTES; // 1 byte
    public static final int TEXTBOOKS_SIZE = 5 * Long.BYTES; // 5 * 8 bytes
    public static final int TEACHING_SIZE = 5 * Integer.BYTES; // 5 * 4 bytes
    public static final int PREVIOUS_BOOKS_SIZE = 10 * Long.BYTES; // 10 * 8 bytes
    public static final int USER_DATUM_SIZE = PRESENT_SIZE + ENABLED_SIZE + TYPE_SIZE + USER_ID_SIZE + EMAIL_SIZE + USERNAME_SIZE + PASSWORD_HASH_SIZE +
            POSSESSIONS_SIZE + VALIDATED_SIZE + TEXTBOOKS_SIZE + TEACHING_SIZE +
            PREVIOUS_BOOKS_SIZE; // Total size

    public byte present;
    public byte enabled;


    public byte type;

    public int userId;

    public String email;
    public String username;
    public String passwordHash;
    public long[] possessions;
    public boolean validated;
    public long[] textbooks;
    public int[] teaching;
    public long[] previousBooks;

    public UserDatum() {
        possessions = new long[10];
        textbooks = new long[5];
        teaching = new int[5];
        previousBooks = new long[10];
    }

    @Override
    public String toString() {
        return "UserDatum{" +
                "present = " + present + '\'' +
                "enabled = " + enabled + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", possessions=" + Arrays.toString(possessions) +
                ", validated=" + validated +
                ", textbooks=" + Arrays.toString(textbooks) +
                ", teaching=" + Arrays.toString(teaching) +
                ", previousBooks=" + Arrays.toString(previousBooks) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDatum userDatum = (UserDatum) o;
        return userId == userDatum.userId &&
                validated == userDatum.validated &&
                Objects.equals(email, userDatum.email) &&
                Objects.equals(username, userDatum.username) &&
                Objects.equals(passwordHash, userDatum.passwordHash) &&
                Arrays.equals(possessions, userDatum.possessions) &&
                Arrays.equals(textbooks, userDatum.textbooks) &&
                Arrays.equals(teaching, userDatum.teaching) &&
                Arrays.equals(previousBooks, userDatum.previousBooks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userId, email, username, passwordHash, validated);
        result = 31 * result + Arrays.hashCode(possessions);
        result = 31 * result + Arrays.hashCode(textbooks);
        result = 31 * result + Arrays.hashCode(teaching);
        result = 31 * result + Arrays.hashCode(previousBooks);
        return result;
    }

    @Override
    public boolean getFlag() {
        return enabled == 1;
    }

    public void setFlag(boolean b) {
        this.enabled = b ? (byte) 1 : (byte) 0;
    }

    @Override
    public String getId() {
        return String.valueOf(userId);
    }
    public void setUserType(USERTYPE userType) {
        this.type = userType.toByte();
    }
    public USERTYPE getUserType() {
        return USERTYPE.fromByte(this.type);
    }


}