package controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicInteger;

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
    	return Results.json(count.get());
    }
}
