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
    private final URI sourceLocation;                   // can either be from jar, classpath, or file
    private final List<String> sourceLines;
    private final int lineNumberOfSourceLines;
    private final int lineNumberOfError;

    public DiagnosticError(String title,
                            Throwable throwable) {
        // error with no source found
        this (title, throwable, null, null, -1, -1);
    }
    
    public DiagnosticError(String title,
                            Throwable throwable,
                            URI sourceLocation,
                            List<String> sourceLines,
                            int lineNumberOfSourceLines,
                            int lineNumberOfError) {
        this.throwable = throwable;
        this.title = title;
        this.sourceLocation = sourceLocation;
        this.sourceLines = sourceLines;
        this.lineNumberOfSourceLines = lineNumberOfSourceLines;
        this.lineNumberOfError =  lineNumberOfError;
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

    @Override
    public void render(Context context, Result result) {
        DiagnosticErrorRenderer
            .tryToRender(context, result, this, true);
    }
    
}
