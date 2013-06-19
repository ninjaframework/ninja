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

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineManagerImpl implements BodyParserEngineManager {
    
    // Keep a reference of providers rather than instances, so body parser engines
    // don't have to be singleton if they don't want
    private final Map<String, Provider<? extends BodyParserEngine>> contentTypeToBodyParserMap;

    @Inject
    public BodyParserEngineManagerImpl(Provider<BodyParserEnginePost> bodyParserEnginePost,
                                       Provider<BodyParserEngineJson> bodyParserEngineJson,
                                       Provider<BodyParserEngineXml> bodyParserEngineXml,
                                       Injector injector) {
        
        
        Map<String, Provider<? extends BodyParserEngine>> map = Maps.newHashMap();

        // First put the built in ones in, this is so they can be overridden by
        // custom bindings
        map.put(bodyParserEnginePost.get().getContentType(),
                bodyParserEnginePost);
        map.put(bodyParserEngineJson.get().getContentType(),
                bodyParserEngineJson);
        map.put(bodyParserEngineXml.get().getContentType(),
                bodyParserEngineXml);
        
        
        // Now lookup all explicit bindings, and find the ones that implement
        // BodyParserEngine
        for (Map.Entry<Key<?>, Binding<?>> binding : injector.getBindings()
                .entrySet()) {
            if (BodyParserEngine.class.isAssignableFrom(binding.getKey()
                    .getTypeLiteral().getRawType())) {
                Provider<? extends BodyParserEngine> provider = (Provider) binding
                        .getValue().getProvider();
                map.put(provider.get().getContentType(), provider);
            }
        }

        contentTypeToBodyParserMap = ImmutableMap.copyOf(map);
        

    }

    @Override
    public BodyParserEngine getBodyParserEngineForContentType(String contentType) {

        Provider<? extends BodyParserEngine> provider = contentTypeToBodyParserMap
                .get(contentType);

        if (provider != null) {
            return provider.get();
        } else {
            return null;
        }

    }
}
