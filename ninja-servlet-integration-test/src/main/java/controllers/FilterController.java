/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package controllers;


import ninja.Context;
import ninja.Cookie;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;

import com.google.inject.Singleton;

import filters.LoggerFilter;
import filters.TeaPotFilter;

@Singleton
public class FilterController {

	/**
	 * Not yet finished.
	 * 
	 * Simply demonstrates how controllers can be annotated and filtered using
	 * the FilterWith annotation:
	 * 
	 * @param context
	 */
	@FilterWith(SecureFilter.class)
	public Result filter(Context context) {

		// System.out.println("cookies: " +
		// context.getHttpServletRequest().getCookies());

		if (context.getCookies() != null) {
			for (Cookie cookie : context.getCookies()) {
				System.out.println("cookie: "
						+ cookie.getName());

			}
		}

		return Results.html()
                .addCookie(Cookie.builder("myname", "myvalue").build());

	}
	/**
	 * Really cool. We are using two filters on the method.
	 * 
	 * Filters are executed sequentially. First the LoggerFilter then the
	 * TeaPotFilter.
	 * 
	 * The TeaPotFilter changes completely the output and the status.
	 * 
	 * @param context
	 */
	@FilterWith({
		LoggerFilter.class, 
		TeaPotFilter.class})
	public Result teapot(Context context) {
		
		//this will never be executed. Have a look at the TeaPotFilter.class!
		return Results.html();
		
	}

}
