package ninja.params;

import ninja.Context;

/**
 * Argument extractor that parses the String argument into another type
 */
public class ParsingArgumentExtractor<T> implements ArgumentExtractor<T> {
    private final ArgumentExtractor<? extends String> wrapped;
    private final ParamParser<T> parser;

    public ParsingArgumentExtractor(ArgumentExtractor<? extends String> wrapped, ParamParser<T> parser) {
        this.wrapped = wrapped;
        this.parser = parser;
    }

    @Override
    public T extract(Context context) {
        return parser.parseParameter(wrapped.getFieldName(), wrapped.extract(context),
                context.getValidation());
    }

    @Override
    public Class<T> getExtractedType() {
        return parser.getParsedType();
    }

    @Override
    public String getFieldName() {
        return wrapped.getFieldName();
    }
}
