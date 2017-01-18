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

package conf;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import ninja.NinjaRouterTest;

import org.junit.Test;

import com.google.common.collect.Maps;

import controllers.ApplicationController;

/**
 * 
 * Remove me once NinjaRouterTest has been put to rest.
 * 
 * @author ra
 * @deprecated
 */
@Deprecated
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

    }
    
    
    @Test
    public void testReverseRoutingWithoutMap() {
        
        startServer();
        
        
        // TEST 1: a simple route without replacements:
        String generatedReverseRoute = router.getReverseRoute(ApplicationController.class, "index");
        
        assertEquals("/", generatedReverseRoute);
        
        // TEST 2: a more complex route with replacements:
        //router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
        
        generatedReverseRoute = router.getReverseRoute(ApplicationController.class, "userDashboard");
        
        // this looks strange, but is expected:
        assertEquals(null, generatedReverseRoute);
        


    }
    
    @Test
    public void testReverseRoutingWithMap() {
        
        startServer();
        
        
        // TEST 1: a simple route without replacements:
        Map<String, Object> map = Maps.newHashMap();
        String generatedReverseRoute = router.getReverseRoute(ApplicationController.class, "index", map);
        
        assertEquals("/", generatedReverseRoute);
        
        // TEST 2: a more complex route with replacements:
        //router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
        map = Maps.newHashMap();
        map.put("id","myId");
        map.put("email","myEmail");
        
        generatedReverseRoute = router.getReverseRoute(ApplicationController.class, "userDashboard", map);
        
        assertEquals("/user/myId/myEmail/userDashboard", generatedReverseRoute);
        


    }
    
    @Test
    public void testReverseRoutingWithMapAndQueryParameter() {
        
        startServer();
        
        // TEST 2: a more complex route with replacements and query parameters
        //router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
        Map<String, Object> map = Maps.newHashMap();
        map.put("id","myId");
        map.put("email","myEmail");        
        map.put("paging_size","100");
        map.put("page","1");
        
        String generatedReverseRoute = router.getReverseRoute(ApplicationController.class, "userDashboard", map);
        
        assertThat(generatedReverseRoute, containsString("page=1"));
        assertThat(generatedReverseRoute, containsString("paging_size=100"));
        assertThat(generatedReverseRoute, containsString("/user/myId/myEmail/userDashboard?"));

    }
    
    @Test
    public void testReverseRoutingWithArrayAndQueryParameters() {
        
        startServer();
        
        String generatedReverseRoute = router.getReverseRoute(
                ApplicationController.class, 
                "userDashboard", 
                "id","myId",
                "email","myEmail",
                "paging_size","100",
                "page","1");

        assertThat(generatedReverseRoute, containsString("page=1"));
        assertThat(generatedReverseRoute, containsString("paging_size=100"));
        assertThat(generatedReverseRoute, containsString("/user/myId/myEmail/userDashboard?"));
    }
    
    
    @Test
    public void testReverseRoutingWithArrayAndWrongAmountOfQueryParameters() {
        
        startServer();
        
        // TEST 2: a more complex route with replacements and query parameters
        //router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
        Map<String, Object> map = Maps.newHashMap();
        map.put("id","myId");
        map.put("email","myEmail");        
        map.put("paging_size","100");
        map.put("page","1");
        
        String generatedReverseRoute = router.getReverseRoute(
                ApplicationController.class, 
                "userDashboard", 
                "1", "2", "3");
        
        assertEquals(null, generatedReverseRoute);

    }
    
    


}
