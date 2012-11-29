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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import ninja.session.SessionCookie;
import ninja.utils.NinjaConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecureFilterTest {

    @Mock
    private Context context;
    
    @Mock
    private SessionCookie sessionCookie;

    @Mock 
    private FilterChain filterChain;
    
    @Mock 
    private Result result;

    SecureFilter secureFilter;

    @Before
    public void setup() {
        secureFilter = new SecureFilter();

    }

    @Test
    public void testSecureFilter() {

        when(context.getSessionCookie()).thenReturn(null);

        // filter that
        secureFilter.filter(filterChain, context);

        verifyZeroInteractions(filterChain);
    }
    
    @Test
    public void testSessionIsNotReturingWhenUserNameMissing() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn(null);
        
        // filter that
        Result result = secureFilter.filter(filterChain, context);
        
        assertEquals(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN, result.getTemplate());
        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn("myname");
        
        // filter that
        secureFilter.filter(filterChain, context);

        verify(filterChain).next(context);
    }

}
