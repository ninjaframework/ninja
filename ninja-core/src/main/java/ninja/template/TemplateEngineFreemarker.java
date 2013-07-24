/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja.template;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateEngineFreemarker implements TemplateEngine {

    private final String FILE_SUFFIX = ".ftl.html";

    private final Configuration cfg;

    private final Messages messages;
    
    private final Lang lang;

    private final TemplateEngineHelper templateEngineHelper;

    private final Logger logger;

    private final TemplateEngineFreemarkerExceptionHandler templateEngineFreemarkerExceptionHandler;

    @Inject
    public TemplateEngineFreemarker(Messages messages,
                                    Lang lang,
                                    Logger logger,
                                    TemplateEngineFreemarkerExceptionHandler templateEngineFreemarkerExceptionHandler,
                                    TemplateEngineHelper templateEngineHelper,
                                    TemplateEngineManager templateEngineManager,
                                    NinjaProperties ninjaProperties) throws Exception {
        this.messages = messages;
        this.lang = lang;
        this.logger = logger;
        this.templateEngineFreemarkerExceptionHandler = templateEngineFreemarkerExceptionHandler;
        this.templateEngineHelper = templateEngineHelper;
        
        cfg = new Configuration();
        
        cfg.setTemplateExceptionHandler(templateEngineFreemarkerExceptionHandler);

        ///////////////////////////////////////////////////////////////////////
        // 1) In dev we load templates from src/java/main and ftl does refreshing
        //    of changed templates.
        //    => but only if we can access src/main/java => otherwise we do template
        //    via the jetty plugin (eg in ninja-appengine)
        // 2) In test and prod we never refresh templates.
        ///////////////////////////////////////////////////////////////////////      
        String srcDir 
        = System.getProperty("user.dir")
            + File.separator 
            + "src" 
            + File.separator 
            + "main" 
            + File.separator
            + "java";        
                
        if (ninjaProperties.isDev()
                && new File(srcDir).exists()) {
            
            try {
                
                cfg.setDirectoryForTemplateLoading(new File(srcDir));
                
            } catch (IOException e) {
                logger.error("Error Loading Freemarker Template " +srcDir , e);
            }
            
            // check for updates each second
            cfg.setTemplateUpdateDelay(1);


        } else {
            // load templates from classpath
            cfg.setClassForTemplateLoading(this.getClass(), "/");
            
            // never update the templates in production or while testing...
            cfg.setTemplateUpdateDelay(Integer.MAX_VALUE);
            
            // Hold 20 templates as strong references as recommended by:
            // http://freemarker.sourceforge.net/docs/pgui_config_templateloading.html
            cfg.setSetting(Configuration.CACHE_STORAGE_KEY, "strong:20, soft:250");

        }
        
        // we are going to enable html escaping by default using this template
        // loader:
        cfg.setTemplateLoader(new TemplateEngineFreemarkerEscapedLoader(cfg
                .getTemplateLoader()));
        
        
        // We also do not want Freemarker to chose a platform dependent
        // number formatting. Eg "1000" could be printed out by FTL as "1,000"
        // on some platform. This is not "least astonishemnt". It will also
        // break stuff really badly sometimes.
        // See also: http://freemarker.sourceforge.net/docs/app_faq.html#faq_number_grouping
        cfg.setNumberFormat("0.######");  // now it will print 1000000
        
        // The defaultObjectWrapper is a BeansWrapper
        // => we fetch it and allow the wrapper to expose all fields
        // for convenience
        BeansWrapper beansWrapper = (BeansWrapper) cfg.getObjectWrapper();
        beansWrapper.setExposeFields(true);
        
    }

    @Override
    public void invoke(Context context, Result result) {

        Object object = result.getRenderable();

        ResponseStreams responseStreams = context.finalizeHeaders(result);

        Map map;
        // if the object is null we simply render an empty map...
        if (object == null) {            
            map = Maps.newHashMap();
            
        } else if (object instanceof Map) {            
            map = (Map) object;
            
        } else {
            // We are getting an arbitrary Object and put that into
            // the root of freemarker
            
            // If you are rendering something like Results.ok().render(new MyObject())
            // Assume MyObject has a public String name field.            
            // You can then access the fields in the template like that:
            // ${myObject.publicField}            
            
            String realClassNameLowerCamelCase = CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());
            
            map = Maps.newHashMap();
            map.put(realClassNameLowerCamelCase, object);
            
        }
        
        // set language from framework. You can access
        // it in the templates as ${lang}
        Optional<String> language = lang.getLanguage(context, Optional.of(result));
        if (language.isPresent()) {
            map.put("lang", language.get());
        }

        // put all entries of the session cookie to the map.
        // You can access the values by their key in the cookie
        if (!context.getSessionCookie().isEmpty()) {
            map.put("session", context.getSessionCookie().getData());
        }
        
        map.put("contextPath", context.getContextPath());

        ///////////////////////////////////////////////////////////////////////
        // this will be deprecated soon. Please use ${i18n("mykey")} 
        // to render i18n messages
        //////////////////////////////////////////////////////////////////////
        // merge messages with this template...
        Map<Object, Object> i18nMap = messages.getAll(context, Optional.of(result));
        map.putAll(i18nMap);
        
        //////////////////////////////////////////////////////////////////////
        // A method that renders i18n messages and can also render messages with 
        // placeholders directly in your template:
        // E.g.: ${i18n("mykey", myPlaceholderVariable)}
        //////////////////////////////////////////////////////////////////////
        map.put("i18n", new TemplateEngineFreemarkerI18nMethod(messages, context, result));
        
        
        

        ///////////////////////////////////////////////////////////////////////
        // Convenience method to translate possible flash scope keys.
        // !!! If you want to set messages with placeholders please do that
        // !!! in your controller. We only can set simple messages.
        // Eg. A message like "errorMessage=my name is: {0}" => translate in controller and pass directly.
        //     A message like " errorMessage=An error occurred" => use that as errorMessage.  
        //
        // prefix keys with "flash_"
        //////////////////////////////////////////////////////////////////////
        for (Entry<String, String> entry : context.getFlashCookie().getCurrentFlashCookieData().entrySet()) {
            
            String messageValue = null;

                
            Optional<String> messageValueOptional = messages.get(entry.getValue(), context, Optional.of(result));
                
            if (!messageValueOptional.isPresent()) {
                messageValue = entry.getValue();
            } else {
                messageValue = messageValueOptional.get();
            }
            
            map.put("flash_" + entry.getKey(), messageValue);           
        }
        


        String templateName = templateEngineHelper.getTemplateForResult(
                context.getRoute(), result, FILE_SUFFIX);

        // Specify the data source where the template files come from.
        // Here I set a file directory for it:
        try {

            if ((result.getStatusCode() % 100) != 3) {
                // not use redirects

                Template freemarkerTemplate = cfg.getTemplate(templateName);

                // convert tuples:
                freemarkerTemplate.process(map, responseStreams.getWriter());
            }

            responseStreams.getWriter().flush();
            responseStreams.getWriter().close();

        } catch (Exception e) {
            logger.error(
                    "Error processing Freemarker Template " + templateName, e);
            
        }

    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return FILE_SUFFIX;
    }
    
}
