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
import java.util.Optional;
import ninja.Bootstrap;
import ninja.utils.NinjaPropertiesImpl;

public class NinjaConsole extends AbstractConsole<NinjaConsole> {
    
    private ConsoleBootstrap bootstrap;
    
    public NinjaConsole() {
        super("NinjaConsole");
    }
    
    public static void main(String [] args) throws Exception {
        // create new instance and run it
        new NinjaConsole().start();
    }
    
    @Override
    public Injector getInjector() {
        checkStarted();
        return this.bootstrap.getInjector();
    }
    
    @Override
    public void doConfigure() throws Exception {
        // create new bootstrap to kickoff ninja
        this.bootstrap = new ConsoleBootstrap(ninjaProperties);
    }
    
    @Override
    public void doStart() throws Exception {
        try {
            this.bootstrap.boot();
        } catch (Exception e) {
            throw tryToUnwrapInjectorException(e);
        }
    }

    @Override
    public void doShutdown() {
        if (this.bootstrap != null) {
            this.bootstrap.shutdown();
            this.bootstrap = null;
        }
    }
    
    static public class ConsoleBootstrap extends Bootstrap {

        public ConsoleBootstrap(NinjaPropertiesImpl ninjaProperties) {
            super(ninjaProperties);
        }

        @Override
        public void initRoutes() throws Exception {
            // do nothing in console apps
        }

    }
    
}