package ninja;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A route
 */
public class Route {
    private final Injector injector;
    private final String httpMethod;
    private final String uri;
    private final Class controllerClass;
    private final Method controllerMethod;
    private final List<Class<? extends Filter>> filters;

    private final List<String> parameterNames;
    private final Pattern regex;

    public Route(Injector injector, String httpMethod, String uri, Class controllerClass, Method controllerMethod,
            List<Class<? extends Filter>> filters) {
        this.injector = injector;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.controllerClass = controllerClass;
        this.controllerMethod = controllerMethod;
        this.filters = ImmutableList.copyOf(filters);

        parameterNames = ImmutableList.copyOf(doParseParameters(uri));
        regex = Pattern.compile(convertRawUriToRegex(uri));
    }

    public String getUrl() {
        return uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public Class getControllerClass() {
        return controllerClass;
    }

    public List<Class<? extends Filter>> getFilters() {
        return filters;
    }

    public Method getControllerMethod() {
        return controllerMethod;
    }

    public void invoke(Context context) {
        try {

            Object applicationController = injector.getInstance(controllerClass);
            controllerMethod.invoke(applicationController, context);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        }

    }

    /**
     * matches /index to /index or /me/1 to /person/{id}
     *
     */
    public boolean matches(String httpMethod, String uri) {
        if (this.httpMethod.equalsIgnoreCase(httpMethod)) {
            Matcher matcher = regex.matcher(uri);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public Map<String, String> getParameters(String uri) {

        Map<String, String> map = new HashMap<String, String>();

        Matcher m = regex.matcher(uri);

        if (m.matches()) {
            for (int i = 1; i < m.groupCount() + 1; i++) {
                map.put(parameterNames.get(i - 1), m.group(i));
            }
        }

        return map;

    }

    private static List<String> doParseParameters(String rawRoute) {
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
     * @return The regex
     */
    private static String convertRawUriToRegex(String rawUri) {
        return rawUri.replaceAll("\\{.*?\\}", "(.*?)");
    }
}
