package data.parsers.binary;

import data.binary.datums.ItemDatum;
import data.parsers.IDataParser;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ItemDatumBinaryParser implements IDataParser<byte[], ItemDatum> {
    @Override
    public byte[] getData(ItemDatum obj) {
        ByteBuffer buffer = ByteBuffer.allocate(ItemDatum.ITEM_DATUM_SIZE);

        buffer.putLong(obj.itemId);
        buffer.put(Arrays.copyOf(obj.title.getBytes(StandardCharsets.UTF_8), ItemDatum.TITLE_LENGTH));
        buffer.put(Arrays.copyOf(obj.author.getBytes(StandardCharsets.UTF_8), ItemDatum.AUTHOR_LENGTH));
        buffer.put((byte) (obj.enabled ? 1 : 0));
        buffer.putInt(obj.getCopiesAvailable());
        buffer.put((byte) (obj.isLost() ? 1 : 0));
        buffer.put(Arrays.copyOf(obj.getLocation().getBytes(StandardCharsets.UTF_8), ItemDatum.LOCATION_LENGTH));

        return buffer.array();
    }

    @Override
    public ItemDatum parseData(byte[] bytes) {
        if (bytes.length != ItemDatum.ITEM_DATUM_SIZE) {
            throw new IllegalArgumentException("Invalid data length: " + bytes.length);
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ItemDatum obj = new ItemDatum();

        obj.itemId = buffer.getLong();
        byte[] titleBytes = new byte[ItemDatum.TITLE_LENGTH];
        buffer.get(titleBytes);
        obj.title = new String(titleBytes, StandardCharsets.UTF_8).trim();

        byte[] authorBytes = new byte[ItemDatum.AUTHOR_LENGTH];
        buffer.get(authorBytes);
        obj.author = new String(authorBytes, StandardCharsets.UTF_8).trim();

        obj.enabled = buffer.get() == 1;
        obj.setCopiesAvailable(buffer.getInt());
        obj.setLost(buffer.get() == 1);

        byte[] locationBytes = new byte[ItemDatum.LOCATION_LENGTH];
        buffer.get(locationBytes);
        obj.setLocation(new String(locationBytes, StandardCharsets.UTF_8).trim());

        return obj;
    }
}
