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

import static org.mockito.Mockito.when;
import ninja.session.Session;
import ninja.utils.NinjaConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticityFilterTest {
    
    @Mock
    private Context context;
    
    @Mock
    private FilterChain filterChain;
    
    @Mock
    private Session session;
    
    @Mock
    private NinjaDefault ninjaDefault;
    
    private AuthenticityFilter authenticityFilter;

    @Before
    public void init() {
        authenticityFilter = new AuthenticityFilter(ninjaDefault);        
    }
    
    @Test
    public void testAuthenticityFail() {
        when(context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN)).thenReturn("foo");
        when(context.getSession()).thenReturn(session);
        when(context.getSession().getAuthenticityToken()).thenReturn("bar");
        this.authenticityFilter.filter(filterChain, context);
        Mockito.verify(ninjaDefault).getForbiddenResult(context);
    }
    
    @Test
    public void testAuthenticityWorks() {
        when(context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN)).thenReturn("foo");
        when(context.getSession()).thenReturn(session);
        when(context.getSession().getAuthenticityToken()).thenReturn("foo");
        this.authenticityFilter.filter(filterChain, context);
        Mockito.verify(filterChain).next(context);
    }
}