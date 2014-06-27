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

public interface Ninja {

	/**
	 * When a route is requested this method is called.
	 */
	void onRouteRequest(Context.Impl context);
    
    Result onException(Context context, Exception exception);
    
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
     * Invoked when the framework starts. Usually inits stuff like the scheduler
     * and so on.
     */
    void onFrameworkStart();

    /**
     * Invoked when the server hosting Ninja is being stopped. Usually
     * shuts down the guice injector and stopps all services.
     */
    void onFrameworkShutdown();
    
    
    void renderErrorResultAndCatchAndLogExceptions(Result result, Context context);

}