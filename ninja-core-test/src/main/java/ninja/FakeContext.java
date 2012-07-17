package ninja;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.ResponseStreams;
import org.apache.commons.fileupload.FileItemIterator;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fake context
 */
public class FakeContext implements Context {
    private String requestContentType;
    
    private String requestPath;
    
    /** please use the requestPath stuff */
    @Deprecated
    private String requestUri;
    private FlashCookie flashCookie;
    private SessionCookie sessionCookie;
    private List<Cookie> addedCookies = new ArrayList<Cookie>();
    private Map<String, String> cookieValues = new HashMap<String, String>();
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> pathParams = new HashMap<String, String>();
    private Map<String, String> headers = new HashMap<String, String>();
    private Object body;

    public FakeContext setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
        return this;
    }

    @Override
    public String getRequestContentType() {
        return requestContentType;
    }

    /**
     * Please use the getServletPath and setServletPath facilities. 
     * 
     * @param requestUri
     * @return
     */
    @Deprecated
    public FakeContext setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }


    /**
     * Please use the getServletPath and setServletPath facilities. 
     * 
     * @param requestUri
     * @return
     */
    @Deprecated
    public String getRequestUri() {
        return requestUri;
    }

    public FakeContext setFlashCookie(FlashCookie flashCookie) {
        this.flashCookie = flashCookie;
        return this;
    }

    public FlashCookie getFlashCookie() {
        return flashCookie;
    }

    public FakeContext setSessionCookie(SessionCookie sessionCookie) {
        this.sessionCookie = sessionCookie;
        return this;
    }

    public SessionCookie getSessionCookie() {
        return sessionCookie;
    }

    @Override
    public Context addCookie(Cookie cookie) {
        this.addedCookies.add(cookie);
        return this;
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    public FakeContext addParameter(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public String getParameter(String key) {
        return params.get(key);
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public Integer getParameterAsInteger(String key) {
        String value = getParameter(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Integer getParameterAsInteger(String key, Integer defaultValue) {
        String value = getParameter(key);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    public FakeContext addPathParameter(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    @Override
    public String getPathParameter(String key) {
        return pathParams.get(key);
    }

    @Override
    public Integer getPathParameterAsInteger(String key) {
        String value = getParameter(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Map<String, String[]> getParameters() {
        return Maps.transformValues(params, new Function<String, String[]>() {
            @Override
            public String[] apply(@Nullable String s) {
                return new String[] {s};
            }
        });
    }

    public FakeContext addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public FakeContext addCookieValue(String name, String value) {
        cookieValues.put(name, value);
        return this;
    }

    @Override
    public String getCookieValue(String name) {
        return cookieValues.get(name);
    }

    public FakeContext setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public <T> T parseBody(Class<T> classOfT) {
        return classOfT.cast(body);
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public void handleAsync() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public void returnResultAsync(Result result) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public void asyncRequestComplete() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public Result controllerReturned() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public Route getRoute() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }
    
	
	public FakeContext setRequestPath(String path) {
		this.requestPath = path;
		return this;
	}

	@Override
	public String getRequestPath() {
		return this.requestPath;
	}
}
