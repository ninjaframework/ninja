/*
 * Copyright (C) 2012-2015 the original author or authors.
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


import static org.mockito.Matchers.any;
import ninja.diagnostics.DiagnosticError;

import ninja.exceptions.BadRequestException;
import ninja.exceptions.InternalServerErrorException;
import ninja.i18n.Messages;
import ninja.lifecycle.LifecycleService;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;

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
    
    @Mock
    Messages messages;
    
    @Mock
    Result result;
    
    @Mock
    NinjaProperties ninjaProperties;
    
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
        ninjaDefault.messages = messages;
        ninjaDefault.ninjaProperties = ninjaProperties;
        
        // Just a dummy to make logging work without
        // Null pointer exceptions.
        route = Mockito.mock(Route.class);
        
        Mockito.when(contextImpl.getRequestPath()).thenReturn("/path");
        Mockito.when(contextImpl.getRoute()).thenReturn(route);
        
       
        Mockito.when(contextImpl.getMethod()).thenReturn("httpMethod");
        Mockito.when(contextImpl.getRequestPath()).thenReturn("requestPath");
        Mockito.when(router.getRouteFor(Matchers.eq("httpMethod"), Matchers.eq("requestPath"))).thenReturn(route);

        // just a default answer so we don't get a nullpointer badRequestException.
        // can be verified later...
        Mockito.when(
                messages.getWithDefault(
                        Matchers.anyString(), 
                        Matchers.anyString(), 
                        any(Optional.class)))
                .thenReturn("NOT_IMPORTANT_MESSAGE");
                
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

        verify(ninjaDefault, Mockito.never()).getInternalServerErrorResult(any(Context.class), any(Exception.class));
        verify(ninjaDefault, Mockito.never()).getBadRequestResult(any(Context.class), any(Exception.class));
        verify(ninjaDefault, Mockito.never()).getNotFoundResult(any(Context.class));
    }
    
    @Test
    public void testOnRouteRequestWhenException() throws Exception {
        
        Mockito.when(
                messages.getWithDefault(
                        Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY), 
                        Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT), 
                        any(Optional.class)))
                .thenReturn(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT);
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        Exception exception 
                = new RuntimeException("That's a very generic exception that should be handled by onError!");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(exception);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, exception, null);
    
    }
    
    @Test
    public void testOnRouteRequestWhenInternalServerErrorException() throws Exception {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        InternalServerErrorException internalServerErrorException 
                = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, internalServerErrorException, null);
    
    }
    
    @Test
    public void testOnRouteRequestWhenInternalServerErrorExceptionInDiagnosticMode() throws Exception {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        InternalServerErrorException internalServerErrorException 
                = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        Result localResult = ninjaDefault.getInternalServerErrorResult(contextImpl, internalServerErrorException);
        
        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }
    
    @Test
    public void testOnRouteRequestWhenOnBadRequest() throws Exception {
    
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        BadRequestException badRequest 
                = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        verify(ninjaDefault).getBadRequestResult(contextImpl, badRequest);
    
    }
    
    @Test
    public void testOnRouteRequestWhenOnBadRequestInDiagnosticMode() throws Exception {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);
          
        BadRequestException badRequest 
                = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");;
        
        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        Result localResult = ninjaDefault.getBadRequestResult(contextImpl, badRequest);
        
        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
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
        
        verify(ninjaDefault).getNotFoundResult(contextImpl);

    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundInDiagnosticMode() throws Exception {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(
                router.getRouteFor(
                        Matchers.anyString(), 
                        Matchers.anyString()))
                .thenReturn(null);
                 
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);
        
        ninjaDefault.onRouteRequest(contextImpl);
        
        Result localResult = ninjaDefault.getNotFoundResult(contextImpl);
        
        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }
    
    @Test
    public void testOnExceptionBadRequest() {
        
        Exception badRequestException = new BadRequestException();
    
        Result result = ninjaDefault.onException(contextImpl, badRequestException);
        
        verify(ninjaDefault).getBadRequestResult(contextImpl, badRequestException);
        assertThat(result.getStatusCode(), equalTo(Result.SC_400_BAD_REQUEST));
    
    }
    
    @Test
    public void testOnExceptionCatchAll() {

        Exception anyException = new Exception();
    
        Result result = ninjaDefault.onException(contextImpl, anyException);
        
        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, anyException, null);
        assertThat(result.getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));

    }
    
     
    @Test
    public void testThatGetInternalServerErrorContentNegotiation() throws Exception {
       Mockito.when(contextImpl.getAcceptContentType()).thenReturn(Result.APPLICATION_JSON);
       Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"));
       assertThat(result.getContentType(), equalTo(null));
       assertThat(result.supportedContentTypes().size(), equalTo(3));

    }
        
    @Test
    public void testThatGetInternalServerErrorDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"));
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void getInternalServerErrorResult() throws Exception {
        
        when(ninjaProperties.getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);
        
        // real test:
        Result result = ninjaDefault.getInternalServerErrorResult(
                contextImpl,
                new Exception("not important"));
        
        assertThat(result.getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY), 
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT), 
            Matchers.eq(contextImpl),
            any(Optional.class));
        
        verify(ninjaProperties).getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
                
    }
    
    @Test
    public void testThatGetBadRequestContentNegotiation() throws Exception {
       Mockito.when(contextImpl.getAcceptContentType()).thenReturn(Result.APPLICATION_JSON);
       Result result = ninjaDefault.getBadRequestResult(contextImpl, new Exception("not important"));
       assertThat(result.getContentType(), equalTo(null));
       assertThat(result.supportedContentTypes().size(), equalTo(3));

    }
        
    @Test
    public void testThatGetBadRequestDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");
        Result result = ninjaDefault.getBadRequestResult(contextImpl, new Exception("not important"));
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }
    
    @Test
    public void testGetBadRequest() throws Exception {
        
        when(ninjaProperties.getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST);

        // real test:
        Result result = ninjaDefault.getBadRequestResult(
                contextImpl,
                new BadRequestException("not important"));
        
        assertThat(result.getStatusCode(), equalTo(Result.SC_400_BAD_REQUEST));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY), 
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT), 
            Matchers.eq(contextImpl),
            any(Optional.class));
        
        verify(ninjaProperties).getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
                
    }
    
    @Test
    public void testThatGetOnNotFoundDoesContentNegotiation() throws Exception {
       Mockito.when(contextImpl.getAcceptContentType()).thenReturn(Result.APPLICATION_JSON);
       Result result = ninjaDefault.getNotFoundResult(contextImpl);
       assertThat(result.getContentType(), equalTo(null));
       assertThat(result.supportedContentTypes().size(), equalTo(3));

    }
        
    @Test
    public void testThatGetOnNotFoundDoesFallsBackToHtml() throws Exception {
        Mockito.when(contextImpl.getAcceptContentType()).thenReturn("not_supported");

        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }
    
    @Test
    public void testGetOnNotFoundResultWorks() throws Exception {
        
        when(ninjaProperties.getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);

        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        
        assertThat(result.getStatusCode(), equalTo(Result.SC_404_NOT_FOUND));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));
        assertTrue(result.getRenderable() instanceof Message);
        
        verify(messages).getWithDefault(
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY), 
            Matchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT), 
            Matchers.eq(contextImpl),
            any(Optional.class));
        
        verify(ninjaProperties).getWithDefault(
                Matchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY), 
                Matchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));
                
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
    
    @Test
    public void testRenderErrorResultAndCatchAndLogExceptionsAsync() {
    
        Mockito.when(contextImpl.isAsync()).thenReturn(true);
        ninjaDefault.renderErrorResultAndCatchAndLogExceptions(result, contextImpl);
    
        verify(contextImpl).isAsync();
        verify(contextImpl).returnResultAsync(result);

    }
    
    @Test
    public void testRenderErrorResultAndCatchAndLogExceptionsSync() {
    
        Mockito.when(contextImpl.isAsync()).thenReturn(false);
        ninjaDefault.renderErrorResultAndCatchAndLogExceptions(result, contextImpl);
    
        verify(resultHandler).handleResult(result, contextImpl);

    }
    
    @Test
    public void testIsDiagnosticsEnabled_TrueInDevAndWithProperNinjaPropertiesConfig() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(true);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        
        assertThat(ninjaDefault.isDiagnosticsEnabled(), equalTo(true));
        verify(ninjaProperties).isDev();
        verify(ninjaProperties).getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }
    
    @Test
    public void testIsDiagnosticsEnabled_FalseDisabledWhenNotInDev() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(true);
        Mockito.when(ninjaProperties.isDev()).thenReturn(false);
        
        assertThat(ninjaDefault.isDiagnosticsEnabled(), equalTo(false));
        verify(ninjaProperties).isDev();
        verify(ninjaProperties, Mockito.never()).getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }
    
    @Test
    public void testIsDiagnosticsEnabled_FalseWheInDevButDisabledInNinjaPropertiesConfig() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(false);
        Mockito.when(ninjaProperties.isDev()).thenReturn(true);
        
        assertThat(ninjaDefault.isDiagnosticsEnabled(), equalTo(false));
        verify(ninjaProperties).isDev();
        verify(ninjaProperties).getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }
    
}
