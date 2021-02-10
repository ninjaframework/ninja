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

package ninja.servlet;

import com.google.inject.servlet.GuiceFilter;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import ninja.Result;
import ninja.Route;
import ninja.utils.HttpHeaderConstants;
import ninja.websockets.jsr356.Jsr356Handshake;
import ninja.websockets.jsr356.Jsr356HandshakePrincipal;
import ninja.websockets.jsr356.Jsr356HandshakeThreadLocal;
import ninja.websockets.WebSocketUtils;

/**
 * Servlet filter that calls into a wrapped Guice filter which in turn will
 * call into Ninja to handle requests.  If a websocket handshake is detected
 * there is some logic to first delegate it to Ninja then hand if off to the
 * container for further processing.
 * 
 * @author jjlauer
 */
public class NinjaServletFilter implements Filter {

    private final GuiceFilter wrapped;
    
    public NinjaServletFilter() {
        this.wrapped = new GuiceFilter();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.wrapped.init(filterConfig);
    }
    
    @Override
    public void destroy() {
        this.wrapped.destroy();
    }

    private boolean isWebSocketHandshake(HttpServletRequest httpRequest) {
        String upgradeHeader = httpRequest.getHeader("Upgrade");
        return upgradeHeader != null && "websocket".equalsIgnoreCase(upgradeHeader);
    }
    
    public HttpServletRequest buildWebSocketHandshakeHttpRequest(HttpServletRequest httpRequest, Jsr356Handshake handshake) {
        // save handshake as attribute so it can be used in ninja
        httpRequest.setAttribute(WebSocketUtils.ATTRIBUTE_HANDSHAKE, handshake);
        
        // modify http method to be WS so ninja can do a route lookup
        return new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getMethod() {
                return Route.HTTP_METHOD_WEBSOCKET;
            }
        };
    }
    
    public HttpServletRequest buildWebSocketUpgradeHttpRequest(HttpServletRequest httpRequest, Jsr356Handshake handshake) {
        // create a "UserPrincipal" that is actually storage of the handshake
        final Jsr356HandshakePrincipal principal
            = new Jsr356HandshakePrincipal(httpRequest.getUserPrincipal(), handshake);

        // create a new wrapped servlet request with this new principal
        return new HttpServletRequestWrapper(httpRequest) {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }
        };
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
       
        if (!isWebSocketHandshake(httpRequest)) {
            
            // handle normally
            wrapped.doFilter(request, response, chain);
            
        } else {

            // create handshake we'll use to help process along the way
            final Jsr356Handshake handshake = new Jsr356Handshake();
            
            // parse requested protocols via header
            handshake.setRequestedProtocols(
                WebSocketUtils.parseProtocolRequestHeader(
                    httpRequest.getHeader(HttpHeaderConstants.SEC_WEBSOCKET_PROTOCOL)));
            
            // wrap request to mark it as a websocket and handoff to ninja
            wrapped.doFilter(buildWebSocketHandshakeHttpRequest(httpRequest, handshake), response, chain);

            // continue with handshake by moving onto next filter in container?
            if (httpResponse.getStatus() == Result.SC_101_SWITCHING_PROTOCOLS) {
                // Due to design flaw with JSR-356 - we will passthru some objects
                // to the eventual endpoint by using both a thread local and then
                // also by hijacking the "UserPrincipal" object on the request.
                // Various servlet containers may handoff the "onOpen" of a websocket
                // to a new thread pool that wouldn't have the thread local context
                
                // get endpoint from current request and save in handshake
                handshake.setEndpoint((Endpoint)httpRequest
                    .getAttribute(WebSocketUtils.ATTRIBUTE_ENDPOINT));
                
                // assign to current thread local
                Jsr356HandshakeThreadLocal.set(handshake);
                try {
                    // stuff handshake as "UserPrincipal" so it can be used by endpoint
                    httpRequest = this.buildWebSocketUpgradeHttpRequest(httpRequest, handshake);

                    chain.doFilter(httpRequest, response);
                } finally {
                    Jsr356HandshakeThreadLocal.remove();
                }
            }
        }
    }

}