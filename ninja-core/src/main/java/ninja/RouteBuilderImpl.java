/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import ninja.params.ControllerMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import java.util.List;
import java.util.Optional;
import ninja.ControllerMethods.ControllerMethod;
import ninja.utils.LambdaRoute;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaProperties;
import ninja.utils.SwissKnife;
import ninja.application.ApplicationFilters;

public class RouteBuilderImpl implements RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(RouteBuilder.class);
    
    protected final static String GLOBAL_FILTERS_DEFAULT_LOCATION = "conf.Filters";

    private String httpMethod;
    private String uri;
    private Method functionalMethod;                // lamda routes can have this + maybe impl and targets
    private Optional<Method> implementationMethod;  // method to use for parameter/annotation extraction
    private Optional<Object> targetObject;          // instance to invoke
    private final NinjaProperties ninjaProperties;
    private Optional<List<Class<? extends Filter>>> globalFiltersOptional;
    private final List<Class<? extends Filter>> localFilters;
    private final NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;

    @Inject
    public RouteBuilderImpl(
            NinjaProperties ninjaProperties,
            NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver) {
        this.implementationMethod = Optional.empty();
        this.targetObject = Optional.empty();
        this.ninjaProperties = ninjaProperties;
        this.ninjaBaseDirectoryResolver = ninjaBaseDirectoryResolver;
        this.globalFiltersOptional = Optional.empty();
        this.localFilters = Lists.newArrayList();
    }
    
    public RouteBuilderImpl GET() {
        httpMethod = Route.HTTP_METHOD_GET;
        return this;
    }

    public RouteBuilderImpl POST() {
        httpMethod = Route.HTTP_METHOD_POST;
        return this;
    }

    public RouteBuilderImpl PUT() {
        httpMethod = Route.HTTP_METHOD_PUT;
        return this;
    }

    public RouteBuilderImpl DELETE() {
        httpMethod = Route.HTTP_METHOD_DELETE;
        return this;
    }

    public RouteBuilderImpl OPTIONS() {
        httpMethod = Route.HTTP_METHOD_OPTIONS;
        return this;
    }
    
    public RouteBuilderImpl HEAD() {
        httpMethod = Route.HTTP_METHOD_HEAD;
        return this;
    }
    
    public RouteBuilderImpl WS() {
        httpMethod = Route.HTTP_METHOD_WEBSOCKET;
        return this;
    }
        
    public RouteBuilderImpl METHOD(String method) {
        httpMethod = method;
        return this;
    }

    @Override
    public void with(Class<?> controllerClass, String controllerMethod) {
        this.functionalMethod
            = verifyControllerMethod(controllerClass, controllerMethod);
    }
    
    @Override @Deprecated
    public void with(MethodReference methodRef) {
        with(methodRef.getDeclaringClass(), methodRef.getMethodName());
    }
    
    @Override @Deprecated
    public void with(final Result result) {
        with(ControllerMethods.of(() -> result));
    }
    
    @Override
    public Void with(ControllerMethod controllerMethod) {
        LambdaRoute lambdaRoute = LambdaRoute.resolve(controllerMethod);
        this.functionalMethod = lambdaRoute.getFunctionalMethod();
        this.implementationMethod = lambdaRoute.getImplementationMethod();
        this.targetObject = lambdaRoute.getTargetObject();
        return null;
    }
    
    @Override
    public RouteBuilder globalFilters(List<Class<? extends Filter>> filtersToAdd) {
        this.globalFiltersOptional = Optional.of(filtersToAdd);
        return this;
    }
    
    @Override
    public RouteBuilder globalFilters(Class<? extends Filter> ... filtersToAdd) {
        List<Class<? extends Filter>> globalFiltersTemp = Lists.newArrayList(filtersToAdd);
        globalFilters(globalFiltersTemp);
        return this;
    }
    
    @Override
    public RouteBuilder filters(List<Class<? extends Filter>> filtersToAdd) {
        this.localFilters.addAll(filtersToAdd);
        return this;
    }
    
    @Override
    public RouteBuilder filters(Class<? extends Filter> ... filtersToAdd) {
        List<Class<? extends Filter>> filtersTemp = Lists.newArrayList(filtersToAdd);
        filters(filtersTemp);
        return this;
    }

    @Override
    public RouteBuilder route(String uri) {
        this.uri = uri;
        return this;
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
     * @param controller
     *            The controller class
     * @param controllerMethod
     *            The method
     * @return The actual method
     */
    private Method verifyControllerMethod(Class<?> controllerClass,
                                          String controllerMethod) {
        try {
            Method methodFromQueryingClass = null;

            // 1. Make sure method is in class
            // 2. Make sure only one method is there. Otherwise we cannot really
            // know what to do with the parameters.
            for (Method method : controllerClass.getMethods()) {
                if (method.getName().equals(controllerMethod)) {
                    if (methodFromQueryingClass == null) {
                        methodFromQueryingClass = method;
                    } else {
                        throw new NoSuchMethodException();
                    }
                }
            }

            if (methodFromQueryingClass == null) {
                throw new NoSuchMethodException();
            }

            // make sure that the return type of that controller method
            // is of type Result.
            if (Result.class.isAssignableFrom(methodFromQueryingClass.getReturnType())) {
                return methodFromQueryingClass;
            } else {
                throw new NoSuchMethodException();
            }

        } catch (SecurityException e) {
            log.error(
                    "Error while checking for valid Controller / controllerMethod combination",
                    e);
        } catch (NoSuchMethodException e) {
            log.error("Error in route configuration!!!");
            log.error("Can not find Controller " + controllerClass.getName()
                    + " and method " + controllerMethod);
            log.error("Hint: make sure the controller returns a ninja.Result!");
            log.error("Hint: Ninja does not allow more than one method with the same name!");
        }
        return null;
    }

    /**
     * Build the route.
     * @param injector The injector to build the route with
     * @return The built route
     */
    public Route buildRoute(Injector injector) {
        if (functionalMethod == null) {
            log.error("Error in route configuration for {}", uri);
            throw new IllegalStateException("Route missing a controller method");
        }
        
        // Calculate filters
        LinkedList<Class<? extends Filter>> allFilters = new LinkedList<>();
        
        allFilters.addAll(calculateGlobalFilters(this.globalFiltersOptional, injector));

        allFilters.addAll(this.localFilters);
        
        allFilters.addAll(calculateFiltersForClass(functionalMethod.getDeclaringClass()));
        FilterWith filterWith = functionalMethod.getAnnotation(FilterWith.class);
        if (filterWith != null) {
            allFilters.addAll(Arrays.asList(filterWith.value()));
        }
        
        FilterChain filterChain = buildFilterChain(injector, allFilters);
        
        return new Route(httpMethod, uri, functionalMethod, filterChain);
    }
    
    private List<Class<? extends Filter>> calculateGlobalFilters(
            Optional<List<Class<? extends Filter>>> globalFiltersList,
            Injector injector) {
        List<Class<? extends Filter>> allFilters = Lists.newArrayList();
        // Setting globalFilters in route will deactivate the filters defined
        // by conf.Filters
        if (globalFiltersList.isPresent()) {
            allFilters.addAll(globalFiltersList.get());
        } else {
            String globalFiltersWithPrefixMaybe
                    = ninjaBaseDirectoryResolver.resolveApplicationClassName(GLOBAL_FILTERS_DEFAULT_LOCATION);
 
            if (SwissKnife.doesClassExist(globalFiltersWithPrefixMaybe, this)) {
                try {
                    Class<?> globalFiltersClass = Class.forName(globalFiltersWithPrefixMaybe);
                    ApplicationFilters globalFilters = (ApplicationFilters) injector.getInstance(globalFiltersClass);
                    globalFilters.addFilters(allFilters);
                } catch (Exception exception) {
                    // That simply means the user did not configure conf.Filters.
                }
            }
        }
        
        return allFilters;
    
    }

    private FilterChain buildFilterChain(Injector injector,
                                         LinkedList<Class<? extends Filter>> filters) {

        if (filters.isEmpty()) {
            
            // either target object (functional method) or guice will create new instance
            Provider<?> targetProvider = (targetObject.isPresent() ?
                Providers.of(targetObject.get())
                    : injector.getProvider(functionalMethod.getDeclaringClass()));

            // invoke functional method with optionally using impl for argument extraction
            ControllerMethodInvoker methodInvoker
                = ControllerMethodInvoker.build(
                    functionalMethod, implementationMethod.orElse(functionalMethod), injector, ninjaProperties);

            return new FilterChainEnd(targetProvider, methodInvoker);
            
        } else {

            Class<? extends Filter> filter = filters.pop();
            
           
            Provider<? extends Filter> filterProvider = injector.getProvider(filter);
                        
            return new FilterChainImpl(filterProvider,buildFilterChain(injector, filters));
            
        }
    }

    private Set<Class<? extends Filter>> calculateFiltersForClass(
            Class controller) {
        LinkedHashSet<Class<? extends Filter>> filters = new LinkedHashSet<>();
        
        //
        // Step up the superclass tree, so that superclass filters come first
        //

        // Superclass
        if (controller.getSuperclass() != null) {
            filters.addAll(calculateFiltersForClass(controller.getSuperclass()));
        }
        
        // Interfaces
        if (controller.getInterfaces() != null) {
            for (Class clazz : controller.getInterfaces()) {
                filters.addAll(calculateFiltersForClass(clazz));
            }
        }
        
        // Now add from here
        FilterWith filterWith = (FilterWith) controller
                .getAnnotation(FilterWith.class);
        if (filterWith != null) {
            filters.addAll(Arrays.asList(filterWith.value()));
        }
        
        // And return
        return filters;
    }
}
