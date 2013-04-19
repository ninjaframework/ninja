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

package ninja.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import ninja.NinjaBootstrap;

/**
 * @author Thomas Broyer
 */
public class Server {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        final EventExecutorGroup executor = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors());
        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        final NinjaBootstrap ninjaBootstrap = new NinjaBootstrap();
        ninjaBootstrap.boot();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.print("Stopping...");
                ninjaBootstrap.shutdown();
                serverBootstrap.shutdown();
                executor.shutdown();
            }
        });

        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
         .channel(NioServerSocketChannel.class)
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline pipeline = ch.pipeline();
                 pipeline.addLast("decoder", new HttpRequestDecoder());
                 pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // TODO: support chunked requests
                 pipeline.addLast("encoder", new HttpRequestEncoder());
                 pipeline.addLast("chunckedWriter", new ChunkedWriteHandler());
//                 pipeline.addLast("compressor", new HttpContentCompressor());

                 pipeline.addLast(executor, "handler", ninjaBootstrap.getInjector().getInstance(ServerHandler.class));
             }
         });

        Channel ch = serverBootstrap.bind(port).sync().channel();
        System.out.println("Web server started at port " + port);

        ch.closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null; // system temp directory

        int port;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new Server(port).run();
    }
}
