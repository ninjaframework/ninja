package ninja.params;

import ninja.Context;

/**
 * Extracts a controller argument from the context
 *
 * @author James Roper
 */
public interface ArgumentExtractor<T> {
    /**
     * Extract the argument from the context
     *
     * @param context The argument to extract
     * @return The extracted argument
     */
    T extract(Context context);

    /**
     * Get the type of the argument that is extracted
     *
     * @return The type of the argument that is being extracted
     */
    Class<T> getExtractedType();

    /**
     * Get the field name that is being extracted, if this value is
     * extracted from a field
     *
     * @return The field name, or null if the argument isn't extracted
     *         from a named field
     */
    String getFieldName();
}
