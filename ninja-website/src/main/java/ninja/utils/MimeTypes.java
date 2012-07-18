package ninja.utils;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ninja.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * MimeTypes utils 
 * Adapted from play 1.2.4
 */
@Singleton
public class MimeTypes {
	
	private Logger logger = LoggerFactory.getLogger(MimeTypes.class);

	private final String PROPERTY_MIMETYPE_PREFIX = "mimetype.";
	private final String DEFAULT_MIMET_TYPE_LOCATIONS = "ninja/utils/mime-types.properties";

	private final Properties mimetypes;
	private Pattern extPattern;

	private final NinjaProperties ninjaProperties;

	@Inject
	public MimeTypes(NinjaProperties ninjaProperties) {
		this.ninjaProperties = ninjaProperties;
		this.extPattern = Pattern.compile("^.*\\.([^.]+)$");

		mimetypes = new Properties();
		initMimetypes();
	}

	/**
	 * return the mimetype from a file name
	 * 
	 * @param filename
	 *            the file name
	 * @return the mimetype or the empty string if not found
	 */
	public String getMimeType(String filename) {
		return getMimeType(filename, "");
	}

	/**
	 * return the mimetype from a file name.<br/>
	 * 
	 * @param filename
	 *            the file name
	 * @param defaultMimeType
	 *            the default mime type to return when no matching mimetype is
	 *            found
	 * @return the mimetype
	 */
	public String getMimeType(String filename, String defaultMimeType) {
		Matcher matcher = extPattern.matcher(filename.toLowerCase());
		String ext = "";
		if (matcher.matches()) {
			ext = matcher.group(1);
		}
		if (ext.length() > 0) {
			String mimeType = mimetypes.getProperty(ext);
			if (mimeType == null) {
				return defaultMimeType;
			}
			return mimeType;
		}
		return defaultMimeType;
	}

	/**
	 * return the content-type from a file name. If none is found returning
	 * application/octet-stream<br/>
	 * For a text-based content-type, also return the encoding suffix eg.
	 * <em>"text/plain; charset=utf-8"</em>
	 * 
	 * @param filename
	 *            the file name
	 * @return the content-type deduced from the file extension.
	 */
	public String getContentType(Context context, String filename) {
		return getContentType(context, filename, "application/octet-stream");
	}

	/**
	 * return the content-type from a file name.<br/>
	 * For a text-based content-type, also return the encoding suffix eg.
	 * <em>"text/plain; charset=utf-8"</em>
	 * 
	 * @param filename
	 *            the file name
	 * @param defaultContentType
	 *            the default content-type to return when no matching
	 *            content-type is found
	 * @return the content-type deduced from the file extension.
	 */
	public String getContentType(Context context, String filename,
			String defaultContentType) {
		String contentType = getMimeType(filename, null);
		if (contentType == null) {
			contentType = defaultContentType;
		}
		if (contentType != null && contentType.startsWith("text/")) {			
			//UTF-8 is fixed for now as ninja only supports utf-8 in files...
			return contentType + "; charset=utf-8";
		}
		return contentType;
	}

	/**
	 * check the mimetype is referenced in the mimetypes database
	 * 
	 * @param mimeType
	 *            the mimeType to verify
	 */
	public boolean isValidMimeType(String mimeType) {
		if (mimeType == null) {
			return false;
		} else if (mimeType.indexOf(";") != -1) {
			return mimetypes.contains(mimeType.split(";")[0]);
		} else {
			return mimetypes.contains(mimeType);
		}
	}

	private void initMimetypes() {

		// Load default mimetypes from the framework
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(DEFAULT_MIMET_TYPE_LOCATIONS);
			
			mimetypes.load(is);

		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
		

		// Load custom mimetypes from the application configuration
		Enumeration confenum = ninjaProperties
				.getAllCurrentNinjaProperties().propertyNames();

		while (confenum.hasMoreElements()) {
			String key = (String) confenum.nextElement();
			if (key.startsWith(PROPERTY_MIMETYPE_PREFIX)) {
				String type = key.substring(key.indexOf('.') + 1).toLowerCase();
				String value = (String) ninjaProperties.get(key);
				mimetypes.setProperty(type, value);
			}
		}
	}

}
