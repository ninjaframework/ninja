package ninja.conversion;

/**
 * All type converters must implement this interface.
 */
public interface TypeConverter<SOURCE_TYPE, TARGET_TYPE> {

    /**
     * Converts the given source object to the target type.
     *
     * @param source The source object to convert
     * @return The converted object
     */
    TARGET_TYPE convert(final SOURCE_TYPE source);
}
