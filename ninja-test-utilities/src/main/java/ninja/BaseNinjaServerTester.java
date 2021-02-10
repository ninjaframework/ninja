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

package ninja;

import com.google.inject.Injector;
import ninja.utils.NinjaTestServer;

abstract public class BaseNinjaServerTester {
    
    abstract public NinjaTestServer getNinjaTestServer();

    public String baseUrl() {
        return getNinjaTestServer().getBaseUrl();
    }
    
    public String withBaseUrl(String path) {
        return getNinjaTestServer().getBaseUrl() + path;
    }
    
    public Injector getInjector() {
        return getNinjaTestServer().getInjector();
    }
    
}
