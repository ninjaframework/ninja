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

package com.example.controllers;

import java.io.IOException;
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
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleWebSocket extends Endpoint implements MessageHandler.Whole<String> {
    static private final Logger LOG = LoggerFactory.getLogger(ExampleWebSocket.class);

    private Session session;
    private RemoteEndpoint.Basic remote;
    
    @Inject
    public ExampleWebSocket(NinjaProperties ninjaProperties) {
        LOG.info("created: is_dev={}", ninjaProperties.isDev());
    }
    
    public Result handshake(Context context) {
        LOG.info("handshake: remote_addr={}", context.getRemoteAddr());
        return Results.webSocketContinue();
    }
    
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        LOG.info("opened: session={}, uri={}, query={}",
            session.getId(), session.getRequestURI(), session.getQueryString());
        this.session = session;
        this.session.addMessageHandler(this);
        this.remote = this.session.getBasicRemote();
    }
    
    @Override
    public void onError(Session session, Throwable cause) {
        LOG.info("error: session={}, cause={}",
            session.getId(), cause.getMessage());
    }

    @Override
    public void onClose(Session session, CloseReason reason) {
        LOG.info("closed: session={}, code={}, reason={}",
            session.getId(), reason.getCloseCode(), reason.getReasonPhrase());
        this.session = null;
        this.remote = null;
    }

    @Override
    public void onMessage(String message) {
        LOG.info("received: session={}, message={}", session.getId(), message);
        try {
            switch (message) {
                case "ping":
                    this.remote.sendText("pong!");
                    break;
                case "hello":
                    this.remote.sendText("world!");
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