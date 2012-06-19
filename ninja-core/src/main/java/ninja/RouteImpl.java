package ninja;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class RouteImpl implements Route {

	public enum HTTP_METHOD {
		GET, POST, PUT, DELETE, OPTION
	}

	private HTTP_METHOD http_method;

	private String uri;

	private Class controller;

	private String controllerMethod;

	private Injector injector;

	@Inject
	public RouteImpl(Injector injector) {
		this.injector = injector;
	}

	@Override
	public Route GET() {
		http_method = HTTP_METHOD.GET;
		return this;
	}

	@Override
	public Route POST() {
		http_method = HTTP_METHOD.POST;
		return this;
	}

	@Override
	public Route PUT() {
		http_method = HTTP_METHOD.PUT;
		return this;
	}

	@Override
	public Route DELETE() {
		http_method = HTTP_METHOD.DELETE;
		return this;
	}

	@Override
	public Route OPTION() {
		http_method = HTTP_METHOD.OPTION;
		return this;
	}

	@Override
	public void with(Class controller, String controllerMethod) {

		this.controller = controller;

		this.controllerMethod = controllerMethod;

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
	public boolean matches(String uri) {

		Pattern pattern = Pattern.compile(convertRawUriToRegex(this.uri));
		Matcher matcher = pattern.matcher(uri);

		return matcher.matches();

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

}
