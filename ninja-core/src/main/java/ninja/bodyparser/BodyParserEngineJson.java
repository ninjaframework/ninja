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

import java.io.IOException;

import ninja.Context;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineJson implements BodyParserEngine {
    
    private Gson gson;
    
    private Logger logger;
    

    @Inject
    public BodyParserEngineJson(Gson gson, Logger logger) {
        this.gson = gson;
        this.logger = logger;

    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        T t = null;

        try {

            t = gson.fromJson(context.getReader(), classOfT);

        } catch (JsonSyntaxException e) {
            logger.error("Error parsing incoming Json", e);
            
        } catch (JsonIOException e) {
            logger.error("Error parsing incoming Json", e);
        } catch (IOException e) {
            logger.error("Error parsing incoming Json", e);
        }

        return t;
    }

}
