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

package ninja.utils;

import com.google.common.base.Optional;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;

import static org.junit.Assert.assertTrue;

/**
 * Mock ninja properties, for testing
 *
 * @author James Roper
 */
public class MockNinjaProperties implements NinjaProperties {

    private String contextPath;
    /**
     * Create a mock ninja properties, with the given args as the properties.
     *
     * The arguments must be in key value pairs, every second argument being the
     * value for the key name in the previous argument.
     *
     * @param args The key value pairs.
     * @throws AssertionError If an odd number of arguments is supplied.
     */
    public static MockNinjaProperties create(String... args) {
        return createWithMode(NinjaConstant.MODE_TEST, null, args);
    }

    /**
     * Create a mock ninja properties, with the given args as the properties.
     *
     * The arguments must be in key value pairs, every second argument being the
     * value for the key name in the previous argument.
     *
     * @param mode The mode
     * @param args The key value pairs.
     * @throws AssertionError If an odd number of arguments is supplied.
     */
    public static MockNinjaProperties createWithMode(String mode, String contextPath, String... args) {
        assertTrue("You must supply an even number of arguments to form key value pairs",
                args.length % 2 == 0);
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.setDelimiterParsingDisabled(true);
        for (int i = 0; i < args.length; i+= 2) {
            props.addProperty(args[i], args[i + 1]);
        }
        return new MockNinjaProperties(mode, contextPath, props);
    }

    private final String mode;
    private final Configuration configuration;

    public MockNinjaProperties(String mode, String contextPath, Configuration configuration) {
        this.mode = mode;
        this.contextPath = contextPath;
        this.configuration = configuration;
    }

    @Override
    public String get(String key) {
        return configuration.getString(key);
    }

    @Override
    public String getOrDie(String key) {
        String value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("No key with name " + key + " found");
        } else {
            return value;
        }
    }

    @Override
    public Integer getInteger(String key) {
        String value = get(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Integer getIntegerOrDie(String key) {
        String value = getOrDie(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Boolean getBooleanOrDie(String key) {
        String value = getOrDie(key);
        if (value == null) {
            return null;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    @Override
    public Boolean getBoolean(String key) {
        String value = get(key);
        if (value == null) {
            return null;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    @Override
    public boolean isDev() {
        return mode.equals("dev");
    }

    @Override
    public boolean isTest() {
        return mode.equals("test");
    }

    @Override
    public boolean isProd() {
        return mode.equals("prod");
    }

    @Override
    public Optional<String> getContextPath() {
        return Optional.fromNullable(contextPath);
    }

    @Override
    public Properties getAllCurrentNinjaProperties() {
        return ConfigurationConverter.getProperties(configuration);
    }

	@Override
    public String[] getStringArray(String key) {
	    return configuration.getStringArray(key);
    }

	@Override
	public String getWithDefault(String key, String defaultValue) {
        String value = get(key);
        if (value == null) {
            return null;
        } else {
            return defaultValue;
        }
	}

	@Override
	public Integer getIntegerWithDefault(String key, Integer defaultValue) {
        Integer value = getInteger(key);
        if (value == null) {
            return null;
        } else {
            return defaultValue;
        }
	}

	@Override
	public Boolean getBooleanWithDefault(String key, Boolean defaultValue) {
        Boolean value = getBoolean(key);
        if (value == null) {
            return null;
        } else {
            return defaultValue;
        }
	}
}
