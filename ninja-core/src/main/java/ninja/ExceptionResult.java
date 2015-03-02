/**
 * Copyright (C) 2012-2015 Joe Lauer
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

/**
 * A result when an error occurs in the application (e.g. internal server error).
 * Will optionally include an <code>Exception</code> as the underlying cause for
 * the error. Template engines may choose to use <code>ExceptionResult.getCause()</code>
 * and/or <Code>Result.getRenderable()</code> during the rendering process to provide
 * a detailed response.  A template engine may handle this type of result
 * as follows:
 * 
 * <code>
 * public void invoke(Context context, Result result) {
 *       
 *       if (result instanceof ExceptionResult) {
 *           ExceptionResult exceptionResult = (ExceptionResult)result;
 *           Exception cause = exceptionResult.getCause();
 * 
 *           // do something with cause
 * 
 *       }
 * 
 *       // rest of method
 * 
 * }
 * </code>
 * 
 * @author Joe Lauer
 */
public class ExceptionResult extends Result {
    
    private final Exception cause;
    
    public ExceptionResult(int statusCode) {
        super(statusCode);
        this.cause = null;
    }
    
    public ExceptionResult(int statusCode, Exception cause) {
        super(statusCode);
        this.cause = cause;
    }

    /**
     * The underlying exception that caused this <code>ErrorResult</code>.
     * @return The underlying cause of the exception or null if none was
     *      provided.
     */
    public Exception getCause() {
        return cause;
    }
    
}
