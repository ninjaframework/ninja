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

import ninja.lifecycle.LifecycleService;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import ninja.servlet.ContextImpl;
import ninja.utils.NinjaConstant;
import ninja.utils.ResultHandler;

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

    /**
     * The most important thing: A cool logo.
     */
    private final String NINJA_LOGO = " _______  .___ _______        ____.  _____   \n"
            + " \\      \\ |   |\\      \\      |    | /  _  \\  \n"
            + " /   |   \\|   |/   |   \\     |    |/  /_\\  \\ \n"
            + "/    |    \\   /    |    \\/\\__|    /    |    \\\n"
            + "\\____|__  /___\\____|__  /\\________\\____|__  /\n"
            + "     web\\/framework   \\/                  \\/ \n";

    private final LifecycleService lifecycleService;
    private final Router router;
    private final ResultHandler resultHandler;

    @Inject
    public NinjaImpl(LifecycleService lifecycleService,
                     Router router,
                     ResultHandler resultHandler) {

        this.router = router;
        this.lifecycleService = lifecycleService;
        this.resultHandler = resultHandler;

        // This system out println is intended.
        System.out.println(NINJA_LOGO);
    }

    /**
     * I do all the main work.
     * 
     * @param context
     *            context
     */
    public void invoke(ContextImpl context) {

        String httpMethod = context.getHttpServletRequest().getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            Result result = route.getFilterChain().next(context);

            resultHandler.handleResult(result, context);

        } else {
            // throw a 404 "not found" because we did not find the route

            Result result = Results.html(Result.SC_404_NOT_FOUND).template(
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

}
