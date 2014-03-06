/**
 * Copyright (C) 2012-2014 the original author or authors.
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

package ninja.template;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

/**
 * Template engine manager. Has a number of built in template engines, and
 * allows registering custom template engines by registering explicit bindings
 * of things that implement TemplateEngine.
 */
@ImplementedBy(TemplateEngineManagerImpl.class)
public interface TemplateEngineManager {

    /**
     * Find the template engine for the given content type
     * 
     * @param contentType
     *            The content type
     * @return The template engine, if found
     */
    TemplateEngine getTemplateEngineForContentType(String contentType);

}
