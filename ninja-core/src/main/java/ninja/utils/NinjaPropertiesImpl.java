package ninja.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class NinjaPropertiesImpl implements NinjaProperties {

    private Properties allCurrentNinjaProperties;

    private String mode = "dev";

    @Inject
    public NinjaPropertiesImpl() {

        allCurrentNinjaProperties = new Properties();

        // get system variables... load application conf files...
        if (System.getProperty("mode") != null) {
            mode = System.getProperty("mode");
        }

        // 1. load application.conf
        Properties applicationProperties = loadProperties("conf/application.conf");

        // please add them:
        allCurrentNinjaProperties.putAll(getAllPropertiesOfThatMode(applicationProperties, mode));

    }

    @Override
    public String get(String key) {
        return allCurrentNinjaProperties.getProperty(key);

    }

    /**
     * This properties loader uses UTF-8... that's important...
     * 
     * @param classLoaderUrl
     * @return
     */
    private Properties loadProperties(String classLoaderUrl) {
        Properties props = new Properties();
        URL resource = getClass().getClassLoader().getResource(classLoaderUrl);

        try {
            props.load(new InputStreamReader(resource.openStream(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return props;
    }

    /**
     * This method does two things.
     * 
     * Of course we want to have environments.
     * By default ninja supports 3 modes:
     * - "test"
     * - "dev"
     * - "prod"
     * 
     * We are using one application.conf file containing all relevant configuration properties.
     * As conventions you have to use "%" when prefixing a property.
     * 
     * For instance if we are in mode "test"
     * This:
     * myproperty = funk
     * is overwritten by
     * %test.myproperty = funkier
     * 
     */
    private Properties getAllPropertiesOfThatMode(Properties properties, String mode) {

        Properties returnProtperties = new Properties();

        // The hashmap we get from properties is not ordered.
        // We therefore do two passes
        // Pass 1: Add all non % arguments
        // Pass 2: Add all % arguments matching this mode
        // Pass 2: Add all % arguments matching this mode
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {

            if (!entry.getKey().toString().startsWith("%")) {
                returnProtperties.put(entry.getKey(), entry.getValue());

            }
        }

        // Pass 2: Add all % arguments matching this mode
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {

            if (entry.getKey().toString().startsWith("%")) {

                // check if this is the mode I want to use...
                String myModePrefix = "%" + mode;

                if (entry.getKey().toString().startsWith(myModePrefix)) {
                    // please replace me:
                    // %test.myproperty=test should become
                    // myproperty=test
                    String keyToReplace = "%" + mode + "\\.";

                    String newKeyName = entry.getKey().toString().replaceFirst(keyToReplace, "");

                    returnProtperties.put(newKeyName, entry.getValue());
                } // else do nothing... that's a property I don't want to use....

            }
        }

        return returnProtperties;

    }

}
