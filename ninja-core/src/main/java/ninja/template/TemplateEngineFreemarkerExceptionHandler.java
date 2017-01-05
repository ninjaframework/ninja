/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.io.PrintWriter;
import java.io.Writer;

import ninja.utils.NinjaProperties;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * A general exception handler for Freemarker.
 * - Outputs a readable error in test / dev mode.
 * - Outputs a general error message in production.
 * 
 * @author ra
 */
@Singleton
public class TemplateEngineFreemarkerExceptionHandler implements
        TemplateExceptionHandler {

    private final NinjaProperties ninjaProperties;
    private final Logger logger;

    @Inject
    public TemplateEngineFreemarkerExceptionHandler(Logger logger,
                                                    NinjaProperties ninjaProperties) {
        this.logger = logger;
        this.ninjaProperties = ninjaProperties;
    }

    public void handleTemplateException(TemplateException te,
                                        Environment env,
                                        Writer out) throws TemplateException {

        if (ninjaProperties.isProd()) {
            // Let the exception bubble up to the central handlers
            // so the application can return the correct error page
            // or perform some other application specific action.
            throw te;
        } else {
            // print out full stacktrace if we are in test or dev mode

            PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter) out
                    : new PrintWriter(out);
            pw.println("<!-- FREEMARKER ERROR MESSAGE STARTS HERE -->"
                    + "<script language=javascript>//\"></script>"
                    + "<script language=javascript>//\'></script>"
                    + "<script language=javascript>//\"></script>"
                    + "<script language=javascript>//\'></script>"
                    + "</title></xmp></script></noscript></style></object>"
                    + "</head></pre></table>"
                    + "</form></table></table></table></a></u></i></b>"
                    + "<div align=left "
                    + "style='background-color:#FFFF00; color:#FF0000; "
                    + "display:block; border-top:double; padding:2pt; "
                    + "font-size:medium; font-family:Arial,sans-serif; "
                    + "font-style: normal; font-variant: normal; "
                    + "font-weight: normal; text-decoration: none; "
                    + "text-transform: none'>"
                    + "<b style='font-size:medium'>FreeMarker template error!</b>"
                    + "<pre><xmp>");
            te.printStackTrace(pw);
            pw.println("</xmp></pre></div></html>");
            logger.error("Templating error.", te);
        }
    }
}