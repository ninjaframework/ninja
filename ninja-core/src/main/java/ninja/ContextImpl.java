  package ninja;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;

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

	private final FlashCookie flashCookie;

	private final SessionCookie sessionCookie;

	@Inject
	public ContextImpl(BodyParserEngineManager bodyParserEngineManager,
			FlashCookie flashCookie, Router router,
			SessionCookie sessionCookie,
			TemplateEngineManager templateEngineManager) {

		this.bodyParserEngineManager = bodyParserEngineManager;
		this.flashCookie = flashCookie;
		this.router = router;
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
		Route route = router.getRouteFor(httpServletRequest.getMethod(),
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

		finalizeResponseHeaders(contentType);

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
		setContentType(contentType);
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

	@Override
	public FlashCookie getFlashCookie() {
		return flashCookie;
	}

	@Override
	public SessionCookie getSessionCookie() {
		return sessionCookie;
	}

}
