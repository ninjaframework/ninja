#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.session.Session;

public class RememberMeFilter implements Filter {
    public Result filter(FilterChain chain, Context context) {
        Result result = chain.next(context);

        Session session = context.getSession();

        // Only extend if we previously saved the value 'rememberMe' to the session
        if (session.get("rememberMe") != null) {
            // Set the expiry time 30 days (in milliseconds) in the future
            session.setExpiryTime(30 * 24 * 60 * 60 * 1000L);
        }

        return result;
    }
}