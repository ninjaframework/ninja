/*
 * Copyright 2016 ninjaframework.
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
package ninja.utils;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Forwards method calls to an underlying java.util.ServiceLoader -- mainly to
 * allow participating and mocking in unit tests.  Designed to be a drop-in
 * replacement at the source-code level since java.util.ServiceLoader is final.
 */
public class ForwardingServiceLoader<T> {
    
    private final ServiceLoader<T> serviceLoader;

    public ForwardingServiceLoader(ServiceLoader<T> serviceLoader) {
        this.serviceLoader = serviceLoader;
    }
    
    public Iterator<T> iterator() {
        return this.serviceLoader.iterator();
    }
    
    public void reload() {
        this.serviceLoader.reload();
    }
    
    static public <T> ForwardingServiceLoader<T> loadWithSystemServiceLoader(Class<T> service) {
        return new ForwardingServiceLoader<>(
            ServiceLoader.load(service)
        );
    }
    
    static public <T> ForwardingServiceLoader<T> loadWithSystemServiceLoader(Class<T> service, ClassLoader classLoader) {
        return new ForwardingServiceLoader<>(
            ServiceLoader.load(service, classLoader)
        );
    }
    
}
