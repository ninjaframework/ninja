package ninja.conversion.exception;

/**
 * Something goes wrong during the converter initialization.
 */
public class InvalidConverterException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param converterName Name of the converter
     * @param errorMessage  The error message
     */
    public InvalidConverterException(final String converterName, final String errorMessage) {
        super("Converter '" + converterName + "' is invalid: " + errorMessage);
    }
}
