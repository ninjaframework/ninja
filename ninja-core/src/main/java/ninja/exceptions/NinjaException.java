/**
 * Copyright (C) 2012-2015 the original author or authors.
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

/**
 * A convenience unchecked exception. 
 * Allows you to wrap any exception (checked or unchecked) and throw it.
 * 
 * Should signal a html error 400 - bad request (the client sent something strange).
 * 
 * Useful inside controllers or filters for instance.
 * 
 * Ninja is supposed to pick it up and render an appropriate error page.
 * 
 */
public class NinjaException extends RuntimeException {  
    
    int httpStatus;
    
    public NinjaException(int httpStatus, String httpMessage) {
        super(httpMessage);
        this.httpStatus = httpStatus;
    }

    public NinjaException(int httpStatus, String httpMessage, Throwable cause) {
        super(httpMessage, cause);
        this.httpStatus = httpStatus;
    }
    
    public int getHttpStatus() {
        return this.httpStatus;
    }

}
