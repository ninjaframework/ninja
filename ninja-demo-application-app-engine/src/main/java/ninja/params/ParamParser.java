package ninja.params;

import ninja.validation.Validation;

/**
 * Parses a String parameter
 */
public interface ParamParser<T> {
    /**
     * Parse the given parameter value
     *
     * @param field The field that is being parsed
     * @param parameterValue The value to parse.  May be null.
     * @param validation The validation context.
     * @return The parsed parameter value.  May be null.
     */
    T parseParameter(String field, String parameterValue, Validation validation);

    /**
     * Get the type that this parser parses to
     *
     * @return The type
     */
    Class<T> getParsedType();
}
