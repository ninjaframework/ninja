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

package ninja.template;

import ninja.Context;
import ninja.Result;

public interface TemplateEngine {

    /**
     * Render the given object to the given context
     * 
     * @param context
     *            The context to render to
     * @param result
     *            The result to render
     */
    public void invoke(Context context, Result result);

    /**
     * For instance returns ".ftl.html" Or .ftl.json.
     * <p>
     * Or anything else. To display error messages in a nice way...
     * <p>
     * But Gson for instance does not use a template to render stuff. Therefore
     * it will return null
     * 
     * @return name of suffix or null if engine is not using a template on disk.
     */
    public String getSuffixOfTemplatingEngine();

    /**
     * Get the content type this template engine renders
     * 
     * @return The content type this template engine renders
     */
    public String getContentType();

}
