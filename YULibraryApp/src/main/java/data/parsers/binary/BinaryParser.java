package data.parsers.binary;

import data.Record;
import data.binary.datums.Datum;
import data.parsers.IDataParser;

/**
 * Extends the IDataParser interface for binary data, specialized for converting between byte arrays and records of a specific Datum type.
 *
 * @param <T> The Datum type which extends the base Datum class, indicating the specific data type this parser handles.
 */
public interface BinaryParser<T extends Datum> extends IDataParser<byte[], Record<T>> {

    /**
     * Gets the size of the data type D.
     *
     * @return The size of the data.
     */
    int getSize();
}