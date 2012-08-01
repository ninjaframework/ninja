package ninja.utils;

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
	 * We are using in the default Apache Commons loading mechanism.
	 * 
	 * With two little tweaks:
	 * 1. We don't accept any delimimter by default
	 * 2. We are reading in UTF-8
	 * 
	 * More about that:
	 * http://commons.apache.org/configuration/userguide/howto_filebased.html#Loading
	 * 
	 * From the docs:
	 * - If the combination from base path and file name is a full URL that points to an existing file, this URL will be used to load the file.
     * - If the combination from base path and file name is an absolute file name and this file exists, it will be loaded.
     * - If the combination from base path and file name is a relative file path that points to an existing file, this file will be loaded.
     * - If a file with the specified name exists in the user's home directory, this file will be loaded.
     * - Otherwise the file name is interpreted as a resource name, and it is checked whether the data file can be loaded from the classpath.
	 *
	 * @param fileOrUrlOrClasspathUrl Location of the file. Can be on file system, or on the classpath. Will both work.
	 * @param clazz We need a classloader and clazz provides the classloader.
	 * @return A configuration or null if there were problems getting it.
	 */
	public static Configuration loadConfigurationInUtf8(String fileOrUrlOrClasspathUrl) {

		PropertiesConfiguration c = new PropertiesConfiguration();
		c.setEncoding(NinjaConstant.UTF_8);
		c.setDelimiterParsingDisabled(true);
		
		try {
		    
            c.load(fileOrUrlOrClasspathUrl);
            
        } catch (ConfigurationException e) {
            
            logger.info(
                    "Could not load file " +  fileOrUrlOrClasspathUrl + " (not a bad thing necessarily, but I am returing null)");
            
            return null;
        }

		return (Configuration) c;
	}

}
