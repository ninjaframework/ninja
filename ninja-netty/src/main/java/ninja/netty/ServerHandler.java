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

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.*;
import ninja.Ninja;

/**
 * @author Thomas Broyer
 */
public class ServerHandler extends ChannelInboundMessageHandlerAdapter<FullHttpRequest> {
    @Inject
    private Injector injector;

    @Inject
    private Ninja ninja;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // TODO: support chunked requests

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        ContextImpl context = injector.getInstance(ContextImpl.class);
        context.init(ctx, request, response);

        ninja.invoke(context);
    }
}
