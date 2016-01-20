/**
 * Copyright (C) 2012-2016 the original author or authors.
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

package ninja.exceptions;

import ninja.Result;

/**
 * A convenience unchecked exception. 
 * Allows you to wrap any exception (checked or unchecked) and throw it.
 * 
 * Should signal a html error 500 (something went wrong on the server).
 * 
 * Useful inside controllers or filters for instance.
 * 
 * Ninja is supposed to pick it up and render an appropriate error page.
 * 
 */
public class InternalServerErrorException extends NinjaException {  
    
    final static String DEFAULT_MESSAGE = "That's an internal server error and all we know.";
    
    public InternalServerErrorException() {
        super(Result.SC_500_INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE);
    }

    public InternalServerErrorException(String message) {
        super(Result.SC_500_INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(Result.SC_500_INTERNAL_SERVER_ERROR, message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(Result.SC_500_INTERNAL_SERVER_ERROR, DEFAULT_MESSAGE, cause);
    }
}
