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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.List;
import ninja.Context;
import ninja.Result;
import ninja.exceptions.InternalServerErrorException;
import ninja.utils.ResponseStreams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for rendering <code>DiagnosticError</code> instances as
 * a Result.  Does not rely on any 3rd party rendering library to permit
 * rendering exceptions in the case where a template engine fails!
 * 
 * @author Joe Lauer (https://twitter.com/jjlauer)
 * @author Fizzed, Inc. (http://fizzed.com)
 */
public class DiagnosticErrorRenderer {
    private static final Logger logger = LoggerFactory.getLogger(DiagnosticErrorRenderer.class);
    
    private final StringBuilder s;
    
    private DiagnosticErrorRenderer() {
        s = new StringBuilder();
    }
    
    public String render() {
        return s.toString();
    }
    
    static public void tryToRender(Context context, Result result, DiagnosticError diagnosticError, boolean throwInternalServerExceptionOnError) {
        try {
            
            DiagnosticErrorRenderer errorRenderer
                = build(context, result, diagnosticError);
            
            errorRenderer.renderResult(
                context,
                result);
            
        } catch (IOException e) {
            // fallback to ninja system-wide error handler?
            if (throwInternalServerExceptionOnError) {
                throw new InternalServerErrorException(e);
            } else {
                logger.error("Something is really fishy. Unable to render diagnostic error", e);
            }
        }
        
    }
    
    public void renderResult(Context context, Result result) throws IOException {
        String out = render();

        ResponseStreams responseStreams = context.finalizeHeaders(result);
        try (Writer w = responseStreams.getWriter()) {
            w.write(out);
            w.flush();
            w.close();
        }
    }
    
    static public DiagnosticErrorRenderer build(Context context,
                                                Result result,
                                                DiagnosticError diagnosticError) throws IOException {
        return new DiagnosticErrorRenderer()
            .appendHeader(
                    context,
                    result,
                    diagnosticError.getTitle())
            .appendSourceSnippet(
                    diagnosticError.getSourceLocation(),
                    diagnosticError.getSourceLines(),
                    diagnosticError.getLineNumberOfSourceLines(),
                    diagnosticError.getLineNumberOfError())
            .appendThrowable(
                    diagnosticError.getThrowable()) 
            .appendFooter();
    }
    
    private DiagnosticErrorRenderer appendHeader(Context context,
                                        Result result,
                                        String title) throws IOException {
        
        String headerTemplate = getResource("diagnostic_header.html");
        String styleTemplate = getResource("diagnostic.css");
        
        // simple token replacement
        headerTemplate = headerTemplate.replace("${TITLE}", title);
        headerTemplate = headerTemplate.replace("${STYLE}", styleTemplate);
        
        s.append(headerTemplate);
        
        if (result != null) {
            s.append("    <p id=\"detail\">\n");
                   
            if (result.getStatusCode() != 200) {
                s.append ("Status code ").append(result.getStatusCode());
            }
                   
            s.append(" for request '").append(context.getMethod()).append(" ").append(context.getRequestPath()).append("'\n");
            s.append("    </p>\n");
        }
        
        return this;
    }
    
    private DiagnosticErrorRenderer appendFooter() throws IOException {
        s.append(getResource("diagnostic_footer.html"));
        return this;
    }
    
    private DiagnosticErrorRenderer appendSourceSnippet(URI sourceLocation,
                                                        List<String> sourceLines,
                                                        int lineNumberOfSourceLines,
                                                        int lineNumberOfError) {
        if (sourceLocation != null) {
            s.append("    <h2>").append(sourceLocation).append("</h2>\n");
        }

        if (sourceLines != null) {
            s.append("    <div>\n");
            for (int i = 0; i < sourceLines.size(); i++) {
                s.append("<pre>");
                
                int lineNumber = lineNumberOfSourceLines + i;
                
                // line of error?
                String cssClass = (lineNumber == lineNumberOfError ? "line error" : "line info");

                s.append("<span class=\"").append(cssClass).append("\">").append(lineNumber).append("</span>");
                s.append("<span class=\"")
                        .append("route")
                        .append("\">")
                        .append(StringEscapeUtils.escapeHtml4(sourceLines.get(i)))
                        .append("</span>");
                s.append("</pre>");
            }
            s.append("    </div>\n");
        }

        return this;
    }
    
    private DiagnosticErrorRenderer appendThrowable(Throwable throwable) {
        if (throwable != null) {
            s.append("    <div>\n")
            .append("      <pre><span class=\"stacktrace\">\n")
            .append(throwableStackTraceToString(throwable))
            .append("      </span></pre>\n")
            .append("    </div>");
        }
        return this;
    }
    
    private String throwableStackTraceToString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        return sw.toString();
    }
    
    private String getResource(String resourceName) throws IOException {
        URL url = getClass().getResource(resourceName);
        if (url == null) {
            throw new IOException("Unable to find diagnostic resource: " + resourceName);
        }
        
        return IOUtils.toString(url);
    }
    
}
