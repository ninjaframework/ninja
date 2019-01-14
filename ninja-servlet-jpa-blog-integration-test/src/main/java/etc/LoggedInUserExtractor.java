/**
 * Copyright (C) 2012-2019 the original author or authors.
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
