package ninja;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class RouteImpl implements Route {

	private String httpMethod;

	private String uri;

	private Class controller;

	private String controllerMethod;

	private Injector injector;

	private final Logger logger;

	@Inject
	public RouteImpl(Injector injector, Logger logger) {
		this.injector = injector;
		this.logger = logger;
	}

	@Override
	public Route GET() {
		httpMethod = "GET";
		return this;
	}

	@Override
	public Route POST() {
		httpMethod = "POST";
		return this;
	}

	@Override
	public Route PUT() {
		httpMethod = "PUT";
		return this;
	}

	@Override
	public Route DELETE() {
		httpMethod = "DELETE";
		return this;
	}

	@Override
	public Route OPTION() {
		httpMethod = "OPTION";
		return this;
	}

	@Override
	public void with(Class controller, String controllerMethod) {

		this.controller = controller;
		this.controllerMethod = controllerMethod;

		verifyThatControllerAndMethodExists(controller, controllerMethod);

	}

	@Override
	public String getUrl() {

		return uri;
	}

	@Override
	public Class getController() {
		return controller;
	}

	@Override
	public String getControllerMethod() {
		return controllerMethod;
	}

	@Override
	public void invoke(Context context) {

		Object applicationController = null;
		try {

			applicationController = injector.getInstance(controller);

			Method method = applicationController.getClass().getMethod(
					controllerMethod, Context.class);
			method.invoke(applicationController, context);

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * matches /index to /index or /me/1 to /person/{id}
	 * 
	 */
	@Override
	public boolean matches(String httpMethod, String uri) {

		if (this.httpMethod.equalsIgnoreCase(httpMethod)) {

			Pattern pattern = Pattern.compile(convertRawUriToRegex(this.uri));
			Matcher matcher = pattern.matcher(uri);

			return matcher.matches();
		} else {
			return false;
		}

	}

	@Override
	public Map<String, String> getParameters(String uri) {

		List<String> parameterNames = doParseParameters(this.uri);

		Map<String, String> map = new HashMap<String, String>();

		String rawRouteAsRegex = convertRawUriToRegex(this.uri);
		Pattern p = Pattern.compile(rawRouteAsRegex);
		Matcher m = p.matcher(uri);

		if (m.matches()) {
			for (int i = 1; i < m.groupCount() + 1; i++) {
				map.put(parameterNames.get(i - 1), m.group(i));
			}
		}

		return map;

	}

	@Override
	public Route route(String uri) {
		// init raw uri and the parts for matching:
		this.uri = uri;

		return this;
	}

	public List<String> doParseParameters(String rawRoute) {

		List<String> list = new ArrayList<String>();

		Pattern p = Pattern.compile("\\{(.*?)\\}");
		Matcher m = p.matcher(rawRoute);

		while (m.find()) {
			list.add(m.group(1));
		}

		return list;

	}

	/**
	 * Gets a raw uri like /{name}/id/* and returns /(.*)/id/*
	 * 
	 * @return
	 */
	public String convertRawUriToRegex(String rawUri) {

		String result = rawUri.replaceAll("\\{.*?\\}", "(.*?)");
		return result;

	}

	/**
	 * Routes are usually defined in conf/Routes.java as
	 * router.GET().route("/teapot").with(FilterController.class, "teapot");
	 * 
	 * Unfortunately "teapot" is not checked by the compiler. We do that here at
	 * runtime.
	 * 
	 * We are reloading when there are changes. So this is almost as good as
	 * compile time checking.
	 * 
	 * @param route
	 */
	private void verifyThatControllerAndMethodExists(Class controller,
			String controllerMethod) {

		try {

			controller.getMethod(controllerMethod, Context.class);

		} catch (SecurityException e) {
			logger.error("Error while checking for valid Controller / controllerMethod combination", e);
		} catch (NoSuchMethodException e) {
			
			logger.error("Error in route configuration!!!");
			logger.error("Can not find Controller " + controller.getName() + " and method " + controllerMethod);
		}

	}

}
