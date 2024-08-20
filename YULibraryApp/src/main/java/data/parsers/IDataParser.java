package data.parsers;

/**
 * Defines a generic data parser that converts between a specific data representation and its object form.
 *
 * @param <D> The data type (e.g., byte array, string) to be parsed or generated.
 * @param <T> The target object type that the data represents.
 */
public interface IDataParser<D, T> {
    /**
     * Converts an object of type T into its data representation D.
     *
     * @param obj The object to convert.
     * @return The data representation of the object.
     */
    D getData(T obj);

    /**
     * Parses the given data of type D into an object of type T.
     *
     * @param data The data to parse.
     * @return The parsed object.
     */
    T parseData(D data);

}
