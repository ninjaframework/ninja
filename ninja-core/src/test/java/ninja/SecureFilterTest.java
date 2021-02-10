/**
 * Copyright (C) the original author or authors.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import ninja.session.Session;
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
    private Session sessionCookie;

    @Mock
    private FilterChain filterChain;
    
    @Mock
    Ninja ninja;

    @Mock
    Result result;
    
    SecureFilter secureFilter;
    
    

    @Before
    public void setup() {
        secureFilter = new SecureFilter(ninja);
        
        when(result.getTemplate()).thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN);
        when(ninja.getForbiddenResult(context)).thenReturn(result);

    }

    @Test
    public void testSecureFilter() {

        when(context.getSession()).thenReturn(null);

        // filter that
        secureFilter.filter(filterChain, context);

        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testSessionIsNotReturingWhenUserNameMissing() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn(null);

        // filter that
        Result result = secureFilter.filter(filterChain, context);

        assertEquals(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN, result.getTemplate());
        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {

        when(context.getSession()).thenReturn(sessionCookie);
        when(sessionCookie.get(SecureFilter.USERNAME)).thenReturn("myname");

        // filter that
        secureFilter.filter(filterChain, context);

        verify(filterChain).next(context);
    }

    @Test
    public void testThatUSERNAMEIsCorrect() {

        assertThat(SecureFilter.USERNAME, equalTo("username"));

    }

}
