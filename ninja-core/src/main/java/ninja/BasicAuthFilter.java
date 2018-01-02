/**
 * Copyright (C) 2012- the original author or authors.
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

import java.nio.charset.Charset;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.codec.binary.Base64;

import com.google.inject.Inject;

/**
 * A Ninja filter that implements HTTP BASIC Authentication.
 *
 * @author James Moger
 *
 */
public class BasicAuthFilter implements Filter {

    protected final Ninja ninja;

    protected final UsernamePasswordValidator credentialsValidator;

    protected final String challenge;

    protected final String realm;

    @Inject
    public BasicAuthFilter(Ninja ninja,
                           NinjaProperties ninjaProperties,
                           UsernamePasswordValidator validator) {
        this.ninja = ninja;
        this.credentialsValidator = validator;
        this.realm = ninjaProperties.getWithDefault(
                NinjaConstant.applicationName, "Ninja");
        this.challenge = "Basic realm=\"" + realm + "\"";
    }

    @Override
    public Result filter(FilterChain chain, Context context) {

        if (context.getSession() == null) {
            // no session
            return ninja.getUnauthorizedResult(context);

        } else if (context.getSession().get(SecureFilter.USERNAME) == null) {
            // no login, conditionally challenge
            String header = context.getHeader("Authorization");

            if (header != null && header.startsWith("Basic")) {
                // Authorization: Basic BASE64PACKET
                String packet = header.substring("Basic".length()).trim();
                String credentials = new String(Base64.decodeBase64(packet),
                        Charset.forName(NinjaConstant.UTF_8));

                // credentials = username:password
                final String[] values = credentials.split(":", 2);
                final String username = values[0];
                final String password = values[1];

                if (credentialsValidator
                        .validateCredentials(username, password)) {

                    context.getSession().put(SecureFilter.USERNAME, username);

                    return chain.next(context);
                }
            }

            Result result = ninja.getUnauthorizedResult(context).addHeader(
                    Result.WWW_AUTHENTICATE, challenge);
            return result;

        } else {
            return chain.next(context);
        }
    }
}
