package ninja.conversion.exception;

/**
 * Same converter detected twice.
 */
public class DuplicateConverterException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param converterName   Name of the converter
     * @param sourceClassName The source object class name
     * @param targetClassName The target object class name
     */
    public DuplicateConverterException(final String converterName,
                                       final String sourceClassName,
                                       final String targetClassName) {
        super("Duplicate Converter<source="
                + sourceClassName
                + ", target="
                + targetClassName
                + "> definition detected in "
                + converterName);
    }
}
