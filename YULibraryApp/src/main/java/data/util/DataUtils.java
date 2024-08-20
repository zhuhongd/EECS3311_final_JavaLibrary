package data.util;

import data.binary.datums.ItemDatum;
import data.binary.datums.UserDatum;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public enum DataUtils {
    ;
    private static final Random random = new Random();

    /**
     * Generates a random string of a given length.
     */

    private static String generateRandomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // Use ThreadLocalRandom to generate a random integer between leftLimit and rightLimit (inclusive)
            int randomLimitedInt = ThreadLocalRandom.current().nextInt(leftLimit, rightLimit + 1);
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }


    /**
     * Generates a random instance of the UserDatum class.
     */
    public static UserDatum generateRandomUserDatum() {
        UserDatum userDatum = new UserDatum();

        // Replace all calls to the shared Random instance with ThreadLocalRandom for thread safety
        userDatum.userId = ThreadLocalRandom.current().nextInt();
        userDatum.email = generateRandomString(10) + "@example.com";
        userDatum.username = generateRandomString(8);
        userDatum.passwordHash = generateRandomString(64); // Assuming a hash length of 64 characters

        for (int i = 0; i < userDatum.possessions.length; i++) {
            userDatum.possessions[i] = ThreadLocalRandom.current().nextLong();
        }

        userDatum.validated = ThreadLocalRandom.current().nextBoolean();

        for (int i = 0; i < userDatum.textbooks.length; i++) {
            userDatum.textbooks[i] = ThreadLocalRandom.current().nextLong();
        }

        for (int i = 0; i < userDatum.teaching.length; i++) {
            userDatum.teaching[i] = ThreadLocalRandom.current().nextInt();
        }

        for (int i = 0; i < userDatum.previousBooks.length; i++) {
            userDatum.previousBooks[i] = ThreadLocalRandom.current().nextLong();
        }

        return userDatum;
    }

    public static ItemDatum generateRandomItemDatum() {
        ItemDatum itemDatum = new ItemDatum();

        // Use ThreadLocalRandom for thread-safe random number generation
        itemDatum.itemId = ThreadLocalRandom.current().nextLong(); // Assuming positive IDs only, adjust if needed
        itemDatum.title = generateRandomString(ItemDatum.TITLE_LENGTH);
        itemDatum.author = generateRandomString(ItemDatum.AUTHOR_LENGTH);
        itemDatum.enabled = ThreadLocalRandom.current().nextBoolean();
        itemDatum.setCopiesAvailable(ThreadLocalRandom.current().nextInt(100)); // Assuming up to 100 copies available
        itemDatum.setLost(ThreadLocalRandom.current().nextBoolean());
        itemDatum.setLocation(generateRandomString(ItemDatum.LOCATION_LENGTH));

        return itemDatum;
    }


}