/**
 * Copyright (C) 2012-2020 the original author or authors.
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

import static org.junit.Assert.assertThat;
import ninja.utils.NinjaProperties;
import org.junit.Before;
import org.junit.Test;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.Collections;
import ninja.params.Param;
import ninja.params.ParamParsers;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.websockets.WebSockets;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReverseRouterTest {

    Router router;
    ReverseRouter reverseRouter;
    NinjaProperties ninjaProperties;
    WebSockets webSockets;
    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;
    Injector injector;
    Provider<TestController> testControllerProvider;

    @Before
    @SuppressWarnings("Convert2Lambda")
    public void before() {
        this.ninjaProperties = mock(NinjaProperties.class);
        this.injector = mock(Injector.class);
        this.webSockets = mock(WebSockets.class);
        when(this.webSockets.isEnabled()).thenReturn(true);
        this.testControllerProvider = mock(Provider.class);
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        when(testControllerProvider.get()).thenReturn(new TestController());
        when(injector.getProvider(TestController.class)).thenReturn(testControllerProvider);
        when(injector.getInstance(ParamParsers.class)).thenReturn(new ParamParsers(Collections.emptySet()));
        Provider<RouteBuilderImpl> routeBuilderImplProvider = mock(Provider.class);
        when(routeBuilderImplProvider.get()).thenAnswer((invocation) -> new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver));
        router = new RouterImpl(injector, ninjaProperties, webSockets, routeBuilderImplProvider);
        reverseRouter = new ReverseRouter(ninjaProperties, router);
        
        router.GET().route("/home").with(TestController::home);
        router.GET().route("/user/{email}/{id: .*}").with(TestController::user);
        router.GET().route("/u{userId: .*}/entries/{entryId: .*}").with(TestController::entry);
        // second route to index should not break reverse routing matching the first
        router.GET().route("/home/index").with(TestController::index);
        router.WS().route("/websocket").with(TestController::websocket);
        
        router.compileRoutes();
    }

    @Test
    public void simple() {
        String route = reverseRouter.with(TestController::home).build();
        
        assertThat(route, is("/home"));
    }
    
    @Test
    public void simpleWithMethodReference() {
        MethodReference methodRef = new MethodReference(TestController.class, "home");
        
        String route = reverseRouter.with(methodRef).build();
        
        assertThat(route, is("/home"));
    }
    
    @Test
    public void simpleWithClassReference() {
        String route = reverseRouter.with(TestController.class, "home").build();
        
        assertThat(route, is("/home"));
    }
    
    @Test
    public void simpleWithContext() {
        String contextPath = "/context";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = reverseRouter.with(TestController::home).build();
        
        assertThat(route, is("/context/home"));
    }
    
    @Test
    public void simpleWithQuery() {
        String route = reverseRouter.with(TestController::home)
            .queryParam("filter", true)
            .queryParam("a", 1L)
            .queryParam("foo", "bar")
            .queryParam("email", "test@example.com")
            .build();
        
        // insertion order retained
        assertThat(route, is("/home?filter=true&a=1&foo=bar&email=test%40example.com"));
    }
    
    @Test
    public void simpleWithRawQuery() {
        String route = reverseRouter.with(TestController::home)
            .rawQueryParam("email", "test@example.com")
            .build();
        
        // insertion order retained
        assertThat(route, is("/home?email=test@example.com"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void routeNotFound() {
        reverseRouter.with(TestController::notfound)
            .build();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void simpleNoPathParamsThrowsException() {
        reverseRouter.with(TestController::home)
            .pathParam("id", 1000000L)
            .build();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void simpleInvalidPathParamThrowsException() {
        reverseRouter.with(TestController::user)
            .pathParam("id2", 1000000L)
            .build();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void simpleMissingAllPathParamThrowsException() {
        // param for email missing
        reverseRouter.with(TestController::user)
            .build();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void simpleNotEnoughPathParamThrowsException() {
        // param for email missing
        reverseRouter.with(TestController::user)
            .pathParam("id", 1000000L)
            .build();
    }
    
    @Test
    public void path() {
        String route = reverseRouter.with(TestController::user)
            .pathParam("email", "test@example.com")
            .pathParam("id", 1000000L)
            .build();
        
        assertThat(route, is("/user/test%40example.com/1000000"));
    }
    
    @Test
    public void rawPath() {
        String route = reverseRouter.with(TestController::user)
            .rawPathParam("email", "test@example.com")
            .pathParam("id", 1000000L)
            .build();
        
        assertThat(route, is("/user/test@example.com/1000000"));
    }
    
    @Test
    public void verifySecondRouteMatched() {
        String route = reverseRouter.with(TestController::index)
            .build();
        
        assertThat(route, is("/home/index"));
    }
    
    @Test
    public void absolute() {
        String route = reverseRouter.with(TestController::user)
            .absolute("https", "www.greenback.com")
            .pathParam("email", "test@example.com")
            .pathParam("id", 1000000L)
            .build();
        
        assertThat(route, is("https://www.greenback.com/user/test%40example.com/1000000"));
    }
    
    @Test
    public void absoluteWebSocket() {
        Context context = mock(Context.class);
        when(context.getScheme()).thenReturn("http");
        when(context.getHostname()).thenReturn("www.example.com:8080");
        
        String route = reverseRouter.with(TestController::websocket)
            .absolute(context)
            .queryParam("a", 1)
            .build();
        
        assertThat(route, is("ws://www.example.com:8080/websocket?a=1"));
    }
    
    @Test
    public void absoluteSecureWebSocket() {
        Context context = mock(Context.class);
        when(context.getScheme()).thenReturn("https");
        when(context.getHostname()).thenReturn("www.example.com:8080");
        
        String route = reverseRouter.with(TestController::websocket)
            .absolute(context)
            .build();
        
        assertThat(route, is("wss://www.example.com:8080/websocket"));
    }
    
    /**
     * A dummy TestController for mocking.
     */
    public static class TestController {
        
        private final String message;
        
        public TestController() {
            this("not set");
        }
        
        public TestController(String message) {
            this.message = message;
        }
        
        public Result notfound() {
            return Results.ok();
        }
        
        public Result index() {
            return Results.ok();
        }

        public Result user() {
            return Results.ok();
        }

        public Result entry() {
            return Results.ok();
        }

        public Result ref() {
            return Results.ok();
        }

        public Result home() {
            return Results.status(201);
        }
        
        public Result message() {
            return Results.ok().render(message);
        }
        
        public Result status(@Param("status") Integer status) {
            return Results.status(status).render(message);
        }
        
        public Result exception() throws Exception {
            throw new Exception("test");
        }
        
        public Result websocket() {
            return Results.webSocketContinue();
        }
        
    }
    
}
