/**
 * Copyright (C) 2012-2019 the original author or authors.
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

package ninja.websockets.jsr356;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;
import ninja.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jsr356ServerEndpointConfig implements ServerEndpointConfig {
    static private final Logger log = LoggerFactory.getLogger(Jsr356ServerEndpointConfig.class);
    
    private final Jsr356ServerEndpointConfigurator configurator;
    private final Route route;

    public Jsr356ServerEndpointConfig(Route route) {
        this.configurator = new Jsr356ServerEndpointConfigurator();
        this.route = route;
    }
    
    @Override
    public Class<?> getEndpointClass() {
        log.trace("getEndpointClass()");
        return Jsr356DelegatingEndpoint.class;
    }

    @Override
    public String getPath() {
        log.trace("getPath()");
        return this.route.getUri();
    }

    @Override
    public List<String> getSubprotocols() {
        log.trace("getSubprotocols()");
        return Collections.emptyList();
    }

    @Override
    public List<Extension> getExtensions() {
        log.trace("getExtensions()");
        return Collections.emptyList();
    }

    @Override
    public Configurator getConfigurator() {
        log.trace("getConfigurator()");
        return this.configurator;
    }

    @Override
    public List<Class<? extends Encoder>> getEncoders() {
        log.trace("getEncoders()");
        return Collections.emptyList();
    }

    @Override
    public List<Class<? extends Decoder>> getDecoders() {
        log.trace("getDecoders()");
        return Collections.emptyList();
    }

    @Override
    public Map<String,Object> getUserProperties() {
        log.trace("getUserProperties()");
        return Collections.emptyMap();
    }
    
}