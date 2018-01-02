/**
 * Copyright (C) 2012- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.params;

import ninja.Context;
import ninja.params.ParamParsers.ArrayParamParser;

/**
 * Argument extractor that parses the String[] argument into a X[]
 */
public class ParsingArrayExtractor<X> implements ArgumentExtractor<X> {
    private final ArgumentExtractor<? extends String[]> wrapped;
    private final ArrayParamParser<?> parser;

    public ParsingArrayExtractor(ArgumentExtractor<? extends String[]> wrapped, ArrayParamParser<?> parser) {
        this.wrapped = wrapped;
        this.parser = parser;
    }

    @Override
    public X extract(Context context) {
        return (X) parser.parseParameter(wrapped.getFieldName(), wrapped.extract(context),
                context.getValidation());
    }

    @Override
    public Class<X> getExtractedType() {
        return (Class<X>) parser.getArrayType();
    }

    @Override
    public String getFieldName() {
        return wrapped.getFieldName();
    }
}
