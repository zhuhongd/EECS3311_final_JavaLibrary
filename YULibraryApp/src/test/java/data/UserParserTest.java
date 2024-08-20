package data;

import data.binary.datums.UserDatum;
import data.parsers.binary.UserDatumBinaryParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class UserParserTest {
    @Test
    public void testParseData() {
        // Create a UserDatum object with test data
        UserDatum originalUserDatum = new UserDatum();
        originalUserDatum.enabled = 1;
        originalUserDatum.userId = 12345;
        originalUserDatum.email = "test@example.com";
        originalUserDatum.username = "testUser";
        originalUserDatum.passwordHash = "hashValue";
        Arrays.fill(originalUserDatum.possessions, 123L);
        originalUserDatum.validated = true;
        Arrays.fill(originalUserDatum.textbooks, 456L);
        Arrays.fill(originalUserDatum.teaching, 789);
        Arrays.fill(originalUserDatum.previousBooks, 321L);

        // Use UserDatumParser to convert to byte array and back to object
        UserDatumBinaryParser parser = new UserDatumBinaryParser();
        byte[] userDataBytes = parser.getData(originalUserDatum);
        UserDatum parsedUserDatum = parser.parseData(userDataBytes);

        // Verify that the original and parsed UserDatum objects are equal
        Assertions.assertEquals(originalUserDatum, parsedUserDatum);
    }
}
