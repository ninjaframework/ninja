/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.Ninja;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * A simple servlet that allows us to run Ninja inside any servlet
 * container.
 * 
 * @author ra
 * 
 */
public class NinjaServletDispatcher extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
    public void service(ServletRequest req,
                         ServletResponse resp
                         ) throws IOException,
            ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        ServletContext servletContext = getServletContext();

        // We generate a Ninja compatible context element
        Key<Context> key;
        if (ServletFileUpload.isMultipartContent(request)) {
            key = Key.get(Context.class, MultipartRequest.class);
        } else {
            key = Key.get(Context.class);
        }
        ContextImpl context = (ContextImpl) injector.getProvider(key).get();

        // And populate it
        context.init(servletContext, request, response);

        // And invoke ninja on it.
        // Ninja handles all defined routes, filters and much more:
        ninja.onRouteRequest(context);

    }
}
