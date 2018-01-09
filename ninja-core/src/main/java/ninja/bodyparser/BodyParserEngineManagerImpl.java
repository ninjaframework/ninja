/**
 * Copyright (C) 2012-2018 the original author or authors.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEngineManagerImpl implements BodyParserEngineManager {

    private final Logger logger = LoggerFactory.getLogger(BodyParserEngineManagerImpl.class);

    // Keep a reference of providers rather than instances, so body parser engines
    // don't have to be singleton if they don't want
    private final Map<String, Provider<? extends BodyParserEngine>> contentTypeToBodyParserMap;

    @Inject
    public BodyParserEngineManagerImpl(Injector injector) {
        Map<String, Provider<? extends BodyParserEngine>> map = Maps.newHashMap();

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

        this.contentTypeToBodyParserMap = ImmutableMap.copyOf(map);

        logBodyParserEngines();
    }

    @Override
    public Set<String> getContentTypes() {
        return ImmutableSet.copyOf(contentTypeToBodyParserMap.keySet());
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

    final protected void logBodyParserEngines() {
        List<String> outputTypes = Lists.newArrayList(getContentTypes());
        Collections.sort(outputTypes);

        int maxContentTypeLen = 0;
        int maxBodyParserEngineLen = 0;

        for (String contentType : outputTypes) {

            BodyParserEngine bodyParserEngine = getBodyParserEngineForContentType(contentType);

            maxContentTypeLen = Math.max(maxContentTypeLen,
                    contentType.length());
            maxBodyParserEngineLen = Math.max(maxBodyParserEngineLen,
                    bodyParserEngine.getClass().getName().length());

        }

        int borderLen = 6 + maxContentTypeLen + maxBodyParserEngineLen;
        String border = Strings.padEnd("", borderLen, '-');

        logger.info(border);
        logger.info("Registered request bodyparser engines");
        logger.info(border);

        for (String contentType : outputTypes) {

            BodyParserEngine templateEngine = getBodyParserEngineForContentType(contentType);
            logger.info("{}  =>  {}",
                    Strings.padEnd(contentType, maxContentTypeLen, ' '),
                    templateEngine.getClass().getName());

        }

    }
}
