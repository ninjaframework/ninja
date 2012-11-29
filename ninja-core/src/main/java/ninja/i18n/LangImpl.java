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

package ninja.i18n;

import java.text.MessageFormat;
import java.util.Map;

import javax.annotation.Nullable;

import ninja.NinjaPaths;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SwissKnife;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

    private static Logger logger = LoggerFactory.getLogger(LangImpl.class);

    private Map<String, Configuration> langToKeyAndValuesMapping;
    
    /**
     * Maps the key to all properties => in that case prefixed by "i18n." by convention.
     * This makes them easily readable and useable in templates.
     */
    private Map<String, Map<Object, Object>> langToPrefixedKeyMapping;

    private final NinjaProperties ninjaProperties;

    @Inject
    public LangImpl(NinjaProperties ninjaProperties) {

        this.ninjaProperties = ninjaProperties;
        this.langToKeyAndValuesMapping = Maps.newHashMap();

        loadAllMessageFilesForRegisteredLanguages();

    }

    @Override
    public String get(String key, String language, Object... params) {

        Configuration configuration = getLanguageConfigurationForLocale(language);

        String value = configuration.getString(key);

        if (value != null) {
            return MessageFormat.format(value, params);
        } else {
            return null;
        }

    }

    @Override
    public Map<Object, Object> getAll(String language) {

        Configuration configuration = getLanguageConfigurationForLocale(language);

        return ConfigurationConverter.getMap(configuration);

    }

    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 String language,
                                 Object... params) {

        String value = get(key, language, params);

        if (value != null) {

            return MessageFormat.format(value, params);

        } else {
            // return default message
            return MessageFormat.format(defaultMessage, params);

        }

    }

    /**
     * Does all the loading of message files.
     * 
     * Only registered messages in application.conf are loaded.
     * 
     */
    private void loadAllMessageFilesForRegisteredLanguages() {

        // Load default messages:
        Configuration defaultLanguage = SwissKnife
                .loadConfigurationInUtf8(NinjaPaths.getI18n());

        // Make sure we got the file.
        // Everything else does not make much sense.
        if (defaultLanguage == null) {
            throw new RuntimeException(
                    "Did not find "+ NinjaPaths.getI18n()+". Please add a default language file.");
        } else {
            langToKeyAndValuesMapping.put("", defaultLanguage);
        }

        // Get the languages from the application configuration.
        String[] applicationLangs = ninjaProperties
                .getStringArray(NinjaConstant.applicationLanguages);

        // If we don't have any languages declared we just return.
        // We'll use the default messages.properties file.
        if (applicationLangs == null) {
            return;
        }

        // Load each language into the HashMap containing the languages:
        for (String lang : applicationLangs) {

            // First step: Load complete language eg. en-US
            Configuration configuration = SwissKnife
                    .loadConfigurationInUtf8(NinjaPaths.getI18n(lang));

            Configuration configurationLangOnly = null;

            // If the language has a country code load the default values for
            // the language, too. For instance missing variables in en-US will
            // be
            // Overwritten by the default languages.
            if (lang.contains("-")) {
                // get the lang
                String langOnly = lang.split("-")[0];

                // And load the configuraion
                configurationLangOnly = SwissKnife
                        .loadConfigurationInUtf8(NinjaPaths.getI18n(lang));

            }

            // This is strange. If you defined the language in application.conf
            // it should be there propably.
            if (configuration == null) {
                logger.info("Did not find "
                        + NinjaPaths.getI18n(lang)
                        + " but it was specified in application.conf. Using default language instead.");

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
                langToKeyAndValuesMapping.put(lang,
                        (Configuration) compositeConfiguration);
            }

        }

    }

    /**
     * Retrieves the matching language file from an arbitrary one or two part
     * locale String ("en-US", or "en" or "de"...).
     * <p>
     * 
     * @param language
     *            A two or one letter language code such as "en-US" or "en"
     * @return The matching configuration from the hashmap. Or the default
     *         mapping if no one has been found.
     */
    private Configuration getLanguageConfigurationForLocale(@Nullable String language) {

        // if language is null we return the default language.
        if (language == null) {
            return langToKeyAndValuesMapping.get("");
        }

        // Check if we get a registered mapping for the language input string.
        // At that point the language may be either language-country or only country
        Configuration configuration = langToKeyAndValuesMapping.get(language);
        if (configuration != null) {

            return configuration;
        }

        // If we got a two part language code like "en-US" we split it and
        // search only for the language "en".
        if (language.contains("-")) {

            String languageWithoutCountry = language.split("-")[0];

            configuration = langToKeyAndValuesMapping
                    .get(languageWithoutCountry);

            if (configuration != null) {

                return configuration;
            }

        }

        // Oops. Nothing found. We return the default language (by convention guaranteed to work).
        return langToKeyAndValuesMapping.get("");

    }

}
