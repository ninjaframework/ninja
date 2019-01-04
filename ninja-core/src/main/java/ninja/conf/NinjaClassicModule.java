/**
 * Copyright (C) 2012-2019 the original author or authors.
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

package ninja.conf;

import ninja.cache.Cache;
import ninja.cache.CacheProvider;
import ninja.jpa.JpaModule;
import ninja.migrations.MigrationInitializer;
import ninja.utils.ObjectMapperProvider;
import ninja.utils.XmlMapperProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.OptionalBinder;
import ninja.bodyparser.BodyParserEngineJson;
import ninja.bodyparser.BodyParserEngineMultipartPost;
import ninja.bodyparser.BodyParserEnginePost;
import ninja.bodyparser.BodyParserEngineXml;
import ninja.migrations.MigrationEngine;
import ninja.migrations.MigrationEngineProvider;
import ninja.template.TemplateEngineFreemarker;
import ninja.template.TemplateEngineJson;
import ninja.template.TemplateEngineJsonP;
import ninja.template.TemplateEngineText;
import ninja.template.TemplateEngineXml;
import ninja.utils.NinjaProperties;
import ninja.scheduler.SchedulerSupport;

/**
 * The classic configuration of the ninja framework (jackson, freemarker, 
 * postoffice, etc.)
 */
public class NinjaClassicModule extends AbstractModule {

    private final NinjaProperties ninjaProperties;
    private boolean freemarker;
    private boolean json;
    private boolean xml;
    private boolean cache;
    private boolean migrations;
    private boolean jpa;
    private boolean scheduler;

    public NinjaClassicModule(NinjaProperties ninjaProperties) {
        this(ninjaProperties, true);
    }
    
    public NinjaClassicModule(NinjaProperties ninjaProperties, boolean defaultEnabled) {
        this.ninjaProperties = ninjaProperties;
        this.freemarker = defaultEnabled;
        this.json = defaultEnabled;
        this.xml = defaultEnabled;
        this.cache = defaultEnabled;
        this.migrations = defaultEnabled;
        this.jpa = defaultEnabled;
        this.scheduler = defaultEnabled;
    }

    public NinjaClassicModule freemarker(boolean enabled) {
        this.freemarker = enabled;
        return this;
    }
    
    public NinjaClassicModule json(boolean enabled) {
        this.json = enabled;
        return this;
    }

    public NinjaClassicModule xml(boolean enabled) {
        this.xml = enabled;
        return this;
    }
    
    public NinjaClassicModule cache(boolean enabled) {
        this.cache = enabled;
        return this;
    }
    
    public NinjaClassicModule migrations(boolean enabled) {
        this.migrations = enabled;
        return this;
    }
    
    public NinjaClassicModule jpa(boolean enabled) {
        this.jpa = enabled;
        return this;
    }
    
    public NinjaClassicModule scheduler(boolean scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    @Override
    public void configure() {
        // NOTE: these are grouped to line up with third-party dependencies
        // (e.g. jackson supports templates & body parsers)
        
        // Text & post require no 3rd party libs
        bind(TemplateEngineText.class);
        bind(BodyParserEnginePost.class);
        bind(BodyParserEngineMultipartPost.class);
        
        // Freemarker
        if (freemarker) {
            bind(TemplateEngineFreemarker.class);
        }
        
        // Jackson json support
        if (json) {
            OptionalBinder.newOptionalBinder(binder(), ObjectMapper.class)
                .setDefault().toProvider(ObjectMapperProvider.class).in(Singleton.class);

            bind(TemplateEngineJson.class);
            bind(TemplateEngineJsonP.class);
            bind(BodyParserEngineJson.class);
        }
        
        // Jackson xml support
        if (xml) {
            OptionalBinder.newOptionalBinder(binder(), XmlMapper.class)
                .setDefault().toProvider(XmlMapperProvider.class).in(Singleton.class);

            bind(TemplateEngineXml.class);
            bind(BodyParserEngineXml.class);
        }

        // Cache
        if (cache) {
            bind(Cache.class).toProvider(CacheProvider.class);
        }
        
        // Migrations
        if (migrations) {
            bind(MigrationEngine.class).toProvider(MigrationEngineProvider.class);
            bind(MigrationInitializer.class).asEagerSingleton();
        }
        
        // JPA
        if (jpa) {
            install(new JpaModule(ninjaProperties));
        }

        // Scheduler
        if (scheduler) {
            install(SchedulerSupport.getModule());
        }
    }

}
