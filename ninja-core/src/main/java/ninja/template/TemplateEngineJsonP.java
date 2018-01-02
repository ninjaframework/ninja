/**
 * Copyright (C) 2012- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import ninja.Context;
import ninja.Result;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * JSONP engine. Outputs the given result as JSONP output: Javascript callback
 * with data as a parameter. Contains JSONP validation regular expression to
 * test the validness of the JSONP callback. The valid callbacks are simple
 * functions like myJsFunction or simple object path looking like:
 * something1.something2.something3 . 
 * See {@linkplain ninja.template.TemplateEngineJsonPValidatorTest} for supported cases tests.
 * This is a subset of rules from the
 * following article:
 *
 * @see <a href="http://tav.espians.com/sanitising-jsonp-callback-identifiers-for-security.html">Sanitizing JSONP callbacks</a>
 */
@Singleton
public class TemplateEngineJsonP implements TemplateEngine {

    private final Logger logger = LoggerFactory.getLogger(TemplateEngineJsonP.class);

    static final String DEFAULT_CALLBACK_PARAMETER_NAME = "callback";

    static final String DEFAULT_CALLBACK_PARAMETER_VALUE = "onResponse";

    static final Pattern CALLBACK_SECURITY_VALIDATION_REGEXP
            = Pattern.compile("^([a-zA-Z$_]{1}[a-zA-Z0-9$_.]*[a-zA-Z0-9$_]{1}){1,}$");

    private final ObjectMapper objectMapper;

    private final String callbackParameterName;

    @Inject
    public TemplateEngineJsonP(ObjectMapper objectMapper, NinjaProperties properties) {

        this.objectMapper = objectMapper;
        this.callbackParameterName = properties.getWithDefault(
                NinjaConstant.NINJA_JSONP_CALLBACK_PARAMETER,
                DEFAULT_CALLBACK_PARAMETER_NAME);
    }

    @Override
    public void invoke(Context context, Result result) {
        ResponseStreams responseStreams = context.finalizeHeaders(result);
        String callback = getCallbackName(context);
        try (OutputStream outputStream = responseStreams.getOutputStream()) {
            objectMapper.writeValue(outputStream, new JSONPObject(callback, result.getRenderable()));
        } catch (IOException e) {
            logger.error("Error while rendering jsonp.", e);
        }
    }

    @Override
    public String getContentType() {
        return Result.APPLICATION_JSONP;
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        // intentionally returns null...
        return null;
    }

    private String getCallbackName(Context context) {
        String callback = context.getParameter(this.callbackParameterName, DEFAULT_CALLBACK_PARAMETER_VALUE);
        return isThisASecureCallbackName(callback) ? callback : DEFAULT_CALLBACK_PARAMETER_VALUE;
    }

    /**
     * Tests whether the given function name is a valid JSONP function
     * name/path.
     *
     * @param callback Callback value to test.
     * @return Whether the given function name is a valid JSONP function
     * name/path.
     */
    public static boolean isThisASecureCallbackName(String callback) {
        return !Strings.isNullOrEmpty(callback)
                && !callback.contains("..") 
                && CALLBACK_SECURITY_VALIDATION_REGEXP.matcher(callback).matches();
    }
}
