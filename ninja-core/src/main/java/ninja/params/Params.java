/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects a multi-valued parameter right into the methods...
 *
 * This equals context.getParameterValues(...)
 *
 * @author James Moger
 *
 */
@WithArgumentExtractor(ArgumentExtractors.ParamsExtractor.class)
@WithArgumentExtractors({
    ArgumentExtractors.FileItemParamsExtractor.class,
    ArgumentExtractors.FileParamsExtractor.class,
    ArgumentExtractors.InputStreamParamsExtractor.class
})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Params {
    String value();
}