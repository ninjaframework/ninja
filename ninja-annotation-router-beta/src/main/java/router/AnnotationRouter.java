/*
 * Copyright 2014 ra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package router;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AnnotationRouter implements ApplicationRoutes {

	final static String ANNOTATION_ROUTER_PACKAGES = "annotation_router.packages";

	final static Logger logger = LoggerFactory.getLogger(AnnotationRouter.class);

	final static int MODE_ALL = 0b000;

	final static int MODE_PROD = 0b001;

	final static int MODE_DEV = 0b010;

	final static int MODE_TEST = 0b100;

	final NinjaProperties ninjaProperties;

	final int runtimeMode;

	@Inject
	public AnnotationRouter(NinjaProperties ninjaProperties) {
		this.ninjaProperties = ninjaProperties;

		if (ninjaProperties.isProd()) {
			runtimeMode = MODE_PROD;
		} else if (ninjaProperties.isDev()) {
			runtimeMode = MODE_DEV;
		} else if (ninjaProperties.isTest()) {
			runtimeMode = MODE_TEST;
		} else {
			runtimeMode = MODE_ALL;
		}
	}

	/**
	 * Method to scan for the Route annotation and add the routes to the router.
	 *
	 * @param router
	 *            at add the routes to
	 */
	@Override
	public void init(Router router) {

		ConfigurationBuilder builder = new ConfigurationBuilder();

		Set<URL> packagesToScan = getPackagesToScanForRoutes(ninjaProperties);
		builder.addUrls(packagesToScan);

		builder.addScanners(new MethodAnnotationsScanner());
		Reflections reflections = new Reflections(builder);

		Set<Method> methods = reflections.getMethodsAnnotatedWith(Path.class);

		if (!methods.isEmpty()) {
			// @Path style
			logger.info("Generating {} routes.", methods.size());

			Map<Class<?>, String[]> controllers = Maps.newHashMap();

			for (Method method : methods) {
				if (!allowMethod(method)) {
					continue;
				}

				Path path = method.getAnnotation(Path.class);
				final Class<?> declaringClass = method.getDeclaringClass();
				if (!controllers.containsKey(declaringClass)) {
					Path controllerPath = declaringClass.getAnnotation(Path.class);
					if (controllerPath != null) {
						controllers.put(declaringClass, controllerPath.value());
					} else {
						controllers.put(declaringClass, new String [] { "" });
					}
				}

				final String [] controllerPaths = controllers.get(declaringClass);
				for (String controllerPath : controllerPaths) {
					for (String pathSpec : path.value()) {
						final String httpMethod = getHttpMethod(method);
						final String fullPath = controllerPath + pathSpec;
						final String methodName = method.getName();

						if (httpMethod == null) {
							throw new IllegalArgumentException(String.format(
									"%s.%s does not specify an HTTP method annotation!",
									declaringClass.getName(), methodName));
						}

						router.METHOD(httpMethod).route(fullPath).with(declaringClass, methodName);
					}
				}
			}
		}
	}

	private Set<URL> getPackagesToScanForRoutes(NinjaProperties ninjaProperties) {

		Set<URL> packagesToScanForRoutes = Sets.newHashSet();

		String[] packagesDefinedByUserOrNull = ninjaProperties.getStringArray(ANNOTATION_ROUTER_PACKAGES);

		if (packagesDefinedByUserOrNull != null) {

			for (String packageDefinedByUser : packagesDefinedByUserOrNull) {
				packagesToScanForRoutes.addAll(ClasspathHelper.forPackage(packageDefinedByUser));
			}

		} else {

			packagesToScanForRoutes.addAll(ClasspathHelper.forPackage(NinjaConstant.CONTROLLERS_DIR));

		}

		return packagesToScanForRoutes;

	}

	/**
	 * Determines if this method may be registered as a route for the current
	 * runtime mode. A method may be annotated to run in multiple modes.
	 *
	 * @param method
	 * @return true if the method can be registered as a route
	 */
	private boolean allowMethod(Method method) {

		int mode = MODE_ALL;
		if (method.isAnnotationPresent(Dev.class)) {
			mode |= MODE_DEV;
		}
		if (method.isAnnotationPresent(Test.class)) {
			mode |= MODE_TEST;
		}
		if (method.isAnnotationPresent(Prod.class)) {
			mode |= MODE_PROD;
		}

		return (mode == MODE_ALL) || (mode & runtimeMode) > 0;
	}

	private String getHttpMethod(Method method) {
		for (Annotation annotation : method.getAnnotations()) {
			Class<? extends Annotation> annotationClass = annotation.annotationType();
			if (annotationClass.isAnnotationPresent(HttpMethod.class)) {
				HttpMethod httpMethod = annotationClass.getAnnotation(HttpMethod.class);
				return httpMethod.value();
			}
		}
		return null;
	}

}
