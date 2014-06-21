/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import com.google.common.base.Optional;
import java.io.InputStream;
import java.util.Properties;

import javax.management.RuntimeErrorException;

import ninja.lifecycle.LifecycleService;

import com.google.inject.Inject;
import ninja.exceptions.BadRequestException;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.Message;

import ninja.utils.NinjaConstant;
import ninja.utils.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class NinjaDefault implements Ninja {
    
    private static final Logger logger = LoggerFactory.getLogger(NinjaDefault.class);

    /**
     * The most important thing: A cool logo.
     */
    private final String NINJA_LOGO = "\n"
            + " _______  .___ _______        ____.  _____   \n"
            + " \\      \\ |   |\\      \\      |    | /  _  \\  \n"
            + " /   |   \\|   |/   |   \\     |    |/  /_\\  \\ \n"
            + "/    |    \\   /    |    \\/\\__|    /    |    \\  http://www.ninjaframework.org\n"
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


    @Override
    public void onRouteRequest(Context.Impl context) {
        
        String httpMethod = context.getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            try {
                
                Result result = route.getFilterChain().next(context);

                resultHandler.handleResult(result, context);
                
            } catch (BadRequestException badRequestException) {
                
                onBadRequest(context, badRequestException);
                            
            } catch (Exception exception) {
                
                // Exception inlcudes InternalServerErrorException
                onError(context, exception);
                            
            }

        } else {
            // throw a 404 "not found" because we did not find the route
            onNotFound(context);

        }
    

        
    }
    
    @Override
    public void onError(Context context, Exception exception) {
            
        logger.error(
                "Emitting bad request 500. Something really wrong when calling route: {} (class: {} method: {})",
                context.getRequestPath(), 
                context.getRoute().getControllerClass(), 
                context.getRoute().getControllerMethod(), 
                exception);
        
        String messageI18n 
                = messages.getWithDefault(
                        NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_KEY,
                        NinjaConstant.I18N_NINJA_SYSTEM_INTERNAL_SERVER_ERROR_TEXT_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n);

        Result result = Results
                .internalServerError()
                .render(message)
                .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);

        handleRenderableAndCatchAndLogExceptions(result, context);

    }
    
    @Override
    public void onNotFound(Context context) {
            
        String messageI18n 
                = messages.getWithDefault(
                        NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_KEY,
                        NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n); 
        
        Result result = Results
                        .notFound()
                        .render(message)
                        .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);
        
        
        handleRenderableAndCatchAndLogExceptions(result, context);

    }
    
    @Override
    public void onBadRequest(Context context, Exception exception) {
        
        String messageI18n 
                = messages.getWithDefault(
                        NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_KEY,
                        NinjaConstant.I18N_NINJA_SYSTEM_BAD_REQUEST_TEXT_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n); 
           
        Result result = Results
                        .badRequest()
                        .render(message)
                        .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST);
        
        handleRenderableAndCatchAndLogExceptions(result, context);

    }
    

    @Override
    public void onFrameworkStart() {

        showSplashScreenViaLogger();
                
        lifecycleService.start();
    }

    @Override
    public void onFrameworkShutdown() {
        lifecycleService.stop();
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
         
        try {

            Properties prop = new Properties();
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(LOCATION_OF_NINJA_BUILTIN_PROPERTIES);
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
        
        // log Ninja splash screen
        logger.info(NINJA_LOGO, ninjaVersion);
        
    }
    
    private void handleRenderableAndCatchAndLogExceptions(
            Result result, Context context) {
    
        try {
            resultHandler.handleResult(result, context);
        } catch (Exception exceptionCausingRenderError) {
            logger.error("Unable to handle result. "
                    + "That's really realy fishy. "
                    + "Original stack trace: {} ... "
                    + "Stack trace causing this error: {}", 
                    exceptionCausingRenderError);
        }
    }

}
