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

import ninja.diagnostics.DiagnosticError;
import ninja.exceptions.BadRequestException;
import ninja.exceptions.ForbiddenRequestException;
import ninja.exceptions.InternalServerErrorException;
import ninja.exceptions.NinjaException;
import ninja.exceptions.RequestNotFoundException;
import ninja.i18n.Messages;
import ninja.lifecycle.LifecycleService;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NinjaDefaultTest {

    @Mock
    private LifecycleService lifecylceService;

    @Mock
    private ResultHandler resultHandler;

    @Mock
    private Router router;

    @Mock
    private Context.Impl contextImpl;

    @Mock
    private Messages messages;

    @Mock
    private Result result;

    @Mock
    private NinjaProperties ninjaProperties;

    private Route route;

    @Captor
    private ArgumentCaptor<Result> resultCaptor;

    private NinjaDefault ninjaDefault;

    @Before
    public final void before() {

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
        Mockito.when(router.getRouteFor(ArgumentMatchers.eq("httpMethod"), ArgumentMatchers.eq("requestPath"))).thenReturn(route);

    }

    @Test
    public void testOnRouteRequestWhenEverythingWorks() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        Result result = Mockito.mock(Result.class);
        Mockito.when(filterChain.next(contextImpl)).thenReturn(result);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(contextImpl).setRoute(route);
        verify(resultHandler).handleResult(result, contextImpl);

        verify(ninjaDefault, Mockito.never()).getInternalServerErrorResult(any(Context.class), any(Exception.class), any(Result.class));
        verify(ninjaDefault, Mockito.never()).getBadRequestResult(any(Context.class), any(BadRequestException.class));
        verify(ninjaDefault, Mockito.never()).getForbiddenResult(any(Context.class), any(ForbiddenRequestException.class));
        verify(ninjaDefault, Mockito.never()).getNotFoundResult(any(Context.class), any(RequestNotFoundException.class));
        verify(ninjaDefault, Mockito.never()).getNotFoundResult(any(Context.class));
    }

    @Test
    public void testOnRouteRequestWhenException() {

        Exception exception
                = new RuntimeException("That's a very generic exception that should be handled by onError!");

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        Mockito.when(filterChain.next(contextImpl)).thenThrow(exception);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, exception, null);

    }

    @Test
    public void testOnRouteRequestWhenInternalServerErrorException() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        InternalServerErrorException internalServerErrorException
                = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, internalServerErrorException, null);

    }

    @Test
    public void testOnRouteRequestWhenInternalServerErrorExceptionInDiagnosticMode() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        InternalServerErrorException internalServerErrorException
                = new InternalServerErrorException("That's an InternalServerErrorException that should be handled by onError!");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(internalServerErrorException);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);

        ninjaDefault.onRouteRequest(contextImpl);

        Result localResult = ninjaDefault.getInternalServerErrorResult(contextImpl, internalServerErrorException, null);

        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnBadRequest() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        BadRequestException badRequest
                = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getBadRequestResult(contextImpl, badRequest);

    }

    @Test
    public void testOnRouteRequestWhenOnBadRequestInDiagnosticMode() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        BadRequestException badRequest
                = new BadRequestException("That's a BadRequest that should be handled by onBadRequest");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(badRequest);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);

        ninjaDefault.onRouteRequest(contextImpl);

        Result localResult = ninjaDefault.getBadRequestResult(contextImpl, badRequest);

        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenForbiddenRequest() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        ForbiddenRequestException forbiddenRequest
                = new ForbiddenRequestException("That's a ForbiddenRequest that should be handled by onForbiddenRequest");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(forbiddenRequest);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getForbiddenResult(contextImpl, forbiddenRequest);

    }

    @Test
    public void testOnRouteRequestWhenForbiddenRequestInDiagnosticMode() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        ForbiddenRequestException forbiddenRequest
                = new ForbiddenRequestException("That's a ForbiddenRequest that should be handled by onForbiddenRequest");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(forbiddenRequest);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);

        ninjaDefault.onRouteRequest(contextImpl);

        Result localResult = ninjaDefault.getForbiddenResult(contextImpl, forbiddenRequest);

        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnNotFound() {

        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(
                router.getRouteFor(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()))
                .thenReturn(null);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getNotFoundResult(contextImpl);

    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundInDiagnosticMode() {

        // This simulates that a route has not been found
        // subsequently the onNotFound method should be called.
        Mockito.when(
                router.getRouteFor(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()))
                .thenReturn(null);

        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);

        ninjaDefault.onRouteRequest(contextImpl);

        Result localResult = ninjaDefault.getNotFoundResult(contextImpl);

        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundException() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        RequestNotFoundException notFoundRequest
                = new RequestNotFoundException("That's a RequestNotFoundException that should be handled by onNotFound");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(notFoundRequest);

        ninjaDefault.onRouteRequest(contextImpl);

        verify(ninjaDefault).getNotFoundResult(contextImpl, notFoundRequest);

    }

    @Test
    public void testOnRouteRequestWhenOnNotFoundExceptionInDiagnosticMode() {

        FilterChain filterChain = Mockito.mock(FilterChain.class);
        Mockito.when(route.getFilterChain()).thenReturn(filterChain);

        RequestNotFoundException notFoundRequest
                = new RequestNotFoundException("That's a RequestNotFoundException that should be handled by onNotFound");

        Mockito.when(filterChain.next(contextImpl)).thenThrow(notFoundRequest);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, true)).thenReturn(true);

        ninjaDefault.onRouteRequest(contextImpl);

        Result localResult = ninjaDefault.getNotFoundResult(contextImpl, notFoundRequest);

        assertThat(localResult.getRenderable(), CoreMatchers.instanceOf(DiagnosticError.class));
    }

    @Test
    public void testOnExceptionBadRequest() {

        BadRequestException badRequestException = new BadRequestException();

        Result result = ninjaDefault.onException(contextImpl, badRequestException);

        verify(ninjaDefault).getBadRequestResult(contextImpl, badRequestException);
        assertThat(result.getStatusCode(), equalTo(Result.SC_400_BAD_REQUEST));

    }

    @Test
    public void testOnExceptionForbiddenRequest() {

        ForbiddenRequestException forbiddenRequestException = new ForbiddenRequestException();

        Result result = ninjaDefault.onException(contextImpl, forbiddenRequestException);

        verify(ninjaDefault).getForbiddenResult(contextImpl, forbiddenRequestException);
        assertThat(result.getStatusCode(), equalTo(Result.SC_403_FORBIDDEN));

    }

    @Test
    public void testOnExceptionRequestNotFound() {

        RequestNotFoundException notFoundRequestException = new RequestNotFoundException();

        Result result = ninjaDefault.onException(contextImpl, notFoundRequestException);

        verify(ninjaDefault).getNotFoundResult(contextImpl, notFoundRequestException);
        assertThat(result.getStatusCode(), equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testOnExceptionCatchAll() {

        Exception anyException = new Exception();

        Result result = ninjaDefault.onException(contextImpl, anyException);

        verify(ninjaDefault).getInternalServerErrorResult(contextImpl, anyException, null);
        assertThat(result.getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testThatGetInternalServerErrorContentNegotiation() {
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"), null);
        assertThat(result.getContentType(), equalTo(null));
        assertThat(result.supportedContentTypes().size(), equalTo(3));

    }

    @Test
    public void testThatGetInternalServerErrorDoesFallsBackToHtml() {
        Result result = ninjaDefault.getInternalServerErrorResult(contextImpl, new Exception("not important"), null);
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void getInternalServerErrorResultCustomException() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);

        Exception exception = new Exception("not important");

        // real test:
        Result result = ninjaDefault.getInternalServerErrorResult(
                contextImpl,
                exception, null);

        assertThat(result.getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

    }

    @Test
    public void getInternalServerErrorResultWithNinjaException() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);

        Exception exception = new NinjaException(418, "I'm a teapot") {{
        }};

        // real test:
        Result result = ninjaDefault.getInternalServerErrorResult(
                contextImpl,
                exception, null);

        assertThat(result.getStatusCode(), equalTo(Result.SC_500_INTERNAL_SERVER_ERROR));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(exception.getMessage()),
                ArgumentMatchers.eq(exception.getLocalizedMessage()),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

    }

    @Test
    public void testThatGetBadRequestContentNegotiation() {
        Result result = ninjaDefault.getBadRequestResult(contextImpl, new BadRequestException("not important"));
        assertThat(result.getContentType(), equalTo(null));
        assertThat(result.supportedContentTypes().size(), equalTo(3));

    }

    @Test
    public void testThatGetBadRequestDoesFallsBackToHtml() {
        Result result = ninjaDefault.getBadRequestResult(contextImpl, new BadRequestException("not important"));
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void testGetBadRequest() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST);

        BadRequestException exception = new BadRequestException("not important");

        // real test:
        Result result = ninjaDefault.getBadRequestResult(
                contextImpl,
                exception);

        assertThat(result.getStatusCode(), equalTo(Result.SC_400_BAD_REQUEST));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(exception.getMessage()),
                ArgumentMatchers.eq(exception.getLocalizedMessage()),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST));

    }

    @Test
    public void testThatGetForbiddenRequestContentNegotiation() {
        Result result = ninjaDefault.getForbiddenResult(contextImpl, new ForbiddenRequestException("not important"));
        assertThat(result.getContentType(), equalTo(null));
        assertThat(result.supportedContentTypes().size(), equalTo(3));

    }

    @Test
    public void testThatGetForbiddenRequestDoesFallsBackToHtml() {
        Result result = ninjaDefault.getForbiddenResult(contextImpl, new ForbiddenRequestException("not important"));
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void testGetForbiddenRequest() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_FORBIDDEN_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN);

        ForbiddenRequestException exception = new ForbiddenRequestException("not important");

        // real test:
        Result result = ninjaDefault.getForbiddenResult(
                contextImpl,
                exception);

        assertThat(result.getStatusCode(), equalTo(Result.SC_403_FORBIDDEN));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(exception.getMessage()),
                ArgumentMatchers.eq(exception.getLocalizedMessage()),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_FORBIDDEN_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN));

    }

    @Test
    public void testThatGetOnNotFoundDoesContentNegotiation() {
        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        assertThat(result.getContentType(), equalTo(null));
        assertThat(result.supportedContentTypes().size(), equalTo(3));

    }

    @Test
    public void testThatGetOnNotFoundDoesFallsBackToHtml() {
        Result result = ninjaDefault.getNotFoundResult(contextImpl);
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void testGetOnNotFoundResultWorks() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);

        Result result = ninjaDefault.getNotFoundResult(contextImpl);

        assertThat(result.getStatusCode(), equalTo(Result.SC_404_NOT_FOUND));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));

    }

    @Test
    public void testThatGetOnNotFoundExceptionDoesContentNegotiation() {
        Result result = ninjaDefault.getNotFoundResult(contextImpl, new RequestNotFoundException("not important"));
        assertThat(result.getContentType(), equalTo(null));
        assertThat(result.supportedContentTypes().size(), equalTo(3));

    }

    @Test
    public void testThatGetOnNotFoundExceptionDoesFallsBackToHtml() {
        Result result = ninjaDefault.getNotFoundResult(contextImpl, new RequestNotFoundException("not important"));
        assertThat(result.fallbackContentType().get(), equalTo(Result.TEXT_HTML));
    }

    @Test
    public void testGetOnNotFoundExceptionResultWorks() {

        when(ninjaProperties.getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND)))
                .thenReturn(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);

        RequestNotFoundException exception = new RequestNotFoundException("not important");

        // real test:
        Result result = ninjaDefault.getNotFoundResult(
                contextImpl,
                exception);

        assertThat(result.getStatusCode(), equalTo(Result.SC_404_NOT_FOUND));
        assertThat(result.getTemplate(), equalTo(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));
        assertTrue(result.getRenderable() instanceof Message);

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY),
                ArgumentMatchers.eq(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(messages).getWithDefault(
                ArgumentMatchers.eq(exception.getMessage()),
                ArgumentMatchers.eq(exception.getLocalizedMessage()),
                ArgumentMatchers.eq(contextImpl),
                any(Optional.class));

        verify(ninjaProperties).getWithDefault(
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY),
                ArgumentMatchers.eq(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));

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
