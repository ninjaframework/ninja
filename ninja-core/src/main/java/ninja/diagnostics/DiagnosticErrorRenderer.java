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
import java.util.Map;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Route;
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

        // set context response content type
        result.contentType("text/html");
        result.charset("utf-8");
        
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
        
        Result underlyingResult = diagnosticError.getUnderlyingResult();
        
        return new DiagnosticErrorRenderer()
            .appendHeader(
                    context,
                    result,
                    diagnosticError.getTitle())
                
            .appendTabsBegin(new String[] { "Exception", "Context", "Request", "Response" })
                
            .appendTabBegin(0)    
            .appendSourceSnippet(
                    diagnosticError.getSourceLocation(),
                    diagnosticError.getSourceLines(),
                    diagnosticError.getLineNumberOfSourceLines(),
                    diagnosticError.getLineNumberOfError())
            .appendThrowable(
                    diagnosticError.getThrowable()) 
            .appendTabEnd()
                
            .appendTabBegin(1)
            .appendContext(context)
            .appendTabEnd()
            
            .appendTabBegin(2)
            .appendRequest(context)
            .appendTabEnd()
            
            .appendTabBegin(3)
            .appendResponse(underlyingResult)
            .appendTabEnd()     
            
            .appendTabsEnd()
            .appendFooter();
    }
    
    private DiagnosticErrorRenderer appendHeader(Context context,
                                        Result result,
                                        String title) throws IOException {
        
        String headerTemplate = getResource("diagnostic_header.html");
        String styleTemplate = getResource("diagnostic.css");
        
        // simple token replacement
        headerTemplate = headerTemplate.replace("${TITLE}", escape(title));
        headerTemplate = headerTemplate.replace("${STYLE}", escape(styleTemplate));
        
        s.append(headerTemplate);
        
        if (result != null) {
            s.append("    <p id=\"detail\">\n");
                   
            if (result.getStatusCode() != 200) {
                s.append ("Status code ").append(result.getStatusCode());
            }
                   
            s.append(" for request '").append(context.getMethod()).append(" ").append(context.getRequestPath()).append("'\n");
            
            // append info about the route itself
            if (context.getRoute() != null) {
                Route route = context.getRoute();
                s.append("<br />In controller method '").append(route.getControllerClass().getCanonicalName()).append(".").append(route.getControllerMethod().getName()).append("'\n");
            }
            
            s.append("    </p>\n");
        }
        
        return this;
    }
    
    private DiagnosticErrorRenderer appendTabsBegin(String[] names) throws IOException {
        s.append("<div class='tabs standard'>\n");
        s.append("    <ul class=\"tab-links\">\n");
        
        for (int i = 0; i < names.length; i++) {
            s.append("        <li");
            
            if (i == 0) {
                s.append(" class=\"active\"");
            }
            
            s.append("><a href=\"#tab").append(i).append("\">").append(escape(names[i])).append("</a></li>\n");
        }
        
        s.append("    </ul>\n");
        s.append("    <div class=\"tab-content\">\n");
        				
        return this;
    }
    
    private DiagnosticErrorRenderer appendTabsEnd() throws IOException {
        s.append("    </div>\n");
        s.append("</div>\n");
        return this;
    }
    
    private DiagnosticErrorRenderer appendTabBegin(int index) throws IOException {
        s.append("        <div id=\"tab").append(index).append("\" class=\"tab");
        
        if (index == 0) {
            s.append(" active");
        }
        
        s.append("\">\n");
        return this;
    }
    
    private DiagnosticErrorRenderer appendTabEnd() throws IOException {
        s.append("        </div>\n");
        return this;
    }
    
    
    private DiagnosticErrorRenderer appendFooter() throws IOException {
        // embed jquery
        s.append("<script type='text/javascript'>").append(getResource("jquery-1.11.1.min.js")).append("</script>");
        // diagnostic javascript
        s.append("<script type='text/javascript'>").append(getResource("diagnostic.js")).append("</script>");
        // footer body -> html tags
        s.append(getResource("diagnostic_footer.html"));
        return this;
    }
    
    private DiagnosticErrorRenderer appendContext(Context context) throws IOException {
        s.append("<div class=\"context\">\n");
        
        s.append("<h2>Route</h2>\n");

        if (context.getRoute() != null) {
            Route route = context.getRoute();
            appendNameValue(s, "Http method", route.getHttpMethod());
            appendNameValue(s, "Controller method", route.getControllerClass().getCanonicalName() + "." + route.getControllerMethod().getName() + "()");
            StringBuilder params = new StringBuilder();
            for (Class type : route.getControllerMethod().getParameterTypes()) {
                if (params.length() > 0) { params.append(", "); }
                params.append(type.getCanonicalName());
            }
            appendNameValue(s, "Controller parameters", params.toString());
        } else {
            appendNoValues(s);
        }
        
        s.append("<h2>Session</h2>\n");

        if (context.getSession() != null && !context.getSession().getData().isEmpty()) {
            for (Map.Entry<String, String> sessionEntry : context.getSession().getData().entrySet()) {
                appendNameValue(s, sessionEntry.getKey(), sessionEntry.getValue());
            }
        } else {
            appendNoValues(s);
        }
        
        s.append("<h2>Flash</h2>\n");

        if (context.getFlashScope() != null && !context.getFlashScope().getCurrentFlashCookieData().isEmpty()) {
            for (Map.Entry<String, String> sessionEntry : context.getFlashScope().getCurrentFlashCookieData().entrySet()) {
                appendNameValue(s, sessionEntry.getKey(), sessionEntry.getValue());
            }
        } else {
            appendNoValues(s);
        }
        
        s.append("<h2>Attributes</h2>\n");

        Map<String,Object> attributes = context.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (Map.Entry<String,Object> entry : attributes.entrySet()) {
                appendNameValue(s, entry.getKey(), (entry.getValue() != null ? entry.getValue().toString() : "null"));
            }
        } else {
            appendNoValues(s);
        }
        
        List<Cookie> cookies = context.getCookies();
        
        if (cookies == null || cookies.isEmpty()) {
            s.append("<h2>Cookies</h2>\n");
            appendNoValues(s);
        } else {
            for (Cookie cookie : context.getCookies()) {
                s.append("<h2>Cookie: ").append(cookie.getName()).append("</h2>\n");
                appendNameValue(s, "Value", cookie.getValue());
                appendNameValue(s, "Path", cookie.getPath());
                appendNameValue(s, "Domain", cookie.getDomain());
                appendNameValue(s, "HTTP only", cookie.isHttpOnly()+"");
                appendNameValue(s, "Secure", cookie.isSecure()+"");
                appendNameValue(s, "Max age", cookie.getMaxAge()+"");
                appendNameValue(s, "Comment", cookie.getComment());
            }
        }
        
        s.append("</div>\n");
        return this;
    }
    
    private DiagnosticErrorRenderer appendRequest(Context context) throws IOException {
        s.append("<div class=\"context\">\n");
        
        
        s.append("<h2>Request</h2>\n");
        
        appendNameValue(s, "Context path", context.getContextPath());
        appendNameValue(s, "Hostname", context.getHostname());
        appendNameValue(s, "Method", context.getMethod());
        appendNameValue(s, "Remote address", context.getRemoteAddr());
        appendNameValue(s, "Content type", context.getRequestContentType());
        appendNameValue(s, "Path", context.getRequestPath());
        appendNameValue(s, "Scheme", context.getScheme());
        
        s.append("<h2>Parameters</h2>\n");
        
        Map<String, String[]> parameters = context.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                for (String value : entry.getValue()) {
                    appendNameValue(s, entry.getKey(), value);
                }
            }
        } else {
            appendNoValues(s);
        }
        
        s.append("<h2>Headers</h2>\n");
        
        Map<String, List<String>> headers = context.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                for (String value : entry.getValue()) {
                    appendNameValue(s, entry.getKey(), value);
                }
            }
        } else {
            appendNoValues(s);
        }
        
        s.append("</div>\n");
        return this;
    }
    
    private DiagnosticErrorRenderer appendResponse(Result result) throws IOException {
        s.append("<div class=\"context\">\n");

        s.append("<h2>Application Result</h2>\n");
        
        if (result == null) {
            // request did not get far enough along in processing to actually
            // have a response for us to debug
            appendNoValues(s, "Application failure before a result was created");
            return this;
        }

        appendNameValue(s, "Template", result.getTemplate());
        appendNameValue(s, "Charset", result.getCharset());
        appendNameValue(s, "Content type", result.getContentType());
        appendNameValue(s, "Status code", result.getStatusCode()+"");
        
        List<String> supportedContentTypes = result.supportedContentTypes();
        if (supportedContentTypes == null || supportedContentTypes.isEmpty()) {
            appendNameValue(s, "Supported content types", "None set");
        } else {
            for (int i = 0; i < supportedContentTypes.size(); i++) {
               appendNameValue(s, "Supported content type #" + i, supportedContentTypes.get(i)); 
            }
        }
        
        appendNameValue(s, "Fallback content type", result.fallbackContentType().or("None set")); 
        appendNameValue(s, "Json View", (result.getJsonView() != null ? result.getJsonView().getClass().getCanonicalName() : "None")); 
        
        
        s.append("<h2>Renderable</h2>\n");
        
        Object renderable = result.getRenderable();
        
        // only rendering exceptions would have the renderable actually set
        // to something other than a DiagnosticError
        if (renderable == null || renderable instanceof DiagnosticError) {
            appendNoValues(s);
        } else if (renderable instanceof Map) {
            Map<String,Object> map = (Map<String,Object>)renderable;
            for (Map.Entry<String,Object> entry : map.entrySet()) {
                appendNameValue(s, entry.getKey(), (entry.getValue() != null ? entry.getValue().toString() : "null")); 
            }
        } else {
            appendNameValue(s, "Class of", renderable.getClass().getCanonicalName()); 
        }

        
        s.append("<h2>Headers</h2>\n");
        
        Map<String, String> headers = result.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                appendNameValue(s, entry.getKey(), entry.getValue());
            }
        } else {
            appendNoValues(s);
        }
        
        
        
        s.append("</div>\n");
        return this;
    }
    
        
    private void appendNameValue(StringBuilder sb, String name, String value) throws IOException {
        sb.append("<pre><span class=\"line\" style=\"width: 200px;\">");
        sb.append(escape(name));
        sb.append("</span><span class=\"route\" style=\"left: 210px\">");
        sb.append(escape(value));
        sb.append("</span></pre>");
    }
    
    private void appendNoValues(StringBuilder sb) throws IOException {
        appendNoValues(sb, "No values");
    }
    
    private void appendNoValues(StringBuilder sb, String title) throws IOException {
        sb.append("<pre style=\"border-bottom: 0px;\"><span style=\"position: absolute; left: 45px;\">").append(escape(title)).append("</span></pre><br/>");
    }
    
    private DiagnosticErrorRenderer appendSourceSnippet(URI sourceLocation,
                                                        List<String> sourceLines,
                                                        int lineNumberOfSourceLines,
                                                        int lineNumberOfError) {
        if (sourceLocation != null) {
            s.append("    <h2>").append(escape(sourceLocation.toString())).append("</h2>\n");
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
                        .append(escape(sourceLines.get(i)))
                        .append("</span>");
                s.append("</pre>");
            }
            s.append("    </div>\n");
        }

        return this;
    }
    
    private DiagnosticErrorRenderer appendThrowable(Throwable throwable) throws IOException {
        s.append("<h2>Stack Trace</h2>");
        if (throwable != null) {
            s.append("<pre><span class=\"stacktrace\">").append(escape(throwableStackTraceToString(throwable))).append("</span></pre>\n");
        } else {
            appendNoValues(s, "Result was not triggered by an exception");
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
    
    private String escape(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }
    
    private String getResource(String resourceName) throws IOException {
        URL url = getClass().getResource(resourceName);
        if (url == null) {
            throw new IOException("Unable to find diagnostic resource: " + resourceName);
        }
        
        return IOUtils.toString(url);
    }
    
}
