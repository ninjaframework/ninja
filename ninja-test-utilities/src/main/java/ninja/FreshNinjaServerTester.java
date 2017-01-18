/*
 * Copyright (C) 2012-2017 the original author or authors.
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
package ninja;

import ninja.utils.NinjaTestServer;
import org.junit.After;
import org.junit.Before;

/**
 * Creates a fresh <code>NinjaTestServer</code> for each unit test in your
 * junit test class. Unless you really need a fresh server for each unit test,
 * please note that its fairly expensive to start/stop a ninja test server 
 * for each unit test.
 * 
 * <code>
 * public class MyControllerTest extends FreshNinjaServerTester {
 * 
 *     @Test
 *     public void usersIndex() {
 *         String url = withBaseUrl("/users");
 *         // do rest of test...
 *     }
 * 
 * }
 * </code>
 * 
 * @see RecycledNinjaServerTester
 */
public class FreshNinjaServerTester extends BaseNinjaServerTester {
    
    private NinjaTestServer ninjaTestServer;
    
    @Before
    public void startNinjaServer() {
        ninjaTestServer = new NinjaTestServer();
    }

    @After
    public void shutdownNinjaServer() {
        ninjaTestServer.shutdown();
    }

    @Override
    public NinjaTestServer getNinjaTestServer() {
        return ninjaTestServer;
    }

}
