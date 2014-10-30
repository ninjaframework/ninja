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

package router;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;

import ninja.Router;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AnnotationRouter {
    
    final static String ANNOTATION_ROUTER_PACKAGES = "annotation_router.packages";
    
    final static Logger logger = LoggerFactory.getLogger(AnnotationRouter.class);
    
    final NinjaProperties ninjaProperties;

    @Inject
    public AnnotationRouter(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
      
    /**
     * Method to scan for the Route annotation and add the routes to the router.
     * 
     * @param router at add the routes to
     */
    public void addAnnotatedRoutes(Router router) {
        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        Set<URL> packagesToScan = getPackagesToScanForRoutes(ninjaProperties);
        builder.addUrls(packagesToScan);
        
        builder.addScanners(new MethodAnnotationsScanner());
        Reflections reflections = new Reflections(builder);

        Set<Method> methods = reflections.getMethodsAnnotatedWith(Route.class);

        logger.info("Generating {} routes.", methods.size());

        for (Method method: methods) {
            
            Route route = method.getAnnotation(Route.class);
            final String path = route.path();
            final Class<?> declaringClass = method.getDeclaringClass();
            final String methodName = method.getName();
            
            router.METHOD(route.httpMethod()).route(path).with(declaringClass,methodName);
       
        }
    }
    
    
    private Set<URL> getPackagesToScanForRoutes(
        NinjaProperties ninjaProperties) {
        
        Set<URL> packagesToScanForRoutes = Sets.newHashSet();
        
        String [] packagesDefinedByUserOrNull 
                = ninjaProperties.getStringArray(ANNOTATION_ROUTER_PACKAGES);
        
        if (packagesDefinedByUserOrNull != null) {
            
            for (String packageDefinedByUser: packagesDefinedByUserOrNull) {
                packagesToScanForRoutes.addAll(
                        ClasspathHelper.forPackage(packageDefinedByUser));
            }
        
        } else {
            
            packagesToScanForRoutes.addAll(
                    ClasspathHelper.forPackage(NinjaConstant.CONTROLLERS_DIR)); 
            
        }
        
        return packagesToScanForRoutes;
    
    }

    
}
