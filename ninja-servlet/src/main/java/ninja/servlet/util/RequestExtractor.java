package ninja.servlet.util;

import javax.servlet.http.HttpServletRequest;

import ninja.Context;
import ninja.params.ArgumentExtractor;
import ninja.servlet.ContextImpl;

public class RequestExtractor implements ArgumentExtractor<HttpServletRequest> {

    @Override
    public HttpServletRequest extract(Context context) {

        if (context instanceof ContextImpl) {
            return ((ContextImpl) context).getHttpServletRequest();
        } else {
            throw new RuntimeException(
                    "RequestExtractor only works with Servlet container implementation of Context.");
        }

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