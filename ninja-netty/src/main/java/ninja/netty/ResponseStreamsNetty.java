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

import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import ninja.utils.ResponseStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * @author Thomas Broyer
 */
public class ResponseStreamsNetty implements ResponseStreams {

    private ChannelHandlerContext ctx;
    private FullHttpResponse response;
    private String charset;

    private OutputStream outputStream;
    private Writer writer;

    public void init(ChannelHandlerContext ctx, FullHttpResponse response, String charset) {
        this.ctx = ctx;
        this.response = response;
        this.charset = charset;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("Illegal attempt to call getOutputStream() after getWriter() has already been called.");
        }
        if (outputStream == null) {
            outputStream = new ByteBufOutputStream(response.data()) {
                @Override
                public void close() throws IOException {
                    super.close();

                    // Close the connection as soon as the response is sent.
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }
            };
        }
        return outputStream;
    }

    @Override
    public Writer getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("Illegal attempt to call getWriter() after getOutputStream() has already been called.");
        }
        if (writer == null) {
            writer = new OutputStreamWriter(new ByteBufOutputStream(response.data()), charset) {
                @Override
                public void close() throws IOException {
                    super.close();

                    // Close the connection as soon as the response is sent.
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }
            };
        }
        return writer;
    }
}
