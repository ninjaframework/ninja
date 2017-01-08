/**
 * Copyright (C) 2012-2017 the original author or authors.
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

public interface Ninja {

	/**
	 * When a route is requested this method is called.
	 */
	void onRouteRequest(Context.Impl context);
    
    /**
     * This result should be used when an error occurs.
     * 
     * @param context The context for this request
     * @param exception The exception to handle. Can be used to customize error message.
     * @return a result you can use to render the error.
     */
    Result onException(Context context, Exception exception);
    
    /**
     * Should handle cases where a <code>RenderingException</code> is thrown
     * when handling the rendering of a Result.
     * 
     * Should lead to an html error 500 -- internal server error. If no special
     * handling is required, simply:
     * 
     * <code>
     *   return getInternalServerErrorResult(context, exception);
     * </code>
     */
    /** NOT REQUIRED YET IN ORDER TO NOT BREAK COMPATIBILITY...
    Result getRenderingExceptionResult(Context context, RenderingException exception);
    */
    
    /**
     * Should handle cases where an exception is thrown
     * when handling a route that let to an internal server error.
     * 
     * Should lead to a html error 500 - internal sever error
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getInternalServerErrorResult(Context context, Exception exception);
    
    /**
     * Should handle cases where the client sent strange date that
     * led to an error.
     * 
     * Should lead to a html error 400 - bad request
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getBadRequestResult(Context context, Exception exception);
    
    /**
     * Should handle cases where no route can be found for a given request.
     * 
     * Should lead to a html error 404 - not found
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getNotFoundResult(Context context);
    
    /**
     * Should handle cases where access is unauthorized
     *
     * Should lead to a html error 401 - unauthorized
     * (and be used with the same mindset).
     *
     * By default, WWW-Authenticate is set to None.
     *
     * Usually used by BasicAuthFilter for instance(...).
     */
    Result getUnauthorizedResult(Context context);

    /**
     * Should handle cases where access is forbidden
     *
     * Should lead to a html error 403 - forbidden
     * (and be used with the same mindset).
     * 
     * Usually used by SecureFilter for instance(...).
     */
    Result getForbiddenResult(Context context);

    /**
     * Invoked when the framework starts. Usually inits stuff like the scheduler
     * and so on.
     */
    void onFrameworkStart();

    /**
     * Invoked when the server hosting Ninja is being stopped. Usually
     * shuts down the guice injector and stopps all services.
     */
    void onFrameworkShutdown();
    
    /**
     * Should be used to render an error. Any errors should be catched
     * and not reported in any way to the request.
     * 
     * For instance if your application catches a sever internal computation
     * error use this method and its implementations to render out
     * an error html page.
     */
    void renderErrorResultAndCatchAndLogExceptions(Result result, Context context);

}