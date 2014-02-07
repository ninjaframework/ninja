/**
 * Copyright (C) 2013 the original author or authors.
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

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main implementation of the ninja framework.
 * 
 * Roughly works in the following order:
 * 
 * - Gets a request
 * 
 * - Searches for a matching route
 * 
 * - Applies filters
 * 
 * - Executes matching controller
 * 
 * - Returns result
 * 
 * @author ra
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class NinjaImpl implements Ninja {
    
    private static final Logger logger = LoggerFactory.getLogger(NinjaImpl.class);

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
    

    private final LifecycleService lifecycleService;
    private final NinjaProperties ninjaProperties;
    private final Router router;
    private final ResultHandler resultHandler;

    @Inject
    public NinjaImpl(LifecycleService lifecycleService,
                     NinjaProperties ninjaProperties,
                     Router router,
                     ResultHandler resultHandler) {

        this.router = router;
        this.ninjaProperties = ninjaProperties;
        this.lifecycleService = lifecycleService;
        this.resultHandler = resultHandler;
        
        String ninjaVersion = readNinjaVersion();

        // log Ninja splash screen
        logger.info(NINJA_LOGO, ninjaVersion);
      
        logNinjaMode(logger, ninjaProperties);
        
    }

    /**
     * I do all the main work.
     * 
     * @param context
     *            context
     */
    public void invoke(Context.Impl context) {

        String httpMethod = context.getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            Result result = route.getFilterChain().next(context);

            resultHandler.handleResult(result, context);

        } else {
            // throw a 404 "not found" because we did not find the route

            Result result = Results.html().status(Result.SC_404_NOT_FOUND).template(
                    NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);

            resultHandler.handleResult(result, context);
        }
    }

    @Override
    public void start() {
        lifecycleService.start();
    }

    @Override
    public void shutdown() {
        lifecycleService.stop();
    }
    
    /**
     * Simply reads a property resource file that contains the version of this
     * Ninja build. Helps to identify the Ninja version currently running.
     * 
     * @return The version of Ninja. Eg. "1.6-SNAPSHOT" while developing of "1.6" when released.
     */
    public String readNinjaVersion() {
        
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
    
    public final void logNinjaMode(Logger logger, NinjaProperties ninjaProperties) {
        
        if (logger.isInfoEnabled()) {
            
            // print out mode:
            String mode = "";
            if (ninjaProperties.isDev()) {
                mode = "dev";
            } else if (ninjaProperties.isTest()) {
                mode = "test";
            } else if (ninjaProperties.isProd()) {
                mode = "prod";
            }
        
            logger.info("Ninja is running in mode: {}", mode); 
        }
    
    
    
    }

}
