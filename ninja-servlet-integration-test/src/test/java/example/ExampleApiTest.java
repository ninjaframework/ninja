/**
 * Copyright (C) 2012-2018 the original author or authors.
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

package example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import test.NinjaTest;

import org.junit.Test;

import com.google.inject.Injector;

import etc.GreetingService;


public class ExampleApiTest extends NinjaTest {


    @Test
    public void testThatStaticAssetsWork() {

        String apiCallResult = ninjaTestBrowser.makeJsonRequest(getServerAddress() + "api/person.json");

        assertTrue(apiCallResult.startsWith("{\"name\":\"zeeess name -"));

    }

    @Test
    public void testThatInjectorAccessibleFromNinjaTestIsTheApplicationInjector() {

        // this is the application guice injector
        Injector injector = getInjector();

        // We know that this service is a singleton and it provides the application initialization time.
        long serviceInitializationTime = injector.getInstance(GreetingService.class).getServiceInitializationTime();

        // provide a json with information about the application initialization time.
        String serviceInitTimeResult = ninjaTestBrowser.makeJsonRequest(getServerAddress() + "/serviceInitTime");

        //The response information must match the internal application state
        assertEquals("{\"initTime\":" + serviceInitializationTime + "}", serviceInitTimeResult);

    }


}
