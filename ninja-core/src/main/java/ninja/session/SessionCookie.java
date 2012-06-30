package ninja.session;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import ninja.Context;
import ninja.utils.Codec;
import ninja.utils.CookieHelper;
import ninja.utils.Crypto;

import com.google.inject.Inject;

/**
 * Session Cookie...
 * In the first version mostly an adaption of Play1's cookie system that in turn is based on the new
 * client side rails cookies.
 */
public class SessionCookie {

	private final String COOKIE_PREFIX = "NINJA";

	// => from config in the future...
	public static final String COOKIE_EXPIRE = "1000";
	

	private Pattern sessionParser = Pattern
	        .compile("\u0000([^:]*):([^\u0000]*)\u0000");
	private final String AT_KEY = "___AT";
	private final String ID_KEY = "___ID";
	private final String TS_KEY = "___TS";

	private Map<String, String> data = new HashMap<String, String>();
	boolean changed = false;

	Context context;
	
	private final Crypto crypto;

	@Inject
	public SessionCookie(Crypto crypto) {
		this.crypto = crypto;

	}

	public void init(Context context) {
		
		this.context = context;
		
		try {

			Cookie cookie = CookieHelper.getCookie(COOKIE_PREFIX + "_SESSION",
			        context.getHttpServletRequest().getCookies());

			if (cookie != null && cookie.getValue() != null
			        && !cookie.getValue().trim().equals("")) {
				String value = cookie.getValue();
				String sign = value.substring(0, value.indexOf("-"));
				String payload = value.substring(value.indexOf("-") + 1);
				if (sign.equals(crypto.signHmacSha1(payload))) {
					String sessionData = URLDecoder.decode(payload, "utf-8");
					Matcher matcher = sessionParser.matcher(sessionData);
					while (matcher.find()) {
						data.put(matcher.group(1), matcher.group(2));
					}
				}

				if (COOKIE_EXPIRE != null) {
					// Verify that the session contains a timestamp, and that
					// it's not expired
					if (!data.containsKey(TS_KEY)) {
						data.clear();
					} else {
						if (Long.parseLong(data.get(TS_KEY)) < System
						        .currentTimeMillis()) {
							// Session expired
							data.clear();
						}
					}
					data.put(
					        TS_KEY,
					        "" + System.currentTimeMillis()
					                + Long.parseLong(COOKIE_EXPIRE) * 1000);
				}
			} else {
				// no previous cookie to restore; but we may have to set the
				// timestamp in the new cookie
				if (COOKIE_EXPIRE != null) {
					data.put(
					        TS_KEY,
					        "" + System.currentTimeMillis()
					                +Long.parseLong(COOKIE_EXPIRE) * 1000);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Corrupted HTTP session");
		}
	}

	public String getId() {
		if (!data.containsKey(ID_KEY)) {
			data.put(ID_KEY, ninja.utils.Codec.UUID());
		}
		return data.get(ID_KEY);

	}

	public Map<String, String> all() {
		return data;
	}

	public String getAuthenticityToken() {
		if (!data.containsKey(AT_KEY)) {
			data.put(AT_KEY, UUID.randomUUID().toString());
		}
		return data.get(AT_KEY);
	}

	void change() {
		changed = true;
	}

	void save() {

		if (isEmpty()) {
			// The session is empty: delete the cookie
			
			if (CookieHelper.getCookie(COOKIE_PREFIX + "_SESSION", context.getHttpServletRequest().getCookies()) != null) {
				
				Cookie cookie = new Cookie(COOKIE_PREFIX + "_SESSION", "");
				cookie.setPath("/");
				cookie.setSecure(false);
				cookie.setMaxAge(0);

				context.getHttpServletResponse().addCookie(cookie);
				
			}
			
			return;

		}
		try {
			StringBuilder session = new StringBuilder();
			for (String key : data.keySet()) {
				session.append("\u0000");
				session.append(key);
				session.append(":");
				session.append(data.get(key));
				session.append("\u0000");
			}
			String sessionData = URLEncoder.encode(session.toString(), "utf-8");
			String sign = crypto.signHmacSha1(sessionData);
			
			if (COOKIE_EXPIRE == null) {

				Cookie cookie = new Cookie(COOKIE_PREFIX + "_SESSION", "");
				cookie.setPath("/");
				cookie.setSecure(false);
				//cookie.setMaxAge();

				context.getHttpServletResponse().addCookie(cookie);
				
			} else {
				Cookie cookie = new Cookie(COOKIE_PREFIX + "_SESSION", "");
				cookie.setPath("/");
				cookie.setSecure(false);
				//cookie.setMaxAge(COOKIE_EXPIRE);

				context.getHttpServletResponse().addCookie(cookie);
				

			}
		} catch (Exception e) {
			throw new RuntimeException("Session serializationProblem", e);
		}
	}

	public void put(String key, String value) {
		if (key.contains(":")) {
			throw new IllegalArgumentException(
			        "Character ':' is invalid in a session key.");
		}
		change();
		if (value == null) {
			data.remove(key);
		} else {
			data.put(key, value);
		}
	}

	public void put(String key, Object value) {
		change();
		if (value == null) {
			put(key, (String) null);
		}
		put(key, value + "");
	}

	public String get(String key) {
		return data.get(key);
	}

	public boolean remove(String key) {
		change();
		return data.remove(key) != null;
	}

	public void remove(String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	public void clear() {
		change();
		data.clear();
	}

	/**
	 * Returns true if the session is empty, e.g. does not contain anything else
	 * than the timestamp
	 */
	public boolean isEmpty() {
		for (String key : data.keySet()) {
			if (!TS_KEY.equals(key)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return data.toString();
	}
}
