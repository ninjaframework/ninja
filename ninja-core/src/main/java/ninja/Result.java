package ninja;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Result {
	
	
	///////////////////////////////////////////////////////////////////////////
	// HTTP Status codes
	///////////////////////////////////////////////////////////////////////////
	public static int SC_200_OK = 200;
	public static int SC_204_NO_CONTENT = 204;
	
	//for redirects:
	public static int SC_300_MULTIPLE_CHOICES = 300;
	public static int SC_301_MOVED_PERMANENTLY = 301;
	public static int SC_302_FOUND = 302;
	public static int SC_303_SEE_OTHER = 303;
	public static int SC_307_TEMPORARY_REDIRECT = 307;
	
	public static int SC_400_BAD_REQUEST = 400;
	public static int SC_403_FORBIDDEN = 403;
	public static int SC_404_NOT_FOUND = 404;
	
	public static int SC_500_INTERNAL_SERVER_ERROR = 500;
	public static int SC_501_NOT_IMPLEMENTED = 501;
	

	///////////////////////////////////////////////////////////////////////////
	// Some MIME types
	///////////////////////////////////////////////////////////////////////////
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_PLAIN = "text/plain";
	public static final String APPLICATON_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	
	/* Used as redirection header */
	public static final String LOCATION = "Location";

	private int statusCode;

	private Object renderable;

	private String contentType;

	private Map<String, String> headers;

	private List<Cookie> cookies;
	
	private String template;

	public Result(int statusCode) {
		
		this.statusCode = statusCode;

		this.headers = Maps.newHashMap();
		this.cookies = Lists.newArrayList();

	}

	public Object getRenderable() {
		return renderable;
	}

	public Result render(Object renderable) {
		this.renderable = renderable;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type
	 * 
	 * @param contentType
	 * @Deprecated => please use contentType(...)
	 */
	@Deprecated
	public Result setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	/**
	 * Sets the content type
	 * 
	 * @param contentType
	 */
	public Result contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Result addHeader(String headerName, String headerContent) {

		headers.put(headerName, headerContent);
		return this;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public Result addCookie(Cookie cookie) {
		cookies.add(cookie);
		return this;
	}
	
    public Result unsetCookie(String name) {
		cookies.add(Cookie.builder(name, null).setMaxAge(0).build());
		return this;
    }

	public int getStatusCode() {
		return statusCode;
	}
	
	public Result status(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getTemplate() {
		return template;
	}

	public Result template(String template) {
		this.template = template;
		return this;
	}
	
	public Result redirect(String url) {
		return addHeader(LOCATION, url);
	}
	
	public Result html() {
		contentType = TEXT_HTML;
		return this;
	}
	
	public Result json() {
		contentType = APPLICATON_JSON;
		return this;
	}
	

}
