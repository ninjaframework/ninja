package ninja.session;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import ninja.Context;
import ninja.utils.CookieHelper;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

/**
 * Flash scope:
 * A client side cookie that can be used to transfer information from one request to another.
 * 
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * Please note also that flash cookies are not signed.
 */
public class FlashCookieImpl implements FlashCookie {

	private Pattern flashParser = Pattern
	        .compile("\u0000([^:]*):([^\u0000]*)\u0000");

	protected Map<String, String> currentFlashCookieData = new HashMap<String, String>();
	protected Map<String, String> outgoingFlashCookieData = new HashMap<String, String>();
	
	private String applicationCookiePrefix;
	
	@Inject
	public FlashCookieImpl(NinjaProperties ninjaProperties) {
		this.applicationCookiePrefix = ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix);
	}
	
	@Override
	public void init(Context context) {
		// get flash cookie:
		Cookie[] cookies = context.getHttpServletRequest().getCookies();

		Cookie flashCookie = CookieHelper.getCookie(
				applicationCookiePrefix
		                + ninja.utils.NinjaConstant.FLASH_SUFFIX, cookies);

		if (flashCookie != null) {
			String flashData;
			try {
				flashData = URLDecoder.decode(flashCookie.getValue(), "utf-8");

				Matcher matcher = flashParser.matcher(flashData);
				while (matcher.find()) {
					currentFlashCookieData.put(matcher.group(1), matcher.group(2));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void save(Context context) {

		if (outgoingFlashCookieData.isEmpty()) {

			if (CookieHelper.getCookie(applicationCookiePrefix
			        + ninja.utils.NinjaConstant.FLASH_SUFFIX, context
			        .getHttpServletRequest().getCookies()) != null) {

				Cookie cookie = new Cookie(applicationCookiePrefix
				        + ninja.utils.NinjaConstant.FLASH_SUFFIX, "");
				cookie.setPath("/");
				cookie.setSecure(false);
				cookie.setMaxAge(0);

				context.getHttpServletResponse().addCookie(cookie);

			}

			return;

		}

		else {
			try {
				StringBuilder flash = new StringBuilder();
				for (String key : outgoingFlashCookieData.keySet()) {
					flash.append("\u0000");
					flash.append(key);
					flash.append(":");
					flash.append(outgoingFlashCookieData.get(key));
					flash.append("\u0000");
				}
				String flashData = URLEncoder.encode(flash.toString(), "utf-8");

				Cookie cookie = new Cookie(applicationCookiePrefix
				        + ninja.utils.NinjaConstant.FLASH_SUFFIX, flashData);
				cookie.setPath("/");
				cookie.setSecure(false);
				cookie.setMaxAge(0);

				context.getHttpServletResponse().addCookie(cookie);

			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
	
	@Override
	public void put(String key, String value) {
		if (key.contains(":")) {
			throw new IllegalArgumentException(
			        "Character ':' is invalid in a flash key.");
		}
		currentFlashCookieData.put(key, value);
		outgoingFlashCookieData.put(key, value);
	}
	
	@Override
	public void put(String key, Object value) {
		if (value == null) {
			put(key, (String) null);
		}
		put(key, value + "");
	}
	
	@Override
	public void now(String key, String value) {
		if (key.contains(":")) {
			throw new IllegalArgumentException(
			        "Character ':' is invalid in a flash key.");
		}
		currentFlashCookieData.put(key, value);
	}
	
	@Override
	public void error(String value, Object... args) {
		put("error", String.format(value, args));
	}
	
	@Override
	public void success(String value, Object... args) {
		put("success", String.format(value, args));
	}
	
	@Override
	public void discard(String key) {
		outgoingFlashCookieData.remove(key);
	}
	
	@Override
	public void discard() {
		outgoingFlashCookieData.clear();
	}
	
	@Override
	public void keep(String key) {
		if (currentFlashCookieData.containsKey(key)) {
			outgoingFlashCookieData.put(key, currentFlashCookieData.get(key));
		}
	}
	
	@Override
	public void keep() {
		outgoingFlashCookieData.putAll(currentFlashCookieData);
	}
	
	@Override
	public String get(String key) {
		return currentFlashCookieData.get(key);
	}
	
	@Override
	public boolean remove(String key) {
		return currentFlashCookieData.remove(key) != null;
	}
	
	@Override
	public void clearCurrentFlashCookieData() {
		currentFlashCookieData.clear();
	}
	
	@Override
	public boolean contains(String key) {
		return currentFlashCookieData.containsKey(key);
	}

	@Override
	public String toString() {
		return currentFlashCookieData.toString();
	}
}
