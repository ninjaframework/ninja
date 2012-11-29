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

package ninja;

import ninja.utils.NinjaConstant;

/**
 * A simple default implementation of a SecureFilter.
 * 
 * If you annotate your methods using that filter it will check if a variable
 * called "username" is saved in the cookie.
 * 
 * If yes it will continue the execution. If not it will break.
 * 
 * 
 * NinjaFilter are really simple. If this one does not suit your needs modify it
 * for your project :)
 * 
 * 
 * @author rbauer
 * 
 */
public class SecureFilter implements Filter {

    /** If a username is saved we assume the session is valid */
    public final String USERNAME = "username";

    @Override
    public Result filter(FilterChain chain, Context context) {

        // if we got no cookies we break:
        if (context.getSessionCookie() == null
                || context.getSessionCookie().get(USERNAME) == null) {

            return Results.forbidden().html()
                    .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN);

        } else {
            return chain.next(context);
        }

    }
}
