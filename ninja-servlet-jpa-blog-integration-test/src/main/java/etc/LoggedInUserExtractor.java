package etc;

import ninja.Context;
import ninja.params.ArgumentExtractor;


public class LoggedInUserExtractor implements ArgumentExtractor<String> {

    @Override
    public String extract(Context context) {
        
        // if we got no cookies we break:
        if (context.getSessionCookie() != null) {
            
            String username = context.getSessionCookie().get("username");
            
            return username;
            
        }
        
        return null;
    }

    @Override
    public Class getExtractedType() {
        return String.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }


}
