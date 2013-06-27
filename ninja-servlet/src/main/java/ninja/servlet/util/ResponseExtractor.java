package ninja.servlet.util;

import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.params.ArgumentExtractor;
import ninja.servlet.ContextImpl;

public class ResponseExtractor implements ArgumentExtractor<HttpServletResponse> {

    @Override
    public HttpServletResponse extract(Context context) {

        if (context instanceof ContextImpl) {
            return ((ContextImpl) context).getHttpServletResponse();
        } else {
            throw new RuntimeException(
                    "RequestExtractor only works with Servlet container implementation of Context.");
        }

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