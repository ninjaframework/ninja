/**
 * Copyright (C) 2013 the original author or authors.
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

import java.io.IOException;

import ninja.ContentTypes;
import ninja.Context;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineJson implements BodyParserEngine {
    
    private final ObjectMapper objectMapper;
    
    private final Logger logger;
    

    @Inject
    public BodyParserEngineJson(ObjectMapper objectMapper, Logger logger) {
        this.objectMapper = objectMapper;
        this.logger = logger;

    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        T t = null;

        try {

            t = objectMapper.readValue(context.getInputStream(), classOfT);

        } catch (IOException e) {
            logger.error("Error parsing incoming Json", e);
        }

        return t;
    }
    
    public String getContentType() {
        return ContentTypes.APPLICATION_JSON; 
    }

}
