package ninja.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class that contains a lot of random stuff that helps to 
 * get things done.
 * 
 * @author ra
 *
 */
public class SwissKnife {
	
	
	private static Logger logger = LoggerFactory.getLogger(SwissKnife.class);
	
	/**
	 * This is important: We load stuff as UTF-8.
	 * 
	 * Returns the file or null if not found.
	 * 
	 * @param classLoaderUrl
	 *            Classpath location of the configuration file. Eg
	 *            /conf/heroku.conf
	 * @param clazz We need a classloader and clazz provides the classloader.
	 * @return A configuration or null if there were problems getting it.
	 */
	public static Configuration loadConfigurationFromClasspathInUtf8(String classLoaderUrl, Class clazz) {

		PropertiesConfiguration c = new PropertiesConfiguration();

		URL resource = clazz.getClassLoader().getResource(classLoaderUrl);

		// if the resource cannot be found return null
		if (resource == null) {
			return null;
		}

		try {
			InputStream inputStream = resource.openStream();

			c.load(new InputStreamReader(inputStream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			c = null;
			logger.error(
			        "Unsupported encoding while loading configuration file", e);
		} catch (IOException e) {
			c = null;
			logger.error("Could not find configuration file. Was looking for "
			        + classLoaderUrl, e);
		} catch (NullPointerException e) {

			logger.error("Could not find configuration file. Was looking for "
			        + classLoaderUrl, e);
			return null;
		} catch (ConfigurationException e) {
			c = null;
			logger.error("Configuration Exception.", e);
		}

		return (Configuration) c;
	}

}
