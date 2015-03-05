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

package ninja.bodyparser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import ninja.ContentTypes;
import ninja.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineJson implements BodyParserEngine {
    
    private final Logger logger = LoggerFactory.getLogger(BodyParserEngineJson.class);
    
    private final ObjectMapper objectMapper;

    @Inject
    public BodyParserEngineJson(ObjectMapper objectMapper) {
        
        this.objectMapper = objectMapper;

    }

    public <T> T invoke(Context context, TypeReference<T> typeOfT) {
        T t = null;

        try (InputStream inputStream = context.getInputStream()) {

            t = objectMapper.readValue(inputStream, typeOfT);

        } catch (IOException e) {
            logger.error("Error parsing incoming Json", e);
        }

        return t;
    }

	@Override
	public <T> T invoke(final Context context, final Class<T> classOfT) {
		return invoke(context, new TypeReference<T>() {
			@Override
			public Type getType() {
				return classOfT;
			}
		});
	}

	public String getContentType() {
        return ContentTypes.APPLICATION_JSON; 
    }

}
