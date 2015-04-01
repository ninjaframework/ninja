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

import com.fizzed.rocker.RenderingException;
import com.google.common.base.Optional;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import ninja.AssetsController;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Provides all <code>N</code> variables and methods available to templates
 * when running inside NinjaFramework.  Exposing a new variable or method
 * can either be done inside this class OR you can extend this and use that
 * as the default template for your application templates.
 * 
 *  @N.isProd()
 *  @N.assetsAt("css/style.css")
 * 
 * @author Fizzed, Inc (http://fizzed.com)
 * @author joelauer (http://twitter.com/jjlauer)
 */
public class NinjaRocker {
    
    // hidden from templates
    private final NinjaRockerContext ninjaRockerContext;
    private final Context context;
    private final Result result;
    private final Locale locale;
    private PrettyTime prettyTime;
    
    // will be visible to template during rendering process as a property
    public String lang;
    public Map<String,String> session;
    public String contextPath;
    
    public NinjaRocker(NinjaRockerContext ninjaRockerContext, Context context, Result result) {
        this.ninjaRockerContext = ninjaRockerContext;
        
         // context & result required for correct i18n method
        this.context = context;
        this.result = result;
        
        // set language from framework
        Lang ninjaLang = ninjaRockerContext.getLangProvider().get();
        Optional<String> language = ninjaLang.getLanguage(context, Optional.of(result));
        if (language.isPresent()) {
            lang = language.get();
        }
        
        Optional<String> requestLang = ninjaLang.getLanguage(context, Optional.of(result));
        this.locale = ninjaLang.getLocaleFromStringOrDefault(requestLang);
     
        // put all entries of the session cookie to the map.
        // You can access the values by their key in the cookie
        // For eg: @session.get("key")
        // should we just set to an empty map?
        if (!context.getSession().isEmpty()) {
            this.session = context.getSession().getData();
        }
        
        this.contextPath = context.getContextPath();
    }
    
    private Class<?> typeNameToClass(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException ex) {
            throw new RenderingException("Unable to find class for type name: " + typeName);
        }
    }
    
    public String reverseRoute(String typeName, String methodName) {
        return ninjaRockerContext.getRouter().getReverseRoute(
            typeNameToClass(typeName),
            methodName);
    }
    
    public String reverseRoute(String typeName, String methodName, Object... params) {
        return ninjaRockerContext.getRouter().getReverseRoute(
            typeNameToClass(typeName),
            methodName,
            params);
    }
    
    public String reverseRoute(Class<?> type, String methodName) {
        return ninjaRockerContext.getRouter().getReverseRoute(
            type,
            methodName);
    }
    
    public String reverseRoute(Class<?> type, String methodName, Object... params) {
        return ninjaRockerContext.getRouter().getReverseRoute(
            type,
            methodName,
            params);
    }
    
    public String assetsAt(String file) {
        return reverseRoute(AssetsController.class, "serveStatic", "fileName", file);
    }
    
    public String webJarsAt(String file) {
        return reverseRoute(AssetsController.class, "serveWebJars", "fileName", file);
    }
    
    public String i18n(String messageKey) throws RenderingException {
        String messageValue = ninjaRockerContext.getMessages()
                .get(messageKey, context, Optional.of(result))
                .or(messageKey);
        
        return messageValue;
    }
    
    public String i18n(String messageKey, Object... params) throws RenderingException {
        String messageValue = ninjaRockerContext.getMessages()
                .get(messageKey, context, Optional.of(result), params)
                .or(messageKey);

        return messageValue;
    }
    
    public String prettyTime(Date d) {
        if (prettyTime == null) {
            prettyTime = new PrettyTime(locale);
        }
        return prettyTime.format(d);
    }
    
    // other custom stuff useful
    
    public boolean isProd() {
        return this.ninjaRockerContext.getNinjaProperties().isProd();
    }
    
    public boolean isTest() {
        return this.ninjaRockerContext.getNinjaProperties().isTest();
    }
    
    public boolean isDev() {
        return this.ninjaRockerContext.getNinjaProperties().isDev();
    }
    
}
