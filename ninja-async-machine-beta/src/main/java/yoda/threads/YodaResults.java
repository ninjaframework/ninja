/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package yoda.threads;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * // The results are not yet finished. BUT
 * 
 * @author ra
 */
@Singleton
public class YodaResults {
    
    private final String I18N_TIMEOUT_EXCEPTION_MESSAGE_KEY = "yoda.timeout_exception";
    private final String I18N_TIMEOUT_EXCEPTION_MESSAGE_DEFAULT = "A timeout error occurred.";
    
    private final String I18N_SERVER_TOO_BUSY_EXCEPTION_MESSAGE_KEY = "yoda.too_busy";
    private final String I18N_SERVER_TOO_BUSY_EXCEPTION_MESSAGE_DEFAULT = "Server too busy. Rejecting request.";
    
    private final String I18N_RESPONSER_REJECTED_EXECUTION_MESSAGE_KEY = "yoda.responser_rejected_execution";
    private final String I18N_RESPONSER_REJECTED_EXECUTION_MESSAGE_DEFAULT = "Internal server error. Responder rejected execution.";
    
    
    
    private final static Logger logger = LoggerFactory.getLogger(YodaResults.class);
    
    @Inject
    Messages messages;
    
    @Inject
    NinjaProperties ninjaProperties;
    
    public Result getTimeoutExceptionResult(Context context) {
        
        String messageI18n 
                = messages.getWithDefault(
                        I18N_TIMEOUT_EXCEPTION_MESSAGE_KEY,
                        I18N_TIMEOUT_EXCEPTION_MESSAGE_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n);

        Result result = Results
                .internalServerError()
                .render(message)
                .template(
                        ninjaProperties.getWithDefault(
                                NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY,
                                NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

        return result;
    
    }
    
    public Result getTooBusyResult(Context context) {
        
        String messageI18n 
                = messages.getWithDefault(
                        I18N_RESPONSER_REJECTED_EXECUTION_MESSAGE_KEY,
                        I18N_RESPONSER_REJECTED_EXECUTION_MESSAGE_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n);

        Result result = Results
                .internalServerError()
                .render(message)
                .template(
                        ninjaProperties.getWithDefault(
                                NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY,
                                NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

        return result;
    
    }
    
    public Result getResponderRejectedExecutionResult(Context context) {
        
        String messageI18n 
                = messages.getWithDefault(
                        I18N_TIMEOUT_EXCEPTION_MESSAGE_KEY,
                        I18N_TIMEOUT_EXCEPTION_MESSAGE_DEFAULT,
                        context,
                        Optional.<Result>absent());
        
        Message message = new Message(messageI18n);

        Result result = Results
                .internalServerError()
                .render(message)
                .template(
                        ninjaProperties.getWithDefault(
                                NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR_KEY,
                                NinjaConstant.LOCATION_VIEW_FTL_HTML_INTERNAL_SERVER_ERROR));

        return result;
        
    }

}
