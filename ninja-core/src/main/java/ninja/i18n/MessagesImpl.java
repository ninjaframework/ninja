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

package ninja.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import ninja.Context;
import ninja.Result;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SwissKnife;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MessagesImpl implements Messages {

    private static Logger logger = LoggerFactory.getLogger(MessagesImpl.class);

    private final Map<String, Configuration> langToKeyAndValuesMapping;

    private final NinjaProperties ninjaProperties;

    private final Lang lang;

    @Inject
    public MessagesImpl(NinjaProperties ninjaProperties,
                        Lang lang) {
        this.ninjaProperties = ninjaProperties;
        this.lang = lang;
        this.langToKeyAndValuesMapping = loadAllMessageFilesForRegisteredLanguages();
    }
    
    
    @Override
    public Optional<String> get(String key,
                      Context context,
                      Optional<Result> result,
                      Object... parameter) {
        Optional<String> language = lang.getLanguage(context, result);
        return get(key, language, parameter);
    }

    @Override
    public Optional<String> get(String key, Optional<String> language, Object... params) {

        Configuration configuration = getLanguageConfigurationForLocale(language);
        String value = configuration.getString(key);

        if (value != null) {
            MessageFormat messageFormat = getMessageFormatForLocale(value, language);
            return Optional.of(messageFormat.format(params));
        } else {
            return Optional.empty();
        }
    }
    
    @Override
    public Map<Object, Object> getAll(Context context, Optional<Result> result) {
        
        Optional<String> language = lang.getLanguage(context, result);
        return getAll(language);
        
    }

    @Override
    public Map<Object, Object> getAll(Optional<String> language) {

        Configuration configuration = getLanguageConfigurationForLocale(language);

        return ConfigurationConverter.getMap(configuration);

    }
    
    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 Context context,
                                 Optional<Result> result,
                                 Object... params) {
        
        Optional<String> language = lang.getLanguage(context, result);
        
        return getWithDefault(key, defaultMessage, language, params);

    }

    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 Optional<String> language,
                                 Object... params) {
        Optional<String> value = get(key, language, params);
        if (value.isPresent()) {
            return value.get();
        } else {
            MessageFormat messageFormat = getMessageFormatForLocale(defaultMessage, language);
            return messageFormat.format(params);
        }
    }

    /**
     * Attempts to load a message file and sets the file changed reloading
     * strategy on the configuration if the runtime mode is Dev.
     */
    private PropertiesConfiguration loadLanguageConfiguration(String fileOrUrl) {
        PropertiesConfiguration configuration = SwissKnife
                .loadConfigurationInUtf8(fileOrUrl);

        if (configuration != null && ninjaProperties.isDev()) {
            // enable runtime reloading of translations in dev mode
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            configuration.setReloadingStrategy(strategy);
        }

        return configuration;
    }

    /**
     * Does all the loading of message files.
     * 
     * Only registered messages in application.conf are loaded.
     * 
     */
    private  Map<String, Configuration> loadAllMessageFilesForRegisteredLanguages() {
        
        Map<String, Configuration> langToKeyAndValuesMappingMutable = Maps.newHashMap();

        // Load default messages:
        Configuration defaultLanguage = loadLanguageConfiguration("conf/messages.properties");

        // Make sure we got the file.
        // Everything else does not make much sense.
        if (defaultLanguage == null) {
            throw new RuntimeException(
                    "Did not find conf/messages.properties. Please add a default language file.");
        } else {
            langToKeyAndValuesMappingMutable.put("", defaultLanguage);
        }

        // Get the languages from the application configuration.
        String[] applicationLangs = ninjaProperties
                .getStringArray(NinjaConstant.applicationLanguages);

        // If we don't have any languages declared we just return.
        // We'll use the default messages.properties file.
        if (applicationLangs == null) {
            return ImmutableMap.copyOf(langToKeyAndValuesMappingMutable);
        }

        // Load each language into the HashMap containing the languages:
        for (String lang : applicationLangs) {

            // First step: Load complete language eg. en-US
            Configuration configuration = loadLanguageConfiguration(String
                    .format("conf/messages_%s.properties", lang));

            Configuration configurationLangOnly = null;

            // If the language has a country code load the default values for
            // the language, too. For instance missing variables in en-US will
            // be
            // Overwritten by the default languages.
            if (lang.contains("-")) {
                // get the lang
                String langOnly = lang.split("-")[0];

                // And load the configuraion
                configurationLangOnly = loadLanguageConfiguration(String
                        .format("conf/messages_%s.properties", langOnly));

            }

            // This is strange. If you defined the language in application.conf
            // it should be there propably.
            if (configuration == null) {
                logger.info(
                        "Did not find conf/messages_{}.properties but it was specified in application.conf. Using default language instead.",
                        lang);

            } else {

                // add new language, but combine with default language if stuff
                // is missing...
                CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
                // Add eg. "en-US"
                compositeConfiguration.addConfiguration(configuration);

                // Add eg. "en"
                if (configurationLangOnly != null) {
                    compositeConfiguration
                            .addConfiguration(configurationLangOnly);
                }
                // Add messages.conf (default pack)
                compositeConfiguration.addConfiguration(defaultLanguage);

                // and add the composed configuration to the hashmap with the
                // mapping.
                langToKeyAndValuesMappingMutable.put(lang,
                        (Configuration) compositeConfiguration);
            }

        }
        
        
        return ImmutableMap.copyOf(langToKeyAndValuesMappingMutable);

    }

    /**
     * Retrieves the matching language file from an arbitrary one or two part
     * locale String ("en-US", or "en" or "de"...).
     * <p>
     * 
     * @param language
     *            A two or one letter language code such as "en-US" or "en" or
     *            "en-US,en;q=0.8,de;q=0.6".
     * @return The matching configuration from the hashmap. Or the default
     *         mapping if no one has been found.
     */
    private Configuration getLanguageConfigurationForLocale(Optional<String> language) {

        // if language is null we return the default language.
        if (!language.isPresent()) {
            return langToKeyAndValuesMapping.get("");
        }

        // Check if we get a registered mapping for the language input string.
        // At that point the language may be either language-country or only country.
        // extract multiple languages from Accept-Language header
        String[] languages = language.get().split(",");
        for (String l: languages){
            l = l.trim();
            // Ignore the relative quality factor in Accept-Language header
            if (l.contains(";")){
                l = l.split(";")[0];
            }
            Configuration configuration = langToKeyAndValuesMapping.get(l);
            if (configuration != null) {
                return configuration;
            }

            // If we got a two part language code like "en-US" we split it and
            // search only for the language "en".
            if (l.contains("-")) {
                String[] array = l.split("-");
                String languageWithoutCountry = array[0];
                // Modify country code to upper case for IE and Firefox
                if(array.length > 1){
                    String country = array[1];
                    String languageWithUpperCaseCountry = languageWithoutCountry + "-" + country.toUpperCase();
                    configuration = langToKeyAndValuesMapping.get(languageWithUpperCaseCountry);
                    if (configuration != null) {
                        return configuration;
                    }
                }
                configuration = langToKeyAndValuesMapping
                        .get(languageWithoutCountry);

                if (configuration != null) {

                    return configuration;
                }

            }
        }

        // Oops. Nothing found. We return the default language (by convention guaranteed to work).
        return langToKeyAndValuesMapping.get("");

    }



    MessageFormat getMessageFormatForLocale(String value, Optional<String> language) {
        Locale locale = lang.getLocaleFromStringOrDefault(language);
        MessageFormat messageFormat = new MessageFormat(value, locale);
        return messageFormat;
    }
}
