package ninja;

import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.ResponseStreams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.validation.Validation;
import org.apache.commons.fileupload.FileItemIterator;

import java.io.*;
import java.util.Map;

/**
 * A wrapped context.  Useful if filters want to modify the context before sending
 * it on.
 *
 * @author James Roper
 */
public class WrappedContext implements Context {
    private final Context wrapped;

    public WrappedContext(Context wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getRequestUri() {
        return wrapped.getRequestUri();
    }

    @Override
    public FlashCookie getFlashCookie() {
        return wrapped.getFlashCookie();
    }

    @Override
    public SessionCookie getSessionCookie() {
        return wrapped.getSessionCookie();
    }

    @Override
    public Context addCookie(Cookie cookie) {
        return wrapped.addCookie(cookie);
    }

    @Override
    @Deprecated
    public HttpServletRequest getHttpServletRequest() {
        return wrapped.getHttpServletRequest();
    }

    @Override
    @Deprecated
    public HttpServletResponse getHttpServletResponse() {
        return wrapped.getHttpServletResponse();
    }

    @Override
    public String getParameter(String key) {
        return wrapped.getParameter(key);
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        return wrapped.getParameter(key, defaultValue);
    }

    @Override
    public Integer getParameterAsInteger(String key) {
        return wrapped.getParameterAsInteger(key);
    }

    @Override
    public Integer getParameterAsInteger(String key, Integer defaultValue) {
        return wrapped.getParameterAsInteger(key, defaultValue);
    }

    @Override
    public String getPathParameter(String key) {
        return wrapped.getPathParameter(key);
    }

    @Override
    public Integer getPathParameterAsInteger(String key) {
        return wrapped.getPathParameterAsInteger(key);
    }

    @Override
    public Map<String, String[]> getParameters() {
        return wrapped.getParameters();
    }

    @Override
    public String getHeader(String name) {
        return wrapped.getHeader(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return wrapped.getHeaders();
    }

    @Override
    public String getCookieValue(String name) {
        return wrapped.getCookieValue(name);
    }

    @Override
    public <T> T parseBody(Class<T> classOfT) {
        return wrapped.parseBody(classOfT);
    }

    @Override
    public void handleAsync() {
        wrapped.handleAsync();
    }

    @Override
    public void returnResultAsync(Result result) {
        wrapped.returnResultAsync(result);
    }

    @Override
    public void asyncRequestComplete() {
        wrapped.asyncRequestComplete();
    }

    @Override
    public Result controllerReturned() {
        return wrapped.controllerReturned();
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        return wrapped.finalizeHeaders(result);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return wrapped.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return wrapped.getReader();
    }

	@Override
	public String getRequestContentType() {
		return wrapped.getRequestContentType();
	}

    @Override
    public Route getRoute() {
        return wrapped.getRoute();
    }

	@Override
    public boolean isMultipart() {
	    return wrapped.isMultipart();
    }

	@Override
    public FileItemIterator getFileItemIterator() {
	    return wrapped.getFileItemIterator();
    }

	@Override
	public String getRequestPath() {
		return wrapped.getRequestPath();
	}

    @Override
    public Validation getValidation() {
        return wrapped.getValidation();
    }

    @Override
    public String getPathParameterEncoded(String key) {
        return wrapped.getPathParameterEncoded(key);
    }

    @Override
    public String getAcceptContentType() {
        return wrapped.getAcceptContentType();
    }

    @Override
    public String getAcceptEncoding() {
        return wrapped.getAcceptEncoding();
    }

    @Override
    public String getAcceptLanguage() {
        return wrapped.getAcceptLanguage();
    }

    @Override
    public String getAcceptCharset() {
        return wrapped.getAcceptCharset();
    }
}
