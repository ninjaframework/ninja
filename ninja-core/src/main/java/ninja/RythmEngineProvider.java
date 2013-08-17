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

package ninja;

import ninja.i18n.Messages;
import ninja.template.TemplateRythmConfiguration;
import ninja.template.TemplateEngineManager;
import ninja.utils.NinjaProperties;

import org.rythmengine.RythmEngine;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Instance provider for RythmEngine class.
 * 
 * @author sojin
 */
public class RythmEngineProvider implements Provider<RythmEngine> {

    private final Messages messages;
    private final Logger logger;
    private final NinjaProperties ninjaProperties;
    private final TemplateEngineManager templateEngineManager;

    @Inject
    public RythmEngineProvider(Messages messages,
                               Logger logger,
                               TemplateEngineManager templateEngineManager,
                               NinjaProperties ninjaProperties) {
        this.messages = messages;
        this.logger = logger;
        this.templateEngineManager = templateEngineManager;
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public RythmEngine get() {
        TemplateRythmConfiguration conf = new TemplateRythmConfiguration(messages, logger,
                templateEngineManager, ninjaProperties);
        return new RythmEngine(conf.getConfiguration());
    }
}
