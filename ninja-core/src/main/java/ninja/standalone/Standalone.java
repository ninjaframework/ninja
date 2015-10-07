/*
 * Copyright 2015 ninjaframework.
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
package ninja.standalone;

import com.google.inject.Injector;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

/**
 *
 * @author joelauer
 */
public interface Standalone<T extends Standalone> {
    
    String KEY_NINJA_STANDALONE         = "ninja.standalone";
    String KEY_NINJA_CONTEXT            = "ninja.context";
    String KEY_NINJA_HOST               = "ninja.host";
    String KEY_NINJA_IDLE_TIMEOUT       = "ninja.idle.timeout";
    String KEY_NINJA_PORT               = "ninja.port";
    
    String DEFAULT_STANDALONE_CLASS     = "ninja.standalone.NinjaJetty";
    String DEFAULT_CONTEXT              = ""; // empty context
    String DEFAULT_HOST                 = null; // bind to any
    Long DEFAULT_IDLE_TIMEOUT           = 30000L; // set to Jetty 9 default
    Integer DEFAULT_PORT                = 8080;

    T configure() throws Exception;
    
    /**
     * Configure, start, add shutdown hook, and join.  Does not exit.
     */
    void run();

    void start() throws Exception;
    
    void join() throws Exception;
    
    void shutdown();

    NinjaMode getNinjaMode();
    
    T ninjaMode(NinjaMode ninjaMode);
    
    String getName();
    
    T name(String name);
    
    String getHost();
    
    T host(String host);
    
    Integer getPort();
    
    T port(int port);
    
    String getContext();

    T context(String context);

    T idleTimeout(long idleTimeout);
    
    Long getIdleTimeout();

    NinjaPropertiesImpl getNinjaProperties();
    
    Injector getInjector();

}
