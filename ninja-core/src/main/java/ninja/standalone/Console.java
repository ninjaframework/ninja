/**
 * Copyright (C) the original author or authors.
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

package ninja.standalone;

import com.google.inject.Injector;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

/**
 *
 * @author jjlauer
 */
public interface Console<T extends Console> {

    /**
     * Configures the standalone to prepare for being started.
     * @return This standalone
     * @throws Exception Thrown if an exception occurs during configuration
     */
    T configure() throws Exception;

    T externalConfigurationPath(String externalConfigurationPath);

    String getExternalConfigurationPath();

    /**
     * Gets the Guice injector that booted the Ninja application. This value
     * is only accessible after start() is successfully called.
     * @return The guice injector
     * @throws IllegalStateException Thrown if attempting to access this variable
     *      before start() is successfully called.
     */
    Injector getInjector();

    String getName();

    NinjaMode getNinjaMode();

    /**
     * Gets the NinjaProperties that were used to configure Ninja. This value
     * is only accessible after configure() is successfully called.
     * @return The NinjaProperties implementation
     * @throws IllegalStateException Thrown if attempting to access this variable
     *      before configure() is successfully called.
     */
    NinjaPropertiesImpl getNinjaProperties();

    T name(String name);

    T ninjaMode(NinjaMode ninjaMode);

    /**
     * Shutdown Ninja and underlying server as safely as possible (tries not
     * to cause exceptions to be thrown).
     * @return This standalone
     */
    T shutdown();

    /**
     * Configures (if not yet done), boots Ninja application and starts the
     * underlying server.
     * @return This standalone
     * @throws Exception Thrown if an exception occurs during Ninja boot or
     *      server start
     */
    T start() throws Exception;
    
}
