package ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.async.AsyncStrategy;
import ninja.async.AsyncStrategyFactoryHolder;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.CookieHelper;
import ninja.utils.NinjaConstant;
import ninja.utils.ResponseStreams;
import ninja.utils.ResponseStreamsServlet;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import com.google.inject.Inject;

public class ContextImpl implements Context {

	private HttpServletRequest httpServletRequest;

	private HttpServletResponse httpServletResponse;

	private Route route;

	private AsyncStrategy asyncStrategy;
	private final Object asyncLock = new Object();

	private final BodyParserEngineManager bodyParserEngineManager;

	private final FlashCookie flashCookie;

	private final SessionCookie sessionCookie;
    private final ResultHandler resultHandler;
    private final Validation validation;
    
    @Inject Logger logger;

	@Inject
	public ContextImpl(BodyParserEngineManager bodyParserEngineManager,
            FlashCookie flashCookie, SessionCookie sessionCookie,
            ResultHandler resultHandler, Validation validation) {

		this.bodyParserEngineManager = bodyParserEngineManager;
		this.flashCookie = flashCookie;
		this.sessionCookie = sessionCookie;
        this.resultHandler = resultHandler;
        this.validation = validation;
    }

	public void init(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;

		// init flash scope:
		flashCookie.init(this);

		// init session scope:
		sessionCookie.init(this);

	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return this.httpServletResponse;
	}

	@Override
	public String getPathParameter(String key) {
	    
	    String encodedParameter = route.getPathParametersEncoded(getRequestPath())
                .get(key);
	    
	    if (encodedParameter == null) {
	        return null;
	    } else {
	        return URI.create(encodedParameter).getPath();
	    }

	}
	
    @Override
    public String getPathParameterEncoded(String key) {

        return route.getPathParametersEncoded(getRequestPath()).get(key);

    }

