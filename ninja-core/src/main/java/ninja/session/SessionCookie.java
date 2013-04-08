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

package ninja.session;

import java.util.Map;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

/**
 * Session Cookie... Mostly an adaption of Play1's excellent cookie system that
 * in turn is based on the new client side rails cookies.
 */
@ImplementedBy(SessionCookieImpl.class)
public interface SessionCookie {
	
	public void init(Context context);

	/**
	 * @return id of a session.
	 */
	public String getId();

	/**
	 * @return complete content of session.
	 */
	public Map<String, String> getData();

	/**
	 * @return an authenticity token or generates a new one.
	 */
	public String getAuthenticityToken();

	public void save(Context context, Result result);


	public void put(String key, String value);

	/**
	 * Returns the value of the key or null.
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key);

	/**
	 * Removes the value of the key and returns the value or null.
	 * 
	 * @param key
	 * @return
	 */
	public String remove(String key);

	public void clear();
	/**
	 * Returns true if the session is empty, e.g. does not contain anything else
	 * than the timestamp key.
	 */
	public boolean isEmpty();

}
