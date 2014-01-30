package etc;

import ninja.Context;
import ninja.params.ArgumentExtractor;


public class LoggedInUserExtractor implements ArgumentExtractor<String> {

    @Override
    public String extract(Context context) {
        
        // if we got no cookies we break:
        if (context.getSession() != null) {
            
            String username = context.getSession().get("username");
            
            return username;
            
        }
        
        return null;
    }

    @Override
    public Class<String> getExtractedType() {
        return String.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }


}
