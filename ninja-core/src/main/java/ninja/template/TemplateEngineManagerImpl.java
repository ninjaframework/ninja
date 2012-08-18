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

package ninja.template;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class TemplateEngineManagerImpl implements TemplateEngineManager {

    // Keep a reference of providers rather than instances, so template engines
    // don't have
    // to be singleton if they don't want
    private final Map<String, Provider<? extends TemplateEngine>> contentTypeToTemplateEngineMap;

    @Inject
    public TemplateEngineManagerImpl(Provider<TemplateEngineFreemarker> templateEngineFreemarker,
                                     Provider<TemplateEngineJsonGson> templateEngineJsonGson,
                                     Injector injector) {

        Map<String, Provider<? extends TemplateEngine>> map = new HashMap<String, Provider<? extends TemplateEngine>>();

        // First put the built in ones in, this is so they can be overridden by
        // custom bindings
        map.put(templateEngineFreemarker.get().getContentType(),
                templateEngineFreemarker);
        map.put(templateEngineJsonGson.get().getContentType(),
                templateEngineJsonGson);

        // Now lookup all explicit bindings, and find the ones that implement
        // TemplateEngine
        for (Map.Entry<Key<?>, Binding<?>> binding : injector.getBindings()
                .entrySet()) {
            if (TemplateEngine.class.isAssignableFrom(binding.getKey()
                    .getTypeLiteral().getRawType())) {
                Provider<? extends TemplateEngine> provider = (Provider) binding
                        .getValue().getProvider();
                map.put(provider.get().getContentType(), provider);
            }
        }

        contentTypeToTemplateEngineMap = ImmutableMap.copyOf(map);
    }

    @Override
    public TemplateEngine getTemplateEngineForContentType(String contentType) {
        Provider<? extends TemplateEngine> provider = contentTypeToTemplateEngineMap
                .get(contentType);

        if (provider != null) {
            return provider.get();
        } else {
            return null;
        }
    }
}
