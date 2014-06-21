/*
 * Copyright 2014 ra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja;

import ninja.exceptions.BadRequestException;
import ninja.exceptions.InternalServerErrorException;
import ninja.lifecycle.LifecycleService;
import ninja.utils.NinjaConstant;
import ninja.utils.ResultHandler;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hibernate.classic.Lifecycle;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NinjaDefaultTest {

    @Mock
    LifecycleService lifecylceService;
    
    @Mock
    ResultHandler resultHandler;
    
    @Mock
    Router router;
    
    @Mock
    Context.Impl contextImpl;
    
    Route route;
    
    @Captor
    ArgumentCaptor<Result> resultCaptor;
    
    NinjaDefault ninjaDefault;
            
            
    @Before
    public void before() {
        
        ninjaDefault = Mockito.spy(new NinjaDefault());
        ninjaDefault.lifecycleService = lifecylceService;
        ninjaDefault.resultHandler = resultHandler;
        ninjaDefault.router = router;
        
        // Just a dummy to make logging work without
        // Null pointer exceptions.
        route = Mockito.mock(Route.class);
        
        Mockito.when(contextImpl.getRequestPath()).thenReturn("/path");
        Mockito.when(contextImpl.getRoute()).thenReturn(route);
        
       
        Mockito.when(contextImpl.getMethod()).thenReturn("httpMethod");
        Mockito.when(contextImpl.getRequestPath()).thenReturn("requestPath");
        Mockito.when(router.getRouteFor(Matchers.eq("httpMethod"), Matchers.eq("requestPath"))).thenReturn(route);


    }
    
    @Test
    public void testOnRouteRequestWhenEverythingWorks() throws Exception {
        
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        
        Result result = Mockito.mock(Result.class);
        Mockito.when(filterChain.next(contextImpl)).thenReturn(result);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(contextImpl).setRoute(route);
        verify(resultHandler).handleResult(result, contextImpl);
    
        verify(ninjaDefault, Mockito.never()).onError(any(Context.class), any(Exception.class));
        verify(ninjaDefault, Mockito.never()).onNotFound(any(Context.class));
    }
    
    @Test
    public void testOnRouteRequestWhenException() throws Exception {
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        Exception exception 
                = new RuntimeException("That's a very generic exception that should be handled by onError!");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(exception);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).onError(contextImpl, exception);
    
    }
    
        @Test
    public void testOnRouteRequestWhenInternalServerErrorException() throws Exception {
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        InternalServerErrorException internalServerErrorException 
                = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).onError(contextImpl, internalServerErrorException);
    
    }
    
    @Test
    public void testOnRouteRequestWhenOnBadRequest() throws Exception {
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        BadRequestException badRequest 
                = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).onBadRequest(contextImpl, badRequest);
    
    }
    
    @Test
    public void testOnRouteRequestWhenOnNotFound() throws Exception {
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
        

        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(
                router.getRouteFor(
                        Matchers.anyString(), 
                        Matchers.anyString()))
                .thenReturn(null);
                 
                 
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).onNotFound(contextImpl);

    
    }
    

    @Test
    public void testOnError() throws Exception {
        
        // real test:
        ninjaDefault.onError(
                contextImpl,
                new Exception("not important"));
        
        // and verify that everything is ok
        verify(resultHandler).handleResult(
                resultCaptor.capture(), 
                any(Context.class));
        
        assertThat(resultCaptor.getValue().getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));
        assertThat(resultCaptor.getValue().getContentType(), equalTo(Result.TEXT_HTML));
        assertThat(resultCaptor.getValue().getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

    }
    
    @Test
    public void testNotFound() throws Exception {
        
        ninjaDefault.onNotFound(contextImpl);
        
        verify(resultHandler).handleResult(
                resultCaptor.capture(), 
                any(Context.class));
        
        assertThat(resultCaptor.getValue().getStatusCode(), equalTo(Result.SC_404_NOT_FOUND));
        assertThat(resultCaptor.getValue().getContentType(), equalTo(Result.TEXT_HTML));
        assertThat(resultCaptor.getValue().getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));

    }
    
    @Test
    public void testOnFrameworkStart() {
        
        ninjaDefault.onFrameworkStart();
        
        verify(lifecylceService).start();
    
    }
    
    @Test
    public void testOnFrameworkShutdown() {
        
        ninjaDefault.onFrameworkShutdown();
        
        verify(lifecylceService).stop();
    
    }
    
}
