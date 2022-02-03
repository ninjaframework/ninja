package ninja.conversion.converter;

import ninja.conversion.TypeConverter;

/**
 * Conversion from {@code String} to {@code Integer}.
 */
public class StringToIntegerConverter implements TypeConverter<String, Integer> {

    @Override
    public Integer convert(final String source) {
        return Integer.valueOf(source);
    }
}
