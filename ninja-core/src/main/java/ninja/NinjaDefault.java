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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.management.RuntimeErrorException;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import ninja.diagnostics.DiagnosticError;
import ninja.diagnostics.DiagnosticErrorBuilder;
import ninja.exceptions.BadRequestException;
import ninja.exceptions.ForbiddenRequestException;
import ninja.exceptions.RenderingException;
import ninja.exceptions.RequestNotFoundException;
import ninja.i18n.Messages;
import ninja.lifecycle.LifecycleService;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class NinjaDefault implements Ninja {
    private static final Logger logger = LoggerFactory.getLogger(NinjaDefault.class);

    /**
     * The most important thing: A cool logo.
     */
    private final String NINJA_LOGO = "\n"
            + " _______  .___ _______        ____.  _____   \n"
            + " \\      \\ |   |\\      \\      |    | /  _  \\  \n"
            + " /   |   \\|   |/   |   \\     |    |/  /_\\  \\ \n"
            + "/    |    \\   /    |    \\/\\__|    /    |    \\  https://www.ninjaframework.org\n"
            + "\\____|__  /___\\____|__  /\\________\\____|__  /  @ninjaframework\n"
            + "     web\\/framework   \\/                  \\/   {}\n";
    

    @Inject
    protected LifecycleService lifecycleService;
    
    @Inject
    protected Router router;
    
    @Inject
    protected ResultHandler resultHandler;
    
    @Inject
    Messages messages;
    
    @Inject
    NinjaProperties ninjaProperties;
    
    
    /**
     * Whether diagnostics are enabled. If enabled then the default system/views
     * will be skipped and a detailed diagnostic error result will be returned
     * by the various methods in this class. You get precise feedback where
     * an error occurred including original source code.
     * 
     * @return True if diagnostics are enabled otherwise false.
     */
    public boolean isDiagnosticsEnabled() {
        // extra safety: only disable detailed diagnostic error pages
        // if both in DEV mode and diagnostics are enabled 0
        return ninjaProperties.isDev() && ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE);
    }

    
    @Override
    public void onRouteRequest(Context.Impl context) {
        
        String httpMethod = context.getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            Result underlyingResult = null;
            
            try {
                
                underlyingResult = route.getFilterChain().next(context);
                
                resultHandler.handleResult(underlyingResult, context);
                
            } catch (Exception exception) {
                
                // call special handler to capture the underlying result if there is one
                Result result = onException(context, exception, underlyingResult);
                renderErrorResultAndCatchAndLogExceptions(result, context);
                            
            } finally {
                
                context.cleanup();
                
            }

        } else {
            
            // throw a 404 "not found" because we did not find the route
            Result result = getNotFoundResult(context);
            renderErrorResultAndCatchAndLogExceptions(result, context);

        }
        
    }
    
    @Override
    public void renderErrorResultAndCatchAndLogExceptions(
            Result result, Context context) {
    
        try {
            if (context.isAsync()) {
                context.returnResultAsync(result);
            } else {
                resultHandler.handleResult(result, context);
            }
        } catch (Exception exceptionCausingRenderError) {
            logger.error("Unable to handle result. That's really really fishy.",
                    exceptionCausingRenderError);
        }
    }
    
    @Override
    public void onFrameworkStart() {
        if (ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_SPLASH_DISPLAY, true)) {
            showSplashScreenViaLogger();
        }
        lifecycleService.start();
    }

    @Override
    public void onFrameworkShutdown() {
        lifecycleService.stop();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Results for exceptions (404, 500 etc)
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public Result onException(Context context, Exception exception) {
        
        return onException(context, exception, null);
        
    }
    
    public Result onException(Context context, Exception exception, Result underlyingResult) {
        
        Result result;
        
        // log the exception as debug
        logger.debug("Unable to process request", exception);
        
        if (exception instanceof BadRequestException) {
            
            result = getBadRequestResult(context, (BadRequestException) exception);

        } else if (exception instanceof ForbiddenRequestException) {

            result = getForbiddenResult(context, (ForbiddenRequestException) exception);
            
        } else if (exception instanceof RequestNotFoundException) {

            result = getNotFoundResult(context, (RequestNotFoundException) exception);

        } else if (exception instanceof RenderingException) {
            
            result = getRenderingExceptionResult(context, (RenderingException) exception, underlyingResult);
            
        } else {
            
            result = getInternalServerErrorResult(context, exception, underlyingResult);

        }
        
        return result;
        
    }
    
    public Result getRenderingExceptionResult(Context context, RenderingException exception, Result underlyingResult) {
        
        if (isDiagnosticsEnabled()) {
            
            // prefer provided title and underlying cause
            DiagnosticError diagnosticError = DiagnosticErrorBuilder
                .buildDiagnosticError(
                    (exception.getTitle() == null ? "Rendering exception" : exception.getTitle()),
                    (exception.getCause() == null ? exception : exception.getCause()),
                    exception.getSourcePath(),
                    exception.getLineNumber(),
                    underlyingResult);

            return Results.internalServerError().render(diagnosticError);
            
        }
        
        return getInternalServerErrorResult(context, exception, underlyingResult);

    }
    
    /**
     * Deprecated. Check {@link Ninja#getInternalServerErrorResult(Context, Exception, Result)}.
     */
    @Deprecated
    public Result getInternalServerErrorResult(Context context, Exception exception) {
        return getInternalServerErrorResult(context, exception, null);
    }
    
    @Override
    public Result getInternalServerErrorResult(Context context, Exception exception, Result underlyingResult) {
        
        if (isDiagnosticsEnabled()) {
            
            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build500InternalServerErrorDiagnosticError(exception, true, underlyingResult);
            
            return Results.internalServerError().render(diagnosticError);
            
        }
        
        logger.error(
                "Emitting bad request 500. Something really wrong when calling route: {} (class: {} method: {})",
                context.getRequestPath(), 
                context.getRoute().getControllerClass(), 
                context.getRoute().getControllerMethod(), 
                exception);
        
        Message message = buildErrorMessage(
                context, 
                NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY, 
                NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT, 
                Optional.ofNullable(exception), 
                Optional.<Result>empty());

        Result result = Results
                .internalServerError()
                .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
                .fallbackContentType(Result.TEXT_HTML)
                .render(message)
                .template(
                        ninjaProperties.getWithDefault(
                                NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY,
                                NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));
        

        return result;
    }
    
    @Override
    public Result getNotFoundResult(Context context) {
        return getNotFoundResult(context, null);
    }
    
    @Override
    public Result getNotFoundResult(Context context, RequestNotFoundException exception) {
        
        if (isDiagnosticsEnabled()) {

            DiagnosticError diagnosticError =
                    exception != null
                    ? DiagnosticErrorBuilder.build404NotFoundDiagnosticError(exception, true)
                    : DiagnosticErrorBuilder.build404NotFoundDiagnosticError(true);
                    
            return Results.notFound().render(diagnosticError);
            
        }
        
        Message message = buildErrorMessage(
                context, 
                NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY, 
                NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT, 
                Optional.ofNullable(exception), 
                Optional.<Result>empty());
        
        Result result = Results
                        .notFound()
                        .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
                        .fallbackContentType(Result.TEXT_HTML)
                        .render(message)
                        .template(
                                ninjaProperties.getWithDefault(
                                        NinjaConstant.LOCATION_VIEW_HTML_NOT_FOUND_KEY,
                                        NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND));
          
        return result;

    }
    
    @Override
    public Result getBadRequestResult(Context context, BadRequestException exception) {
        
        if (isDiagnosticsEnabled()) {
            
            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build400BadRequestDiagnosticError(exception, true);
            
            return Results.badRequest().render(diagnosticError);
            
        }
        
        Message message = buildErrorMessage(
                context, 
                NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY, 
                NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT, 
                Optional.ofNullable(exception), 
                Optional.<Result>empty());
           
        Result result = Results
                        .badRequest()
                        .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
                        .fallbackContentType(Result.TEXT_HTML)
                        .render(message)
                        .template(
                                ninjaProperties.getWithDefault(
                                        NinjaConstant.LOCATION_VIEW_HTML_BAD_REQUEST_KEY,
                                        NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST));
        
        return result;

    }
    
    @Override
    public Result getUnauthorizedResult(Context context) {

        if (isDiagnosticsEnabled()) {
            
            DiagnosticError diagnosticError =
                DiagnosticErrorBuilder.build401UnauthorizedDiagnosticError();
            
            return Results.unauthorized().render(diagnosticError);
            
        }
        
        Message message = buildErrorMessage(
                context, 
                NinjaConstant.I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_KEY, 
                NinjaConstant.I18N_NINJA_SYSTEM_UNAUTHORIZED_REQUEST_TEXT_DEFAULT, 
                Optional.<Throwable>empty(), 
                Optional.<Result>empty());

        // WWW-Authenticate must be included per the spec
        // http://www.ietf.org/rfc/rfc2617.txt 3.2.1 The WWW-Authenticate Response Header
        Result result = Results
                        .unauthorized()
                        .addHeader(Result.WWW_AUTHENTICATE, "None")
                        .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
                        .fallbackContentType(Result.TEXT_HTML)
                        .render(message)
                        .template(
                                ninjaProperties.getWithDefault(
                                        NinjaConstant.LOCATION_VIEW_HTML_UNAUTHORIZED_KEY,
                                        NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED));

        return result;

    }
    
    @Override
    public Result getForbiddenResult(Context context) {
        return getForbiddenResult(context, null);
    }

    @Override
    public Result getForbiddenResult(Context context, ForbiddenRequestException exception) {
        
        // diagnostic mode
        if (isDiagnosticsEnabled()) {
            
            DiagnosticError diagnosticError =
                exception != null
                ? DiagnosticErrorBuilder.build403ForbiddenDiagnosticError(exception, true)
                : DiagnosticErrorBuilder.build403ForbiddenDiagnosticError();
            
            return Results.forbidden().render(diagnosticError);
            
        }
        
        Message message = buildErrorMessage(
                context, 
                NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_KEY, 
                NinjaConstant.I18N_NINJA_SYSTEM_FORBIDDEN_REQUEST_TEXT_DEFAULT, 
                Optional.ofNullable(exception), 
                Optional.<Result>empty());
           
        Result result = Results
                        .forbidden()
                        .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
                        .fallbackContentType(Result.TEXT_HTML)
                        .render(message)
                        .template(
                                ninjaProperties.getWithDefault(
                                        NinjaConstant.LOCATION_VIEW_HTML_FORBIDDEN_KEY,
                                        NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN));
        
        return result;

    }
    
    protected Message buildErrorMessage(Context context,
                                        String errorTextKey,
                                        String errorTextDefault,
                                        Optional<Throwable> exception,
                                        Optional<Result> underlyingResult) {

        String messageI18n 
                = messages.getWithDefault(
                        errorTextKey,
                        errorTextDefault,
                        context,
                        underlyingResult);

        String errorI18n 
                = !exception.isPresent()
                    ? null 
                    : messages.getWithDefault(
                        exception.get().getMessage(),
                        exception.get().getLocalizedMessage(),
                        context,
                        underlyingResult);
        
        return new Message(messageI18n, errorI18n);
    }
    
    /**
     * Simply reads a property resource file that contains the version of this
     * Ninja build. Helps to identify the Ninja version currently running.
     * 
     * @return The version of Ninja. Eg. "1.6-SNAPSHOT" while developing of "1.6" when released.
     */
    private final String readNinjaVersion() {
        
        // location of the properties file
        String LOCATION_OF_NINJA_BUILTIN_PROPERTIES = "ninja/ninja-builtin.properties";
        // and the key inside the properties file.
        String NINJA_VERSION_PROPERTY_KEY = "ninja.version";
        
        String ninjaVersion;
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(LOCATION_OF_NINJA_BUILTIN_PROPERTIES)){

            Properties prop = new Properties();
            prop.load(stream);
            
            ninjaVersion = prop.getProperty(NINJA_VERSION_PROPERTY_KEY);
        
        } catch (Exception e) {
            //this should not happen. Never.
            throw new RuntimeErrorException(new Error("Something is wrong with your build. Cannot find resource " + LOCATION_OF_NINJA_BUILTIN_PROPERTIES));
        }
        
        return ninjaVersion;
        
    }
    
    private final void showSplashScreenViaLogger() {
        
        String ninjaVersion = readNinjaVersion();

        // log Ninja splash screen, from resources if available (so it can be overridden)
        String NINJA_LOGO_LOCATION = "ninja/logo.txt";
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(NINJA_LOGO_LOCATION);) {
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is), Charsets.UTF_8);
            logger.info(CharStreams.toString(reader), ninjaVersion);
        } catch (IOException | NullPointerException e) {
            // if anything happens, use the old one
            logger.info(NINJA_LOGO, ninjaVersion);
        }
        
    }

}
