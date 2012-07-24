package ninja.utils;

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
        return createWithMode(NinjaConstant.MODE_TEST, args);
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
    public static MockNinjaProperties createWithMode(String mode, String... args) {
        assertTrue("You must supply an even number of arguments to form key value pairs",
                args.length % 2 == 0);
        PropertiesConfiguration props = new PropertiesConfiguration();
        props.setDelimiterParsingDisabled(true);
        for (int i = 0; i < args.length; i+= 2) {
            props.addProperty(args[i], args[i + 1]);
        }
        return new MockNinjaProperties(mode, props);
    }

    private final String mode;
    private final Configuration configuration;

    public MockNinjaProperties(String mode, Configuration configuration) {
        this.mode = mode;
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
            throw new RuntimeException();
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
