/**
 * Copyright (C) 2015 Fizzed, Inc.
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

package ninja.rocker;

import ninja.rocker.views.common_error;
import java.util.Map;

import javax.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;

import com.google.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineHelper;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;
import org.slf4j.LoggerFactory;

/**
 * Template engine for Rocker templates.  See https://github.com/fizzed/rocker
 * 
 * @author Fizzed, Inc (http://fizzed.com)
 * @author joelauer (http://twitter.com/jjlauer)
 */
@Singleton
public class TemplateEngineRocker implements TemplateEngine {
    static private final Logger log = LoggerFactory.getLogger(TemplateEngineRocker.class);
    
    private final String FILE_SUFFIX = ".rocker.html";
    private final String CONTENT_TYPE = "text/html";

    private final NinjaRockerContext ninjaRockerContext;
    private final String fileSuffix;
    private final String contentType;
    private final Map<String, Class<? extends NinjaRockerTemplate>> commonErrorTemplates;
    
    @Inject
    public TemplateEngineRocker(NinjaRockerContext ninjaRockerContext,
                                TemplateEngineHelper templateEngineHelper,
                                NinjaProperties ninjaProperties) throws Exception {
        this.ninjaRockerContext = ninjaRockerContext;
        this.fileSuffix = FILE_SUFFIX;
        this.contentType = CONTENT_TYPE;
        
        // setup default error templates
        this.commonErrorTemplates = new HashMap<>();
        this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR, common_error.class);
        this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST, common_error.class);
        this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN, common_error.class);
        this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND, common_error.class);
        this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED, common_error.class);
 
        loadCustomErrorTemplates();
    }
    
    public final void loadCustomErrorTemplates() {
        Class<NinjaRockerTemplate> internalServerErrorClass = getCustomErrorViewClass("views.system.internal_server_error");
        if (internalServerErrorClass != null) {
            this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR, internalServerErrorClass);
        }
        
        Class<NinjaRockerTemplate> badRequestClass = getCustomErrorViewClass("views.system.bad_request");
        if (badRequestClass != null) {
            this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_BAD_REQUEST, badRequestClass);
        }
        
        Class<NinjaRockerTemplate> forbiddenClass = getCustomErrorViewClass("views.system.forbidden");
        if (forbiddenClass != null) {
            this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN, forbiddenClass);
        }
        
        Class<NinjaRockerTemplate> notFoundClass = getCustomErrorViewClass("views.system.not_found");
        if (notFoundClass != null) {
            this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND, notFoundClass);
        }
        
        Class<NinjaRockerTemplate> unauthorizedClass = getCustomErrorViewClass("views.system.unauthorized");
        if (unauthorizedClass != null) {
            this.commonErrorTemplates.put(NinjaConstant.LOCATION_VIEW_FTL_HTML_UNAUTHORIZED, unauthorizedClass);
        }
    }
    
    public Class<NinjaRockerTemplate> getCustomErrorViewClass(String className) {
        try {
            return (Class<NinjaRockerTemplate>)Class.forName(className);
        } catch (Throwable t) {
            // do nothing
            return null;
        }
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return this.fileSuffix;
    }

    @Override
    public void invoke(Context context, Result result) {
        
        Object object = result.getRenderable();
        
        // various types of objects renderable may store
        Map<String,Object> valueMap = null;
        Message valueMessage = null;
        NinjaRockerTemplate rockerTemplate = null;
        
        // if the object is null we simply render an empty map...
        if (object == null) {            
            valueMap = Collections.EMPTY_MAP;
        }
        else if (object instanceof NinjaRockerTemplate) {            
            rockerTemplate = (NinjaRockerTemplate)object;   
        }
        else if (object instanceof Map) {            
            valueMap = (Map<String,Object>)object;    
        }
        else if (object instanceof Message) {
            valueMessage = (Message)object;
        }
        else {
            log.error("Unable to handle renderable not of type: " + object.getClass());
            return;
        } 

        //
        // until NinjaFramework makes these template names configurable -- these
        // need to be handled by creating instances of correct Rocker template
        //
        if (rockerTemplate == null) {
            String templateName = result.getTemplate();
            if (templateName != null && commonErrorTemplates.containsKey(templateName)) {
                Class<? extends NinjaRockerTemplate> commonErrorType = commonErrorTemplates.get(templateName);

                NinjaRockerTemplate rockerErrorTemplate = null;

                try {
                    rockerErrorTemplate = commonErrorType.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw renderingOrRuntimeException("Unable to create template class " + commonErrorType, e);
                }

                if (rockerErrorTemplate instanceof CommonErrorTemplate) {

                    ((CommonErrorTemplate)rockerErrorTemplate).message(valueMessage);

                }

                rockerTemplate = rockerErrorTemplate;
            }
        }
        
        if (rockerTemplate != null) {
            // inject context for ninja into template, then delegate rendering to itself
            rockerTemplate.ninjaRender(ninjaRockerContext, context, result);
        }
        else {
            throw new RuntimeException("Something really fishy. No idea how to handle this request");
        }
    }
    
    
    static public RuntimeException renderingOrRuntimeException(String message, Exception cause) throws RuntimeException {
        
        return renderingOrRuntimeException(message, cause, null, null, null, -1);
        
    }
    
    // this lets us support old versions of Ninja that do not have the new
    // RenderingException support
    static public RuntimeException renderingOrRuntimeException(String message,
                                                        Exception cause,
                                                        Result result,
                                                        String title,
                                                        String sourcePath,
                                                        int lineOfError) {
        try {
            // are we running in Ninja w/ rendering exceptions?
            Class<?> ninjaRenderingExceptionClass
                = TemplateEngineRocker.class.getClassLoader().loadClass("ninja.exceptions.RenderingException");
            
            Constructor<?> constructor = ninjaRenderingExceptionClass.getDeclaredConstructor(
                    String.class, Throwable.class, Result.class, String.class, String.class, int.class);
            
            return (RuntimeException)constructor.newInstance(cause.getMessage(),
                                                                cause,
                                                                result,
                                                                "Rocker rendering exception",
                                                                sourcePath,
                                                                lineOfError);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.warn("Unable to throw new ninja.exceptions.RenderingException, falling back to old RuntimeException", e);
            
            // throw normal runtime exception, but build useful message
            String errMessage = new StringBuilder()
                .append(title)
                .append(": ")
                .append(message)
                .append(" from ")
                .append(sourcePath)
                .append(" @line ")
                .append(lineOfError)
                .toString();
            
            return new RuntimeException(errMessage, cause);
        }
    }
}
