/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.util.Optional;
import ninja.Context;

/**
 * Argument extractor that wraps an extractor so that it can handle Optional<...>
 * in controller methods.
 * 
 * For example:
 * 
 *     myControllerMethod(@Param("param1") Optional<String> myValue)
 */
class OptionalArgumentExtractor<T> implements ArgumentExtractor<Optional<T>> {
    private final ArgumentExtractor<T> wrapped;

    public OptionalArgumentExtractor(ArgumentExtractor<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Optional<T> extract(Context context) {
        return Optional.ofNullable(wrapped.extract(context));
    }

    @Override
    public Class<Optional<T>> getExtractedType() {
        throw new RuntimeException("This is a framework-internal ArgumentExtractor. This method should not be used by anyone.");
    }

    @Override
    public String getFieldName() {
        return wrapped.getFieldName();
    }
}
