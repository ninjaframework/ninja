/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.ContentTypes;
import ninja.Context;
import ninja.exceptions.BadRequestException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Built in Json body parser.
 *
 * @author Raphael Bauer
 * @author Thibault Meyer
 * @see ninja.bodyparser.BodyParserEngine
 */
@Singleton
public class BodyParserEngineJson implements BodyParserEngine {

    private final ObjectMapper objectMapper;

    @Inject
    public BodyParserEngineJson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        try (InputStream inputStream = context.getInputStream()) {
            return objectMapper.readValue(inputStream, classOfT);
        } catch (JsonParseException | JsonMappingException ex) {
            throw new BadRequestException("Error parsing incoming Json", ex);
        } catch (IOException e) {
            throw new BadRequestException("Invalid Json document", e);
        }
    }

    public String getContentType() {
        return ContentTypes.APPLICATION_JSON;
    }

}
