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

package ninja.jackson;

import java.io.IOException;

import ninja.ContentTypes;
import ninja.Context;
import ninja.bodyparser.BodyParserEngine;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.InputStream;

import org.slf4j.LoggerFactory;

@Singleton
public class BodyParserEngineJson implements BodyParserEngine {
    
    private final Logger logger = LoggerFactory.getLogger(BodyParserEngineJson.class);
    
    private final ObjectMapper objectMapper;

    @Inject
    public BodyParserEngineJson(ObjectMapper objectMapper) {
        
        this.objectMapper = objectMapper;

    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        T t = null;

        try (InputStream inputStream = context.getInputStream()) {

            t = objectMapper.readValue(inputStream, classOfT);

        } catch (IOException e) {
            logger.error("Error parsing incoming Json", e);
        }

        return t;
    }
    
    public String getContentType() {
        return ContentTypes.APPLICATION_JSON; 
    }

}
