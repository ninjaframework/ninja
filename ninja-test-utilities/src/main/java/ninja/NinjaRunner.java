/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import com.google.inject.Injector;
import ninja.utils.NinjaTestServer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * NinjaRunner makes it easy to test DAOs and service objects.
 * Service objects are generally the place where business logic executed.
 * Each service object may be injected a lot of DAOs.
 * </p>
 *
 * <p>
 * Suppose you have some service objects ,
 * without NinjaRunner , the only way to test service objects is to getInstance() of these DAOs
 * from Guice's injector and manually constructor-injecting to the service object
 * in NinjaTest's {@code @Before} method.
 * </p>
 *
 * <p>
 * With NinjaRunner , you just add {@code @RunWith(NinjaRunner.class)} to your test class ,
 * and declare {@code @Inject private ServiceObj serviceObj;} and you'll get an
 * injected serviceObj. No more {@code @Before} methods.
 * </p>
 *
 * <p>
 *     Code Example :
 * </p>
 *
 * <pre>
 * {@code &#064;RunWith(NinjaRunner.class)
 *   public class DataServiceTest  {
 *     &#064;Inject private ServiceObj serviceObj;
 *
 *     &#064;Test
 *     public void testDataService() {
 *       assert (serviceObj != null);
 *     }
 *   }
 * }
 * </pre>
 */
public class NinjaRunner extends BlockJUnit4ClassRunner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Injector injector = null;

    public NinjaRunner(Class<?> klass) throws InitializationError {
        super(klass);
        NinjaTestServer ninjaTestServer = new NinjaTestServer();
        injector = ninjaTestServer.getInjector();
    }

    @Override
    protected Object createTest() throws Exception {
        Object testObj = super.createTest();
        injector.injectMembers(testObj);
        return testObj;
    }
}

