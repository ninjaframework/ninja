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

package controllers;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import ninja.RecycledNinjaServerTester;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;

public class ChatWebSocketTest extends RecycledNinjaServerTester {
    
    public String withBaseWebSocketUrl(String path) {
        return withBaseUrl(path).replace("http://", "ws://");
    }
    
    @Test
    public void handshakeUnauthorized() throws IOException, WebSocketException {
        String url = withBaseWebSocketUrl("/chat?status=401");
        WebSocket ws = new WebSocketFactory().createSocket(url);
        try {
            ws.connect();
            fail("should have failed with a 401");
        } catch (OpeningHandshakeException e) {
            assertThat(e.getStatusLine().getStatusCode(), is(401));
        } finally {
            ws.disconnect(WebSocketCloseCode.NORMAL);
        }
    }
    
    @Test
    public void sendAndReceiveText() throws IOException, WebSocketException, InterruptedException {
        String chatId = UUID.randomUUID().toString();
        String url = withBaseWebSocketUrl("/chat");
        WebSocket ws = new WebSocketFactory().createSocket(url);
        try {
            ws.addHeader("X-Chat-Id", chatId);
            ws.connect();
            BlockingQueue<String> received = new LinkedBlockingQueue<>();
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket ws, String text) throws Exception {
                    received.put(text);
                }
            });
            
            ws.sendText("chat-id");
            
            String reply = received.poll(2L, TimeUnit.SECONDS);
            
            assertThat(reply, is(chatId));
        } finally {
            ws.disconnect(WebSocketCloseCode.NORMAL);
        }
    }
    
    @Test
    public void sendAndReceiveBinary() throws IOException, WebSocketException, InterruptedException {
        String chatId = UUID.randomUUID().toString();
        String url = withBaseWebSocketUrl("/chat");
        WebSocket ws = new WebSocketFactory().createSocket(url);
        try {
            ws.addHeader("X-Chat-Id", chatId);
            ws.connect();
            BlockingQueue<byte[]> received = new LinkedBlockingQueue<>();
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    received.put(binary);
                }
            });
            
            ws.sendText("binary1");
            
            byte[] reply = received.poll(2L, TimeUnit.SECONDS);
            
            assertThat(reply, is(ChatWebSocket.BINARY1));
        } finally {
            ws.disconnect(WebSocketCloseCode.NORMAL);
        }
    }
    
    @Test
    public void supportedProtocol() throws IOException, WebSocketException {
        String url = withBaseWebSocketUrl("/chat");
        WebSocket ws = new WebSocketFactory().createSocket(url);
        try {
            ws.addProtocol("chat");
            ws.connect();
            
            assertThat(ws.getAgreedProtocol(), is("chat"));
        } finally {
            ws.disconnect(WebSocketCloseCode.NORMAL);
        }
    }
    
    @Test
    public void unsupportedProtocol() throws IOException, WebSocketException {
        String url = withBaseWebSocketUrl("/chat");
        WebSocket ws = new WebSocketFactory().createSocket(url);
        try {
            ws.addProtocol("chat2");
            ws.connect();
            
            assertThat(ws.getAgreedProtocol(), is(nullValue()));
        } finally {
            ws.disconnect(WebSocketCloseCode.NORMAL);
        }
    }
    
}