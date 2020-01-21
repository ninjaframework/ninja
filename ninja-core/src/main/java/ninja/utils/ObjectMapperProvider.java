/**
 * Copyright (C) 2012-2020 the original author or authors.
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

package ninja.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.inject.Provider;

/**
 * ObjectMapper is used in several classes. For instance in the
 * BodyParser for Json and the Json rendering engines for both
 * Json and JsonP.
 * 
 * This provider makes it simple to configure the ObjectMapper in one place
 * for all places where it is used.
 */
public class ObjectMapperProvider implements Provider<ObjectMapper>{

    @Override
    public ObjectMapper get() {
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Afterburner optimizes performance of Pojo to Json mapper
        objectMapper.registerModule(new AfterburnerModule());
        
        return objectMapper;
        
    }
    
}
