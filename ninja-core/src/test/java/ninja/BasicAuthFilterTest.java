/**
 * Copyright (C) 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;

import ninja.session.Session;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthFilterTest {

    @Mock
    private NinjaProperties ninjaProperties;

    @Mock
    private Context context;

    @Mock
    private Session sessionCookie;

    @Mock
    private FilterChain filterChain;

    @Mock
    Ninja ninja;

    @Mock
    Result result;

    BasicAuthFilter basicAuthFilter;

    @Before
    public void setup() {
        String app = "Ninja";
        String challenge = "Basic realm=\"" + app + "\"";

        when(ninjaProperties.getWithDefault(NinjaConstant.applicationName, app))
                .thenReturn(app);

        basicAuthFilter = new BasicAuthFilter(ninja, ninjaProperties,
                new UsernamePasswordValidator() {

                    @Override
                    public boolean validateCredentials(String username,
                                                       String password) {
                        return "james".equals(username)
                                && "bond".equals(password);
                    }
                });

        when(ninja.getUnauthorizedResult(context)).thenReturn(result);
        when(result.status(Result.SC_401_UNAUTHORIZED)).thenReturn(result);
        when(result.addHeader(Result.WWW_AUTHENTICATE, challenge)).thenReturn(
                result);

    }

    @Test
    public void testNullSession() {

        when(context.getSession()).thenReturn(null);

        // filter that
        basicAuthFilter.filter(filterChain, context);

        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testUnauthenticatedSession() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn(null);
        when(result.getTemplate()).thenReturn(
                NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED);

        // filter that
        Result result = basicAuthFilter.filter(filterChain, context);

        assertEquals(NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED,
                result.getTemplate());
        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn("myname");

        // filter that
        basicAuthFilter.filter(filterChain, context);

        verify(filterChain).next(context);
    }

    @Test
    public void testSessionIsNotReturingWithInvalidCredentials() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn(null);
        when(result.getTemplate()).thenReturn(
                NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED);
        when(context.getHeader("Authorization")).thenReturn(
                auth("test", "user"));

        // filter that
        Result result = basicAuthFilter.filter(filterChain, context);

        assertEquals(NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED,
                result.getTemplate());
        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testSessionIsReturningWithValidCredentials() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn(null);
        when(context.getHeader("Authorization")).thenReturn(
                auth("james", "bond"));

        // filter that
        basicAuthFilter.filter(filterChain, context);

        verify(filterChain).next(context);
    }

    private String auth(String username, String password) {
        return "Basic "
                + Base64.encodeBase64String((username + ":" + password)
                        .getBytes(Charset.forName("UTF-8")));
    }

}
