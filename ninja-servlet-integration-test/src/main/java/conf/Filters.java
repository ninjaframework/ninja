/*
 * Copyright (C) 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package conf;

import java.util.List;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Filters implements ninja.application.ApplicationFilters {
    
    private static Logger logger = LoggerFactory.getLogger(Filters.class);
    
    static class DemoFilter implements Filter {

        @Override
        public Result filter(FilterChain filterChain, Context context) {
            
            logger.info("Executed Demo Filter...");
            return filterChain.next(context);
        }
    
    }

    @Override
    public void addFilters(List<Class<? extends Filter>> filters) {
        filters.add(DemoFilter.class);
    }
    
}
