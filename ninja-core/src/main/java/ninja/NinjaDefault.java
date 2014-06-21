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

import java.io.InputStream;
import java.util.Properties;

import javax.management.RuntimeErrorException;

import ninja.lifecycle.LifecycleService;

import com.google.inject.Inject;
import ninja.exceptions.BadRequestException;
import ninja.exceptions.InternalServerErrorException;

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

        Result result = Results
                .html()
                .status(Result.SC_500_INTERNAL_SERVER_ERROR)
                .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR);


        handleRenderableAndCatchAndLogExceptions(result, context);


    }
    
    @Override
    public void onNotFound(Context context) {
            
        Result result = Results
                        .html()
                        .status(Result.SC_404_NOT_FOUND)
                        .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);
        
        
        handleRenderableAndCatchAndLogExceptions(result, context);

    }
    
    @Override
    public void onBadRequest(Context context, Exception exception) {
            
        Result result = Results
                        .html()
                        .status(Result.SC_400_BAD_REQUEST)
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
    
    // Simple tool to render an error message if request accepts only json
    // or xml.
    public static class MessagePojo {
        
        public String message;
   
        public MessagePojo(String message) {
            this.message = message;
        }
    
    }

}
