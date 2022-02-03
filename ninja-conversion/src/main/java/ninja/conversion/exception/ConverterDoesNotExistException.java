package ninja.conversion.exception;

/**
 * Needed converter does not exist.
 */
public class ConverterDoesNotExistException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     */
    public ConverterDoesNotExistException(final Class<?> sourceTypeClass,
                                          final Class<?> targetTypeClass) {
        super("Converter<source=" + sourceTypeClass + ", target=" + targetTypeClass + "> does not exist");
    }
}
