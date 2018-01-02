/**
 * Copyright (C) 2012- the original author or authors.
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
 * A convenience unchecked exception for "rendering" exceptions.
 * 
 * Allows you to wrap any exception (checked or unchecked) and throw it along
 * with info about what...
 * 
 * Should signal a html error 400 - bad request (the client sent something strange).
 * 
 * Useful inside controllers or filters for instance.
 * 
 * Ninja is supposed to pick it up and render an appropriate error page.
 * 
 */
public class RenderingException extends NinjaException {  
    
    final static String DEFAULT_MESSAGE = "Result rendering failed and that's all we know.";
    
    private final String title;
    private final Result result;
    private final String sourcePath;
    private final int lineNumber;
    
    public RenderingException() {
        this(DEFAULT_MESSAGE, null, null, null, null, -1);
    }

    public RenderingException(String message) {
        this(message, null, null, null, null, -1);
    }
   
    public RenderingException(String message, Throwable cause) {
        this(message, cause, null, null, null, -1);
    }
    
    public RenderingException(String message, Throwable cause, Result result, String sourcePath, int lineNumber) {
        this(message, cause, result, null, sourcePath, lineNumber);
    }
    
    public RenderingException(String message, Throwable cause, Result result, String title, String sourcePath, int lineNumber) {
        super(Result.SC_500_INTERNAL_SERVER_ERROR, message, cause);
        this.title = title;
        this.result = result;
        this.sourcePath = sourcePath;
        this.lineNumber = lineNumber;
    }

    public String getTitle() {
        return title;
    }

    public Result getResult() {
        return result;
    }
    
    public String getSourcePath() {
        return sourcePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    
}
