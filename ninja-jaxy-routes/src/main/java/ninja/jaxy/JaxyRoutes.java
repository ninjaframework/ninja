/**
 * Copyright (C) 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.jaxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implementation of a JAX-RS style route builder.
 *
 * @author James Moger
 *
 */
@Singleton
public class JaxyRoutes implements ApplicationRoutes {

    final static Logger logger = LoggerFactory.getLogger(JaxyRoutes.class);

    final NinjaProperties ninjaProperties;

    final NinjaMode runtimeMode;

    @Inject
    public JaxyRoutes(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;

        if (ninjaProperties.isDev()) {
            runtimeMode = NinjaMode.dev;
        } else if (ninjaProperties.isTest()) {
            runtimeMode = NinjaMode.test;
        } else {
            runtimeMode = NinjaMode.prod;
        }

    }

    /**
     * Scans, identifies, and registers annotated controller methods for the
     * current runtime settings.
     *
     * @param router
     */
    @Override
    public void init(Router router) {

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Set<URL> packagesToScan = getPackagesToScanForRoutes();
        builder.addUrls(packagesToScan);

        builder.addScanners(new MethodAnnotationsScanner());
        Reflections reflections = new Reflections(builder);

        // collect the allowed annotated methods
        // order them to be sure not abstract ones are managed first
        List<Method> methods = Lists.newArrayList(reflections.getMethodsAnnotatedWith(Path.class));
        Collections.sort(methods, new Comparator<Method>() {

            @Override
            public int compare(Method m1, Method m2) {
                boolean m1abstract = Modifier.isAbstract(m1.getDeclaringClass().getModifiers());
                boolean m2abstract = Modifier.isAbstract(m2.getDeclaringClass().getModifiers());
                
                if(m1abstract && !m2abstract) {
                    return 1;
                } else if(!m1abstract && m2abstract) {
                    return -1;
                } else if(m1abstract && m2abstract) {
                    if(m1.getDeclaringClass().isAssignableFrom(m2.getDeclaringClass())) {
                        return 1;
                    } else if(m2.getDeclaringClass().isAssignableFrom(m1.getDeclaringClass())) {
                        return -1;
                    }
                }
                return 0;
            }
        });
        
        // compute annotated method path and http method
        Map<Method, Path> methodsPath = Maps.newHashMap();
        Map<Method, String> methodsHttp = Maps.newHashMap();
        List<Method> notAllowedMethods = Lists.newArrayList();
        
        for (Method method : methods) {

            if(Modifier.isAbstract(method.getDeclaringClass().getModifiers())) {
                // for an abstract controller, get the same method but for all real implementation
            	Set<?> subTypes = reflections.getSubTypesOf(method.getDeclaringClass());
            	
            	for(Object subtype : subTypes) {
            	    // skip nested asbtract subtypes
            	    if(Modifier.isAbstract(((Class<?>) subtype).getModifiers())) {
            	        continue;
            	    }
            	    
        	        try {
        	            // add the method from the implemented controller to our todo list
        	            Method implementedMethod = ((Class<?>) subtype).getMethod(method.getName(), method.getParameterTypes());
        	            
        	            if (!methodsPath.containsKey(implementedMethod) && !notAllowedMethods.contains(implementedMethod) && allowMethod(method)) {            	            
            	            // store Path and HttpMethod annotations for the method (will not be available later by reflections)
            	            methodsPath.put(implementedMethod, method.getAnnotation(Path.class));
            	            methodsHttp.put(implementedMethod, getHttpMethod(method, true));
            	        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                        continue;
                    }
            	}
            } else if (allowMethod(method)) {
                // add the method to our todo list
                methodsPath.put(method, method.getAnnotation(Path.class));
                methodsHttp.put(method, getHttpMethod(method, true));
            } else {
                notAllowedMethods.add(method);
            }

        }

        if (methodsPath.isEmpty()) {
            // nothing to do
            return;
        }
        
        // collect controller path for all the methods
        Map<Class<?>, Set<String>> controllers = Maps.newHashMap();
        for (Method method : methodsPath.keySet()) {
            if (!controllers.containsKey(method.getDeclaringClass())) {
                controllers.put(method.getDeclaringClass(), collectPaths(method.getDeclaringClass()));
            }
        }

        List<Method> methodsList = Lists.newArrayList(methodsPath.keySet());
        // Sort the methods into registration order
        Collections.sort(methodsList, new Comparator<Method>() {

            @Override
            public int compare(Method m1, Method m2) {
                int o1 = Integer.MAX_VALUE;
                if (m1.isAnnotationPresent(Order.class)) {
                    Order order = m1.getAnnotation(Order.class);
                    o1 = order.value();
                }

                int o2 = Integer.MAX_VALUE;
                if (m2.isAnnotationPresent(Order.class)) {
                    Order order = m2.getAnnotation(Order.class);
                    o2 = order.value();
                }

                if (o1 == o2) {
                    // same or unsorted, compare controller+method
                    String s1 = m1.getDeclaringClass().getName() + "."
                            + m1.getName();
                    String s2 = m2.getDeclaringClass().getName() + "."
                            + m2.getName();
                    return s1.compareTo(s2);
                }

                if (o1 < o2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        // register routes for all the methods
        for (Method method : methodsList) {

            final Class<?> controllerClass = method.getDeclaringClass();
            final Path methodPath = methodsPath.get(method);
            final String httpMethod = methodsHttp.get(method);
            final Set<String> controllerPaths = controllers
                    .get(controllerClass);

            for (String controllerPath : controllerPaths) {

                for (String methodPathSpec : methodPath.value()) {
                    
                    final String fullPath = controllerPath + methodPathSpec;
                    final String methodName = method.getName();

                    router.METHOD(httpMethod).route(fullPath)
                            .with(controllerClass, methodName);

                }

            }

        }

    }

    /**
     * Recursively builds the paths for the controller class.
     *
     * @param controllerClass
     * @return the paths for the controller
     */
    private Set<String> collectPaths(Class<?> controllerClass) {
        Set<String> parentPaths = Collections.emptySet();
        if (controllerClass.getSuperclass() != null) {
            parentPaths = collectPaths(controllerClass.getSuperclass());
        }

        Set<String> paths = Sets.newLinkedHashSet();
        Path controllerPath = controllerClass.getAnnotation(Path.class);

        if (controllerPath != null) {

            if (parentPaths.isEmpty()) {

                // add all controller paths
                paths.addAll(Arrays.asList(controllerPath.value()));

            } else {

                // create controller paths based on the parent paths
                for (String parentPath : parentPaths) {

                    for (String path : controllerPath.value()) {
                        paths.add(parentPath + path);
                    }

                }

            }

        } else {
            // add all parent paths
            paths.addAll(parentPaths);
        }

        return paths;
    }

    /**
     * Returns the set of packages to scan for annotated controller methods.
     *
     * @return the set of packages to scan
     */
    private Set<URL> getPackagesToScanForRoutes() {

        Set<URL> packagesToScanForRoutes = Sets.newHashSet();

        packagesToScanForRoutes.addAll(ClasspathHelper
                .forPackage(NinjaConstant.CONTROLLERS_DIR));

        return packagesToScanForRoutes;

    }

    /**
     * Determines if this method may be registered as a route. Ninja properties
     * are considered as well as runtime modes.
     *
     * @param method
     * @return true if the method can be registered as a route
     */
    private boolean allowMethod(Method method) {

        // NinjaProperties-based route exclusions/inclusions
        if (method.isAnnotationPresent(Requires.class)) {
            String key = method.getAnnotation(Requires.class).value();
            String value = ninjaProperties.get(key);
            if (value == null) {
                return false;
            }
        }

        // NinjaMode-based route exclusions/inclusions
        Set<NinjaMode> modes = Sets.newTreeSet();
        for (Annotation annotation : method.getAnnotations()) {

            Class<? extends Annotation> annotationClass = annotation
                    .annotationType();

            if (annotationClass.isAnnotationPresent(RuntimeMode.class)) {

                RuntimeMode mode = annotationClass
                        .getAnnotation(RuntimeMode.class);
                modes.add(mode.value());

            }
        }

        return modes.isEmpty() || modes.contains(runtimeMode);
    }

    /**
     * Returns the HTTP method for the controller method. Defaults to GET if
     * unspecified.
     *
     * @param method
     * @return the http method for this controller method
     */
    private String getHttpMethod(Method method, boolean useDefault) {

        for (Annotation annotation : method.getAnnotations()) {

            Class<? extends Annotation> annotationClass = annotation
                    .annotationType();

            if (annotationClass.isAnnotationPresent(HttpMethod.class)) {
                HttpMethod httpMethod = annotationClass
                        .getAnnotation(HttpMethod.class);
                return httpMethod.value();
            }

        }

        if(useDefault) {
            // default to GET
            logger.info(String
                    .format("%s.%s does not specify an HTTP method annotation! Defaulting to GET.",
                            method.getClass().getName(), method.getName()));
    
            return HttpMethod.GET;
        } else {
            return null;
        }
    }

}
