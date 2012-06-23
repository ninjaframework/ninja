package ninja;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;

import com.google.inject.Inject;

public class ContextImpl implements Context {

	private HttpServletRequest httpServletRequest;

	private HttpServletResponse httpServletResponse;

	private Router router;

	// * if set this template is used. otherwise the default mapping **/
	private String templateName = null;

	private HTTP_STATUS httpStatus;

	public String contentType;

	private final TemplateEngineManager templateEngineManager;

	private final BodyParserEngineManager bodyParserEngineManager;

	@Inject
	public ContextImpl(Router router,
	                   TemplateEngineManager templateEngineManager,
	                   BodyParserEngineManager bodyParserEngineManager) {

		this.router = router;
		this.templateEngineManager = templateEngineManager;
		this.bodyParserEngineManager = bodyParserEngineManager;

		this.httpStatus = HTTP_STATUS.ok200;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;

	}

	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;

	}

	public void setHttServletResponse(HttpServletResponse httServletResponse) {
		this.httpServletResponse = httServletResponse;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return this.httpServletResponse;
	}

	@Override
	public Context template(String explicitTemplateName) {
		this.templateName = explicitTemplateName;
		return this;
	}

	@Override
	public Context status(HTTP_STATUS httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	@Override
	public String getPathParameter(String key) {

		// FIXME: not really efficient...
		Route route = router.getRouteFor(
				httpServletRequest.getMethod(),
				httpServletRequest.getServletPath());

		return route.getParameters(httpServletRequest.getServletPath())
		        .get(key);

	}

	@Override
	public void redirect(String url) {

		try {
			httpServletResponse.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void render() {

		render(null);

	}

	@Override
	public void render(Object object) {

		setContentType(contentType);
		setStatusOnResponse(httpStatus);

		TemplateEngine templateEngine = templateEngineManager
		        .getTemplateEngineForContentType(contentType);

		templateEngine.invoke(this, object);

	}

	@Override
	public void renderHtml() {
		renderHtml(new HashMap<String, String>());
	}

	@Override
	public void renderHtml(Object object) {
		setContentType(ContentTypes.TEXT_HTML);
		setStatusOnResponse(httpStatus);

		TemplateEngine templateEngine = templateEngineManager
		        .getTemplateEngineForContentType(ContentTypes.TEXT_HTML);

		templateEngine.invoke(this, object);

	}

	@Override
	public void renderJson(Object object) {

		setContentType(ContentTypes.APPLICATION_JSON);
		setStatusOnResponse(httpStatus);

		TemplateEngine templateEngine = templateEngineManager
		        .getTemplateEngineForContentType(ContentTypes.APPLICATION_JSON);

		templateEngine.invoke(this, object);

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
		}

	}

	@Override
	public void setContentType(String contentType) {
		httpServletResponse.setContentType(contentType);

	}

	@Override
	public String getTemplateName() {
		return templateName;
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

}
