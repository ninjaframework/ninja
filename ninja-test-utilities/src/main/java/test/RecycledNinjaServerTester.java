/*
 * Copyright (C) 2012-2019 the original author or authors.
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
package test;

import ninja.utils.NinjaMode;
import test.utils.NinjaTestServer;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;

/**
 * Uses a single <code>NinjaTestServer</code> per junit test class.  The server
 * is started a single time when the class loads and used across all tests.
 * If you're unit tests do not rely on a fresh server then this approach will
 * dramatically increase the speed of your unit tests -- by not starting up
 * and shutting down a ninja test server for each unit test.
 * 
 * <code>
 * public class MyControllerTest extends RecycledNinjaServerTester {
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
 * @see FreshNinjaServerTester
 */
public class RecycledNinjaServerTester extends BaseNinjaServerTester {
    
    static private NinjaTestServer ninjaTestServer;
    
    @ClassRule
    static public ExternalResource ninjaTestServerResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            ninjaTestServer = new NinjaTestServer(NinjaMode.test);
        }

        @Override
        protected void after() {
            ninjaTestServer.shutdown();
            ninjaTestServer = null;
        }
    };

    @Override
    public NinjaTestServer getNinjaTestServer() {
        return ninjaTestServer;
    }
    
}
