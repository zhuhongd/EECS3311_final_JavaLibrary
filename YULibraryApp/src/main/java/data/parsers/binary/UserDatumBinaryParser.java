package data.parsers.binary;

import data.binary.datums.UserDatum;
import data.parsers.IDataParser;
import data.USERTYPE; // Make sure to import the USERTYPE enum

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UserDatumBinaryParser implements IDataParser<byte[], UserDatum> {
    @Override
    public byte[] getData(UserDatum obj) {
        ByteBuffer buffer = ByteBuffer.allocate(UserDatum.USER_DATUM_SIZE);
        buffer.put(obj.present);
        buffer.put(obj.enabled);
        // Use the USERTYPE enum to get the byte value
        buffer.put(obj.getUserType().toByte());
        buffer.putInt(obj.userId);
        buffer.put(Arrays.copyOf(obj.email.getBytes(), UserDatum.EMAIL_SIZE));
        buffer.put(Arrays.copyOf(obj.username.getBytes(), UserDatum.USERNAME_SIZE));
        buffer.put(Arrays.copyOf(obj.passwordHash.getBytes(), UserDatum.PASSWORD_HASH_SIZE));
        for (long possession : obj.possessions) buffer.putLong(possession);
        buffer.put((byte) (obj.validated ? 1 : 0));
        for (long textbook : obj.textbooks) buffer.putLong(textbook);
        for (int teaching : obj.teaching) buffer.putInt(teaching);
        for (long previousBook : obj.previousBooks) buffer.putLong(previousBook);
        return buffer.array();
    }

    @Override
    public UserDatum parseData(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        UserDatum obj = new UserDatum();

        obj.present = buffer.get();
        obj.enabled = buffer.get();
        // Use the byte value to find the corresponding USERTYPE enum
        byte typeByte = buffer.get();
        obj.setUserType(USERTYPE.fromByte(typeByte)); // Assuming there's a setUserType method in UserDatum
        obj.userId = buffer.getInt();
        byte[] emailBytes = new byte[UserDatum.EMAIL_SIZE];
        buffer.get(emailBytes);
        obj.email = new String(emailBytes).trim();

        byte[] usernameBytes = new byte[UserDatum.USERNAME_SIZE];
        buffer.get(usernameBytes);
        obj.username = new String(usernameBytes).trim();

        byte[] passwordHashBytes = new byte[UserDatum.PASSWORD_HASH_SIZE];
        buffer.get(passwordHashBytes);
        obj.passwordHash = new String(passwordHashBytes).trim();

        for (int i = 0; i < obj.possessions.length; i++) obj.possessions[i] = buffer.getLong();
        obj.validated = buffer.get() == 1;

        for (int i = 0; i < obj.textbooks.length; i++) obj.textbooks[i] = buffer.getLong();
        for (int i = 0; i < obj.teaching.length; i++) obj.teaching[i] = buffer.getInt();
        for (int i = 0; i < obj.previousBooks.length; i++) obj.previousBooks[i] = buffer.getLong();

        return obj;
    }
}
