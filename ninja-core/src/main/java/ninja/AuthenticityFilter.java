/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author svenkubiak
 *
 */
public class AuthenticityFilter implements Filter {
    static private final Logger logger = LoggerFactory.getLogger(AuthenticityFilter.class);
    
    private final Ninja ninja;

    @Inject
    public AuthenticityFilter(Ninja ninja) {
        this.ninja = ninja;
    }
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        String authenticityToken = context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN);
        
        if (!context.getSession().getAuthenticityToken().equals(authenticityToken)) {
            logger.warn("Authenticity token mismatch. Request from {} is forbidden!", context.getRemoteAddr());
            return ninja.getForbiddenResult(context);
        }
        
        return filterChain.next(context);
    }
}