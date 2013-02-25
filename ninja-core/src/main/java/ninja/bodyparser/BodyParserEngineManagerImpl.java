/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja.bodyparser;

import ninja.ContentTypes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineManagerImpl implements BodyParserEngineManager {
    private final BodyParserEnginePost bodyParserEnginePost;
    private final BodyParserEngineJson bodyParserEngineJson;
    private final BodyParserEngineXml bodyParserEngineXml;

    @Inject
    public BodyParserEngineManagerImpl(BodyParserEnginePost bodyParserEnginePost,
                                       BodyParserEngineJson bodyParserEngineJson,
                                       BodyParserEngineXml bodyParserEngineXml) {
        this.bodyParserEngineJson = bodyParserEngineJson;
        this.bodyParserEngineXml = bodyParserEngineXml;
        this.bodyParserEnginePost = bodyParserEnginePost;

    }

    @Override
    public BodyParserEngine getBodyParserEngineForContentType(String contentType) {

        if (contentType.equals(ContentTypes.APPLICATION_JSON)) {
            return bodyParserEngineJson;
        } else if (contentType.equals(ContentTypes.APPLICATION_XML)) {
            return bodyParserEngineXml;
        } else if (contentType.equals(ContentTypes.APPLICATION_POST_FORM)) {
            return bodyParserEnginePost;
        } else {
            return null;
        }

    }
}
