/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja.params;

import com.google.common.collect.ImmutableMap;
import ninja.Context;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.validation.Validation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Built in argument extractors
 *
 * @author James Roper
 */
public class ArgumentExtractors {

    private static final Map<Class<?>, ArgumentExtractor<?>> STATIC_EXTRACTORS =
        ImmutableMap.<Class<?>, ArgumentExtractor<?>>builder()
                .put(Context.class, new ContextExtractor())
                .put(Validation.class, new ValidationExtractor())
                .put(SessionCookie.class, new SessionExtractor())
                .put(FlashCookie.class, new FlashExtractor())
                .put(HttpServletRequest.class, new HttpServletRequestExtractor())
                .put(HttpServletResponse.class, new HttpServletResponseExtractor())
                .build();

    public static ArgumentExtractor<?> getExtractorForType(Class<?> type) {
        return STATIC_EXTRACTORS.get(type);
    }

    public static class ContextExtractor implements ArgumentExtractor<Context> {
        @Override
        public Context extract(Context context) {
            return context;
        }

        @Override
        public Class<Context> getExtractedType() {
            return Context.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class ValidationExtractor implements ArgumentExtractor<Validation> {
        @Override
        public Validation extract(Context context) {
            return context.getValidation();
        }

        @Override
        public Class<Validation> getExtractedType() {
            return Validation.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class SessionExtractor implements ArgumentExtractor<SessionCookie> {
        @Override
        public SessionCookie extract(Context context) {
            return context.getSessionCookie();
        }

        @Override
        public Class<SessionCookie> getExtractedType() {
            return SessionCookie.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class FlashExtractor implements ArgumentExtractor<FlashCookie> {
        @Override
        public FlashCookie extract(Context context) {
            return context.getFlashCookie();
        }

        @Override
        public Class<FlashCookie> getExtractedType() {
            return FlashCookie.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class HttpServletRequestExtractor implements ArgumentExtractor<HttpServletRequest> {
        @Override
        public HttpServletRequest extract(Context context) {
            return context.getHttpServletRequest();
        }

        @Override
        public Class<HttpServletRequest> getExtractedType() {
            return HttpServletRequest.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class HttpServletResponseExtractor implements ArgumentExtractor<HttpServletResponse> {
        @Override
        public HttpServletResponse extract(Context context) {
            return context.getHttpServletResponse();
        }

        @Override
        public Class<HttpServletResponse> getExtractedType() {
            return HttpServletResponse.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class PathParamExtractor implements ArgumentExtractor<String> {
        private final String key;

        public PathParamExtractor(PathParam pathParam) {
            this.key = pathParam.value();
        }

        @Override
        public String extract(Context context) {
            return context.getPathParameter(key);
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return key;
        }
    }

    public static class ParamExtractor implements ArgumentExtractor<String> {
        private final String key;

        public ParamExtractor(Param param) {
            this.key = param.value();
        }

        @Override
        public String extract(Context context) {
            return context.getParameter(key);
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return key;
        }
    }

    public static class SessionParamExtractor implements ArgumentExtractor<String> {
        private final String key;

        public SessionParamExtractor(SessionParam sessionParam) {
            this.key = sessionParam.value();
        }

        @Override
        public String extract(Context context) {
            return context.getSessionCookie().get(key);
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return key;
        }
    }

    public static class BodyAsExtractor<T> implements ArgumentExtractor<T> {
        private final Class<T> bodyType;

        public BodyAsExtractor(Class<T> bodyType) {
            this.bodyType = bodyType;
        }

        @Override
        public T extract(Context context) {
            return context.parseBody(bodyType);
        }

        @Override
        public Class<T> getExtractedType() {
            return bodyType;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }
}
