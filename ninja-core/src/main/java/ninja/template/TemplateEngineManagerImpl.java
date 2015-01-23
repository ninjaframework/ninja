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

package ninja.template;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class TemplateEngineManagerImpl implements TemplateEngineManager {

    private final Logger logger = LoggerFactory.getLogger(TemplateEngineManagerImpl.class);

    // Keep a reference of providers rather than instances, so template engines
    // don't have
    // to be singleton if they don't want
    private final Map<String, Provider<? extends TemplateEngine>> contentTypeToTemplateEngineMap;

    @Inject
    public TemplateEngineManagerImpl(Provider<TemplateEngineFreemarker> templateEngineFreemarker,
                                     Provider<TemplateEngineJson> templateEngineJson,
                                     Provider<TemplateEngineJsonP> templateEngineJsonP,
                                     Provider<TemplateEngineXml> templateEngineXmlProvider,
                                     Provider<TemplateEngineText> templateEngineTextProvider,
                                     Injector injector) {

        Map<String, Provider<? extends TemplateEngine>> map = new HashMap<String, Provider<? extends TemplateEngine>>();

        // First put the built in ones in, this is so they can be overridden by
        // custom bindings
        map.put(templateEngineFreemarker.get().getContentType(),
                templateEngineFreemarker);
        map.put(templateEngineJson.get().getContentType(),
                templateEngineJson);
        map.put(templateEngineJsonP.get().getContentType(),
                templateEngineJsonP);
        map.put(templateEngineXmlProvider.get().getContentType(),
                templateEngineXmlProvider);
        map.put(templateEngineTextProvider.get().getContentType(),
                templateEngineTextProvider);

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

        this.contentTypeToTemplateEngineMap = ImmutableMap.copyOf(map);

        logTemplateEngines();
    }

    @Override
    public Set<String> getContentTypes() {
        return ImmutableSet.copyOf(contentTypeToTemplateEngineMap.keySet());
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

    protected void logTemplateEngines() {
        List<String> outputTypes = Lists.newArrayList(getContentTypes());
        Collections.sort(outputTypes);

        if (outputTypes.isEmpty()) {

            logger.error("No registered template engines?! Please install a template module!");
            return;

        }

        int maxContentTypeLen = 0;
        int maxTemplateEngineLen = 0;

        for (String contentType : outputTypes) {

            TemplateEngine templateEngine = getTemplateEngineForContentType(contentType);

            maxContentTypeLen = Math.max(maxContentTypeLen,
                    contentType.length());
            maxTemplateEngineLen = Math.max(maxTemplateEngineLen,
                    templateEngine.getClass().getName().length());

        }

        int borderLen = 6 + maxContentTypeLen + maxTemplateEngineLen;
        String border = Strings.padEnd("", borderLen, '-');

        logger.info(border);
        logger.info("Registered response template engines");
        logger.info(border);

        for (String contentType : outputTypes) {

            TemplateEngine templateEngine = getTemplateEngineForContentType(contentType);
            logger.info("{}  =>  {}",
                    Strings.padEnd(contentType, maxContentTypeLen, ' '),
                    templateEngine.getClass().getName());

        }

    }
}
