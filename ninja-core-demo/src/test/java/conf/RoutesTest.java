/**
 * Copyright (C) 2013 the original author or authors.
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

package conf;

import org.junit.Test;

import controllers.ApplicationController;
import ninja.NinjaRouterTest;

public class RoutesTest extends NinjaRouterTest {
    
    @Test
    public void testTestRoutesAreHiddenFromProduction() {
        
        startServerInProdMode();
        
        //test that test route is not available in production mode.
        aRequestLike("GET",  "/_test/testPage").isNotHandledByRoutesInRouter();

        
    }
    
    @Test
    public void testRoutes() {
        
        startServer();
        
        //some tests that routes are there:
        aRequestLike("GET",  "/").isHandledBy(ApplicationController.class, "index");
        aRequestLike("GET",  "/examples").isHandledBy(ApplicationController.class, "examples");

    }

}
