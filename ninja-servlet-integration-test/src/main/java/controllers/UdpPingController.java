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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Counts the number of UDP packets received, demonstrates the lifecycle capabilities of the framework
 */
@Singleton
public class UdpPingController {

    private final AtomicInteger count = new AtomicInteger();
    private static final Logger log = LoggerFactory.getLogger(UdpPingController.class);

    private volatile Thread receiveThread;
    private volatile DatagramSocket serverSocket;

    @Start(order = 90)
    public void startReceiving() throws IOException {
        log.info("Starting UDP listener on port 19876");
        serverSocket = new DatagramSocket(19876);

        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] receiveData = new byte[1024];
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        log.info("Received UDP packet from " + receivePacket.getAddress() + ": " +
                                new String(receivePacket.getData(), 0, receivePacket.getLength()));
                        count.incrementAndGet();
                    }
                } catch (IOException e) {
                    // Ignore, it just means we've been shut down
                }
            }
        });

        receiveThread.start();
    }

    @Dispose(order = 90)
    public void stopReceiving() {
        log.info("Stopping UDP listener");
        receiveThread.interrupt();
        serverSocket.close();
        receiveThread = null;
        serverSocket = null;
    }

    public Result getCount(Context ctx) {
    	return Results.json().render(count.get());
    }
}
