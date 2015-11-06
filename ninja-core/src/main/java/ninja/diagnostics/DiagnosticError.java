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

package ninja.diagnostics;

import java.net.URI;
import java.util.List;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;

/**
 * Represents an application error/exception that includes extra
 * information in order to diagnose it. Knows how to render itself as the
 * renderable inside a <code>Result</code>.
 * 
 * @author Joe Lauer (https://twitter.com/jjlauer)
 * @author Fizzed, Inc. (http://fizzed.com)
 */
public class DiagnosticError implements Renderable {
 
    private final String title;
    private final Throwable throwable;
    /** can either be from jar, classpath, or file */
    private final URI sourceLocation;
    private final List<String> sourceLines;
    private final int lineNumberOfSourceLines;
    private final int lineNumberOfError;
    // underlying result
    private final Result underlyingResult;

    public DiagnosticError(String title,
                            Throwable throwable,
                            Result underlyingResult) {
        // error with no source found
        this (title, throwable, null, null, -1, -1, underlyingResult);
    }
    
    public DiagnosticError(String title,
                            Throwable throwable,
                            URI sourceLocation,
                            List<String> sourceLines,
                            int lineNumberOfSourceLines,
                            int lineNumberOfError,
                            Result underlyingResult) {
        this.throwable = throwable;
        this.title = title;
        this.sourceLocation = sourceLocation;
        this.sourceLines = sourceLines;
        this.lineNumberOfSourceLines = lineNumberOfSourceLines;
        this.lineNumberOfError =  lineNumberOfError;
        this.underlyingResult = underlyingResult;
    }

    public String getTitle() {
        return title;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public URI getSourceLocation() {
        return sourceLocation;
    }

    public List<String> getSourceLines() {
        return sourceLines;
    }

    public int getLineNumberOfSourceLines() {
        return lineNumberOfSourceLines;
    }

    public int getLineNumberOfError() {
        return lineNumberOfError;
    }

    public Result getUnderlyingResult() {
        return underlyingResult;
    }
    
    @Override
    public void render(Context context, Result result) {
        DiagnosticErrorRenderer
            .tryToRender(context, result, this, true);
    }
    
}
