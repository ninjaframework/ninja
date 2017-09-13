/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketFactory;
import static ninja.standalone.NinjaJettyTest.RANDOM_PORT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class WebSocketTest {
    
    @Test
    public void websocketsEnabled() throws Exception {
        // this test is not really specific to jetty, but its easier to test here
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.com.session.conf")
            .port(RANDOM_PORT);
        
        try {
            standalone.start();
            
            String url = "ws://localhost:" + RANDOM_PORT + "/example";
            WebSocket ws = new WebSocketFactory().createSocket(url);
            try {
                ws.connect();
                assertThat(ws.isOpen(), is(true));
            } finally {
                ws.disconnect(WebSocketCloseCode.NORMAL);
            }
        } finally {
            standalone.shutdown();
        }
    }
    
}