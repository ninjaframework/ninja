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

package controllers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.utils.NinjaProperties;
import ninja.websockets.WebSocketHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample Ninja websocket endpoint that implements both a Ninja handshake
 * as well as JSR-356 handling once connected.
 * 
 * @author jjlauer
 */
public class ChatWebSocket extends Endpoint implements MessageHandler.Whole<String> {
    static private final Logger LOG = LoggerFactory.getLogger(ChatWebSocket.class);
    static private final AtomicInteger SEQUENCE = new AtomicInteger();
    // random bytes to test binary
    static public final byte[] BINARY1 = new byte[] { (byte)1, (byte)4, (byte)10 };
    
    private final int id;
    private String chatId;
    private Session session;
    private RemoteEndpoint.Basic remote;
    
    @Inject
    public ChatWebSocket(NinjaProperties ninjaProperties) {
        this.id = SEQUENCE.incrementAndGet();
        LOG.info("created: id={}", this.id);
    }
    
    /**
     * A Ninja-specific method that gets called first when a websocket handshake is
     * initiated by a client.  All ninja functionality is available such as
     * filters, session, context, parameter handling, etc.  You can return
     * any HTTP result you'd like or return a status code of 101 to tell ninja
     * (and the HTTP container) that is okay to proceed with the websocket connection.
     */
    public Result handshake(
            Context context,
            WebSocketHandshake handshake,
            @Param("status") Optional<Integer> status) {
        
        LOG.info("handshake: id={}", this.id);
        
        // select "chat" subprotocol if client sent it
        handshake.selectProtocol("chat");

        // allow unit tests to pass in this value so we can echo it back
        this.chatId = context.getHeader("X-Chat-Id");
        
        // simple way to allow a client to trigger a different status code
        // to easily demonstrate how Ninja can control if a handshake will
        // continue or not (one of the many complaints about JSR-356)
        if (status.isPresent()) {
            return Results.status(status.get());
        } else {
            return Results.webSocketContinue();
        }
    }
    
    /**
     * After a handshake successfully completes these JSR-356 methods will
     * then handle the connection from that point forward.
     */
    
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("opened: id={}, session={}", this.id, session.getId());
        this.session = session;
        this.session.addMessageHandler(this);
        this.remote = this.session.getBasicRemote();
    }
    
    @Override
    public void onError(Session session, Throwable cause) {
        LOG.info("error: id={}, session={}, cause={}",
            this.id, session.getId(), cause.getMessage());
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        LOG.info("closed: id={}, session={}, code={}, reason={}",
            this.id, session.getId(), reason.getCloseCode(), reason.getReasonPhrase());
        this.session = null;
        this.remote = null;
    }

    @Override
    public void onMessage(String message) {
        LOG.info("received: id={}, session={}, message={}",
            this.id, session.getId(), message);
        
        try {
            switch (message) {
                case "chat-id":
                    this.remote.sendText(this.chatId);
                    break;
                case "hello":
                    this.remote.sendText("world!");
                    break;
                case "binary1":
                    this.remote.sendBinary(ByteBuffer.wrap(BINARY1));
                    break;
                default:
                    this.remote.sendText("did not understand your message");
                    break;
            }
        } catch (IOException e) {
            LOG.error("", e);
        }
    }
}