	@Override
	public Integer getPathParameterAsInteger(String key) {
		String parameter = getPathParameter(key);

		try {
			return Integer.parseInt(parameter);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getParameter(String key) {
		return httpServletRequest.getParameter(key);
	}

	@Override
	public String getParameter(String key, String defaultValue) {
		String parameter = getParameter(key);

		if (parameter == null) {
			parameter = defaultValue;
		}

		return parameter;
	}

	@Override
	public Integer getParameterAsInteger(String key) {
		return getParameterAsInteger(key, null);
	}

	@Override
	public Integer getParameterAsInteger(String key, Integer defaultValue) {
		String parameter = getParameter(key);

		try {
			return Integer.parseInt(parameter);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public Map<String, String[]> getParameters() {
		return httpServletRequest.getParameterMap();
	}

	@Override
	public String getHeader(String name) {
		return httpServletRequest.getHeader(name);
	}

	@Override
	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			headers.put(name, httpServletRequest.getHeader(name));
		}
		return headers;
	}

	@Override
	public String getCookieValue(String name) {
		return CookieHelper.getCookieValue(name,
				httpServletRequest.getCookies());
	}

	@Override
	public <T> T parseBody(Class<T> classOfT) {

		BodyParserEngine bodyParserEngine = bodyParserEngineManager
				.getBodyParserEngineForContentType(ContentTypes.APPLICATION_JSON);

		if (bodyParserEngine == null) {
			return null;
		}

		return bodyParserEngine.invoke(this, classOfT);

	}

	@Override
	public FlashCookie getFlashCookie() {
		return flashCookie;
	}

	@Override
	public SessionCookie getSessionCookie() {
		return sessionCookie;
	}

    @Override
    public Context addCookie(Cookie cookie) {
        httpServletResponse.addCookie(CookieHelper.convertNinjaCookieToServletCookie(cookie));
        return this;
    }

    @Deprecated
    @Override
	public String getRequestUri() {
		return getHttpServletRequest().getRequestURI();
	}

	public void handleAsync() {
		synchronized (asyncLock) {
			if (asyncStrategy == null) {
				asyncStrategy = AsyncStrategyFactoryHolder.getInstance(httpServletRequest)
						.createStrategy(httpServletRequest, resultHandler);
				asyncStrategy.handleAsync();
			}
		}
	}

	@Override
	public void returnResultAsync(Result result) {
		synchronized (asyncLock) {
			handleAsync();
			asyncStrategy.returnResultAsync(result, this);
		}
	}

    @Override
    public void asyncRequestComplete() {
        returnResultAsync(null);
    }

    /**
	 * Used to indicate that the controller has finished executing
	 */
	public Result controllerReturned() {
        if (asyncStrategy != null) {
            return asyncStrategy.controllerReturned();
        }
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return httpServletRequest.getInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return httpServletRequest.getReader();
	}

	@Override
	public ResponseStreams finalizeHeaders(Result result) {
	    
		httpServletResponse.setStatus(result.getStatusCode());
		
		//copy headers
		for (Entry<String, String> header : result.getHeaders().entrySet()) {
			httpServletResponse.addHeader(header.getKey(), header.getValue());
		}

        //copy ninja cookies / flash and session
        flashCookie.save(this);
        sessionCookie.save(this);

        //copy cookies
        for (ninja.Cookie cookie : result.getCookies()) {
            httpServletResponse.addCookie(CookieHelper
					.convertNinjaCookieToServletCookie(cookie));

        }

        // set content type
        if (result.getContentType() != null) {
            
            httpServletResponse.setContentType(result.getContentType());
        }
        
        //Set charset => use utf-8 if not set
        //Sets correct encoding for Content-Type. But also for the output writers.
        if (result.getCharset() != null) {
            httpServletResponse.setCharacterEncoding(result.getCharset());
        } else {
            httpServletResponse.setCharacterEncoding(NinjaConstant.UTF_8);
        }
		
		//possibly
		ResponseStreamsServlet responseStreamsServlet = new ResponseStreamsServlet();
		responseStreamsServlet.init(httpServletResponse);
		
		return (ResponseStreams) responseStreamsServlet;

	}

	@Override
	public String getRequestContentType() {
		return httpServletRequest.getContentType();
	}

    @Override
    public String getAcceptContentType() {
        String contentType = httpServletRequest.getHeader("accept");

        if (contentType == null) {
            return Result.TEXT_HTML;
        }

        if (contentType.indexOf("application/xhtml") != -1 || contentType.indexOf("text/html") != -1 || contentType.startsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        if (contentType.indexOf("application/xml") != -1 || contentType.indexOf("text/xml") != -1) {
            return Result.APPLICATION_XML;
        }

        if (contentType.indexOf("application/json") != -1 || contentType.indexOf("text/javascript") != -1) {
            return Result.APPLICATON_JSON;
        }

        if (contentType.indexOf("text/plain") != -1) {
            return Result.TEXT_PLAIN;
        }

        if (contentType.indexOf("application/octet-stream") != -1) {
            return Result.APPLICATION_OCTET_STREAM;
        }

        if (contentType.endsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        return Result.TEXT_HTML;
    }

    @Override
    public String getAcceptEncoding() {
        return httpServletRequest.getHeader("accept-encoding");
    }

    @Override
    public String getAcceptLanguage() {
        return httpServletRequest.getHeader("accept-language");
    }

    @Override
    public String getAcceptCharset() {
        return httpServletRequest.getHeader("accept-charset");
    }

    @Override
    public Route getRoute() {
        return route;
    }

	@Override
    public boolean isMultipart() {
		
	    return ServletFileUpload.isMultipartContent(httpServletRequest);
    }

	@Override
    public FileItemIterator getFileItemIterator() {
		
		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator fileItemIterator = null;

		try {
			fileItemIterator = upload.getItemIterator(httpServletRequest);
        } catch (FileUploadException e) {
	        logger.error("Error while trying to process mulitpart file upload", e);
        } catch (IOException e) {
        	logger.error("Error while trying to process mulitpart file upload", e);
        }
		
		return fileItemIterator;
    }

	@Override
	public String getRequestPath() {
	    //http://stackoverflow.com/questions/966077/java-reading-undecoded-url-from-servlet
	    
		//this one is unencoded:
		String unencodedContextPath = httpServletRequest.getContextPath();
		//this one is unencoded, too, but may containt the context:
		String fullUnencodedUri = httpServletRequest.getRequestURI();
		
		String result = fullUnencodedUri.substring(unencodedContextPath.length());
		
		return result;

	}

    @Override
    public Validation getValidation() {
        return validation;
    }
}
