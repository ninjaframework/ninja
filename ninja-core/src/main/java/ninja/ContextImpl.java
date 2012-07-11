package ninja;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import ninja.async.AsyncStrategy;
import ninja.async.AsyncStrategyFactoryHolder;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;
import ninja.utils.CookieHelper;
import ninja.utils.NinjaConstant;

import com.google.inject.Inject;

public class ContextImpl implements Context {

	private HttpServletRequest httpServletRequest;

	private HttpServletResponse httpServletResponse;

    private Route route;

	// * if set this template is used. otherwise the default mapping **/
	private String templateOverride = null;

	private HTTP_STATUS httpStatus;

	public String contentType;

    private AsyncStrategy asyncStrategy;
    private final Object asyncLock = new Object();

	private final TemplateEngineManager templateEngineManager;

	private final BodyParserEngineManager bodyParserEngineManager;

	private final FlashCookie flashCookie;

	private final SessionCookie sessionCookie;

	private final Logger logger;

	@Inject
	public ContextImpl(
			BodyParserEngineManager bodyParserEngineManager,
			FlashCookie flashCookie,
			Logger logger,
			SessionCookie sessionCookie,
			TemplateEngineManager templateEngineManager) {

		this.bodyParserEngineManager = bodyParserEngineManager;
		this.flashCookie = flashCookie;
		this.logger = logger;
		this.sessionCookie = sessionCookie;
		this.templateEngineManager = templateEngineManager;

		this.httpStatus = HTTP_STATUS.ok200;
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
	public Context template(String explicitTemplateName) {
		this.templateOverride = explicitTemplateName;
		return this;
	}

	@Override
	public Context status(HTTP_STATUS httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	@Override
	public String getPathParameter(String key) {
		return route.getParameters(httpServletRequest.getServletPath())
				.get(key);
	}

    @Override
    public Integer getPathParameterNumeric(String key) {
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
	public Integer getParameterNumeric(String key, Integer defaultValue) {
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
        return CookieHelper.getCookieValue(name, httpServletRequest.getCookies());
    }

    @Override
    public void addCookie(ninja.Cookie cookie) {
        httpServletResponse.addCookie(CookieHelper.convertNinjaCookieToServletCookie(cookie));
    }

    @Override
    public void unsetCookie(String name) {
        httpServletResponse.addCookie(new Cookie(name, null));
    }

    @Override
	public void redirect(String url) {

		try {
			httpServletResponse.sendRedirect(url);
		} catch (IOException e) {
			logger.error("Error while calling redirect on the context", e);
		}

	}

	@Override
	public void render() {

		render(null);

	}

	@Override
	public void render(Object object) {

		finalizeResponseHeaders(contentType);

        if (contentType != null) {
            TemplateEngine templateEngine = templateEngineManager
                    .getTemplateEngineForContentType(contentType);

            if (templateEngine == null) {
                if (object instanceof String) {
                    // Simply write it out
                    try {
                        getWriter().write((String) object);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (object instanceof byte[]) {
                    // Simply write it out
                    try {
                        getOutputStream().write((byte[]) object);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                throw new IllegalArgumentException("No template engine found for content type " + contentType);
            }

            templateEngine.invoke(this, object);
        }
	}

	@Override
	public void renderHtml() {
		renderHtml(new HashMap<String, String>());
	}

	@Override
	public void renderHtml(Object object) {
		finalizeResponseHeaders(ContentTypes.TEXT_HTML);

		TemplateEngine templateEngine = templateEngineManager
				.getTemplateEngineForContentType(ContentTypes.TEXT_HTML);

		templateEngine.invoke(this, object);

	}

	@Override
	public void renderJson(Object object) {

		finalizeResponseHeaders(ContentTypes.APPLICATION_JSON);

		TemplateEngine templateEngine = templateEngineManager
				.getTemplateEngineForContentType(ContentTypes.APPLICATION_JSON);

		templateEngine.invoke(this, object);


	}

	private void finalizeResponseHeaders(String contentType) {
        if (contentType != null) {
            setContentType(contentType);
        }
		setStatusOnResponse(httpStatus);

		flashCookie.save(this);
		sessionCookie.save(this);

	}

	/**
	 * set status on response finally...
	 * 
	 * @param httpStatus
	 */
	private void setStatusOnResponse(HTTP_STATUS httpStatus) {

		if (httpStatus.equals(HTTP_STATUS.ok200)) {
			httpServletResponse.setStatus(200);
		} else if (httpStatus.equals(HTTP_STATUS.notFound404)) {
			httpServletResponse.setStatus(404);
		} else if (httpStatus.equals(HTTP_STATUS.forbidden403)) {
			httpServletResponse.setStatus(403);
		} else if (httpStatus.equals(HTTP_STATUS.teapot418)) {
			httpServletResponse.setStatus(418);
		} else if (httpStatus.equals(HTTP_STATUS.badRequest400)) {
			httpServletResponse.setStatus(400);
		} else if (httpStatus.equals(HTTP_STATUS.noContent204)) {
			httpServletResponse.setStatus(204);
		}

	}

	@Override
	public void setContentType(String contentType) {
	    this.contentType = contentType;
		httpServletResponse.setContentType(contentType);

	}

	@Override
	public String getTemplateName(String suffix) {
        if (templateOverride == null) {
            Class controller = route.getControllerClass();

            // Calculate the correct path of the template.
            // We always assume the template in the subdir "views"

            // 1) If we are in the main project => /views/ControllerName/templateName.ftl.html
            // 2) If we are in a plugin / subproject
            //    => some/packages/submoduleName/views/ControllerName/templateName.ftl.html

            // So let's calculate the parent package of the controller:
            String controllerPackageName = controller.getPackage().getName();
            // This results in something like controllers or some.package.controllers

            // Let's remove "controllers" so we cat all parent packages:
            String parentPackageOfController = controllerPackageName.replaceAll(NinjaConstant.CONTROLLERS_DIR, "");

            // And now we rewrite everything from "." notation to directories /
            String parentControllerPackageAsPath = parentPackageOfController.replaceAll("\\.", "/");


            // and the final path of the controller will be something like:
            // some/package/views/ControllerName/templateName.ftl.html
            return String.format("%sviews/%s/%s%s", parentControllerPackageAsPath, controller
                    .getSimpleName(), route.getControllerMethod().getName(), suffix);
        }
		return templateOverride;
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
    public String getRequestUri() {
        return getHttpServletRequest().getRequestURI();
    }

    @Override
    public void handleAsync() {
        synchronized (asyncLock) {
            if (asyncStrategy == null) {
                asyncStrategy = AsyncStrategyFactoryHolder.INSTANCE.createStrategy(httpServletRequest);
                asyncStrategy.handleAsync();
            }
        }
    }

    @Override
    public void requestComplete() {
        synchronized (asyncLock) {
            if (asyncStrategy == null) {
                throw new IllegalStateException("Request complete called on non async request");
            }
            asyncStrategy.requestComplete();
        }
    }

    /**
     * Used to indicate that the controller has finished executing
     */
    public void controllerReturned() {
        synchronized (asyncLock) {
            if (asyncStrategy != null) {
                asyncStrategy.controllerReturned();
            }
        }
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
    public OutputStream getOutputStream() throws IOException {
        return httpServletResponse.getOutputStream();
    }

    @Override
    public Writer getWriter() throws IOException {
        return httpServletResponse.getWriter();
    }
}
