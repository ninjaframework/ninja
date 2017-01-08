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
