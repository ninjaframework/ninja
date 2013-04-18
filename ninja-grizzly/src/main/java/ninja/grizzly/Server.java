/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja.grizzly;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Injector;
import ninja.NinjaBootstrap;
import org.glassfish.grizzly.http.server.HttpServer;

import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Broyer
 */
public class Server {

    public static void main(String[] args) {
        int port;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        final NinjaBootstrap ninjaBootstrap = new NinjaBootstrap();
        ninjaBootstrap.boot();
        Injector injector = ninjaBootstrap.getInjector();

        final HttpServer server = HttpServer.createSimpleServer(null, port);
        server.getServerConfiguration().addHttpHandler(injector.getInstance(NinjaHandler.class));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.print("Stopping...");
                server.stop();
                ninjaBootstrap.shutdown();
            }
        });

        try {
            server.start();
            System.out.println("Web server started at port " + port);
            while (server.isStarted()) {
                if (System.in.available() > 0) {
                    System.in.read();
                    break;
                }
                Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            }

            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
