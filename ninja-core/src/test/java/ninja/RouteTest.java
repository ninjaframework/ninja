/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import org.hamcrest.CoreMatchers;
import org.junit.Test;

/**
 * NOTE: almost all functionality of the route matching is tested via
 * RouteBuilderImplTest.
 * 
 * @author ra
 */
public class RouteTest {

    @Test
    public void convertRawUriToRegex() {
        
        assertThat(
                Route.convertRawUriToRegex("/me/{username: .*}"), 
                CoreMatchers.equalTo("/me/(.*)")); 
        
        assertThat(
                Route.convertRawUriToRegex("/me/{username: [a-zA-Z][a-zA-Z_0-9]}"), 
                CoreMatchers.equalTo("/me/([a-zA-Z][a-zA-Z_0-9])")); 
        
        assertThat(
                Route.convertRawUriToRegex("/me/{username}"), 
                CoreMatchers.equalTo("/me/([^/]*)")); 

        // check regex with escapes/backslashes (\)
        assertThat(
                Route.convertRawUriToRegex("/me/{id: \\d+}"),
                CoreMatchers.equalTo("/me/(\\d+)"));

        // check regex with groups, they should be converted to non-capturing groups
        // people may want to have both "/users/mike" and "/mike" in one route
        // https://github.com/ninjaframework/ninja/issues/497
        assertThat(
                Route.convertRawUriToRegex("(/users)?/{user}"),
                CoreMatchers.equalTo("(?:/users)?/([^/]*)")
        );
    }
    
}
