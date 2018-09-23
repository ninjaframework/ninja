package ninja.bodyparser;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.params.ParamParsers;

@Singleton
public class BodyParserEngineMultipartPost extends BodyParserEnginePost {

    @Inject
    public BodyParserEngineMultipartPost(ParamParsers paramParsers) {
        super(paramParsers);
    }

    public String getContentType() {
        return "multipart/form-data";
    }

}
