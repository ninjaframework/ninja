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

package ninja.template;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;

import org.rythmengine.Rythm;
import org.rythmengine.extension.ILoggerFactory;
import org.rythmengine.extension.ISourceCodeEnhancer;
import org.rythmengine.logger.ILogger;
import org.rythmengine.template.ITemplate;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * To Configure Rythm in Ninja style.
 * 
 * @author sojin
 * 
 */
@Singleton
public class TemplateRythmConfiguration {

    private final Messages messages;

    private final NinjaProperties ninjaProperties;

    private final Logger logger;

    private final Properties properties = new Properties();

    @Inject
    public TemplateRythmConfiguration(Messages messages,
                              Logger ninjaLogger,
                              TemplateEngineManager templateEngineManager,
                              NinjaProperties ninjaProperties) {

        this.messages = messages;
        this.logger = ninjaLogger;
        this.ninjaProperties = ninjaProperties;

        init();
    }

    private void init() {

        final boolean isProd = ninjaProperties.isProd();

        properties.put("rythm.engine.mode", isProd ? Rythm.Mode.prod
                : Rythm.Mode.dev);
        properties.put("rythm.engine.plugin.version", "");

        properties.put("rythm.log.factory", new ILoggerFactory() {
            @Override
            public ILogger getLogger(Class<?> clazz) {
                return new TemplateEngineRythmLogger(logger);
            }
        });

        String templateDir = System.getProperty("user.dir") + File.separator
                + "src" + File.separator + "main" + File.separator + "java"
                + File.separator + "views";

        properties.put("rythm.home.template", new File(templateDir));
        logger.debug("rythm template root set to: {}",
                properties.get("rythm.home.template"));

        boolean gae = false;// TODO get GAE status.
        if (!gae) {
            File tmpDir = new File(System.getProperty("user.dir")
                    + File.separator + "target" + File.separator + "tmp",
                    "rythm");
            tmpDir.mkdirs();
            properties.put("rythm.home.tmp", tmpDir);
            logger.debug("rythm tmp dir set to {}",
                    properties.get("rythm.home.tmp"));
        } else {
            logger.warn("GAE enabled");
            properties.put("rythm.engine.file_write", false);
        }

        properties.put("rythm.resource.autoScan", false);

        properties.put("rythm.codegen.source_code_enhancer",
                new ISourceCodeEnhancer() {

                    @Override
                    public Map<String, ?> getRenderArgDescriptions() {
                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("flash", "java.util.Map<String, String>");
                        m.put("lang", "java.lang.String");
                        m.put("contextPath", "java.lang.String");
                        m.put("session", "java.util.Map<String, String>");
                        return m;
                    }

                    @Override
                    public void setRenderArgs(ITemplate template) {

                    }

                    @Override
                    public List<String> imports() {
                        return new ArrayList<String>();
                    }

                    @Override
                    public String sourceCode() {
                        return null;
                    }
                });

        properties.put("rythm.i18n.message.resolver",
                new TemplateEngineRythmI18nMessageResolver(messages));

        // Set Rythm properties coming from Ninja application.conf
        // allows users to override any rythm properties.
        for (String key : ninjaProperties.getAllCurrentNinjaProperties()
                .stringPropertyNames()) {
            if (key.startsWith("rythm.")) {
                properties.setProperty(key, ninjaProperties.get(key));
            }
        }
    }

    public Properties getConfiguration() {
        return properties;
    }
}
