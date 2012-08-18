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
import ninja.utils.CookieHelper;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

/**
 * Session Cookie... Mostly an adaption of Play1's excellent cookie system that
 * in turn is based on the new client side rails cookies.
 */
public class SessionCookieImpl implements SessionCookie {

    private static final String AUTHENTICITY_KEY = "___AT";
    private static final String ID_KEY = "___ID";
    private static final String TIMESTAMP_KEY = "___TS";
    private static final Pattern sessionParser = Pattern
            .compile("\u0000([^:]*):([^\u0000]*)\u0000");

    private final Crypto crypto;

    private final Integer sessionExpireTimeInMs;
    private final Boolean sessionSendOnlyIfChanged;
    private final Boolean sessionTransferredOverHttpsOnly;
    private final String applicationCookiePrefix;
    private final Map<String, String> data = new HashMap<String, String>();

    /** Has cookie been changed => only send new cookie stuff has been changed */
    private boolean sessionDataHasBeenChanged = false;

	@Inject
	public SessionCookieImpl(
			Crypto crypto,
			NinjaProperties ninjaProperties) {
		
		this.crypto = crypto;
		
		//read configuration stuff:
        Integer sessionExpireTimeInSeconds = ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds);
        if (sessionExpireTimeInSeconds != null) {
            this.sessionExpireTimeInMs = sessionExpireTimeInSeconds * 1000;
        } else {
            this.sessionExpireTimeInMs = null;
        }
        
		this.sessionSendOnlyIfChanged = ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionSendOnlyIfChanged, true);
		this.sessionTransferredOverHttpsOnly = ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true);
		
		this.applicationCookiePrefix = ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix);
	}

	/**
	 * Has to be called initially. => maybe in the future as assisted inject.
	 * 
	 * @param context
	 */
	@Override
	public void init(Context context) {

		try {

			// get the cookie that contains session information:
			Cookie cookie = CookieHelper.getCookie(
					applicationCookiePrefix
			                + ninja.utils.NinjaConstant.SESSION_SUFFIX,
			        context.getHttpServletRequest().getCookies());

			// check that the cookie is not empty:
			if (cookie != null && cookie.getValue() != null
			        && !cookie.getValue().trim().equals("")) {

				String value = cookie.getValue();

				// the first substring until "-" is the sign
				String sign = value.substring(0, value.indexOf("-"));

				// rest from "-" until the end it the payload of the cookie
				String payload = value.substring(value.indexOf("-") + 1);

				// check if payload is valid:
				if (sign.equals(crypto.signHmacSha1(payload))) {

					String sessionData = URLDecoder.decode(payload,
					        NinjaConstant.UTF_8);

					// parse the stuff...
					Matcher matcher = sessionParser.matcher(sessionData);

					// ... and put it into the data hashmap...
					while (matcher.find()) {
						data.put(matcher.group(1), matcher.group(2));
					}

				}

				if (sessionExpireTimeInMs != null) {
					// Make sure session contains valid timestamp

					if (!data.containsKey(TIMESTAMP_KEY)) {

						data.clear();

					} else {
						if (Long.parseLong(data.get(TIMESTAMP_KEY)) + sessionExpireTimeInMs
                                < System.currentTimeMillis()) {
							// Session expired
                            sessionDataHasBeenChanged = true;
							data.clear();
						}
					}

					// Everything's alright => prolong session
					data.put(TIMESTAMP_KEY, "" + System.currentTimeMillis());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Corrupted HTTP session", e);
		}
	}

	/**
	 * @return id of a session.
	 */
	@Override
	public String getId() {
		if (!data.containsKey(ID_KEY)) {
			data.put(ID_KEY, UUID.randomUUID().toString());
		}
		return data.get(ID_KEY);

	}

	/**
	 * @return complete content of session.
	 */
    @Override
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @return an authenticity token or generates a new one.
	 */
    @Override
	public String getAuthenticityToken() {
		if (!data.containsKey(AUTHENTICITY_KEY)) {
			data.put(AUTHENTICITY_KEY, UUID.randomUUID().toString());
		}
		return data.get(AUTHENTICITY_KEY);
	}

    @Override
	public void save(Context context) {

        // Don't save the cookie nothing has changed, and if we're not expiring or
        // we are expiring but we're only updating if the session changes
        if (!sessionDataHasBeenChanged && (sessionExpireTimeInMs == null
                || sessionSendOnlyIfChanged)) {
			// Nothing changed and no cookie-expire, consequently send nothing
			// back.
			return;
		}

        if (isEmpty()) {
            Cookie sessionCookie = CookieHelper.getCookie(
                    applicationCookiePrefix + NinjaConstant.SESSION_SUFFIX,
                    context.getHttpServletRequest().getCookies());

            // It is empty, but there was a session coming in, therefore clear it
            if (sessionCookie != null) {

                Cookie expiredSessionCookie = new Cookie(applicationCookiePrefix
                        + NinjaConstant.SESSION_SUFFIX, null);
                expiredSessionCookie.setPath("/");
                expiredSessionCookie.setMaxAge(0);

                context.getHttpServletResponse().addCookie(expiredSessionCookie);

            }
            return;

        }

        // Make sure if has a timestamp, if it needs one
        if (sessionExpireTimeInMs != null && !data.containsKey(TIMESTAMP_KEY)) {
            data.put(TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
        }

		try {
			StringBuilder session = new StringBuilder();

			for (String key : data.keySet()) {
				session.append(NinjaConstant.UNI_CODE_NULL_ENTITY);
				session.append(key);
				session.append(":");
				session.append(data.get(key));
				session.append(NinjaConstant.UNI_CODE_NULL_ENTITY);
			}
			String sessionData = URLEncoder.encode(session.toString(),
			        NinjaConstant.UTF_8);

			String sign = crypto.signHmacSha1(sessionData);

			Cookie cookie;

			cookie = new Cookie(applicationCookiePrefix
			        + NinjaConstant.SESSION_SUFFIX, sign + "-" + sessionData);
            cookie.setPath("/");

            if (sessionExpireTimeInMs != null) {
                cookie.setMaxAge(sessionExpireTimeInMs / 1000);
            }
            if (sessionTransferredOverHttpsOnly != null) {
                cookie.setSecure(sessionTransferredOverHttpsOnly);
            }

			context.getHttpServletResponse().addCookie(cookie);

		} catch (Exception e) {
			throw new RuntimeException("Session serialization problem", e);
		}

	}

	/**
	 * Puts key into session. PLEASE NOTICE: If value == null the key will be
	 * removed!
	 * 
	 * @param key
	 * @param value
	 */
    @Override
	public void put(String key, String value) {

		// make sure key is valid:
		if (key.contains(":")) {
			throw new IllegalArgumentException(
			        "Character ':' is invalid in a session key.");
		}

		sessionDataHasBeenChanged = true;

		if (value == null) {
			remove(key);
		} else {
			data.put(key, value);
		}

	}

	/**
	 * Returns the value of the key or null.
	 * 
	 * @param key
	 * @return
	 */
    @Override
	public String get(String key) {
		return data.get(key);
	}
    
    @Override
	public String remove(String key) {

		sessionDataHasBeenChanged = true;
		String result = get(key);
		data.remove(key);
		return result;
	}

    @Override
	public void clear() {
		sessionDataHasBeenChanged = true;
		data.clear();
	}

	/**
	 * Returns true if the session is empty, e.g. does not contain anything else
	 * than the timestamp key.
	 */
    @Override
	public boolean isEmpty() {
        return (data.isEmpty() || data.size() == 1 && data.containsKey(TIMESTAMP_KEY));
	}

}
