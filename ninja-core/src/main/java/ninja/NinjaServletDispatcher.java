/**
 * Copyright (C) 2012 the original author or authors.
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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A simple servlet filter that allows us to run Ninja inside any servlet
 * container.
 * 
 * This dispatcher targets Servlet 2.5.
 * 
 * @author ra
 * 
 */
public class NinjaServletDispatcher implements Filter {

    @Inject
    private Injector injector;

    @Inject
    private Ninja ninja;

    public NinjaServletDispatcher() {

    }

    /**
     * Special constructor for usage in JUnit tests.
     * in regular case we have injector from NinjaServletListener
     */
    public NinjaServletDispatcher(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
 
    }

    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse resp,
                         FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // We generate a Ninja compatible context element
        ContextImpl context = (ContextImpl) injector.getProvider(Context.class)
                .get();

        // And populate it
        context.init(request, response);

        // And invoke ninja on it.
        // Ninja handles all defined routes, filters and much more:
        ninja.invoke(context);

    }

    @Override
    public void destroy() {

    }

}
