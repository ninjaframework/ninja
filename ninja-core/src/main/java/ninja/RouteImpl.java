package ninja;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Injector;

@SuppressWarnings("rawtypes")
public class RouteImpl implements Route {

	public enum HTTP_METHOD {
		GET, POST, PUT, DELETE, OPTION
	}

	private HTTP_METHOD http_method;

	private String uri;

	private List<UriTokenPart> parts;

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

		if (uri.equals(this.uri)) {
			return true;
		}

		String[] givenParts = uri.split("/");

		if (parts.size() != givenParts.length) {
			return false;
		}
		// Scan through the Url to check if all corresponding
		// parts match.
		for (int i = 0; i < givenParts.length; i++) {
			if (!parts.get(i).matches(givenParts[i])) {
				return false;
			}
		}

		return true;
	}

	// /////////////////////////////////////////////////////////////////////////
	// support for nicely parsing and comparing routes
	// like /person/{name}/{john}
	// Idea from James' implementation at gwt-platform project
	// /////////////////////////////////////////////////////////////////////////

	private interface UriTokenPart {
		boolean matches(String part);

		String getParameterName();
	}

	private static class ParameterizedUriPart implements UriTokenPart {
		private final String parameterName;

		private ParameterizedUriPart(String parameterName) {
			this.parameterName = parameterName;
		}

		/**
		 * As this is a dynamic part everything is allowed to match.
		 * 
		 * For instance a route like: /user/{userId}/dashboard matches both: -
		 * /user/sara/dashboard - /user/bob/dashboard
		 */
		@Override
		public boolean matches(String part) {
			return true;
		}

		@Override
		public String getParameterName() {
			return parameterName;
		}
	}

	private static class StaticUriTokenPart implements UriTokenPart {
		private final String partName;

		private StaticUriTokenPart(String partName) {
			this.partName = partName;
		}

		@Override
		public boolean matches(String part) {
			return partName.equals(part);
		}

		@Override
		public String getParameterName() {
			return null;
		}
	}

	private static List<UriTokenPart> parseUriToken(String uri) {
		List<UriTokenPart> parts = new ArrayList<UriTokenPart>();
		for (String part : uri.split("/")) {
			if (part.matches("\\{.*\\}")) {
				String parameterName = part.substring(1, part.length() - 1);
				parts.add(new ParameterizedUriPart(parameterName));
			} else {
				parts.add(new StaticUriTokenPart(part));
			}
		}

		return parts;
	}

	@Override
	public Map<String, String> getParameters(String uri) {

		Map<String, String> map = new HashMap<String, String>();

		String[] givenParts = uri.split("/");

		// Store all parameters.
		for (int i = 0; i < givenParts.length; i++) {
			String parameterName = parts.get(i).getParameterName();
			// Store if this part is a parameter meaning != null.
			if (parameterName != null) {
				map.put(parameterName, givenParts[i]);
			}
		}

		return map;

	}

	@Override
	public Route route(String uri) {
		// init raw uri and the parts for matching:
		this.uri = uri;
		this.parts = parseUriToken(uri);

		return this;
	}

}
