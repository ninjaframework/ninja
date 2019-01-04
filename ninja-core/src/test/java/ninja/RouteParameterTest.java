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

package ninja;

import static org.junit.Assert.assertThat;
import org.junit.Test;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.aMapWithSize;

public class RouteParameterTest {

    @Test
    public void parse() {
        Map<String,RouteParameter> params;
        RouteParameter param;
        
        // no named parameters is null
        params = RouteParameter.parse("/user");
        assertThat(params, aMapWithSize(0));
        
        params = RouteParameter.parse("/user/{id}/{email: [0-9]+}");
        
        param = params.get("id");
        assertThat(param.getName(), is("id"));
        assertThat(param.getToken(), is("{id}"));
        assertThat(param.getRegex(), is(nullValue()));
        
        param = params.get("email");
        assertThat(param.getName(), is("email"));
        assertThat(param.getToken(), is("{email: [0-9]+}"));
        assertThat(param.getRegex(), is("[0-9]+"));
    }
    
}
